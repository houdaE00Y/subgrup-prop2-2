/**
 @file Collaborative.java
 @brief Codigo de la clase Collaborative

 */
package dominio.clases;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @class Collaborative
 * @brief Clase dedicada a la implementacion del algoritmo K-means
 * @author Muhammad Haris
 */


public class Collaborative extends Algorisme{

    public Collaborative(){
        super();
    }

    /**
     * @brief Instancia de Random()
     */

    private static final Random random = new Random();

    public static long total_time = 0l;

    /**
     * @brief Metodo que ejecuta el algoritmo k-means. Devuelve k clusters con sus centroides y los usuarios correspondientes
     * @param Usuaris Lista de usuarios del dataset
     * @param k Numero de clusters
     * @param distance Instancia de la clase DistanceUsers
     * @param n Numero maximo de iteraciones
     * @return K clusters
     */
    public ArrayList<Cluster> recommendations (List<Usuari> Usuaris, int k, DistanceUsers distance, int n) {
        long startTimeTotal = System.nanoTime();
        long startTimeApplyCentroids = System.nanoTime();
        List<Centroid> centroids = applyCentroids(Usuaris,k);
        long stopTimeApplyCentroids = System.nanoTime();
        long elapsedTimeApplyCentroids = stopTimeApplyCentroids - startTimeApplyCentroids;
        System.out.println("TIME APPLYCENTROIDS "  + elapsedTimeApplyCentroids/1000000000);
        ArrayList<Cluster> clusters = new ArrayList<>();
        int aka = 0;
        int nc = 0;
        long elapsedTimeRC = 0l;
        long elapsedTimeNC = 0l;
        long elapsedTimeAC = 0l;
        long elapsedTimeEC = 0l;
        long elapsedTimeIF = 0l;
        for (int i = 0; i < n; ++i) {
            aka = i;
            boolean isLastIteration = i == n - 1;
            int terminar = 0;
            for (Usuari Usuari : Usuaris) {
                if (i == 0) {
                    long startTimeNC = System.nanoTime();
                    ++nc;
                    Centroid centroid = nearestCentroid(Usuari, centroids, distance, k);
                    for (Item key : Usuari.getRatedItems().keySet()) {
                        if (centroid.getValoraciones().containsKey(key)) centroid.getValoraciones().put(key, centroid.getValoraciones().get(key)+Usuari.getRatedItems().get(key));
                        else centroid.getValoraciones().put(key, Usuari.getRatedItems().get(key));
                        if (!centroid.getTimes().containsKey(key)) centroid.getTimes().put(key,1);
                        else centroid.getTimes().put(key,centroid.getTimes().get(key)+1);
                    }
                    Usuari.setCentroid(centroid);
                    long stopTimeNC = System.nanoTime();
                    elapsedTimeNC += stopTimeNC - startTimeNC;
                    long startTimeAC = System.nanoTime();
                    assignToCluster(clusters, Usuari, centroid);
                    long stopTimeAC = System.nanoTime();
                    elapsedTimeAC += stopTimeAC - startTimeAC;
                }
                else {
                    long startTimeIF = System.nanoTime();
                    if (distance.calculateEuclideanDistance(Usuari.getRatedItems(), Usuari.getCentroid().getCentroid()) <= Usuari.getNearest_dist()) {
                        ++terminar;
                        long stopTimeIF = System.nanoTime();
                        elapsedTimeIF += stopTimeIF - startTimeIF;
                    }

                    else {
                        long stopTimeIF = System.nanoTime();
                        elapsedTimeIF += stopTimeIF - startTimeIF;
                        long startTimeNC = System.nanoTime();
                        ++nc;
                        for (Item key : Usuari.getRatedItems().keySet()) {
                            Usuari.getCentroid().getValoraciones().put(key, Usuari.getCentroid().getValoraciones().get(key)- Usuari.getRatedItems().get(key));
                            Usuari.getCentroid().getTimes().put(key,Usuari.getCentroid().getTimes().get(key)-1);
                            if (Usuari.getCentroid().getTimes().get(key) == 0) Usuari.getCentroid().getValoraciones().remove(key);
                        }
                        Centroid centroid = nearestCentroid(Usuari, centroids, distance, k);
                        long stopTimeNC = System.nanoTime();
                        elapsedTimeNC += stopTimeNC - startTimeNC;
                        long startTimeEC = System.nanoTime();
                        eliminateFromCluster(Usuari, clusters);
                        for (Item key : Usuari.getRatedItems().keySet()) {
                            if (centroid.getValoraciones().containsKey(key))
                                centroid.getValoraciones().put(key, centroid.getValoraciones().get(key) + Usuari.getRatedItems().get(key));
                            else centroid.getValoraciones().put(key, Usuari.getRatedItems().get(key));
                            if (!centroid.getTimes().containsKey(key)) centroid.getTimes().put(key, 1);
                            else centroid.getTimes().put(key, centroid.getTimes().get(key) + 1);
                        }
                        Usuari.setCentroid(centroid);
                        long stopTimeEC = System.nanoTime();
                        elapsedTimeEC += stopTimeEC - startTimeEC;
                        long startTimeAC = System.nanoTime();
                        assignToCluster(clusters, Usuari, centroid);
                        long stopTimeAC = System.nanoTime();
                        elapsedTimeAC += stopTimeAC - startTimeAC;
                    }
                }
            }
            boolean shouldTerminate = isLastIteration || terminar == Usuaris.size();
            if (shouldTerminate) {
                break;
            }
            long startTimeRC = System.nanoTime();
            centroids = relocateCentroids(clusters);
            long stopTimeRC = System.nanoTime();
            elapsedTimeRC += stopTimeRC - startTimeRC;
        }
        System.out.println("AKA " + aka);
        System.out.println("TIME NC "  + elapsedTimeNC);
        System.out.println("TIME AC "  + elapsedTimeAC);
        System.out.println("TIME RC "  + elapsedTimeRC);
        System.out.println("TIME EC "  + elapsedTimeEC);
        System.out.println("TIME IF "  + elapsedTimeIF);
        long stopTimeTotal = System.nanoTime();
        total_time = ( stopTimeTotal - startTimeTotal) - total_time ;
        return clusters;
    }




    public static void eliminateFromCluster(Usuari u, List<Cluster> clusters) {
        for (Cluster c: clusters) {
            if (c.getCentroid() == u.getCentroid()) {
                HashSet<Usuari> usuaris = new HashSet<>();
                usuaris = c.getUsuaris();
                usuaris.remove(u);
                c.setUsuaris(usuaris);
            }
        }
    }

    /**
     * @brief Metodo que genera y devuelve k centroides
     * @param Usuaris Lista de usuarios del dataset
     * @param k Numero de clusters
     * @return Lista de los centroides generados
     */


    public static List<Centroid> applyCentroids(List<Usuari> Usuaris, int k) {

        List<Centroid> centroids = new ArrayList<>();
        for (int j = 0; j < k; ++j) {
            int aux = 0;
            Usuari u =  Usuaris.get(random.nextInt(Usuaris.size()));
            for (Centroid ce : centroids) {
                if (u.getUserId() != ce.getCentroid_user_id()) {
                    ++aux;
                }
            }
            if (aux == centroids.size()) {
                Centroid c = new Centroid();
                c.setCentroid_number(j + 1);
                u.getRatedItems().forEach((key, value) -> {
                    c.getCentroid().put(key, value);
                });
                c.setCentroid_user_id(u.getUserId());
                centroids.add(c);
            }
            else --j;
        }
        return centroids;




/*
          List<Centroid> centroids = new ArrayList<>();
        ArrayList<Usuari> mk = new ArrayList<>();
        ArrayList<Usuari> aux = new ArrayList<>();
        mk = minDstanceUsers(Usuaris, new DistanceUsers());
        Usuaris.remove(mk.get(0));
        Usuaris.remove(mk.get(1));
        Centroid c1 = new Centroid();
        c1.setCentroid_number(1);
        mk.get(0).getRatedItems().forEach((key, value) -> {
            c1.getCentroid().put(key, value);
        });
        c1.setCentroid_user_id(mk.get(0).getUserId());
        centroids.add(c1);
        Centroid c2 = new Centroid();
        c2.setCentroid_number(2);
        mk.get(1).getRatedItems().forEach((key, value) -> {
            c2.getCentroid().put(key, value);
        });
        c2.setCentroid_user_id(mk.get(1).getUserId());
        centroids.add(c2);
        for (int i = 0; i < k - 2; ++i) {
            Usuari u = minDstanceUserSet(Usuaris, mk, new DistanceUsers());
            aux.add(u);
            Usuaris.remove(u);
            Centroid c3 = new Centroid();
            c3.setCentroid_number(3+i);
            u.getRatedItems().forEach((key, value) -> {
                c3.getCentroid().put(key, value);
            });
            c3.setCentroid_user_id(u.getUserId());
            centroids.add(c3);
       }
        Usuaris.add(mk.get(0));
        Usuaris.add(mk.get(1));
        for (int i = 0; i < aux.size(); ++i) {
            Usuaris.add(aux.get(i));
        }
        return centroids;


 */
/*
        List<Centroid> centroids = new ArrayList<>();
        int i = 0;
        for (Usuari Usuari : Usuaris) {
            if (i < k) {
                Centroid c = new Centroid();
                c.setCentroid_number(i + 1);
                Usuari.getRatedItems().forEach((key,value) -> { c.getCentroid().put(key,value);});
                centroids.add(c);
                ++i;
            } else break;
        }
        return centroids;


*/

    }




    /**
     * @brief Metodo que recoloca el centroide de todos los clusters respecto al promedio de los usuarios asignados
     * @param clusters Los clusters actuales
     * @return Lista de los centroides recolocados
     */

    public static List<Centroid> relocateCentroids(ArrayList<Cluster> clusters) {
        List<Centroid> centroids = new ArrayList<>();
        for (Cluster c : clusters) {
            Centroid ce = average(c.getCentroid());
            centroids.add(ce);
        }
        return centroids;
    }

    /**
     * @brief Metodo que mueve el centroide a su posicion promedio. Se suman las valoraciones de los items en comun de todos los usuarios pertenecientes al cluster y se divide entre el numero de veces que aparece el item.
     * @param centroid El centroide
     * @return El centroide promedio
     */

    public static Centroid average(Centroid centroid) {
        HashMap<Item, Double> average = new HashMap<>();
        for (Item key : centroid.getValoraciones().keySet()) {
            average.put(key, centroid.getValoraciones().get(key)/centroid.getTimes().get(key));
        }
        centroid.setCentroid(average);
        return centroid ;
    }

    /**
     * @brief Metodo que devuelve los items en comun entre un usuario y un centroide
     * @param f1 Map que guarda los items del usuario y sus respectivas valoraciones
     * @param f2 Map que guarda los items del centroide y sus respectivas valoraciones
     * @return Numero de items en comun
     */

    public static int calculaItemsComun(Map<Item, Double> f1, Map<Item, Double> f2) {
        int i = 0;
        for (Item key : f1.keySet()) {
            if (f2.containsKey(key) == true) {
                ++i;
            }
        }

        return i;
    }

    /**
     * @brief Metodo que devuelve el centroide mas cercano a un usuario
     * @param Usuari El usuario
     * @param centroids La lista de todos los centroides
     * @param distance Instancia de la clase DistanceUsers
     * @return El centroide mas cercano al usuario
     */

    public static Centroid nearestCentroid(Usuari Usuari, List<Centroid> centroids, DistanceUsers distance, int k) {
        double minimumDistance = Double.MAX_VALUE;
        int maxComun = -1;
        Centroid nearest = new Centroid();
        for (Centroid centroid : centroids) {
            double currentDistance =  distance.calculateEuclideanDistance(Usuari.getRatedItems(), centroid.getCentroid());
            int currentComun = calculaItemsComun(Usuari.getRatedItems(), centroid.getCentroid());
            if (currentDistance < minimumDistance && currentDistance != -1 || currentDistance == minimumDistance && currentDistance != -1 && currentComun > maxComun) {
                minimumDistance = currentDistance;
                maxComun = currentComun;
                nearest = centroid;

            }

        }
        if (nearest.getCentroid().size() == 0) {
            nearest = centroids.get(random.nextInt(k));
        }
        Usuari.setNearest_dist(minimumDistance);
        return nearest;
    }

    /**
     * @brief Metodo que asigna el usuario a su centroide correspondiente
     * @param clusters Clusters
     * @param Usuari El usuario
     * @param centroid El centroide al que pertenece el usuario
     */

    public static void assignToCluster(ArrayList<Cluster> clusters, Usuari Usuari, Centroid centroid) {
        boolean exist = false;
        for (Cluster c : clusters) {
            if (c.getCentroid() == centroid) {
                exist = true;
                c.addUsuari(Usuari);
            }
        }

        if (!exist) {
            Cluster cl = new Cluster();
            cl.setCentroid(centroid);
            HashSet<Usuari> u = new HashSet<>();
            u.add(Usuari);
            cl.setUsuaris(u);
            clusters.add(cl);
        }

    }

    public static double maxDistanceUserCentroid(ArrayList<Cluster> clusters, DistanceUsers distance) {
        Double maxDistance = Double.MIN_VALUE;
        for (Cluster c: clusters) {
            HashSet<Usuari>  users = c.getUsuaris();
            Iterator<Usuari> it = users.iterator();
            while (it.hasNext()) {
                double d = distance.calculateEuclideanDistance(it.next().getRatedItems(), c.getCentroid().getCentroid());
                if (d > maxDistance) maxDistance = d;
            }
        }
        return maxDistance;
    }

    public static ArrayList<Cluster> calcula_k(List<Usuari> users) {
        long elapsedTimeApplyCentroids = 0l;
        ArrayList<ArrayList<Cluster>> clustersDef = new ArrayList<>();
        List<Double> maxDistances1 = new ArrayList<>();
        double mindis = Double.MAX_VALUE;
        int k_definitiva = 0;
        int k_alternativa = 0;
        boolean acabat = false;
        for (int k1 = 3; k1 <= 12; ++k1) {
            long startTimeApplyCentroids = System.nanoTime();
            ArrayList<Cluster> clusters12 = new Collaborative().recommendations(users,k1,new DistanceUsers(),100);
            System.out.println("ESTE SI " + total_time);
            long stopTimeApplyCentroids = System.nanoTime();
            elapsedTimeApplyCentroids += stopTimeApplyCentroids - startTimeApplyCentroids;
            clustersDef.add(clusters12);
            double mdistance1 = maxDistanceUserCentroid(clusters12, new DistanceUsers());
            maxDistances1.add(mdistance1);
            if (mdistance1 < mindis) {
                mindis = mdistance1;
                k_alternativa = k1;
            }
            for (int i = 0; i < maxDistances1.size(); ++i) {
                if (i != maxDistances1.size() - 1) {
                    if (maxDistances1.get(i) >= maxDistances1.get(i+1)) {
                        if (maxDistances1.get(i) - maxDistances1.get(i+1) < 0.5) {
                            k_definitiva = i + 3;
                            acabat = true;
                            break;
                        }
                    }
                    else {
                        if (maxDistances1.get(i+1) - maxDistances1.get(i) > 0.25) {
                            k_definitiva = i + 3;
                            acabat = true;
                            break;
                        }
                    }
                }
            }
            if (acabat) break;
        }
        System.out.println("AAAAAAAAAAAAAA " + elapsedTimeApplyCentroids);
        if (k_definitiva != 0) return clustersDef.get(k_definitiva-3);
        else {
            ArrayList<Cluster> aux;
            k_definitiva = k_alternativa;
            aux = new Collaborative().recommendations(users,k_definitiva,new DistanceUsers(),100);
            return aux;
        }
    }
    public static ArrayList<Usuari> minDstanceUsers(List<Usuari> Usuaris, DistanceUsers distance) {
        Double minDistance = Double.MAX_VALUE;
        ArrayList<Usuari> min = new ArrayList<>();
        for (int i = 0; i < Usuaris.size(); ++i) {
            if (i != Usuaris.size() -1) {
                double d = distance.calculateEuclideanDistance(Usuaris.get(i).getRatedItems(), Usuaris.get(i+1).getRatedItems());
                if (d < minDistance && d != -1) {
                    minDistance = d;
                    min.clear();
                    min.add(Usuaris.get(i));
                    min.add(Usuaris.get(i+1));
                }
            }
        }
        return min;
    }

    public static Usuari minDstanceUserSet(List<Usuari> Usuaris, ArrayList<Usuari> mk, DistanceUsers distance) {
        Double minDistance = Double.MAX_VALUE;
        Usuari u = Usuaris.get(0);
        for (int i = 0; i < Usuaris.size(); ++i) {
            double d1 = distance.calculateEuclideanDistance(Usuaris.get(i).getRatedItems(), mk.get(0).getRatedItems());
            double d2 = distance.calculateEuclideanDistance(Usuaris.get(i).getRatedItems(), mk.get(1).getRatedItems());
            if (d1 < d2 && d1 < minDistance && d1 != -1) {
                minDistance = d1;
                u = Usuaris.get(i);
            }
            else if (d2 < d1 && d2 < minDistance && d2 != -1) {
                minDistance = d2;
                u = Usuaris.get(i);
            }

        }
        return u;
    }


}
