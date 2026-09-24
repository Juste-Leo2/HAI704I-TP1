package cabinet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Interface graphique Swing (Partie C - Bonus).
 * Conforme aux consignes du sujet :
 * 1. Réutilise directement CabinetService sans dupliquer la gestion RMI.
 * 2. Transmet les alertes réseau au fil d'exécution graphique via SwingUtilities.invokeLater.
 */
public class SwingUI extends JFrame 
{
    private final CabinetService service;
    private final JTextArea zoneTexte = new JTextArea(16, 50);

    public SwingUI(CabinetService service) 
    {
        super("Cabinet Vétérinaire - Interface Graphique (Partie C)");
        this.service = service;

        initUI();
    }

    private void initUI() 
    {
        setLayout(new BorderLayout(10, 10));

        // 1. Zone centrale d'affichage
        zoneTexte.setEditable(false);
        zoneTexte.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(new JScrollPane(zoneTexte), BorderLayout.CENTER);

        // 2. Panneau de boutons
        JPanel panneauBoutons = new JPanel(new GridLayout(1, 5, 5, 5));
        JButton btnLister = new JButton("1. Lister");
        JButton btnRechercher = new JButton("2. Rechercher");
        JButton btnAjouter = new JButton("3. Ajouter");
        JButton btnDossier = new JButton("4. Dossier");
        JButton btnAlerte = new JButton("5. Alertes");

        panneauBoutons.add(btnLister);
        panneauBoutons.add(btnRechercher);
        panneauBoutons.add(btnAjouter);
        panneauBoutons.add(btnDossier);
        panneauBoutons.add(btnAlerte);
        add(panneauBoutons, BorderLayout.SOUTH);

        // --- Actions des boutons ---

        // 1. Lister les patients
        btnLister.addActionListener(e -> {
            try 
            {
                List<Animal> patients = service.recupererPatients();
                zoneTexte.setText("=== LISTE DES PATIENTS (" + patients.size() + ") ===\n");
                for (int i = 0; i < patients.size(); i++) 
                {
                    Animal a = patients.get(i);
                    String etat = (a.getDossier() != null) ? a.getDossier().getEtat() : "Aucun";
                    zoneTexte.append(String.format("[%d] %s | Race : %s | Maître : %s | État : %s\n",
                            (i + 1), a.getNom(), a.getRace(), a.getMaitre(), etat));
                }
            } 
            catch (Exception ex) 
            {
                zoneTexte.append("\n[ERREUR] Impossible de lister les patients : " + ex.getMessage() + "\n");
            }
        });

        // 2. Rechercher un patient
        btnRechercher.addActionListener(e -> {
            String nom = JOptionPane.showInputDialog(this, "Nom du patient à rechercher :");
            if (nom != null && !nom.trim().isEmpty()) 
            {
                try 
                {
                    Animal a = service.rechercherPatient(nom.trim());
                    if (a != null) 
                    {
                        String etat = (a.getDossier() != null) ? a.getDossier().getEtat() : "Aucun";
                        zoneTexte.append("\n[PATIENT TROUVÉ] " + a.getNom() + " | Race : " + a.getRace() 
                                         + " | Maître : " + a.getMaitre() + " | État : " + etat + "\n");
                    } 
                    else 
                    {
                        zoneTexte.append("\n[RECHERCHE] Aucun patient trouvé au nom de : " + nom + "\n");
                    }
                } 
                catch (Exception ex) 
                {
                    zoneTexte.append("\n[ERREUR] " + ex.getMessage() + "\n");
                }
            }
        });

        // 3. Ajouter un patient (formulaire complet)
        btnAjouter.addActionListener(e -> {
            JTextField champNom = new JTextField(12);
            JTextField champMaitre = new JTextField(12);
            JTextField champRace = new JTextField(12);
            JTextField champEspece = new JTextField("Chien", 12);
            JTextField champDuree = new JTextField("12", 5);

            JPanel formulaire = new JPanel(new GridLayout(5, 2, 5, 5));
            formulaire.add(new JLabel("Nom du patient :"));
            formulaire.add(champNom);
            formulaire.add(new JLabel("Nom du maître :"));
            formulaire.add(champMaitre);
            formulaire.add(new JLabel("Race :"));
            formulaire.add(champRace);
            formulaire.add(new JLabel("Espèce (ex: Chien, Chat) :"));
            formulaire.add(champEspece);
            formulaire.add(new JLabel("Durée de vie (ans) :"));
            formulaire.add(champDuree);

            int option = JOptionPane.showConfirmDialog(this, formulaire, 
                    "Ajouter un nouveau patient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) 
            {
                String nom = champNom.getText().trim();
                String maitre = champMaitre.getText().trim();
                String race = champRace.getText().trim();
                String espece = champEspece.getText().trim();
                int duree = 10;
                try 
                {
                    duree = Integer.parseInt(champDuree.getText().trim());
                } 
                catch (NumberFormatException ignored) {}

                if (!nom.isEmpty() && !maitre.isEmpty() && !race.isEmpty() && !espece.isEmpty()) 
                {
                    try 
                    {
                        Animal nouveau = service.ajouterPatient(nom, maitre, race, new Espece(espece, duree));
                        zoneTexte.append("\n[SUCCÈS] Patient ajouté côté serveur : " + nouveau.getNom() 
                                         + " | Race : " + race + " | Espèce : " + espece + "\n");
                    } 
                    catch (Exception ex) 
                    {
                        zoneTexte.append("\n[ERREUR] Échec de l'ajout : " + ex.getMessage() + "\n");
                    }
                } 
                else 
                {
                    zoneTexte.append("\n[AVERTISSEMENT] Tous les champs doivent être remplis pour ajouter un patient.\n");
                }
            }
        });

        // 4. Modifier dossier
        btnDossier.addActionListener(e -> {
            JTextField champNom = new JTextField(12);
            JTextField champEtat = new JTextField(15);

            JPanel formulaire = new JPanel(new GridLayout(2, 2, 5, 5));
            formulaire.add(new JLabel("Nom du patient :"));
            formulaire.add(champNom);
            formulaire.add(new JLabel("Nouvel état de santé :"));
            formulaire.add(champEtat);

            int option = JOptionPane.showConfirmDialog(this, formulaire, 
                    "Modifier le dossier médical", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) 
            {
                String nom = champNom.getText().trim();
                String nouvelEtat = champEtat.getText().trim();

                if (!nom.isEmpty() && !nouvelEtat.isEmpty()) 
                {
                    try 
                    {
                        Animal a = service.rechercherPatient(nom);
                        if (a == null) 
                        {
                            zoneTexte.append("\n[ERREUR] Patient introuvable au nom de : " + nom + "\n");
                            return;
                        }
                        service.modifierDossier(a, nouvelEtat);
                        zoneTexte.append("\n[SUCCÈS] Dossier de " + nom + " mis à jour : " + nouvelEtat + "\n");
                    } 
                    catch (Exception ex) 
                    {
                        zoneTexte.append("\n[ERREUR] " + ex.getMessage() + "\n");
                    }
                }
            }
        });

        // 5. Basculer abonnement alertes (avec SwingUtilities.invokeLater)
        btnAlerte.addActionListener(e -> {
            try 
            {
                boolean abonne = service.basculerAbonnement(alerte -> {
                    // Exigence du sujet : mise à jour confiée au fil graphique Swing
                    SwingUtilities.invokeLater(() -> {
                        zoneTexte.append("\n>>> [ALERTE REÇUE DU SERVEUR] " + alerte + "\n");
                    });
                });

                if (abonne) 
                {
                    zoneTexte.append("\n[ALERTES] Client ABONNÉ aux alertes de seuil du serveur.\n");
                } 
                else 
                {
                    zoneTexte.append("\n[ALERTES] Client DÉSABONNÉ des alertes.\n");
                }
            } 
            catch (Exception ex) 
            {
                zoneTexte.append("\n[ERREUR] " + ex.getMessage() + "\n");
            }
        });

        // Gestion de la fermeture propre de la fenêtre
        addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e) 
            {
                service.arreter();
                dispose();
                System.exit(0);
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        zoneTexte.setText("Bienvenue sur l'interface graphique Swing du Cabinet Vétérinaire.\nCliquez sur un bouton pour interagir.\n");
    }
}
