package com.example.super_cep.model.Releve.Chauffage;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Chauffage {




    public String nom;
    public String type;
    public float puissance;
    public int quantite;
    public String marque;
    public String modele;
    public List<String> zones;
    public String regulation;

    public CategorieChauffage categorie;


    public List<String> images;
    public boolean aVerifier;

    public String note;

    @JsonCreator
    public Chauffage(@JsonProperty("nom") String nom,
                     @JsonProperty("type") String type,
                        @JsonProperty("puissance") float puissance,
                        @JsonProperty("quantite") int quantite,
                        @JsonProperty("marque") String marque,
                     @JsonProperty("modele") String modele,
                     @JsonProperty("zones") List<String> zones,
                        @JsonProperty("categorie") CategorieChauffage categorie,
                        @JsonProperty("regulation") String regulation,
    @JsonProperty("images") List<String> images,
    @JsonProperty("aVerifier") boolean aVerifier,
    @JsonProperty("note") String note

    ) {
        this.nom = nom;
        this.type = type;
        this.puissance = puissance;
        this.quantite = quantite;
        this.marque = marque;
        this.modele = modele;
        this.zones = zones;
        this.categorie = categorie;
        this.regulation = regulation;
        this.images = images;
        this.aVerifier = aVerifier;
        this.note = note;

    }

}
