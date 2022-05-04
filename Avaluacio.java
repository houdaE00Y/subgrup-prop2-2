/**
 * @file Avaluacio.java
 * @brief Codigo de la clase Avaluacio
 */
package dominio.clases;

import java.util.*;

/**
 * @author Cristian Sanchez Estape
 * @class Avaluacio
 * @brief Clase dedicada a la evaluacion de recomendaciones mediante el calculo del DCG de un conjunto de valoraciones para un conjunto de usuarios
 */

public class Avaluacio {

    /**
     * @brief Metodo responsable de la aplicacion de la formula del DCG propiamente dicho
     * @param LR Usuario con las valoraciones ideales que deberia realizar
     * @param LT Usuario con las valoraciones predecidas/realizadas
     */

    public static String DCG(Usuari LR, Usuari LT) {
        HashMap<Item, Double> copyLR = LR.getRatedItems();
        HashMap<Item, Double> copyLT = LT.getRatedItems();
        HashMap<Item, Double> valueHolder = new HashMap<>(copyLR);
        double DCG, iDCG;
        iDCG = computeIdealDCG(sortByValue(valueHolder));
        int i;
        i = 1;
        DCG = 0.;
        for (Item item : copyLR.keySet()) {
            if (copyLT.get(item) != null) {
                DCG += (Math.pow(2, copyLR.get(item)) - 1) / log(i + 1, 2);
            } else DCG += (Math.pow(2, 0) - 1) / log(i + 1, 2);
            i++;
        }
        return "" + DCG/iDCG;
    }

    /**
     * @brief Metodo que computa el DCG ideal
     * @param LR Mapa de items de los cuales hay que sacar el valor del DCG ideal
     * @return El valor correspondiente al DCG ideal
     */

    private static double computeIdealDCG(HashMap<Item, Double> LR) {
        int i = 1;
        double iDCG = 0.;
        for (Item item : LR.keySet()) {
            iDCG += (Math.pow(2, LR.get(item)) - 1) / log(i + 1, 2);
            i++;
        }
        return iDCG;
    }

    /**
     * @brief Metodo dedicado a la reordenacion de las valoraciones segun su valor (paso necesario para la aplicacion del DCG)
     * @param data Conjunto de items valorados por un usuario
     * @return Conjunto de items valorados por un usuario y ordenados segun la valoracion (numerica) de los items
     */

    public static HashMap<Item, Double> sortByValue(HashMap<Item, Double> data) {
        List<Map.Entry<Item, Double>> list = new LinkedList<>(data.entrySet());
        Collections.sort(list, (o1, o2) -> {
            int cmp = o2.getValue().compareTo(o1.getValue());
            if (cmp == 0) return o2.getKey().getId().compareTo(o1.getKey().getId());
            return cmp;
        });
        HashMap<Item, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Item, Double> entry : list) sortedMap.put(entry.getKey(), entry.getValue());
        return sortedMap;
    }

    /**
     * @brief Metodo que realiza el calculo del logaritmo de un valor en base b, esto es: log_b(x)
     * @param x Indice del item tratado
     * @param b Base del logaritmo
     * @return Logaritmo en base b de x
     */

    private static double log(int x, int b) {
        return (Math.log(x) / Math.log(b));
    }

}
