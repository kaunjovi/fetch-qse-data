package fun.n.games;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestService {

	private static final Logger log = LoggerFactory.getLogger(RestService.class);

	public static String getRestDataAsString(String stockRestUrl) {
		log.debug("Fetching data from [{}].", stockRestUrl);

		StringBuffer jsonResponse = new StringBuffer();
		HttpURLConnection conn = null;

		try {

			conn = createConnectionToURL(stockRestUrl);

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				jsonResponse.append(output);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
		}

		log.debug("The data fetched is ...");
		log.debug(jsonResponse.toString());

		return jsonResponse.toString();

	}

	private static HttpURLConnection createConnectionToURL(String restUrl)
			throws MalformedURLException, IOException, ProtocolException {
		URL url = new URL(restUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		return conn;
	}

}
