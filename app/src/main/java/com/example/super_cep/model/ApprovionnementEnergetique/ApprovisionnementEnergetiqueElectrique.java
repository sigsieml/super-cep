package com.example.super_cep.model.ApprovionnementEnergetique;

import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ApprovisionnementEnergetiqueElectrique extends ApprovisionnementEnergetique {

    public float puissance;
    public String formuleTarifaire;
    public String numeroPDL;


    @JsonCreator
    public ApprovisionnementEnergetiqueElectrique(@JsonProperty("nom") String nom,
                                                  @JsonProperty("energie") String energie,
                                                  @JsonProperty("puissance") float puissance,
                                                  @JsonProperty("formuleTarifaire") String formuleTarifaire,
                                                  @JsonProperty("numeroPDL") String numeroPDL,
                                                  @JsonProperty("zones") List<String> zones,
                                                  @JsonProperty("uriImages") List<Uri> uriImages,
                                                  @JsonProperty("aVerifier") boolean aVerifier,
                                                  @JsonProperty("note") String note) {
        super(nom,energie,  zones, uriImages, aVerifier, note);
        this.puissance = puissance;
        this.formuleTarifaire = formuleTarifaire;
        this.numeroPDL = numeroPDL;
    }

}
