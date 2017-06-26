package fun.n.games;

import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoService {

	private static final Logger log = LoggerFactory.getLogger(MongoService.class);
	private static final String MONGODB1_URI = "mongodb://alibaba:40chor@ds131512.mlab.com:31512/pine";
	private static final String TICKERS = "TICKERS";

	public static int bulkUpsert(List<Document> tickerInMongoFormat) {
		// TODO Auto-generated method stub

		MongoClientURI uri = new MongoClientURI(MONGODB1_URI);
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(uri.getDatabase());

		/*
		 * Connect to mongo db. Connect to the countries. Drop the existing
		 * Data. Insert the fresh set of data
		 */
		MongoCollection<Document> tickers = db.getCollection(TICKERS);
		// tickers.drop();
		tickers.insertMany(tickerInMongoFormat);

		client.close();

		return 0;
	}

}
