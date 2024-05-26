package data;

public class Option {
    private String nom;
    private int placesDisponibles;
    private String parcours;

    public Option(String nom, int placesDisponibles, String parcours) {
        this.nom = nom;
        this.placesDisponibles = placesDisponibles;
        this.parcours = parcours;
    }

    public String getParcours() {
        return parcours;
    }

    public void setParcours(String parcours) {
        this.parcours = parcours;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getPlacesDisponibles() {
        return placesDisponibles;
    }

    public void setPlacesDisponibles(int placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }
}
