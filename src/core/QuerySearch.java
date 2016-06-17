package core;

import java.util.ArrayList;
import java.util.Date;

public abstract class QuerySearch {
	protected String query;
	protected ArrayList<SearchResult> results; 
	private Date time;
	protected int maxResults;
	
	public QuerySearch(String query) {
		this(query, 10);
	}
	
	public QuerySearch(String query, int maxResults) {
		this.query = query;
		this.results = new ArrayList<SearchResult>();
		this.time = null;
		this.maxResults = maxResults;
	}
	
	public void findQuery() throws Exception {
		this.time = new Date(System.currentTimeMillis());
		this.queryEngine();
	}
	
	protected abstract void queryEngine() throws Exception;
	
	public ArrayList<SearchResult> getResults() {
		return results;
	}
	
	public String getQuery() {
		return query;
	}
	
	public Date getTimestamp() {
		return time;
	}
	
	public int getMaxResults() {
		return maxResults;
	}
}
