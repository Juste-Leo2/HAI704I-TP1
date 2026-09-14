## Comment j'ai procédé ?

A1 : création des fichiers de bases necessaire pour faire une premiere communication client serveur

j'ai travaillé sur 
- ClientMain.java : Le client
- ServerMain.java : le serveur
- AnimalImpl.java : La class (servant)
- Animal.java : Le contrat

On crée des dossiers bin pour avoir une bonne organisation.

Pour compiler, dans l'ordre :
```bash
javac -d common/bin common/src/cabinet/*.java 
javac -cp common/bin -d server/bin server/src/cabinet/*.java
javac -cp common/bin -d client/bin client/src/cabinet/*.java
```

Pour lancer le test:

```bash
java -cp "server/bin;common/bin" cabinet.ServerMain
java -cp "client/bin;common/bin" cabinet.ClientMain
```

Résultats :

[resultat](docs/a1.png)