package com.example.super_cep.model.ApprovionnementEnergetique;

import android.net.Uri;

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
                                           @JsonProperty("uriImages") List<Uri> uriImages,
                                           @JsonProperty("aVerifier") boolean aVerifier,
                                           @JsonProperty("note") String note) {
        super(nom, energie, zones, uriImages, aVerifier, note);
        this.numeroRAE = numeroRAE;
    }
}
