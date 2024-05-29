package app;

import data.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class ResponsableInterface {
    private ResponsableOrientation responsable;
    private JFrame frame;
    private OrientationApp app;

    public ResponsableInterface(ResponsableOrientation responsable, JFrame frame, OrientationApp app) {
        this.responsable = responsable;
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

        JButton activerRemplissageBtn = new JButton("Activer Remplissage");
        JButton desactiverRemplissageBtn = new JButton("Désactiver Remplissage");
        JButton consulterVoeuxBtn = new JButton("Consulter Fiches de Vœux");
        JButton lancerOrientationBtn = new JButton("Lancer Orientation");
        JButton voirStatistiquesBtn = new JButton("Voir Statistiques");

        activerRemplissageBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        desactiverRemplissageBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        consulterVoeuxBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        lancerOrientationBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        voirStatistiquesBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        activerRemplissageBtn.addActionListener(e -> changerEtatRemplissage(true));
        desactiverRemplissageBtn.addActionListener(e -> changerEtatRemplissage(false));
        consulterVoeuxBtn.addActionListener(e -> consulterVoeux());
        lancerOrientationBtn.addActionListener(e -> lancerOrientation());
        voirStatistiquesBtn.addActionListener(e -> showVoeuxStatistics());

        centerPanel.add(activerRemplissageBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(desactiverRemplissageBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(consulterVoeuxBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(lancerOrientationBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(voirStatistiquesBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        frame.getContentPane().removeAll();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void changerEtatRemplissage(boolean etat) {
        try {
            JSONObject config = GestionnaireJSON.chargerConfig();
            config.put("remplissageAutorise", etat);
            GestionnaireJSON.mettreAJourConfig(config);
            JOptionPane.showMessageDialog(frame, "État du remplissage des vœux changé !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void consulterVoeux() {
        JFrame voeuxFrame = new JFrame("Fiches de Vœux");
        voeuxFrame.setSize(400, 300);
        DefaultListModel<String> listModel = new DefaultListModel<>();

        JSONArray voeuxArray = GestionnaireJSON.chargerItems("src/voeux.json");
        for (int i = 0; i < voeuxArray.length(); i++) {
            JSONObject voeu = voeuxArray.getJSONObject(i);
            String voeuText = voeu.getInt("id") + ": " + voeu.getString("nom") + " " + voeu.getString("prenom") + " - Vœu: " + voeu.getString("voeux") + " - Statut: " + voeu.getString("status") + " - Accepté: " + voeu.getString("accepte");
            if (voeu.getBoolean("desistement")) {
                voeuText += " - Désisté";
            }
            listModel.addElement(voeuText);
        }

        JList<String> voeuxList = new JList<>(listModel);
        voeuxList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton accepterBtn = new JButton("Accepter");
        JButton refuserBtn = new JButton("Refuser");
        JButton relancerBtn = new JButton("Relancer");

        accepterBtn.addActionListener(e -> {
            String selectedVoeu = voeuxList.getSelectedValue();
            if (selectedVoeu != null) {
                int voeuId = Integer.parseInt(selectedVoeu.split(":")[0]);
                GestionnaireJSON.mettreAJourVoeu(voeuId, "traité", "oui", false, "src/voeux.json");
                JOptionPane.showMessageDialog(voeuxFrame, "Vœu accepté!");
                consulterVoeux();
                voeuxFrame.dispose();
            }
        });

        refuserBtn.addActionListener(e -> {
            String selectedVoeu = voeuxList.getSelectedValue();
            if (selectedVoeu != null) {
                int voeuId = Integer.parseInt(selectedVoeu.split(":")[0]);
                GestionnaireJSON.mettreAJourVoeu(voeuId, "traité", "non", false, "src/voeux.json");
                JOptionPane.showMessageDialog(voeuxFrame, "Vœu refusé!");
                consulterVoeux();
                voeuxFrame.dispose();
            }
        });

        relancerBtn.addActionListener(e -> {
            String selectedVoeu = voeuxList.getSelectedValue();
            if (selectedVoeu != null) {
                int voeuId = Integer.parseInt(selectedVoeu.split(":")[0]);
                GestionnaireJSON.mettreAJourVoeu(voeuId, "en attente", "non", false, "src/voeux.json");
                JOptionPane.showMessageDialog(voeuxFrame, "Vœu relancé!");
                consulterVoeux();
                voeuxFrame.dispose();
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(voeuxList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(accepterBtn);
        buttonPanel.add(refuserBtn);
        buttonPanel.add(relancerBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        voeuxFrame.add(panel);
        voeuxFrame.setVisible(true);
    }
    
    private void showVoeuxStatistics() {
        Map<String, Integer> voeuxCount = GestionnaireJSON.compterVoeuxParOption();
        Map<Integer, Integer> prioriteCount = GestionnaireJSON.compterVoeuxParPriorite();

        // Create dataset for histogram
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : voeuxCount.entrySet()) {
            dataset.addValue(entry.getValue(), "Voeux", entry.getKey());
        }

        // Create histogram
        JFreeChart barChart = ChartFactory.createBarChart(
            "Histogramme des Vœux",
            "Options",
            "Nombre de Vœux",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false);

        // Show histogram
        ChartPanel chartPanel = new ChartPanel(barChart);
        JFrame histogramFrame = new JFrame("Statistiques des Vœux");
        histogramFrame.setSize(800, 600);
        histogramFrame.setContentPane(chartPanel);
        histogramFrame.setVisible(true);

        // Calculate and show percentages
        int totalVoeux = prioriteCount.values().stream().mapToInt(Integer::intValue).sum();
        int premierChoixCount = prioriteCount.getOrDefault(1, 0);
        int deuxiemeChoixCount = prioriteCount.getOrDefault(2, 0);
        int troisiemeChoixCount = prioriteCount.getOrDefault(3, 0);

        double premierChoixPourcentage = (double) premierChoixCount / totalVoeux * 100;
        double deuxiemeChoixPourcentage = (double) deuxiemeChoixCount / totalVoeux * 100;
        double troisiemeChoixPourcentage = (double) troisiemeChoixCount / totalVoeux * 100;

        JOptionPane.showMessageDialog(histogramFrame,
            String.format("Pourcentage des élèves ayant eu leur premier choix: %.2f%%\nDeuxième choix: %.2f%%\nTroisième choix: %.2f%%",
                premierChoixPourcentage, deuxiemeChoixPourcentage, troisiemeChoixPourcentage));
    }


    private void lancerOrientation() {
        JSONArray voeuxArray = GestionnaireJSON.chargerItems("src/voeux.json");
        JSONArray etudiantsArray = GestionnaireJSON.chargerItems("src/etudiants.json");
        JSONArray optionsArray = GestionnaireJSON.chargerOptions();
        Map<String, Integer> optionCapacities = new HashMap<>();

        // Initialiser les capacités des options
        for (int i = 0; i < optionsArray.length(); i++) {
            JSONObject option = optionsArray.getJSONObject(i);
            optionCapacities.put(option.getString("nom"), option.getInt("placesDisponibles"));
        }

        // Charger les étudiants dans une Map pour accès rapide
        Map<Integer, Double> etudiantNotes = new HashMap<>();
        for (int i = 0; i < etudiantsArray.length(); i++) {
            JSONObject etudiant = etudiantsArray.getJSONObject(i);
            etudiantNotes.put(etudiant.getInt("numeroEtu"), etudiant.getDouble("moyenne"));
        }

        // Étape 1 : Trier les vœux par priorité, puis par note décroissante
        ArrayList<JSONObject> voeuxList = new ArrayList<>();
        for (int i = 0; i < voeuxArray.length(); i++) {
            JSONObject voeu = voeuxArray.getJSONObject(i);
            if (!voeu.getBoolean("desistement")) { // Ignorer les voeux désistés
                voeuxList.add(voeu);
            }
        }
        voeuxList.sort((v1, v2) -> {
            int priorityComparison = Integer.compare(v1.getInt("priorite"), v2.getInt("priorite"));
            if (priorityComparison != 0) {
                return priorityComparison;
            }
            return Double.compare(etudiantNotes.get(v2.getInt("numeroEtu")), etudiantNotes.get(v1.getInt("numeroEtu")));
        });

        // Étape 2 : Attribution initiale des options
        Map<Integer, String> assignments = new HashMap<>();
        for (JSONObject voeu : voeuxList) {
            String option = voeu.getString("voeux");
            int studentId = voeu.getInt("numeroEtu");
            if (optionCapacities.get(option) > 0 && !assignments.containsKey(studentId)) {
                assignments.put(studentId, option);
                optionCapacities.put(option, optionCapacities.get(option) - 1);
                voeu.put("status", "attribué");
                voeu.put("accepte", "oui");
            } else {
                voeu.put("status", "en attente");
                voeu.put("accepte", "non");
            }
        }

        // Mettre à jour les priorités des vœux en attente si un désistement est survenu
        updateVoeuxPriorities(voeuxList);

        // Mise à jour des options dans le fichier JSON
        JSONArray updatedOptionsArray = new JSONArray();
        for (String optionName : optionCapacities.keySet()) {
            for (int i = 0; i < optionsArray.length(); i++) {
                JSONObject option = optionsArray.getJSONObject(i);
                if (option.getString("nom").equals(optionName)) {
                    option.put("placesDisponibles", optionCapacities.get(optionName));
                    updatedOptionsArray.put(option);
                    break;
                }
            }
        }
        GestionnaireJSON.mettreAJourOptions(updatedOptionsArray);

        // Mise à jour des vœux dans le fichier JSON
        JSONArray updatedVoeuxArray = new JSONArray(voeuxList);
        GestionnaireJSON.mettreAJourFichier(updatedVoeuxArray, "src/voeux.json");

        JOptionPane.showMessageDialog(frame, "Orientation lancée !");
    }


    private void updateVoeuxPriorities(ArrayList<JSONObject> voeuxList) {
        for (JSONObject voeu : voeuxList) {
            if (voeu.getString("status").equals("désisté")) {
                int desistedPriority = voeu.getInt("priorite");
                for (JSONObject otherVoeu : voeuxList) {
                    if (otherVoeu.getString("nom").equals(voeu.getString("nom")) &&
                        otherVoeu.getString("prenom").equals(voeu.getString("prenom")) &&
                        otherVoeu.getInt("priorite") > desistedPriority) {
                        otherVoeu.put("priorite", otherVoeu.getInt("priorite") - 1);
                    }
                }
            }
        }
    }


    
    public void desisterVoeu(int voeuId) {
        JSONArray voeuxArray = GestionnaireJSON.chargerItems("src/voeux.json");
        for (int i = 0; i < voeuxArray.length(); i++) {
            JSONObject voeu = voeuxArray.getJSONObject(i);
            if (voeu.getInt("id") == voeuId) {
                voeu.put("desistement", true);
                GestionnaireJSON.mettreAJourFichier(voeuxArray, "src/voeux.json");
                GestionnaireJSON.mettreAJourPlacesDisponibles(voeu.getString("voeux"), 1);
                relancerOrientation();
                break;
            }
        }
    }

    private void relancerOrientation() {
        lancerOrientation();
    }
}
