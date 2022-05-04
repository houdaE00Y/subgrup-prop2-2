/**
 @file Algorisme.java
 @brief Codigo de la clase Algorisme

 */
package dominio.clases;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * @class Algorisme
 * @brief Superclase de los Algortimos de recomendacion
 * @author Cristian Sanchez Estape y Jordi Elgueta Serra
 */

public class Algorisme {
    /**
     * @brief Dataset sobre el cual se quiere aplicar la recomendacion
     */
    protected Cjt_items dataset;

    /**
     * @brief Usuario que pide la recomendacion
     */
    protected Usuari user;
    /**
     * @brief Diccionario con todos los items del dataset que guarda, para cada uno, todas las valoraciones que tiene
     */
    protected HashMap<Item, ArrayList<Double>> ratings;

    /**
     * @brief Lista con los usuarios con valoraciones para el presente dataset
     */

    protected List<Usuari> users;

    /**
     * @brief Creadora de la clase Algorisme
     * @param user Usuario que pide la recomendacion
     * @param dataset Dataset sobre el cual se quiere aplicar la recomendacion
     * @param ratings Diccionario con todos los items del dataset que guarda, para cada uno, todas las valoraciones que tiene
     */
    public Algorisme(Usuari user, Cjt_items dataset, HashMap<Item, ArrayList<Double>> ratings) {
        this.user=user;
        this.dataset=dataset;
        this.ratings=new HashMap<>(ratings);
    }

    /**
     * @brief Creadora por defecto de la clase Algorisme
     */
    public Algorisme(){}

    /**
     * @brief Metodo set del usuario
     * @param user Usuario sobre el cual trabajamos
     */

    public void setUser(Usuari user) {
        this.user = new Usuari(user);
    }

    /**
     * @brief Metodo set de los usuarios del dataset
     * @param users Usuarios que tienen valoraciones para el dataset
     */

    public void setUsers(List<Usuari> users) {
        this.users = new LinkedList<>(users);
    }

    /**
     * @brief Metodo que imprime por pantalla los usuarios junto con todas sus valoraciones
     * @param valoracions Diccionario que contiene los usuarios con todas sus valoraciones en forma de otro diccionario
     *                    que contiene los items y sus respectivas valoraciones
     */
    public static void printData(Map<Integer, HashMap<Item, Double>> valoracions) {
        for (Integer u : valoracions.keySet()) {
            System.out.println("Usuari " + u + ":");
            print(valoracions.get(u));
        }
    }

    /**
     * @brief Metodo que imprime por pantalla los items con su valoracion
     * @param ratings Diccionario que contine todos los items con su valoracion
     */
    public static void print(HashMap<Item, Double> ratings) {
        NumberFormat formatter = new DecimalFormat("#0.000");
        for (Item i : ratings.keySet()) System.out.println(" " + i.getId() + " --> " + formatter.format(ratings.get(i).doubleValue()));

    }

    public static void print(HashMap<Item, Double> ratings, int times) {
        NumberFormat formatter = new DecimalFormat("#0.000");
        int x = 0;
        for (Item i : ratings.keySet()) {
            System.out.println(" " + i.getId() + " --> " + formatter.format(ratings.get(i).doubleValue()));
            if(x==times) break;
            x++;
        }

    }

}
