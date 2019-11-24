package market;

import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Market {
    private int numShelves = 10;
    private List<Product> products = new ArrayList<Product>();
    private List<Shelf> shelves = new ArrayList<Shelf>();
    private List<Lot> lots = new ArrayList<Lot>();
    private List<Integer> requests = new ArrayList<Integer>();
    private int distanceTotal;
    private int failures;
    private int[][] distanceMatrix;

    public Market() {
        this.products = readProducts();
        for (int i = 1; i <= this.numShelves; i++) {
            this.shelves.add(new Shelf(i));
        }
        this.lots = readlots();
        this.requests = readRequests();
        this.distanceTotal = 0;
        this.failures =0;
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
        this.distanceMatrix = distanceMatrix;

        List<Lot> lotsList = new ArrayList<>();
        //Utilização do Algoritmo de Dijkstra para a solução do problema do caminho mínimo
        ShortestPath t = new ShortestPath(products.size());
        for(int i=1; i<products.size(); i++){
            lotsList.add(new Lot(i, t.dijkstra(distanceMatrix, 0)[i]));
        }

        return lotsList;
    }

    private List<Integer> readRequests() {
        List<Integer> requestsList = new ArrayList<>();

        try {
            BufferedReader requests = new BufferedReader(new FileReader("Pedidos.txt"));
            String line = requests.readLine();
            while (line != null) {
                requestsList.add(Integer.parseInt(line));
                line = requests.readLine();
            }
            requests.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void changeByShelfPreference() {
        //Para todos os pedidos faca:
        for (Integer requestProdId : this.requests) {
            //Se existe alguma prateleira com aquele ID de produto:
            if (productAvailable(requestProdId)){
                //Pega a prateleira com o Id do produto pedido:
                Shelf shelf = shelfWithProductAvailable(requestProdId);
                //Tira um produto
                shelf.takeProduct();
                //Se era o último produto:
                if (shelf.getNumProducts()==0){
                    //Vai para o lote do produto
                    goToLot(shelf.getTypeProduct().getId());
                    //Volta lote do produto
                    goToLot(shelf.getTypeProduct().getId());
                    //Enche a prateleira
                    shelf.fillShelf(shelf.getTypeProduct());
                }
            } //Page not found / Não tem produto pedido na prateleira
            else {
                //Aumenta o numero de falhas
                failures++;
                //ATENCAO AQUI QUE ESCOLHE COMO SUBSTITUI. Escolhe o id da prateleira que vai substituir (no caso a com a prioridade maior do produto requisitado)
                int idShelf = idPriorityShelf(products.get(requestProdId-1).getShelfPreference());
                //Esvazia prateleira
                shelves.get(idShelf-1).clearSheilf();
                //Vai para o lote do produto velho devolver os produtos
                goToLot(idShelf);
                //Vai do lote do produto velho para o do produto novo recolher os produtos
                goBetweenLots(idShelf, requestProdId);
                //Volta para o mercadinho
                goToLot(requestProdId);
                //Enche a prateleira com o produto novo
                shelves.get(idShelf-1).fillShelf(products.get(requestProdId-1));
            }
            }
        }

    private void goToLot(int id) {
        for (Lot lot: this.lots) {
            if (lot.getProductId()==id){
                distanceTotal= distanceTotal + lot.getDistanceFromMarket();
            }
        }
    }

    private void goBetweenLots(int idOld, int idNew) {
        ShortestPath t = new ShortestPath(products.size());
        distanceTotal = distanceTotal + t.dijkstra(this.distanceMatrix, idOld-1)[idNew-1];
    }

    private int idPriorityShelf(int[] preferences) {
        int indexMax = 1;
        for (int i =1; i<preferences.length; i++){
            if (preferences[i] > preferences[indexMax])
                indexMax = i;
        }
        return indexMax;
    }

    private Shelf shelfWithProductAvailable(Integer requestProdId) {
        for (Shelf shelf: shelves) {
            if (shelf.isTaken())
            if (shelf.getTypeProduct().getId() == requestProdId) return shelf;
        }
        return null;
    }

    private boolean productAvailable(Integer requestProdId) {
        for (Shelf shelf: shelves) {
            if (shelf.isTaken()) if (shelf.getTypeProduct().getId() == requestProdId){
                return true;
            }
        }
         return false;
        }

    public void printResult() {
        System.out.println("Número de vezes que produto não foi encontrado na prateleira: "+this.failures);
        System.out.println("Distância total percorrida: "+this.distanceTotal);

    }
}