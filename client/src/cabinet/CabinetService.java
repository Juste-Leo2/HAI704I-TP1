package cabinet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Service applicatif client : encapsule les interactions avec le stub RMI CabinetVeterinaire
 * et maintient l'état de la session client (notamment l'observateur pour les alertes).
 */
public class CabinetService 
{
    private final CabinetVeterinaire cabinet;
    private ClientObserverImpl observateur;
    private boolean estAbonne = false;

    public CabinetService(CabinetVeterinaire cabinet) 
    {
        this.cabinet = cabinet;
    }

    /**
     * Récupère la liste de tous les patients enregistrés sur le serveur.
     */
    public List<Animal> recupererPatients() throws RemoteException 
    {
        return this.cabinet.getPatients();
    }

    /**
     * Recherche un patient par son nom (insensible à la casse).
     */
    public Animal rechercherPatient(String nom) throws RemoteException 
    {
        return this.cabinet.rechercherPatient(nom);
    }

    /**
     * Ajoute un nouveau patient côté serveur.
     */
    public Animal ajouterPatient(String nom, String nomMaitre, String race, Espece espece) throws RemoteException 
    {
        return this.cabinet.ajouterPatient(nom, nomMaitre, race, espece);
    }

    /**
     * Modifie l'état du dossier de suivi d'un animal distant.
     */
    public void modifierDossier(Animal animal, String nouvelEtat) throws RemoteException 
    {
        if (animal != null && animal.getDossier() != null) 
        {
            animal.getDossier().setEtat(nouvelEtat);
        }
    }

    /**
     * Bascule l'état d'abonnement aux alertes (s'abonne si désabonné, se désabonne sinon).
     * @return true si le client est maintenant abonné, false sinon.
     */
    public boolean basculerAbonnement() throws RemoteException 
    {
        return basculerAbonnement(null);
    }

    /**
     * Bascule l'abonnement avec un callback pour notifier l'IHM graphique (fil EDT Swing).
     */
    public boolean basculerAbonnement(java.util.function.Consumer<String> onAlerte) throws RemoteException 
    {
        if (!this.estAbonne) 
        {
            if (this.observateur == null) 
            {
                this.observateur = new ClientObserverImpl(onAlerte);
            }
            this.cabinet.sAbonner(this.observateur);
            this.estAbonne = true;
        } 
        else 
        {
            if (this.observateur != null) 
            {
                this.cabinet.seDesabonner(this.observateur);
            }
            this.estAbonne = false;
        }
        return this.estAbonne;
    }

    /**
     * Indique si le client est actuellement abonné aux alertes.
     */
    public boolean estAbonne() 
    {
        return this.estAbonne;
    }

    /**
     * Renvoie le texte de la dernière alerte reçue.
     */
    public String getDerniereAlerte() 
    {
        return (this.observateur != null) ? this.observateur.getDerniereAlerte() : "";
    }

    /**
     * Arrêt propre du client : se désabonne du serveur et dé-exporte l'objet distant
     * pour permettre à la machine virtuelle Java (JVM) de s'arrêter complètement.
     */
    public void arreter() 
    {
        if (this.observateur != null) 
        {
            try 
            {
                if (this.estAbonne) 
                {
                    this.cabinet.seDesabonner(this.observateur);
                    this.estAbonne = false;
                }
                // unexportObject retire l'observateur du runtime RMI, libérant ainsi les threads d'écoute
                UnicastRemoteObject.unexportObject(this.observateur, true);
            } 
            catch (Exception ignored) 
            {
                // En cas d'erreur de communication réseau lors de l'arrêt, on ne bloque pas la sortie
            }
        }
    }
}
