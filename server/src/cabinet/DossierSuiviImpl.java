package cabinet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DossierSuiviImpl extends UnicastRemoteObject implements DossierSuivi 
{
    private String etat = "En bonne santé";

    public DossierSuiviImpl() throws RemoteException 
    {
        super();
    }

    //getters
    @Override
    public String getEtat() throws RemoteException 
    {
        return this.etat;
    }

    //setters
    @Override
    public void setEtat(String etat) throws RemoteException 
    {
        this.etat = etat;
    }
}
