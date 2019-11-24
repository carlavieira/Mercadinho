package market;

public class Shelf {
    private int id;
    private boolean taken;
    private Product productType;
    private int numProducts;
    private static final Double MAX_WEIGHT = 10.0;

    public Shelf(int id) {
        this.id = id;
        this.taken = false;
        this.productType = null;
        this.numProducts = 0;
    }

    public int getId() {
        return id;
    }

    public boolean isTaken() {
        return taken;
    }

    public Product getTypeProduct() {
        return productType;
    }

    public int getNumProducts() {
        return numProducts;
    }

    public void fillShelf(Product product) {
        if (!this.isTaken() || this.productType == product) {
            this.productType = product;
            this.taken = true;
            this.numProducts = (int) (MAX_WEIGHT / product.getWeight());
        }
    }

    public void clearSheilf() {
        this.productType = null;
        this.taken = false;
        this.numProducts = 0;
    }

    public void takeProduct() {
        if (this.numProducts > 0) this.numProducts--;
    }
}
