package com.example.super_cep.model.Releve.Enveloppe;

import androidx.annotation.NonNull;

import com.example.super_cep.model.Releve.ZoneElement;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Sol extends ZoneElement implements Cloneable {

    public String typeSol;
    public String niveauIsolation;
    public String typeIsolant;
    public float epaisseurIsolant;



    @JsonCreator
    public Sol(@JsonProperty("nom") String nom,
                     @JsonProperty("typeSol") String typeSol,
                     @JsonProperty("niveauIsolation") String niveauIsolation,
                     @JsonProperty("typeIsolant") String typeIsolant,
                     @JsonProperty("epaisseurIsolant") float epaisseurIsolant,
                     @JsonProperty("aVerifier") boolean aVerifier,
                     @JsonProperty("note") String note,
                     @JsonProperty("images") List<String> images) {
        super(nom, aVerifier,note, images);
        this.typeSol = typeSol;
        this.niveauIsolation = niveauIsolation;
        this.typeIsolant = typeIsolant;
        this.epaisseurIsolant = epaisseurIsolant;
    }


    @Override
    public String logo() {
        return "sol";
    }

    @NonNull
    @Override
    public ZoneElement clone(){
        return new Sol(nom, typeSol, niveauIsolation, typeIsolant, epaisseurIsolant, aVerifier, note, images);
    }
}
