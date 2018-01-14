/**
 * Agent that prints the information that has received from ProcessoAgent
 */
package presentation;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import domain.Storage;

/* imports of jade */
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * @author Alberto Aranda García
 * @author Cristian Gómez Portes
 *
 */
public class PrinterAgent extends Agent{
	private Object[] args;
	private Hashtable<String, Integer> table;
	
	protected void setup() {
		table = new Hashtable<String, Integer>();
		
		/* Get arguments */
		args = getArguments();

		/* check arguments */
		if(args != null && args.length == 1) {
			
			/* Get the agent */
			String agent = (String) args[0];

			/* Add behaviour */
			addBehaviour(new PrinterBehaviour(agent));
		}
		else
			System.out.println("Error. You must type the name of agent as an argument");
	}
	
	private class PrinterBehaviour extends Behaviour {
		private String agent;
		private boolean end;
		private int messageReceived;
		private MessageTemplate template = null;
		
		/**
		 * Constructor of the ProcessalBehaviour class
		 * @param agent
		 */
		public PrinterBehaviour(String agent) {
			this.agent = agent;
			
			AID aid = new AID();
			aid.setLocalName(agent);

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
			messageReceived = 0;
		}
		
		/**
		 * Run the process of the behaviour
		 */
		public void action() {
			if(messageReceived <= 3) {
				ACLMessage message = receive(template);
				if(message != null) {
					messageReceived++;
					Storage storage = null;
					ArrayList<String> ads = null;
					String webpage = null;
					try {
						storage = (Storage) message.getContentObject();
						print("%s: received message from %s", getLocalName(), agent);

						/* get web page */
						webpage = storage.getWebpage();

						/* get ads */
						ads = storage.getLinks();
						
						/* Add information in table */
						table.put(webpage, ads.size());
						
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					/* Block the agent */
					block();
				}
			}else {
				/* Print solution */
				print("******************** %s *********************", "Solution");
				String leftAlignFormat = "| %-40s | %-4d |%n";
				System.out.format("+------------------------------------------+------+%n");
				System.out.format("| WebPage                                  | Ads  |%n");
				System.out.format("+------------------------------------------+------+%n");
				Enumeration<String> keys = table.keys();
				do {
					String key = keys.nextElement();
					int value = table.get(key);
				    System.out.format(leftAlignFormat, key, value);
				}while(keys.hasMoreElements());
				System.out.format("+------------------------------------------+------+%n");
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
