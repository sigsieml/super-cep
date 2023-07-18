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


## Installation et Configuration
### Prérequis

Pour installer et exécuter l'application Super-CEP, vous devez avoir un appareil Android avec :

- Une version Android minimale de SDK 28.
- (fortement conseillé) Les applications PowerPoint et Excel installées pour visualiser et modifier les fichiers générés.

### Téléchargement et Installation

- Pour télécharger l'application, rendez-vous dans la section des releases du dépôt GitHub et téléchargez le fichier APK de la version la plus récente.

- Pour installer l'APK téléchargé, vous devrez autoriser les installations à partir de sources inconnues. Vous pouvez généralement trouver cette option dans les paramètres de sécurité de votre appareil Android.

## Configuration

Une fois l'application installée, aucune étape supplémentaire de configuration ou d'inscription n'est requise. Vous pouvez commencer à utiliser l'application immédiatement. 
## Utilisation de l'application


L'application fournit des listes pour remplir les champs de texte (type de mur, marque de chauffage, etc.). Ces listes sont modifiables et exportables. Vous pouvez récupérer les listes d'un collègue au format JSON et les importer dans l'application dans la section "Modifier les listes".

L'application sauvegarde automatiquement vos données à chaque changement d'écran.

Si vous avez besoin d'aide pour utiliser l'application, des aides sont disponibles en cliquant sur les trois petits points en haut de l'écran.

## Mise à jour de l'application

Pour mettre à jour l'application, téléchargez simplement la dernière version de l'APK à partir de la section des releases du dépôt GitHub et installez-la. Vous n'avez pas besoin de désinstaller l'ancienne version de l'application, et vos données seront préservées lors de la mise à jour.


## Génération de votre propre APK avec Android Studio

Pour générer votre propre APK de l'application Super-CEP, vous aurez besoin d'Android Studio. Si vous ne l'avez pas déjà fait, vous pouvez télécharger et installer Android Studio depuis le site officiel d'Android.

Suivez les étapes suivantes pour générer l'APK :

- Récupération du code source : Clonez le dépôt GitHub de l'application Super-CEP sur votre machine locale en utilisant la commande git clone.

- Ouverture du projet : Ouvrez Android Studio et cliquez sur File > Open. Naviguez jusqu'à l'emplacement où vous avez cloné le dépôt et sélectionnez le dossier du projet.

- Génération de l'APK : Dans Android Studio, cliquez sur Build > Build Bundle(s) / APK(s) > Build APK(s). Android Studio commencera à construire l'APK de votre application.

- Récupération de l'APK : Une fois la génération terminée, une notification apparaîtra en bas à droite de la fenêtre d'Android Studio. Cliquez sur locate pour ouvrir l'emplacement de l'APK dans l'explorateur de fichiers. Vous pouvez maintenant transférer cet APK sur votre appareil Android pour l'installer.

## Personnalisation du Modèle PowerPoint

L'application Super-CEP vous offre la possibilité de personnaliser le modèle PowerPoint qui est utilisé pour générer les présentations. Ce modèle se trouve dans le fichier powerpointvierge.pptx dans le répertoire app/src/main/res/assets/.

Pour modifier ce modèle, vous pouvez suivre les étapes ci-dessous :

- Localisation du fichier modèle : Ouvrez le projet dans Android Studio et naviguez vers app > src > main > res > assets. Vous y trouverez le fichier powerpointvierge.pptx.

- Modification du modèle : Faites un clic droit sur le fichier powerpointvierge.pptx et sélectionnez Show in Explorer (Windows) ou Reveal in Finder (macOS). Ceci ouvrira l'emplacement du fichier dans votre explorateur de fichiers. Vous pouvez maintenant ouvrir ce fichier dans Microsoft PowerPoint et apporter toutes les modifications souhaitées (par exemple, changer les couleurs, la disposition, la taille des éléments, etc.).

- Sauvegarde des modifications : Une fois que vous avez terminé de modifier le modèle, sauvegardez-le. Assurez-vous de ne pas changer le nom ou l'emplacement du fichier pour que l'application puisse toujours y accéder.

- Génération de l'APK : Maintenant que vous avez personnalisé votre modèle PowerPoint, vous pouvez générer un nouvel APK de l'application. L'application utilisera alors le modèle PowerPoint modifié pour générer les présentations.

De cette façon, vous pouvez facilement personnaliser l'aspect des présentations PowerPoint générées par l'application Super-CEP.
