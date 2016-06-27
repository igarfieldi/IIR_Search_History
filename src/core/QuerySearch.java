package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Query search for a search engine.
 * Contains the query, the returned results, the timestamp and the maximum number of results expected.
 * @author Florian Bethe, Tino Liebusch
 */
public abstract class QuerySearch implements Serializable {
	private static final long serialVersionUID = -8022814148564187528L;
	
	protected String query;
	protected ArrayList<SearchResult> results; 
	private Date time;
	protected int maxResults;
	
	/**
	 * Constructor.
	 * Defaults the maximum number of results to 10.
	 * @param query Query for the search
	 */
	public QuerySearch(String query) {
		this(query, 10);
	}
	
	/**
	 * Constructor.
	 * @param query Query for the search
	 * @param maxResults Maximum number of results for the search
	 */
	public QuerySearch(String query, int maxResults) {
		this.query = query;
		this.time = null;
		this.maxResults = maxResults;
	}
	
	/**
	 * Exposed method to perform the query search.
	 * Utilizes {@link #queryEngine() queryEngine()} to perform the search.
	 * Sets the timestamp for the search and (re-)initializes the result list.
	 * @throws Exception Pass-through from {@link #queryEngine() queryEngine()}
	 */
	public void findQuery() throws Exception {
		this.results = new ArrayList<SearchResult>(maxResults);
		this.time = new Date(System.currentTimeMillis());
		this.queryEngine();
	}
	
	/**
	 * Performs the actual search.
	 * Has to be implemented by the engine-specific search class.
	 * @throws Exception If the engine-specific search fails, an exception should be thrown
	 */
	protected abstract void queryEngine() throws Exception;
	
	/**
	 * Gets the results of the search.
	 * If no search has yet been conducted, null will be returned.
	 * The enforcement of the maximum result number is up to the implementing class!
	 * @return The search results
	 */
	public ArrayList<SearchResult> getResults() {
		return results;
	}
	
	/**
	 * Gets the query of this search.
	 * @return Search query
	 */
	public String getQuery() {
		return query;
	}
	
	/**
	 * Gets the timestamp of this search.
	 * If no search has yet been performed, null is returned instead.
	 * @return Timestamp of the search
	 */
	public Date getTimestamp() {
		return time;
	}
	
	/**
	 * Gets the maximum number of search results.
	 * @return maximum number of search results
	 */
	public int getMaxResults() {
		return maxResults;
	}
}
