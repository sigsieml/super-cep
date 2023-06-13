package com.example.super_cep.model.Enveloppe;

public class Eclairage extends ZoneElement{

    public Eclairage(String nom) {
        super(nom);
    }

    @Override
    public String getImage() {
        return "éclairage";
    }
}
