package data;

import java.util.List;

public class ResponsableOrientation extends User {
    private boolean remplissageOuvert;
    private String nom;
    private String prenom;
    private String mail;
    private String motdepasse;

    public ResponsableOrientation(String username, String password, String nom, String prenom, String mail) {
        super(username, password, "responsable");
        this.remplissageOuvert = false;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.motdepasse = password;
    }

    public boolean isRemplissageOuvert() {
        return remplissageOuvert;
    }

    public void setRemplissageOuvert(boolean remplissageOuvert) {
        this.remplissageOuvert = remplissageOuvert;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMotdepasse() {
        return motdepasse;
    }

    public void setMotdepasse(String motdepasse) {
        this.motdepasse = motdepasse;
    }

    public void activerRemplissage() {
        remplissageOuvert = true;
    }

    public void desactiverRemplissage() {
        remplissageOuvert = false;
    }

    public void lancerOrientation(List<Etudiant> etudiants, List<Option> options) {
        // Implémentation de l'algorithme d'orientation
    }
}
