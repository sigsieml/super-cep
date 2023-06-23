package com.example.super_cep.model.Enveloppe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Eclairage extends ZoneElement{

    public String typeEclairage;
    public String typeDeRegulation;

    @JsonCreator
    public Eclairage(@JsonProperty("nom") String nom,
                     @JsonProperty("typeEclairage") String typeEclairage,
                     @JsonProperty("typeDeRegulation") String typeDeRegulation,
                     @JsonProperty("aVerifier") boolean aVerifier,
                     @JsonProperty("note") String note,
                     @JsonProperty("images") List<String> images) {
        super(nom, aVerifier,note, images);
        this.typeEclairage = typeEclairage;
        this.typeDeRegulation = typeDeRegulation;
    }

    @Override
    public String logo() {
        return "éclairage";
    }


    @Override
    public String toString() {
        return "Eclairage{" +
                "typeEclairage='" + typeEclairage + '\'' +
                ", typeDeRegulation='" + typeDeRegulation + '\'' +
                ", aVerifier=" + aVerifier +
                ", note='" + note + '\'' +
                ", uriImages=" + images +
                '}';
    }
}
