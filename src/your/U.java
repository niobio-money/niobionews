package your;

import java.util.*;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.*;
import org.apache.http.util.*;
import org.json.*;

import util.*;


public final class U {

	static Random r = new Random();

	private static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	private static CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

	static JSONObject httpRequest(String url, JSONObject data) {
		JSONObject r = null;
		try {
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(data.toString());
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			String json_string = EntityUtils.toString(response.getEntity());
			r = new JSONObject(json_string);

		} catch (Exception ex) {
			//U.print(ex.toString());
		}
		return r;
	}

	static String formatCryptoMoneyHumanToMachine(String balance) {
		String[] value = null; // ex: 1.5 -> [0] = 1, [1] = 5 
		String result = null;
		if (balance != null && !balance.trim().equals("")) {
			if (balance.contains(".")) { // value has cents
				value = balance.split("\\.");
				if (value.length == 2) {
					if (value[0].matches("[0-9]+") && value[1].matches("[0-9]+")) {
						result = (value[0].equals("0") ? "" : value[0])
								+ String.format("%-8s", value[1]).replace(' ', '0');
					}
				}
			} else if (balance.matches("[0-9]+")) {
				result = balance + "00000000";
			}
		}

		while (result != null && result.startsWith("0")) {
			result = result.substring(1, result.length());
		}
		return result;
	}

	static String formatCryptoMoneyMachineToHuman(String balance) {
		String result = null;
		if (balance != null && !balance.trim().equals("") && balance.matches("[0-9]+")) {
			if (balance.length() > 8) {
				result = balance.substring(0, balance.length() - 8) + "."
						+ balance.substring(balance.length() - 8, balance.length());
			} else if (balance.length() < 8 && !"0".equals(balance)) {
				result = "0." + String.format("%08d", Long.parseLong(balance));
			} else {
				result = "0." + balance;
			}
		}
		return result;
	}

	static String generatePaymentId() {
		int numchars = 64;
		StringBuffer sb = new StringBuffer();
		while (sb.length() < numchars) {
			sb.append(Integer.toHexString(r.nextInt()));
		}
		return sb.toString().substring(0, numchars).toUpperCase();
	}
	
	static Integer random() {
		return U.r.nextInt(Integer.SIZE - 1);
	}

	public static int centsToNBR(int cents) {
		return (int)Math.floor(cents/(Braziliex.cotacao*100));
	}	

}
