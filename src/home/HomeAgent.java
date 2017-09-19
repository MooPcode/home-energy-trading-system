package home;

import java.util.*;
import jade.core.Agent;
import appliances.*;
import retailers.*;

public class HomeAgent extends Agent {
	RetailerAgent retailer;
	List<RetailerAgent> retailers;
	List<ApplianceAgent> appliances;
	
	public HomeAgent() {
		// changing this for a test commit
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
