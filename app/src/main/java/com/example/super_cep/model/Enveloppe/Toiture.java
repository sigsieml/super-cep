package com.example.super_cep.model.Enveloppe;

public class Toiture extends ZoneElement{
    public Toiture(String nom) {
        super(nom);
    }

    @Override
    public String getImage() {
        return "toiture";
    }
}
