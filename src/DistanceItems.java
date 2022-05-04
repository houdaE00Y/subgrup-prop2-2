/**
 @file DistanceItems.java
 @brief Codigo de la clase DistanceItems

 */
package dominio.clases;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * @class DistanceItems
 * @brief Clase que calcula la distancia entre un par de items. Para ello se calcula la distancia entre cada par de atributos
 *    de los items en funcion del tipo que sean, y luego se usa la distancia euclidiana para obtener la distancia total.
 *    Se hace una ponderacion de forma que se da mas peso a los atributos tipo categorico, luego los de tipo int,
 *    double y date por igual y finalmente los de tipo boolean, al hacer el computo de la distancia.
 * @author Jordi Elgueta Serra
 */
public class DistanceItems {

    /**
     * @brief Primer item en el computo de la distancia
     */
    private final Item a;
    /**
     * @brief Segundo item en el computo de la distancia
     */
    private final Item b;
    /**
     * @brief ArrayList con los valores maximos que tienen los atributos de tipo integer, en el mismo orden que aparecen a las
     * columnas del dataset
     */
    private final ArrayList<Long> MaxValIntegers;
    /**
     * @brief ArrayList con los valores minimos que tienen los atributos de tipo integer, en el mismo orden que aparecen a las
     * columnas del dataset
     */
    private final ArrayList<Long> MinValIntegers;
    /**
     * @brief ArrayList con los valores maximos que tienen los atributos de tipo double, en el mismo orden que aparecen a las
     * columnas del dataset
     */
    private final ArrayList<Double> MaxValDoubles;
    /**
     * @brief ArrayList con los valores minimos que tienen los atributos de tipo double, en el mismo orden que aparecen a las
     * columnas del dataset
     */
    private final ArrayList<Double> MinValDoubles;
    /**
     * @brief ArrayList con los valores maximos que tienen los atributos de tipo date, en el mismo orden que aparecen a las
     * columnas del dataset
     */
    private final ArrayList<LocalDate> MaxDates;
    /**
     * @brief ArrayList con los valores minimos que tienen los atributos de tipo date, en el mismo orden que aparecen a las
     * columnas del dataset
     */
    private final ArrayList<LocalDate> MinDates;

    /**
     * @brief Constructora de la clase DistanceItems
     * @param a Primer item en el computo de la distancia
     * @param b Segundo item en el computo de la distancia
     * @param MaxValIntegers Lista con los valores maximos que tienen los atributos de tipo integer, en el mismo orden que aparecen
     *      *                      a las columnas del dataset
     * @param MinValIntegers Lista con los valores minimos que tienen los atributos de tipo integer, en el mismo orden que aparecen
     *      *                      a las columnas del dataset
     * @param MaxValDoubles Lista con los valores minimos que tienen los atributos de tipo double, en el mismo orden que aparecen
     *      *                      a las columnas del dataset
     * @param MinValDoubles Lista con los valores minimos que tienen los atributos de tipo double, en el mismo orden que aparecen
     *                      a las columnas del dataset
     * @param MaxValDate Lista con los valores maximos que tienen los atributos de tipo date, en el mismo orden que aparecen
     *      *            a las columnas del dataset
     * @param MinValDate Lista con los valores minimos que tienen los atributos de tipo date, en el mismo orden que aparecen
     *      *            a las columnas del dataset
     */
    public DistanceItems(Item a, Item b, Collection MaxValIntegers, Collection MinValIntegers, Collection MaxValDoubles,
                         Collection MinValDoubles, Collection MaxValDate, Collection MinValDate) {
        this.a = a;
        this.b = b;
        this.MaxValIntegers = new ArrayList<>(MaxValIntegers);
        this.MinValIntegers = new ArrayList<>(MinValIntegers);
        this.MaxValDoubles = new ArrayList<>(MaxValDoubles);
        this.MinValDoubles = new ArrayList<>(MinValDoubles);
        this.MaxDates = new ArrayList<>(MaxValDate);
        this.MinDates = new ArrayList<>(MinValDate);
    }

    /**
     * @brief Metodo que devuelve la distancia entre los items a y b. La distancia siempre  sera un numero entre 0 y 1
     * donde 0 significa que son ítems exacatamente iguales y 1 significa que son completamente opuestos.
     * @return Devuelve el valor de la distancia, un double entre 0 y 1
     */
    public double get_distance() {
        double distInt = distBetweenInt();
        double distBool = distBetweenBool();
        double distDate = distBetweenDate();
        double distDouble = distBetweenDouble();
        //dist += distBetweenString();
        double distBS = distBetweenCategory();
        return 0.2*distInt + 0.2*distDouble + 0.2*distDate + 0.1*distBool + 0.3*distBS;
    }

    /**
     * @brief Metodo que calcula la similitud entre dos atributos de tipo categorico. Entre cada par de atributos categoricos la similitud se obtiene haciendo el cardinal
     * de la interseccion entre el cardinal de la union. Para la similitud global entre los atributos categoricos de los dos items se calcula la distancia euclidiana
     * normalizada para que el resultado sea entre 0 y 1.
     * @return Devuelve la distancia entre los dos items teniendo solo en cuenta los atributos categoricos
     */
    private double distBetweenCategory() {
        ArrayList<BitSet> c1 = a.getCategoryPscBits();
        ArrayList<BitSet> c2 = b.getCategoryPscBits();
        int n = c1.size();
        if (n==0) return 0.0;
        double dist = 0.0;
        for (int i = 0; i < n; ++i) {
            BitSet unio = (BitSet) c1.get(i).clone();
            BitSet interseccio = (BitSet) c1.get(i).clone();
            unio.or(c2.get(i));
            interseccio.and(c2.get(i));
            if (unio.cardinality() != 0)
                dist += Math.pow(1 - (double) interseccio.cardinality() / unio.cardinality(), 2);
        }
        return Math.sqrt(dist/n);
    }

    /**
     * @brief Metodo que calcula la similitud entre dos atributos de tipo date. Entre cada par de datas la similitud se obtiene haciendo la diferencia
     * entre los años dividido por la máxima diferencia que puede haber. Para la similitud global entre las datas de los dos items se calcula
     * la distancia euclidiana normalizada para que el resultado sea entre 0 y 1.
     * @return Devuelve la distancia entre los dos items teniendo solo en cuenta los atributos de tipo date
     */
    private double distBetweenDate() {
        ArrayList<LocalDate> ld1 = a.getDateAttributes();
        ArrayList<LocalDate> ld2 = b.getDateAttributes();
        int n = ld1.size();
        if (n == 0) return 0.0;
        double dist = 0.0;
        for (int i = 0; i < n; ++i) {
            int maxDif = MaxDates.get(i).getYear() - MinDates.get(i).getYear();
            if (maxDif != 0)
                dist += Math.pow((double) (ld1.get(i).getYear() - ld2.get(i).getYear()) / maxDif, 2);
        }
        return Math.sqrt(dist/n);
    }

    /**
     * @brief Metodo que calcula la similitud entre dos atributos de tipo boolean. Entre cada par de bools la similitud puede ser 1 si son diferentes
     * o 0 si son iguales. Para la similitud global entre los bools de los dos items se calcula
     * la distancia euclidiana normalizada para que el resultado sea entre 0 y 1.
     * @return Devuelve la distancia entre los dos items teniendo solo en cuenta los atributos de tipo boolean
     */
    private double distBetweenBool() {
        ArrayList<String> b1 = a.getBooleanAttributes();
        ArrayList<String> b2 = b.getBooleanAttributes();

        int n = b1.size();
        if (n==0) return 0.0;
        double dist = 0.0;
        for (int i = 0; i < n; ++i)
            dist += (b1.get(i).equals(b2.get(i))) ? 0 : 1;

        return Math.sqrt(dist/n);
    }

    /**
     * @brief Metodo que calcula la similitud entre dos atributos de tipo double. Entre cada par de doubles la similitud se obtiene haciendo la diferencia
     * dividido por la maxima diferencia que puede haber. Para la similitud global entre los doubles de los dos items se calcula la distancia euclidiana
     * normalizada para que el resultado sea entre 0 y 1.
     * @return Devuelve la distancia entre los dos items teniendo solo en cuenta los atributos de tipo double.
     */
    private double distBetweenDouble() {
        ArrayList<Double> d1 = a.getDoubleAttributes();
        ArrayList<Double> d2 = b.getDoubleAttributes();
        int n = d1.size();
        if (n==0) return 0.0;
        double dist = 0.0;
        for (int i = 0; i < n; ++i) {
            double maxDif = MaxValDoubles.get(i) - MinValDoubles.get(i);
            if (maxDif != 0)
                dist += Math.pow((d1.get(i) - d2.get(i))/maxDif,2);
        }
        return Math.sqrt(dist/n);
    }

    /**
     * @brief Metodo que calcula la similitud entre dos atributos de tipo integer. Entre cada par de integers la similitud se obtiene haciendo la diferencia
     * dividido por la maxima diferencia que puede haber. Para la similitud global entre los integers de los dos items se calcula la distancia euclidiana
     * normalizada para que el resultado sea entre 0 y 1.
     * @return Devuelve la distancia entre los dos items teniendo solo en cuenta los atributos de tipo double.
     */
    private double distBetweenInt() {

        ArrayList<Long> i1 = a.getIntAttributes();
        ArrayList<Long> i2 = b.getIntAttributes();
        int n = i1.size();
        if (n==0) return 0.0;
        double dist = 0.0;
        for (int i = 0; i < n; ++i) {
            Long maxDif = MaxValIntegers.get(i) - MinValIntegers.get(i);
            if (maxDif != 0)
                dist += Math.pow((double) (i1.get(i) - i2.get(i)) / maxDif, 2);
        }
        return Math.sqrt(dist/n);
    }

    private double distBetweenString() {
        LinkedHashMap<Integer, HashMap<String,Integer>> s1 = a.getWordFrequencies();
        LinkedHashMap<Integer, HashMap<String,Integer>> s2 = b.getWordFrequencies();
        LinkedHashMap<String, Integer> difWords = new LinkedHashMap<>();
        int n = s1.size();
        if (n == 0) return 0.0;
        double dist = 0.0;
        for (int i : s1.keySet()) {
            for (String s : s1.get(i).keySet())
                difWords.put(s, 0);
            for (String s : s2.get(i).keySet())
                difWords.put(s, 0);
            ArrayList<Integer> v1 = new ArrayList<>();
            ArrayList<Integer> v2 = new ArrayList<>();
            for (String w : difWords.keySet()) {
                if (s1.get(i).containsKey(w)) {
                    v1.add(s1.get(i).get(w));
                }
                if (s2.get(i).containsKey(w)) {
                    v2.add(s2.get(i).get(w));
                }
            }
            double sumProduct = 0;
            double sum1Sq = 0;
            double sum2Sq = 0;
            for (int j = 0; j < v1.size(); j++) {
                sumProduct += v1.get(j) * v2.get(j);
                sum1Sq += v1.get(i) * v1.get(i);
                sum2Sq += v2.get(i) * v2.get(i);
            }
            if (sum1Sq != 0 || sum2Sq != 0) {
                dist += Math.pow(sumProduct / (Math.sqrt(sum1Sq) * Math.sqrt(sum2Sq)), 2);
            } else dist += 1;
        }
        return Math.sqrt(dist / n);
    }
}

