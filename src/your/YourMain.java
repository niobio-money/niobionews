package your;

import java.sql.*;
import java.text.*;
import java.util.*;

import javax.servlet.http.*;

import org.json.*;

public class YourMain extends jetty.Main {

	public static int SERVER_PORT = 80;	
	private static String INSERT = "INSERT INTO News (URL, TITULO, LIDE, TEXTO, CARTEIRA, PRECO) VALUES (?, ?, ?, ?, ?, ?)";

	@Override
	public void main(String[] args) {
		JSONObject j = getJSON();
		try {			
			switch (args[0]) {
			case "/insert":
				ifEmptyThrowException(j, new String[] {"titulo", "lide", "texto", "carteira", "preco"});
				String url = url(j.getString("titulo"));
				j.put("url", url);
				int preco = Integer.parseInt(j.getString("preco").trim());
				if (preco > 100) preco = 100;
				if (preco < 0) preco = 0;
				PreparedStatement stmt = getConnection().prepareStatement(INSERT);
			    stmt.setString(1, url);
			    stmt.setString(2, j.getString("titulo"));
			    stmt.setString(3, j.getString("lide"));
			    stmt.setString(4, j.getString("texto"));
			    stmt.setString(5, j.getString("carteira"));
			    stmt.setInt(6, preco);
			    stmt.executeUpdate();
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
		for(String x: strings) {
			if (j.getString(x) == null || "".equals(j.getString(x).trim())) throw new Exception("o campo " + x + " não foi preenchido!");
		}
	}

	private String url(String s) {
		s = s.replaceAll("[^a-zA-Z0-9]", "_");
		return s;
	}

}
