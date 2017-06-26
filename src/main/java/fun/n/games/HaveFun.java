package fun.n.games;

import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HaveFun {

	private static final Logger log = LoggerFactory.getLogger(HaveFun.class);

	public static void main(String[] args) {

		// TODO We should create the REST url from configuration, rather than
		// having a static url.
		String stockRestUrl = "http://finance.google.com/finance/info?client=ig&q=NSE:NIFTY,NSE:RELIANCE,NSE:ABB,NSE:ACC,NSE:ADANIPORTS";

		String stockDataRawString = RestService.getRestDataAsString(stockRestUrl);
		// TODO Figure this out. Why is the first two characters // ?
		String stockDataJsonString = stockDataRawString.substring(3);

		List<Document> tickerInMongoFormat = DataConverterService.convertJsonArrayToDocumentArray(stockDataJsonString);

		MongoService.bulkUpsert(tickerInMongoFormat);

	}

}
