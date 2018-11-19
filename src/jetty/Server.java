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
				if (request.getRequestURI().contains("favicon.ico")) return;
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
					String html = new String(Files.readAllBytes(Paths.get("./html/index.html")),
							StandardCharsets.UTF_8);
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
	}
}
