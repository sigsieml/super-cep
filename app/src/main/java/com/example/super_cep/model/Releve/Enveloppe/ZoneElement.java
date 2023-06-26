package com.example.super_cep.model.Releve.Enveloppe;


import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Mur.class, name = "mur"),
        @JsonSubTypes.Type(value = Sol.class, name = "sol"),
        @JsonSubTypes.Type(value = Eclairage.class, name = "eclairage"),
        @JsonSubTypes.Type(value = Toiture.class, name = "toiture"),
        @JsonSubTypes.Type(value = Menuiserie.class, name = "menuiserie"),
})
public class ZoneElement {

    private String nom;
    public boolean aVerifier;

    public String note;
    public List<String> images;


    @JsonCreator
    public ZoneElement(@JsonProperty("nom") String nom, @JsonProperty("aVerifier") boolean aVerifier, @JsonProperty("note") String note, @JsonProperty("uriImages") List<String> images) {
        this.nom = nom;
        this.aVerifier = aVerifier;
        this.images = images;
        this.note = note;
    }

    public String getNom() {
        return nom;
    }

    @JsonIgnore
    public String logo(){
        return "mur";
    }



    @NonNull
    @Override
    public String toString() {
        return "ZoneElement{" +
                "nom='" + nom + '\'' + '}';
    }
}
