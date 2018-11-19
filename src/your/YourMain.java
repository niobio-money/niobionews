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
			case "/helloworld":
				print("OLA MUNDO!");
				break;
			case "/datetime":
				print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
				break;
			/*
			default:
				ResultSet rs = executeQuery("SELECT ID, WHO, WHEN, JSON FROM Log ORDER BY ID DESC");
				List<JSONObject> l = new ArrayList<JSONObject>();
				JSONObject o = null;
				while (rs.next()) {
					o = new JSONObject();
					o.put("id", rs.getString("ID"));
					o.put("who", rs.getString("WHO"));
					o.put("when", rs.getString("WHEN"));
					o.put("json", rs.getString("JSON"));
					l.add(o);
				}
				print(o.toString());
				break;*/
			}
		} catch (Exception e) {
			print(e.getMessage());
		}
	}

}
