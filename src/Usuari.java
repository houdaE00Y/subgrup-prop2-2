/**
 @file Usuari.java
 @brief Codigo de la clase Usuari

 */
package dominio.clases;

import java.util.*;

/**
 * @class Usuari
 * @brief Clase que representa un Usuario
 * @author Muhammad Haris y Cristian Sanchez Estape
 */

public class Usuari {

    /**
     * @brief Identificador de un usuario
     */

    private int userId;


    private Centroid centroid = new Centroid();

    public double getNearest_dist() {
        return nearest_dist;
    }

    public void setNearest_dist(double nearest_dist) {
        this.nearest_dist = nearest_dist;
    }

    private double nearest_dist;

    public Centroid getCentroid() {
        return centroid;
    }


    public void setCentroid(Centroid centroid) {
        this.centroid = centroid;
    }

    /**
     * @brief Map que guarda los items valorados por el usuario y sus respectivas valoraciones
     */

    private HashMap<Item, Double> ratedItems = new HashMap<>();

    /**
     * @brief Constructora de Usuari con id = userId
     * @param userId Identificador del Usuario
     */
    public Usuari(int userId) {
        this.userId = userId;
    }

    /**
     * @brief Constructora de un usuario a partir de una referencia a otro usuario
     * @param u Usuario sobre el cual hay que crear una nueva referencia
     */

    public Usuari(Usuari u) {
        this.userId = u.getUserId();
        ratedItems = new HashMap<>(u.getRatedItems());
    }

    /**
     * @brief Constructora de Usuari con id = userId y ratedItems = ratings
     * @param userId Identificador del Usuario
     * @param ratings Map de los items valorados y sus valoraciones
     */
    public Usuari(int userId, HashMap<Item,Double> ratings) {
        this.userId = userId;
        this.ratedItems= new HashMap<>(ratings);
    }


    /**
     * @brief Metodo getter del ID del usuario
     * @return El ID de usuario
     */

    public int getUserId() { return userId; }

    /**
     * @brief Metodo getter de las valoraciones realizadas por un usuario
     * @return Una estructura de datos que almacena las valoraciones realizdas por el usuario
     */

    public HashMap<Item, Double> getRatedItems() {
        return ratedItems;
    }

    /**
     * @brief Setter de ratedItems
     * @param ratedItems Map de los items valorados y sus valoraciones
     */
    public void setRatedItems(HashMap<Item, Double> ratedItems) {
        this.ratedItems = ratedItems;
    }

    /**
     * @brief Metodo que añade una valoracion de un item en ratedItems
     * @param i El item que se valora
     * @param rating La valoracion del item
     */
    public void addRatedItems(Item i, Double rating) {
        ratedItems.put(i,rating);
    }

    /** @brief override del metodo equals */
    @Override
    public boolean equals(Object o) {
        if(o==null | !(o instanceof Usuari)) return false;
        Usuari u = (Usuari)o;
        return userId == u.getUserId();
    }
    /** @brief override del hashCode */
    @Override
    public int hashCode(){
        return userId;
    }
}