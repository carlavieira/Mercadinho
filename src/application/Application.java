package application;

import market.Market;
import util.Teclado;

public class Application {


    public static void main(String[] args) {
        int op;
        System.out.println("\n###                             Mercadinho do Raul                      ###");
        System.out.println("       ===================================================================");
        System.out.println("      | Realizar Troca Por:                                               |");
        System.out.println("      |    1. Prateleira preferencial do produto                          |");
        System.out.println("      |    2. Prateleira que está com um produto a mais tempo (FIFO)      |");
        System.out.println("      |    3. Prateleira que está com produto menos recentemente comprado |");
        System.out.println("      |    4. Prateleira que está com um produto de lote menos distante   |");
        System.out.println("      |    0. Sair                                                        |");
        System.out.println("       ===================================================================\n");

        System.out.print("Informe uma opcao: ");
        op = Teclado.readInt();
        System.out.print("\n");
        switch (op) {
            case 1:
                Market market = new Market();
                market.changeByShelfPreference();
                market.printResult();
        }
    }
}