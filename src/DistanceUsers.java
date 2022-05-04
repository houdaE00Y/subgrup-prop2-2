/**
 @file DistanceUsers.java
 @brief Codigo de la clase DistanceUsers

 */
package dominio.clases;

import java.util.*;

/**
 * @class DistanceUsers
 * @brief Clase que calcula la distancia entre un usuario y un centroide
 * @author Muhammad Haris
 */

public class DistanceUsers {

    /**
     * @brief Metodo que calcula la distancia euclidiana entre un usuario y un centroide
     * @param f1 Map que guarda los items del usuario y sus respectivas valoraciones
     * @param f2 Map que guarda los items del centroide y sus respectivas valoraciones
     * @return Distancia euclidiana entre el usuario y el centroide
     */
    public double calculateEuclideanDistance (Map<Item, Double> f1, Map<Item, Double> f2) {
        double sum = 0;
        int i = 0;
        for (Item key : f1.keySet()) {
            Double v1 = f1.get(key);
            Double v2 = f2.get(key);
            if (v1 != null && v2 != null) sum += Math.pow(v1-v2,2);
            else ++i;
        }
        if (i == f1.size()) return -1.0;
        else return Math.sqrt(sum);
    }

    /*
    public double calculateCosineSimilarity(Map<Item,Double> f1, Map<Item, Double> f2) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        int i = 0;
        for (Item key : f1.keySet()) {
            Double v1 = f1.get(key);
            Double v2 = f2.get(key);
            if (v1 != null && v2 != null) {
                dot += v1*v2;
                normA += Math.pow(v1, 2);
                normB += Math.pow(v2, 2);
            }
            else ++i;
        }
        if (i == f1.size()) return -1.0;
        else return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
     */
}