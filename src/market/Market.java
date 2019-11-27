package market;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.IntStream;

public class Market {
    private List<Product> products = new ArrayList<Product>();
    private List<Shelf> shelves = new ArrayList<Shelf>();
    private List<Lot> lots = new ArrayList<Lot>();
    private List<Integer> requests = new ArrayList<Integer>();
    private int numShelves = 10;
    private int[][] distanceMatrix;
    private int distanceTotal;
    private int failures;
    private int[] sales;
    private int counter;


    public Market() {
        this.products = readProducts();
        for (int i = 1; i <= this.numShelves; i++) {
            this.shelves.add(new Shelf(i));
        }
        this.lots = readlots();
        this.requests = readRequests();
        IntStream.range(0, shelves.size()).forEach(i -> shelves.get(i).fillShelf(products.get(i)));
        this.distanceTotal = 0;
        this.failures = 0;
        this.sales = new int[products.size()+1];
    }

    private List<Product> readProducts() {
        List<Product> productList = new ArrayList<>();

        try {
            BufferedReader products = new BufferedReader(new FileReader("Produtos.txt"));
            BufferedReader preferences = new BufferedReader(new FileReader("PreferenciaDePrateleiras.txt"));

            String lineProduct = products.readLine().substring(1);
            String linePreferences = preferences.readLine().substring(1);

            while (lineProduct != null && linePreferences != null) {
                String[] productData = lineProduct.split(";");
                String[] preferencesData = linePreferences.split(";");
                int id = Integer.parseInt(productData[0]);
                double unitaryValue = Double.parseDouble(productData[1].toString().replaceAll(",", "."));
                double weight = Double.parseDouble(productData[2].toString().replaceAll(",", "."));
                int[] shelfPreference = new int[preferencesData.length];
                for (int i = 0; i < preferencesData.length; i++) {
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
        int[][] distanceMatrix = new int[products.size() + 1][products.size() + 1];
        try {
            BufferedReader distances = new BufferedReader(new FileReader("Distancias.txt"));
            String line = distances.readLine();
            int i = 0;
            while (line != null) {
                String[] lotDistances = line.split(" ");
                int j = 0;
                for (String distance : lotDistances) {
                    distanceMatrix[i][j] = Integer.parseInt(distance);
                    j++;
                }
                i++;
                line = distances.readLine();
            }
            distances.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.distanceMatrix = distanceMatrix;

        List<Lot> lotsList = new ArrayList<>();
        //Utilização do Algoritmo de Dijkstra para a solução do problema do caminho mínimo
        ShortestPath t = new ShortestPath(products.size()+1);
        int[] vector = t.dijkstra(distanceMatrix, 0);
        for (int i = 0; i < distanceMatrix.length; i++) {
            lotsList.add(new Lot(i,vector[i]));
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
            if (productAvailable(requestProdId)) {
                //Pega a prateleira com o Id do produto pedido:
                Shelf shelf = shelfWithProductAvailable(requestProdId);
                //Tira um produto
                takeProduct(shelf);
            } //Page not found / Não tem produto pedido na prateleira
            else {
                //Aumenta o numero de falhas
                failures++;
                //ATENCAO AQUI QUE ESCOLHE COMO SUBSTITUI. Escolhe o id da prateleira que vai substituir (no caso a com a prioridade maior do produto requisitado)
                int idShelf = idPriorityShelf(products.get(requestProdId - 1).getShelfPreference());
                //Esvazia prateleira
                shelves.get(idShelf - 1).clearSheilf();
                //Vai para o lote do produto velho devolver os produtos
                goToLot(idShelf);
                //Vai do lote do produto velho para o do produto novo recolher os produtos
                goBetweenLots(idShelf, requestProdId);
                //Volta para o mercadinho
                goToLot(requestProdId);
                //Enche a prateleira com o produto novo
                Shelf shelf = shelves.get(idShelf - 1);
                shelf.fillShelf(products.get(requestProdId - 1));
                takeProduct(shelf);
            }
        }
        printResult();
    }

    private void refill(Shelf shelf) {
        //Vai para o lote do produto
        goToLot(shelf.getTypeProduct().getId());
        //Volta lote do produto
        goToLot(shelf.getTypeProduct().getId());
        //Enche a prateleira
        shelf.fillShelf(shelf.getTypeProduct());
    }

    private void takeProduct(Shelf shelf) {
        shelf.takeProduct();
        sales[shelf.getTypeProduct().getId()]++;
        shelf.setR(true);
        //Se era o último item do produto, reabastece.
        if (shelf.getNumProducts() == 0) {
            refill(shelf);
        }
    }


    private void goToLot(int id) {
        int distance = 0;
        for (Lot lot : this.lots) {
            if (lot.getProductId() == id) {
                distanceTotal = distanceTotal + lot.getDistanceFromMarket();
            }
        }
    }

    private int distance(int lotIdSource, int lotIdDestiny) {
        ShortestPath t = new ShortestPath(products.size()+1);
        return t.dijkstra(this.distanceMatrix, lotIdSource )[lotIdDestiny];
    }

    private void goBetweenLots(int idOld, int idNew) {
        ShortestPath t = new ShortestPath(products.size()+1);
        distanceTotal = distanceTotal + t.dijkstra(this.distanceMatrix, idOld)[idNew];
    }


    private int idPriorityShelf(int[] preferences) {
        int indexMax = 1;
        for (int i = 1; i < preferences.length; i++) {
            if (preferences[i] > preferences[indexMax])
                indexMax = i;
        }
        return indexMax;
    }

    private Shelf shelfWithProductAvailable(Integer requestProdId) {
        for (Shelf shelf : shelves) {
            if (shelf.isTaken())
                if (shelf.getTypeProduct().getId() == requestProdId) return shelf;
        }
        return null;
    }

    private boolean productAvailable(Integer requestProdId) {
        for (Shelf shelf : shelves) {
            if (shelf.isTaken()) if (shelf.getTypeProduct().getId() == requestProdId) {
                return true;
            }
        }
        return false;
    }

    public void printResult() {
        int porc = (int) (((double) this.failures / (double) this.requests.size()) * 100);
        System.out.println("Número de vezes que produto não foi encontrado em alguma prateleira: " + this.failures + " (" + porc + "%)");
        System.out.println("Distância total percorrida: " + this.distanceTotal);

    }

    public void printSituation() {
        for (Shelf shelf : this.shelves) {
            System.out.print(shelf.getId() + " Prod:" + shelf.getTypeProduct() + " Qnt:" + shelf.getNumProducts() + " * ");
        }
        System.out.println();
    }

    public void changeByFIFO() {
        Queue<Shelf> shelvesQueue = new LinkedList<>();
        shelvesQueue.addAll(shelves);
        //Para todos os pedidos faca:
        for (Integer requestProdId : this.requests) {
            //Se existe alguma prateleira com aquele ID de produto:
            if (productAvailable(requestProdId)) {
                //Pega a prateleira com o Id do produto pedido:
                Shelf shelf = shelfWithProductAvailable(requestProdId);
                //Tira um produto
                takeProduct(shelf);
            } //Page not found / Não tem produto pedido na prateleira
            else {
                //Aumenta o numero de falhas
                failures++;
                //ATENCAO AQUI QUE ESCOLHE COMO SUBSTITUI. Escolhe o id da prateleira que vai substituir (no caso a com a prioridade maior do produto requisitado)
                int idShelf = shelvesQueue.remove().getId();
                //Esvazia prateleira
                shelves.get(idShelf - 1).clearSheilf();
                //Vai para o lote do produto velho devolver os produtos
                goToLot(idShelf);
                //Vai do lote do produto velho para o do produto novo recolher os produtos
                goBetweenLots(idShelf, requestProdId);
                //Volta para o mercadinho
                goToLot(requestProdId);
                //Enche a prateleira com o produto novo
                Shelf shelf = shelves.get(idShelf - 1);
                shelf.fillShelf(products.get(requestProdId - 1));
                takeProduct(shelf);
                shelvesQueue.add(shelf);
            }
        }
        printResult();
    }

    public void chageByNotReacentlyBought() {
        List<Shelf> shelvesList = new LinkedList<>(shelves);
        //Para todos os pedidos faca:
        for (Integer requestProdId : this.requests) {
            //Se existe alguma prateleira com aquele ID de produto:
            if (productAvailable(requestProdId)) {
                //Pega a prateleira com o Id do produto pedido:
                Shelf shelf = shelfWithProductAvailable(requestProdId);
                //Tira um produto
                takeProduct(shelf);
                shelvesList.remove(shelf);
                shelvesList.add(shelf);
            } //Page not found / Não tem produto pedido na prateleira
            else {
                //Aumenta o numero de falhas
                failures++;
                //ATENCAO AQUI QUE ESCOLHE COMO SUBSTITUI. Escolhe o id da prateleira que vai substituir (no caso a com a prioridade maior do produto requisitado)
                int idShelf = shelvesList.remove(0).getId();
                //Esvazia prateleira
                shelves.get(idShelf - 1).clearSheilf();
                //Vai para o lote do produto velho devolver os produtos
                goToLot(idShelf);
                //Vai do lote do produto velho para o do produto novo recolher os produtos
                goBetweenLots(idShelf, requestProdId);
                //Volta para o mercadinho
                goToLot(requestProdId);
                //Enche a prateleira com o produto novo
                Shelf shelf = shelves.get(idShelf - 1);
                shelf.fillShelf(products.get(requestProdId - 1));
                takeProduct(shelf);
                shelvesList.add(shelf);
            }
        }

        printResult();
    }

    public void changeByShelfDistance() {
        // Para todos os pedidos faca:
        for (Integer requestProdId : this.requests) {
            // Se existe alguma prateleira com aquele ID de produto:
            if (productAvailable(requestProdId)) {
                // Pega a prateleira com o Id do produto pedido:
                Shelf shelf = shelfWithProductAvailable(requestProdId);
                // Tira um produto
                takeProduct(shelf);
            } // Page not found / NÃ£o tem produto pedido na prateleira
            else {
                // Aumenta o numero de falhas
                failures++;
                // ATENCAO AQUI QUE ESCOLHE COMO SUBSTITUI. Escolhe o id da prateleira que vai

                int idShelf = idProjectWithShortestDistance(requestProdId);

                // Esvazia prateleira
                shelves.get(idShelf - 1).clearSheilf();
                // Vai para o lote do produto velho devolver os produtos
                goToLot(idShelf);
                // Vai do lote do produto velho para o do produto novo recolher os produtos
                goBetweenLots(idShelf, requestProdId);
                // Volta para o mercadinho
                goToLot(requestProdId);
                // Enche a prateleira com o produto novo
                Shelf shelf = shelves.get(idShelf - 1);
                shelf.fillShelf(products.get(requestProdId - 1));
                takeProduct(shelf);
            }
        }
        printResult();
    }

    private int idProjectWithShortestDistance(int idProduct){
        int min = Integer.MAX_VALUE;
        int idShelf = 0;
        for (Shelf shelf: shelves) {
            if (shelf.isTaken()){
                if (lots.get(shelf.getTypeProduct().getId()).getDistanceFromMarket()<min){
                    int idShelfActual = shelf.getTypeProduct().getId();
                    min = distance(0, idShelfActual) + distance(idShelfActual, idProduct) + distance(idProduct, 0);
                    idShelf = shelf.getId();
                }
            }
        }
        return idShelf;
    }

    public void changeByLessSold() {
        //Para todos os pedidos faca:
        for (Integer requestProdId : this.requests) {
            //Se existe alguma prateleira com aquele ID de produto:
            if (productAvailable(requestProdId)) {
                //Pega a prateleira com o Id do produto pedido:
                Shelf shelf = shelfWithProductAvailable(requestProdId);
                //Tira um produto
                takeProduct(shelf);
            } //Page not found / Não tem produto pedido na prateleira
            else {
                //Aumenta o numero de falhas
                failures++;
                //ATENCAO AQUI QUE ESCOLHE COMO SUBSTITUI. Escolhe o id da prateleira que vai substituir (no caso a com a prioridade maior do produto requisitado)
                int idShelf = shelfIdWithLeastSoldProduct();
                //Esvazia prateleira
                shelves.get(idShelf - 1).clearSheilf();
                //Vai para o lote do produto velho devolver os produtos
                goToLot(idShelf);
                //Vai do lote do produto velho para o do produto novo recolher os produtos
                goBetweenLots(idShelf, requestProdId);
                //Volta para o mercadinho
                goToLot(requestProdId);
                //Enche a prateleira com o produto novo
                Shelf shelf = shelves.get(idShelf - 1);
                shelf.fillShelf(products.get(requestProdId - 1));
                takeProduct(shelf);
            }
        }
        printResult();
    }

    private int shelfIdWithLeastSoldProduct(){
        int max = 0;
        int idShelf = 0;
        for (Shelf shelf: shelves) {
            if (shelf.isTaken()){
                if (this.sales[shelf.getTypeProduct().getId()]>max){
                    max = this.sales[shelf.getTypeProduct().getId()];
                    idShelf = shelf.getId();
                }
            }
        }
        return idShelf;
    }

    public void changeByNRU() {
        //Para todos os pedidos faca:
        for (Integer requestProdId : this.requests) {
            //Se existe alguma prateleira com aquele ID de produto:
            if (productAvailable(requestProdId)) {
                //Pega a prateleira com o Id do produto pedido:
                Shelf shelf = shelfWithProductAvailable(requestProdId);
                //Tira um produto
                takeProduct(shelf);
            } //Page not found / Não tem produto pedido na prateleira
            else {
                //Aumenta o numero de falhas
                failures++;
                //ATENCAO AQUI QUE ESCOLHE COMO SUBSTITUI. Escolhe o id da prateleira que vai substituir (no caso a com a prioridade maior do produto requisitado)
                int idShelf = shelfIdWithLeastNRUClass();
                //Esvazia prateleira
                shelves.get(idShelf - 1).clearSheilf();
                //Vai para o lote do produto velho devolver os produtos
                goToLot(idShelf);
                //Vai do lote do produto velho para o do produto novo recolher os produtos
                goBetweenLots(idShelf, requestProdId);
                //Volta para o mercadinho
                goToLot(requestProdId);
                //Enche a prateleira com o produto novo
                Shelf shelf = shelves.get(idShelf - 1);
                shelf.fillShelf(products.get(requestProdId - 1));
                takeProduct(shelf);
            }
            counter++;
            checkEpoch();
        }
        printResult();
    }

    private void checkEpoch(){
        if (counter==10){
            counter=0;
            for (Shelf shelf: shelves) {
                for (int i = (shelf.getTime().length-2); i>=0; i--) {
                    shelf.getTime()[i+1]=shelf.getTime()[i];
                }
                shelf.getTime()[0]=shelf.isR();
                shelf.setR(false);
            }
        }
    }

    private int shelfIdWithLeastNRUClass(){
        for (Shelf shelf: shelves) if (!shelf.isR()) return shelf.getId();
        return 0;
        }

    public void changeByAging() {
        //Para todos os pedidos faca:
        for (Integer requestProdId : this.requests) {
            //Se existe alguma prateleira com aquele ID de produto:
            if (productAvailable(requestProdId)) {
                //Pega a prateleira com o Id do produto pedido:
                Shelf shelf = shelfWithProductAvailable(requestProdId);
                //Tira um produto
                takeProduct(shelf);
            } //Page not found / Não tem produto pedido na prateleira
            else {
                //Aumenta o numero de falhas
                failures++;
                //ATENCAO AQUI QUE ESCOLHE COMO SUBSTITUI. Escolhe o id da prateleira que vai substituir (no caso a com a prioridade maior do produto requisitado)
                int idShelf = shelfIdWithLeastRTime();
                //Esvazia prateleira
                shelves.get(idShelf - 1).clearSheilf();
                //Vai para o lote do produto velho devolver os produtos
                goToLot(idShelf);
                //Vai do lote do produto velho para o do produto novo recolher os produtos
                goBetweenLots(idShelf, requestProdId);
                //Volta para o mercadinho
                goToLot(requestProdId);
                //Enche a prateleira com o produto novo
                Shelf shelf = shelves.get(idShelf - 1);
                shelf.fillShelf(products.get(requestProdId - 1));
                takeProduct(shelf);
            }
            counter++;
            checkEpoch();
        }
        printResult();
    }

    private int shelfIdWithLeastRTime(){
        long min = Long.MAX_VALUE;
        int idShelf = 0;
        int[] timeQArray = new int[10];
        int j = 0;
        for (Shelf shelf: shelves){
            int timeQ = 0;
            for (int i = 0; i<shelf.getTime().length; i++) {
                if (shelf.getTime()[i]) timeQ = timeQ + 10 * (shelf.getTime().length - i - 1);
            }
            timeQArray[j]=timeQ;
            j++;
        }
        for (Shelf shelf: shelves)
            if (!shelf.isR()) {
                for (int i = 0; i<shelf.getTime().length; i++) {
                    if (timeQArray[i]<min){
                        min = timeQArray[i];
                        idShelf = i+1;
                    }
                }
        }
        return idShelf;
    }

}