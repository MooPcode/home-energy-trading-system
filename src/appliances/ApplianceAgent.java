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
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.addReceiver(new AID("home", AID.ISLOCALNAME));
		msg.setContent("appliance");
		send(msg);
		
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = receive();
				if (msg != null) {
					// Get usage command
					if(msg.getContent().contains("GetUsage"))
					{
						// Reply to message
						ACLMessage reply = msg.createReply();
						reply.setPerformative(50);
						reply.setContent("USAGE:" + String.valueOf(getUsage()));

						// Send reply
						send(reply);	
					}
			}
			}
		});
	}

	public int getUsage()
	{
		return (int) Math.round(Math.random() * 100);
	}
}
