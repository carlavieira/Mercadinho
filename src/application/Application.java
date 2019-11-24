package application;

import market.Market;
import util.Teclado;

public class Application {
    Market market = new Market();


    public static void main(String[] args) {
        int op;
            System.out.println("\n###                    Mercadinho do Raul               ###");
            System.out.println("       ====================================================");
            System.out.println("      | Realizar Troca Por:                                |");
            System.out.println("      |    1. Prateleira preferencial do produto           |");
            System.out.println("      |    2. Execuçao em tempo real                       |");
            System.out.println("      |    0. Sair                                         |");
            System.out.println("       ====================================================\n");

            System.out.print("Informe uma opcao: ");
            op = Teclado.readInt();
            System.out.print("\n");

//            switch (op) {
//                case 1:
//
    }
}