package com.example.super_cep.model.Releve.Enveloppe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Sol extends ZoneElement{

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
}
