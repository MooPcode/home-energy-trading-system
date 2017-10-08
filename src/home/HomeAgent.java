package home;

import java.util.*;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import appliances.*;
import retailers.*;

public class HomeAgent extends Agent {
	RetailerAgent retailer;
	List<RetailerAgent> retailers;
	List<AID> appliances = new ArrayList<AID>();
	Map<AID, Integer> applianceUsage = new HashMap<AID, Integer>();
	
	public HomeAgent() {
		// changing this for a test commit
	}
	
	protected void setup() {
		// First set-up message receiving behaviour
		CyclicBehaviour messageListeningBehaviour = new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = receive();
				while(msg != null) {
					if(msg.getPerformative() == ACLMessage.INFORM)
					{
						System.out.println(getLocalName() + ": Received response " + msg.getContent() + " from " + msg.getSender().getLocalName());
						applianceUsage.put(msg.getSender(), Integer.valueOf(msg.getContent()));
					} else if(msg.getPerformative() == ACLMessage.SUBSCRIBE)
					{
						appliances.add(msg.getSender());
						applianceUsage.put(msg.getSender(), null);
						System.out.println(msg.getSender().getLocalName() + " subscribed to home");
					}
					
					msg = receive();
				}
				block();
			}
		};
		addBehaviour(messageListeningBehaviour);
		
		CyclicBehaviour getUsage = new CyclicBehaviour(this) {
			@Override
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setContent("GetUsage");
				
				for(AID appliance : appliances)
				{
					msg.addReceiver(appliance);
				}
				
				send(msg);
			}
		};
		addBehaviour(getUsage);
		
		/**
		// Send messages to two agents whose names are "a1" and "a2" (hard-coded)
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent("GetUsage");
		msg.addReceiver(new AID("appliance", AID.ISLOCALNAME));
		// Send Message (only once)
		System.out.println(getLocalName() + ": Sending message " + msg.getContent() + " to ");
		Iterator receivers = msg.getAllIntendedReceiver();
		while (receivers.hasNext()) {
			System.out.println(((AID) receivers.next()).getLocalName());
		}
		send(msg);**/
	}
	
	public void doThings()
	{
		// ask appliances for the things
		// appliances send back the things
		
		// ask retailers for the things
		// retails send back the things
	}
	
	public void tick()
	{
		// get current power usage
		int power = 100;
		
		float bestRetailerPrice;
		RetailerAgent bestRetailer;
		
		// get prices, compare prices and find cheapest
		bestRetailer = retailers.get(0);
		bestRetailerPrice = bestRetailer.GetCurrentPrice(power);
		
		for (RetailerAgent temp : retailers) {
			float price = temp.GetCurrentPrice(power);
			if(price < bestRetailerPrice)
			{
				bestRetailer = temp;
				bestRetailerPrice = price;
			}
		}
		
		// disconnect from the old retailer
		if(retailer != null && retailer != bestRetailer)
			retailer.Disconnect();
		
		// connect to the new retailer
		retailer = bestRetailer;
		retailer.Connect();
		
	}
}
