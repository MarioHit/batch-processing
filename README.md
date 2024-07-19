# batch-processing
Demo pour batch : 

Nous avons pour l'instant une seul step :  importCsv  


- on récupére les données depuis un csv
- on modifie le nom : en le mettant en majiscule
- on les mets dans la table student

Le lancement de l'application se fait  en faisant une requête http : POST localhost:8080/students

Pour faire les traitement le test a été fait avec 100 000 lignes pour 41s292ms


Par la suite nous allons ajouter 3 autres étapes 
- une pour écrire dans une nouvelle table en ajoutant la colonne "categorie" qui va indiquer la tranche d'age ( dizaine ,vigntaine, ... )
- une autre pour lire cette nouvelle table et inserer dans le fichier CSV.
- faire une réponse au post avec le json correspondant 
