package jetty;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.json.JSONObject;

public class MinimalServlets {

	private static Main main;

	public static void main(String[] args) throws Exception {
		// Create a basic jetty server object that will listen on port 8080.
		// Note that if you set this to port 0 then a randomly available port
		// will be assigned that you can either look in the logs for the port,
		// or programmatically obtain it for use in test cases.
		Server server = new Server(9090);

		// The ServletHandler is a dead simple way to create a context handler
		// that is backed by an instance of a Servlet.
		// This handler then needs to be registered with the Server object.
		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);

		// Passing in the class for the Servlet allows jetty to instantiate an
		// instance of that Servlet and mount it on a given context path.

		// IMPORTANT:
		// This is a raw Servlet, not a Servlet that has been configured
		// through a web.xml @WebServlet annotation, or anything similar.
		handler.addServletWithMapping(HelloServlet.class, "/*");

		// Start things up!
		server.start();

		// The use of server.join() the will make the current thread join and
		// wait until the server is done executing.
		// See
		// http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
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
				System.out.println(request.getRequestURI());

				// ----- is json? -----------
				boolean isJson = true;
				JSONObject json = null;
				StringBuilder sb = new StringBuilder();
				String s;
				while ((s = request.getReader().readLine()) != null) {
					sb.append(s);
				}
				try {
					json = new JSONObject(sb.toString());
				} catch (org.json.JSONException e) {
					isJson = false;
				}
				// -------------------------

				main = Main.getInstance();

				if (isJson) {
					main.execute("INSERT INTO Log (WHO, JSON) VALUES ('" + request.getSession().getId() + "', '"
							+ json.toString() + "')");
					main.setJSON(json);
					main.main(json.getString("text").split(" "));
					main.execute("INSERT INTO Log (WHO, JSON) VALUES ('" + request.getSession().getId() + "', '"
							+ json.toString() + "')");

				} else {
					response.setContentType("text/html");
					response.setStatus(HttpServletResponse.SC_OK);

					PrintWriter out = response.getWriter();

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
					out.println("console.log(result);");
					out.println("},");
					out.println("error: function(xhr, resp, text) {");
					out.println("console.log(xhr, resp, text);");
					out.println("}");
					out.println("})");
					out.println("});");
					out.println("});");
					out.println("</script>");

					out.println("<h1>App Status</h1>");

					out.println("<form id=\"form\" method=\"post\">");
					out.println("<textarea id=\"json\" name=\"json\" rows=\"10\" cols=\"50\"></textarea><br/>");
					out.println("<input type=\"hidden\" id=\"custId\" name=\"custId\" value=\"3487\">");
					out.println("<input id=\"b1\" name=\"b1\" type=\"button\" value=\"Submit\">");
					out.println("</form>");

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

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			doPost(req, resp);
		}
	}
}
