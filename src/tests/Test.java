package tests;

import market.*;

import java.util.List;

public class Test {

    private static int [][] geraMatriz(int quantidade){
        int[][] matriz = new int[quantidade][quantidade];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                matriz[i][j] = (int) (Math.random() * quantidade) - 1;
            }
        }
        return matriz;
    }

    public static void main(String[] args) {
        for (int n = 1; n <= 10; n++) {
            System.out.println(" Vetor " + n);
            for (int i = 100; i <= 10000; i += 1100) {
                int [][] matriz = geraMatriz(i);

                long tempoInicial = System.currentTimeMillis();

                new ShortestPath(i).dijkstra(matriz, 0);

                long tempoFinal = System.currentTimeMillis();

                System.out.println((tempoFinal - tempoInicial));
            }
        }


    }
}
