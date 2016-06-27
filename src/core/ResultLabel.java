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

/**
 * Label visualizing a search result.
 * The headline is a clickable hyperlink opening the result in the browser.
 * @author Florian Bethe, Tino Liebusch
 *
 */
public class ResultLabel extends JLabel {

	private static final long serialVersionUID = 4221537970955575561L;
	
	protected Link resultLink;
	
	/**
	 * Constructor.
	 * Creates new label for the given result with the given alignment (from {@link javax.swing.SwingConstants SwingConstants})
	 * and displays the result summary as well.
	 * @param result Search result
	 * @param horAlignment Alignment of label text
	 */
	public ResultLabel(SearchResult result, int horAlignment) {
		this(result, horAlignment, true);
	}
	
	/**
	 * Constructor.
	 * Creates new label for the given result with the given alignment (from {@link javax.swing.SwingConstants SwingConstants}).
	 * If expressiveLabel is false, the result summary will be omitted.
	 * @param result Search result
	 * @param horAlignment Alignment of label text
	 * @param expressiveLabel Off-switch for displaying summary
	 */
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
		Dimension pref = super.getPreferredSize();
		return new Dimension(this.getParent().getWidth(), pref.height);
	}
	
	/**
	 * Checks whether a given point (on screen/label) is over the link.
	 * @param point Mouse point
	 * @return Is point over link
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
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Represents a link in the label.
	 * F*** encapsulation, this is basically a C struct.
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
		
		/**
		 * Checks whether the given position is within the start-end bounds.
		 * Saves some writing.
		 * @param position Position to be checked
		 * @return Is position within start-end bounds
		 */
		public boolean isInside(int position) {
			return (position >= start) && (position <= end);
		}
	}
}
