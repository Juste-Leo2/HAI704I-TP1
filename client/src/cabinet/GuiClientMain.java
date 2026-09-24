package cabinet;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.SwingUtilities;

/**
 * Lanceur du client avec interface graphique Swing (Partie C - Bonus).
 */
public class GuiClientMain 
{
    public static void main(String[] args) 
    {
        String host = (args.length >= 1) ? args[0] : "localhost";
        int port = 1099;

        if (args.length >= 2) 
        {
            try 
            {
                port = Integer.parseInt(args[1]);
            } 
            catch (NumberFormatException ignored) {}
        }

        try 
        {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CabinetVeterinaire cabinet = (CabinetVeterinaire) registry.lookup("CabinetVeterinaire");

            CabinetService service = new CabinetService(cabinet);

            // Démarrage de l'interface graphique Swing sur le fil d'exécution graphique
            SwingUtilities.invokeLater(() -> new SwingUI(service));
        } 
        catch (Exception e) 
        {
            System.err.println("[ERREUR] Impossible de lancer l'interface graphique Swing : " + e.getMessage());
        }
    }
}
