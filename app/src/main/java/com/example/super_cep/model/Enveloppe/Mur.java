package com.example.super_cep.model.Enveloppe;

import android.net.Uri;

import java.util.List;

public class Mur extends ZoneElement{


    public String typeMur;
    public String typeMiseEnOeuvre;
    public String typeIsolant;
    public String niveauIsolation;
    public float epaisseurIsolant;
    public String note;

    public List<Uri> uriImages;

    public boolean aVerifier;

    public Mur(String nom, String typeMur, String typeMiseEnOeuvre, String typeIsolant, String niveauIsolation, float epaisseurIsolant,boolean aVerifier, String note, List<Uri> uriImages) {
        super(nom);
        this.typeMur = typeMur;
        this.typeMiseEnOeuvre = typeMiseEnOeuvre;
        this.typeIsolant = typeIsolant;
        this.niveauIsolation = niveauIsolation;
        this.epaisseurIsolant = epaisseurIsolant;
        this.uriImages = uriImages;
        this.note = note;
    }

    @Override
    public String getImage() {
        return "mur";
    }


    @Override
    public String toString() {
        return "Mur{" +
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
