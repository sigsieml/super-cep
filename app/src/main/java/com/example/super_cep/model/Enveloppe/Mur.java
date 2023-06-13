package com.example.super_cep.model.Enveloppe;

public class Mur extends ZoneElement{


    private String typeMur;
    private String typeMiseEnOeuvre;
    private String typeIsolant;
    private String niveauIsolation;
    private float epaisseurIsolant;
    private String note;

    private boolean aVerifier;

    public Mur(String nom, String typeMur, String typeMiseEnOeuvre, String typeIsolant, String niveauIsolation, float epaisseurIsolant,boolean aVerifier, String note) {
        super(nom);
        this.typeMur = typeMur;
        this.typeMiseEnOeuvre = typeMiseEnOeuvre;
        this.typeIsolant = typeIsolant;
        this.niveauIsolation = niveauIsolation;
        this.epaisseurIsolant = epaisseurIsolant;
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
                '}';
    }
}
