package jetty;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.jetty.servlet.*;
import org.json.*;

import your.*;

public class Server {

	private static Main main;

	public static void main(String[] args) throws Exception {
		System.setProperty("derby.stream.error.field", "jetty.DerbyUtil.DEV_NULL");
		org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(YourMain.SERVER_PORT);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
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
				String requestUri = request.getRequestURI();
				if (requestUri.contains("favicon.ico")) return;
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
					try {
						main.main(json.getString("text").split(" "), request, response);
					} catch (Exception e) {
						json.put("error", e.getMessage());
					} finally {
						main.insert(request.getSession().getId(), json.toString());
					}

					out.println(json);

				} else {
					response.setContentType("text/html");
					main.execute("INSERT INTO Log (WHO, JSON) VALUES ('test', '----------------------------------')");					
					
					String html = "";
					switch(requestUri) {						
						case "/status":
						case "/status.html":
							html = loadHtml("./html/status.html");
							String table = "";							
							table += "<table>";
							ResultSet rs = main.executeQuery("SELECT ID, WHO, WHEN, JSON FROM Log ORDER BY ID DESC");
							while (rs.next()) {
								table += "<tr>";
								table += "<td>" + rs.getString("ID") + "</td>";
								table += "<td>" + rs.getString("WHO") + "</td>";
								table += "<td>" + rs.getString("WHEN") + "</td>";
								table += "<td>" + rs.getString("JSON") + "</td>";
								table += "</tr>";
							}					
							html = html.replace("<!--TB_RESULT-->", table);							
							break;
						case "/":
						case "/index.html":
							html = loadHtml("./html/index.html");
							break;
						case "/post":
						case "/post.html":
							html = loadHtml("./html/post.html");
							break;							
					}
										
					out.print(html);
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
		
		private String loadHtml(String filePath) throws IOException {
			return new String(Files.readAllBytes(Paths.get(filePath)),
					StandardCharsets.UTF_8);
		}
	}
}
