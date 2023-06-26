package com.example.super_cep.model.Releve;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Remarque implements Comparable<Remarque>{


    public String nom;
    public String description;
    public boolean personalise;

    @JsonCreator
    public Remarque(@JsonProperty("nom")String  nom, @JsonProperty("description") String description, @JsonProperty("personalise") boolean personalise){
        this.nom = nom;
        this.description = description;
        this.personalise = personalise;
    }


    @Override
    public int compareTo(Remarque o) {
        return this.nom.compareTo(o.nom);
    }
}
