package cabinet;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Animal extends Remote
{
    //getters ici
    String get_nom() throws RemoteException;
    String get_maitre() throws RemoteException;
    String get_race() throws RemoteException;
    String get_espece() throws RemoteException;
}