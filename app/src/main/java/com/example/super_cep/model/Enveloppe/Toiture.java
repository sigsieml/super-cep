package com.example.super_cep.model.Enveloppe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Toiture extends ZoneElement{

    public String typeToiture;
    public String typeMiseEnOeuvre;
    public String typeIsolant;
    public String niveauIsolation;
    public float epaisseurIsolant;


    @JsonCreator
    public Toiture(@JsonProperty("nom") String nom,
               @JsonProperty("typeToiture") String typeToiture,
               @JsonProperty("typeMiseEnOeuvre") String typeMiseEnOeuvre,
               @JsonProperty("typeIsolant") String typeIsolant,
               @JsonProperty("niveauIsolation") String niveauIsolation,
               @JsonProperty("epaisseurIsolant") float epaisseurIsolant,
               @JsonProperty("aVerifier") boolean aVerifier,
               @JsonProperty("note") String note,
               @JsonProperty("images") List<String> images) {
        super(nom, aVerifier,note, images);
        this.typeToiture = typeToiture;
        this.typeMiseEnOeuvre = typeMiseEnOeuvre;
        this.typeIsolant = typeIsolant;
        this.niveauIsolation = niveauIsolation;
        this.epaisseurIsolant = epaisseurIsolant;
    }

    @Override
    public String logo() {
        return "toiture";
    }

    @Override
    public String toString() {
        return "Toiture{" +
                "typeToiture='" + typeToiture + '\'' +
                ", typeMiseEnOeuvre='" + typeMiseEnOeuvre + '\'' +
                ", typeIsolant='" + typeIsolant + '\'' +
                ", niveauIsolation='" + niveauIsolation + '\'' +
                ", epaisseurIsolant=" + epaisseurIsolant +
                ", note='" + note + '\'' +
                ", aVerifier=" + aVerifier +
                ", uriImages=" + images +
                '}';
    }
}
