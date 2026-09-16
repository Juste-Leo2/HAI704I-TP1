package cabinet;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class ServerMain 
{
    public ServerMain(){}

    public static void main(String[] args) 
    {
        try
        {
            //A1 : implémentation initial
            AnimalImpl obj = new AnimalImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            //registry.bind("Animal", obj);
            //System.err.println("Serveur Prêt");

            //A4 généralisation
            CabinetVeterinaire cabinet = new CabinetVeterinaireImpl();
            registry.bind("CabinetVeterinaire", cabinet);
            System.err.println("Serveur Prêt");

        }
        catch (Exception e)
        {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();

        }
    }

}
