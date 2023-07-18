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


L'application Super-CEP est construite en utilisant l'architecture Modèle-Vue-Contrôleur (MVC). Cette architecture divise l'application en trois composants distincts pour séparer les préoccupations internes, ce qui facilite le développement, le test et la maintenance.

Le Modèle comprend la logique métier de l'application et les structures de données. Dans notre cas, il ne possède aucune dépendance avec Android, ce qui signifie que ce code peut être réutilisé sur n'importe quelle plateforme. Cette modularité augmente la portabilité et la réutilisabilité du code. Nous utilisons la bibliothèque Jackson pour décrire le format de données d'un relevé, ce qui facilite la sérialisation et la désérialisation des données JSON.

Grâce à cette architecture indépendante de la plateforme, je peux réaliser des tests unitaires sur mon PC sans nécessiter d'environnement Android. Les fonctionnalités clés du modèle comprennent l'importation et l'exportation de relevés, ainsi que la génération de présentations PowerPoint et d'archives.




