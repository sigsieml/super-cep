package com.example.super_cep.model.Enveloppe;

import androidx.annotation.NonNull;

import com.example.super_cep.R;

public class ZoneElement {

    private String nom;

    public ZoneElement(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public String getImage(){
        return "mur";
    }

    @NonNull
    @Override
    public String toString() {
        return "ZoneElement{" +
                "nom='" + nom + '\'' + '}';
    }
}
