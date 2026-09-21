## Comment j'ai procédé ?

A1 : création des fichiers de base nécessaires pour faire une première communication client-serveur

J'ai travaillé sur :
- ClientMain.java : Le client
- ServerMain.java : Le serveur
- AnimalImpl.java : La classe d'implémentation (servant)
- Animal.java : Le contrat (interface distante)

On crée des dossiers bin pour avoir une bonne organisation.

Pour compiler, dans l'ordre :
```bash
javac -d common/bin common/src/cabinet/*.java 
javac -cp common/bin -d server/bin server/src/cabinet/*.java
javac -cp common/bin -d client/bin client/src/cabinet/*.java
```

Pour lancer le test :

```bash
# Linux / macOS (séparateur :)
java -cp "server/bin:common/bin" cabinet.ServerMain
java -cp "client/bin:common/bin" cabinet.ClientMain

# Windows (séparateur ;)
java -cp "server/bin;common/bin" cabinet.ServerMain
java -cp "client/bin;common/bin" cabinet.ClientMain
```

Résultats :

[resultat](docs/img1.png)


A2 : Création d'un type Espece avec comme attributs le nom et la durée de vie.

Ensuite, un test de modification via un setter est effectué par le client pour le nom de l'espèce. Tout se passe donc côté client et la modification de l'espèce n'est pas propagée côté serveur (passage par valeur / Serializable).

Résultats :

[resultat](docs/img2.png)

A3 : Mise en place du dossier pour l'animal et modification distante partagée

J'ai créé un nouveau contrat DossierSuivi (interface Remote) qui définit les méthodes getEtat() et setEtat(). Ce dossier est implémenté côté serveur par DossierSuiviImpl.

Ensuite, j'ai relié ce dossier à l'animal en ajoutant DossierSuivi getDossier() dans le contrat Animal.

Le client n'a pas besoin de chercher le dossier dans l'annuaire RMI : il récupère directement le stub du dossier via stub.getDossier(). L'appel à dossier.setEtat(...) passe lui aussi par le RMI et modifie l'état directement sur le serveur, ce qui permet un partage en temps réel entre tous les vétérinaires.

Résultats :
[resultat](docs/img3.png)

A4 : Création du cabinet vétérinaire

J'ai créé le contrat CabinetVeterinaire qui remplace Animal comme point d'entrée unique dans le registre RMI.

le registre ne publie qu'un seul objet : le cabinet. Les animaux et leurs dossiers ne sont plus enregistrés dans l'annuaire RMI : c'est le cabinet qui les contient et les distribue au client.

Le contrat propose deux façons d'accéder aux patients :
- getPatients() : pour lister l'ensemble des animaux du cabinet.
- rechercherPatient(nom) : pour retrouver directement un animal précis par son nom (la recherche s'exécute côté serveur).

Résultat :
[resultat](docs/img4.png)

A5 : Création d'un patient côté serveur

Pour éviter que le patient ne soit créé localement dans la mémoire du client (ce qui le ferait disparaître à la déconnexion), l'instanciation doit se faire sur le serveur. 

J'ai donc ajouté la méthode ajouterPatient(...) dans le contrat CabinetVeterinaire, implémentée côté CabinetVeterinaireImpl pour créer et enregistrer l'animal sur la JVM du serveur.

Résultat : 
[resultat](docs/img5.png)


A6 : Système d'alertes et callbacks (mécanisme d'abonnement)

L'objectif est d'avertir automatiquement les vétérinaires (les clients) dès que le nombre de patients franchit un seuil.

Pour que le serveur puisse envoyer des notifications au client (au lieu que le client doive interroger le serveur en continu), on met en place un système d'abonnement / callback :
- J'ai créé le contrat CabinetObserver (interface Remote) dans common avec la méthode notifierAlerte(String message).

- Côté client, ClientObserverImpl implémente ce contrat et hérite de UnicastRemoteObject. Le client devient donc à son tour hébergeur d'un objet distant : c'est son stub qu'on transmet au serveur lors de l'appel à sAbonner(...).

- Côté serveur, CabinetVeterinaireImpl gère la liste des abonnés (sAbonner et seDesabonner). Dès qu'un ajout de patient atteint un seuil, le serveur parcourt les clients abonnés et appelle leur méthode distante notifierAlerte.

- Pour la robustesse : si un client est coupé brutalement, le serveur capture la RemoteException lors de l'envoi de l'alerte et le retire de sa liste d'abonnés

Résultat :
[resultat](docs/img6.png)
