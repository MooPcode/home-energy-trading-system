package home;

import java.util.*;
import jade.proto.AchieveREInitiator;
import jade.domain.FIPANames;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import appliances.*;
import retailers.*;

public class HomeAgent extends Agent {
	AID currentRetailer;
	List<AID> retailers = new ArrayList<AID>();
	List<AID> appliances = new ArrayList<AID>();
	Map<AID, Float> applianceUsage = new HashMap<AID, Float>();
	Map<AID, Float> retailerOffers = new HashMap<AID, Float>();
	private int nResponders;
	
	public HomeAgent() {
		// changing this for a test commit
	}
	
	protected void setup() {
		// First set-up message receiving behaviour
		CyclicBehaviour messageListeningBehaviour = new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = receive();

				if(msg != null && !(msg.getPerformative() == ACLMessage.SUBSCRIBE || msg.getPerformative() == 50))
				{
					putBack(msg);
					return;
				}

				while(msg != null) {
					if(msg.getPerformative() == 50)
					{
						//System.out.println(getLocalName() + ": Received response " + msg.getContent() + " from " + msg.getSender().getLocalName());
						applianceUsage.put(msg.getSender(), Float.valueOf(msg.getContent().replace("USAGE:", "")));
					} else if(msg.getPerformative() == ACLMessage.SUBSCRIBE)
					{
						if(msg.getContent().contains("appliance"))
						{
							appliances.add(msg.getSender());							
							applianceUsage.put(msg.getSender(), null);
						}
						else
						{
							retailers.add(msg.getSender());
						}
						System.out.println(msg.getSender().getLocalName() + " subscribed to home");
					}
					
					msg = null;
				}
				//block();
			}
		};
		addBehaviour(messageListeningBehaviour);
		
		CyclicBehaviour getUsage = new CyclicBehaviour(this) {
			@Override
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setContent("GetUsage");
				
				for(AID appliance : appliances)
					msg.addReceiver(appliance);
				
				send(msg);
			}
		};
		addBehaviour(getUsage);

		TickerBehaviour negotiate = new TickerBehaviour(this, 5000) {
			@Override
			public void onTick() {
				// Create a REQUEST message
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				for(AID retailer : retailers)
					msg.addReceiver(retailer);
				// Set the interaction protocol
				msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
				// Specify the reply deadline (10 seconds)
				msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
				// Set message content
				msg.setContent("QUOTE:" + getPowerDemand());
				addBehaviour(new RetailerNegotiate(myAgent, msg));
			}
		};
		addBehaviour(negotiate);
	}

	public int getPowerDemand()
	{
		// get current power usage
		int totalPower = 0;
		
		for(AID appliance : appliances)
		{
			if(applianceUsage.containsKey(appliance) && applianceUsage.get(appliance) != null)
				totalPower += applianceUsage.get(appliance);
		}

		return totalPower;
	}

	private class RetailerNegotiate extends AchieveREInitiator
	{
		public RetailerNegotiate(Agent a, ACLMessage msg)
		{
			super(a, msg);

		}

		protected void handleAgree(ACLMessage agree) {
			System.out.println(getLocalName() + ": " + agree.getSender().getName() + " has agreed to the request");
		}

		// Method to handle an inform message from responder
		protected void handleInform(ACLMessage inform) {
			//System.out.println(getLocalName() + ": " + inform.getSender().getName() + " successfully performed the requested action");
			System.out.println(getLocalName() + ": " + inform.getSender().getName() + "'s offer is " + inform.getContent());

			retailerOffers.put(inform.getSender(), Float.valueOf(inform.getContent()));
		}

		// Method to handle a refuse message from responder
		protected void handleRefuse(ACLMessage refuse) {
			//System.out.println(getLocalName() + ": " + refuse.getSender().getName() + " refused to perform the requested action");
			nResponders--;
		}

		// Method to handle a failure message (failure in delivering the message)
		protected void handleFailure(ACLMessage failure) {
			if (failure.getSender().equals(myAgent.getAMS())) {
				// FAILURE notification from the JADE runtime: the receiver (receiver does not exist)
				System.out.println(getLocalName() + ": " + "Responder does not exist");
			} else {
				System.out.println(getLocalName() + ": " + failure.getSender().getName() + " failed to perform the requested action");
			}
		}

		// Method that is invoked when notifications have been received from all responders
		protected void handleAllResultNotifications(Vector notifications) {
			if (notifications.size() < retailers.size()) {
				// Some responder didn't reply within the specified timeout
				System.out.println(
						getLocalName() + ": " + "Timeout expired: missing " + (nResponders - notifications.size()) + " responses");
			} else {
				System.out.println(getLocalName() + ": " + "Received notifications about every responder");
				System.out.println(getLocalName() + ": " + "Finding best offer...");

				AID bestRetailer = retailers.get(0);
				for(AID retailer : retailers)
				{
					if(!retailerOffers.containsKey(retailer))
						continue;

					System.out.println(retailer.getLocalName() + " offered $" + retailerOffers.get(retailer));

					if(retailerOffers.get(bestRetailer) > retailerOffers.get(retailer))
						bestRetailer = retailer;
				}

				System.out.println(bestRetailer.getLocalName() + " had the best offer!");
			}
		}
	}
}
