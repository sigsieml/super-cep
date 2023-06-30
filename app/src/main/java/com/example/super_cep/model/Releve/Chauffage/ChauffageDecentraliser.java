package com.example.super_cep.model.Releve.Chauffage;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ChauffageDecentraliser extends Chauffage{

    public String zone;

    @JsonCreator
    public ChauffageDecentraliser(@JsonProperty("nom") String nom,
                     @JsonProperty("type") String type,
                     @JsonProperty("puissance") float puissance,
                     @JsonProperty("quantite") int quantite,
                     @JsonProperty("marque") String marque,
                     @JsonProperty("modele") String modele,
                        @JsonProperty("zone") String zone,
                     @JsonProperty("categorie") CategorieChauffage categorie,
                     @JsonProperty("regulation") String regulation,
                     @JsonProperty("images") List<String> images,
                     @JsonProperty("aVerifier") boolean aVerifier,
                     @JsonProperty("note") String note
    ) {
        super(nom, type, puissance, quantite, marque, modele, categorie, regulation, images, aVerifier, note);
        this.zone = zone;
    }

    @Override
    public String getZoneText() {
        return zone;
    }

    @NonNull
    @Override
    public Chauffage clone() {
        return new ChauffageDecentraliser(nom, type, puissance, quantite, marque, modele, zone, categorie, regulation, new ArrayList<>(images), aVerifier, note);
    }
}
