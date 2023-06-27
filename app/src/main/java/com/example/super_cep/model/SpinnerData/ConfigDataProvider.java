package com.example.super_cep.model.SpinnerData;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigDataProvider {

    public Map<String, List<String>> configData;


    @JsonCreator
    public ConfigDataProvider(@JsonProperty("configData") Map<String, List<String>> configData) {
        this.configData = configData;
    }

    public ConfigDataProvider(){
        configData = new HashMap<>();
        configData.put("typeMur", List.of("Béton", "Bois", "Brique", "Parpain","Métal", "Inconnu"));
        configData.put("typeDeMiseEnOeuvre", List.of("Extérieur", "Intérieur", "de façon répartie", "Aucun", "Inconnu"));
        configData.put("typeIsolant", List.of("Laine de verre", "Laine minérale", "Laine de roche", "Polystyrène", "Fibre de bois", "Laine de bois", "Ouate de celllulose", "Laine minérale soufflée", "Polyurethane", "Verre cellulaire", "Isolant biosourcé", "Inconnu"));
        configData.put("niveauIsolation", List.of("Non isolé", "Faiblement isolé", "Moyennement isolé", "Assez bien isolé", "Bien isolé", "Inconnu"));
        configData.put("typeToiture", List.of("Faux plafond en dalles", "Plafond en platre", "Plafond autres", "Toiture terrasse", "Rampants", "Combles perdues", "Inconnu"));
        configData.put("typeMenuiserie", List.of("Fenêtre", "Porte-fenêtre", "Fenêtre de toiture", "Porte Pleine", "Inconnu"));
        configData.put("materiau", List.of("Aluminium", "Bois", "PVC", "Mixte","Polycarbonate", "Inconnu"));
        configData.put("protectionsSolaires", List.of("Volets roulants électriques extérieurs", "Persiennes", "Volets extérieurs simples", "Grille ou grillage extérieur", "Rideau intérieur", "Inconnu"));
        configData.put("typeVitrage", List.of("Simple vitrage", "Double vitrage ancien", "Double vitrage assez récent", "Double vitrage récent", "DV 4-6-4 ou éq", "DV 4-8-4 ou éq", "DV 4-10-4 ou éq", "DV 4-12-4 ou éq", "DV 4-14-4 ou éq", "DV 4-16-4 ou éq", "DV 4-20-4 ou éq", "Triple vitrage", "Inconnu"));
        configData.put("typeSol", List.of("Terre plein", "Vide sanitaire", "Cave", "Sous-sol", "Inconnu"));
        configData.put("typeEclairage", List.of("LED", "Tube fluorescent", "Fluocompact", "Ampoule basse consommation", "Halogène", "Inconnu"));
        configData.put("typeRegulation", List.of("interrupteur", "détection de présence", "relance temporisée", "Inconnu"));
        configData.put("typeChauffageProducteur",  List.of("Chaudière gaz simple", "Chaudière gaz à condensation", "Chaudière fioul simple", "Chaudière fioul à condensation", "Chaudière bois granulés", "Chaudière bois plaquettes", "PAC air/air", "PAC air/eau", "PAC géothermique", "Chaudière électrique", "Inconnu"));
        configData.put("typeChauffageEmetteur",  List.of("Convecteur électrique simple", "Panneau rayonnant électrique", "Radiateur à inertie électrique", "Emetteur à infrarouges", "Plancher chauffant", "Ventilo-convecteur", "Aérotherme", "Radiateur acier avec rob thermo", "Radiateur acier sans rob thermo", "Radiateur fonte avec rob thermo", "Radiateur fonte sans rob thermo", "Inconnu"));
        configData.put("typeChauffageProducteurEmetteur", List.of("Radiateurs électriques", "Poêles à bois", "Pompes à chaleur air-air", "Poêles à granulés", "Inconnu"));
        configData.put("regulationChauffage",  List.of("Thermostat simple sur émetteur", "Thermostat programmable centralisé", "Régulation programmable à distance", "GTC", "Horloge", "Sonde d'ambiance intérieure", "Interrupteur", "T° constante", "Loi d'eau et vanne(s) 3 voies", "Loi d'eau + programmation hebdo confort / réduit / HG", "Sonde d'ambiance + programmation hebdo confort / réduit / HG", "Télécommande", "Inconnu"));
        configData.put("marqueProducteur", List.of( "Acova", "Airelec", "Ariston", "Atlantic", "Chappee", "Daikin", "De Dietrich", "Frisquet", "Froling", "Noirot", "Oertli", "Okofen", "Sauter", "Thermor", "Unical", "Vaillant", "Viessmann", "Weishaupt", "Inconnu"));
        configData.put("marqueEmetteur", List.of("Acova", "Airelec", "Atlantic", "Sauter", "Thermor", "Inconnu"));
        configData.put("marqueProducteurEmetteur", List.of("Mitsubishi","Daikin","Hitachi", "Inconnu"));
        configData.put("typeClimatisation", List.of("Climatisation réversible", "Climatisation non réversible", "PAC air/air", "PAC air/eau", "PAC géothermique", "Inconnu"));
        configData.put("marqueClimatisation", List.of("Daikin", "Mitsubishi", "Hitachi", "Panasonic", "Toshiba", "Fujitsu General", "Technibel", "Samsung", "LG", "Altech", "Inconnu"));
        configData.put("regulationClimatisation", List.of("Régulateur thermostatique", "Régulateur séquentiel", "Capteurs", "Actionneurs", "Régulateur", "Domotique", "Inconnu"));
        configData.put("regulationVentilation", List.of("Absence de régulation", "Horloge", "Programmation horaire", "Sonde CO2", "Interrupteur", "relance temporisée", "Inconnu"));
        configData.put("typeVentilation", List.of("Ventilation naturelle", "VMC simple flux", "VMC double flux", "Inconnu"));
        configData.put("marqueECS", List.of("Atlantic", "Thermor", "De Dietrich", "Chaffoteaux", "Styx", "Inconnu"));
        configData.put("typeECS", List.of("Cumulus électrique", "Cumulus propane", "Cumulus fioul", "Micro accumulation", "Instantané", "Ballon intégré chaudière", "Inconnu"));
        configData.put("energieElectrique", List.of("Electricité"));
        configData.put("energieGaz", List.of("Gaz Naturel", "Propane Réseau", "Propane Cuve"));
        configData.put("energieAutre",  List.of("Bois Granulés", "Bois Déchiqueté", "Bois Bûches", "Fioul", "Inconnue"));
        configData.put("formuleTarifaire", List.of("Base", "HP/HC", "4HS", "4HP", "Inconnu"));
        configData.put("nomRemarques", List.of("Descriptif de l'enveloppe thermique", "Descriptif des systèmes", "Usage et occupation du bâtiment", "Elements de contexte sur le bâtiment", "Descriptif du chauffage", "Energie et consommations", "Remarque diverse"));
        configData.put("preconisations", List.of("Remplacer les tubes fluo par des LEDS", "Réaliser une isolation thermique extérieur", "Remplacer les menuiseries", "Remplacer le système de chauffage", "Remplacer le système de régulation ancien"));
    }

}
