package cabinet;


import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Animal extends Remote
{
    //getters ici (A1)
    String getNom() throws RemoteException;
    String getMaitre() throws RemoteException;
    String getRace() throws RemoteException;
    Espece getEspece() throws RemoteException;
    DossierSuivi getDossier() throws RemoteException;
}