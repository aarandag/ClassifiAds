/**
 * Agent that retrieves advertisements from web
 */
package domain;

import jade.core.AID;

/**
 * @author Alberto Aranda García y Cristian Gómez Portes
 *
 */

/* imports of jade */
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.ACLMessage;

/* imports of jsoup */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class RetrieverAgent extends Agent{
	Object[] args;
	
	protected void setup() {
		/* Get arguments */
		args = getArguments();
		
		/* check arguments */
		if(args != null && args.length == 1) {

			/* Get the url */
			String url = "http://" + (String) args[0] + "/";
			
			/* Run behaviour */
			addBehaviour(new RetrievalBehaviour(url));
		}
		else
			System.out.println("Error. You must type an url as an argument");
	}
	
	private class RetrievalBehaviour extends Behaviour{
		private String url;
		private boolean end;
		private MessageTemplate template = null;
		
		/**
		 * Constructor of the RetrievalBehaviour class
		 * @param url
		 */
		public RetrievalBehaviour(String url) {
			this.url = url;
			
			AID aid = new AID();
			aid.setLocalName("processor");
			
			/* Return a template from message that matches with 'Processor' */
			MessageTemplate senderFilter = MessageTemplate.MatchSender(aid);
			
			/* Return a template from message that matches with this performative */
			MessageTemplate informFilter = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			
			/* Build template */
			template = MessageTemplate.and(senderFilter, informFilter);
			
		}
		
		/**
		 * Initialization of the variables
		 */
		public void onStart() {
			end = false;
		}
		
		/**
		 * Run the process of the behaviour
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
	        ArrayList<String> attributes = new ArrayList<String>();
	        
	        /* Retrieve links */
	        print("\nLinks: (%d)", links.size());
	        for (Element link : links) {
	            attributes.add(link.attr("abs:href"));
	        }
	        
	        /* Prepare message */
	        AID aid = new AID();
	        aid.setLocalName("processor");
	        
	        /* Send message if we have found links */
	        if(links.size() > 0) {
	        	
	        	ACLMessage sendMessage = new ACLMessage(ACLMessage.INFORM);
	        	sendMessage.setSender(getAID());
	        	sendMessage.addReceiver(aid);
	        	try {
					sendMessage.setContentObject(attributes);
		        	send(sendMessage);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
	        	
	        	/* Block behaviour until message is received */
	        	blockingReceive(template);
	        	end = true;
	        }
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
		 * Behaviour finalizes if end is equal to true
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
