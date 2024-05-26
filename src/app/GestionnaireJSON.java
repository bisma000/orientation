package app;

import data.Etudiant;
import data.Option;
import data.ResponsableOrientation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionnaireJSON {

    // Charger les items depuis un fichier JSON
    public static JSONArray chargerItems(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonText.append(line);
            }
            return new JSONArray(jsonText.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    // Charger la configuration depuis un fichier JSON
    public static JSONObject chargerConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader("config.json"))) {
            StringBuilder jsonText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonText.append(line);
            }
            return new JSONObject(jsonText.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    // Mettre à jour le fichier JSON
    public static void mettreAJourFichier(JSONArray itemsArray, String filePath) {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(itemsArray.toString(2));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Mettre à jour la configuration
    public static void mettreAJourConfig(JSONObject config) {
        try (FileWriter file = new FileWriter("config.json")) {
            file.write(config.toString(2));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ajouter un étudiant au fichier JSON
    public static void ajouterEtudiant(Etudiant etudiant, String filePath) {
        JSONArray etudiantsArray = chargerItems(filePath);
        JSONObject nouvelEtudiant = new JSONObject();
        nouvelEtudiant.put("username", etudiant.getUsername());
        nouvelEtudiant.put("password", etudiant.getPassword());
        nouvelEtudiant.put("nom", etudiant.getNom());
        nouvelEtudiant.put("prenom", etudiant.getPrenom());
        nouvelEtudiant.put("numeroEtu", etudiant.getNumeroEtu());
        nouvelEtudiant.put("moyenne", etudiant.getMoyenne());
        nouvelEtudiant.put("parcours", etudiant.getParcours());
        nouvelEtudiant.put("mail", etudiant.getMail());

        etudiantsArray.put(nouvelEtudiant);
        mettreAJourFichier(etudiantsArray, filePath);
    }

    // Charger un étudiant par username
    public static Etudiant chargerEtudiant(String username, String filePath) {
        JSONArray etudiantsArray = chargerItems(filePath);
        for (int i = 0; i < etudiantsArray.length(); i++) {
            JSONObject etudiantJSON = etudiantsArray.getJSONObject(i);
            if (etudiantJSON.getString("username").equals(username)) {
                return new Etudiant(
                        etudiantJSON.getString("username"),
                        etudiantJSON.getString("password"),
                        etudiantJSON.getString("nom"),
                        etudiantJSON.getString("prenom"),
                        etudiantJSON.getDouble("numeroEtu"),
                        etudiantJSON.getDouble("moyenne"),
                        etudiantJSON.getString("parcours"),
                        etudiantJSON.getString("mail")
                );
            }
        }
        return null;
    }

    // Ajouter un responsable au fichier JSON
    public static void ajouterResponsable(ResponsableOrientation responsable, String filePath) {
        JSONArray responsablesArray = chargerItems(filePath);
        JSONObject nouveauResponsable = new JSONObject();
        nouveauResponsable.put("username", responsable.getUsername());
        nouveauResponsable.put("password", responsable.getPassword());
        nouveauResponsable.put("nom", responsable.getNom());
        nouveauResponsable.put("prenom", responsable.getPrenom());
        nouveauResponsable.put("mail", responsable.getMail());

        responsablesArray.put(nouveauResponsable);
        mettreAJourFichier(responsablesArray, filePath);
    }

    // Charger un responsable par username
    public static ResponsableOrientation chargerResponsable(String username, String filePath) {
        JSONArray responsablesArray = chargerItems(filePath);
        for (int i = 0; i < responsablesArray.length(); i++) {
            JSONObject responsableJSON = responsablesArray.getJSONObject(i);
            if (responsableJSON.getString("username").equals(username)) {
                return new ResponsableOrientation(
                        responsableJSON.getString("username"),
                        responsableJSON.getString("password"),
                        responsableJSON.getString("nom"),
                        responsableJSON.getString("prenom"),
                        responsableJSON.getString("mail")
                );
            }
        }
        return null;
    }

    // Authentifier un utilisateur
    public static boolean authentifier(String username, String password, String role, String filePath) {
        JSONArray usersArray = chargerItems(filePath);
        for (int i = 0; i < usersArray.length(); i++) {
            JSONObject userJSON = usersArray.getJSONObject(i);
            if (userJSON.getString("username").equals(username) &&
                userJSON.getString("password").equals(password) &&
                userJSON.getString("role").equals(role)) {
                return true;
            }
        }
        return false;
    }

    // Ajouter un utilisateur au fichier JSON
    public static boolean ajouterUtilisateur(String username, String password, String role, String filePath) {
        JSONArray usersArray = chargerItems(filePath);
        for (int i = 0; i < usersArray.length(); i++) {
            JSONObject userJSON = usersArray.getJSONObject(i);
            if (userJSON.getString("username").equals(username)) {
                return false; // Utilisateur déjà existant
            }
        }
        JSONObject userJSON = new JSONObject();
        userJSON.put("username", username);
        userJSON.put("password", password);
        userJSON.put("role", role);
        usersArray.put(userJSON);
        mettreAJourFichier(usersArray, filePath);
        return true;
    }

    // Ajouter une fiche de voeux au fichier JSON
    public static void ajouterVoeux(JSONObject voeu, String filePath) {
        JSONArray voeuxArray = chargerItems(filePath);
        voeuxArray.put(voeu);
        mettreAJourFichier(voeuxArray, filePath);
    }

 // Mettre à jour un voeu existant dans le fichier JSON
    public static void mettreAJourVoeu(int voeuId, String status, String accepte, String filePath) {
        JSONArray voeuxArray = chargerItems(filePath);

        for (int i = 0; i < voeuxArray.length(); i++) {
            JSONObject voeu = voeuxArray.getJSONObject(i);
            if (voeu.getInt("id") == voeuId) {
                voeu.put("status", status);
                voeu.put("accepte", accepte);

                if (accepte.equals("oui")) {
                    // Réduire le nombre de places disponibles pour l'option
                    String optionName = voeu.getString("voeux");
                    mettreAJourPlacesDisponibles(optionName, -1);
                }
                break;
            }
        }

        mettreAJourFichier(voeuxArray, filePath);
    }
    
 // Mettre à jour les places disponibles pour une option
    public static void mettreAJourPlacesDisponibles(String optionName, int change) {
        List<Option> allOptions = OrientationApp.getAllOptions();
        for (Option option : allOptions) {
            if (option.getNom().equals(optionName)) {
                option.setPlacesDisponibles(option.getPlacesDisponibles() + change);
                mettreAJourOptions(option);
                break;
            }
        }
    }

    // Mettre à jour le fichier JSON pour les options
    public static void mettreAJourOptions(Option updatedOption) {
        try {
            // Charger les options actuelles
            JSONArray optionsArray = new JSONArray();
            for (Option option : OrientationApp.getAllOptions()) {
                JSONObject optionJSON = new JSONObject();
                optionJSON.put("nom", option.getNom());
                optionJSON.put("placesDisponibles", option.getPlacesDisponibles());
                optionJSON.put("parcours", option.getParcours());
                optionsArray.put(optionJSON);
            }

            // Mettre à jour le fichier des options (par exemple, "options.json")
            try (FileWriter file = new FileWriter("options.json")) {
                file.write(optionsArray.toString(2));
                file.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> compterVoeuxParOption() {
        JSONArray voeuxArray = chargerItems("voeux.json");
        Map<String, Integer> voeuxCount = new HashMap<>();
        for (int i = 0; i < voeuxArray.length(); i++) {
            String voeu = voeuxArray.getJSONObject(i).getString("voeux");
            voeuxCount.put(voeu, voeuxCount.getOrDefault(voeu, 0) + 1);
        }
        return voeuxCount;
    }

    public static Map<Integer, Integer> compterVoeuxParPriorite() {
        JSONArray voeuxArray = chargerItems("voeux.json");
        Map<Integer, Integer> voeuxCount = new HashMap<>();
        for (int i = 0; i < voeuxArray.length(); i++) {
            int priorite = voeuxArray.getJSONObject(i).getInt("priorite");
            voeuxCount.put(priorite, voeuxCount.getOrDefault(priorite, 0) + 1);
        }
        return voeuxCount;
    }
    
    


    
    
}
