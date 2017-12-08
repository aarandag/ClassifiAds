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

public class Agente1 extends Agent{

	protected void setup() {
		addBehaviour(new RetrievalBehaviour());
	}
	
	private class RetrievalBehaviour extends Behaviour{
		private String url;
		private boolean fin;
		
		public void onStart() {
			url = "http://www.marca.com/";
			fin = false;
		}
		
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
	                        trim(src.attr("alt"), 20));
	            else
	                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
	        }
	        
	        /* Retrieve links */
	        print("\nLinks: (%d)", links.size());
	        for (Element link : links) {
	            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
	        }
	        
	        /* Finalize the behaviour */
	        if(media.size() > 0 && links.size() > 0)
	        	fin = true;
		}
		
		private void print(String msg, Object... args) {
	        System.out.println(String.format(msg, args));
	    }

	    private String trim(String s, int width) {
	        if (s.length() > width)
	            return s.substring(0, width-1) + ".";
	        else
	            return s;
	    }
		
		public boolean done() {
			return fin;
		}
		
		public int onEnd() {
			doDelete();
			return 0;
		}
	}
	
	protected void takeDown() {
		System.out.println(getLocalName() + " frees resources");
	}
}
