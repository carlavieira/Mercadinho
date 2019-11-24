package market;

public class Shelf {
    private int id;
    private boolean taken;
    private Product typeProduct;
    private int numProducts;
    private double totalWeight;
    private static final Double MAX_WEIGHT = 10.0;

    public Shelf(int id) {
        this.id = id;
        this.taken = false;
        this.typeProduct = null;
        this.numProducts = 0;
        this.totalWeight = 0;
    }

    public int getId() {
        return id;
    }

    public boolean isTaken() {
        return taken;
    }

    public Product getTypeProduct() {
        return typeProduct;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public int getNumProducts() {
        return numProducts;
    }

    public void setProduct(Product product) {
        this.typeProduct = product;
        this.taken = true;
    }

    public void clearSheilf(){
        this.typeProduct = null;
        this.taken = true;
    }
}
