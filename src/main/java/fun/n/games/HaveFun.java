package fun.n.games;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HaveFun {

	private static final Logger log = LoggerFactory.getLogger(HaveFun.class);

	public static void main(String[] args) {

		// TODO We should create the REST url from configuration, rather than
		// having a static url.
		String stockRestUrl = "http://finance.google.com/finance/info?client=ig&q=NSE:NIFTY,NSE:RELIANCE";

		String stockDataRawString = RestService.getRestDataAsString(stockRestUrl);

		// TODO Figure this out. Why is the first two characters // ?
		String stockDataJson = stockDataRawString.substring(3);
		/*
		 * log.debug("The cleaned up JSON ..."); log.debug(stockDataJson);
		 */

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode tickerArray = mapper.readTree(stockDataJson);
			log.debug("Is this an array? [{}]", tickerArray.isArray());

			for (final JsonNode tickerNode : tickerArray) {
				String ticker = tickerNode.get("t").asText();
				log.debug("Ticker [{}]", ticker);

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
