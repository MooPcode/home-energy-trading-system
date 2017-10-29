package retailers;

public class RandomRetailerAgent extends RetailerAgent {
    private int minPrice = 5;
    private int maxPrice = 25;

    @Override
    public float GetCurrentPrice(int power)
    {
        int price = (int) (Math.random() * (maxPrice - minPrice)) + minPrice;

        // return the price
        return price * power;
    }
}
