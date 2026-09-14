package cabinet;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain
{
    private ClientMain(){}

    public static void main(String[] args)
    {
        String host = (args.length < 1) ? null : args[0];

        try
        {
            //////////////////////////////////A1/////////////////////////////////////// 
            Registry registry = LocateRegistry.getRegistry(host);

            Animal stub = (Animal) registry.lookup("Animal");

            System.out.println("Nom de l'animal : " + stub.getNom());
            System.out.println("Classe du stub : " + stub.getClass().getName());
            System.out.println("Est-ce un Proxy dynamique ? " + java.lang.reflect.Proxy.isProxyClass(stub.getClass()));
            ////////////////////////////////////////////////////////////////////////////

            Espece e = stub.getEspece();
            System.out.println("Espèce reçue : " + e.getNom()); // "Chien"

            e.setNom("Chat");
            System.out.println("Espèce modifiée localement : " + e.getNom()); // "Chat"

            Espece e2 = stub.getEspece();
            System.out.println("Espèce redemandée au serveur : " + e2.getNom());


            System.out.println("HashCode e  : " + System.identityHashCode(e));
            System.out.println("HashCode e2 : " + System.identityHashCode(e2));


        }
        catch (Exception e)
        {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }
}