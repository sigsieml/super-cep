package com.example.super_cep.model.Releve.Enveloppe;

import androidx.annotation.NonNull;

import com.example.super_cep.model.Releve.ZoneElement;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Mur extends ZoneElement implements Cloneable {


    public String typeMur;
    public String typeMiseEnOeuvre;
    public String typeIsolant;
    public String niveauIsolation;
    public float epaisseurIsolant;

    @JsonCreator
    public Mur(@JsonProperty("nom") String nom,
               @JsonProperty("typeMur") String typeMur,
               @JsonProperty("typeMiseEnOeuvre") String typeMiseEnOeuvre,
               @JsonProperty("typeIsolant") String typeIsolant,
               @JsonProperty("niveauIsolation") String niveauIsolation,
               @JsonProperty("epaisseurIsolant") float epaisseurIsolant,
               @JsonProperty("aVerifier") boolean aVerifier,
               @JsonProperty("note") String note,
               @JsonProperty("images") List<String> images) {
        super(nom, aVerifier, note, images);
        this.typeMur = typeMur;
        this.typeMiseEnOeuvre = typeMiseEnOeuvre;
        this.typeIsolant = typeIsolant;
        this.niveauIsolation = niveauIsolation;
        this.epaisseurIsolant = epaisseurIsolant;
    }

    @JsonIgnore
    @Override
    public String logo() {
        return "mur";
    }

    @Override
    public String toString() {
        return "Mur{" +
                "nom='" + super.nom + '\'' +
                "typeMur='" + typeMur + '\'' +
                ", typeMiseEnOeuvre='" + typeMiseEnOeuvre + '\'' +
                ", typeIsolant='" + typeIsolant + '\'' +
                ", niveauIsolation='" + niveauIsolation + '\'' +
                ", epaisseurIsolant=" + epaisseurIsolant +
                ", note='" + note + '\'' +
                ", aVerifier=" + aVerifier +
                ", uriImages=" + images +
                '}';
    }

    @NonNull
    @Override
    public ZoneElement clone(){
        Mur clone = new Mur(nom, typeMur, typeMiseEnOeuvre, typeIsolant, niveauIsolation, epaisseurIsolant, aVerifier, note, images);
        return clone;
    }
}
