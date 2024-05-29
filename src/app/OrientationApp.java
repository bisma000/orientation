package app;

import data.*;
import app.GestionnaireJSON;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrientationApp {
    private JFrame frame;
    private List<Etudiant> etudiants;
    private static List<Option> optionsMI;
    private static List<Option> optionsMF;
    private static List<Option> optionsGI;
    private static final String USERS_FILE = "src/users.json";
    private static final String ETUDIANTS_FILE = "src/etudiants.json";
    private static final String RESPONSABLES_FILE = "src/responsables.json";
    private static final String VOEUX_FILE = "src/voeux.json";
    private static final String CONFIG_FILE = "src/config.json";

    public OrientationApp() {
        etudiants = new ArrayList<>();
        optionsMI = new ArrayList<>();
        optionsMF = new ArrayList<>();
        optionsGI = new ArrayList<>();
        initOptions();
        initUI();
    }

    private void initOptions() {
        JSONArray optionsArray = GestionnaireJSON.chargerItems("src/options.json");

        for (int i = 0; i < optionsArray.length(); i++) {
            JSONObject optionJSON = optionsArray.getJSONObject(i);
            Option option = new Option(
                    optionJSON.getString("nom"),
                    optionJSON.getInt("placesDisponibles"),
                    optionJSON.getString("parcours")
            );

            switch (option.getParcours()) {
                case "MI":
                    optionsMI.add(option);
                    break;
                case "MF":
                    optionsMF.add(option);
                    break;
                case "GI":
                    optionsGI.add(option);
                    break;
            }
        }
    }

    
    public void logout() {
        showInitialPanel();
    }

    public static List<Option> getAllOptions() {
        List<Option> allOptions = new ArrayList<>();
        allOptions.addAll(optionsMI);
        allOptions.addAll(optionsMF);
        allOptions.addAll(optionsGI);
        return allOptions;
    }

    private void initUI() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Application d'Orientation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        // Logo 
        
        ImageIcon logoIcon = new ImageIcon("/images/logo.png");
        frame.setIconImage(logoIcon.getImage());

        UIManager.put("Button.background", Color.decode("#0C77E0"));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 16));
        UIManager.put("Panel.background", Color.decode("#E0E0E0"));
        UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("PasswordField.font", new Font("Arial", Font.PLAIN, 14));

        showInitialPanel();
 

        frame.setVisible(true);
    }


    private void showInitialPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Créer un compte");

        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> showLoginDialog());
        registerBtn.addActionListener(e -> showRegisterDialog());

        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(loginBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(registerBtn);

        frame.getContentPane().removeAll();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void showLoginDialog() {
        JDialog loginDialog = new JDialog(frame, "Login", true);
        loginDialog.setSize(300, 250);
        loginDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"etudiant", "responsable"});
        JButton loginBtn = new JButton("Login");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        loginDialog.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        loginDialog.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginDialog.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        loginDialog.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginDialog.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        loginDialog.add(roleBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleBox.getSelectedItem();
            
           

            if (GestionnaireJSON.authentifier(username, password, role, USERS_FILE)) {
            	
                loginDialog.dispose();
                if ("etudiant".equals(role)) {
                	
                    Etudiant etudiant = GestionnaireJSON.chargerEtudiant(username, ETUDIANTS_FILE);
                    
                    showEtudiantInterface(etudiant);
                } else if ("responsable".equals(role)) {
                	
                    ResponsableOrientation responsable = GestionnaireJSON.chargerResponsable(username, RESPONSABLES_FILE);
                    showResponsableInterface(responsable);
                }
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Login échoué!");
            }
        });
        loginDialog.add(loginBtn, gbc);

        loginDialog.setVisible(true);
    }

    private void showRegisterDialog() {
        JDialog registerDialog = new JDialog(frame, "Créer un compte", true);
        registerDialog.setSize(400, 600);
        registerDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"etudiant", "responsable"});
        JButton registerBtn = new JButton("Créer un compte");

        JTextField nomField = new JTextField(15);
        JTextField prenomField = new JTextField(15);
        JTextField numeroEtuField = new JTextField(15);
        JTextField moyenneField = new JTextField(15);
        JComboBox<String> parcoursBox = new JComboBox<>(new String[]{"MI", "MF", "GI"});
        JTextField mailField = new JTextField(15);

        numeroEtuField.setVisible(true);
        moyenneField.setVisible(true);
        parcoursBox.setVisible(true);
        mailField.setVisible(true);

        roleBox.addActionListener(e -> {
            String role = (String) roleBox.getSelectedItem();
            boolean isStudent = "etudiant".equals(role);
            numeroEtuField.setVisible(isStudent);
            moyenneField.setVisible(isStudent);
            parcoursBox.setVisible(isStudent);
            mailField.setVisible(true);
            registerDialog.pack();
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        registerDialog.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        registerDialog.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        registerDialog.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        registerDialog.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        registerDialog.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        registerDialog.add(roleBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        registerDialog.add(new JLabel("Nom:"), gbc);

        gbc.gridx = 1;
        registerDialog.add(nomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        registerDialog.add(new JLabel("Prénom:"), gbc);

        gbc.gridx = 1;
        registerDialog.add(prenomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        registerDialog.add(new JLabel("Numéro Étudiant:"), gbc);

        gbc.gridx = 1;
        registerDialog.add(numeroEtuField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        registerDialog.add(new JLabel("Moyenne:"), gbc);

        gbc.gridx = 1;
        registerDialog.add(moyenneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        registerDialog.add(new JLabel("Parcours:"), gbc);

        gbc.gridx = 1;
        registerDialog.add(parcoursBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        registerDialog.add(new JLabel("Mail:"), gbc);

        gbc.gridx = 1;
        registerDialog.add(mailField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        registerBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleBox.getSelectedItem();
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String parcours = (String) parcoursBox.getSelectedItem();
            String mail = mailField.getText();

            if (username.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty() || (role.equals("etudiant") && (numeroEtuField.getText().isEmpty() || moyenneField.getText().isEmpty() || parcours.isEmpty())) || (role.equals("responsable") && mail.isEmpty())) {
                JOptionPane.showMessageDialog(registerDialog, "Tous les champs doivent être remplis!");
                return;
            }

            double numeroEtu = 0;
            double moyenne = 0;

            if ("etudiant".equals(role)) {
                try {
                    numeroEtu = Double.parseDouble(numeroEtuField.getText());
                    moyenne = Double.parseDouble(moyenneField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(registerDialog, "Veuillez entrer des valeurs numériques valides pour le numéro étudiant et la moyenne.");
                    return;
                }

                if (GestionnaireJSON.numeroEtudiantExiste(numeroEtu, ETUDIANTS_FILE)) {
                    JOptionPane.showMessageDialog(registerDialog, "Le numéro étudiant existe déjà!");
                    return;
                }
            }

            if (!GestionnaireJSON.isUsernameUnique(username, USERS_FILE)) {
                JOptionPane.showMessageDialog(registerDialog, "Le nom d'utilisateur existe déjà!");
                return;
            }

            if (GestionnaireJSON.ajouterUtilisateur(username, password, role, USERS_FILE)) {
                if ("etudiant".equals(role)) {
                    Etudiant etudiant = new Etudiant(
                        username, password,
                        nom, prenom,
                        numeroEtu, moyenne,
                        parcours, mail
                    );
                    GestionnaireJSON.ajouterEtudiant(etudiant, ETUDIANTS_FILE);
                    JOptionPane.showMessageDialog(registerDialog, "Compte étudiant créé avec succès!");
                    registerDialog.dispose();
                } else if ("responsable".equals(role)) {
                    ResponsableOrientation responsable = new ResponsableOrientation(
                        username, password,
                        nom, prenom,
                        mail
                    );
                    GestionnaireJSON.ajouterResponsable(responsable, RESPONSABLES_FILE);
                    JOptionPane.showMessageDialog(registerDialog, "Compte responsable créé avec succès!");
                    registerDialog.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(registerDialog, "Échec de la création du compte!");
            }
        });
        registerDialog.add(registerBtn, gbc);

        registerDialog.pack();
        registerDialog.setVisible(true);
    }





    private void showEtudiantInterface(Etudiant etudiant) {
        frame.getContentPane().removeAll();
        new EtudiantInterface(etudiant, frame, this);
        frame.revalidate();
        frame.repaint();
    }

    private void showResponsableInterface(ResponsableOrientation responsable) {
        frame.getContentPane().removeAll();
        new ResponsableInterface(responsable, frame, this);
        frame.revalidate();
        frame.repaint();
    }


    public static void main(String[] args) {
        new OrientationApp();
    }
}
