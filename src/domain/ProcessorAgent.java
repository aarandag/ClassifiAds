/**
 * Agent that processes information from RetrieverAgent
 */
package domain;

/* imports of jade */
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.domain.FIPANames;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Alberto Aranda García
 * @author Cristian Gómez Portes
 *
 */

public class ProcessorAgent extends Agent{

	protected void setup() {
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
		/* List that stores the name of retrievers */
		ArrayList<String> retrievers = new ArrayList<String>();
		print("%s: obtaining retrievers from AMS catalogue", getLocalName());
		for(int i = 0; i < agents.length; i++) {
			String agent = agents[i].getName().getLocalName();
			if(agent.startsWith("retriever")) {
				retrievers.add(agent);
			}
		}
		
		/* Check agents */
		if(retrievers != null && retrievers.size() == 4) {
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
			for(String retriever : retrievers) {
				/* Add subBehaviour */
				pb.addSubBehaviour(new ProcessorBehaviour((String) retriever));

			}
			
			/* Add ParallelBehaviour */
			addBehaviour(pb);
		}
		else
			System.out.println("Error. You have must create four retrieverAgents");
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
					print("%s: received message from %s", getLocalName(), agent);

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
					/* Store the web page and ad links */
					Storage storage_ads = new Storage(adlinks, webpage);

					/* Prepare message */
					AID aid = new AID();
					aid.setLocalName("printer");

					ACLMessage sendMessage = new ACLMessage(ACLMessage.REQUEST);
					sendMessage.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
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
		 * Behaviour finalizes if end is equal to true
		 */
		public boolean done() {
			return end;
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
