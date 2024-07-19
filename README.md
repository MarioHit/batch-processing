# Batch Processing

## Introduction

Ce projet est une démonstration de traitement par lots (batch processing) utilisant Spring Batch. L'objectif principal est de lire des données à partir d'un fichier CSV, de les transformer et de les enregistrer dans une base de données, puis d'effectuer des traitements supplémentaires sur ces données.

## Fonctionnalités Actuelles

### Étape 1 : Importation CSV

- **Lecture des données** : Récupère les données depuis un fichier CSV.
- **Transformation des données** : Modifie le nom des étudiants en les mettant en majuscules.
- **Enregistrement des données** : Insère les données transformées dans la table `student`.

Le lancement de l'application se fait en envoyant une requête HTTP POST à l'URL suivante : `http://localhost:8080/students`.

### Performances

- **Test de performance** : Le traitement de 100 000 lignes prend environ 41 secondes et 292 millisecondes.

## Évolutions Futures

### Étape 2 : Ajout de la colonne "cat"

- **Objectif** : Ajouter une nouvelle étape pour écrire les données dans une nouvelle table avec une colonne supplémentaire `cat` qui indique la tranche d'âge (dizaine, vingtaine, etc.).

### Étape 3 : Filtrage et Exportation CSV

- **Objectif** : Lire la nouvelle table, filtrer les enregistrements pour ne prendre que ceux dont la colonne `cat` est "cinquantaine ou plus", puis insérer ces données filtrées dans un fichier CSV.

### Étape 4 : Réponse JSON

- **Objectif** : Faire une réponse au POST initial avec les données JSON correspondantes après traitement.

## Instructions de Lancement

1. Cloner le dépôt :
    ```sh
    git clone https://github.com/MarioHit/batch-processing
    cd batch-processing
    ```

2. Configurer votre base de données dans le fichier `application.yml` :
    ```yaml
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/docker
        username: docker
        password: docker
        driver-class-name: org.postgresql.Driver

      jpa:
        hibernate:
          ddl-auto: create-drop
        show-sql: false
        properties:
          hibernate:
            format_sql: true
        database: postgresql
        database-platform: org.hibernate.dialect.PostgreSQLDialect

      batch:
        jdbc:
          initialize-schema: ALWAYS
        job:
          enabled: false

      server:
        port: 9090

    batch:
      input:
        file: src/main/resources/students.csv
    ```

3. Lancer l'application Spring Boot :
    ```sh
    mvn spring-boot:run
    ```

4. Envoyer une requête HTTP POST pour démarrer le traitement par lots :
    ```sh
    curl -X POST http://localhost:8080/students
    ```

## Technologies Utilisées

- **Spring Boot** : Framework pour créer des applications Java autonomes et de production.
- **Spring Batch** : Framework pour faciliter le traitement par lots.
- **PostgreSQL** : Base de données relationnelle utilisée pour stocker les données.

## Structure du Projet

- `BatchConfig.java` : Configuration des jobs et des steps.
- `Student.java` : Entité représentant un étudiant.
- `StudentRepository.java` : Repository pour gérer les opérations CRUD sur les étudiants.
- `StudentProcessor.java` : Classe pour transformer les données des étudiants.
- `students.csv` : Fichier CSV d'exemple pour l'importation des données.


