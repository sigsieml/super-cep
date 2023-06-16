package com.example.super_cep.model.Enveloppe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Toiture extends ZoneElement{

    @JsonCreator
    public Toiture(@JsonProperty("nom") String nom) {
        super(nom);
    }

    @Override
    public String logo() {
        return "toiture";
    }
}
