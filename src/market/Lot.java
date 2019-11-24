package market;

public class Lot {
    private int productId;
    private int distanceFromMarket;

    public Lot(int productId, int distanceFromMarket) {
        this.productId = productId;
        this.distanceFromMarket = distanceFromMarket;
    }

    public int getProductId() {
        return productId;
    }

    public int getDistanceFromMarket() {
        return distanceFromMarket;
    }
}
