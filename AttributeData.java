package dominio.clases;

/* why make it final? */
public class AttributeData {
    private int index;
    private String type;

    public AttributeData() {

    }

    public AttributeData(int index, String type) {
        this.index = index;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }
}
