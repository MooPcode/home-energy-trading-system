package retailers;

public class HighDemandRetailerAgent extends RetailerAgent {
    @Override
    public float GetCurrentPrice(int power)
    {
        float price = 0;
        System.out.println("power is " + power);
        for (int i = 1; i < power; i++) {
            price += 100 * Math.pow(i, -0.7);
        }
        // return the price
        return price;
    }
}
