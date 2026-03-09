<div align="center">

# Projet Liquid War - CPOO 2025-2026

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

Ce projet est une implémentation du jeu **Liquid War** en Java, un jeu de combat de particules fluides.

</div>

## Concepts Abordés

Ce projet met en pratique plusieurs concepts de la programmation orientée objet :

* **Architecture MVC (Modèle-Vue-Contrôleur) :** Le code est organisé en trois parties. Le Modèle contient les données du jeu (la carte, les équipes, les particules), la Vue gère l'affichage graphique à l'écran, et le Contrôleur fait le lien entre les actions du joueur et les mises à jour du jeu.
* **Encapsulation :** Les informations internes des objets (comme la position exacte d'une particule) sont cachées et protégées. On interagit avec ces données uniquement grâce à des méthodes bien précises.
* **Gestion graphique :** Création d'une interface visuelle avec les outils standards de Java pour dessiner la fenêtre de jeu et réagir aux interactions de l'utilisateur.
* **Outil d'automatisation :** Utilisation de Gradle pour faciliter la compilation du programme et le lancement des tests unitaires sans avoir à tout configurer manuellement.

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
