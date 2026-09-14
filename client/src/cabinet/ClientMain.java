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
            Registry registry = LocateRegistry.getRegistry(host);

            Animal stub = (Animal) registry.lookup("Animal");

            System.out.println("Nom de l'animal : " + stub.get_nom());
            System.out.println("Classe du stub : " + stub.getClass().getName());
            System.out.println("Est-ce un Proxy dynamique ? " + java.lang.reflect.Proxy.isProxyClass(stub.getClass()));
        }
        catch (Exception e)
        {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }
}