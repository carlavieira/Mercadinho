package tests;

import market.*;

import java.util.LinkedList;
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
//        for (int n = 1; n <= 10; n++) {
//            System.out.println(" Vetor " + n);
//            for (int i = 100; i <= 10000; i += 1100) {
//                int [][] matriz = geraMatriz(i);
//
//                long tempoInicial = System.currentTimeMillis();
//
//                new ShortestPath(i).dijkstra(matriz, 0);
//
//                long tempoFinal = System.currentTimeMillis();
//
//                System.out.println((tempoFinal - tempoInicial));
//            }
//        }
        List<Integer> integerList = new LinkedList<>();
        integerList.add(1);
        integerList.add(3);
        integerList.add(5);
        integerList.add(7);
        integerList.add(9);
        Integer remove = integerList.remove(3);
        System.out.println("removeu o item "+remove);
        System.out.println("novo item 3 "+integerList.get(3));
        integerList.add(7);
        System.out.println("depois de adicionar "+integerList.get(integerList.size()-1));
        for (Integer i:
             integerList) {
            System.out.println(i);
        }
    }
}
