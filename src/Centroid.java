/**
 @file Centroid.java
 @brief Codigo de la clase Centroid

 */
package dominio.clases;

import java.util.*;
/**
 * @class Centroid
 * @brief Clase que representa un Centroide
 * @author Muhammad Haris
 */

public class Centroid {


    /**
     * @brief Identificador de un centroide
     */

    private int centroid_user_id;
    private int centroid_number;

    public int getCentroid_user_id() {
        return centroid_user_id;
    }


    public void setCentroid_user_id(int centroid_user_id) {
        this.centroid_user_id = centroid_user_id;
    }

    /**
     * @brief Map para la representacion de un centroide que guarda los items del centroide y sus respectivas valoraciones
     */

    private HashMap<Item, Double> centroid = new HashMap<>();

    private HashMap<Item,Double> valoraciones = new HashMap<>();
    private HashMap<Item,Integer> times = new HashMap<>();
    private HashSet<Usuari> users = new HashSet<>();

    /**
     * @brief Getter del centroide
     * @return Estructura de datos que contiene los items (y las valoraciones asociadas a los mismos) para un centroide dado
     */
    public HashMap<Item, Double> getCentroid() {
        return centroid;
    }

    /**
     * @brief Getter del numero de centroide
     * @return Nº del centroide
     */

    public int getCentroid_number() { return centroid_number;}

    /**
     * @brief Setter del numero de centroides
     * @param centroid_number Nº de centroides nuevo
     */

    public void setCentroid_number(int centroid_number) {
        this.centroid_number = centroid_number;
    }

    /**
     * @brief Setter del centroide
     * @param centroid El centroide
     */

    public void setCentroid(HashMap<Item, Double> centroid) {

        this.centroid = centroid;
    }

    public HashMap<Item, Double> getValoraciones() {
        return valoraciones;
    }

    public void setValoraciones(HashMap<Item, Double> valoraciones) {
        this.valoraciones = valoraciones;
    }

    public HashMap<Item, Integer> getTimes() {
        return times;
    }

    public void setTimes(HashMap<Item, Integer> times) {
        this.times = times;
    }

    public HashSet<Usuari> getUsers() {
        return users;
    }

    public void setUsers(HashSet<Usuari> users) {
        this.users = users;
    }
}
