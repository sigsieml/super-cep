# SUPER-CEP

L'application Super-CEP est destinée aux conseillers en énergie du SIEMl. Elle permet, lors de la visite d'un bâtiment, de noter toutes les informations nécessaires :

    Informations générales sur le bâtiment
    Approvisionnement énergétique du bâtiment
    Usage et occupation du bâtiment
    Enveloppe (Murs, Sols, Menuiseries, Toiture, Éclairage)
    Ventilation
    Système d'eau chaude sanitaire
    Chauffage
    Climatisation
    Préconisations
    Remarques

Toutes les informations peuvent être accompagnées de photos prises lors de la visite ou stockées sur le téléphone.

Une fois la visite terminée, l'application permet de générer une présentation PowerPoint destinée aux élus. Cette présentation montre l'état actuel du bâtiment et les observations du conseiller en énergie.

Si le conseiller en énergie dispose des informations de consommation du bâtiment, un graphique peut également être généré dans la présentation PowerPoint. Deux types de graphiques sont créés : un pour la consommation en watts et un autre pour la consommation en euros. Le graphique est un diagramme à barres empilées pour chaque type d'énergie utilisée pour chauffer le bâtiment.

L'application Super-CEP est donc un outil précieux pour l'évaluation énergétique des bâtiments, la formulation de recommandations et la présentation des résultats aux parties prenantes.

## Architecture 
L'application Super-CEP est construite en utilisant l'architecture Modèle-Vue-Contrôleur (MVC). Cette architecture divise l'application en trois composants distincts pour séparer les préoccupations internes, ce qui facilite le développement, le test et la maintenance.

Le Modèle comprend la logique métier de l'application et les structures de données. Dans notre cas, il ne possède aucune dépendance avec Android, ce qui signifie que ce code peut être réutilisé sur n'importe quelle plateforme. Cette modularité augmente la portabilité et la réutilisabilité du code. Nous utilisons la bibliothèque Jackson pour décrire le format de données d'un relevé, ce qui facilite la sérialisation et la désérialisation des données JSON.

Grâce à cette architecture indépendante de la plateforme, je peux réaliser des tests unitaires sur mon PC sans nécessiter d'environnement Android. Les fonctionnalités clés du modèle comprennent l'importation et l'exportation de relevés, ainsi que la génération de présentations PowerPoint et d'archives.

## Dépendances

L'application Super-CEP utilise plusieurs bibliothèques de tiers pour fournir diverses fonctionnalités. Voici une liste des dépendances spéciales et de leur rôle dans l'application :

- Apache POI (poi, poi-ooxml, poi-scratchpad, poi-ooxml-full) : Cette bibliothèque est utilisée pour la génération de documents PowerPoint. Elle fournit une API pour la manipulation de divers formats de fichiers Microsoft Office.

- androidawt : Cette bibliothèque fournit une version Android du kit d'outils de fenêtrage abstrait (AWT) de Java. Elle est nécessaire pour l'utilisation de certaines fonctionnalités de Apache POI sur Android.

- Jackson (jackson-databind, jackson-datatype-jsr310) : Jackson est une bibliothèque pour le traitement de données JSON en Java. Elle est utilisée pour sérialiser et désérialiser des données JSON, ce qui est utile pour l'importation et l'exportation de relevés.

- Play Services Location : Cette bibliothèque est utilisée pour accéder aux services de localisation de Google sur les appareils Android. Elle peut être utilisée pour obtenir la localisation actuelle de l'utilisateur.

Ces dépendances sont spécifiées dans le fichier build.gradle de l'application, et elles sont automatiquement téléchargées et intégrées à l'application par Gradle lors de la construction de l'application. Pour exclure une dépendance spécifique (par exemple, 'poi-ooxml-lite'), utilisez la configuration exclude dans le bloc configurations de votre fichier build.gradle.

Les autres dépendances incluent diverses bibliothèques Android pour la création d'interfaces utilisateur, la gestion du cycle de vie des composants de l'application, la navigation entre les écrans, etc., ainsi que des bibliothèques pour les tests unitaires et les tests d'interface utilisateur.


