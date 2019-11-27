package market;

import java.util.*;
import java.lang.*;
import java.io.*;

public class ShortestPath {
    // Acha o vértice com menor distância de um conjunto de vértices ainda não na árvore de caminho mínimo
    static int size;

    public ShortestPath(int size) {
        this.size= size;
    }


    private int minDistance(int dist[], Boolean sptSet[]) {
        // Valor mínimo iniciando como "infinito" (maior valor possível) e índice -1;
        int min = Integer.MAX_VALUE;
        int min_index = -1;

        for (int v = 0; v < size; v++)
            //Se v ainda não é caminho mínimo e a distância de v até a origem é menor que o mínimo
            if (sptSet[v] == false && dist[v] <= min) {
                //min recebe v
                min = dist[v];
                min_index = v;
            }
        //retorna o indice do vertice com menor valor até a origem naquema iteração
        return min_index;
    }


    public int[] dijkstra(int graph[][], int src) {
        //dist[i] guardará a menor distância entre a origem para i.
        int dist[] = new int[size];

        // Vetor booleano o qual responde se o vertice i está na árvore de menor caminho ou não
        Boolean sptSet[] = new Boolean[size];

        //inicia colocando todas as distâncias como infinitas e sptSet como falso.
        for (int i = 0; i < size; i++) {
            dist[i] = Integer.MAX_VALUE;
            sptSet[i] = false;
        }

        // Distancia da origem para ela mesma é 0.
        dist[src] = 0;

        // Achar o caminho mínimo para todos os vertices
        for (int count = 0; count < size - 1; count++) {
            // Pega a distância mínima de um vértice até o conjunto de vértices não processados.
            // Na primeira iteração u sempre será a origem.
            int u = minDistance(dist, sptSet);

            // Marcar id u como processado.
            sptSet[u] = true;

            // Atualiza os valores dos demais vértices em relação ao novo elemento na árvore de caminho mínimo
            for (int v = 0; v < size; v++)
                //Se tiver um caminho de v para a origem, passando por u, que seja menor que a distância de v para a origem, substitui valores
                if (!sptSet[v] &&  graph[u][v] != -1 &&
                        dist[u] != Integer.MAX_VALUE && dist[u] + graph[u][v] < dist[v])
                    dist[v] = dist[u] + graph[u][v];
        }

        // retorna vetor dist com os caminhos mínimos entre a origem e cada vértice
        return dist;
    }
}