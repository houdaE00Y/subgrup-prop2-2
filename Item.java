/**
 @file Item.java
 @brief Codigo de la clase Item

 */
package dominio.clases;

import java.util.ArrayList;
import java.util.BitSet; //BitSet
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @class Item
 * @brief Representa un item y su conjunto de atributos
 * @author Houda El Fezzak Bekkouri
 */
public class Item {

    /** representa el identifiador unico del item en el contexto del conjunto de items al que pertenece */
    private String id_item;

    /** contiene el conjunto de atributos enteros del item */
    private ArrayList<Long> IntAtt;

    /** contiene el conjunto de atributos reales del item */
    private ArrayList<Double> DoubleAtt;

    /** contiene el conjunto de atributos de tipo string del item */
    private ArrayList<String> StringAtt;

    /** contiene el conjunto de atributos categoricos del item */
    private ArrayList<BitSet> CategoricalAtt_Psc_bits;

    private HashMap<Integer, ArrayList<String>> CatAttr;

    /** contiene el conjunto de atributos fecha del item */
    private ArrayList<LocalDate> DateAtt;

    /** contiene el conjunto de atributos booleanos del item */
    private ArrayList<String> BooleanAtt;

    /** */
    private LinkedHashMap<Integer, HashMap<String,Integer>> wordFrequencies;

    /**
     * Constructora vacia para la clase Item. Inicializa una nueva instancia.
     */
    public Item() {

        this.id_item = "";
        this.IntAtt = new ArrayList<>();
        this.DoubleAtt = new ArrayList<>();
        this.StringAtt = new ArrayList<>();
        this.CategoricalAtt_Psc_bits = new ArrayList<>();
        this.CatAttr = new HashMap<>();
        this.DateAtt = new ArrayList<>();
        this.BooleanAtt = new ArrayList<>();
        this.wordFrequencies = new LinkedHashMap<>();
    }

    /**
     * Constructora parametrizada que asocia a una instancia de Item el identificador id e inicializa sus atributos
     *
     * @param id indica el identificador que se asociara a la instancia de item creada
     */
    public Item(String id) {

        this.id_item = id;
        this.IntAtt = new ArrayList<>();
        this.DoubleAtt = new ArrayList<>();
        this.StringAtt = new ArrayList<>();
        this.CategoricalAtt_Psc_bits = new ArrayList<>();
        this.DateAtt = new ArrayList<>();
        this.BooleanAtt = new ArrayList<>();
        this.wordFrequencies = new LinkedHashMap<>();
    }

    /**
     * Asigna a una instancia de item el identificador id
     *
     * @param id indica el identificador que se asignara al item
     */
    public void setId(String id) {
        this.id_item = id;
    }

    /**
     * Obtiene el identificador asociado al item
     *
     * @return el identificador del item
     */
    public String getId() {return this.id_item; }

    /**
     * Asigna a una instancia de item los n atributos no nulos dados en los parametros
     * @param attrI Lista de parametros de tipo Integer a asignar al item
     * @param attrD Lista de parametros de tipo Double a asignar al item
     * @param attrStr Lista de parametros de tipo String a asignar al item
     * @param attrCatBits Lista de parametros de tipo BitSet a asignar al item
     * @param attrDate Lista de parametros de tipo LocalDate a asignar al item
     * @param attrBool Lista de parametros de tipo attrBool a asignar al item
     */
    public void setAttributes(ArrayList<Long> attrI, ArrayList<Double> attrD, ArrayList<String> attrStr, ArrayList<BitSet> attrCatBits,
                              ArrayList<LocalDate> attrDate, ArrayList<String> attrBool, HashMap<Integer, ArrayList<String>> CatAttr){

        if (!(attrI==null)) this.IntAtt = attrI;
        if (!(attrD==null)) this.DoubleAtt = attrD;
        if (!(attrStr==null)) this.StringAtt = attrStr;
        if (!(attrCatBits==null)) this.CategoricalAtt_Psc_bits = attrCatBits;
        if (!(attrDate==null)) this.DateAtt = attrDate;
        if (!(attrBool==null)) this.BooleanAtt = attrBool;
        if (!(CatAttr==null)) this.CatAttr = CatAttr;
    }

    public void setWordFrequencies(LinkedHashMap<Integer,HashMap<String,Integer>> wordFrequencies){
        this.wordFrequencies = wordFrequencies;
    }

    public LinkedHashMap<Integer, HashMap<String,Integer>> getWordFrequencies(){
        return this.wordFrequencies;
    }

    /**
     * Metodo que devuelve el conjunto de atributos enteros del item
     *
     * @return el conjunto de atributos enteros del item
     */
    public ArrayList<Long> getIntAttributes(){
        return this.IntAtt;
    }

    /**
     * Metodo que devuelve el conjunto de atributos reales del item
     *
     * @return el conjunto de atributos reales del item
     */
    public ArrayList<Double> getDoubleAttributes(){
        return this.DoubleAtt;
    }

    /**
     * Metodo que devuelve el conjunto de atributos de tipo string del item
     *
     * @return el conjunto de atributos de tipo string del item
     */
    public ArrayList<String> getStringAttributes(){
        return this.StringAtt;
    }

    /**
     * Metodo que devuelve el conjunto de atributos categoricos del item
     *
     * @return el conjunto de atributos categoricos del item
     */
    public ArrayList<BitSet> getCategoryPscBits(){
        return this.CategoricalAtt_Psc_bits;
    }

    /**
     * Metodo que devuelve el conjunto de atributos categoricos del item
     *
     * @return el conjunto de atributos categoricos del item
     */
    public HashMap<Integer, ArrayList<String>> getCategoricalAttributes(){
        return this.CatAttr;
    }

    /**
     * Metodo que devuelve el conjunto de atributos fecha del item
     *
     * @return el conjunto de atributos fecha del item
     */
    public ArrayList<LocalDate> getDateAttributes(){
        return this.DateAtt;
    }

    /**
     * Metodo que devuelve el conjunto de atributos booleanos del item
     *
     * @return el conjunto de atributos booleanos del item
     */
    public ArrayList<String> getBooleanAttributes(){
        return this.BooleanAtt;
    }

    /**
     * Metodo que devuelve la totalidad de atributos del item en un contenedor de objetos genericos
     *
     * @return el conjunto de atributos del item
     */
    public ArrayList<Object> getItemAttributes(){
        ArrayList<Object> Attributes = new ArrayList<Object>();
        Attributes.addAll(this.IntAtt);
        Attributes.addAll(this.DoubleAtt);
        Attributes.addAll(this.StringAtt);
        for (int i = 0; i < this.CategoricalAtt_Psc_bits.size(); ++i){

            Attributes.add(this.CategoricalAtt_Psc_bits.get(i));
        }

        Attributes.addAll(this.DateAtt);
        Attributes.addAll(this.BooleanAtt);

        return Attributes;
    }

    /** override del metodo equals para dos instancias de Item */
    @Override
    public boolean equals(Object o) {
        if(o==null || !(o instanceof Item)) return false;
        Item i = (Item)o;
        return this.id_item.equals(i.getId());
    }

    /** override necesario para el uso de items en contenedores como HashMap o HashSet,
     *  definimos el atributo de item: id_item -que identifica al item en el conjunto de items al que pertenece- al que se aplicara el hashCode()
     * */
    @Override
    public int hashCode(){
        return id_item.hashCode();
    }
}
