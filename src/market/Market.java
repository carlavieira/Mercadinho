package market;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
        List<Product> productList = new ArrayList<>();

        try {
            BufferedReader products = new BufferedReader(new FileReader("Produtos.txt"));
            BufferedReader preferences = new BufferedReader(new FileReader("PreferenciaDePrateleiras.txt"));

            String lineProduct = products.readLine().substring(1);
            String linePreferences = preferences.readLine().substring(1);

        while (lineProduct != null && linePreferences!= null) {
            String[] productData = lineProduct.split(";");
            String[] preferencesData = linePreferences.split(";");
            int id = Integer.parseInt(productData[0]);
            double unitaryValue = Double.parseDouble(productData[1].toString().replaceAll(",", "."));
            double weight = Double.parseDouble(productData[2].toString().replaceAll(",", "."));
            int[] shelfPreference = new int[preferencesData.length];
            for (int i = 0; i<preferencesData.length; i++) {
                shelfPreference[i] = Integer.parseInt(preferencesData[i]);
            }
            productList.add(new Product(id, unitaryValue, weight, shelfPreference));
            lineProduct = products.readLine();
            linePreferences = preferences.readLine();
        }
        products.close();
        preferences.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return productList;
    }

    private List<Lot> readlots() {
        int[][] distanceMatrix = new int[products.size()+1][products.size()+1];
        try {
            BufferedReader distances = new BufferedReader(new FileReader("Distancias.txt"));
            String line = distances.readLine();
            int i = 0;
            while (line != null) {
                String[] lotDistances = line.split(" ");
                int j=0;
                for (String distance: lotDistances) {
                    distanceMatrix[i][j] = Integer.parseInt(distance);
                    j++;
                }
                i++;
                line = distances.readLine();
            }
            distances.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        List<Lot> lotsList = new ArrayList<>();
        //Utilização do Algoritmo de Dijkstra para a solução do problema do caminho mínimo
        ShortestPath t = new ShortestPath(products.size());
        for(int i=1; i<products.size(); i++){
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


    public List<Product> getProducts() {
        return products;
    }

    public List<Shelf> getShelves() {
        return shelves;
    }

    public void setShelves(List<Shelf> shelves) {
        this.shelves = shelves;
    }

    public List<Lot> getLots() {
        return lots;
    }

    public List<Integer> getRequests() {
        return requests;
    }

}