package com.example.super_cep.model.Enveloppe;

public class Menuiserie extends ZoneElement{
    public Menuiserie(String nom) {
        super(nom);
    }

    @Override
    public String getImage() {
        return "menuiserie";
    }
}
