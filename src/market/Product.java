package market;

public class Product {
    private int id;
    private double unitaryValue;
    private double weight;
    private int[] shelfPreference;


    public Product(int id, double unitaryValue, double weight, int[] shelfPreference) {
        this.id = id;
        this.unitaryValue = unitaryValue;
        this.weight = weight;
        this.shelfPreference = shelfPreference;
    }

    public int getId() {
        return id;
    }

    public double getUnitaryValue() {
        return unitaryValue;
    }

    public double getWeight() {
        return weight;
    }

    public int[] getShelfPreference(){
        return shelfPreference;
    }
}
