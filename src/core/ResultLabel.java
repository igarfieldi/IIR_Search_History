package core;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleText;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 * Label visualizing a search result.
 * The headline is a clickable link opening the result in the browser.
 * @author Florian Bethe
 *
 */
public class ResultLabel extends JLabel {

	private static final long serialVersionUID = 4221537970955575561L;
	
	protected final static Pattern linkPattern = Pattern.compile("<a href=\"(.*?)\">(.*?)</a>");
	protected final static Pattern linkOpenPattern = Pattern.compile("<a href=\"(.*?)\">");
	protected final static Pattern linkClosePattern = Pattern.compile("</a>");
	
	protected Link resultLink;
	
	public ResultLabel(SearchResult result, int horAlignment) {
		this(result, horAlignment, true);
	}
	
	public ResultLabel(SearchResult result, int horAlignment, boolean expressiveLabel) {
		super("", null, horAlignment);
		
		if(expressiveLabel) {
			this.setText("<html>&nbsp;<a href=\"" + result.getUrl().toString() + "\">" + result.getHeadline()
			+ "</a><p>" + result.getSummary() + "</p></html>");
		} else {
			this.setText("<html>&nbsp;<a href=\"" + result.getUrl().toString() + "\">" + result.getHeadline()
			+ "</a></html>");
		}
		
		resultLink = new Link(result.getUrl().toString(), 2, result.getHeadline().length() + 1);
		// DEBUG
		//System.out.println(result.getUrl() + " " + result.getHeadline().length() + " " + result.getUrl().toString().length());
		
		// Listen for clicks on the links in the label
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if(ResultLabel.this.isOverLink(evt.getPoint())) {
					try {
						Desktop.getDesktop().browse(new URI(resultLink.url));
						result.incrementClickCounter();
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null,
								"Failed to open link in browser",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		// (For style points) change the cursor when hovering a link
		this.addMouseMotionListener(new MouseAdapter() {
			// Previous mouse hover state
			boolean wasOverLink = false;
			
			@Override
			public void mouseMoved(MouseEvent evt) {
				boolean currOverLink = ResultLabel.this.isOverLink(evt.getPoint());
				
				// Switch cursor only if hover state changes
				if(currOverLink != wasOverLink) {
					if(currOverLink) {
						ResultLabel.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					} else {
						ResultLabel.this.setCursor(Cursor.getDefaultCursor());
					}
					wasOverLink = currOverLink;
				}
			}
		});
	}
	
	@Override
	public Dimension getPreferredSize() {
		// TODO: how to incorporate scrolling bar?
		//System.out.println(this.getParent().getSize());
		Dimension pref = super.getPreferredSize();
		return new Dimension(this.getParent().getWidth(), pref.height);
	}
	
	@Override
	public Dimension getMinimumSize() {
		return this.getPreferredSize();
	}
	
	@Override
	public Dimension getMaximumSize() {
		return this.getPreferredSize();
	}
	
	/**
	 * Returns the link corresponding to a mouse location, if existing.
	 * @param point Mouse point
	 * @return Link at the location
	 */
	protected boolean isOverLink(Point point) {
		// TODO: border is also counted as link...
		// Current workaround: add whitespace in front of the link (eugh...)
		
		// Obtain the context for the label and the label text
		AccessibleContext context = this.getAccessibleContext();
		if(context instanceof AccessibleJLabel) {
			AccessibleJLabel label = (AccessibleJLabel) context;
			AccessibleText text = label.getAccessibleText();
			
			if(text != null) {
				// Get the corresponding character index and check whether it is part of the link
				int position = label.getIndexAtPoint(point);
				
				if(resultLink.isInside(position)) {
					// DEBUG
					//System.out.println(link.url + " " + link.start + " " + link.end + " " + point.getX() + " " + point.getY() + " " + position);
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Represents a link in the label.
	 * F**k encapsulation, this is basically a C struct.
	 * @author Florian Bethe
	 *
	 */
	protected class Link {
		public String url;
		public int start;
		public int end;
		
		public Link(String url, int start, int end) {
			this.url = url;
			this.start = start;
			this.end = end;
		}
		
		public boolean isInside(int position) {
			return (position >= start) && (position <= end);
		}
	}
}
