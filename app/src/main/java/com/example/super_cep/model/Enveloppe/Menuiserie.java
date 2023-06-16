package com.example.super_cep.model.Enveloppe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Menuiserie extends ZoneElement{

    @JsonCreator
    public Menuiserie(@JsonProperty("nom") String nom) {
        super(nom);
    }

    @Override
    public String logo() {
        return "menuiserie";
    }
}
