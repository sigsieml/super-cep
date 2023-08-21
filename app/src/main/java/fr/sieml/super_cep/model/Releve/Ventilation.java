package fr.sieml.super_cep.model.Releve;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Ventilation {

    public String nom;
    public String type;
    public String regulation;

    public List<String> zones;


    public List<String> images;
    public boolean aVerifier;
    public String note;


    @JsonCreator
    public Ventilation(@JsonProperty("nom") String nom,
                       @JsonProperty("type") String type,
                       @JsonProperty("regulation") String regulation,
                       @JsonProperty("zones") List<String> zones,
                       @JsonProperty("images") List<String> images,
                       @JsonProperty("aVerifier") boolean aVerifier,
                       @JsonProperty("note") String note) {
        this.nom = nom;
        this.type = type;
        this.regulation = regulation;
        this.zones = zones;
        this.images = images;
        this.aVerifier = aVerifier;
        this.note = note;
    }

}
