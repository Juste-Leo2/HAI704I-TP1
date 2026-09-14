# Cahier des Charges & Feuille de Route — TP 1 : Java RMI
**UE HAI704I · Architectures logicielles distribuées (2026–2027)**  
*Enseignant : Bachar Rima*

---

## 1. Problématique & Objectifs Globaux

### 1.1. Contexte Métier
Construire une application distribuée de gestion d'un **cabinet vétérinaire** :
- Les **patients (animaux)** résident en mémoire dans la JVM du serveur.
- Plusieurs **clients (vétérinaires)** consultent, modifient et créent des patients à distance via **Java RMI natif**.
- Un système d'**alertes asynchrones** notifie les vétérinaires connectés quand certains seuils de patients sont franchis.

### 1.2. Problématique d'Architecture Distribuée
Le cœur du TP repose sur le choix du **régime de passage des objets** sur le réseau (CM 02) :
1. **Passage par référence distante (Objets Distants RMI)** :
   - L'objet réside sur le serveur (hébergeur).
   - Le client ne manipule qu'un **Stub** (proxy dynamique). Les appels de méthode traversent le réseau.
   - *Cas d'usage* : `CabinetVeterinaire`, `Animal`, et les `Observateurs` (côté client).
2. **Passage par valeur (Objets Sérialisables / Clichés)** :
   - L'objet implémente `Serializable`. Une copie complète de son état est transmise par le flux.
   - Une mutation côté client n'affecte pas l'objet serveur (mémoires disjointes).
   - *Cas d'usage* : `Espece` (donnée statique/consultable).
3. **Cas de conception à trancher** : Le `DossierSuivi` (Doit-il être partagé en temps réel ou copié ? Justification requise).

### 1.3. Contraintes d'Ingénierie & Architecture
- **Découpage strict en 3 sous-projets** :
  - `common` : interfaces distantes (`Remote`), classes de données (`Serializable`), exceptions métier.
  - `server` : implémentations (`Servants`), export RMI, registre. Dépend de `common`.
  - `client` : point d'entrée, logique métier distante, interface utilisateur (CLI). Dépend de `common` (**aucune référence vers `server`**).
- **Architecture Client en 3 couches distinctes** (Crucial pour A8 et Bonus C) :
  - `Point d'entrée` : parse les arguments (hôte, port), récupère le stub, initialise l'UI.
  - `Logique client` : encapsule les appels RMI via les stubs, gère le cache/état local.
  - `Interface console (CLI)` : lit les entrées utilisateur (`Scanner`), affiche les menus et résultats. **Zéro import RMI dans cette couche**.
- **Environnement** : Java 17 minimum (LTS), non modulaire (pas de `module-info.java`).

---

## 2. Barème Synthétique (Total /20 + 2 pts Bonus)

| Catégorie | Éléments évalués | Étape(s) | Pts |
| :--- | :--- | :--- | :---: |
| **Mise en œuvre RMI** | Objet distant publié & invoqué (`Animal`) | A1 | **2** |
| | Cabinet : point d'entrée unique & recherche | A4 | **2** |
| | Création d'un patient depuis le client (instanciation serveur) | A5 | **2** |
| | Alertes par rappel des clients (Observer RMI) | A6 | **2** |
| **Conception & Maîtrise** | Sémantique de passage justifiée (valeur vs référence, A2/A3/A7) | A2, A3, A7 | **1.5** |
| | Architecture en 3 projets découplés (`client` ignore `server`) | A0–A8 | **1.5** |
| **Application & Robustesse** | Interface CLI (séparation UI/Logique, pas de modif de code) | A8 | **1.5** |
| | Robustesse (multi-processus, saisies invalides, crashs, arrêt propre) | A1–A8 | **1.5** |
| **Rapport & Livrables** | Compte-rendu PDF (justifications, schémas, synthèse, recul) | Livrable | **2** |
| | Vidéo de démonstration (CLI interactive, multi-clients, effets) | Livrable | **2** |
| | README PDF (guide clair pour compiler, configurer, lancer) | Livrable | **1** |
| | Déclaration d'usage des IA (transparente et sincère) | Livrable | **1** |
| **Total Partie A** | **Travail obligatoire** | | **20 / 20** |
| *Bonus B (Spring Boot)* | Compilation + Registry + Injection Stub + CLI | B1–B6 | *+0.5* |
| | Analyse comparative B7 (assemblage vs RMI) | B7 | *+0.5* |
| *Bonus C (Interface GUI)* | GUI utilisable (Swing/JavaFX) avec alertes | C1–C2 | *+0.5* |
| | Réutilisation stricte de la couche Logique de A8 (sans duplication) | C1–C3 | *+0.5* |
| *Plafond Bonus* | *Bonus max pris en compte en compensation de A* | | *+2 max* |

---

## 3. Détail des Consignes — Partie A (Obligatoire, sur 20)

### Étape A0 : Structure & Socle Technique
- **Travail** :
  1. Configurer Java 17+ (`java -version`).
  2. Mettre en place l'arborescence à 3 projets : `common`, `server`, `client`.
  3. `server` et `client` ont `common` dans leur classpath (via Maven ou scripts de build).
- **Point de contrôle 0** : Tout compile ; `client` ne dépend pas de `server`.

---

### Étape A1 : Premier Objet Distant — `Animal` *(2 pts)*
- **Travail** :
  1. `common` : interface `Animal` étendant `java.rmi.Remote`, méthodes lançant `RemoteException` (nom, maître, race, espèce sous forme de `String`).
  2. `server` : classe `AnimalImpl` (étend `UnicastRemoteObject` ou exportée via `UnicastRemoteObject.exportObject`). Lanceur publiant l'animal dans le registre RMI (`LocateRegistry.createRegistry` ou `getRegistry`).
  3. `client` : lookup dans le registre, appel des méthodes distantes.
- **Point de contrôle A** :
  - Client et serveur tournent dans 2 processus distincts.
  - Le client affiche `stub.getClass().getName()` ($\rightarrow$ `jdk.proxyN.$ProxyM`) et `Proxy.isProxyClass(...)` renvoie `true`.

---

### Étape A2 : L'Espèce par Valeur (`Serializable`) *(Évalué dans Conception : 1.5 pt)*
- **Travail** :
  1. `common` : classe `Espece` (ex: nom, espérance de vie) implémentant `java.io.Serializable`.
  2. `Animal` expose un getter `Espece getEspece()`.
  3. Côté client : récupérer l'espèce, modifier un champ localement, redemander l'espèce au serveur.
- **Point de contrôle B** :
  - `System.identityHashCode(espece)` est différent côté client et côté serveur.
  - La mutation locale côté client n'affecte pas l'objet resté sur le serveur (transmission par copie).

---

### Étape A3 : Le Dossier de Suivi — Décision de Conception *(Évalué dans Conception : 1.5 pt)*
- **Problématique** : Un animal a un dossier (état de santé, liste d'observations/visites). Plusieurs vétérinaires y accèdent.
- **Travail** :
  1. **Décider** :
     - *Option 1 (Recommandée pour un partage concurrent)* : Objet distant RMI (`Remote`). Le dossier reste sur le serveur, toute modification est immédiatement visible par tous.
     - *Option 2* : Objet sérialisable (`Serializable`). Copie locale, nécessite une méthode explicite de réenregistrement/push sur le serveur.
  2. Implémenter et documenter la décision dans le compte-rendu.
- **Point de contrôle C** :
  - Un client modifie le dossier ; un autre client relit le dossier et constate la modification.

---

### Étape A4 : Point d'Entrée Unique — `CabinetVeterinaire` *(2 pts)*
- **Travail** :
  1. `common` : interface `CabinetVeterinaire` étendant `Remote`.
     - `List<Animal> getPatients()` ou `int getNbPatients()`
     - `Animal rechercherPatient(String nom)` (définir le comportement si introuvable : `null` ou exception métier dédiée).
  2. `server` : implémentation `CabinetVeterinaireImpl`. Le serveur **ne publie plus que le cabinet** dans le registre RMI.
  3. `client` : ne lookup que le cabinet, puis navigue vers les animaux à partir de celui-ci.
- **Point de contrôle D** :
  - Une seule liaison dans le registre RMI.
  - Le client liste les patients et recherche par nom.

---

### Étape A5 : Création de Patient depuis le Client *(2 pts)*
- **Travail** :
  1. Ajouter une méthode distante d'ajout au cabinet :  
     `Animal ajouterPatient(String nom, String nomMaitre, String race, Espece espece, ...)`
  2. **Règle fondamentale** : L'instance d'`AnimalImpl` DOIT être créée et exportée **sur le serveur**. Un `new AnimalImpl()` fait dans le client résiderait sur la JVM du client !
- **Point de contrôle E** :
  - Un patient ajouté par un client apparaît dans la liste et peut être retrouvé par recherche distante.

---

### Étape A6 : Alertes & Callback — Patron Observer *(2 pts)*
- **Travail** :
  1. `common` : interface `CabinetObserver` (ou `AlertListener`) étendant `Remote` avec `void notifierAlerte(String message) throws RemoteException`.
  2. `client` : implémente l'observateur et l'exporte (`UnicastRemoteObject.exportObject`).
  3. `server` : méthodes `s'abonner(CabinetObserver obs)` et `seDesabonner(CabinetObserver obs)`.
  4. Déclencher une alerte lors du franchissement des seuils **100, 500 et 1000 patients** (à la hausse et à la baisse).
  5. **Robustesse** : Si un client se déconnecte brutalement, le serveur doit attraper la `RemoteException` lors du broadcast et désinscrire proprement le stub mort sans planter.
- **Point de contrôle F** :
  - Deux clients lancés et abonnés. Au franchissement du seuil 100, les deux affichent l'alerte.
  - Fermer un client brutalement ne bloque pas le second lors de l'alerte suivante.

---

### Étape A7 : Expérience de Sérialisation *(Évalué dans Conception)*
- **Travail** :
  1. Créer une sous-classe `Chien extends Espece` **uniquement dans le projet `client`**.
  2. Tenter d'ajouter un patient avec cette instance.
  3. Constater l'exception (`ClassNotFoundException` encapsulée dans `UnmarshalException` / `ServerException`).
  4. Expliquer la cause (le flux RMI transmet l'état et le nom de classe, pas le bytecode) et corriger (déplacer la classe dans `common`).
- **Point de contrôle G** :
  - Erreur observée et comprise, puis résolution validée.

---

### Étape A8 : Application CLI Complète & Découpage 3 Tiers *(3 pts : 1.5 CLI + 1.5 Robustesse)*
- **Fonctionnalités requises dans le menu console** :
  - Consulter la liste des patients.
  - Rechercher un patient par son nom.
  - Créer un nouveau patient (avec espèce).
  - Consulter et mettre à jour le dossier de suivi d'un patient.
  - S'abonner / Se désabonner des alertes de seuil.
  - Quitter proprement (dé-exporter l'observateur pour permettre l'arrêt réel de la JVM !).
- **Structure interne du client (Obligatoire)** :
  ```
  client/
  ├── Main (Point d'entrée : arguments hôte/port, lookup, lance UI)
  ├── service/LogiqueClient (Opérations métier, appels sur stubs RMI)
  └── ui/ConsoleUI (Menu, Scanner, affichage — ZÉRO import RMI)
  ```
- **Robustesse attendue** :
  - Gestion des erreurs de saisie sans crash.
  - Messages clairs si le serveur est coupé ou indisponible (`ConnectException`).
  - Arrêt complet du processus client en sortie de menu (`UnicastRemoteObject.unexportObject` sur l'observateur).
- **Point de contrôle H** : Deux clients CLI en terminaux indépendants interagissent simultanément sans crash.

---

## 4. Détail des Consignes — Parties Bonus (B & C)

### Partie B : Java RMI piloté par Spring Boot *(Bonus jusqu'à +1 pt)*
*Ne pas utiliser Spring Web / REST, ni de vieux remoting Spring déprécié. RMI reste natif (`LocateRegistry`, `rebind`, etc.).*
- **B1–B2** : 4 modules Maven (parent, `vet-common`, `vet-server`, `vet-client`) avec `spring-boot-starter` (Spring Boot 3.x, Java 17).
- **B3–B4** : Le servant `Cabinet` devient un `@Service` Spring. Injection par constructeur de la logique métier / référentiel patients.
- **B5** : Classe `@Configuration` créant le registre via `@Bean` et publiant le service via un `ApplicationRunner`. Propriétés dans `application.properties`.
- **B6** : Côté client, `@Configuration` effectue le `lookup` et injecte le stub dans la classe de logique client. CLI lancée via `ApplicationRunner`.
- **B7** : Rédaction comparative dans le compte-rendu : ce que Spring Boot prend en charge (cycle de vie, IoC, injection) vs ce qui reste strictement du RMI (communication réseau, stubs, sérialisation).

### Partie C : Interface Graphique GUI *(Bonus jusqu'à +1 pt)*
*Condition stricte : Réutiliser directement la couche `LogiqueClient` de l'étape A8 sans dupliquer la gestion RMI.*
- **C1** : Choisir Swing (recommandé car natif JDK) ou JavaFX.
- **C2** : Écrans pour lister, chercher, ajouter des patients, éditer le dossier, et voir les alertes en direct.
- **Points techniques** :
  - Mettre à jour l'UI lors d'une alerte via le fil d'exécution graphique (`SwingUtilities.invokeLater`).
  - Ne pas bloquer l'IHM lors des appels distants réseau.

---

## 5. Livrables et Critères d'Évaluation (6 pts)

L'archive finale doit être nommée `NOM_Prenom_HAI704I_TP1.zip` et contenir :
1. **`Compte-Rendu.pdf` (2 pts)** :
   - Rapport d'ingénierie structuré par étapes (A0 à A8, + B/C).
   - Justification technique des choix (valeur vs référence, conception du dossier).
   - Schémas explicatifs (UML de classes, séquence des alertes Observer, déploiement).
   - Analyse critique (difficultés, pistes d'amélioration).
2. **`README.pdf` (1 pt)** :
   - Guide pas à pas : version JDK, arborescence, commandes de build (`javac`/`mvn`), ordre exact de démarrage des processus, ports, utilisation de la CLI.
3. **`source/` (Code source propre)** :
   - Arborescence `common/`, `server/`, `client/`.
   - **Aucun artefact compilé** (ni `.class`, ni `target/`, ni `.jar`, ni caches d'IDE).
4. **`video/` ou `liens.txt` (2 pts)** :
   - Vidéo mp4 (ou lien streaming) montrant :
     - Démarrage des processus (registre, serveur, 2 clients).
     - Démonstration interactive de la CLI (recherche, ajout, modification partagée).
     - Déclenchement de l'alerte sur les deux clients.
     - Arrêt propre.
5. **Déclaration d'usage de l'IA (1 pt)** :
   - Section obligatoire dans le compte-rendu précisant les outils utilisés, les prompts/tâches déléguées, la vérification effectuée et le code produit personnellement.

---

## 6. Structure Projet Recommandée pour Démarrer

Pour implémenter simplement et proprement cette architecture :

```text
HAI704I-TP1/
├── common/
│   └── src/
│       └── fr/umontpellier/ig5/vet/common/
│           ├── model/
│           │   ├── Espece.java                 # Serializable (A2)
│           │   └── DossierSuivi.java           # Remote ou Serializable (A3)
│           ├── dto/ (ou exceptions)
│           │   └── PatientNotFoundException.java
│           ├── remote/
│           │   ├── Animal.java                 # Remote (A1)
│           │   ├── CabinetVeterinaire.java     # Remote (A4)
│           │   └── CabinetObserver.java        # Remote (A6)
├── server/
│   └── src/
│       └── fr/umontpellier/ig5/vet/server/
│           ├── impl/
│           │   ├── AnimalImpl.java             # Servant
│           │   ├── DossierSuiviImpl.java       # Si choix Remote
│           │   └── CabinetVeterinaireImpl.java # Servant
│           └── ServerMain.java                 # Registre + Publication
├── client/
│   └── src/
│       └── fr/umontpellier/ig5/vet/client/
│           ├── service/
│           │   └── CabinetServiceClient.java   # Logique client (appels RMI)
│           ├── observer/
│           │   └── ClientObserverImpl.java     # Implémentation Observer client
│           ├── ui/
│           │   └── CommandLineInterface.java   # Menus & saisies (ZÉRO RMI)
│           └── ClientMain.java                 # Lanceur client CLI
├── build.sh / pom.xml                          # Outils de compilation
├── CAHIER_DES_CHARGES_TP1.md                   # Ce document de référence
└── README.md
```
