package retailers;

public class LinearRetailerAgent extends RetailerAgent {
    @Override
    public float GetCurrentPrice(int power)
    {
        // total price = power
        int minPrice = 2;
        int gradient = 10;

        // total = gradient * power + min;
        int price = gradient * power + minPrice;

        // return the price
        return price;
    }
}
