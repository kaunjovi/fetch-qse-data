package fun.n.games;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchQSEData {

	private static final Logger log = LoggerFactory.getLogger(FetchQSEData.class);

	private static final String REST_URL = "http://services.groupkt.com/country/get/all";

	public static void main(String[] args) {
		log.debug("Hello world.");

		try {

			URL url = new URL(REST_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			log.debug("Fetched from server...");

			while ((output = br.readLine()) != null) {
				log.debug(output);

			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}
