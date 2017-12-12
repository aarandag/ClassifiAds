/**
 * Agent that retrieves advertisements from web
 */
package domain;

/**
 * @author Alberto Aranda García y Cristian Gómez Portes
 *
 */

/* imports of jade */
import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

/* imports of jsoup */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Agente extends Agent{
	Object[] args;
	
	protected void setup() {
		args = getArguments();
		
		/* Get the url */
		String url = (String) args[0];
		
		/* Run behaviour */
		addBehaviour(new RetrievalBehaviour(url));
	}
	
	private class RetrievalBehaviour extends Behaviour{
		private String url;
		private boolean end;
		
		public RetrievalBehaviour(String url) {
			this.url = url;
		}
		
		/**
		 * Initialization of the variables
		 */
		public void onStart() {
			end = false;
		}
		
		/**
		 * Run the process of the agent
		 */
		public void action() {
	        print("Fetching %s...", url);

	        Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
	        Elements links = doc.select("a[href]");
	        Elements media = doc.select("[src]");
	        
	        /* Retrieve media */
	        print("\nMedia: (%d)", media.size());
	        for (Element src : media) {
	            if (src.tagName().equals("img"))
	                print(" * %s: <%s> %sx%s (%s)",
	                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
	                        src.attr("alt"));
	            else
	                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
	        }
	        
	        /* Retrieve links */
	        print("\nLinks: (%d)", links.size());
	        for (Element link : links) {
	            print(" * a: <%s>  (%s)", link.attr("abs:href"), link.text());
	        }
	        
	        /* Finalize the behaviour */
	        if(media.size() > 0 && links.size() > 0)
	        	end = true;
		}
		
		/**
		 * Print message in a predetermine format
		 * @param msg
		 * @param args
		 */
		private void print(String msg, Object... args) {
	        System.out.println(String.format(msg, args));
	    }
		
		/**
		 * If end is equal to true, the behaviour finalizes
		 */
		public boolean done() {
			return end;
		}
		
		/**
		 * Kill the agent
		 */
		public int onEnd() {
			doDelete();
			return 0;
		}
	}
	
	/**
	 * Free resources from the agent
	 */
	protected void takeDown() {
		System.out.println(getLocalName() + " frees resources");
	}
}
