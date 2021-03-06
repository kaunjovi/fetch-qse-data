package fun.n.games;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class FetchQSEData {

	private static final Logger log = LoggerFactory.getLogger(FetchQSEData.class);

	private static final String REST_URL = "http://services.groupkt.com/country/get/all";
	private static final String MONGODB1_URI = "mongodb://alibaba:40chor@ds131512.mlab.com:31512/pine";
	private static final String COUNTRIES = "COUNTRIES";

	public static void main(String[] args) {

		String jsonDataAsString = getJSONStringfromRESTUrl(REST_URL);

		Country[] countries = getCountryArrayFromJSON(jsonDataAsString);

		List<Document> countriesInMongoFormat = generateDocumentListFromCountries(countries);

		MongoClientURI uri = new MongoClientURI(MONGODB1_URI);
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(uri.getDatabase());

		/*
		 * Connect to mongo db. Connect to the countries. Drop the existing
		 * Data. Insert the fresh set of data
		 */
		MongoCollection<Document> countriesInMongo = db.getCollection(COUNTRIES);
		countriesInMongo.drop();
		countriesInMongo.insertMany(countriesInMongoFormat);

		/*
		 * First we'll add a few songs. Nothing is required to create the songs
		 * collection; it is created automatically when we insert.
		 */

		MongoCollection<Document> songs = db.getCollection("songs");

		// Note that the insert method can take either an array or a document.

		// songs.insertMany(seedData);

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

	private static List<Document> generateDocumentListFromCountries(Country[] countries) {

		List<Document> dataInMongoFormat = new ArrayList<Document>();

		int numberOfCountriesConvertedIntoMongoFormat = 0;
		for (Country c : countries) {
			dataInMongoFormat.add(new Document("name", c.getName()).append("alpha2_code", c.getAlpha2_code())
					.append("alpha3_code", c.getAlpha3_code()));
			numberOfCountriesConvertedIntoMongoFormat++;
		}

		log.debug("Number of counries converted to Mongo format [{}].", numberOfCountriesConvertedIntoMongoFormat);
		return dataInMongoFormat;
	}

	private static Country[] getCountryArrayFromJSON(String jsonDataAsString) {
		ObjectMapper mapper = new ObjectMapper();
		Country[] countries = null;
		try {
			JsonNode fullJsonTree = mapper.readTree(jsonDataAsString);
			// log.debug("Lets check");
			// log.debug(fullJsonTree.toString());

			JsonNode arrayOfCountries = fullJsonTree.get("RestResponse").get("result");
			// log.debug("Lets check again");
			// log.debug(arrayOfCountries.toString());

			// ObjectMapper mapper1 = new ObjectMapper();
			countries = mapper.readValue(arrayOfCountries.toString(), Country[].class);
			for (Country country : countries) {
				log.debug(country.toString());

			}

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return countries;

	}

	private static String getJSONStringfromRESTUrl(String restUrl) {
		StringBuffer jsonResponse = new StringBuffer();
		HttpURLConnection conn = null;

		try {

			conn = createConnectionToURL(restUrl);

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				jsonResponse.append(output);

			}

			// log.debug("This is the JSON object ..");
			// log.debug(jsonResponse.toString());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
		}

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
