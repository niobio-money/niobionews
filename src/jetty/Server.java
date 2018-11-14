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

import your.YourMain;

public class Server {

	private static Main main;

	public static void main(String[] args) throws Exception {
		System.setProperty( "derby.stream.error.field", "jetty.DerbyUtil.DEV_NULL" );
		org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(YourMain.SERVER_PORT);
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
					json.remove("response");
					response.setContentType("application/json");
					main.insert(request.getSession().getId(), json.toString());
					main.setJSON(json);
					try{						
						main.main(json.getString("text").split(" "), request, response);
					} catch (Exception e) {
						json.put("error", e.getMessage());						
					} finally {
						main.insert(request.getSession().getId(), json.toString());						
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
					out.println("$(\"#btnSubmit\").on('click', function(){");
					out.println("var formData = toJSONString(document.getElementById(\"form\"));");
					out.println("$.ajax({");
					out.println("url: '/',");
					out.println("type : \"POST\",");
					out.println("dataType : 'json',");
					out.println("contentType: \"application/json; charset=utf-8\",");
					out.println("data : formData,");
					out.println("success : function(result) {");
					out.println("console.log(result);$('#response').val(JSON.stringify(result));");
					out.println("},");
					out.println("error: function(xhr, resp, text) {");
					out.println("console.log(xhr, resp, text);");
					out.println("}");
					out.println("})");
					out.println("});");
					out.println("});");
					out.println("</script>");

					out.println("<h1>Server and App Status</h1><br/>");
					out.println("Para testar, digite /helloworld ou /datetime ou /seucomando, clique no submit, e veja o resultado na variavel text (campo response). <br/>");
					out.println("No niobot, digite /createcmd seucomando http://seuservidor:porta para criar o comando /seucomando e vincular a chamada ao seu servidor.<br/>");
					out.println("No niobot, digite /seucomando e veja se chamou aqui (F5 para atualizar a pagina).<br/>");
					out.println("Se chamou, deve ter retornado \"Comando nao encontrado\" e, portanto, é hora de implementar de verdade o seu comando.<br/><br/>");

					out.println("<form id=\"form\" method=\"post\">");					
					out.println("type your command: <input type=\"text\" id=\"text\" name=\"text\" value=\"\">");										
					out.println("<input id=\"btnSubmit\" name=\"btnSubmit\" type=\"button\" value=\"Submit\"><br/><br/>");
					out.println("response: <textarea id=\"response\" name=\"response\" rows=\"5\" cols=\"40\"></textarea><br/>");
					out.println("some field: <input type=\"text\" id=\"someFieldNameId\" name=\"someFieldNameId\" value=\"some value\"><br/>");
					out.println("</form><br/><br/>");

					main.execute("INSERT INTO Log (WHO, JSON) VALUES ('test', '----------------------------------')");
					ResultSet rs = main.executeQuery("SELECT ID, WHO, WHEN, JSON FROM Log ORDER BY ID DESC");
					
					out.println("Ultimas 50 chamadas para debug. (Aperte F5)<br/><br/>");

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
				
				main.cleanLog();
				
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
