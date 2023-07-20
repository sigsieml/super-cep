package com.example.super_cep.model.Releve.ApprovionnementEnergetique;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Class représentant un approvisionnement énergétique électrique
 * <p>
 *     Cette classe est une sous-classe de {@link ApprovisionnementEnergetique}
 *     Elle est utilisée pour représenter un approvisionnement énergétique électrique
 * </p>
 */
public class ApprovisionnementEnergetiqueElectrique extends ApprovisionnementEnergetique {

    public float puissance;
    public String formuleTarifaire;

    @JsonCreator
    public ApprovisionnementEnergetiqueElectrique(@JsonProperty("nom") String nom,
                                                  @JsonProperty("energie") String energie,
                                                  @JsonProperty("puissance") float puissance,
                                                  @JsonProperty("formuleTarifaire") String formuleTarifaire,
                                                  @JsonProperty("zones") List<String> zones,
                                                  @JsonProperty("images") List<String> images,
                                                  @JsonProperty("aVerifier") boolean aVerifier,
                                                  @JsonProperty("note") String note) {
        super(nom,energie,  zones, images, aVerifier, note);
        this.puissance = puissance;
        this.formuleTarifaire = formuleTarifaire;
    }

}
