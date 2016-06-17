package core;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class BingSearch extends QuerySearch {

	private final static String bingAccountKey = "auUSumxaZySXi95rzYxhOSUpFFHG2Zj4Gx1PkphXRlw=";
	private final static String bingUrlPattern = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%%27%s%%27&$format=json&$top=%s";

	public BingSearch(String query) {
		super(query);
	}
	
	protected void queryEngine() throws IOException {
		// Encode the account key into the proper format for bing
        String accountKeyEncoder = Base64.getEncoder().encodeToString(
        		(bingAccountKey + ":" + bingAccountKey).getBytes());
        
        // Assemble the URL with query, max. results etc.
        URL requestUrl = new URL(String.format(bingUrlPattern, URLEncoder.encode(query, "UTF-8"), Integer.toString(this.getMaxResults())));
        System.out.println(requestUrl.toString());
        
        // Connect to the bing server
        URLConnection bingConnection = requestUrl.openConnection();
        bingConnection.setRequestProperty("Authorization", "Basic " + accountKeyEncoder);
        
        this.results = new ArrayList<SearchResult>();
        
        // Read the JSON data from the connection stream
        try (final JsonReader reader = Json.createReader(bingConnection.getInputStream())) {
        	JsonObject jsonData = reader.readObject();
        	JsonArray jsonResults = jsonData.getJsonObject("d").getJsonArray("results");
        	
        	// Iterate over the 'results' objects from bing
        	// Don't ask me why the standard JsonArray iterator iterates over values,
        	// I'd also much rather use for-each...
        	for(int i = 0; i < jsonResults.size(); i++) {
        		results.add(new SearchResult(this.query, jsonResults.getJsonObject(i)));
        	}
        	
        	// DEBUG
        	System.out.println(jsonData.toString());
        }
	}
}