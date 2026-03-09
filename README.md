<div align="center">

# liquid war

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

Ce projet est une implémentation du jeu **Liquid War** en Java, un jeu de combat de particules fluides.

</div>

## Concepts Abordés

Ce projet met en pratique plusieurs concepts de la programmation orientée objet :

* **Architecture MVC (Modèle-Vue-Contrôleur) :** Séparation des responsabilités. Le **Modèle** gère la logique métier et la physique (collisions, déplacements), la **Vue** s'occupe du rendu graphique, et le **Contrôleur** écoute les événements utilisateur pour mettre à jour le Modèle sans que ce dernier n'ait connaissance de l'interface.
* **Encapsulation et Cohérence :** Utilisation des modificateurs d'accès (`private`, `protected`). L'état interne des entités (comme les coordonnées d'une particule ou la grille de la carte) est protégé. Il ne peut être altéré que via des méthodes spécifiques, pour garantir l'intégrité des données pendant les calculs.
* **Design Pattern "Fabrique" (Factory) :** Implémentation de ce motif de conception pour instancier dynamiquement les éléments du jeu. Cela centralise la logique de création et permet d'ajouter de nouveaux comportements sans avoir à modifier le code de base.
* **Multithreading et Threads Virtuels (Java 21) :** La boucle principale du jeu (Game Loop) doit calculer la physique et rafraîchir l'affichage simultanément. Le projet exploite la concurrence en Java, notamment avec les **Threads Virtuels** (très légers en ressources), pour exécuter ces tâches en parallèle de manière fluide et performante.
* **Automatisation avec Gradle :** Utilisation de cet outil de build pour standardiser le cycle de vie du projet : gestion des dépendances, compilation des classes, et exécution automatisée de la suite de tests unitaires.

## Prérequis

* **Java 21+**

## Exécution

```bash
./gradlew run    # démarrer le jeu
```
```bash
./gradlew test   # lancer les tests unitaires
```
```bash
./gradlew clean  # nettoyer le projet
```
