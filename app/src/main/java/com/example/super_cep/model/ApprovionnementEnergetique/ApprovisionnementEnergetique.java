package com.example.super_cep.model.ApprovionnementEnergetique;

import android.net.Uri;

import com.example.super_cep.model.Export.UriDeserializer;
import com.example.super_cep.model.Export.UriSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApprovisionnementEnergetiqueElectrique.class, name = "electrique"),
        @JsonSubTypes.Type(value = ApprovisionnementEnergetiqueGaz.class, name = "gaz"),
})
public class ApprovisionnementEnergetique {

    public String nom;
    public String energie;

    public List<String> zones;
    @JsonSerialize(using = UriSerializer.class)
    @JsonDeserialize(using = UriDeserializer.class)
    public List<Uri> uriImages;
    public boolean aVerifier;
    public String note;

    @JsonCreator
    public ApprovisionnementEnergetique(@JsonProperty("nom") String nom,
                                        @JsonProperty("energie") String energie,
                                        @JsonProperty("zones") List<String> zones,
                                        @JsonProperty("uriImages") List<Uri> uriImages,
                                        @JsonProperty("aVerifier") boolean aVerifier,
                                        @JsonProperty("note") String note) {
        this.nom = nom;
        this.energie = energie;
        this.zones = zones;
        this.uriImages = uriImages;
        this.aVerifier = aVerifier;
        this.note = note;
    }
}
