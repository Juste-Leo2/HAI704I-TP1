package cabinet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


//A1
public class AnimalImpl extends UnicastRemoteObject implements Animal
{
    private String nom = "Opaline";
    private String race = "Chihuahua";
    private Espece espece = new Espece("Chien", 13);
    private String maitre = "Anne";
    private DossierSuivi dossier = new DossierSuiviImpl();

    public AnimalImpl () throws RemoteException 
    {
        super();
    }

    @Override
    public String getNom()
    {
        return this.nom;
    }
    @Override
    public String getRace()
    {
        return this.race;
    }

    @Override
    public Espece getEspece() {
        return this.espece;
    }
    @Override
    public String getMaitre()
    {
        return this.maitre;
    }

    @Override
    public DossierSuivi getDossier() 
    {
        return this.dossier;
    }

}