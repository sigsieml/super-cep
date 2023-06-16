package com.example.super_cep.model.Enveloppe;

import android.net.Uri;

import com.example.super_cep.model.Export.UriDeserializer;
import com.example.super_cep.model.Export.UriSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

public class Mur extends ZoneElement{


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
               @JsonProperty("uriImages") List<Uri> uriImages) {
        super(nom, aVerifier, note, uriImages);
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
                "nom='" + super.getNom() + '\'' +
                "typeMur='" + typeMur + '\'' +
                ", typeMiseEnOeuvre='" + typeMiseEnOeuvre + '\'' +
                ", typeIsolant='" + typeIsolant + '\'' +
                ", niveauIsolation='" + niveauIsolation + '\'' +
                ", epaisseurIsolant=" + epaisseurIsolant +
                ", note='" + note + '\'' +
                ", aVerifier=" + aVerifier +
                ", uriImages=" + uriImages +
                '}';
    }
}
