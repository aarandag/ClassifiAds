/**
 * Agent that retrieves advertisements from web
 */
package domain;

/* imports of jade */
import jade.core.AID;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alberto Aranda García
 * @author Cristian Gómez Portes
 *
 */

public class RetrieverAgent extends Agent{
	private Object [] args;

	protected void setup() {
		/* Get arguments */
		args = getArguments();

		/* check arguments */
		if(args != null && args.length == 1) {

			/* Get the url */
			String url = "http://" + (String) args[0] + "/";

			/* Add behaviour */
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

			/* pattern to obtain only the domain */
			String pattern = "((?:[a-z][a-z\\.\\d\\-]+)\\.(?:[a-z][a-z\\-]+))(?![\\w\\.])";

			Pattern r = Pattern.compile(pattern);

			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			Elements links = doc.select("a[href]");
			ArrayList<String> attributes = new ArrayList<String>();

			/* Retrieve links */
			print("\n%s: Links --> (%d) from <%s>", getLocalName(), links.size(), url);
			for (Element link : links) {
				String attr = link.attr("abs:href");
				Matcher m = r.matcher(attr);
				if(m.find())
					attributes.add(m.group(0));
			}

			/* Store the web page and links */
			Storage storage = new Storage(attributes, url);

			/* Prepare message */
			AID aid = new AID();
			aid.setLocalName("processor");

			/* Send message if we have found links */
			if(links.size() > 0) {
				ACLMessage sendMessage = new ACLMessage(ACLMessage.INFORM);
				sendMessage.setSender(getAID());
				sendMessage.addReceiver(aid);
				try {
					sendMessage.setContentObject(storage);
					send(sendMessage);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/* Block behaviour until message is received */
				blockingReceive(template);
				end = true;
			}
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
	 * Print message in a predetermine format
	 * @param msg
	 * @param args
	 */
	private void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}

	/**
	 * Free resources from the agent
	 */
	protected void takeDown() {
		System.out.println(getLocalName() + " frees resources");
	}
}
