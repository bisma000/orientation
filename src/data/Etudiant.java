package data;

import java.util.ArrayList;
import java.util.List;

public class Etudiant {
    private String username;
    private String password;
    private String nom;
    private String prenom;
    private double moyenne;
    private List<String> voeux;
    private String optionAssignee;
    private String parcours;
    private double numeroEtu;
    private String mail;

    public Etudiant(String username, String password, String nom, String prenom, double numeroEtu, double moyenne, String parcours, String mail) {
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.moyenne = moyenne;
        this.parcours = parcours;
        this.voeux = new ArrayList<>();
        this.numeroEtu = numeroEtu;
        this.mail = mail;
    }

    // Getters et setters...

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public double getMoyenne() { return moyenne; }
    public void setMoyenne(double moyenne) { this.moyenne = moyenne; }

    public List<String> getVoeux() { return voeux; }
    public void setVoeux(List<String> voeux) { this.voeux = voeux; }

    public String getParcours() { return parcours; }
    public void setParcours(String parcours) { this.parcours = parcours; }

    public double getNumeroEtu() { return numeroEtu; }
    public void setNumeroEtu(double numeroEtu) { this.numeroEtu = numeroEtu; }

    public String getOptionAssignee() { return optionAssignee; }
    public void setOptionAssignee(String optionAssignee) { this.optionAssignee = optionAssignee; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public void ajouterVoeu(String option) {
        voeux.add(option);
    }
}
