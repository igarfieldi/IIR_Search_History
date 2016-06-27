package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Stores the search history of the user.
 * @author Florian Bethe, Tino Liebusch
 */
public class SearchHistory {
	private ArrayList<QuerySearch> history;
	private String historyPath;
	private static final String DEFAULT_HISTORY_PATH = "history.ser";

	/**
	 * Default constructor.
	 * Sets the history path to the default one.
	 */
	public SearchHistory() {
		this(DEFAULT_HISTORY_PATH);
	}
	
	/**
	 * Constructor.
	 * Uses (or creates) history at the provided location.
	 * @param fileName History file location
	 */
	public SearchHistory(String fileName) {
		historyPath = fileName;
		history = loadHistory();
	}

	/**
	 * Loads the history at the current history location (if present).
	 * @return List of {@link QuerySearch QuerySearch} representing the history
	 */
	public ArrayList<QuerySearch> loadHistory() {
		return this.loadHistory(historyPath);
	}
	/**
	 * Loads and deserializes history from file.
	 * @param fileName File path to history
	 * @return returns loaded history
     */
	@SuppressWarnings("unchecked")
	public ArrayList<QuerySearch> loadHistory(String fileName){
		File historyFile = new File(fileName);
		
		// Check if the file exists and if not, create it
		try {
			if(!historyFile.exists()) {
				historyFile.createNewFile();
				return new ArrayList<QuerySearch>();
			}
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
		
		// Try-with-resources for reading an existing history file
		try(
				final FileInputStream fis = new FileInputStream(fileName);
				final ObjectInputStream ois = new ObjectInputStream(fis)
		) {
			
			return (ArrayList<QuerySearch>) ois.readObject();
		} catch(IOException i) {
			i.printStackTrace();
		} catch(ClassNotFoundException c) {
			System.err.println("History class not found");
			c.printStackTrace();
		}
		return null;
	}

	/**
	 * Saves history object to file at current history file location.
     */
	public void saveHistory() {
		this.saveHistory(historyPath);
	}
	
	/**
	 * Saves history object to file.
	 * @param fileName File path where history shall be saved.
     */
	public void saveHistory(String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(history);
			oos.close();
			fos.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Adds an element to the search history.
	 * It is assumed that this search has occurred after the latest historie'd search.
	 * @param search Search to append
	 */
	public void addEntry(QuerySearch search) {
		history.add(search);
		this.saveHistory(historyPath);
	}
	
	/**
	 * Returns up to the n most recent searches in the history.
	 * @param maxAmount number of desired searches
	 * @return List of most recent searches
	 */
	public List<QuerySearch> getRecentSearches(int maxAmount) {
		if(history.isEmpty())
			return history;
		maxAmount = Math.min(maxAmount, history.size());
		return history.subList(history.size() - maxAmount, history.size());
	}
	
	/**
	 * Returns the entire search history ordered by date.
	 * @return List of searches
	 */
	public List<QuerySearch> getHistoryDateOrdered() {
		return this.getHistoryDateOrdered(null, null);
	}
	
	/**
	 * Returns a subset of the history ordered by date.
	 * @param begin Earliest date to include in result
	 * @param end Latest date to include in result
	 * @return List of searches within date range
	 */
	public List<QuerySearch> getHistoryDateOrdered(Date begin, Date end) {
		// If we don't have a history yet or no limitations on the date, we're done
		if((begin == null) && (end == null))
			return history;
		if(history.size() == 0)
			return history;

		int beginIndex = -1;
		int endIndex = -1;
		
		// If only one date is missing, the other one will be set to the first/latest possible date
		if(begin == null) {
			begin = new Date(0);
			beginIndex = 0;
		}
		if(end == null) {
			end = new Date(System.currentTimeMillis());
			endIndex = history.size() - 1;
		}
		
		// Determine the indices of the history by iterating through the list
		// and finding the first entries violating the date constraints
		// Todo: Is this right? The for loop checks for beginIndex<0 and then you check for beginIndex<0 again? Not clear what you do here at the moment
		for(int i = 0; (i < history.size()) && (beginIndex < 0) && (endIndex < 0); i++) {
			if(beginIndex < 0) {
				if(!history.get(i).getTimestamp().before(begin))
					beginIndex = i;
			}
			if(endIndex < 0) {
				if(history.get(i).getTimestamp().after(end))
					endIndex = i-1;
			}
		}
		
		// TODO: is this needed/correct? E.g. if the end index wasn't set because everything was earlier?
		if(beginIndex < 0)
			beginIndex = 0;
		
		if(endIndex < 0)
			endIndex = history.size() - 1;
		
		return history.subList(beginIndex, endIndex);
	}
}
