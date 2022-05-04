package dominio.clases;

import java.util.*;

import static dominio.clases.Avaluacio.sortByValue;

/**
 * @class Hybrid
 * @brief Clase que implementa el sistema de recomendacion con el metodo Hibrido
 * @author Jordi Elgueta Serra
 */

public class Hybrid {
    /**
     * @brief Map que almacena los items recomendados por el metodo Collaborative
     */
    private HashMap<Item, Double> collaborative;
    /**
     * @brief Map que almacena los items recomendados por el metodo ContentBased
     */
    private HashMap<Item, Double> content;
    /**
     * @brief Parametro k que indica el numero de items que se recomendaran
     */
    private int k;

    /**
     * @brief Constructora de la clase Hybrid
     * @param collaborative Map con los items recomendados por el metodo Collaborative como key y sus predicciones como value
     * @param content Map con los items recomendados por el metodo ContentBased como key y sus predicciones como value
     */
    public Hybrid(HashMap<Item, Double> collaborative, HashMap<Item, Double> content) {
        this.collaborative = new HashMap<>(collaborative);
        this.content = new HashMap<>(content);
        this.k = content.size();
    }

    /**
     * @brief Metodo para generar una recomendacion con el Hybrid Recommender
     * @return Map con los items recomendados como keys y las predicciones de la valoracion como values
     */
    public HashMap<Item, Double> recommended_items() {
        HashMap<Item, Double> recommended_items = new HashMap<>();
        ArrayList<Item> deletedItems = new ArrayList<>();
        for (Item i : content.keySet()) {
            if (collaborative.containsKey(i)) {
                recommended_items.put(i, (collaborative.get(i) + content.get(i)) / 2);
                deletedItems.add(i);
            }
        }
        if (recommended_items.size() < k) {
            for(Item i : deletedItems){
                collaborative.remove(i);
                content.remove(i);
            }
            LinkedHashMap<Item, Double> sortedCol = (LinkedHashMap) sortByValue(collaborative);
            LinkedHashMap<Item, Double> sortContent = (LinkedHashMap) sortByValue(content);

            Iterator it1 = sortedCol.entrySet().iterator();
            Iterator it2 = sortContent.entrySet().iterator();
            Map.Entry pair1 = (Map.Entry) it1.next();
            Map.Entry pair2 = (Map.Entry) it2.next();
            while (recommended_items.size() < k) {
                if ((Double) pair1.getValue() > (Double) pair2.getValue()) {
                    recommended_items.put((Item) pair1.getKey(), (Double) pair1.getValue());
                    if (it1.hasNext())
                        pair1 = (Map.Entry) it1.next();
                } else {
                    recommended_items.put((Item) pair2.getKey(), (Double) pair2.getValue());
                    if (it2.hasNext())
                        pair2 = (Map.Entry) it2.next();
                }
            }


//            while (it1.hasNext() && it2.hasNext() && recommended_items.size() < k) {
//                Map.Entry pair1 = (Map.Entry) it1.next();
//                Map.Entry pair2 = (Map.Entry) it2.next();
//                if ((Double) pair1.getValue() > (Double) pair2.getValue()) {
//                    recommended_items.put((Item) pair1.getKey(), (Double) pair1.getValue());
//                } else {
//                    recommended_items.put((Item) pair2.getKey(), (Double) pair2.getValue());
//                }
//            }
//            while (recommended_items.size() < k && it1.hasNext()) {
//                Map.Entry pair1 = (Map.Entry) it1.next();
//                recommended_items.put((Item) pair1.getKey(), (Double) pair1.getValue());
//            }
//            while (recommended_items.size() < k && it2.hasNext()) {
//                Map.Entry pair2 = (Map.Entry) it2.next();
//                recommended_items.put((Item) pair2.getKey(), (Double) pair2.getValue());
//            }
        }
        return recommended_items;
    }
}
