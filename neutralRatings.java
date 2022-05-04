/**
 @file Valoracio.java
 @brief Codigo de la clase Valoracio

 */
package dominio.clases;

import java.net.URISyntaxException;
import java.util.*;
import java.io.*;
import java.io.File;

/**
 * @class Datos
 * @brief Clase dedicada al procesado de un dataset que contiene para un determinado usuario e item, una valoracion de este
 * @author Houda El Fezzak Bekkouri
 */
public class neutralRatings {

    /** @brief contiene el conjunto de valoraciones de cada item,
     *  indexado por el identificador del usuario que ha realizado la valoracion */
    private HashMap<Integer, HashMap<Item, Double>> UserRatings;

    /** @brief contiene el conjunto usuarios que han valorado los items del dataset */
    private List<Usuari> users = new ArrayList<>();

    /**
     * @brief Metodo que devuelve el conjunto de valoraciones asociadas a cada uno de los items del dataset
     *
     * @return el conjunto de valoraciones asociadas a cada uno de los items del dataset
     */
    public HashMap<Item, ArrayList<Double>> getRatings() {
        return ratings;
    }

    private HashMap<Item,ArrayList<Double>> ratings = new HashMap<>();

    /**
     * @brief Metodo que devuelve el conjunto de usuarios que tienen alguna valoracion asociada en el dataset
     *
     * @return el conjunto de usuarios que tienen alguna valoracion asociada en el dataset
     * <pre> @code
     *
     * </pre>
     */
    public List<Usuari> getUsers() {
        return users;
    }

    /**
     * @brief Metodo que devuelve la valoracion maxima del conjunto de valoraciones
     *
     * @return valoracion maxima del conjunto de valoraciones
     */
    public double getMaxRating() {
        return maxRating;
    }

    private double maxRating;

    /**
     * @brief Constructora vacia para la clase Valoracio. Inicializa una nueva instancia.
     */
    public neutralRatings() {
        this.UserRatings = new HashMap<>();
    }

    /**
     * @brief Metodo que devuelve el conjunto usuarios y sus respectivas valoraciones para cada item del dataset
     *
     * @return el conjunto usuarios y sus respectivas valoraciones para cada item del dataset
     */
    public HashMap<Integer, HashMap<Item, Double>> getUserRatings() {
        return UserRatings;
    }

    /**
     * @brief Constructora parametrizada que asocia a una instancia de Valoracio el conjunto de valoraciones de cada usuario del data set para un conjunto de items determinado
     * @param file_name indica el camino hasta el archivo en que se halla el dataset de valoraciones a leer
     * @param dataset indica el Cjt_items del que se dan las valoraciones en el archivo a procesar
     */
    public neutralRatings(String file_name, Cjt_items dataset) {

        this.UserRatings = new HashMap<>();
        HashMap<Integer, HashMap<Item, Double>> User_neutralRatings = new HashMap<>();
        ArrayList<Integer> indices = new ArrayList<>();
        maxRating = 0.0;
        double minRating = 0.0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(readFile(file_name)));
            String line = "";

            try {
                line = br.readLine();
                String[] colNames = line.split(",", -1);
                if (colNames[0].toLowerCase().contains("id")) {
                    if (colNames[0].toLowerCase().contains("item")) indices.add(0,0);
                    else if (colNames[0].toLowerCase().contains("user")) indices.add(0,1);
                }
                else indices.add(0,2);
                if (colNames[1].toLowerCase().contains("id")) {
                    if (colNames[1].toLowerCase().contains("item")) indices.add(1,0);
                    else if (colNames[1].toLowerCase().contains("user")) indices.add(1,1);
                }
                else indices.add(1,2);
                if (colNames[2].toLowerCase().contains("id")) {
                    if (colNames[2].toLowerCase().contains("item")) indices.add(2,0);
                    else if (colNames[2].toLowerCase().contains("user")) indices.add(2,1);
                }
                else indices.add(2,2);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(-1);
            }
            try {
                int id_user = 0;
                String id_item = "";
                double rating = 0;

                ArrayList<Double> item_i_ratings;
                while ((line = br.readLine()) != null) {
                    /** indica la coma como separador de valores */
                    String[] readCols = line.split(",", -1);
                    ArrayList<String> myCols = new ArrayList<>(Arrays.asList(readCols));

                    /** si alguno de los campos es nulo no tiene sentido guardar la valoracion */
                    if (!(myCols.get(0) == null) && !myCols.get(0).isEmpty() && !(myCols.get(1) == null) && !myCols.get(1).isEmpty() && !(myCols.get(2) == null) && !myCols.get(2).isEmpty()) {

                        for (int i = 0; i < myCols.size(); ++i){
                            if (indices.get(i) == 0) id_item = myCols.get(i) ;
                            else if (indices.get(i) == 1) id_user = Integer.parseInt(myCols.get(i));
                            else{
                                rating = Double.parseDouble(myCols.get(i));
                                if (maxRating < rating) maxRating = rating;
                                else if (rating < minRating) minRating = rating;
                            }
                        }
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
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        //User_neutralRatings = new HashMap<>(this.UserRatings);
        HashMap<String,Item> datasetItems = dataset.getItems();
        double neutral = (double)(maxRating+minRating)/2;
        for (int i : UserRatings.keySet()){
            HashMap<Item,Double> item_ratings = UserRatings.get(i);
            for (String id : datasetItems.keySet()){
                Item it = new Item(id);
                if (!item_ratings.containsKey(it)){
                    item_ratings.put(it, neutral);

                }
            }
            UserRatings.put(i,item_ratings);
        }
        for(int i: UserRatings.keySet()){
            users.add(new Usuari(i,UserRatings.get(i)));
        }
    }


    /**
     * @brief Metodo que realiza la lectura de un archivo csv desde un path relativo (el de la carpeta src)
     * @param pathToCsv Ruta relativa desde la cual se accede al archivo deseado
     * @return Archivo sobre el cual hay que realizar la lectura
     * @throws URISyntaxException Error respecto al parseado de la ruta en formato URI
     */
    private File readFile(String pathToCsv) throws URISyntaxException {
        return new File(pathToCsv);
    }

}