/**
 @file Cjt_items.java
 @brief Codigo de la clase Cjt_items

 */
package dominio.clases;

import dominio.controladores.ControladorDominio;
import persistencia.ControladorPersistencia;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.time.LocalDate;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.String;
import java.time.format.DateTimeFormatter;
import java.lang.Boolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @class Cjt_items
 * @brief Clase dedicada al preprocesado de un dataset de items con numero y tipo de atributos uniforme y la posterior asignacion del conjunto de items resultante a la instancia de Cjt_items
 * @author Houda El Fezzak Bekkouri
 */

public class Cjt_items {

    /**
     * @brief Contiene el conjunto de items asociado a la instancia de la clase
     * */
    private HashMap<String,Item> Items;

    /**
     * @brief Contiene la lista de cabeceras que indexa los atributos de los items del dataset
     * */
    private ArrayList<String> headers;

    /**
     * @brief contiene el conjunto de valores maximos para cada atributo entero del conjunto
     * */
    private LinkedHashMap<Integer, Long> MaxValIntegers;

    /**
     * @brief contiene el conjunto de valores minimos para cada atributo entero del conjunto
     * */
    private LinkedHashMap<Integer, Long> MinValIntegers;

    /**
     * @brief contiene el conjunto de valores maximos para cada atributo real del conjunto
     * */
    private LinkedHashMap<Integer, Double> MaxValDoubles;

    /**
     * @brief contiene el conjunto de valores minimos para cada atributo real del conjunto
     * */
    private LinkedHashMap<Integer, Double> MinValDoubles;

    /**
     * @brief contiene el conjunto de valores maximos para cada atributo fecha del conjunto
     * */
    private LinkedHashMap<Integer, LocalDate> MaxDates;

    /**
     * @brief contiene el conjunto de valores minimos para cada atributo fecha del conjunto
     * */
    private LinkedHashMap<Integer, LocalDate> MinDates;

    /**
     * @brief contiene, para el indice general de los atributos enteros, el indice que indexa el atributo en el subconjunto de su tipo de dato correspondiente
     * */
    private HashMap<Integer,Integer> IntAttrIndexes = new HashMap<>();

    /**
     * @brief contiene, para el indice general de los atributos double, el indice que indexa el atributo en el subconjunto de su tipo de dato correspondiente
     * */
    private HashMap<Integer,Integer> DoubleAttrIndexes = new HashMap<>();

    /**
     * @brief contiene, para el indice general de los atributos fecha, el indice que indexa el atributo en el subconjunto de su tipo de dato correspondiente
     * */
    private HashMap<Integer,Integer> DateAttrIndexes = new HashMap<>();

    /**
     * @brief contiene, para el indice general de los atributos booleanos, el indice que indexa el atributo en el subconjunto de su tipo de dato correspondiente
     * */
    private HashMap<Integer,Integer> BooleanAttrIndexes = new HashMap<>();

    /**
     * @brief contiene, para el indice general de los atributos categoricos, el indice que indexa el atributo en el subconjunto de su tipo de dato correspondiente
     * */
    private HashMap<Integer,Integer> CategoricalAttrIndexes = new HashMap<>();

    /**
     * @brief contiene, para el indice general de los atributos freetext, el indice que indexa el atributo en el subconjunto de su tipo de dato correspondiente
     * */
    private HashMap<Integer,Integer> StringAttrIndexes = new HashMap<>();

    /**
     * @brief contiene, para el indice general de los atributos other, el indice que indexa el atributo en el subconjunto de su tipo de dato correspondiente
     * */
    private HashMap<Integer, Integer> OtherAttrIndexes = new HashMap<>();

    /**
     * @brief asocia un indice para cada cabecera del dataset tal que sigue el orden en que aparecen los atributos
     * */
    private HashMap<String, Integer> HeaderIndexes = new LinkedHashMap<>();

    /**
     * @brief indica la lengua utilizada en el dataset
     * */
    private String language;

    private int itemId_index;

    /**
     * @brief Constructora vacia para la clase Cjt_items. Inicializa una nueva instancia.
     */
    public Cjt_items() {

        this.Items = new HashMap<>();
        this.headers = new ArrayList<>();
        this.MaxValIntegers = new LinkedHashMap<>();
        this.MinValIntegers = new LinkedHashMap<>();
        this.MaxValDoubles = new LinkedHashMap<>();
        this.MinValDoubles = new LinkedHashMap<>();
        this.MaxDates = new LinkedHashMap<>();
        this.MinDates = new LinkedHashMap<>();
    }

    /**
     * @brief Obtiene las cabeceras para el conjunto atributos del dataset
     */
    public ArrayList<String> getHeaders(){
        return this.headers;
    }

    /**
     * @brief Obtiene el tipo de dato de la cabecera attrHeader y
     * el índice con el que indexar el atributo dentro del conjunto de atributos de su mismo tipo
     * @param attrHeader Cabecera del atributo para el que se obtiene la información de indexación
     */
    public AttributeData getTypeIndex(String attrHeader) {
        //starting at 0
        AttributeData result = new AttributeData();
        int index = -1;
        String type = null;
        int header_index = this.HeaderIndexes.get(attrHeader);
        if (IntAttrIndexes.containsKey(header_index)){
            index = IntAttrIndexes.get(header_index);
            type = "integer";
        }
        else if (DoubleAttrIndexes.containsKey(header_index)){
            index = DoubleAttrIndexes.get(header_index);
            type = "float";
        }
        else if (DateAttrIndexes.containsKey(header_index)){
            index = DateAttrIndexes.get(header_index);
            type = "date";
        }
        else if (BooleanAttrIndexes.containsKey(header_index)){
            index = BooleanAttrIndexes.get(header_index);
            type = "boolean";
        }
        else if (CategoricalAttrIndexes.containsKey(header_index)){
            index = CategoricalAttrIndexes.get(header_index);
            type = "categorical";
        }
        else if (StringAttrIndexes.containsKey(header_index)){
            index = StringAttrIndexes.get(header_index);
            type = "freetext";
        }
        else if (OtherAttrIndexes.containsKey(header_index)){
            index = OtherAttrIndexes.get(header_index);
            type = "other";
        }

        result = new AttributeData(index,type);
        return result;
    }

    /**
     * @brief Metodo que devuelve los valores maximos de los atributos enteros del dataset
     *
     * @return Conjunto de valores maximos de los atributos enteros de los items del dataset, indexados por la columna en que aparecen
     */
    public LinkedHashMap<Integer, Long> getMaxValIntegers() {
        return MaxValIntegers;
    }

    /**
     * @brief Metodo que devuelve los valores minimos de los atributos enteros del dataset
     *
     * @return Conjunto de valores minimos de los atributos enteros de los items del dataset, indexados por la columna en que aparecen
     */
    public LinkedHashMap<Integer, Long> getMinValIntegers() {
        return MinValIntegers;
    }

    /**
     * @brief Metodo que devuelve los valores maximos de los atributos reales del dataset
     *
     * @return Conjunto de valores maximos de los atributos reales de los items del dataset, indexados por la columna en que aparecen
     */
    public LinkedHashMap<Integer, Double> getMaxValDoubles() {
        return MaxValDoubles;
    }

    /**
     * @brief Metodo que devuelve los valores minimos de los atributos reales del dataset
     *
     * @return Conjunto de valores minimos para de atributos reales de los items del dataset, indexados por la columna en que aparecen
     */
    public LinkedHashMap<Integer, Double> getMinValDoubles() {
        return MinValDoubles;
    }

    /**
     * @brief Metodo que devuelve los valores maximos de los atributos fecha del dataset
     *
     * @return Conjunto de valores maximos de los atributos fecha de los items del dataset, indexados por la columna en que aparecen
     */
    public LinkedHashMap<Integer, LocalDate> getMaxDates() {
        return MaxDates;
    }

    /**
     * @brief Metodo que devuelve los valores minimos de los atributos fecha del dataset
     *
     * @return Conjunto de valores minimos de los atributos fecha de los items del dataset, indexados por la columna en que aparecen
     */
    public LinkedHashMap<Integer, LocalDate> getMinDates() {
        return MinDates;
    }


    /**
     * @brief Metodo que asocia a la instancia de Cjt_items el conjunto de items dado en el parametro
     *
     * @param items conjunto de items a asignar
     */
    public void setItems(HashMap<String,Item> items) {
        this.Items = items;
    }


    /**
     * @brief Metodo que retorna el conjunto de items asociado a la instancia de la clase
     *
     * @return conjunto de items asociado a la instancia de Cjt_items
     */
    public HashMap<String,Item> getItems() {
        return this.Items;
    }


    /**
     * @brief Metodo que devuelve si el string column es un atributo en formato de fecha
     *
     * @param column atributo column en formato string para el que queremos inferir el tipo
     * @return true si column tiene formato de fecha y se corresponde con una fecha válida
     */
    private boolean isDate(String column) {
        try {
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("[yyyy/MM/dd][dd/MM/yyyy][yyyy-MM-dd][dd-MM-yyyy]");
            LocalDate date = LocalDate.parse(column, parser);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);
            LocalDate parsedDate = LocalDate.parse(formattedDate, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
        //return column.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})") || column.matches("([0-9]{4})/([0-9]{2})/([0-9]{2})");
    }

    /**
     * @brief Metodo que devuelve si el string column es un atributo entero
     *
     * @param column atributo column en formato string para el que queremos inferir el tipo
     * @return true si column es un valor entero
     */
    private boolean isInteger(String column) {
        if (column.contains("l")) return false;
        try {
            int count = column.length() - column.replaceAll("\\.", "").length();
            int countc = column.length() - column.replaceAll("\\,", "").length();

            if ((countc == 1 || count == 1) && (countc & count) == 0) {
                //test
                if (!column.matches("^\\d+$")) return false;
                else{
                    column = column.replaceAll("\\.", "");
                    column = column.replaceAll("\\,", "");
                }
            }
            if (countc > 1 || count > 1) {
                column = column.replaceAll("\\.", "");
                column = column.replaceAll("\\,", "");
            }
            Long.parseLong(column);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @brief Metodo que devuelve si el string column es un atributo real
     *
     * @param column atributo column en formato string para el que queremos inferir el tipo
     * @return true si column es un valor real
     */
    private boolean isDouble(String column) {
        if (column.contains("d")) return false;
        try {
            Double.parseDouble(column);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * @brief Metodo que convierte el string dado en un conjunto de strings determinado por el separador que contiene
     *
     * @param column string a convertir
     * @return la lista de strings obtenidos al separar el string
     */
    private ArrayList<String> parseToArray(String column) {
        ArrayList<String> attr = new ArrayList<String>();
        if (column.contains(",")) {
            attr = new ArrayList<>(Arrays.asList(column.split(",")));
        } else if (column.contains(";")) {
            attr = new ArrayList<>(Arrays.asList(column.split(";")));
        } else attr.add(column);

        return attr;
    }

    private static ArrayList<String> parseToArray2( String column ){
        if (column == null) return null;
        String[] readCol = Arrays.stream(column.split("[^\\w'\\.\\-\\_\\s\\(\\)\\{\\}\\[\\]]|\\s{2,}|\\-{2,}|\\.{2,}|\\_{2,}|(?<=[^\\w\\.])\\s|\\s(?=[^\\w\\.])")).filter(str -> !str.isEmpty()).toArray(String[]::new);
        ArrayList<String> attr = new ArrayList<>(Arrays.asList(readCol));

        int lastInd = attr.size()-1;
        if (lastInd > -1) attr.set(lastInd,attr.get(lastInd).replaceAll("\\s+$", ""));
        return attr;
    }

    /**
     * @brief Metodo que devuelve si el string column es un atributo booleano
     *
     * @param column atributo column en formato string para el que queremos inferir el tipo
     * @return true si column es un valor booleano
     */
    private boolean isBoolean(String column) {

        return "true".equals(column.toLowerCase()) || "false".equals(column.toLowerCase());
    }

    /**
     * @brief Metodo que imprime por la salida estandar de output un conjunto de items
     *
     * @param Items conjunto de items a imprimir
     */
    public static void printItems(HashMap<String,Item> Items) {
        //Entry can only be accessed from the Map interface:
        Item x = new Item();

        for (Map.Entry<String,Item> entry : Items.entrySet()) {
            Item i = entry.getValue();
            System.out.print("Item = " + i.getId() + " ");
            ArrayList<Object> AttrList = i.getItemAttributes();
            for (int j = 0; j < AttrList.size(); j++) {
                System.out.print(AttrList.get(j) + "   ");
            }
            System.out.print("\n");
        }
    }

    /**
     * @brief Metodo que actualiza la mediana de la columnas que se corresponden con tipos enteros
     *
     * @param mapL Inicialmente vacío - Guarda la mitad mas pequeña de los valores de la columna
     * @param mapH Inicialmente vacío - Guarda la mitad mas grande de los valores de la columna
     * @param index la columna para la que se ha modificado un valor
     * @param val el valor modificado en la columna
     */
    private void updateLongMedianMaps(HashMap<Integer, NavigableSet<Long> > mapL, HashMap<Integer, NavigableSet<Long> > mapH, Long val, int index){
        NavigableSet<Long> setL = mapL.get(index);
        NavigableSet<Long> setH = mapH.get(index);
        if (setL.size() == 0 && setH.size() == 0) setL.add(val);
        else if (val <= setL.descendingIterator().next()){
            if (setL.size() > setH.size() + 1){
                Long max = setL.pollLast();
                setH.add(max);
            }
            setL.add(val);
        }
        else{
            if (setH.size() > setL.size()){
                Long min = setH.pollFirst();
                setL.add(min);
            }
            setH.add(val);
        }
        mapL.put(index, setL);
        mapH.put(index, setH);
    }

    /**
     * @brief Metodo que actualiza la mediana de la columnas que se corresponden con tipos doubles
     *
     * @param mapL Inicialmente vacío - Guarda la mitad mas pequeña de los valores de la columna
     * @param mapH Inicialmente vacío - Guarda la mitad mas grande de los valores de la columna
     * @param index la columna para la que se ha modificado un valor
     * @param val el valor modificado en la columna
     */
    private void updateDoubleMedianMaps(HashMap<Integer, NavigableSet<Double> > mapL, HashMap<Integer, NavigableSet<Double> > mapH, Double val, int index){
        NavigableSet<Double> setL = mapL.get(index);
        NavigableSet<Double> setH = mapH.get(index);
        if (setL.size() == 0 && setH.size() == 0) setL.add(val);
        else if (val <= setL.descendingIterator().next()){
            if (setL.size() > setH.size() + 1){
                Double max = setL.pollLast();
                setH.add(max);
            }
            setL.add(val);
        }
        else{
            if (setH.size() > setL.size()){
                Double min = setH.pollFirst();
                setL.add(min);
            }
            setH.add(val);
        }
        mapL.put(index, setL);
        mapH.put(index, setH);
    }

    /**
     * @brief Metodo que actualiza la mediana de la columnas que se corresponden con tipos fecha
     *
     * @param mapL Inicialmente vacío - Guarda la mitad mas pequeña de los valores de la columna
     * @param mapH Inicialmente vacío - Guarda la mitad mas grande de los valores de la columna
     * @param index la columna para la que se ha modificado un valor
     * @param val el valor modificado en la columna
     */
    private void updateIntMedianMaps(HashMap<Integer, NavigableSet<Integer> > mapL, HashMap<Integer, NavigableSet<Integer> > mapH, Integer val, int index){
        NavigableSet<Integer> setL = mapL.get(index);
        NavigableSet<Integer> setH = mapH.get(index);
        if (setL.size() == 0 && setH.size() == 0) setL.add(val);
        else if (val <= setL.descendingIterator().next()){
            if (setL.size() > setH.size() + 1){
                Integer max = setL.pollLast();
                setH.add(max);
            }
            setL.add(val);
        }
        else{
            if (setH.size() > setL.size()){
                Integer min = setH.pollFirst();
                setL.add(min);
            }
            setH.add(val);
        }
        mapL.put(index, setL);
        mapH.put(index, setH);
    }

    /**
     * @brief Metodo que lee un dataset de items des del fichero con path filename y escribe en los parametros la mediana de los valores para los conjuntos de distintos tipos numericos
     * @param IntColMedian Estructura sobre la que escribiremos el resultado (relativo a los enteros), indexada por la columna para la cual se realiza el calculo
     * @param DoubleColMedian Estructura sobre la que escribiremos el resultado (relativo a los doubles), indexada por la columna para la cual se realiza el calculo
     * @param DateColMedian Estructura sobre la que escribiremos el resultado (relativo a las fechas), indexada por la columna para la cual se realiza el calculo
     * @param pathToDataset Representa el path hacia el archivo csv que contiene el dataset de items
     * @return El numero de items del conjunto
     */
    public int getColMedians(HashMap<Integer, Long> IntColMedian, HashMap<Integer, Double> DoubleColMedian, HashMap<Integer, Integer> DateColMedian, String pathToDataset, String[] dataType) throws IOException {
        ControladorPersistencia ctrlPers = ControladorPersistencia.getInstance();
        ArrayList<String> st = new ArrayList<>();
        try {
            ctrlPers.initializeCtrlItems(pathToDataset);
            ctrlPers.getAttributeHeaders();
        } catch (Exception e) {}
        int nAttributes = ctrlPers.getNumOfAttributes();

        int c = 0;
        HashMap<Integer, NavigableSet<Long> > IntColL = new HashMap<>();
        HashMap<Integer, NavigableSet<Long> > IntColH = new HashMap<>();
        HashMap<Integer, NavigableSet<Double> > DoubleColL = new HashMap<>();
        HashMap<Integer, NavigableSet<Double> > DoubleColH = new HashMap<>();
        HashMap<Integer, NavigableSet<Integer> > DateColL = new HashMap<>();
        HashMap<Integer, NavigableSet<Integer> > DateColH = new HashMap<>();
        try {
            while (ctrlPers.hasMoreItems()) {

                ++c;
                st = ctrlPers.getItemAttributes();

                for (int i = 0; i < nAttributes; i++) {

                    String attributeObject = st.get(i);

                    if (attributeObject != null) {
                        if (dataType[i].equalsIgnoreCase("integer")) {

                            IntColL.putIfAbsent(i, new TreeSet<Long>());
                            IntColH.putIfAbsent(i, new TreeSet<Long>());
                            long attr = -1;
                            if (isInteger(attributeObject)) {
                                attr = Long.parseLong(attributeObject);
                                updateLongMedianMaps(IntColL, IntColH,attr,i);
                                IntColMedian.put(i, IntColL.get(i).descendingIterator().next());
                            }

                        } else if (dataType[i].equalsIgnoreCase("float")) {

                            DoubleColL.putIfAbsent(i, new TreeSet<Double>());
                            DoubleColH.putIfAbsent(i, new TreeSet<Double>());
                            double attr = -1.0;
                            if (isDouble(attributeObject)) {
                                attr = Double.parseDouble(attributeObject);
                                updateDoubleMedianMaps(DoubleColL, DoubleColH,attr,i);
                                DoubleColMedian.put(i, DoubleColL.get(i).descendingIterator().next());
                            }
                        } else if (dataType[i].equalsIgnoreCase("date")) {

                            if (isDate(attributeObject)) {
                                DateTimeFormatter parser = DateTimeFormatter.ofPattern("[yyyy/MM/dd][dd/MM/yyyy][yyyy-MM-dd][dd-MM-yyyy]");
                                DateColL.putIfAbsent(i, new TreeSet<Integer>());
                                DateColH.putIfAbsent(i, new TreeSet<Integer>());
                                try {
                                    LocalDate date = LocalDate.parse(attributeObject, parser);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    String formattedDate = date.format(formatter);
                                    LocalDate parsedDate = LocalDate.parse(formattedDate, formatter);
                                    updateIntMedianMaps(DateColL, DateColH,parsedDate.getYear(),i);

                                } catch (Exception e) {
                                }
                                DateColMedian.put(i, DateColL.get(i).descendingIterator().next());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        ctrlPers.reset();
        return c;
    }

    /**
     * @brief Metodo que obtiene el item que tiene asociado el identificador id
     *
     * @param id El identificador del item que se desea obtener
     */
    public Item getItem(String id) throws Exception {
        if (!this.Items.containsKey(id))
            throw new Exception("El item con identificador " + id + " no existe!");
        return this.Items.get(id);
    }

    /**
     * @brief Constructora parametrizada que asocia a una instancia de Cjt_items el conjunto de items dado por el fichero con path=pathToDataset preprocesado
     *
     * @param pathToDataset indica el camino hasta el archivo en que se halla el dataset a leer
     * @param pathToAttrData_types indica el camino hasta el archivo en que se halla el tipo de dato para cada atributo del dataset
     */
    public Cjt_items(String pathToDataset, String pathToAttrData_types) {

        ControladorPersistencia CtrlPers = ControladorPersistencia.getInstance();
        try {
            CtrlPers.reset();
        } catch (IOException e) {
        }
        HashMap<Integer, Long> IntColMedian = new HashMap<Integer, Long>();
        HashMap<Integer, Double> DoubleColMedian = new HashMap<Integer, Double>();
        HashMap<Integer, Integer> DateColMedian = new HashMap<Integer, Integer>();

        /** instance of Item class */

        Item item = new Item();

        /** c identifica e indexa los items en el contexto de cada instancia de la clase */
        int c = 0;

        /** contiene para cada indice el conjunto union de instancias de cada variable categorica del conjunto */
        LinkedHashMap<Integer, ArrayList<String>> dsCategoricalVars = new LinkedHashMap<>();

        /** contiene para cada instancia de variable categorica un indice que la identifica para el posterior One-hot Encoding */
        HashMap<String, HashMap<Integer,Integer> >CategoricalVarsBit = new HashMap<>();

        /** contiene el numero de instancias o categorias diferentes
         * que toma la variable categorica asociada al atributo de la columna i en el conjunto
         */
        HashMap<Integer, Integer> CatVarIndexes = new HashMap<>();

        /** contiene para cada item y cada variable catogorica del conjunto, las ategorias asociadas */
        HashMap<String, LinkedHashMap<Integer, ArrayList<String>>> AllCategoricalAttributes = new HashMap<>();

        HashMap<Integer, Boolean> isCatAttNull = new HashMap<>();

        this.MaxValIntegers = new LinkedHashMap<>();
        this.MinValIntegers = new LinkedHashMap<>();
        this.MaxValDoubles = new LinkedHashMap<>();
        this.MinValDoubles = new LinkedHashMap<>();
        this.MaxDates = new LinkedHashMap<>();
        this.MinDates = new LinkedHashMap<>();
        this.HeaderIndexes = new HashMap<>();
        this.headers = new ArrayList<>();

        this.Items = new HashMap<>();
        boolean belongsTo = true;
        try {
            String[] readData = new String[]{};
            try {
                readData = CtrlPers.getDataTypes(pathToAttrData_types);
            } catch (Exception e){}
            int nItems = getColMedians(IntColMedian, DoubleColMedian, DateColMedian, pathToDataset, readData);
            int int_index, double_index, date_index, boolean_index, str_index, other_index, categorical_index;
            int_index = double_index = date_index = boolean_index = str_index = other_index = categorical_index =0;

            ArrayList<String> st = new ArrayList<>();
            try {
                CtrlPers.initializeCtrlItems(pathToDataset);
                st = new ArrayList<>(CtrlPers.getAttributeHeaders());
            } catch (Exception e){}

            int nAttributes = CtrlPers.getNumOfAttributes();

            if (st.size() > 0) {
                this.headers = st;

                for (int i = 0; i < st.size(); i++) {

                    String attribute = st.get(i);

                    String attL = attribute.toLowerCase();

                    if ((attL == "id") || (attL.contains("id"))) {
                        Pattern pattern_1 = Pattern.compile("^[a-z]{1,10}id[a-z]{1,10}$");
                        Pattern pattern_2 = Pattern.compile("^id[\\W\\S_a-zA-Z0-9]{1,10}$");
                        Pattern pattern_3 = Pattern.compile("^[\\W\\S_a-zA-Z0-9]{1,10}id$");
                        Matcher m1 = pattern_1.matcher(attL);
                        Matcher m2 = pattern_2.matcher(attL);
                        Matcher m3 = pattern_3.matcher(attL);
                        if ((!(m1.matches()) && !(m2.matches()) && !(m3.matches())) || attL.contains("item")) {
                            itemId_index = i;
                        }
                    }
                }
            }
            if (nAttributes != readData.length){
                //throw new Exception("The number of data types does not correspond with the number of attributes");
            }
            else{
                for (int k = 0; k < readData.length; ++k){
                    HeaderIndexes.put(this.headers.get(k),k);

                    if (k == itemId_index) StringAttrIndexes.put(k,str_index++);
                    else if (readData[k].equalsIgnoreCase("integer")) IntAttrIndexes.put(k, int_index++);
                    else if (readData[k].equalsIgnoreCase("float")) DoubleAttrIndexes.put(k, double_index++);
                    else if (readData[k].equalsIgnoreCase("date")) DateAttrIndexes.put(k, date_index++);
                    else if (readData[k].equalsIgnoreCase("boolean")) BooleanAttrIndexes.put(k, boolean_index++);
                    else if (readData[k].equalsIgnoreCase("categorical")) CategoricalAttrIndexes.put(k, k);
                    else if (readData[k].equalsIgnoreCase("freetext")) StringAttrIndexes.put(k, str_index++);
                    else if (readData[k].equalsIgnoreCase("other")) OtherAttrIndexes.put(k, other_index++);

                }
            }
            while (CtrlPers.hasMoreItems()) {

                ++c;

                ArrayList<Long> IntAttr = new ArrayList<>();
                ArrayList<Double> DoubleAttr = new ArrayList<>();
                ArrayList<String> StringAttr = new ArrayList<>();
                ArrayList<LocalDate> DateAttr = new ArrayList<>();
                ArrayList<String> BooleanAttr = new ArrayList<>();
                LinkedHashMap<Integer, HashMap<String,Integer> > wordFrequencies = new LinkedHashMap<>();

                try { st = CtrlPers.getItemAttributes(); }
                catch (Exception e){}

                String itemId = "";

                item = new Item();

                LinkedHashMap<Integer, ArrayList<String>> CategoricalAttr = new LinkedHashMap<Integer, ArrayList<String>>();

                /** itera sobre los campos de todos los atributos de un item del dataset */
                for (int i = 0; i < nAttributes; i++) {

                    String attributeObject = st.get(i);

                    if (i == itemId_index) {
                        itemId = attributeObject;
                        item.setId(itemId);
                        StringAttr.add(attributeObject);
                    } else if (attributeObject != null) {

                        if (readData[i].equalsIgnoreCase("integer")) {
                            long attr = 0;

                            if (isInteger(attributeObject)) {
                                if (attributeObject.contains(".") || attributeObject.contains(",")) {

                                    /** utilizamos el caracter de escape '\\' ya que el punto en las expresiones regulares es un caracter especial
                                     *  que crea una correspondencia para cualquier valor del caracter
                                     */
                                    attributeObject = attributeObject.replace("\\.", "");
                                    attributeObject = attributeObject.replace("\\,", "");
                                }
                                attr = Long.parseLong(attributeObject);

                                long median = IntColMedian.get(i);

                                if ((attr - (100 * median)) > median || (attr + (100 * median)) < median)
                                    attr = median;

                                if (c == 1 || (MaxValIntegers.size() == 0 && MinValIntegers.size() == 0)) {
                                    MaxValIntegers.put(i, attr);
                                    MinValIntegers.put(i, attr);
                                } else if (MaxValIntegers.containsKey(i) || attr > MaxValIntegers.get(i)) MaxValIntegers.put(i, attr);
                                else if (MinValIntegers.containsKey(i) || attr < MinValIntegers.get(i)) MinValIntegers.put(i, attr);
                                IntAttr.add(attr);
                            } else {
                                if (IntColMedian.get(i) != null){
                                    attr = IntColMedian.get(i);
                                    IntAttr.add(attr);
                                }
                            }
                        } else if (readData[i].equalsIgnoreCase("float")) {

                            double attr = 0.0;
                            if (isDouble(attributeObject)) {
                                attr = Double.parseDouble(attributeObject);
                                double median = DoubleColMedian.get(i);
                                if ((attr - 100 * median) > median || (attr + 100 * median) < median) attr = median;

                                if (c == 1) {
                                    MaxValDoubles.put(i, attr);
                                    MinValDoubles.put(i, attr);
                                } else if (!MaxValDoubles.containsKey(i) || attr > MaxValDoubles.get(i)) MaxValDoubles.put(i, attr);
                                else if (!MinValDoubles.containsKey(i) || attr < MinValDoubles.get(i)) MinValDoubles.put(i, attr);
                                DoubleAttr.add(attr);
                            } else {
                                if (DoubleColMedian.get(i) != null){
                                    attr = DoubleColMedian.get(i);
                                    DoubleAttr.add(attr);
                                }
                            }
                        } else if (readData[i].equalsIgnoreCase("boolean")) {
                            BooleanAttr.add(attributeObject); //check afterwards if it indeed is a boolean-- when eliminating absurd categorical attr
                        } else if (readData[i].equalsIgnoreCase("date")) {

                            LocalDate parsedDate = null;
                            if (isDate(attributeObject)) {
                                int median = DateColMedian.get(i);
                                DateTimeFormatter parser = DateTimeFormatter.ofPattern("[yyyy/MM/dd][dd/MM/yyyy][yyyy-MM-dd][dd-MM-yyyy]");

                                try {
                                    LocalDate date = LocalDate.parse(attributeObject, parser);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    String formattedDate = date.format(formatter);
                                    parsedDate = LocalDate.parse(formattedDate, formatter);

                                    if (c == 1) {
                                        MaxDates.put(i, parsedDate);
                                        MinDates.put(i, parsedDate);
                                        if ((parsedDate.getYear() - 0.8 * median) > median || (parsedDate.getYear() + 0.8 * median) < median) {
                                            parsedDate = LocalDate.of(median, parsedDate.getMonth(), parsedDate.getDayOfMonth());
                                        }
                                    } else {
                                        if ((parsedDate.getYear() - 0.8 * median) > median || (parsedDate.getYear() + 0.8 * median) < median) {
                                            parsedDate = LocalDate.of(median, MinDates.get(i).getMonth(), MinDates.get(i).getDayOfMonth());
                                        }
                                        if (parsedDate.isAfter(MaxDates.get(i))) MaxDates.put(i, parsedDate);
                                        else if (parsedDate.isBefore(MinDates.get(i))) MinDates.put(i, parsedDate);
                                    }
                                } catch (Exception e) {
                                }
                            } else {
                                if (DateColMedian.get(i) != null) {
                                    parsedDate = LocalDate.of(DateColMedian.get(i), MinDates.get(i).getMonth(), MinDates.get(i).getDayOfMonth());
                                    DateAttr.add(parsedDate);
                                }
                            }
                        } else if (readData[i].equalsIgnoreCase("categorical")) {
                            isCatAttNull.put(i, attributeObject.length() == 0);
                            HashSet<Integer> indexesCatVar = new HashSet<Integer>();

                            ArrayList<String> CategoricalVars = new ArrayList<String>();
                            CategoricalVars = parseToArray2(attributeObject);

                            ArrayList<String> ColVar = dsCategoricalVars.get(i);
                            if (dsCategoricalVars.containsKey(i)) ColVar.addAll(CategoricalVars);
                            else ColVar = CategoricalVars;
                            dsCategoricalVars.put(i, ColVar);

                            for (int k = 0; k < CategoricalVars.size(); ++k) {
                                if ((CategoricalVarsBit.get(CategoricalVars.get(k)) != null && !CategoricalVarsBit.get(CategoricalVars.get(k)).containsKey(i)) || !CategoricalVarsBit.containsKey(CategoricalVars.get(k))) {
                                    CatVarIndexes.merge(i, 1, Integer::sum);
                                }
                                if (!CategoricalVarsBit.containsKey(CategoricalVars.get(k)))
                                    CategoricalVarsBit.put(CategoricalVars.get(k), new HashMap<Integer, Integer>());
                                HashMap<Integer, Integer> colBits = CategoricalVarsBit.get(CategoricalVars.get(k));
                                colBits.putIfAbsent(i, CatVarIndexes.get(i));
                                CategoricalVarsBit.put(CategoricalVars.get(k), colBits);
                            }
                            CategoricalAttr.put(i, new ArrayList<String>(CategoricalVars));
                        } else if (readData[i].equalsIgnoreCase("freetext")){
                            ArrayList<String> sw = new ArrayList<>();
                            try {
                                if (language.equals("english")){
                                    sw = CtrlPers.getEnglishStepWords();
                                    belongsTo = true;
                                }
                                else if (language.equals("catalan")){
                                    sw = CtrlPers.getCatalanStepWords();
                                    belongsTo = true;
                                }
                                else if (language.equals("spanish")){
                                    sw = CtrlPers.getSpanishStepWords();
                                    belongsTo = true;
                                }
                                else if (language.equals("french")){
                                    sw = CtrlPers.getFrenchStepWords();
                                    belongsTo = true;
                                }
                                else if (language.equals("portuguese")){
                                    sw = CtrlPers.getPortugueseStepWords();
                                    belongsTo = true;
                                }
                                else if (language.equals("italian")){
                                    sw = CtrlPers.getItalianStepWords();
                                    belongsTo = true;
                                }
                                else if (language.equals("german")){
                                    sw = CtrlPers.getGermanStepWords();
                                    belongsTo = true;
                                }
                            } catch (Exception e){}
                            String mapped_words = attributeObject;
                            for (String s : sw){
                                mapped_words = mapped_words.replaceAll(".*\\b($"+s+")\\b.", "");
                            }
                            wordFrequencies.putIfAbsent(i, new HashMap<>());
                            HashMap<String,Integer> FreqMap = wordFrequencies.get(i);
                            String[] words = Arrays.stream(mapped_words.split("[^\\w\\d']")).filter(str -> !str.isEmpty()).toArray(String[]::new);
                            for (String s : words){
                                FreqMap.merge(s, 1, Integer::sum);
                            }
                            wordFrequencies.put(i,FreqMap);
                            StringAttr.add(attributeObject);
                        }
                    }
                }
                LinkedHashMap<Integer, ArrayList<String>> CatAttr = new LinkedHashMap<Integer, ArrayList<String>>(CategoricalAttr);

                /** asigna las categorias asociadas a cada variable categorica e item */
                AllCategoricalAttributes.put(itemId, CatAttr);

                item.setWordFrequencies(wordFrequencies);
                item.setAttributes(IntAttr, DoubleAttr, StringAttr, null, DateAttr, BooleanAttr, CatAttr);
                this.Items.put(itemId,item);
            }
            CtrlPers.reset();
        } catch (IOException e) {
            System.out.println(e);
        }

        for (Iterator<Map.Entry<Integer, ArrayList<String>>> i = dsCategoricalVars.entrySet().iterator(); i.hasNext(); ) {

            Map.Entry<Integer, ArrayList<String>> ds_entry = i.next();
            ArrayList<String> CatInstances = ds_entry.getValue();

            /** convertimos la ArrayList a Set tal que se mapen solo instancias unicas
            de categorias para cada columna asociada a un atributo categorico */
            Set<String> uniqueInstances = new HashSet<String>(CatInstances);

            double uniquevsTotal = (double) uniqueInstances.size() / CatInstances.size();

            /** si mas del 90% de las categorias son unicas con respecto a la union de todas las instancias,
             *  consideramos que se trata de un atributo categorico absurdo, descartamos los atributos que no tienen ningun valor no nulo
             */
            if ((isCatAttNull.get(ds_entry.getKey())) || uniquevsTotal > 0.90) {

                for (Map.Entry<String, LinkedHashMap<Integer, ArrayList<String>>> j : AllCategoricalAttributes.entrySet()) {
                    LinkedHashMap<Integer, ArrayList<String>> jthItem = j.getValue();

                    for (Iterator<Map.Entry<Integer, ArrayList<String>>> it = jthItem.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<Integer, ArrayList<String>> entry = it.next();
                        if (entry.getKey().equals(ds_entry.getKey())) {
                            //it.remove();
                            entry.setValue(null);
                        }
                    }
                    /** actualizamos el item en el conjunto,
                     * que ahora contiene un valor null en el atributo categorico "absurdo"
                     */
                    AllCategoricalAttributes.put(j.getKey(), jthItem);
                }
            }
        }

        for (Iterator<Map.Entry<String, LinkedHashMap<Integer, ArrayList<String>>> > it = AllCategoricalAttributes.entrySet().iterator(); it.hasNext(); ) {

            ArrayList<BitSet> instance_presenceBits = new ArrayList<BitSet>();
            BitSet psc_bits;
            Map.Entry<String, LinkedHashMap<Integer, ArrayList<String>>> item_entries = it.next();
            HashMap<Integer, ArrayList<String>> currentItem = item_entries.getValue();

            for (Iterator<Map.Entry<Integer, ArrayList<String>>> i = currentItem.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry<Integer, ArrayList<String>> entry = i.next();

                int sz = 0;

                if (CatVarIndexes.containsKey(entry.getKey())) sz = CatVarIndexes.get(entry.getKey());

                ArrayList<String> ithcategory = entry.getValue();
                psc_bits = new BitSet(sz);

                for (String instance : ithcategory) {
                    psc_bits.set(CategoricalVarsBit.get(instance).get(entry.getKey()));
                }
                instance_presenceBits.add(psc_bits);
            }
            /** obtenemos el item correspondiente en el conjunto y le asignamos el conjunto de One-Hot Encodings
             *  para cada uno de los atributos categoricos asociados
             */
            Item currItem = this.Items.get(item_entries.getKey());
            currItem.setAttributes(null, null, null, instance_presenceBits, null, null,null);

            this.Items.put(item_entries.getKey(), currItem);
        }
    }


    /**
     * Constructora parametrizada que asocia a una instancia de Cjt_items el conjunto de items dado por el fichero con path=pathToDataset preprocesado
     *
     * @param pathToDataset indica el camino hasta el archivo en que se halla el dataset a leer
     * @param pathToAttrData_types indica el camino hasta el archivo en que se halla el tipo de dato para cada atributo del dataset
     * @param CtrlDominio Instancia de ControladorDominio
     */
    public Cjt_items(String pathToDataset, String pathToAttrData_types, ControladorDominio CtrlDominio) {

        ControladorPersistencia CtrlPers = ControladorPersistencia.getInstance();
        try {
            CtrlPers.reset();
        } catch (IOException e) {
        }

        HashMap<Integer, Long> IntColMedian = new HashMap<Integer, Long>();
        HashMap<Integer, Double> DoubleColMedian = new HashMap<Integer, Double>();
        HashMap<Integer, Integer> DateColMedian = new HashMap<Integer, Integer>();

        Item item = new Item();

        /** c identifica e indexa los items en el contexto de cada instancia de la clase */
        int c = 0;

        /** contiene para cada indice el conjunto union de instancias de cada variable categorica del conjunto */
        LinkedHashMap<Integer, ArrayList<String> > dsCategoricalVars = new LinkedHashMap<>();

        /** contiene para cada instancia de variable categorica un indice que la identifica para el posterior One-hot Encoding */
        HashMap<String, HashMap<Integer,Integer> > CategoricalVarsBit = new HashMap<>();

        /** contiene el numero de instancias o categorias diferentes
         * que toma la variable categorica asociada al atributo de la columna i en el conjunto
         */
        HashMap<Integer, Integer> CatVarIndexes = new HashMap<>();

        /** contiene para cada item y cada variable catogorica del conjunto, las ategorias asociadas */
        HashMap<String, LinkedHashMap<Integer, ArrayList<String>>> AllCategoricalAttributes = new HashMap<>();

        HashMap<Integer, Boolean> isCatAttNull = new HashMap<>();

        this.MaxValIntegers = new LinkedHashMap<>();
        this.MinValIntegers = new LinkedHashMap<>();
        this.MaxValDoubles = new LinkedHashMap<>();
        this.MinValDoubles = new LinkedHashMap<>();
        this.MaxDates = new LinkedHashMap<>();
        this.MinDates = new LinkedHashMap<>();

        this.Items = new HashMap<>();

        try {
            String[] readData = new String[]{};
            try {
                readData = CtrlPers.getDataTypes(pathToAttrData_types);
            } catch (Exception e){}
            int nItems = getColMedians(IntColMedian, DoubleColMedian, DateColMedian, pathToDataset, readData);
            int int_index, double_index, date_index, boolean_index, str_index, other_index, categorical_index;
            int_index = double_index = date_index = boolean_index = str_index = other_index = categorical_index =0;

            ArrayList<String> st = new ArrayList<>();
            try {
                CtrlPers.initializeCtrlItems(pathToDataset);
                st = CtrlPers.getAttributeHeaders();
            } catch (Exception e){}

            int nAttributes = CtrlPers.getNumOfAttributes();

            if (st.size() > 0) {
                this.headers = st;

                for (int i = 0; i < st.size(); i++) {

                    String attribute = st.get(i);

                    String attL = attribute.toLowerCase();

                    if ((attL == "id") || (attL.contains("id"))) {
                        Pattern pattern_1 = Pattern.compile("^[a-z]{1,10}id[a-z]{1,10}$");
                        Pattern pattern_2 = Pattern.compile("^id[\\W\\S_a-zA-Z0-9]{1,10}$");
                        Pattern pattern_3 = Pattern.compile("^[\\W\\S_a-zA-Z0-9]{1,10}id$");
                        Matcher m1 = pattern_1.matcher(attL);
                        Matcher m2 = pattern_2.matcher(attL);
                        Matcher m3 = pattern_3.matcher(attL);
                        if ((!(m1.matches()) && !(m2.matches()) && !(m3.matches())) || attL.contains("item")) {
                            itemId_index = i;
                        }
                    }
                }
            }
            if (nAttributes != readData.length){}
            else{
                for (int k = 0; k < readData.length; ++k){
                    HeaderIndexes.put(this.headers.get(k),k);

                    if (k == itemId_index) StringAttrIndexes.put(k,str_index++);
                    else if (readData[k].equalsIgnoreCase("integer")) IntAttrIndexes.put(k, int_index++);
                    else if (readData[k].equalsIgnoreCase("float")) DoubleAttrIndexes.put(k, double_index++);
                    else if (readData[k].equalsIgnoreCase("date")) DateAttrIndexes.put(k, date_index++);
                    else if (readData[k].equalsIgnoreCase("boolean")) BooleanAttrIndexes.put(k, boolean_index++);
                    else if (readData[k].equalsIgnoreCase("categorical")) CategoricalAttrIndexes.put(k, k);
                    else if (readData[k].equalsIgnoreCase("freetext")) StringAttrIndexes.put(k, str_index++);
                    else if (readData[k].equalsIgnoreCase("other")) OtherAttrIndexes.put(k, other_index++);

                }
            }
            while (CtrlPers.hasMoreItems()) {

                ++c;

                ArrayList<Long> IntAttr = new ArrayList<>();
                ArrayList<Double> DoubleAttr = new ArrayList<>();
                ArrayList<String> StringAttr = new ArrayList<>();
                ArrayList<LocalDate> DateAttr = new ArrayList<>();
                ArrayList<String> BooleanAttr = new ArrayList<>();

                /** la expresion regex que rige el split() utiliza la coma como separador, ignora las comas que estan dentro de una string,
                 *  y permite campos nulos en el resultado
                 */
                try { st = CtrlPers.getItemAttributes(); }
                catch (Exception e){}

                String itemId = "";

                item = new Item();

                LinkedHashMap<Integer, ArrayList<String>> CategoricalAttr = new LinkedHashMap<Integer, ArrayList<String>>();

                /** itera sobre los campos de todos los atributos de un item del dataset */
                for (int i = 0; i < nAttributes; i++) {

                    String attributeObject = st.get(i);

                    if (i == itemId_index) {
                        itemId = attributeObject;
                        item.setId(itemId);
                        StringAttr.add(attributeObject);
                    } else if (attributeObject != null) {
                        boolean discarded = false;
                        if (readData[i].equalsIgnoreCase("integer")) {

                            long attr = 0;
                            String newVal = "";
                            //long median = IntColMedian.get(i);
                            if (isInteger(attributeObject)) {
                                long median = IntColMedian.get(i);
                                if (attributeObject.contains(".") || attributeObject.contains(",")) {

                                    /** utilizamos el caracter de escape '\\' ya que el punto en las expresiones regulares es un caracter especial
                                     *  que crea una correspondencia para cualquier valor del caracter
                                     */
                                    attributeObject = attributeObject.replace("\\.", "");
                                    attributeObject = attributeObject.replace("\\,", "");
                                }
                                attr = Long.parseLong(attributeObject);

                                if ((attr - (100 * median)) > median || (attr + (100 * median)) < median)
                                    attr = median;

                                if (c == 1 || (MaxValIntegers.size() == 0 && MinValIntegers.size() == 0)) {
                                    MaxValIntegers.put(i, attr);
                                    MinValIntegers.put(i, attr);
                                } else if (!MaxValIntegers.containsKey(i) || attr > MaxValIntegers.get(i)) MaxValIntegers.put(i, attr);
                                else if (!MinValIntegers.containsKey(i) || attr < MinValIntegers.get(i)) MinValIntegers.put(i, attr);
                            } else {
                                ArrayList<Object> ans = CtrlDominio.pedirInput(1, headers.get(i), st.get(itemId_index));
                                int retCode = Integer.parseInt(ans.get(0).toString());
                                if (retCode == 2 && (IntColMedian.get(i) != null)) attr = IntColMedian.get(i);
                                else if (retCode == 3) discarded = true;
                                else if (retCode == 1){
                                    newVal = ans.get(1).toString();
                                }
                                if (!isInteger(newVal) && (IntColMedian.get(i) != null)) attr = IntColMedian.get(i);
                                else if (!isInteger(newVal)) discarded = true;
                                else{
                                    attr = Long.parseLong(newVal);
                                    try {
                                        if (CtrlPers.UpdfileNotCreated()){
                                            CtrlPers.firstUpdateCSVFile(c,i,newVal,pathToDataset);
                                        }
                                        else CtrlPers.UpdateCSVFile(c,i,newVal,pathToDataset);
                                    }
                                    catch (Exception e){}
                                }
                            }
                            if (!discarded) IntAttr.add(attr);
                            else IntAttr.add(null);
                        } else if (readData[i].equalsIgnoreCase("float")) {
                            double attr = 0.0;
                            discarded = false;
                            String newVal = "";

                            if (isDouble(attributeObject)) {
                                double median = DoubleColMedian.get(i);
                                attr = Double.parseDouble(attributeObject);

                                if ((attr - 100 * median) > median || (attr + 100 * median) < median) attr = median;

                                if (c == 1) {
                                    MaxValDoubles.put(i, attr);
                                    MinValDoubles.put(i, attr);
                                } else if (!MaxValDoubles.containsKey(i) || attr > MaxValDoubles.get(i)) MaxValDoubles.put(i, attr);
                                else if (!MinValDoubles.containsKey(i) || attr < MinValDoubles.get(i)) MinValDoubles.put(i, attr);
                            } else {
                                ArrayList<Object> ans = CtrlDominio.pedirInput(2, headers.get(i), st.get(itemId_index));
                                int retCode = Integer.parseInt(ans.get(0).toString());
                                if (retCode == 2 && (DoubleColMedian.get(i) != null)) attr = DoubleColMedian.get(i);
                                else if (retCode == 3) discarded = true;
                                else if (retCode == 1){
                                    newVal = ans.get(1).toString();
                                }
                                if (!isDouble(newVal) && (DoubleColMedian.get(i) != null)) attr = DoubleColMedian.get(i);
                                else if (!isDouble(newVal)) discarded = true;
                                else{
                                    attr = Double.parseDouble(newVal);
                                    try {
                                        if (CtrlPers.UpdfileNotCreated()){
                                            CtrlPers.firstUpdateCSVFile(c,i,newVal,pathToDataset);
                                        }
                                        else CtrlPers.UpdateCSVFile(c,i,newVal,pathToDataset);
                                    }
                                    catch (Exception e){}
                                }
                            }
                            if (!discarded) DoubleAttr.add(attr);
                            else DoubleAttr.add(null);
                        } else if (readData[i].equalsIgnoreCase("boolean")) {

                            if (isInteger(attributeObject) || isDouble(attributeObject) || isDate(attributeObject)){
                                ArrayList<Object> ans = CtrlDominio.pedirInput(3, headers.get(i), st.get(itemId_index));
                                String newVal = "";
                                int retCode = Integer.parseInt(ans.get(0).toString());
                                if (retCode == 2 || retCode == 3) attributeObject = null;
                                else if (retCode == 1){
                                    newVal = ans.get(1).toString();
                                }
                                if (isInteger(newVal) || isDouble(newVal) || isDate(newVal)) attributeObject = null;
                                else{
                                    attributeObject = newVal;
                                    try {
                                        if (CtrlPers.UpdfileNotCreated()){
                                            CtrlPers.firstUpdateCSVFile(c,i,newVal,pathToDataset);
                                        }
                                        else CtrlPers.UpdateCSVFile(c,i,newVal,pathToDataset);
                                    }
                                    catch (Exception e){}
                                }
                            }
                            BooleanAttr.add(attributeObject); //check afterwards if it indeed is a boolean-- when eliminating absurd categorical attr
                        } else if (readData[i].equalsIgnoreCase("date")) {

                            LocalDate parsedDate = null;
                            if (isDate(attributeObject)) {
                                int median = DateColMedian.get(i);
                                DateTimeFormatter parser = DateTimeFormatter.ofPattern("[yyyy/MM/dd][dd/MM/yyyy][yyyy-MM-dd][dd-MM-yyyy]");

                                try {
                                    LocalDate date = LocalDate.parse(attributeObject, parser);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    String formattedDate = date.format(formatter);
                                    parsedDate = LocalDate.parse(formattedDate, formatter);

                                    if (c == 1) {
                                        MaxDates.put(i, parsedDate);
                                        MinDates.put(i, parsedDate);
                                        if ((parsedDate.getYear() - 0.8 * median) > median || (parsedDate.getYear() + 0.8 * median) < median) {
                                            parsedDate = LocalDate.of(median, parsedDate.getMonth(), parsedDate.getDayOfMonth());
                                        }
                                    } else {
                                        if ((parsedDate.getYear() - 0.8 * median) > median || (parsedDate.getYear() + 0.8 * median) < median) {
                                            parsedDate = LocalDate.of(median, MinDates.get(i).getMonth(), MinDates.get(i).getDayOfMonth());
                                        }
                                        if (parsedDate.isAfter(MaxDates.get(i))) MaxDates.put(i, parsedDate);
                                        else if (parsedDate.isBefore(MinDates.get(i))) MinDates.put(i, parsedDate);
                                    }
                                } catch (Exception e) {
                                }
                            } else {
                                ArrayList<Object> ans = CtrlDominio.pedirInput(4, headers.get(i), st.get(itemId_index));
                                String newVal = "";
                                int retCode = Integer.parseInt(ans.get(0).toString());
                                if (retCode == 2 && (DateColMedian.get(i) != null)) parsedDate = LocalDate.of(DateColMedian.get(i), MinDates.get(i).getMonth(), MinDates.get(i).getDayOfMonth());
                                else if (retCode == 3) parsedDate = null;
                                else if (retCode == 1){
                                    newVal = ans.get(1).toString();
                                }
                                if (!isDate(newVal) && (DateColMedian.get(i) != null)) parsedDate = LocalDate.of(DateColMedian.get(i), MinDates.get(i).getMonth(), MinDates.get(i).getDayOfMonth());
                                else if (!isDate(newVal)) parsedDate = null;
                                else{
                                    DateTimeFormatter parser = DateTimeFormatter.ofPattern("[yyyy/MM/dd][dd/MM/yyyy][yyyy-MM-dd][dd-MM-yyyy]");
                                    LocalDate date = LocalDate.parse(newVal, parser);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    String formattedDate = date.format(formatter);
                                    parsedDate = LocalDate.parse(formattedDate, formatter);
                                    try {
                                        if (CtrlPers.UpdfileNotCreated()){
                                            CtrlPers.firstUpdateCSVFile(c,i,newVal,pathToDataset);
                                        }
                                        else CtrlPers.UpdateCSVFile(c,i,newVal,pathToDataset);
                                    }
                                    catch (Exception e){}
                                }
                            }
                            DateAttr.add(parsedDate);
                        } else if (readData[i].equalsIgnoreCase("categorical")) {
                            ArrayList<String> CategoricalVars = new ArrayList<String>();
                            CategoricalVars = parseToArray2(attributeObject);
                            boolean wrongType = false;
                            for (String s : CategoricalVars){
                                if (isInteger(s) || isDouble(s) || isDate(s)){
                                    wrongType = true;
                                }
                            }
                            if (wrongType){
                                ArrayList<Object> ans = CtrlDominio.pedirInput(5, headers.get(i), st.get(itemId_index));
                                String newVal = "";
                                int retCode = Integer.parseInt(ans.get(0).toString());
                                if (retCode == 2 || retCode == 3) attributeObject = "";
                                else if (retCode == 1){
                                    wrongType = false;
                                    newVal = ans.get(1).toString();
                                    ArrayList<String> newCategories = parseToArray2(newVal);
                                    for (String s : newCategories){
                                        if (isInteger(s) || isDouble(s) || isDate(s)) {
                                            attributeObject = "";
                                            wrongType = true;
                                        }
                                    }
                                    if (!wrongType) {
                                        CategoricalVars = newCategories;
                                        try {
                                            if (CtrlPers.UpdfileNotCreated()){
                                                CtrlPers.firstUpdateCSVFile(c,i,newVal,pathToDataset);
                                            }
                                            else CtrlPers.UpdateCSVFile(c,i,newVal,pathToDataset);
                                        }
                                        catch (Exception e){}
                                    }
                                }
                            }
                            isCatAttNull.put(i, attributeObject.length() == 0);
                            HashSet<Integer> indexesCatVar = new HashSet<Integer>();
                            ArrayList<String> ColVar = dsCategoricalVars.get(i);
                            if (dsCategoricalVars.containsKey(i)) ColVar.addAll(CategoricalVars);
                            else ColVar = CategoricalVars;
                            dsCategoricalVars.put(i, ColVar);

                            for (int k = 0; k < CategoricalVars.size(); ++k) {
                                if ((CategoricalVarsBit.get(CategoricalVars.get(k)) != null && !CategoricalVarsBit.get(CategoricalVars.get(k)).containsKey(i)) || !CategoricalVarsBit.containsKey(CategoricalVars.get(k))) {
                                    CatVarIndexes.merge(i, 1, Integer::sum);
                                }
                                if (!CategoricalVarsBit.containsKey(CategoricalVars.get(k)))
                                    CategoricalVarsBit.put(CategoricalVars.get(k), new HashMap<Integer, Integer>());
                                HashMap<Integer, Integer> colBits = CategoricalVarsBit.get(CategoricalVars.get(k));
                                colBits.putIfAbsent(i, CatVarIndexes.get(i));
                                CategoricalVarsBit.put(CategoricalVars.get(k), colBits);
                            }
                            CategoricalAttr.put(i, new ArrayList<String>(CategoricalVars));
                        } else if (readData[i].equalsIgnoreCase("freetext")){
                            if ((isInteger(attributeObject) || isDouble(attributeObject) || isDate(attributeObject)) && (i != itemId_index)){
                                ArrayList<Object> ans = CtrlDominio.pedirInput(6, headers.get(i), st.get(itemId_index));
                                String newVal = "";
                                int retCode = Integer.parseInt(ans.get(0).toString());
                                if (retCode == 2 || retCode == 3) attributeObject = null;
                                else if (retCode == 1){
                                    newVal = ans.get(1).toString();
                                }
                                if (isInteger(newVal) || isDouble(newVal) || isDate(newVal)) attributeObject = null;
                                else{
                                    attributeObject = newVal;
                                    try {
                                        if (CtrlPers.UpdfileNotCreated()){
                                            CtrlPers.firstUpdateCSVFile(c,i,newVal,pathToDataset);
                                        }
                                        else CtrlPers.UpdateCSVFile(c,i,newVal,pathToDataset);
                                    }
                                    catch (Exception e){}
                                }
                            }
                            StringAttr.add(attributeObject);
                        }
                    }
                }
                LinkedHashMap<Integer, ArrayList<String>> CatAttr = new LinkedHashMap<Integer, ArrayList<String>>(CategoricalAttr);

                /** asigna las categorias asociadas a cada variable categorica e item */
                AllCategoricalAttributes.put(itemId, CatAttr);

                item.setAttributes(IntAttr, DoubleAttr, StringAttr, null, DateAttr, BooleanAttr, CatAttr);
                this.Items.put(itemId,item);
            }
            CtrlPers.reset();
        } catch (IOException e) {
            System.out.println(e);
        }

        for (Iterator<Map.Entry<Integer, ArrayList<String>>> i = dsCategoricalVars.entrySet().iterator(); i.hasNext(); ) {

            Map.Entry<Integer, ArrayList<String>> ds_entry = i.next();
            ArrayList<String> CatInstances = ds_entry.getValue();
            Set<String> uniqueInstances = new HashSet<String>(CatInstances);

            /** convertimos la ArrayList a Set tal que se mapen solo instancias unicas
             * de categorias para cada columna asociada a un atributo categorico */
            double uniquevsTotal = (double) uniqueInstances.size() / CatInstances.size();

            /** si mas del 90% de las potenciales categorias son unicas con respecto a la union de todas las instancias,
             *  consideramos que se trata de un atributo categorico absurdo, descartamos los atributos que no tienen ningun valor no nulo
             */
            if ((isCatAttNull.get(ds_entry.getKey())) || uniquevsTotal > 0.90) {

                for (Map.Entry<String, LinkedHashMap<Integer, ArrayList<String>>> j : AllCategoricalAttributes.entrySet()) {
                    LinkedHashMap<Integer, ArrayList<String>> jthItem = j.getValue();

                    for (Iterator<Map.Entry<Integer, ArrayList<String>>> it = jthItem.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<Integer, ArrayList<String>> entry = it.next();
                        if (entry.getKey().equals(ds_entry.getKey())) {
                            //it.remove();
                            entry.setValue(null);
                        }
                    }
                    /** actualizamos el item en el conjunto,
                     * que ahora contiene un valor null en el atributo categorico "absurdo"
                     */
                    AllCategoricalAttributes.put(j.getKey(), jthItem);
                }
            }
        }

        for (Iterator<Map.Entry<String, LinkedHashMap<Integer, ArrayList<String>>> > it = AllCategoricalAttributes.entrySet().iterator(); it.hasNext(); ) {

            ArrayList<BitSet> instance_presenceBits = new ArrayList<BitSet>();
            BitSet psc_bits;
            Map.Entry<String, LinkedHashMap<Integer, ArrayList<String>>> item_entries = it.next();
            LinkedHashMap<Integer, ArrayList<String>> currentItem = item_entries.getValue();

            for (Iterator<Map.Entry<Integer, ArrayList<String>>> i = currentItem.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry<Integer, ArrayList<String>> entry = i.next();

                int sz = 0;

                if (CatVarIndexes.containsKey(entry.getKey())) sz = CatVarIndexes.get(entry.getKey());

                ArrayList<String> ithcategory = entry.getValue();
                psc_bits = new BitSet(sz);

                for (String instance : ithcategory) {
                    psc_bits.set(CategoricalVarsBit.get(instance).get(entry.getKey()));
                }
                instance_presenceBits.add(psc_bits);
            }
            /** obtenemos el item correspondiente en el conjunto y le asignamos el conjunto de One-Hot Encodings
             *  para cada uno de los atributos categoricos asociados
             */
            Item currItem = this.Items.get(item_entries.getKey());
            currItem.setAttributes(null, null, null, instance_presenceBits, null, null,null);

            this.Items.put(item_entries.getKey(), currItem);
        }
    }

    /**
     * Constructora parametrizada que asocia a una instancia de Cjt_items el conjunto de items dado por el fichero con path=pathToDataset preprocesado
     *
     * @param pathToDataset indica el camino hasta el archivo en que se halla el dataset a leer
     * @param pathToAttrData_types indica el camino hasta el archivo en que se halla el tipo de dato para cada atributo del dataset
     * @param CtrlDominio Instancia de ControladorDominio
     * @param language Lengua utilizada en el dataset de items
     */
    public Cjt_items(String pathToDataset, String pathToAttrData_types, ControladorDominio CtrlDominio, String language) {

        this.language = language;
        ControladorPersistencia CtrlPers = ControladorPersistencia.getInstance();
        try {
            CtrlPers.reset();
        } catch (IOException e) {
        }

        HashMap<Integer, Long> IntColMedian = new HashMap<Integer, Long>();
        HashMap<Integer, Double> DoubleColMedian = new HashMap<Integer, Double>();
        HashMap<Integer, Integer> DateColMedian = new HashMap<Integer, Integer>();

        Item item = new Item();

        /** c identifica e indexa los items en el contexto de cada instancia de la clase */
        int c = 0;

        /** contiene para cada indice el conjunto union de instancias de cada variable categorica del conjunto */
        LinkedHashMap<Integer, ArrayList<String> > dsCategoricalVars = new LinkedHashMap<>();

        /** contiene para cada instancia de variable categorica un indice que la identifica para el posterior One-hot Encoding */
        HashMap<String, HashMap<Integer,Integer> > CategoricalVarsBit = new HashMap<>();

        /** contiene el numero de instancias o categorias diferentes
         * que toma la variable categorica asociada al atributo de la columna i en el conjunto
         */
        HashMap<Integer, Integer> CatVarIndexes = new HashMap<>();

        /** contiene para cada item y cada variable catogorica del conjunto, las ategorias asociadas */
        HashMap<String, LinkedHashMap<Integer, ArrayList<String>>> AllCategoricalAttributes = new HashMap<>();

        HashMap<Integer, Boolean> isCatAttNull = new HashMap<>();

        this.MaxValIntegers = new LinkedHashMap<>();
        this.MinValIntegers = new LinkedHashMap<>();
        this.MaxValDoubles = new LinkedHashMap<>();
        this.MinValDoubles = new LinkedHashMap<>();
        this.MaxDates = new LinkedHashMap<>();
        this.MinDates = new LinkedHashMap<>();

        this.Items = new HashMap<>();
        boolean belongsTo = false;

        try {
            String[] readData = new String[]{};
            try {
                readData = CtrlPers.getDataTypes(pathToAttrData_types);
            } catch (Exception e){}
            int nItems = getColMedians(IntColMedian, DoubleColMedian, DateColMedian, pathToDataset, readData);
            int int_index, double_index, date_index, boolean_index, str_index, other_index, categorical_index;
            int_index = double_index = date_index = boolean_index = str_index = other_index = categorical_index =0;

            ArrayList<String> st = new ArrayList<>();
            try {
                CtrlPers.initializeCtrlItems(pathToDataset);
                st = CtrlPers.getAttributeHeaders();
            } catch (Exception e){}

            int nAttributes = CtrlPers.getNumOfAttributes();

            if (st.size() > 0) {
                this.headers = st;

                for (int i = 0; i < st.size(); i++) {

                    String attribute = st.get(i);

                    String attL = attribute.toLowerCase();

                    if ((attL == "id") || (attL.contains("id"))) {
                        Pattern pattern_1 = Pattern.compile("^[a-z]{1,10}id[a-z]{1,10}$");
                        Pattern pattern_2 = Pattern.compile("^id[\\W\\S_a-zA-Z0-9]{1,10}$");
                        Pattern pattern_3 = Pattern.compile("^[\\W\\S_a-zA-Z0-9]{1,10}id$");
                        Matcher m1 = pattern_1.matcher(attL);
                        Matcher m2 = pattern_2.matcher(attL);
                        Matcher m3 = pattern_3.matcher(attL);
                        if ((!(m1.matches()) && !(m2.matches()) && !(m3.matches())) || attL.contains("item")) {
                            itemId_index = i;
                        }
                    }
                }
            }

            if (nAttributes != readData.length){} //send message to user, incorrect data type definition for the item's attributs
            else{
                for (int k = 0; k < readData.length; ++k){
                    HeaderIndexes.put(this.headers.get(k),k);

                    if (k == itemId_index) StringAttrIndexes.put(k,str_index++);
                    else if (readData[k].equalsIgnoreCase("integer")) IntAttrIndexes.put(k, int_index++);
                    else if (readData[k].equalsIgnoreCase("float")) DoubleAttrIndexes.put(k, double_index++);
                    else if (readData[k].equalsIgnoreCase("date")) DateAttrIndexes.put(k, date_index++);
                    else if (readData[k].equalsIgnoreCase("boolean")) BooleanAttrIndexes.put(k, boolean_index++);
                    else if (readData[k].equalsIgnoreCase("categorical")) CategoricalAttrIndexes.put(k, k);
                    else if (readData[k].equalsIgnoreCase("freetext")) StringAttrIndexes.put(k, str_index++);
                    else if (readData[k].equalsIgnoreCase("other")) OtherAttrIndexes.put(k, other_index++);

                }
            }
            while (CtrlPers.hasMoreItems()) {

                ++c;

                ArrayList<Long> IntAttr = new ArrayList<>();
                ArrayList<Double> DoubleAttr = new ArrayList<>();
                ArrayList<String> StringAttr = new ArrayList<>();
                ArrayList<LocalDate> DateAttr = new ArrayList<>();
                ArrayList<String> BooleanAttr = new ArrayList<>();
                LinkedHashMap<Integer, HashMap<String,Integer> > wordFrequencies = new LinkedHashMap<>();

                /** la expresion regex que rige el split() utiliza la coma como separador, ignora las comas que estan dentro de una string,
                 *  y permite campos nulos en el resultado
                 */
                try { st = CtrlPers.getItemAttributes(); }
                catch (Exception e){}

                String itemId = "";

                item = new Item();

                LinkedHashMap<Integer, ArrayList<String>> CategoricalAttr = new LinkedHashMap<Integer, ArrayList<String>>();

                /** itera sobre los campos de todos los atributos de un item del dataset */
                for (int i = 0; i < nAttributes; i++) {

                    String attributeObject = st.get(i);

                    if (i == itemId_index) {
                        itemId = attributeObject;
                        item.setId(itemId);
                        StringAttr.add(attributeObject);
                    } else if (attributeObject != null) {
                        boolean discarded = false;
                        if (readData[i].equalsIgnoreCase("integer")) {

                            long attr = 0;
                            String newVal = "";
                            //long median = IntColMedian.get(i);
                            if (isInteger(attributeObject)) {
                                long median = IntColMedian.get(i);
                                if (attributeObject.contains(".") || attributeObject.contains(",")) {

                                    /** utilizamos el caracter de escape '\\' ya que el punto en las expresiones regulares es un caracter especial
                                     *  que crea una correspondencia para cualquier valor del caracter
                                     */
                                    attributeObject = attributeObject.replace("\\.", "");
                                    attributeObject = attributeObject.replace("\\,", "");
                                }
                                attr = Long.parseLong(attributeObject);

                                if ((attr - (100 * median)) > median || (attr + (100 * median)) < median)
                                    attr = median;

                                if (c == 1 || (MaxValIntegers.size() == 0 && MinValIntegers.size() == 0)) {
                                    MaxValIntegers.put(i, attr);
                                    MinValIntegers.put(i, attr);
                                } else if (!MaxValIntegers.containsKey(i) || attr > MaxValIntegers.get(i)) MaxValIntegers.put(i, attr);
                                else if (!MinValIntegers.containsKey(i) || attr < MinValIntegers.get(i)) MinValIntegers.put(i, attr);
                            } else {
                                ArrayList<Object> ans = CtrlDominio.pedirInput(1, headers.get(i), st.get(itemId_index));
                                int retCode = Integer.parseInt(ans.get(0).toString());
                                if (retCode == 2 && (IntColMedian.get(i) != null)) attr = IntColMedian.get(i);
                                else if (retCode == 3) discarded = true;
                                else if (retCode == 1){
                                    newVal = ans.get(1).toString();
                                }
                                if (!isInteger(newVal) && (IntColMedian.get(i) != null)) attr = IntColMedian.get(i);
                                else if (!isInteger(newVal)) discarded = true;
                                else{
                                    attr = Long.parseLong(newVal);
                                    try {
                                        if (CtrlPers.UpdfileNotCreated()){
                                            CtrlPers.firstUpdateCSVFile(c,i,newVal,pathToDataset);
                                        }
                                        else CtrlPers.UpdateCSVFile(c,i,newVal,pathToDataset);
                                    }
                                    catch (Exception e){}
                                }
                            }
                            if (!discarded) IntAttr.add(attr);
                            else IntAttr.add(null);
                        } else if (readData[i].equalsIgnoreCase("float")) {
                            double attr = 0.0;
                            discarded = false;
                            String newVal = "";

                            if (isDouble(attributeObject)) {
                                double median = DoubleColMedian.get(i);
                                attr = Double.parseDouble(attributeObject);

                                if ((attr - 100 * median) > median || (attr + 100 * median) < median) attr = median;

                                if (c == 1) {
                                    MaxValDoubles.put(i, attr);
                                    MinValDoubles.put(i, attr);
                                } else if (!MaxValDoubles.containsKey(i) || attr > MaxValDoubles.get(i)) MaxValDoubles.put(i, attr);
                                else if (!MinValDoubles.containsKey(i) || attr < MinValDoubles.get(i)) MinValDoubles.put(i, attr);
                            } else {
                                ArrayList<Object> ans = CtrlDominio.pedirInput(2, headers.get(i), st.get(itemId_index));
                                int retCode = Integer.parseInt(ans.get(0).toString());
                                if (retCode == 2 && (DoubleColMedian.get(i) != null)) attr = DoubleColMedian.get(i);
                                else if (retCode == 3) discarded = true;
                                else if (retCode == 1){
                                    newVal = ans.get(1).toString();
                                }
                                if (!isDouble(newVal) && (DoubleColMedian.get(i) != null)) attr = DoubleColMedian.get(i);
                                else if (!isDouble(newVal)) discarded = true;
                                else{
                                    attr = Double.parseDouble(newVal);
                                    try {
                                        if (CtrlPers.UpdfileNotCreated()){
                                            CtrlPers.firstUpdateCSVFile(c,i,newVal,pathToDataset);
                                        }
                                        else CtrlPers.UpdateCSVFile(c,i,newVal,pathToDataset);
                                    }
                                    catch (Exception e){}
                                }
                            }
                            if (!discarded) DoubleAttr.add(attr);
                            else DoubleAttr.add(null);
                        } else if (readData[i].equalsIgnoreCase("boolean")) {

                            if (isInteger(attributeObject) || isDouble(attributeObject) || isDate(attributeObject)){
                                ArrayList<Object> ans = CtrlDominio.pedirInput(3, headers.get(i), st.get(itemId_index));
                                String newVal = "";
                                int retCode = Integer.parseInt(ans.get(0).toString());
                                if (retCode == 2 || retCode == 3) attributeObject = null;
                                else if (retCode == 1){
                                    newVal = ans.get(1).toString();
                                }
                                if (isInteger(newVal) || isDouble(newVal) || isDate(newVal)) attributeObject = null;
                                else{
                                    attributeObject = newVal;
                                    try {
                                        if (CtrlPers.UpdfileNotCreated()){
                                            CtrlPers.firstUpdateCSVFile(c,i,newVal,pathToDataset);
                                        }
                                        else CtrlPers.UpdateCSVFile(c,i,newVal,pathToDataset);
                                    }
                                    catch (Exception e){}
                                }
                            }
                            BooleanAttr.add(attributeObject); //check afterwards if it indeed is a boolean-- when eliminating absurd categorical attr
                        } else if (readData[i].equalsIgnoreCase("date")) {

                            LocalDate parsedDate = null;
                            if (isDate(attributeObject)) {
                                int median = DateColMedian.get(i);
                                DateTimeFormatter parser = DateTimeFormatter.ofPattern("[yyyy/MM/dd][dd/MM/yyyy][yyyy-MM-dd][dd-MM-yyyy]");

                                try {
                                    LocalDate date = LocalDate.parse(attributeObject, parser);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    String formattedDate = date.format(formatter);
                                    parsedDate = LocalDate.parse(formattedDate, formatter);

                                    if (c == 1) {
                                        MaxDates.put(i, parsedDate);
                                        MinDates.put(i, parsedDate);
                                        if ((parsedDate.getYear() - 0.8 * median) > median || (parsedDate.getYear() + 0.8 * median) < median) {
                                            parsedDate = LocalDate.of(median, parsedDate.getMonth(), parsedDate.getDayOfMonth());
                                        }
                                    } else {
                                        if ((parsedDate.getYear() - 0.8 * median) > median || (parsedDate.getYear() + 0.8 * median) < median) {
                                            parsedDate = LocalDate.of(median, MinDates.get(i).getMonth(), MinDates.get(i).getDayOfMonth());
                                        }
                                        if (parsedDate.isAfter(MaxDates.get(i))) MaxDates.put(i, parsedDate);
                                        else if (parsedDate.isBefore(MinDates.get(i))) MinDates.put(i, parsedDate);
                                    }
                                } catch (Exception e) {
                                }
                            } else {
                                ArrayList<Object> ans = CtrlDominio.pedirInput(4, headers.get(i), st.get(itemId_index));
                                String newVal = "";
                                int retCode = Integer.parseInt(ans.get(0).toString());
                                if (retCode == 2 && (DateColMedian.get(i) != null)) parsedDate = LocalDate.of(DateColMedian.get(i), MinDates.get(i).getMonth(), MinDates.get(i).getDayOfMonth());
                                else if (retCode == 3) parsedDate = null;
                                else if (retCode == 1){
                                    newVal = ans.get(1).toString();
                                }
                                if (!isDate(newVal) && (DateColMedian.get(i) != null)) parsedDate = LocalDate.of(DateColMedian.get(i), MinDates.get(i).getMonth(), MinDates.get(i).getDayOfMonth());
                                else if (!isDate(newVal)) parsedDate = null;
                                else{
                                    DateTimeFormatter parser = DateTimeFormatter.ofPattern("[yyyy/MM/dd][dd/MM/yyyy][yyyy-MM-dd][dd-MM-yyyy]");
                                    LocalDate date = LocalDate.parse(newVal, parser);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    String formattedDate = date.format(formatter);
                                    parsedDate = LocalDate.parse(formattedDate, formatter);
                                    try {
                                        if (CtrlPers.UpdfileNotCreated()){
                                            CtrlPers.firstUpdateCSVFile(c,i,newVal,pathToDataset);
                                        }
                                        else CtrlPers.UpdateCSVFile(c,i,newVal,pathToDataset);
                                    }
                                    catch (Exception e){}
                                }
                            }
                            DateAttr.add(parsedDate);
                        } else if (readData[i].equalsIgnoreCase("categorical")) {
                            ArrayList<String> CategoricalVars = new ArrayList<String>();
                            CategoricalVars = parseToArray2(attributeObject);
                            boolean wrongType = false;
                            for (String s : CategoricalVars){
                                if (isInteger(s) || isDouble(s) || isDate(s)){
                                    wrongType = true;
                                }
                            }
                            if (wrongType){
                                ArrayList<Object> ans = CtrlDominio.pedirInput(5, headers.get(i), st.get(itemId_index));
                                String newVal = "";
                                int retCode = Integer.parseInt(ans.get(0).toString());
                                if (retCode == 2 || retCode == 3) attributeObject = "";
                                else if (retCode == 1){
                                    wrongType = false;
                                    newVal = ans.get(1).toString();
                                    ArrayList<String> newCategories = parseToArray2(newVal);
                                    for (String s : newCategories){
                                        if (isInteger(s) || isDouble(s) || isDate(s)) {
                                            attributeObject = "";
                                            wrongType = true;
                                        }
                                    }
                                    if (!wrongType) {
                                        CategoricalVars = newCategories;
                                        try {
                                            if (CtrlPers.UpdfileNotCreated()){
                                                CtrlPers.firstUpdateCSVFile(c,i,newVal,pathToDataset);
                                            }
                                            else CtrlPers.UpdateCSVFile(c,i,newVal,pathToDataset);
                                        }
                                        catch (Exception e){}
                                    }
                                }
                            }
                            isCatAttNull.put(i, attributeObject.length() == 0);
                            HashSet<Integer> indexesCatVar = new HashSet<Integer>();
                            ArrayList<String> ColVar = dsCategoricalVars.get(i);
                            if (dsCategoricalVars.containsKey(i)) ColVar.addAll(CategoricalVars);
                            else ColVar = CategoricalVars;
                            dsCategoricalVars.put(i, ColVar);

                            for (int k = 0; k < CategoricalVars.size(); ++k) {
                                if ((CategoricalVarsBit.get(CategoricalVars.get(k)) != null && !CategoricalVarsBit.get(CategoricalVars.get(k)).containsKey(i)) || !CategoricalVarsBit.containsKey(CategoricalVars.get(k))) {
                                    CatVarIndexes.merge(i, 1, Integer::sum);
                                }
                                if (!CategoricalVarsBit.containsKey(CategoricalVars.get(k)))
                                    CategoricalVarsBit.put(CategoricalVars.get(k), new HashMap<Integer, Integer>());
                                HashMap<Integer, Integer> colBits = CategoricalVarsBit.get(CategoricalVars.get(k));
                                colBits.putIfAbsent(i, CatVarIndexes.get(i));
                                CategoricalVarsBit.put(CategoricalVars.get(k), colBits);
                            }
                            CategoricalAttr.put(i, new ArrayList<String>(CategoricalVars));
                        } else if (readData[i].equalsIgnoreCase("freetext")){
                            if ((isInteger(attributeObject) || isDouble(attributeObject) || isDate(attributeObject)) && (i != itemId_index)){
                                ArrayList<Object> ans = CtrlDominio.pedirInput(6, headers.get(i), st.get(itemId_index));
                                String newVal = "";
                                int retCode = Integer.parseInt(ans.get(0).toString());
                                if (retCode == 2 || retCode == 3) attributeObject = null;
                                else if (retCode == 1){
                                    newVal = ans.get(1).toString();
                                }
                                if (isInteger(newVal) || isDouble(newVal) || isDate(newVal)) attributeObject = null;
                                else{
                                    attributeObject = newVal;
                                    try {
                                        if (CtrlPers.UpdfileNotCreated()){
                                            CtrlPers.firstUpdateCSVFile(c,i,newVal,pathToDataset);
                                        }
                                        else CtrlPers.UpdateCSVFile(c,i,newVal,pathToDataset);
                                    }
                                    catch (Exception e){}
                                }
                            }
                            ArrayList<String> sw = new ArrayList<>();

                            try {
                                if (language.equals("Ingles")){
                                    sw = CtrlPers.getEnglishStepWords();
                                    belongsTo = true;
                                }
                                else if (language.equals("Catalan")){
                                    sw = CtrlPers.getCatalanStepWords();
                                    belongsTo = true;
                                }
                                else if (language.equals("Espanol")){
                                    sw = CtrlPers.getSpanishStepWords();
                                    belongsTo = true;
                                }
                                else if (language.equals("Frances")){
                                    sw = CtrlPers.getFrenchStepWords();
                                    belongsTo = true;
                                }
                                else if (language.equals("Portugues")){
                                    sw = CtrlPers.getPortugueseStepWords();
                                    belongsTo = true;
                                }
                                else if (language.equals("Italiano")){
                                    sw = CtrlPers.getItalianStepWords();
                                    belongsTo = true;
                                }
                                else if (language.equals("Aleman")){
                                    sw = CtrlPers.getGermanStepWords();
                                    belongsTo = true;
                                }
                            } catch (Exception e){}
                            String mapped_words = attributeObject;
                            ArrayList<String> allwords = Stream.of(mapped_words.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+")).collect(Collectors.toCollection(ArrayList<String>::new));
                            allwords.removeAll(sw);
                            wordFrequencies.putIfAbsent(i, new HashMap<>());
                            HashMap<String,Integer> FreqMap = wordFrequencies.get(i);
//                            String[] words = Arrays.stream(allwords.split("[^\\w\\d']")).filter(str -> !str.isEmpty()).toArray(String[]::new);
                            for (String s : allwords){
                                FreqMap.merge(s, 1, Integer::sum);
                                //System.out.println("word! " + s);
                            }
                            wordFrequencies.put(i,FreqMap);
                            StringAttr.add(attributeObject);
                        }
                    }
                }
                LinkedHashMap<Integer, ArrayList<String>> CatAttr = new LinkedHashMap<Integer, ArrayList<String>>(CategoricalAttr);

                // asigna las categorias asociadas a cada variable categorica e item
                AllCategoricalAttributes.put(itemId, CatAttr);

                if (belongsTo) item.setWordFrequencies(wordFrequencies);
                item.setAttributes(IntAttr, DoubleAttr, StringAttr, null, DateAttr, BooleanAttr, CatAttr);
                this.Items.put(itemId,item);
            }
            CtrlPers.reset();
        } catch (IOException e) {
            System.out.println(e);
        }

        for (Iterator<Map.Entry<Integer, ArrayList<String>>> i = dsCategoricalVars.entrySet().iterator(); i.hasNext(); ) {

            Map.Entry<Integer, ArrayList<String>> ds_entry = i.next();
            ArrayList<String> CatInstances = ds_entry.getValue();
            Set<String> uniqueInstances = new HashSet<String>(CatInstances);

            /** convertimos la ArrayList a Set tal que se mapen solo instancias unicas
             * de categorias para cada columna asociada a un atributo categorico */
            double uniquevsTotal = (double) uniqueInstances.size() / CatInstances.size();

            /** si mas del 90% de las potenciales categorias son unicas con respecto a la union de todas las instancias,
             *  consideramos que se trata de un atributo categorico absurdo, descartamos los atributos que no tienen ningun valor no nulo
             */
            if ((isCatAttNull.get(ds_entry.getKey())) || uniquevsTotal > 0.90) {

                for (Map.Entry<String, LinkedHashMap<Integer, ArrayList<String>>> j : AllCategoricalAttributes.entrySet()) {
                    LinkedHashMap<Integer, ArrayList<String>> jthItem = j.getValue();

                    for (Iterator<Map.Entry<Integer, ArrayList<String>>> it = jthItem.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<Integer, ArrayList<String>> entry = it.next();
                        if (entry.getKey().equals(ds_entry.getKey())) {
                            it.remove();
                        }
                    }
                    /** actualizamos el item en el conjunto,
                     * que ahora contiene un valor null en el atributo categorico "absurdo"
                     */
                    AllCategoricalAttributes.put(j.getKey(), jthItem);
                }
            }
        }

        for (Iterator<Map.Entry<String, LinkedHashMap<Integer, ArrayList<String>>> > it = AllCategoricalAttributes.entrySet().iterator(); it.hasNext(); ) {

            ArrayList<BitSet> instance_presenceBits = new ArrayList<BitSet>();
            BitSet psc_bits;
            Map.Entry<String, LinkedHashMap<Integer, ArrayList<String>>> item_entries = it.next();
            HashMap<Integer, ArrayList<String>> currentItem = item_entries.getValue();

            for (Iterator<Map.Entry<Integer, ArrayList<String>>> i = currentItem.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry<Integer, ArrayList<String>> entry = i.next();

                int sz = 0;

                if (CatVarIndexes.containsKey(entry.getKey())) sz = CatVarIndexes.get(entry.getKey());

                ArrayList<String> ithcategory = entry.getValue();
                psc_bits = new BitSet(sz);

                for (String instance : ithcategory) {
                    psc_bits.set(CategoricalVarsBit.get(instance).get(entry.getKey()));
                }
                instance_presenceBits.add(psc_bits);
            }
            /** obtenemos el item correspondiente en el conjunto y le asignamos el conjunto de One-Hot Encodings
             *  para cada uno de los atributos categoricos asociados
             */
            Item currItem = this.Items.get(item_entries.getKey());
            currItem.setAttributes(null, null, null, instance_presenceBits, null, null,null);

            this.Items.put(item_entries.getKey(), currItem);
        }
    }

    public HashMap<Integer, Integer> getIntAttrIndexes() {
        return this.IntAttrIndexes;
    }

    public HashMap<Integer, Integer> getDoubleAttrIndexes() {
        return DoubleAttrIndexes;
    }

    public HashMap<Integer, Integer> getDateAttrIndexes() {
        return DateAttrIndexes;
    }

    public HashMap<Integer, Integer> getBooleanAttrIndexes() {
        return BooleanAttrIndexes;
    }

    public HashMap<Integer, Integer> getCategoricalAttrIndexes() {
        return CategoricalAttrIndexes;
    }

    public HashMap<Integer, Integer> getStringAttrIndexes() {
        return StringAttrIndexes;
    }

    public HashMap<Integer, Integer> getOtherAttrIndexes() {
        return OtherAttrIndexes;
    }

    public HashMap<String, Integer> getHeaderIndexes() {
        return HeaderIndexes;
    }

    public String getLanguage() {
        return language;
    }

    public void setHeaders(ArrayList<String> headers) {
        this.headers = new ArrayList<>(headers);
    }

    public void setMaxValIntegers(LinkedHashMap<Integer, Long> maxValIntegers) {
        MaxValIntegers = new LinkedHashMap<>(maxValIntegers);
    }

    public void setMinValIntegers(LinkedHashMap<Integer, Long> minValIntegers) {
        MinValIntegers = new LinkedHashMap<>(minValIntegers);
    }

    public void setMaxValDoubles(LinkedHashMap<Integer, Double> maxValDoubles) {
        MaxValDoubles = new LinkedHashMap<>(maxValDoubles);
    }

    public void setMinValDoubles(LinkedHashMap<Integer, Double> minValDoubles) {
        MinValDoubles = new LinkedHashMap<>(minValDoubles);
    }

    public void setMaxDates(LinkedHashMap<Integer, LocalDate> maxDates) {
        MaxDates = new LinkedHashMap<>(maxDates);
    }

    public void setMinDates(LinkedHashMap<Integer, LocalDate> minDates) {
        MinDates = new LinkedHashMap<>(minDates);
    }

    public void setIntAttrIndexes(HashMap<Integer, Integer> intAttrIndexes) {
        IntAttrIndexes = new HashMap<>(intAttrIndexes);
    }

    public void setDoubleAttrIndexes(HashMap<Integer, Integer> doubleAttrIndexes) {
        DoubleAttrIndexes = new HashMap<>(doubleAttrIndexes);
    }

    public void setDateAttrIndexes(HashMap<Integer, Integer> dateAttrIndexes) {
        DateAttrIndexes = new HashMap<>(dateAttrIndexes);
    }

    public void setBooleanAttrIndexes(HashMap<Integer, Integer> booleanAttrIndexes) {
        BooleanAttrIndexes = new HashMap<>(booleanAttrIndexes);
    }

    public void setCategoricalAttrIndexes(HashMap<Integer, Integer> categoricalAttrIndexes) {
        CategoricalAttrIndexes = new HashMap<>(categoricalAttrIndexes);
    }

    public void setStringAttrIndexes(HashMap<Integer, Integer> stringAttrIndexes) {
        StringAttrIndexes = new HashMap<>(stringAttrIndexes);
    }

    public void setOtherAttrIndexes(HashMap<Integer, Integer> otherAttrIndexes) {
        OtherAttrIndexes = new HashMap<>(otherAttrIndexes);
    }

    public void setHeaderIndexes(HashMap<String, Integer> headerIndexes) {
        HeaderIndexes = new HashMap<>(headerIndexes);
    }

    public void setLanguage(String data) {
        this.language=data;
    }
}
