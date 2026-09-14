package cabinet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AnimalImpl extends UnicastRemoteObject implements Animal
{
    private String nom = "Opaline";
    private String race = "Chihuahua";
    private String espece = "Chien";
    private String maitre = "Anne";

    public AnimalImpl () throws RemoteException 
    {
        super();
    }

    @Override
    public String get_nom()
    {
        return this.nom;
    }
    @Override
    public String get_race()
    {
        return this.race;
    }
    @Override
    public String get_espece()
    {
        return this.espece;
    }
    @Override
    public String get_maitre()
    {
        return this.maitre;
    }

}