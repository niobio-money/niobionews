package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class Braziliex implements Runnable {

	private static String URL = "https://braziliex.com/api/v1/public/ticker";
	//private static DecimalFormat df2 = new DecimalFormat(".##");
	
	public static double cotacao = 0;
	
    private static String post(String targetURL) {
        HttpsURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpsURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuffer response = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }	
	
    public void run() {
    	System.setProperty("http.agent", "Chrome");
        while(true) {
        	try {        		        		
            	String start = post(URL);
            	start = start.substring(start.indexOf("nbr_brl"));
            	String x = start.substring(9, start.indexOf("}")+1);
            	JSONObject json = new JSONObject(x);            	
            	//df2.format(d);
            	cotacao = json.getDouble("highestBid");
            	Thread.sleep(10000);
        	} catch (Exception e) {
        		System.err.println(e.getMessage());
        	}        	
        }
    }

    public static void main(String args[]) {
        (new Thread(new Braziliex())).start();
    }

}