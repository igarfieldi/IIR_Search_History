package core;

import javax.json.JsonObject;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a single search result.
 * Stores all the relevant information including a click counter to obtain
 * information about the visits from the user.
 * @author Florian Bethe
 *
 */
public class SearchResult implements Serializable {
	private static final long serialVersionUID = 483580055070483160L;
	
	private String query;
	private URL url;
	private String headline;
	private String summary;
	private int clickCounter;
	
	public SearchResult(String query, URL url, String headline, String summary) {
		this.query = query;
		this.url = url;
		this.headline = headline;
		this.summary = summary;
		this.clickCounter = 0;
	}
	
	public SearchResult(String query, JsonObject obj) throws MalformedURLException {
		this(query, new URL(obj.getString("Url")), obj.getString("Title"), obj.getString("Description"));

		// DEBUG
		//System.out.println(obj.toString());
	}
	
	public String getQuery() {
		return query;
	}
	
	public URL getUrl() {
		return url;
	}
	
	public String getHeadline() {
		return headline;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public int getClickCounter() {
		return clickCounter;
	}
	
	public void incrementClickCounter() {
		this.clickCounter++;
	}
}
