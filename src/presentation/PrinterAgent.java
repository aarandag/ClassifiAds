/**
 * Agent that prints the information that has received from ProcessoAgent
 */
package presentation;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/* imports of jade */
import domain.Storage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.proto.AchieveREResponder;

/**
 * @author Alberto Aranda García
 * @author Cristian Gómez Portes
 *
 */
public class PrinterAgent extends Agent{
	private Hashtable<String, Integer> table;
	
	protected void setup() {
		table = new Hashtable<String, Integer>();
		/* List that stores the agents that are in the AMS catalogue */
		AMSAgentDescription [] agents = null;
		
		/* create search constraint to get all agents */
		SearchConstraints restrictions = new SearchConstraints();
		restrictions.setMaxResults(new Long(-1));
		try {
			/* Communicate with the AMS Service to store all agents */
			agents = AMSService.search(this, new AMSAgentDescription(), restrictions);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String processor = null;
		print("%s: obtaining processor from AMS catalogue", getLocalName());
		for(int i = 0; i < agents.length; i++) {
			String agent = agents[i].getName().getLocalName();
			if(agent.startsWith("processor")) {
				processor = agent;
			}
		}
		
		/* Check agents */
		if(processor != null) {
			/* Add Behaviour */
		    addBehaviour(new PrinterBehaviour(this, processor));
		}
		else
			System.out.println("Error. You have must create a processorAgent");
	}
	
	private class PrinterBehaviour extends AchieveREResponder {
		private String agent;
		private boolean end;
		private int messageReceived;
		private MessageTemplate template = null;
		
		/**
		 * Constructor of the PrinterBehaviour class
		 * @param agent
		 */
	    public PrinterBehaviour(Agent a, String agent) {
		super(a, MessageTemplate.and(MessageTemplate.MatchSender(new AID(agent, false)),
						  MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
			this.agent = agent;
			this.messageReceived = 0;
		}
		
	    
		/**
		 * Run the process of the behaviour
		 */
	    protected ACLMessage handleRequest(ACLMessage message) throws NotUnderstoodException, RefuseException {
			if(messageReceived <= 2) {				
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
				e.printStackTrace();
				throw new NotUnderstoodException("Unreadable message's content");
			    }
			} else {
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
			ACLMessage agree = message.createReply();
			agree.setPerformative(ACLMessage.AGREE);
			return agree;
	    }

	    protected ACLMessage prepareResultNotification(ACLMessage request,ACLMessage response) throws FailureException {
		ACLMessage inform = request.createReply();
		inform.setPerformative(ACLMessage.INFORM);
		return inform;
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
