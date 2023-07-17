package com.example.super_cep.controller.Conso;

public enum Energie {
    //{"Electricité", "Butane", "Réseau de chaleur", "Gaz Naturel", "Bois Granulés", "Réseau de froid", "Fioul", "Bois Plaquettes", "Essence", "Propane", "Bois Bûches", "Gazole"}
    ELECTRICITE("Electricité"),
    BUTANE("Butane"),
    RESEAU_DE_CHALEUR("Réseau de chaleur"),
    GAZ_NATUREL("Gaz Naturel"),
    BOIS_GRANULES("Bois Granulés"),
    RESEAU_DE_FROID("Réseau de froid"),
    FIOUL("Fioul"),
    BOIS_PLAQUETTES("Bois Plaquettes"),
    ESSENCE("Essence"),
    PROPANE("Propane"),
    BOIS_BUCHES("Bois Bûches"),
    GAZOLE("Gazole");


    public static final String[] ENERGIES = {"Electricité", "Butane", "Réseau de chaleur", "Gaz Naturel", "Bois Granulés", "Réseau de froid", "Fioul", "Bois Plaquettes", "Essence", "Propane", "Bois Bûches", "Gazole"};
    public String nomEnergie;

    Energie(String nomEnergie) {
        this.nomEnergie = nomEnergie;
    }
}
