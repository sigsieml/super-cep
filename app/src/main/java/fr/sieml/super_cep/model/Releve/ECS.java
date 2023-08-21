package fr.sieml.super_cep.model.Releve;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ECS {


    public String nom;
    public String type;
    public String modele;
    public String marque;
    public float volume;
    public int quantite;



    public List<String> zones;

    public List<String> images;
    public boolean aVerifier;
    public String note;


    @JsonCreator
    public ECS(@JsonProperty("nom") String nom,
               @JsonProperty("type") String type,
               @JsonProperty("modele") String modele,
               @JsonProperty("marque") String marque,
               @JsonProperty("volume") float volume,
               @JsonProperty("quantite") int quantite,
               @JsonProperty("zones") List<String> zones,
               @JsonProperty("images") List<String> images,
               @JsonProperty("aVerifier") boolean aVerifier,
               @JsonProperty("note") String note) {
        this.nom = nom;
        this.type = type;
        this.modele = modele;
        this.marque = marque;
        this.volume = volume;
        this.quantite = quantite;
        this.zones = zones;
        this.images = images;
        this.aVerifier = aVerifier;
        this.note = note;
    }

}
