package your;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Main extends jetty.Main {
	// if you need request (session?) or response, use this main method
	public void main(String[] args, HttpServletRequest request, HttpServletResponse response) {
		print(request.getSession().getId());
	}
	
	public void main(String[] args) {
		switch (args[0]) {
			case "/hello":
				print("OLÁ MUNDO!");
				break;
			case "/now":
				print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
				break;				
			default:
				print("Comando não encontrado");
				break;
		}		
	}
}
