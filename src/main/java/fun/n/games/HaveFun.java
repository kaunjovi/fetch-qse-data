package fun.n.games;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HaveFun {

	private static final Logger log = LoggerFactory.getLogger(HaveFun.class);

	public static void main(String[] args) {
		log.debug("Hello again.");

		// TODO We should create the REST url from configuration, rather than
		// having a static url.
		String stockRestUrl = "http://finance.google.com/finance/info?client=ig&q=NSE:NIFTY,NSE:RELIANCE";

		String stockDataRawString = RestService.getRestDataAsString(stockRestUrl);

	}
}
