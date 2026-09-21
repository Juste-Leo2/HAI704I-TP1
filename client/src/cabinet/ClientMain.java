package cabinet;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain
{
    private ClientMain(){}

    public static void main(String[] args)
    {
        String host = (args.length < 1) ? null : args[0];

        try
        {
            //////////////////////////////////A1/////////////////////////////////////// 
            Registry registry = LocateRegistry.getRegistry(host);

            //A1 juste animal
            //Animal stub = (Animal) registry.lookup("Animal");

            //A4 Generalisation
            CabinetVeterinaire cabinet = (CabinetVeterinaire) registry.lookup("CabinetVeterinaire");
            Animal stub = cabinet.rechercherPatient("Opaline");

            System.out.println("Nom de l'animal : " + stub.getNom());
            System.out.println("Classe du stub : " + stub.getClass().getName());
            System.out.println("Est-ce un Proxy dynamique ? " + java.lang.reflect.Proxy.isProxyClass(stub.getClass()));
            ///////////////////////////////////////////////////////////////////////////

            //////////////////////////////////A2/////////////////////////////////////// 
            Espece e = stub.getEspece();
            System.out.println("Espèce reçue : " + e.getNom()); // "Chien"

            e.setNom("Chat");
            System.out.println("Espèce modifiée localement : " + e.getNom()); // "Chat"

            Espece e2 = stub.getEspece();
            System.out.println("Espèce redemandée au serveur : " + e2.getNom());


            System.out.println("HashCode e  : " + System.identityHashCode(e));
            System.out.println("HashCode e2 : " + System.identityHashCode(e2));
            ///////////////////////////////////////////////////////////////////////////

            //////////////////////////////////A3/////////////////////////////////////// 
            DossierSuivi dossier = stub.getDossier();
            System.out.println("État initial du dossier : " + dossier.getEtat()); 
            dossier.setEtat("En convalescence (patte cassée)");
            System.out.println("Dossier modifié par le client !");
            DossierSuivi dossier2 = stub.getDossier();
            System.out.println("État relu depuis le serveur : " + dossier2.getEtat()); 

            System.out.println("Classe du dossier : " + dossier.getClass().getName());
            System.out.println("Est-ce un Proxy RMI ? " + java.lang.reflect.Proxy.isProxyClass(dossier.getClass()));
            ///////////////////////////////////////////////////////////////////////////

            //////////////////////////////////A4/////////////////////////////////////// 
            System.out.println("Patients dans le cabinet : " + cabinet.getPatients().size());
            ///////////////////////////////////////////////////////////////////////////

            //////////////////////////////////A5/////////////////////////////////////// 
            Animal medor = cabinet.ajouterPatient("Sora", "Paul", "husky", new Espece("Chien", 12));
            System.out.println("Patients dans le cabinet : " + cabinet.getPatients().size());

            Animal trouve = cabinet.rechercherPatient("Sora");
            System.out.println("Patient retrouvé par recherche : " + trouve.getNom());
            ///////////////////////////////////////////////////////////////////////////

            //////////////////////////////////A6/////////////////////////////////////// 
            // 1. Instancier l'observateur et s'abonner aux alertes
            CabinetObserver obs = new ClientObserverImpl();
            cabinet.sAbonner(obs);
            System.out.println("Client abonné aux alertes du cabinet !");

            // 2. Ajouter un 3eme patient (Rex) pour franchir le seuil 3 et declencher l'alerte
            Animal patient3 = cabinet.ajouterPatient("Rex", "Sophie", "Berger Allemand", new Espece("Chien", 11));
            System.out.println("Patients dans le cabinet : " + cabinet.getPatients().size());
            ///////////////////////////////////////////////////////////////////////////

            //////////////////////////////////A7/////////////////////////////////////// 
            System.out.println("\n--- Test A7 : Envoi d'une instance de Chien (partagée dans common) ---");
            Animal patientChien = cabinet.ajouterPatient("Rocky", "Alice", "Labrador", new Chien());
            System.out.println("Patient ajouté avec succès : " + patientChien.getNom() + " (Espèce : " + patientChien.getEspece().getNom() + ")");
            System.out.println("Patients dans le cabinet : " + cabinet.getPatients().size());
            ///////////////////////////////////////////////////////////////////////////

            //////////////////////////////////A8/////////////////////////////////////// 
            System.out.println("\n=======================================================");
            System.out.println("  Lancement de l'application interactive A8 (3 tiers)  ");
            System.out.println("=======================================================");
            CabinetService service = new CabinetService(cabinet);
            ConsoleUI ui = new ConsoleUI(service);
            ui.demarrer();
            ///////////////////////////////////////////////////////////////////////////

        }
        catch (Exception e)
        {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }
}