package fun.n.games;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataConverterService {

	private static final Logger log = LoggerFactory.getLogger(DataConverterService.class);

	public static List<Document> convertJsonArrayToDocumentArray(String stockDataJsonString) {
		List<Document> tickerInMongoFormat = new ArrayList<Document>();
		int numberOfTickersConvertedIntoMongoFormat = 0;

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tickerArray = mapper.readTree(stockDataJsonString);

			if (tickerArray.isArray()) {

				for (final JsonNode tickerNode : tickerArray) {

					/*
					 * String ticker = tickerNode.get("t").asText(); String
					 * exchange = tickerNode.get("e").asText(); String
					 * lastPriceTraded = tickerNode.get("l").asText(); String
					 * lastTradeDateTime = tickerNode.get("lt").asText();
					 * log.debug("Ticker [{}]", ticker);
					 */

					// TODO Figure out how to single out a particular column as
					// id. If that happens we we can upsert in bulk.
					tickerInMongoFormat.add(new Document("_id", tickerNode.get("id").asText())
							.append("ticker", tickerNode.get("t").asText())
							.append("exchange", tickerNode.get("e").asText())
							.append("lastPriceTraded", tickerNode.get("l").asText())
							.append("lastTradeDateTime", tickerNode.get("lt").asText()));
					numberOfTickersConvertedIntoMongoFormat++;

				}
			} else {
				log.debug(
						"We did not get proper JSON datase. Check the dataset received. There was no array of tickers.");

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.debug("We converted [{}] ticker data to MongoDocuments format.", numberOfTickersConvertedIntoMongoFormat);
		return tickerInMongoFormat;
	}

}
