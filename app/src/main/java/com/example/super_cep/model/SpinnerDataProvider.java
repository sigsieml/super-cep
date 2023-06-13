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
    }

    public Map<String, List<String>> getSpinnersData(){
        return spinnersData;
    }
}
