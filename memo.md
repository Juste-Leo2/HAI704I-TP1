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
