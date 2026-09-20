package cabinet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// A6 : Implantation de l'observateur cote client (exporte en RMI pour etre appele par le serveur)
public class ClientObserverImpl extends UnicastRemoteObject implements CabinetObserver
{
    public ClientObserverImpl() throws RemoteException 
    {
        super();
    }

    @Override
    public void notifierAlerte(String alerte) throws RemoteException 
    {
        System.out.println("\n[ALERTE REÇUE DU SERVEUR] " + alerte);
    }
}