package cabinet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class CabinetVeterinaireImpl extends UnicastRemoteObject implements CabinetVeterinaire {
    private List<Animal> patients = new ArrayList<>();
    private List<CabinetObserver> observateurs = new ArrayList<>();
    private int patientMax = 3;

    public CabinetVeterinaireImpl() throws RemoteException {
        super();
        this.patients.add(new AnimalImpl()); // Opaline par defaut
    }

    // getters
    @Override
    public List<Animal> getPatients() throws RemoteException {
        return this.patients;
    }

    // rechercher le patient
    @Override
    public Animal rechercherPatient(String nom) throws RemoteException {
        for (Animal a : this.patients) {
            if (a.getNom().equalsIgnoreCase(nom)) {
                return a;
            }
        }
        return null;
    }

    // setters
    @Override
    public Animal ajouterPatient(String nom, String nomMaitre, String race, Espece espece) throws RemoteException {
        Animal nouvelAnimal = new AnimalImpl(nom, race, espece, nomMaitre, new DossierSuiviImpl());
        this.patients.add(nouvelAnimal);

        int total = this.patients.size();

        // Declenchement de l'alerte au franchissement des seuils (100, 500, 1000 ou
        // seuils de test 3 et 5 prévus par le sujet)
        if (total == 100 || total == 500 || total == 1000 || total == this.patientMax || total == 5) {
            diffuserAlerte("Seuil franchi : le cabinet compte maintenant " + total + " patients !");
        }

        return nouvelAnimal;
    }

    // Gestion des abonnements (A6)
    @Override
    public void sAbonner(CabinetObserver obs) throws RemoteException {
        if (obs != null && !this.observateurs.contains(obs)) {
            this.observateurs.add(obs);
        }
    }

    @Override
    public void seDesabonner(CabinetObserver obs) throws RemoteException {
        this.observateurs.remove(obs);
    }

    // Diffusion de l'alerte et gestion des clients deconnectes brutalement (Point
    // de controle F)
    private void diffuserAlerte(String message) {
        List<CabinetObserver> deconnectes = new ArrayList<>();
        for (CabinetObserver obs : this.observateurs) {
            try {
                obs.notifierAlerte(message);
            } catch (RemoteException e) {
                // Le client ne repond plus (fermeture brutale), on le retire proprement
                deconnectes.add(obs);
            }
        }
        this.observateurs.removeAll(deconnectes);
    }
}
