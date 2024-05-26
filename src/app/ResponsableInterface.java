package app;

import data.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

        JSONArray voeuxArray = GestionnaireJSON.chargerItems("voeux.json");
        for (int i = 0; i < voeuxArray.length(); i++) {
            JSONObject voeu = voeuxArray.getJSONObject(i);
            listModel.addElement(voeu.getInt("id") + ": " + voeu.getString("nom") + " " + voeu.getString("prenom") + " - Vœu: " + voeu.getString("voeux") + " - Statut: " + voeu.getString("status") + " - Accepté: " + voeu.getString("accepte"));
        }

        JList<String> voeuxList = new JList<>(listModel);
        voeuxList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton accepterBtn = new JButton("Accepter");
        JButton refuserBtn = new JButton("Refuser");

        accepterBtn.addActionListener(e -> {
            String selectedVoeu = voeuxList.getSelectedValue();
            if (selectedVoeu != null) {
                int voeuId = Integer.parseInt(selectedVoeu.split(":")[0]);
                GestionnaireJSON.mettreAJourVoeu(voeuId, "traité", "oui", "voeux.json");
                JOptionPane.showMessageDialog(voeuxFrame, "Vœu accepté!");
                consulterVoeux();
                voeuxFrame.dispose();
            }
        });

        refuserBtn.addActionListener(e -> {
            String selectedVoeu = voeuxList.getSelectedValue();
            if (selectedVoeu != null) {
                int voeuId = Integer.parseInt(selectedVoeu.split(":")[0]);
                GestionnaireJSON.mettreAJourVoeu(voeuId, "traité", "non", "voeux.json");
                JOptionPane.showMessageDialog(voeuxFrame, "Vœu refusé!");
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
        JSONArray voeuxArray = GestionnaireJSON.chargerItems("voeux.json");
        JSONArray etudiantsArray = GestionnaireJSON.chargerItems("etudiants.json");
        Map<String, Integer> optionCapacities = new HashMap<>();

        // Initialiser les capacités des options
        for (Option option : OrientationApp.getAllOptions()) {
            optionCapacities.put(option.getNom(), option.getPlacesDisponibles());
        }

        // Parcours des voeux pour l'orientation
        for (int i = 0; i < voeuxArray.length(); i++) {
            JSONObject voeu = voeuxArray.getJSONObject(i);
            int etudiantId = voeu.getInt("id_etudiant");
            String option = voeu.getString("voeux");

            // Vérifier si l'étudiant n'a pas déjà été attribué
            if (!aDejaEteAssigné(etudiantId, voeuxArray)) {
                // Vérifier si l'option a encore des places disponibles
                if (optionCapacities.get(option) > 0) {
                    // Assigner l'option à l'étudiant
                    GestionnaireJSON.mettreAJourVoeu(voeu.getInt("id"), "traité", "oui", "voeux.json");
                    optionCapacities.put(option, optionCapacities.get(option) - 1);
                }
            }
        }
        JOptionPane.showMessageDialog(frame, "Orientation effectuée avec succès !");
    }

    

    // Vérifie si l'étudiant a déjà été attribué à une option dans les voeux
    private boolean aDejaEteAssigné(int etudiantId, JSONArray voeuxArray) {
        for (int i = 0; i < voeuxArray.length(); i++) {
            JSONObject voeu = voeuxArray.getJSONObject(i);
            if (voeu.getInt("id_etudiant") == etudiantId && voeu.getString("traité").equals("oui")) {
                return true;
            }
        }
        return false;
    }

}
