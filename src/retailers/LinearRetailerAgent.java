package retailers;

public class LinearRetailerAgent extends RetailerAgent {
    @Override
    public float GetCurrentPrice(int power)
    {
        // total price = power
        int minPrice = 0;
        int gradient = 20;
        // total = gradient * power + min;
        int price = gradient * power + minPrice;

        // return the price
        return price;
    }
}
