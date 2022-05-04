package dominio.clases;

import java.util.*;

public class Recomendacion {

    /**
     * @brief Usuario sobre el cual se generaran recomendaciones
     */

    private Usuari user;

    /**
     * @brief Dataset sobre el cual se trabajara (en cuanto a items y usuarios se refiere)
     */

    private Cjt_items dataset;

    /**
     * @brief Diccionario con todos los items del dataset que guarda, para cada uno, todas las valoraciones que tiene
     */

    private HashMap<Item, ArrayList<Double>> ratings;

    /**
     * @brief Valoracion maxima posible para un dataset dado
     */

    private double maxRating;

    /**
     * @brief Lista de los usuarios que han realizado valoraciones sobre el dataset
     */

    private List<Usuari> users;

    private int k;

    /**
     * @param user    Usuario sobre el cual se generaran recomendaciones
     * @param dataset Dataset sobre el cual se trabajara (en cuanto a items y usuarios se refiere)
     * @param ratings Diccionario con todos los items del dataset que guarda, para cada uno, todas las valoraciones que tiene
     * @param mR      Valoracion maxima posible para un dataset dado
     * @param users   Lista de los usuarios que han realizado valoraciones sobre el dataset
     * @brief Constructora del objeto Recomendacion
     */

    public Recomendacion(Usuari user, Cjt_items dataset, HashMap<Item, ArrayList<Double>> ratings, double mR, List<Usuari> users, int k) {
        this.user = user;
        this.dataset = dataset;
        this.ratings = new HashMap<>(ratings);
        this.maxRating = mR;
        this.users = new LinkedList<>(users);
        this.k = k;
    }

    /**
     * @return La prediccion de las valoraciones para cada item no valorado del usuario
     * @brief Metodo de generacion de recomendaciones a traves del algoritmo Collaborative
     */

    public HashMap<Item, Double> Collaborative() {
        if(!users.contains(user)) users.add(user);
        ArrayList<Cluster> centroids = new Collaborative().calcula_k(users);
        List<Usuari> desiredUsers = null;
        for (Cluster c : centroids) {
            HashSet<Usuari> search = c.getUsuaris();
            if (search.contains(user)) {
                desiredUsers = new LinkedList<>(search);
                break;
            }
        }
        desiredUsers.remove(user);
        SlopeOne sp = new SlopeOne(user);
        sp.slopeOne(desiredUsers, maxRating, k);
        return Avaluacio.sortByValue(sp.getPredictions());
    }

    /**
     * @return La prediccion de las valoraciones para cada item no valorado del usuario
     * @brief Metodo de generacion de recomendaciones a traves del algoritmo Content-Based
     */

    public HashMap<Item, Double> Content() {
        ContentBased cb = new ContentBased(user, dataset, ratings, maxRating);
        cb.setK(k);
        return Avaluacio.sortByValue(cb.recommended_items());
    }

    /**
     * @return La prediccion de las valoraciones para cada item no valorado del usuario
     * @brief Metodo de generacion de recomendaciones a traves del algoritmo Hybrid
     */

    public HashMap<Item, Double> Hybrid() {
        Hybrid h = new Hybrid(Collaborative(), Content());
        return Avaluacio.sortByValue(h.recommended_items());
    }


}