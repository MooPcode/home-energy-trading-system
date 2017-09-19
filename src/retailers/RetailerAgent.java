package retailers;

import java.util.*;
import jade.core.Agent;

abstract public class RetailerAgent extends Agent{
	int contractLength = 0; // how long the home has been with this retailer
	float initialPrice = 50; // the starting price
	float minPrice = 1; // minimum cost per unit
	float maxPrice = 500; // maximum cost per unit
	boolean inUse = false; // whether they are currently being used by home
	ArrayList<Float> priceHistory = new ArrayList<Float>(); // history of prices  
	
	public RetailerAgent() 
	{
		priceHistory.add(initialPrice);
	}
	
	public void Connect()
	{
		inUse = true;
	}
	
	public void Disconnect()
	{
		inUse = false;
		contractLength = 0;
	}
	
	// the price the home buys at (per unit)
	public float GetCurrentPrice(int power)
	{
		// starts off with last price
		float price = priceHistory.get(priceHistory.size()  - 1);
		
		// function to work out new price
		price = power * 5;
		// do other things
		
		price = price / power; // per unit price
		
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
