package jetty;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.jetty.servlet.*;
import org.json.*;

import util.Braziliex;
import your.*;

public class Server {

	private static DecimalFormat df2 = new DecimalFormat(".##");
	private static Main main;

	public static void main(String[] args) throws Exception {
		System.setProperty("derby.stream.error.field", "jetty.DerbyUtil.DEV_NULL");
		org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(YourMain.SERVER_PORT);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.setResourceBase(System.getProperty("java.io.tmpdir"));
		server.setHandler(context);
		context.addServlet(HelloServlet.class, "/");
		main = Main.getInstance();
		Braziliex.main(args);
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
					ResultSet rs = null;
					String precoNBR = null;
					switch(requestUri) {						
						case "/status":
						case "/status.html":
							html = loadHtml("./html/status.html");
							String table = "";							
							table += "<table>";
							rs = main.executeQuery("SELECT ID, WHO, WHEN, JSON FROM Log ORDER BY ID DESC");
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
							String newsSpace = html.substring(html.indexOf("<!--NEWS-->"), html.indexOf("<!--/NEWS-->"));
							rs = main.executeQuery("SELECT * FROM NEWS ORDER BY ID DESC");
							String all = "";
							String each = "";
							precoNBR = "";
							while (rs.next()) {
								precoNBR = "" + (int)Math.floor(rs.getDouble("PRECO")/(Braziliex.cotacao*100));								
								each = newsSpace.replace("URL", rs.getString("URL"));
								each = each.replace("TITULO", rs.getString("TITULO"));
								each = each.replace("LIDE", rs.getString("LIDE"));
								each = each.replace("PRECO", rs.getString("PRECO"));
								each = each.replace("NIOBIOSNBR", precoNBR);
								each = each.replace("NAOCURTI", rs.getString("NAOCURTI"));
								each = each.replace("CURTI", rs.getString("CURTI"));
								each = each.replace("SCAM", rs.getString("SCAM"));
								each = each.replace("DATACRIACAO", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("DATACRIACAO")));
								all += each;
							}
							html = html.replace(newsSpace, all);							
							break;
						case "/post":
						case "/post.html":
							html = loadHtml("./html/post.html");
							break;
						default:
							html = loadHtml("./html/post.html");
							requestUri = requestUri.substring(1);
							rs = main.executeQuery("SELECT * FROM News WHERE url = '" + requestUri + "'");
							precoNBR = "";
							if (rs.next()) {
								precoNBR = "" + (int)Math.floor(rs.getDouble("PRECO")/(Braziliex.cotacao*100));
								html = html.replace("TITULO", rs.getString("TITULO"));
								html = html.replace("CARTEIRA", rs.getString("CARTEIRA"));
								html = html.replace("LIDE", rs.getString("LIDE"));
								html = html.replace("TEXTO", rs.getString("TEXTO"));
								html = html.replace("PRECO", rs.getString("PRECO"));
								html = html.replace("NIOBIOSNBR", precoNBR);
								html = html.replace("NAOCURTI", rs.getString("NAOCURTI"));
								html = html.replace("CURTI", rs.getString("CURTI"));
								html = html.replace("SCAM", rs.getString("SCAM"));
								html = html.replace("DATACRIACAO", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("DATACRIACAO")));								
							}
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
