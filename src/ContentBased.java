/**
 @file ContentBased.java
 @brief Codigo de la clase ContentBased

 */
package dominio.clases;


import java.util.*;

/**
 * @class ContentBased
 * @brief Clase que implementa el sistema de recomendacion con el metodo "Content Based Filtering"
 * @author Jordi Elgueta Serra
 */

public class ContentBased extends Algorisme {

    /**
     * @brief Parametro k que se usa para el algoritmo K-NN
     */
    private static int k;
    /**
     * @brief Diccionario con los ítems valorados por el Usuario que pide la recomendacion junto con su valoracion
     */
    private HashMap<Item,Double> ratedItems;
    /**
     * @brief Parametro que representa la valoracion maxima que se puede dar a un item
     */
    private double maxRating;

    /**
     * @brief Constructora de ContentBased
     * @param user Usuari que pide la recomendacion
     * @param dataset Instancia de Cjt_items que almacena el dataset de entrada procesado
     * @param ratings Diccionario con los items (keys) y una lista de todas sus valoraciones
     * @param maxRating Valoración máxima registrada
     */
    public ContentBased(Usuari user, Cjt_items dataset, HashMap<Item, ArrayList<Double>> ratings, double maxRating) {
        super(user,dataset, ratings);
        ratedItems = user.getRatedItems();
        this.maxRating = maxRating;
    }

    /**
     * @brief Setter del parametro k
     * @param k Valor que se asigna al parametro k para el K-NN
     */
    public void setK (int k) {
        this.k = k;
    }

    /**
     * @brief Metodo que devuelve los items recomendados al usuario que pide la recomendacion junto con
     * la prediccion de la valoracion qe haría dicho usuario para cada uno de los items
     * @return Diccionario de los items con sus respectivas predicciones de valoracion
     */
    public HashMap<Item, Double> recommended_items() {
        HashMap<Item, Double> predictions = new HashMap<>();
        Item minimum = null;
        for (Item item : ratedItems.keySet()) {
            double rating = ratedItems.get(item);
            HashMap<Item, Double> knn = get_KNN(item, rating);
            for (Item key : knn.keySet()) {
                if (minimum == null) {
                    minimum = key;
                    predictions.put(key, knn.get(key));
                }
                //If item already in the map take the greatest rating prediction
                else if (predictions.containsKey(key)) {
                    if (predictions.get(key) < knn.get(key))
                        predictions.replace(key, knn.get(key));
                }
                else if (predictions.size() < k) {
                    if (predictions.get(minimum) > knn.get(key))
                        minimum = key;
                    predictions.put(key, knn.get(key));
                }
                else if (predictions.get(minimum) < knn.get(key)) {
                    predictions.remove(minimum);
                    predictions.put(key, knn.get(key));
                    minimum = Collections.min(predictions.entrySet(), Map.Entry.comparingByValue()).getKey();
                }
            }
        }
        return predictions;
    }

    /**
     * @brief Metodo que implementa el algortimo K-NN para un item a que recibe como parametro. Devuelve un diccionario
     * con los k items con mejor prediccion de valoracion. Para la prediccion se usa la similitud entre ítems
     * y se pondera con el rating dado por el usuario que pide la recomendacion del item a y con el rating promedio
     * del ítem candidato a formar parte de los k-NN
     * @param a Item para el cual se calculan los K-NN
     * @param rating Valoracion del usuario que pide la recomendacion al ítem a
     * @return Estructura de datos que contiene, para un conjunto de items determinado, sus predicciones
     */

    private HashMap<Item, Double> get_KNN(Item a, double rating) {
        SortedMap<Double, List<Item>> maxPred = new TreeMap<>();
        HashMap<String,Item> data = super.dataset.getItems();
        for (Item dItem : data.values()) {
            if (!ratedItems.containsKey(dItem)) {
                DistanceItems d = new DistanceItems(a, dItem, dataset.getMaxValIntegers().values(), dataset.getMinValIntegers().values(),
                        dataset.getMaxValDoubles().values(), dataset.getMinValDoubles().values(), dataset.getMaxDates().values(),
                        dataset.getMinDates().values());
                double prediction = (1 - d.get_distance()) * maxRating - (maxRating - rating) / (2*maxRating);
                if (super.ratings.containsKey((dItem))) {
                    double meanRating = 0;
                    ArrayList<Double> stars = super.ratings.get(dItem);
                    for (double rate : stars)
                        meanRating += rate;
                    meanRating /= stars.size();
                    prediction -= (maxRating - meanRating) / (2*maxRating);

                }
                if (prediction < 0) prediction = 0.0;
                if(maxPred.containsKey(prediction)) {
                    maxPred.get(prediction).add(dItem);
                }
                else {
                    List<Item> value = new ArrayList<>(List.of(dItem));
                    if (maxPred.size() < k)
                        maxPred.put(prediction, value);
                    else if (maxPred.firstKey() < prediction) {
                        maxPred.remove(maxPred.firstKey());
                        maxPred.put(prediction, value);
                    }
                }
            }
        }
        HashMap<Item, Double> knn = new HashMap<>();
        int itemCounter = 0;
        for (Double d : maxPred.keySet()) {
            for (int i = 0; i < maxPred.get(d).size() && itemCounter < k; ++i, ++itemCounter) {
                knn.put(maxPred.get(d).get(i), d);
            }
            if (itemCounter == k) break;
        }
        return knn;
    }
}
