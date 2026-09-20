package cabinet;


import java.rmi.Remote;
import java.rmi.RemoteException;


public interface CabinetObserver extends Remote
{
    //A6 getters
    void notifierAlerte(String message) throws RemoteException;

}