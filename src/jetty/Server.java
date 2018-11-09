package jetty;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.json.JSONObject;

public class Server {

	private static Main main;

	public static void main(String[] args) throws Exception {
		org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(9090);
        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase(System.getProperty("java.io.tmpdir"));
        server.setHandler(context);
        context.addServlet(HelloServlet.class, "/");

        server.start();
        server.join();
	}

	@SuppressWarnings("serial")
	public static class HelloServlet extends HttpServlet {
		@Override
		protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			try {
				if (request.getRequestURI().contains("favicon.ico"))
					return;
				// System.out.println(request.getRequestURI());

				main = Main.getInstance();
				
				PrintWriter out = response.getWriter();
				response.setStatus(HttpServletResponse.SC_OK);

				JSONObject json = null;
				json = getJson(request);
				
				if (json != null) {
					response.setContentType("application/json");
					
					main.execute("INSERT INTO Log (WHO, JSON) VALUES ('" + request.getSession().getId() + "', '"
							+ json.toString() + "')");
					main.setJSON(json);
					try{
						json.remove("out");
						main.main(json.getString("text").split(" "), request, response);
					} catch (Exception e) {
						json.put("error", e.getMessage());						
					} finally {
						main.execute("INSERT INTO Log (WHO, JSON) VALUES ('" + request.getSession().getId() + "', '"
								+ json.toString() + "')");						
					}
					
					out.println(json);

				} else {
					response.setContentType("text/html");					

					out.println("<!DOCTYPE html>");
					out.println("<html>");
					out.println("<head>");
					out.println(
							"<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>");

					out.println("<script>");
					out.println("$(document).ready(function(){");
					out.println("function toJSONString( form ) {");
					out.println("var obj = {};");
					out.println("var elements = form.querySelectorAll( \"input, select, textarea\" );");
					out.println("for( var i = 0; i < elements.length; ++i ) {");
					out.println("var element = elements[i];");
					out.println("var name = element.name;");
					out.println("var value = element.value;");
					out.println("if( name ) {");
					out.println("obj[ name ] = value;");
					out.println("}");
					out.println("}");
					out.println("return JSON.stringify( obj );");
					out.println("}");
					out.println("$(\"#b1\").on('click', function(){");
					out.println("var formData = toJSONString(document.getElementById(\"form\"));");
					out.println("$.ajax({");
					out.println("url: '/',");
					out.println("type : \"POST\",");
					out.println("dataType : 'json',");
					out.println("contentType: \"application/json; charset=utf-8\",");
					out.println("data : formData,");
					out.println("success : function(result) {");
					out.println("console.log(result);$('#out').val(JSON.stringify(result));");
					out.println("},");
					out.println("error: function(xhr, resp, text) {");
					out.println("console.log(xhr, resp, text);");
					out.println("}");
					out.println("})");
					out.println("});");
					out.println("});");
					out.println("</script>");

					out.println("<h1>Server and App Status</h1>");

					out.println("<form id=\"form\" method=\"post\">");					
					out.println("type your command: <input type=\"text\" id=\"text\" name=\"text\" value=\"\">");
					//out.println("var1: <input type=\"text\" id=\"var1\" name=\"var1\" value=\"valor1\">");					
					out.println("<input id=\"b1\" name=\"b1\" type=\"button\" value=\"Submit\"><br/><br/>");
					out.println("out: <textarea id=\"out\" name=\"out\" rows=\"5\" cols=\"40\"></textarea><br/>");
					out.println("</form><br/>");

					main.execute("INSERT INTO Log (WHO, JSON) VALUES ('test', '----------------------------------')");
					ResultSet rs = main.executeQuery("SELECT ID, WHO, WHEN, JSON FROM Log ORDER BY ID DESC");

					out.println("<table>");
					while (rs.next()) {
						out.println("<tr>");
						out.println("<td>" + rs.getString("ID") + "</td>");
						out.println("<td>" + rs.getString("WHO") + "</td>");
						out.println("<td>" + rs.getString("WHEN") + "</td>");
						out.println("<td>" + rs.getString("JSON") + "</td>");
						out.println("</tr>");
					}
					out.println("</table>");
					out.println("</body>");
					out.println("</html>");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		private JSONObject getJson(HttpServletRequest request) throws IOException {
			JSONObject json = null;
			StringBuilder sb = new StringBuilder();
			String s;
			while ((s = request.getReader().readLine()) != null) {
				sb.append(s);
			}
			try {
				json = new JSONObject(sb.toString());
			} catch (org.json.JSONException e) {				
			}
			return json;
		}

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			doPost(req, resp);
		}
	}
}
