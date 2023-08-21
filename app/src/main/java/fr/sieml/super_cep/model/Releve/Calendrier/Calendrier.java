package fr.sieml.super_cep.model.Releve.Calendrier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class Calendrier {

    public String nom;
    public List<String> zones;
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
