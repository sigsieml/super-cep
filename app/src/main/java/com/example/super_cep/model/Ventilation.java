package com.example.super_cep.model;

import android.net.Uri;

import com.example.super_cep.model.Export.UriDeserializer;
import com.example.super_cep.model.Export.UriSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

public class Ventilation {

    public String nom;
    public String type;
    public String regulation;

    public List<String> zones;


    @JsonSerialize(using = UriSerializer.class)
    @JsonDeserialize(using = UriDeserializer.class)
    public List<Uri> uriImages;
    public boolean aVerifier;
    public String note;


    @JsonCreator
    public Ventilation(@JsonProperty("nom") String nom,
                       @JsonProperty("type") String type,
                       @JsonProperty("regulation") String regulation,
                       @JsonProperty("zones") List<String> zones,
                       @JsonProperty("uriImages") List<Uri> uriImages,
                       @JsonProperty("aVerifier") boolean aVerifier,
                       @JsonProperty("note") String note) {
        this.nom = nom;
        this.type = type;
        this.regulation = regulation;
        this.zones = zones;
        this.uriImages = uriImages;
        this.aVerifier = aVerifier;
        this.note = note;
    }

}
