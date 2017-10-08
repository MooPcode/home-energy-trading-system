package appliances;

import jade.core.Agent;
import java.util.Iterator;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ApplianceAgent extends Agent {

	public ApplianceAgent() {
		// TODO Auto-generated constructor stub
	}
	
	protected void setup() {
		// Send messages to two agents whose names are "a1" and "a2" (hard-coded)
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.addReceiver(new AID("home", AID.ISLOCALNAME));
		send(msg);
		
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				//System.out.println(getLocalName() + ": Waiting for message");
				ACLMessage msg = receive();
				if (msg != null) {
					// Handle message
					System.out.println(getLocalName()+ ": Received message " + msg.getContent() + " from " + msg.getSender().getLocalName());
					
					// Get usage command
					if(msg.getContent().contains("GetUsage"))
					{
						// Reply to message
						ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.INFORM);
						reply.setContent(String.valueOf(getUsage()));
						// Send reply
						System.out.println(getLocalName() + ": Sending response " +
						reply.getContent() + " to " + msg.getAllReceiver().next());
						send(reply);	
					}
			}
			}
		});
	}

	public int getUsage()
	{
		return (int) Math.round(Math.random() * 10);
	}
}
