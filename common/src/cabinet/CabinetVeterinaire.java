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
}