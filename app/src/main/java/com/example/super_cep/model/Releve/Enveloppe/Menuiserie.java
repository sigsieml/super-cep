package com.example.super_cep.model.Releve.Enveloppe;

import com.example.super_cep.model.Releve.ZoneElement;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Menuiserie extends ZoneElement {

    public String typeMenuiserie;
    public String materiau;
    public String protectionsSolaires;
    public String typeVitrage;

    @JsonCreator
    public Menuiserie(@JsonProperty("nom") String nom,
                     @JsonProperty("typeMenuiserie") String typeMenuiserie,
                     @JsonProperty("materiau") String materiau,
                     @JsonProperty("protectionsSolaires") String protectionsSolaires,
                      @JsonProperty("typeVitrage") String typeVitrage,
                     @JsonProperty("aVerifier") boolean aVerifier,
                     @JsonProperty("note") String note,
                     @JsonProperty("images") List<String> images) {
        super(nom, aVerifier,note, images);
        this.typeMenuiserie = typeMenuiserie;
        this.materiau = materiau;
        this.protectionsSolaires = protectionsSolaires;
        this.typeVitrage = typeVitrage;
    }


    @Override
    public String logo() {
        return "menuiserie";
    }

    @Override
    public String toString() {
        return "Menuiserie{" +
                "typeMenuiserie='" + typeMenuiserie + '\'' +
                ", materiau='" + materiau + '\'' +
                ", protectionsSolaires='" + protectionsSolaires + '\'' +
                ", typeVitrage='" + typeVitrage + '\'' +
                ", aVerifier=" + aVerifier +
                ", note='" + note + '\'' +
                ", uriImages=" + images +
                '}';
    }
}
