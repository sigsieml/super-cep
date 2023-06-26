package com.example.super_cep.model.Releve.ApprovionnementEnergetique;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ApprovisionnementEnergetiqueGaz extends ApprovisionnementEnergetique {


    public String numeroRAE;


    @JsonCreator
    public ApprovisionnementEnergetiqueGaz(@JsonProperty("nom") String nom,
                                           @JsonProperty("energie") String energie,
                                           @JsonProperty("numeroRAE") String numeroRAE,
                                           @JsonProperty("zones") List<String> zones,
                                           @JsonProperty("images") List<String> images,
                                           @JsonProperty("aVerifier") boolean aVerifier,
                                           @JsonProperty("note") String note) {
        super(nom, energie, zones, images, aVerifier, note);
        this.numeroRAE = numeroRAE;
    }
}
