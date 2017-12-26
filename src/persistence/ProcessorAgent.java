/**
 * Agent that processes information from RetrieverAgent
 */
package persistence;

import java.util.ArrayList;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jade.core.AID;

/**
 * @author Alberto Aranda García y Cristian Gómez Portes
 *
 */

/* imports of jade */
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.lang.acl.ACLMessage;

public class ProcessorAgent extends Agent{
	Object[] args;
	
	protected void setup() {
		/* Get arguments */
		args = getArguments();
		
		/* check arguments */
		if(args != null && args.length == 1) {
			
			/* Get retriever */
			String retriever = (String) args[0];
			
			/* Run behaviour */
			addBehaviour(new ProcessorBehaviour(retriever));
		}
		else
			System.out.println("Error. You must type the name of agent as an argument");
	}
	private class ProcessorBehaviour extends Behaviour{
		private String agent;
		private boolean end;
		private MessageTemplate template = null;
		
		/**
		 * Constructor of the ProcessalBehaviour class
		 * @param agent
		 */
		public ProcessorBehaviour(String agent) {
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
		}
		
		/**
		 * Run the process of the behaviour
		 */
		public void action() {
			ACLMessage message = receive(template);
			if(message != null) {
				ArrayList<String> links = null;
				try {
					links = (ArrayList<String>) message.getContentObject();
					
					 /* Process links */
					 
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/* Prepare message */
				AID aid = new AID();
				aid.setLocalName(agent);
				
				/* Send response */
				ACLMessage response = message.createReply();
				response.setSender(getAID());
				response.setContent("");
				response.addReceiver(aid);
				
				send(response);
				end = true;
			}else {
				System.out.println(getBehaviourName() + " is waiting for RetrieverAgent");
				block();
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
	 * Free resources from the agent
	 */
	protected void takeDown() {
		System.out.println(getLocalName() + " frees resources");
	}
}
