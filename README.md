# Batch Processing avec Spring Batch

## Introduction

Ce projet propose une étude détaillée et des exemples pratiques sur le traitement par lots (batch processing) en utilisant Spring Batch. L'objectif est d'illustrer les concepts fondamentaux ainsi que des techniques avancées pour la gestion des données par lots.

## Concepts de Batch Processing

### Batch Job

Un travail batch (Batch Job) est composé de plusieurs étapes (steps). Chaque step est responsable d'une partie du traitement et peut inclure la lecture des données, leur transformation et leur écriture.

### Step

Une étape (Step) est une phase du travail batch. Elle comprend généralement un lecteur (reader), un processeur (processor) et un écrivain (writer).

### Chunk

Le traitement par morceaux (Chunk) permet de diviser les données en petits morceaux pour les traiter. Cela améliore les performances et la gestion des transactions.

## Explication à travers un Exemple

Ce projet utilise un exemple concret pour illustrer le fonctionnement de Spring Batch. Les étapes du projet sont les suivantes :

1. [Étape 1 : Importation CSV](etape-1-importation-csv.md)
2. [Étape 2 : Ajout de la colonne "cat"](etape-2-ajout-colonne-cat.md)
3. [Étape 3 : Filtrage et Exportation CSV](etape-3-filtrage-exportation-csv.md)

Chaque étape est détaillée dans un fichier Markdown séparé, qui inclut l'objectif, le code et les explications nécessaires.

## Lancer le Projet

### Pré-requis

- Java 17
- Maven
- Docker

### Instructions de Lancement

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
    curl -X POST http://localhost:9090/students
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
