package cabinet;

import java.util.List;
import java.util.Scanner;

/**
 * Interface utilisateur en ligne de commande (CLI).
 * CONFORME AU SUJET : Aucun import java.rmi.* dans cette classe.
 */
public class ConsoleUI 
{
    private final CabinetService service;
    private final Scanner scanner;

    public ConsoleUI(CabinetService service) 
    {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Démarre la boucle principale d'interaction utilisateur.
     */
    public void demarrer() 
    {
        boolean enCours = true;
        System.out.println("=================================================");
        System.out.println("   BIENVENUE AU CABINET VÉTÉRINAIRE (CLI)       ");
        System.out.println("=================================================");

        while (enCours) 
        {
            afficherMenu();
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine().trim();

            switch (choix) 
            {
                case "1":
                    listerPatients();
                    break;
                case "2":
                    rechercherPatient();
                    break;
                case "3":
                    ajouterPatient();
                    break;
                case "4":
                    modifierDossierPatient();
                    break;
                case "5":
                    gererAbonnement();
                    break;
                case "0":
                    quitter();
                    enCours = false;
                    break;
                default:
                    System.out.println("\n[!] Choix invalide, veuillez saisir un numéro entre 0 et 5.");
                    break;
            }
        }
    }

    private void afficherMenu() 
    {
        System.out.println("\n---------------- MENU PRINCIPAL ----------------");
        System.out.println("1. Consulter la liste des patients");
        System.out.println("2. Rechercher un patient par son nom");
        System.out.println("3. Créer et ajouter un nouveau patient");
        System.out.println("4. Consulter et modifier le dossier de suivi d'un patient");
        System.out.println("5. S'abonner / Se désabonner des alertes de seuil (Actuel : " 
                           + (service.estAbonne() ? "ABONNÉ" : "DÉSABONNÉ") + ")");
        System.out.println("0. Quitter le client");
        System.out.println("------------------------------------------------");
    }

    private void listerPatients() 
    {
        System.out.println("\n--- LISTE DES PATIENTS DU CABINET ---");
        try 
        {
            List<Animal> patients = service.recupererPatients();
            if (patients == null || patients.isEmpty()) 
            {
                System.out.println("Aucun patient n'est actuellement enregistré dans le cabinet.");
                return;
            }

            System.out.println("Nombre total de patients : " + patients.size());
            for (int i = 0; i < patients.size(); i++) 
            {
                Animal a = patients.get(i);
                afficherDetailsAnimal(i + 1, a);
            }
        } 
        catch (Exception e) 
        {
            System.out.println("[ERREUR RÉSEAU] Impossible de récupérer la liste des patients : " + e.getMessage());
        }
    }

    private void rechercherPatient() 
    {
        System.out.println("\n--- RECHERCHE D'UN PATIENT ---");
        System.out.print("Nom du patient à rechercher : ");
        String nom = scanner.nextLine().trim();

        if (nom.isEmpty()) 
        {
            System.out.println("[!] Le nom ne peut pas être vide.");
            return;
        }

        try 
        {
            Animal a = service.rechercherPatient(nom);
            if (a != null) 
            {
                System.out.println("\nPatient trouvé avec succès !");
                afficherDetailsAnimal(1, a);
            } 
            else 
            {
                System.out.println("\nAucun patient trouvé au nom de : " + nom);
            }
        } 
        catch (Exception e) 
        {
            System.out.println("[ERREUR RÉSEAU] Erreur lors de la recherche : " + e.getMessage());
        }
    }

    private void ajouterPatient() 
    {
        System.out.println("\n--- AJOUT D'UN NOUVEAU PATIENT ---");
        System.out.print("Nom du patient : ");
        String nom = scanner.nextLine().trim();

        System.out.print("Nom du propriétaire (maître) : ");
        String maitre = scanner.nextLine().trim();

        System.out.print("Race de l'animal : ");
        String race = scanner.nextLine().trim();

        System.out.print("Nom de l'espèce (ex: Chien, Chat...) : ");
        String nomEspece = scanner.nextLine().trim();

        System.out.print("Durée de vie moyenne estimée (années) : ");
        String dureeVieStr = scanner.nextLine().trim();
        int dureeVie = 10;
        try 
        {
            if (!dureeVieStr.isEmpty()) 
            {
                dureeVie = Integer.parseInt(dureeVieStr);
            }
        } 
        catch (NumberFormatException e) 
        {
            System.out.println("[!] Saisie invalide pour la durée de vie, valeur par défaut appliquée : 10 ans.");
        }

        if (nom.isEmpty() || maitre.isEmpty() || race.isEmpty() || nomEspece.isEmpty()) 
        {
            System.out.println("[!] Tous les champs d'identification sont requis. Ajout annulé.");
            return;
        }

        try 
        {
            Espece espece = new Espece(nomEspece, dureeVie);
            Animal nouveau = service.ajouterPatient(nom, maitre, race, espece);
            System.out.println("\n[SUCCÈS] Le patient '" + nouveau.getNom() + "' a été ajouté au cabinet côté serveur !");
        } 
        catch (Exception e) 
        {
            System.out.println("[ERREUR RÉSEAU] Échec de l'ajout du patient : " + e.getMessage());
        }
    }

    private void modifierDossierPatient() 
    {
        System.out.println("\n--- MODIFIER LE DOSSIER DE SUIVI ---");
        System.out.print("Nom du patient dont vous voulez modifier le dossier : ");
        String nom = scanner.nextLine().trim();

        try 
        {
            Animal a = service.rechercherPatient(nom);
            if (a == null) 
            {
                System.out.println("[!] Patient introuvable au nom de : " + nom);
                return;
            }

            DossierSuivi dossier = a.getDossier();
            String etatActuel = (dossier != null) ? dossier.getEtat() : "Aucun dossier";
            System.out.println("État de santé actuel : " + etatActuel);

            System.out.print("Nouvel état de santé : ");
            String nouvelEtat = scanner.nextLine().trim();

            if (nouvelEtat.isEmpty()) 
            {
                System.out.println("[!] Modification annulée (saisie vide).");
                return;
            }

            service.modifierDossier(a, nouvelEtat);
            System.out.println("\n[SUCCÈS] Dossier mis à jour à distance avec succès !");
            System.out.println("Nouvel état enregistré : " + a.getDossier().getEtat());
        } 
        catch (Exception e) 
        {
            System.out.println("[ERREUR RÉSEAU] Échec de la modification du dossier : " + e.getMessage());
        }
    }

    private void gererAbonnement() 
    {
        try 
        {
            boolean nouvelEtat = service.basculerAbonnement();
            if (nouvelEtat) 
            {
                System.out.println("\n[SUCCÈS] Vous êtes maintenant ABONNÉ aux alertes de seuil du cabinet.");
            } 
            else 
            {
                System.out.println("\n[SUCCÈS] Vous êtes maintenant DÉSABONNÉ des alertes.");
            }
        } 
        catch (Exception e) 
        {
            System.out.println("[ERREUR RÉSEAU] Impossible de modifier l'abonnement : " + e.getMessage());
        }
    }

    private void quitter() 
    {
        System.out.println("\nFermeture du client en cours...");
        service.arreter();
        System.out.println("Au revoir !");
    }

    private void afficherDetailsAnimal(int index, Animal a) 
    {
        try 
        {
            String nom = a.getNom();
            String race = a.getRace();
            String maitre = a.getMaitre();
            Espece e = a.getEspece();
            String especeStr = (e != null) ? (e.getNom() + " (durée de vie : " + e.getDureeVie() + " ans)") : "Inconnue";
            DossierSuivi d = a.getDossier();
            String etatDossier = (d != null) ? d.getEtat() : "Aucun";

            System.out.println("  [" + index + "] " + nom + " | Race : " + race 
                               + " | Espèce : " + especeStr 
                               + " | Maître : " + maitre 
                               + " | État suivi : " + etatDossier);
        } 
        catch (Exception ex) 
        {
            System.out.println("  [" + index + "] Erreur de lecture de l'animal : " + ex.getMessage());
        }
    }
}
