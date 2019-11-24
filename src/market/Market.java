package market;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Market {
    private int numShelves = 10;
    private List<Product> products = new ArrayList<Product>();
    private List<Shelf> shelves = new ArrayList<Shelf>();
    private List<Lot> lots = new ArrayList<Lot>();
    private List<Integer> requests = new ArrayList<Integer>();


    public Market() {
        this.products = readProducts();
        for (int i = 1; i <= this.numShelves; i++) {
            this.shelves.add(new Shelf(i));
        }
        this.lots = readlots();
        this.requests = readRequests();
    }

    private List<Product> readProducts() {
        Scanner products = null;
        try {
            products = new Scanner(new FileReader("Produtos.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Scanner preferences = null;
        try {
            preferences = new Scanner(new FileReader("PreferenciaDePrateleiras.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert products != null;
        products.nextLine();
        assert preferences != null;
        preferences.nextLine();

        List<Product> productList = new ArrayList<>();

        while (products.hasNextLine()) {
            String lineProduct = products.nextLine();
            String[] productData = lineProduct.split(";");
            String linePreferences = preferences.nextLine();
            String[] preferencesData = linePreferences.split(";");
            int id = Integer.parseInt(productData[0]);
            double unitaryValue = Double.parseDouble(productData[1]);
            double weight = Double.parseDouble(productData[2]);
            int[] shelfPreference = new int[preferencesData.length];
            for (int i = 0; i<preferencesData.length; i++) {
                shelfPreference[i] = Integer.parseInt(preferencesData[i]);
            }
            productList.add(new Product(id, unitaryValue, weight, shelfPreference));
        }
        products.close();
        preferences.close();

        return productList;
    }

    private List<Lot> readlots() {
        Scanner distances = null;
        try {
            distances = new Scanner(new FileReader("Distancias.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert distances != null;
        distances.nextLine();
        int[][] distanceMatrix = new int[products.size()][products.size()];
        int i = 0;
        while (distances.hasNextLine()) {
            String line = distances.nextLine();
            String[] lotDistances = line.split(" ");
            int j=0;
            for (String distance: lotDistances) {
                distanceMatrix[i][j] = Integer.parseInt(distance);
                j++;
            }
            i++;
        }
        distances.close();

        List<Lot> lotsList = new ArrayList<>();
        //Utilização do Algoritmo de Dijkstra para a solução do problema do caminho mínimo
        ShortestPath t = new ShortestPath(products.size());
        for(i=1; i<=products.size(); i++){
            lotsList.add(new Lot(i, t.dijkstra(distanceMatrix, 0)[i]));
        }

        return lotsList;
    }

    private List<Integer> readRequests() {
        Scanner requests = null;
        try {
            requests = new Scanner(new FileReader("Pedidos.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert requests != null;
        requests.nextLine();
        List<Integer> requestsList = new ArrayList<>();

        while (requests.hasNextLine()) {
            int prodRequest = Integer.parseInt(requests.nextLine());
            requestsList.add(prodRequest);
        }
        requests.close();
        return requestsList;
    }

}