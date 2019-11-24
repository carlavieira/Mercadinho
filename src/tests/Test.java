package tests;

import market.*;

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
        List<Lot> lots = market.getLots();
        for (Lot lot: lots) {
            System.out.println(lot);
        }

        List<Integer> requests = market.getRequests();
        int i = 1;
        for (Integer element: requests) {
            System.out.println(i+": "+element);
            i++;
        }


    }
}
