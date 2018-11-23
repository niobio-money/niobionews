package your;

import java.sql.*;

import org.json.*;

public class YourMain extends jetty.Main {

	public static int SERVER_PORT = 80;
	private static String INSERT = "INSERT INTO News (URL, TITULO, LIDE, TEXTO, CARTEIRA, PRECO) VALUES (?, ?, ?, ?, ?, ?)";
	private static String SELECT = "SELECT * FROM News WHERE URL = ?";	

	@Override
	public void main(String[] args) {
		JSONObject j = getJSON();
		String url = null;
		String session = null;
		PreparedStatement stmt = null;
		Integer preco = null;
		JSONObject jsonResponse = null; 
		try {
			switch (args[0]) {
			case "/insert":
				ifEmptyThrowException(j, new String[] { "titulo", "lide", "texto", "carteira", "preco" });
				url = url(j.getString("titulo"));
				j.put("url", url);
				preco = Integer.parseInt(j.getString("preco").trim());
				if (preco > 100) preco = 100;
				if (preco < 0) preco = 0;
				stmt = getConnection().prepareStatement(INSERT);
				stmt.setString(1, url);
				stmt.setString(2, j.getString("titulo"));
				stmt.setString(3, j.getString("lide"));
				stmt.setString(4, j.getString("texto"));
				stmt.setString(5, j.getString("carteira"));
				stmt.setInt(6, preco);
				stmt.executeUpdate();
				break;
			case "/oniobista":
				url = args[1];
				session = args[2];
				stmt = getConnection().prepareStatement(SELECT);
				stmt.setString(1, url);
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
					preco = rs.getInt("preco");
					String cmd = "{\"params\": {\"address\":\"" + j.getString("address") + "\"},\"jsonrpc\": \"2.0\", \"id\": \"" + U.random()
					+ "\",\"method\":\"getBalance\"}";
					jsonResponse = U.httpRequest("http://localhost:8070/json_rpc", new JSONObject(cmd));
					Long availableBalance = jsonResponse.getJSONObject("result").getLong("availableBalance");					
					if(availableBalance >= U.centsToNBR(preco)) {
						// tranfer and save session to show news
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
