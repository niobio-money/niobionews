package your;

import java.sql.*;
import java.text.*;
import java.util.*;

import javax.servlet.http.*;

import org.json.*;

public class YourMain extends jetty.Main {

	public static int SERVER_PORT = 80;

	// if you need request (session?) or response, use this main method, otherwise you can remove it
	@Override
	public void main(String[] args, HttpServletRequest request, HttpServletResponse response) {
		main(args);
	}

	// IMPORTANT: The real void main is in jetty.Server class. 
	// So, to run go to (Eclipse) menu Debug -> Server class (not "on Server")
	// When is ready: go to c:\workspace\MyServer\bin an execute comand below to create new server.jar 
	// jar cfm ..\prod\server.jar ..\META-INF\Manifest.txt * ..\lib
	@Override
	public /*not static*/ void main(String[] args) {
		try {
			switch (args[0]) {
			case "/insert":
				JSONObject j = getJSON();
				String url = url(j.getString("titulo"));
				j.put("url", url);		
				String sql = "INSERT INTO News (URL, TITULO, LIDE, TEXTO, CARTEIRA, PRECO) VALUES ('" + url + "','" + j.getString("titulo") + "','" + j.getString("lide") +  "','" + j.getString("texto") +  "','" + j.getString("carteira") +  "'," + j.getString("preco") + ")";
				execute(sql);
				break;
			default:
				print("command not found");
				break;
			}
		} catch (Exception e) {
			print(e.getMessage());
		}
	}

	private String url(String s) {
		s = s.replaceAll("[^a-zA-Z0-9]", "_");
		return s;
	}

}
