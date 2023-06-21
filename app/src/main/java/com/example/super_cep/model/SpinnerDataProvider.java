package com.example.super_cep.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpinnerDataProvider {

    private Map<String, List<String>> spinnersData;

    public SpinnerDataProvider(){
        spinnersData = new HashMap<>();
        spinnersData.put("typeMur", List.of("Pierre", "Brique", "Parpaing", "Bois", "Autre"));
        spinnersData.put("typeDeMiseEnOeuvre", List.of("Enduit", "Bardage", "Autre"));
        spinnersData.put("typeIsolant", List.of("Laine de verre", "Laine de roche", "Polystyrène", "Autre"));
        spinnersData.put("niveauIsolation", List.of("Inconnu", "Non isolé", "Faiblement isolé", "Moyennement isolé", "Assez bien isolé", "Bien isolé"));
        spinnersData.put("typeToiture", List.of("Faux plafond en dalles", "Plafond en platre", "Plafond autres", "Toiture terrasse", "Rampants", "Combles perdues"));
        spinnersData.put("typeMenuiserie", List.of("Fenêtre", "Porte-fenêtre", "Fenêtre de toiture", "Porte Pleine"));
        spinnersData.put("materiau", List.of("Bois", "PVC", "Aluminium", "Acier", "Mixte", "Autre"));
        spinnersData.put("protectionsSolaires", List.of("Volets roulants électriques extérieurs", "Persiennes", "Volets extérieurs simples", "Grille ou grillage extérieur", "Rideau intérieur"));
        spinnersData.put("typeVitrage", List.of("Simple vitrage", "Double vitrage ancien", "Double vitrage assez récent", "Double vitrage récent", "DV 4-6-4 ou éq", "DV 4-8-4 ou éq", "DV 4-10-4 ou éq", "DV 4-12-4 ou éq", "DV 4-14-4 ou éq", "DV 4-16-4 ou éq", "DV 4-20-4 ou éq", "Triple vitrage"));
        spinnersData.put("typeSol", List.of("Terre plein", "Vide sanitaire", "Cave", "Sous-sol"));
        spinnersData.put("typeEclairage", List.of("LED", "Tube fluorescent", "Fluocompact", "Ampoule basse consommation", "Halogène"));
        spinnersData.put("typeRegulation", List.of("interrupteur", "détection de présence", "relance temporisée"));
        spinnersData.put("typeChauffageProducteur",  List.of("Chaudière gaz simple", "Chaudière gaz à condensation", "Chaudière fioul simple", "Chaudière fioul à condensation", "Chaudière bois granulés", "Chaudière bois plaquettes", "PAC air/air", "PAC air/eau", "PAC géothermique", "Chaudière électrique"));
        spinnersData.put("typeChauffageEmetteur",  List.of("Convecteur électrique simple", "Panneau rayonnant électrique", "Radiateur à inertie électrique", "Emetteur à infrarouges", "Plancher chauffant", "Ventilo-convecteur", "Aérotherme", "Radiateur acier avec rob thermo", "Radiateur acier sans rob thermo", "Radiateur fonte avec rob thermo", "Radiateur fonte sans rob thermo"));
        spinnersData.put("regulationChauffage",  List.of("Thermostat simple sur émetteur", "Thermostat programmable centralisé", "Régulation programmable à distance", "GTC", "Horloge", "Sonde d'ambiance intérieure", "Interrupteur", "T° constante", "Loi d'eau et vanne(s) 3 voies", "Loi d'eau + programmation hebdo confort / réduit / HG", "Sonde d'ambiance + programmation hebdo confort / réduit / HG", "Télécommande"));
        spinnersData.put("marqueProducteur", List.of( "Acova", "Airelec", "Ariston", "Atlantic", "Chappee", "Daikin", "De Dietrich", "Frisquet", "Froling", "Noirot", "Oertli", "Okofen", "Sauter", "Thermor", "Unical", "Vaillant", "Viessmann", "Weishaupt"));
        spinnersData.put("marqueEmetteur", List.of("Acova", "Airelec", "Atlantic", "Sauter", "Thermor"));
        spinnersData.put("typeClimatisation", List.of("Climatisation réversible", "Climatisation non réversible", "PAC air/air", "PAC air/eau", "PAC géothermique"));
        spinnersData.put("marqueClimatisation", List.of("Daikin", "Mitsubishi", "Hitachi", "Panasonic", "Toshiba", "Fujitsu General", "Technibel", "Samsung", "LG", "Altech"));
        spinnersData.put("regulationClimatisation", List.of("Régulateur thermostatique", "Régulateur séquentiel", "Capteurs", "Actionneurs", "Régulateur", "Domotique"));
        spinnersData.put("regulationVentilation", List.of("Absence de régulation", "Horloge", "Programmation horaire", "Sonde CO2", "Interrupteur", "relance temporisée"));
        spinnersData.put("typeVentilation", List.of("Ventilation naturelle", "VMC simple flux", "VMC double flux"));
        spinnersData.put("marqueECS", List.of("Atlantic", "Thermor", "De Dietrich", "Chaffoteaux", "Styx"));
        spinnersData.put("typeECS", List.of("Cumulus électrique", "Cumulus propane", "Cumulus fioul", "Micro accumulation", "Instantané", "Ballon intégré chaudière"));
        spinnersData.put("energieElectrique", List.of("Electricité"));
        spinnersData.put("energieGaz", List.of("Gaz Naturel", "Propane Réseau", "Propane Cuve"));
        spinnersData.put("energieAutre",  List.of("Bois Granulés", "Bois Déchiqueté", "Bois Bûches", "Fioul"));
        spinnersData.put("formuleTarifaire", List.of("Base", "HP/HC", "4HS", "4HP"));
        spinnersData.put("nomRemarques", List.of("Remarques enveloppe thermique", "Remarque systèmes", "Remarques occupation et régulation", "Elements de contexte sur le bâtiment", "Remarque production et distribution du chauffage", "Remarque conso, facture et appro énergétique", "Remarque diverse"));
        spinnersData.put("preconisations", List.of("Remplacer les tubes fluo par des LEDS", "Réaliser une isolation thermique extérieur", "Remplacer les menuiseries", "Remplacer le système de chauffage", "Remplacer le système de régulation ancien"));
    }

    public Map<String, List<String>> getSpinnersData(){
        return spinnersData;
    }
}
