package your;

import java.sql.*;

import org.json.*;

public class YourMain extends jetty.Main {

	public static int SERVER_PORT = 80;
	private static String INSERT = "INSERT INTO News (URL, TITULO, LIDE, TEXTO, CARTEIRA, PRECO) VALUES (?, ?, ?, ?, ?, ?)";
	private static String SELECT = "SELECT * FROM News WHERE URL = ?";
	private static String INSERT_BUY = "INSERT INTO Compra (NEWS, SESSION) VALUES (?, ?)";
	private static String UPDATE = "UPDATE News SET pago = true WHERE url = ?"; 

	@Override
	public void main(String[] args) {
		JSONObject j = getJSON();
		String url = null;
		String session = null;
		PreparedStatement stmt = null;
		Integer precoCentavos = null;
		Double precoNBR = null;
		JSONObject jsonResponse = null; 
		try {
			switch (args[0]) {
			case "/insert":
				ifEmptyThrowException(j, new String[] { "titulo", "lide", "texto", "carteira", "preco" });
				url = url(j.getString("titulo"));
				j.put("url", url);
				precoCentavos = Integer.parseInt(j.getString("preco").trim());
				if (precoCentavos > 100) precoCentavos = 100;
				if (precoCentavos < 0) precoCentavos = 0;
				stmt = getConnection().prepareStatement(INSERT);
				stmt.setString(1, url);
				stmt.setString(2, j.getString("titulo"));
				stmt.setString(3, j.getString("lide"));
				stmt.setString(4, j.getString("texto"));
				stmt.setString(5, j.getString("carteira"));
				stmt.setInt(6, precoCentavos);
				stmt.executeUpdate();
				break;
			case "/oniobista":
				url = args[1];
				session = args[2];
				stmt = getConnection().prepareStatement(SELECT);
				stmt.setString(1, url);
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
					precoCentavos = rs.getInt("preco");
					if (precoCentavos == 0 && rs.getBoolean("pago") == false) precoCentavos = 10;
					String cmd = "{\"params\": {\"address\":\"" + j.getString("address") + "\"},\"jsonrpc\": \"2.0\", \"id\": \"" + U.random()
					+ "\",\"method\":\"getBalance\"}";
					jsonResponse = U.httpRequest("http://localhost:8070/json_rpc", new JSONObject(cmd));
					Long availableBalance = jsonResponse.getJSONObject("result").getLong("availableBalance");
					precoNBR = U.centsToNBR(precoCentavos);
					Long precoNBRLong = Long.parseLong(U.formatCryptoMoneyHumanToMachine(""+precoNBR));
					if(availableBalance >= precoNBRLong) {
						String paymentId = U.generatePaymentId();
						cmd = "{\"params\":{\"anonymity\":0,\"fee\":1000,\"unlockTime\":0,\"paymentId\":\"" + paymentId 
								+ "\",\"addresses\":[\"" + j.getString("address") + "\"],\"transfers\":[";
						cmd += "{\"amount\":" + precoNBRLong + ",\"address\":\"" + rs.getString("carteira") + "\"}";
						cmd += "]},\"jsonrpc\":\"2.0\",\"id\":\"" + U.random() + "\",\"method\":\"sendTransaction\"}";
						jsonResponse = U.httpRequest("http://localhost:8070/json_rpc", new JSONObject(cmd));
						if (jsonResponse.has("error")) {
							throw new Exception(jsonResponse.getJSONObject("error").getString("message"));
						} else {			
							stmt = getConnection().prepareStatement(INSERT_BUY);
							stmt.setLong(1, rs.getLong("id"));
							stmt.setString(2, session);
							stmt.executeUpdate();
							print("payId:" + paymentId + "\ntxHash:" + jsonResponse.getJSONObject("result").getString("transactionHash"));
							if (rs.getBoolean("pago") != true) {
								stmt = getConnection().prepareStatement(UPDATE);
								stmt.setString(1, url);
								stmt.executeUpdate();
							}
						}
					} else {
						throw new Exception("saldo insuficiente.");
					}
				}
				
				break;
			default:
				print("command not found");
				break;
			}
		} catch (Exception e) {
			print(e.getMessage());
			j.put("error", e.getMessage());
		}
	}

	private void ifEmptyThrowException(JSONObject j, String[] strings) throws Exception {
		for (String x : strings) {
			if (j.getString(x) == null || "".equals(j.getString(x).trim()))
				throw new Exception("o campo " + x + " não foi preenchido!");
		}
	}

	private String url(String s) {
		s = s.replaceAll("[^a-zA-Z0-9]", "_");
		return s;
	}

}
