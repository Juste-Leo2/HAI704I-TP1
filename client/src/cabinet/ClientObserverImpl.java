package cabinet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.function.Consumer;

// A6 : Implantation de l'observateur cote client (exporte en RMI pour etre appele par le serveur)
public class ClientObserverImpl extends UnicastRemoteObject implements CabinetObserver
{
    private String derniereAlerte = "";
    private Consumer<String> onAlerte;

    public ClientObserverImpl() throws RemoteException 
    {
        super();
    }

    public ClientObserverImpl(Consumer<String> onAlerte) throws RemoteException 
    {
        super();
        this.onAlerte = onAlerte;
    }

    public String getDerniereAlerte() 
    {
        return this.derniereAlerte;
    }

    @Override
    public void notifierAlerte(String alerte) throws RemoteException 
    {
        this.derniereAlerte = alerte;
        System.out.println("\n[ALERTE REÇUE DU SERVEUR] " + alerte);
        if (this.onAlerte != null) 
        {
            this.onAlerte.accept(alerte);
        }
    }
}