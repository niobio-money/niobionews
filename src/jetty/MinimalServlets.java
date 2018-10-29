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

public class MinimalServlets {
	
	private static Main main;
	
    public static void main( String[] args ) throws Exception
    {
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
    public static class HelloServlet extends HttpServlet
    {
        @Override
        protected void doGet( HttpServletRequest request,
                              HttpServletResponse response ) throws ServletException,
                                                            IOException
        {   
        	try {
        		PrintWriter out = response.getWriter();
        		main = Main.getInstance();
        		
	            response.setContentType("text/html");
	            response.setStatus(HttpServletResponse.SC_OK);
	            response.getWriter().println("<h1>Hello from HelloServlet</h1>");
	            response.getWriter().println("<table>");
        		
				main.execute("INSERT INTO Log (WHO, JSON) VALUES ('test', 'test')");
				ResultSet rs = main.executeQuery("SELECT ID, WHO, WHEN, JSON FROM Log ORDER BY ID DESC");
				
				while (rs.next()) {
					out.println("<tr>");
					out.println("<td>" + rs.getString("ID") + "</td>");
					out.println("<td>" + rs.getString("WHO") + "</td>");
					out.println("<td>" + rs.getString("WHEN") + "</td>");
					out.println("<td>" + rs.getString("JSON") + "</td>");
					out.println("</tr>");
				}
				out.println("</table>");
			} catch (SQLException e) {
				e.printStackTrace();
			}
            
        }
    }
}
