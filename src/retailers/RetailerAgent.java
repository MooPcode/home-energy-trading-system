package retailers;

import java.util.*;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;


public class RetailerAgent extends Agent{
	int contractLength = 0; // how long the home has been with this retailer
	float initialPrice = 50; // the starting price
	float minPrice = 1; // minimum cost per unit
	float maxPrice = 500; // maximum cost per unit
	int lastRequest = 0;
	boolean inUse = false; // whether they are currently being used by home
	ArrayList<Float> priceHistory = new ArrayList<Float>(); // history of prices  
	
	public RetailerAgent() 
	{
		priceHistory.add(initialPrice);
	}
	
	protected void setup()
	{
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.addReceiver(new AID("home", AID.ISLOCALNAME));
		msg.setContent("retailer");
		send(msg);
		
		System.out.println(getLocalName() + ": waiting for requests...");
		// Message template to listen only for messages matching the correct interaction protocol and performative
		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

		// Add the AchieveREResponder behaviour which implements the responder role in a FIPA_REQUEST interaction protocol
		// The responder can either choose to agree to request or refuse request
		addBehaviour(new AchieveREResponder(this, template) {
			protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
				lastRequest = Integer.parseInt(request.getContent().replace("QUOTE:", ""));

				ACLMessage agree = request.createReply();
				agree.setPerformative(ACLMessage.AGREE);
				return agree;
			}

			// If the agent agreed to the request received, then it has to perform the associated action and return the result of the action
			protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
					throws FailureException {
				ACLMessage inform = request.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				inform.setContent(String.valueOf(GetCurrentPrice(lastRequest)));
				return inform;
			}
		});
	}

	// the price the home buys at (per unit)
	public float GetCurrentPrice(int power)
	{
		int price = (int) Math.round(Math.random() * 10) * power;
		
		// return the price
		return price;
	}
	
	// the price the home sells at (per unit)
	public float GetSellingPrice()
	{
		return 5;
	}
	
	public void tick()
	{
		if(inUse)
			contractLength++;
	}
}
