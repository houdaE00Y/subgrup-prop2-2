/**
 @file Datos.java
 @brief Codigo de la clase Valoracio

 */
package dominio.clases;
import persistencia.ControladorPersistencia;
import java.net.URISyntaxException;
import java.util.*;
import java.io.*;
import java.io.File;

/**
 * @class Datos
 * @brief Clase dedicada al procesado de un dataset que contiene para un determinado usuario e item, una valoracion de este
 * @author Houda El Fezzak Bekkouri
 */
public class Datos {

    /**
     * @brief contiene el conjunto de valoraciones de cada item,
     * indexado por el identificador del usuario que ha realizado la valoracion
     */
    private HashMap<Integer, HashMap<Item, Double>> UserRatings;

    /**
     * @brief contiene el conjunto usuarios que han valorado los items del dataset
     */
    private List<Usuari> users = new ArrayList<>();


    /**
     * @return el conjunto de valoraciones asociadas a cada uno de los items del dataset
     * @brief Metodo que devuelve el conjunto de valoraciones asociadas a cada uno de los items del dataset
     */
    public HashMap<Item, ArrayList<Double>> getRatings() {
        return ratings;
    }

    private HashMap<Item, ArrayList<Double>> ratings = new HashMap<>();

    /**
     * @return el conjunto de usuarios que tienen alguna valoracion asociada en el dataset
     * <pre> @code
     *
     * </pre>
     * @brief Metodo que devuelve el conjunto de usuarios que tienen alguna valoracion asociada en el dataset
     */
    public List<Usuari> getUsers() {
        return users;
    }

    /**
     * @return valoracion maxima del conjunto de valoraciones
     * @brief Metodo que devuelve la valoracion maxima del conjunto de valoraciones
     */
    public double getMaxRating() {
        return maxRating;
    }

    private double maxRating;

    /**
     * @brief Constructora vacia para la clase Valoracio. Inicializa una nueva instancia.
     */
    public Datos() {
        this.UserRatings = new HashMap<>();
    }

    /**
     * @return el conjunto usuarios y sus respectivas valoraciones para cada item del dataset
     * @brief Metodo que devuelve el conjunto usuarios y sus respectivas valoraciones para cada item del dataset
     */
    public HashMap<Integer, HashMap<Item, Double>> getUserRatings() {
        return UserRatings;
    }

    /**
     * @param pathToFile indica el camino hasta el archivo en que se halla el dataset de valoraciones a leer
     * @param dataset    indica el Cjt_items del que se dan las valoraciones en el archivo a procesar
     * @brief Constructora parametrizada que asocia a una instancia de Valoracio el conjunto de valoraciones de cada usuario del data set para un conjunto de items determinado
     */
    public Datos(String pathToFile, Cjt_items dataset) {

        ControladorPersistencia CtrlPers = ControladorPersistencia.getInstance();
        try {
            CtrlPers.resetDatos();
        } catch (IOException e){
        }
        this.UserRatings = new HashMap<>();
        ArrayList<Integer> indices = new ArrayList<>();
        maxRating = 0.0;

        try {
            CtrlPers.initializeCtrlDatos(pathToFile);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(-1);
        }
        try {
            int id_user = 0;
            String id_item = "";
            double rating = 0;

            ArrayList<Double> item_i_ratings;
            while (CtrlPers.hasMoreRatings()) {
                CtrlPers.readRating();
                id_user = CtrlPers.getUserId();
                id_item = CtrlPers.getItemId();
                rating = CtrlPers.getRating();
                /** agregamos el item valorado por el usuario con identificador id_user a la lista de valoraciones del usuario  */
                HashMap<Item, Double> itemRatings = this.UserRatings.getOrDefault(id_user, new HashMap<>());
                Item i = dataset.getItem(id_item);
                itemRatings.put(i, rating);
                if (!ratings.containsKey(i)) {
                    item_i_ratings = new ArrayList<>();
                    item_i_ratings.add(rating);
                    ratings.put(i, item_i_ratings);
                } else {
                    item_i_ratings = ratings.get(i);
                    item_i_ratings.add(rating);
                    ratings.put(i, item_i_ratings);
                }
                this.UserRatings.put(id_user, itemRatings);
            }
            maxRating = CtrlPers.getMaxRating();
        } catch (Exception e) {
            System.out.println(e);
        }
        for(int i:UserRatings.keySet())users.add(new Usuari(i, UserRatings.get(i)));
        try {
            CtrlPers.resetDatos();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File readFile(String pathToCsv) throws URISyntaxException {
        return new File(pathToCsv);
    }
}
