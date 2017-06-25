package fun.n.games;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class FetchQSEData {

	private static final Logger log = LoggerFactory.getLogger(FetchQSEData.class);

	private static final String REST_URL = "http://services.groupkt.com/country/get/all";
	private static final String MONGODB1_URI = "mongodb://alibaba:40chor@ds131512.mlab.com:31512/pine";

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
		log.debug("Hello world. check check.");

		// ===========

		// Create seed data

		List<Document> seedData = new ArrayList<Document>();

		seedData.add(new Document("decade", "1970s").append("artist", "Debby Boone")
				.append("song", "You Light Up My Life").append("weeksAtOne", 10));

		seedData.add(new Document("decade", "1980s").append("artist", "Olivia Newton-John").append("song", "Physical")
				.append("weeksAtOne", 10));

		seedData.add(new Document("decade", "1990s").append("artist", "Mariah Carey").append("song", "One Sweet Day")
				.append("weeksAtOne", 16));

		// Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname

		MongoClientURI uri = new MongoClientURI(MONGODB1_URI);
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(uri.getDatabase());

		/*
		 * First we'll add a few songs. Nothing is required to create the songs
		 * collection; it is created automatically when we insert.
		 */

		MongoCollection<Document> songs = db.getCollection("songs");

		// Note that the insert method can take either an array or a document.

		songs.insertMany(seedData);

		/*
		 * Then we need to give Boyz II Men credit for their contribution to the
		 * hit "One Sweet Day".
		 */

		Document updateQuery = new Document("song", "One Sweet Day");
		songs.updateOne(updateQuery, new Document("$set", new Document("artist", "Mariah Carey ft. Boyz II Men")));

		/*
		 * Finally we run a query which returns all the hits that spent 10 or
		 * more weeks at number 1.
		 */

		Document findQuery = new Document("weeksAtOne", new Document("$gte", 10));
		Document orderBy = new Document("decade", 1);

		MongoCursor<Document> cursor = songs.find(findQuery).sort(orderBy).iterator();

		try {
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				System.out.println("In the " + doc.get("decade") + ", " + doc.get("song") + " by " + doc.get("artist")
						+ " topped the charts for " + doc.get("weeksAtOne") + " straight weeks.");
			}
		} finally {
			cursor.close();
		}

		// Since this is an example, we'll clean up after ourselves.

		// songs.drop();

		// Only close the connection when your app is terminating

		client.close();

	}

}
