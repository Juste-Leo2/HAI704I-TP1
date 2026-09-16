package cabinet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class CabinetVeterinaireImpl extends UnicastRemoteObject implements CabinetVeterinaire
{
    private List<Animal> patients = new ArrayList<>();

    public CabinetVeterinaireImpl() throws RemoteException 
    {
        super();
        this.patients.add(new AnimalImpl()); // Opaline
    }

    //getters
    @Override
    public List<Animal> getPatients() throws RemoteException 
    {
        return this.patients;
    }

    //rechercher le patient 
    @Override
    public Animal rechercherPatient(String nom) throws RemoteException 
    {
        for (Animal a : this.patients) 
        {
            if (a.getNom().equalsIgnoreCase(nom)) 
            {
                return a;
            }
        }
        return null;
    }

    //setters

    //ajouter le nouveau patient côté serveur
    @Override
    public Animal ajouterPatient(String nom, String nomMaitre, String race, Espece espece) throws RemoteException 
    {
        Animal nouvelAnimal = new AnimalImpl(nom, race, espece, nomMaitre, new DossierSuiviImpl());
        this.patients.add(nouvelAnimal);
        return nouvelAnimal;
    }

}
