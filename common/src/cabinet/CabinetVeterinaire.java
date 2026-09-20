package cabinet;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

//ça deviendra notre nouveau contrat global relier au registre RMI

public interface CabinetVeterinaire extends Remote
{
    //getters (A4)
    List<Animal> getPatients() throws RemoteException;
    Animal rechercherPatient(String nom) throws RemoteException;

    //setters (A5)
    Animal ajouterPatient(String nom, String nomMaitre, String race, Espece espece) throws RemoteException;

    // l'objectif est que le client puisse suivre les alertes, on crée donc un système d'abonnement (A6)
    // on peut faire une analogie avec les subscripters et publisher avec ROS pour la robotique, ce que j'ai fait l'année précédente
    void sAbonner(CabinetObserver obs) throws RemoteException;
    void seDesabonner(CabinetObserver obs) throws RemoteException;


}