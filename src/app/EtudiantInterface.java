package app;

import data.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class EtudiantInterface {
    private Etudiant etudiant;
    private JFrame frame;
    private OrientationApp app;

    public EtudiantInterface(Etudiant etudiant, JFrame frame, OrientationApp app) {
        this.etudiant = etudiant;
        this.frame = frame;
        this.app = app;
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton logoutBtn = new JButton("Déconnexion");
        logoutBtn.addActionListener(e -> app.logout());
        topPanel.add(logoutBtn, BorderLayout.EAST);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JButton consulterOptionsBtn = new JButton("Consulter Options");
        JButton remplirVoeuxBtn = new JButton("Remplir Fiche de Voeux");
        JButton consulterResultatsBtn = new JButton("Consulter Résultats");

        consulterOptionsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        remplirVoeuxBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        consulterResultatsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        consulterOptionsBtn.addActionListener(e -> consulterOptions());
        remplirVoeuxBtn.addActionListener(e -> remplirVoeux());
        consulterResultatsBtn.addActionListener(e -> consulterResultats());

        centerPanel.add(consulterOptionsBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(remplirVoeuxBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(consulterResultatsBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        frame.getContentPane().removeAll();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void consulterOptions() {
        JFrame optionsFrame = new JFrame("Options Disponibles");
        optionsFrame.setSize(400, 300);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        
        for (Option option : OrientationApp.getAllOptions()) {
            if (option.getParcours().equals(etudiant.getParcours())) {
                listModel.addElement(option.getNom() + " - Places: " + option.getPlacesDisponibles());
            }
        }
        
        JList<String> optionsList = new JList<>(listModel);
        optionsFrame.add(new JScrollPane(optionsList));
        optionsFrame.setVisible(true);
    }

    private void consulterResultats() {
        JFrame resultatsFrame = new JFrame("Résultats d'Orientation");
        resultatsFrame.setSize(400, 300);
        DefaultListModel<String> listModel = new DefaultListModel<>();

        JSONArray voeuxArray = GestionnaireJSON.chargerItems("src/voeux.json");
        for (int i = 0; i < voeuxArray.length(); i++) {
            JSONObject voeu = voeuxArray.getJSONObject(i);
            if (voeu.getString("nom").equals(etudiant.getNom()) && voeu.getString("prenom").equals(etudiant.getPrenom())) {
                String voeuText = "Vœu: " + voeu.getString("voeux") + " - Statut: " + voeu.getString("status") + " - Accepté: " + voeu.getString("accepte");
                if (voeu.getBoolean("desistement")) {
                    voeuText += " - Désisté";
                }
                listModel.addElement(voeu.getInt("id") + ": " + voeuText);
            }
        }

        JList<String> voeuxList = new JList<>(listModel);
        voeuxList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resultatsFrame.add(new JScrollPane(voeuxList), BorderLayout.CENTER);

        JButton desistBtn = new JButton("Se désister des vœux sélectionnés");     /// désistement 
        desistBtn.addActionListener(e -> {
            ArrayList<String> selectedVoeux = (ArrayList<String>) voeuxList.getSelectedValuesList();  // on cherche la priorité du voeux 
            for (String selectedVoeu : selectedVoeux) {
                int voeuId = Integer.parseInt(selectedVoeu.split(":")[0]);
                JSONObject voeu = GestionnaireJSON.getVoeuById(voeuId, "src/voeux.json");
                int priorite = voeu.getInt("priorite");
                GestionnaireJSON.mettreAJourVoeu(voeuId, "désisté", "non", true, "src/voeux.json");
                GestionnaireJSON.updateVoeuxPriorities(etudiant.getNom(), etudiant.getPrenom(), priorite, "src/voeux.json");
            }
            JOptionPane.showMessageDialog(resultatsFrame, "Désistement réussi!");
            resultatsFrame.dispose();
            consulterResultats(); // Refresh the results
        });

        resultatsFrame.add(desistBtn, BorderLayout.SOUTH);
        resultatsFrame.setVisible(true);
    }


    private void remplirVoeux() {
        if (!isRemplissageAutorise()) {
            JOptionPane.showMessageDialog(frame, "Le remplissage des vœux est désactivé !");
            return;
        }

        JFrame voeuxFrame = new JFrame("Remplir Fiche de Voeux");
        voeuxFrame.setSize(500, 200);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // Label pour Vœux Disponibles
        panel.add(new JLabel("Vœux Disponibles"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("Priorité"), gbc);
        
        // Liste des vœux disponibles
        gbc.gridx = 0;
        gbc.gridy = 1;
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JSONArray optionsArray = GestionnaireJSON.chargerOptions();
        for (int i = 0; i < optionsArray.length(); i++) {
            JSONObject option = optionsArray.getJSONObject(i);
            if (option.getInt("placesDisponibles") > 0 && option.getString("parcours").equals(etudiant.getParcours())) {
                listModel.addElement(option.getString("nom"));
            }
        }
        JList<String> voeuxList = new JList<>(listModel);
        voeuxList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane voeuxScrollPane = new JScrollPane(voeuxList);
        voeuxScrollPane.setPreferredSize(new Dimension(200, 100));
        panel.add(voeuxScrollPane, gbc);

        // Liste déroulante pour la priorité
        gbc.gridx = 1;
        gbc.gridy = 1;
        JComboBox<Integer> priorityBox = new JComboBox<>();
        for (int i = 1; i <= 3; i++) {
            priorityBox.addItem(i);
        }
        priorityBox.setPreferredSize(new Dimension(50, 30));
        panel.add(priorityBox, gbc);

        // Désactiver les priorités déjà utilisées
        ArrayList<Integer> usedPriorities = getUsedPriorities();
        for (Integer usedPriority : usedPriorities) {
            priorityBox.removeItem(usedPriority);
        }

        // Bouton pour ajouter un vœu
        gbc.gridx = 2;
        gbc.gridy = 1;
        JButton ajouterVoeuBtn = new JButton("Ajouter Vœu");
        ajouterVoeuBtn.setPreferredSize(new Dimension(120, 30));
        ajouterVoeuBtn.addActionListener(e -> {
            String selectedVoeu = voeuxList.getSelectedValue();
            int priority = (int) priorityBox.getSelectedItem();

            if (selectedVoeu != null && !voeuDejaExistant(selectedVoeu)) {
                saveVoeux(selectedVoeu, priority);
                priorityBox.removeItem(priority);
                if (priorityBox.getItemCount() == 0) {
                    ajouterVoeuBtn.setEnabled(false);
                }
                JOptionPane.showMessageDialog(voeuxFrame, "Vœu ajouté avec succès!");
            } else {
                JOptionPane.showMessageDialog(voeuxFrame, "Vous avez déjà sélectionné ce vœu ou atteint la limite de vœux !");
            }
        });
        panel.add(ajouterVoeuBtn, gbc);

        voeuxFrame.add(panel);
        voeuxFrame.setVisible(true);
    }

    
    private void addBackPriority(int priority) {
        ArrayList<Integer> usedPriorities = getUsedPriorities();
        if (!usedPriorities.contains(priority)) {
            usedPriorities.add(priority);
        }
    }

    private ArrayList<Integer> getUsedPriorities() {
        ArrayList<Integer> usedPriorities = new ArrayList<>();
        JSONArray voeuxArray = GestionnaireJSON.chargerItems("src/voeux.json");
        for (int i = 0; i < voeuxArray.length(); i++) {
            JSONObject v = voeuxArray.getJSONObject(i);
            if (v.getString("nom").equals(etudiant.getNom()) && v.getString("prenom").equals(etudiant.getPrenom()) && !v.getBoolean("desistement")) {
                usedPriorities.add(v.getInt("priorite"));
            }
        }
        return usedPriorities;
    }

    private boolean voeuDejaExistant(String voeu, int priorite) {
        JSONArray voeuxArray = GestionnaireJSON.chargerItems("src/voeux.json");
        for (int i = 0; i < voeuxArray.length(); i++) {
            JSONObject v = voeuxArray.getJSONObject(i);
            if (v.getString("nom").equals(etudiant.getNom()) && 
                v.getString("prenom").equals(etudiant.getPrenom()) && 
                v.getString("voeux").equals(voeu) && 
                v.getInt("priorite") == priorite) {
                return true;
            }
        }
        return false;
    }

    private boolean voeuDejaExistant(String voeu) {
        JSONArray voeuxArray = GestionnaireJSON.chargerItems("src/voeux.json");
        for (int i = 0; i < voeuxArray.length(); i++) {
            JSONObject v = voeuxArray.getJSONObject(i);
            if (v.getString("nom").equals(etudiant.getNom()) && 
                v.getString("prenom").equals(etudiant.getPrenom()) && 
                v.getString("voeux").equals(voeu)) {
                return true;
            }
        }
        return false;
    }

    private void saveVoeux(String selectedVoeu, int priority) {
        try {
            JSONArray voeuxArray = GestionnaireJSON.chargerItems("src/voeux.json");
            JSONObject config = GestionnaireJSON.chargerConfig();
            int currentId = config.getInt("currentId");

            // Vérifiez si un vœu avec cette priorité existe déjà
            for (int i = 0; i < voeuxArray.length(); i++) {
                JSONObject voeu = voeuxArray.getJSONObject(i);
                if (voeu.getString("nom").equals(etudiant.getNom()) && 
                    voeu.getString("prenom").equals(etudiant.getPrenom()) && 
                    voeu.getInt("priorite") == priority && !voeu.getBoolean("desistement")) {
                    // Si oui, mettez à jour ce vœu
                    voeu.put("voeux", selectedVoeu);
                    GestionnaireJSON.mettreAJourFichier(voeuxArray, "src/voeux.json");
                    return;
                }
            }

            // Sinon, ajoutez un nouveau vœu
            JSONObject newVoeu = new JSONObject();
            newVoeu.put("id", currentId + 1);
            newVoeu.put("nom", etudiant.getNom());
            newVoeu.put("prenom", etudiant.getPrenom());
            newVoeu.put("numeroEtu", etudiant.getNumeroEtu());
            newVoeu.put("parcours", etudiant.getParcours());
            newVoeu.put("voeux", selectedVoeu);
            newVoeu.put("priorite", priority);
            newVoeu.put("status", "en attente");
            newVoeu.put("accepte", "non");
            newVoeu.put("desistement", false);

            voeuxArray.put(newVoeu);
            GestionnaireJSON.mettreAJourFichier(voeuxArray, "src/voeux.json");

            // Mettre à jour currentId dans config
            config.put("currentId", currentId + 1);
            GestionnaireJSON.mettreAJourConfig(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isRemplissageAutorise() {
        try {
            JSONObject config = GestionnaireJSON.chargerConfig();
            return config.getBoolean("remplissageAutorise");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
