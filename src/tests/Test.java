package tests;

import market.Market;
import market.Product;
import market.Shelf;

import java.util.List;

public class Test {

    public static void main(String[] args) {
        Market market = new Market();
        List<Product> products = market.getProducts();
        for (Product product: products) {
            System.out.println(product);
        }
        List<Shelf> shelves = market.getShelves();
        for (Shelf shelf: shelves) {
            System.out.println(shelf);
        }
        shelves.get(4).fillShelf(products.get(1));
        for (Shelf shelf: shelves) {
            System.out.println(shelf);
        }


    }
}
