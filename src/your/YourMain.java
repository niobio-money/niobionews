package your;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class YourMain extends jetty.Main {
	
	public static int SERVER_PORT = 9090;
	
	// if you need request (session?) or response, use this main method, otherwise you can remove it
	public void main(String[] args, HttpServletRequest request, HttpServletResponse response) {
		main(args);
	}
	
	public /*not static*/ void main(String[] args) {
		
		switch (args[0]) {
			case "/helloworld":
				print("OLA MUNDO!");
				break;
			case "/datetime":
				print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
				break;				
			default:
				print("Comando nao encontrado");
				break;
		}		
	}

}
