package com.example.super_cep.model.Calendrier;

import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Export.CalendrierDateKeyDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;

public class Calendrier {

    public String nom;
    public List<String> zones;
    @JsonDeserialize(keyUsing = CalendrierDateKeyDeserializer.class)
    public Map<CalendrierDate, ChaufferOccuper> calendrierDateChaufferOccuperMap;

    @JsonCreator
    public Calendrier(@JsonProperty("nom") String nom,@JsonProperty("zones") List<String> zones, @JsonProperty("calendrierDateChaufferOccuperMap") Map<CalendrierDate, ChaufferOccuper> calendrierDateChaufferOccuperMap) {
        this.nom = nom;
        this.zones = zones;
        this.calendrierDateChaufferOccuperMap = calendrierDateChaufferOccuperMap;
    }

    @Override
    public String toString() {
        return "Calendrier{" +
                "nom='" + nom + '\'' +
                ", zones=" + zones +
                ", calendrierDateChaufferOccuperMap=" + calendrierDateChaufferOccuperMap +
                '}';
    }
}
