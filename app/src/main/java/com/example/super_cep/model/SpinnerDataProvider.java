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

    }

    public Map<String, List<String>> getSpinnersData(){
        return spinnersData;
    }
}
