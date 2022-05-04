/**
 * @file SlopeOne.java
 * @brief Codigo de la clase SlopeOne
 */
package dominio.clases;

import java.util.*;

/**
 * @author Cristian Sanchez Estape
 * @class SlopeOne
 * @brief Clase dedicada a la implementacion del algoritmo de prediccion SlopeOne
 */

public class SlopeOne extends Algorisme {
    /**
     * @brief Map que almacena, para un unico usuario, las predicciones sobre un conjunto determinado de items
     */
    private HashMap<Item, Double> predictions = new HashMap<>();

    /**
     * @param users Lista de usuarios con valoraciones sobre un conjunto determinado de items
     * @param max   Valoracion maxima posible (cota maxima)
     * @brief Metodo que computa el Weighted SlopeOne
     */

    public void slopeOne(List<Usuari> users, double max, int k) {
        HashMap<Item, Double> queries = user.getRatedItems(), usersRatings;
        HashMap<Item, Double> sums = new HashMap<>();
        HashMap<Item, Integer> nums = new HashMap<>();
        for (Item j : queries.keySet()) {
            for (Usuari i : users) {
                usersRatings = i.getRatedItems();
                if (usersRatings.containsKey(j))
                    for (Item J : usersRatings.keySet()) {
                        if (!J.equals(j)) {
                            if (sums.get(J) == null) {
                                sums.put(J, 0.);
                                nums.put(J, 0);
                            }
                            sums.put(J, sums.get(J) + queries.get(j) + (usersRatings.get(J) - usersRatings.get(j)));
                            nums.put(J, nums.get(J) + 1);
                        }
                    }
            }
        }
        for (Item j : sums.keySet()) {
            double val = sums.get(j);
            if (val >= 0.) predictions.put(j, val / nums.get(j));
        }
        if (predictions.size() > k) {
            Avaluacio.sortByValue(predictions);
            int count = 0;
            HashMap<Item, Double> finalPreds = new LinkedHashMap<>();
            for (Item i : predictions.keySet()) {
                double val = predictions.get(i);
                if (val > max) finalPreds.put(i, max);
                else finalPreds.put(i, val);
                count++;
                if (count == k) break;
            }
            predictions = new LinkedHashMap<>(finalPreds);
        }
    }

    /**
     * @return
     * @brief Metodo get de las predicciones generadas
     */

    public HashMap<Item, Double> getPredictions() {
        return predictions;
    }

    /**
     * @param u Usuario sobre el cual se realizaran las predicciones
     * @brief Constructora de SlopeOne
     */

    public SlopeOne(Usuari u) {
        super();
        super.setUser(u);
    }

}