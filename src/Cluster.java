package dominio.clases;
import java.util.*;

public class Cluster {

    private Centroid centroid;

    private HashSet<Usuari> usuaris = new HashSet<>();

    public Centroid getCentroid() {
        return centroid;
    }

    public HashSet<Usuari> getUsuaris() {
        return usuaris;
    }

    public void setCentroid(Centroid centroid) {
        this.centroid = centroid;
    }

    public void setUsuaris(HashSet<Usuari> usuaris) {
        this.usuaris = usuaris;
    }

    public void addUsuari(Usuari u) {
        usuaris.add(u);
    }

}

