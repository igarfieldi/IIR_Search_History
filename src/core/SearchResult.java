package core;

import javax.json.JsonObject;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a single search result.
 * Stores all the relevant information including a click counter to obtain
 * information about the visits from the user.
 * @author Florian Bethe, Tino Liebusch
 *
 */
public class SearchResult implements Serializable {
	private static final long serialVersionUID = 483580055070483160L;
	
	private String query;
	private URL url;
	private String headline;
	private String summary;
	private int clickCounter;
	
	/**
	 * Constructor.
	 * Creates a new search result from the given components.
	 * @param query Search query which produced the result
	 * @param url URL of the result page
	 * @param headline Headline of the result page
	 * @param summary Summary (created by the engine) of the result page
	 */
	public SearchResult(String query, URL url, String headline, String summary) {
		this.query = query;
		this.url = url;
		this.headline = headline;
		this.summary = summary;
		this.clickCounter = 0;
	}
	
	/**
	 * Constructor.
	 * Parses a result for the given query from a JSON object. The components have to stored in the values
	 * 'Url', 'Title' and 'Description'!
	 * @param query Search query which produced the result 
	 * @param obj JSON object containing the result
	 * @throws MalformedURLException Throws if the resulting URL is malformed
	 */
	public SearchResult(String query, JsonObject obj) throws MalformedURLException {
		this(query, new URL(obj.getString("Url")), obj.getString("Title"), obj.getString("Description"));
	}
	
	/**
	 * Gets the associated search query.
	 * @return Search query
	 */
	public String getQuery() {
		return query;
	}
	
	/**
	 * Gets the result page's URL.
	 * @return URL of the page
	 */
	public URL getUrl() {
		return url;
	}
	
	/**
	 * Gets the result page's headline.
	 * @return Headline of the page
	 */
	public String getHeadline() {
		return headline;
	}
	
	/**
	 * Gets the (engine-created) summary of the result page.
	 * @return Summary of the page
	 */
	public String getSummary() {
		return summary;
	}
	
	/**
	 * Gets the current click counter of the result.
	 * This tracks how often the user has clicked on the result. Currently no differentiation
	 * between clicks in the history and clicks in the engine.
	 * @return Amount of times the result was clicked
	 */
	public int getClickCounter() {
		return clickCounter;
	}
	
	/**
	 * Increments the click counter, indicating that the user clicked on the result.
	 */
	public void incrementClickCounter() {
		this.clickCounter++;
	}
}
