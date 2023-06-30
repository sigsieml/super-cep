package com.example.super_cep.model.Releve.Chauffage;


import androidx.annotation.NonNull;

import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueElectrique;
import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueGaz;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.apache.harmony.luni.util.NotImplementedException;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "centralisation")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChauffageCentraliser.class, name = "centraliser"),
        @JsonSubTypes.Type(value = ChauffageDecentraliser.class, name = "decentraliser"),
})
public abstract class Chauffage implements Cloneable {
    public String nom;
    public String type;
    public float puissance;
    public int quantite;
    public String marque;
    public String modele;
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
        this.categorie = categorie;
        this.regulation = regulation;
        this.images = images;
        this.aVerifier = aVerifier;
        this.note = note;

    }

    @JsonIgnore
    public abstract String getZoneText();

    @NonNull
    @Override
    public Chauffage clone() {
        throw new NotImplementedException();
    }
}
