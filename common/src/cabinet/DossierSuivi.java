package cabinet;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DossierSuivi extends Remote 
{
    String getEtat() throws RemoteException;
    void setEtat(String etat) throws RemoteException;
}
