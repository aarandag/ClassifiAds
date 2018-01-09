/**
 * Agent that processes information from RetrieverAgent
 */
package domain;

/* imports of jade */
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Alberto Aranda García y Cristian Gómez Portes
 *
 */

public class ProcessorAgent extends Agent{
	private Object[] args;

	protected void setup() {
		/* Get arguments */
		args = getArguments();

		/* check arguments */
		if(args != null && args.length == 4) {
			/* Create ParallelBehaviour */
			ParallelBehaviour pb = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL) {
				/**
				 * Kill the Agent
				 */
				public int onEnd() {
					doDelete();
					return super.onEnd();
				}
			};

			/* Get retriever */
			for(Object retriever : args) {
				/* Add subBehaviour */
				pb.addSubBehaviour(new ProcessorBehaviour((String) retriever));

			}
			
			/* Add ParallelBehaviour */
			addBehaviour(pb);
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
				Storage storage = null;
				ArrayList<String> links = null;
				String webpage = null;
				try {
					storage = (Storage) message.getContentObject();
					print("Message received from %s", agent);

					/* get web page */
					webpage = storage.getWebpage();

					/* get links */
					links = storage.getLinks();

					/* object that contains all ad servers */
					ServerList serverlist = new ServerList();

					/* ArrayList that will contain the ad links */
					ArrayList<String> adlinks = new ArrayList<String>();

					/* Process links and compare them with the serverlist */
					for(String link : links) {
						if(serverlist.contains(link)) {
							adlinks.add(link);
						}
					}					
					print("Ads detected in %s: %s", webpage, adlinks.size());

					/* Store the web page and ad links */
					Storage storage_ads = new Storage(adlinks, webpage);

					/* Prepare message */
					AID aid = new AID();
					aid.setLocalName("printer");

					ACLMessage sendMessage = new ACLMessage(ACLMessage.INFORM);
					sendMessage.setSender(getAID());
					sendMessage.addReceiver(aid);

					sendMessage.setContentObject(storage_ads);
					send(sendMessage);

				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/* Prepare message */
				AID aid = new AID();
				aid.setLocalName(agent);

				/* Send response */
				ACLMessage response = message.createReply();
				response.setSender(getAID());
				response.addReceiver(aid);

				send(response);
				end = true;
			}else {
				/* Block the agent */
				block();
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
	}

	/**
	 * Free resources from the agent
	 */
	protected void takeDown() {
		System.out.println(getLocalName() + " frees resources");
	}
}
