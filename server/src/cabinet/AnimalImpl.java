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
    private DossierSuivi dossier = new DossierSuiviImpl(); //observation à A5, créer un dossier de suivi est automatique, plus besoin de le mentionner à chaque fois quand on veut ajouter un nouvel animal

    public AnimalImpl () throws RemoteException 
    {
        super();
    }

    public AnimalImpl (String nom, String race, Espece espece, String maitre, DossierSuivi dossier) throws RemoteException 
    {
        super();
        this.nom = nom;
        this.race = race;
        this.espece = espece;
        this.maitre = maitre;
        this.dossier = (dossier != null) ? dossier : new DossierSuiviImpl();
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