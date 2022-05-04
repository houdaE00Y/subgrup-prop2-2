/** @mainpage
 * Doxygen del proyecto
 *
 */

/**
 @file Main.java
 @brief Codigo de la clase Main

 */
package dominio.clases;

import java.util.*;

/**
 * @class Main
 * @brief Clase que ejecuta el sistema recomendador. El sistema permite al usuario elegir un dataset y pedir recomendaciones
 * con el algoritmo Collaborative Filtering y ContentBased Filtering.
 * @author Cristian Sanchez Estape y Jordi Elgueta Serra
 */

public class Main {

    public static void main(String[] args) {
        boolean finished = false;
        System.out.print("Bienvenido al sistema recomendador. ");
        while (!finished) {
            int id, algo, ds, sz = 0;
            String pathToCsv = System.getProperty("user.dir") + "/Datasets/";
            String pathToAuxCsv = System.getProperty("user.dir") + "/Datasets/";
            Scanner sc = new Scanner(System.in);
            System.out.print("Indique el dataset a usar (0 -> Movielens; 1 -> Series): ");
            ds = sc.nextInt();
            while (ds != 0 && ds != 1) {
                System.out.print("Número incorrecto, inténtalo de nuevo:");
                ds = sc.nextInt();
            }
            switch (ds) {
                case 0:
                    pathToCsv += "Movielens/";
                    pathToAuxCsv += "Movielens/data_types_movies.csv";
                    System.out.print("Elije el tamaño del dataset (0 -> 250; 1 -> 750, 2 -> 2250, 3 -> 6750): ");
                    sz = sc.nextInt();
                    while (sz != 0 && sz != 1 && sz != 2 && sz != 3) {
                        System.out.print("Tamaño erroneo. Pruebe de nuevo: ");
                        sz = sc.nextInt();
                    }
                    break;
                case 1:
                    pathToCsv += "Series/";
                    pathToAuxCsv += "Series/data_types_series.csv";
                    System.out.print("Elije el tamaño del dataset (0 -> pequeño; 1 -> mediano; 2 -> grande): ");
                    sz = sc.nextInt();
                    while (sz != 0 && sz != 1 && sz != 2) {
                        System.out.print("Tamaño erroneo. Pruebe de nuevo: ");
                        sz = sc.nextInt();
                    }
            }
            switch (sz) {
                case 0:
                    pathToCsv += "250/";
                    break;
                case 1:
                    pathToCsv += "750/";
                    break;
                case 2:
                    pathToCsv += "2250/";
                    break;
                case 3:
                    pathToCsv += "6750/";
                    break;
            }
            //Lectura del dataset y de los ratings
            Cjt_items dataset = new Cjt_items(pathToCsv + "items.csv", pathToAuxCsv);
            HashMap<String,Item> items = dataset.getItems();
            Datos ratings = new Datos(pathToCsv + "ratings.db.csv", dataset);
            Datos ratingsKnown = new Datos(pathToCsv + "ratings.test.known.csv", dataset);
            Datos ratingsUnknown = new Datos(pathToCsv + "ratings.test.unknown.csv", dataset);
            double maxRating = ratings.getMaxRating();

            System.out.print("Elije el usuario al que se le hara la recomendacion (-1 -> Si quieres que se hagan recomendaciones a todos los usuarios del fichero known): ");
            id = sc.nextInt();
            while (!ratingsKnown.getUserRatings().containsKey(id) && id != -1) {
                System.out.println("El usuario que has introducido es incorrecto, introducelo de nuevo: ");
                id = sc.nextInt();
            }
            System.out.print("Elije el algoritmo a usar (0 -> Collaborative filltering; 1 -> Content-based; 2 -> Hybrid): ");
            algo = sc.nextInt();
            while (algo != 0 && algo != 1 && algo != 2) {
                System.out.print("Instruccion incorrecta, inténtalo de nuevo: ");
                algo = sc.nextInt();
            }

            HashMap<Integer,HashMap<Item,Double>> unkown = ratingsUnknown.getUserRatings();
            int k;
            System.out.print("Escoje el numero de items que quieres que se recomiendan: ");
            k = sc.nextInt();
            while (k > items.size()) {
                System.out.print("Número incorrecto, inténtalo de nuevo:");
                k = sc.nextInt();
            }
            if (id != -1) {
                Usuari us = null;
                for (Usuari u : ratingsKnown.getUsers())
                    if (u.getUserId() == id) {
                        us = u;
                        break;
                    }
                switch (algo) {
                    case 0:
                        SlopeOne sp = new SlopeOne(us);
                        System.out.println("Los ítems que recomendamos para el usuario " + us.getUserId() + " son: ");
                        Collaborative alg = new Collaborative();
                        List<Usuari> aux2 = ratings.getUsers();
                        aux2.add(us);
                        ArrayList<Cluster> centroids = alg.recommendations(aux2, 10, new DistanceUsers(),
                                100);
                        List<Usuari> nearby = new LinkedList<>();
                        Set<Item> neededItems = new HashSet<>();
                        for (Cluster c : centroids) {
                            HashSet<Usuari> aux = c.getUsuaris();
                            if (aux.contains(us)) {
                                nearby.addAll(aux);
                                break;
                            }
                        }
                        for(Usuari u: nearby) neededItems.add((Item) u.getRatedItems().keySet());
                        List<Item> analizedItems = new ArrayList<>();
                        analizedItems.addAll(neededItems);
                        sp.slopeOne(nearby, maxRating,k);
                        HashMap<Item, Double> preds = sp.getPredictions();
                        Algorisme.print(preds);
                        break;
                    case 1:
                        System.out.println("Los ítems que recomendados para el usuario " + us.getUserId() + " son: ");
                        ContentBased cb = new ContentBased(us, dataset, ratings.getRatings(), maxRating);
                        cb.setK(k);
                        HashMap<Item, Double> recomendations = cb.recommended_items();
                        Algorisme.print(Avaluacio.sortByValue(recomendations));
                        HashMap<Integer, HashMap<Item, Double>> vals = new HashMap<>();
                        vals.put(us.getUserId(), recomendations);
                        break;
                }

                System.out.print("Quieres pedir recomendaciones con otro Dataset?(y/n) ");
                String s = sc.next();
                finished = s.equals("n");
            } else {
                for (Usuari us : ratingsKnown.getUsers()) {
                    switch (algo) {
                        case 0:
                            SlopeOne sp = new SlopeOne(us);
                            System.out.println("Los ítems que recomendamos para el usuario " + us.getUserId() + " son: ");
                            Collaborative alg = new Collaborative();
                            List<Usuari> aux2 = ratings.getUsers();
                            aux2.add(us);
                            ArrayList<Cluster> centroids = alg.recommendations(aux2, 5, new DistanceUsers(),
                                    100);
                            List<Usuari> nearby = new LinkedList<>();
                            Set<Item> neededItems = new HashSet<>();
                            for (Cluster c : centroids) {
                                HashSet<Usuari> aux = c.getUsuaris();
                                if (aux.contains(us)) {
                                    nearby.addAll(aux);
                                    break;
                                }
                            }
                            for(Usuari u: nearby) neededItems.addAll(u.getRatedItems().keySet());
                            List<Item> analizedItems = new ArrayList<>();
                            analizedItems.addAll(neededItems);
                            sp.slopeOne(nearby, maxRating,k);
                            HashMap<Item, Double> preds = sp.getPredictions();
                            Algorisme.print(preds);
                            Usuari u = new Usuari(us.getUserId());
                            u.setRatedItems(preds);
                            System.out.print("Evaluacion de la recomendacion: ");
                            System.out.println(Avaluacio.DCG(u,us));
                            break;
                        case 1:
                            System.out.println("Los ítems que recomendados para el usuario " + us.getUserId() + " son: ");
                            ContentBased cb = new ContentBased(us, dataset, ratings.getRatings(), maxRating);
                            cb.setK(k);
                            HashMap<Item, Double> recomendations = cb.recommended_items();
                            Algorisme.print(Avaluacio.sortByValue(recomendations));
                            u = new Usuari(us.getUserId());
                            u.setRatedItems(recomendations);
                            System.out.print("Evaluacion de la recomendacion: ");
                            System.out.println(Avaluacio.DCG(u,us));
                            break;
                        case 2:
                            System.out.println("Los ítems que recomendados para el usuario " + us.getUserId() + " son: ");
                            List<Usuari> aux = ratings.getUsers();
                            aux.add(us);
                            HashMap<Item, Double> recomendations1 = new Recomendacion(us, dataset, ratings.getRatings(), maxRating,aux,k).Hybrid();
                            Algorisme.print(Avaluacio.sortByValue(recomendations1));
                            u = new Usuari(us.getUserId());
                            u.setRatedItems(recomendations1);
                            System.out.print("Evaluacion de la recomendacion: ");
                            System.out.println(Avaluacio.DCG(u,us));
                    }
                }
                System.out.print("Quieres pedir recomendaciones con otro Dataset?(y/n) ");
                String s = sc.next();
                finished = s.equals("n");
            }
        }
        System.out.println("La ejecución ha terminado.");
    }

}
