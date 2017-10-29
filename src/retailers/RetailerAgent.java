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

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

abstract class RetailerAgent extends Agent{
	int minPrice = 8; // minimum cost per unit
	int lastRequest = 0; // the electricity required in the last request
	int lastRequestRound = 0; // the round of the last request
	
	public RetailerAgent() {
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
				JSONParser parser = new JSONParser();
				String s = request.getContent();

				try {
					Object obj = parser.parse(s);
					JSONObject jsonObj = (JSONObject)obj;
					lastRequest = ((Long)jsonObj.get("usage")).intValue();
					lastRequestRound = ((Long)jsonObj.get("round")).intValue();
				}	catch(ParseException pe){
					System.err.println("Invalid JSON encountered");
					System.err.println(s);
				}

				ACLMessage agree = request.createReply();
				agree.setPerformative(ACLMessage.AGREE);
				return agree;
			}

			// If the agent agreed to the request received, then it has to perform the associated action and return the result of the action
			protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
				ACLMessage inform = request.createReply();
				inform.setPerformative(ACLMessage.INFORM);

				float price = GetCurrentPrice(lastRequest);

				// if we are past round 1, try and offer discounts to win over the home
				double discount = (lastRequestRound - 1) * (price * 0.05);
				double finalPrice = price - discount;

				// if it goes below the min price per unit, dont offer the discount
				if(finalPrice / lastRequest < minPrice)
					finalPrice = price;

				inform.setContent(String.valueOf(finalPrice));

				return inform;
			}
		});
	}

	// the price the home buys at (per unit)
	abstract public float GetCurrentPrice(int power);
}
