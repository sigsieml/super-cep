package com.example.super_cep.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Climatisation {


    public String nom;
    public String type;
    public float puissance;
    public int quantite;
    public String marque;
    public String modele;
    public String regulation;

    public List<String> zones;


    public List<String> images;
    public boolean aVerifier;
    public String note;


    @JsonCreator
    public Climatisation(@JsonProperty("nom") String nom,
                         @JsonProperty("type") String type,
                         @JsonProperty("puissance") float puissance,
                         @JsonProperty("quantite") int quantite,
                         @JsonProperty("marque") String marque,
                         @JsonProperty("modele") String modele,
                         @JsonProperty("regulation") String regulation,
                         @JsonProperty("zones") List<String> zones,
                         @JsonProperty("images") List<String> images,
                         @JsonProperty("aVerifier") boolean aVerifier,
                         @JsonProperty("note") String note) {
        this.nom = nom;
        this.type = type;
        this.puissance = puissance;
        this.quantite = quantite;
        this.marque = marque;
        this.modele = modele;
        this.regulation = regulation;
        this.zones = zones;
        this.images = images;
        this.aVerifier = aVerifier;
        this.note = note;

    }
}
