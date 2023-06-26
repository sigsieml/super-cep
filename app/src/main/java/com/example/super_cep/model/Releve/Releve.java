package com.example.super_cep.model.Releve;

import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Chauffage.Chauffage;
import com.example.super_cep.model.Releve.Enveloppe.Zone;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Releve {

    public Map<String, Zone> zones;

    public Map<String, Calendrier> calendriers;
    public Map<String, Chauffage> chauffages;
    public Map<String, Climatisation> climatisations;
    public Map<String, Ventilation> ventilations;

    public String nomBatiment = "";
    public Calendar dateDeConstruction;
    public Calendar dateDeDerniereRenovation;
    public float surfaceTotaleChauffe = 0;
    public String description = "";
    public String adresse = "";
    public Map<String, ECS> ecs;
    public Map<String, ApprovisionnementEnergetique> approvisionnementEnergetiques;
    public Map<String, Remarque> remarques;
    public List<String> preconisations;

    public String imageBatiment;


    public Releve(){
        this.zones = new HashMap<>();
        this.calendriers = new HashMap<>();
        this.chauffages = new HashMap<>();
        this.climatisations = new HashMap<>();
        this.ventilations = new HashMap<>();
        this.ecs = new HashMap<>();
        this.approvisionnementEnergetiques = new HashMap<>();
        this.remarques = new HashMap<>();
        this.preconisations = new ArrayList<>();
    }

    public void addZone(Zone zone){
        if(zones.containsKey(zone.nom))
            throw new IllegalArgumentException("La zone existe déjà");
        zones.put(zone.nom, zone);
    }

    @JsonIgnore
    public Zone[] getZonesValues() {
        return zones.values().toArray(new Zone[0]);
    }

    public void removeZone(Zone zone) {
        zones.remove(zone.nom);
    }

    public Zone getZone(String name){
        if(!zones.containsKey(name))
            throw new IllegalArgumentException("La zone n'existe pas + " + name);
        return zones.get(name);
    }

    @JsonIgnore
    public Calendrier[] getCalendriersValues(){
        return calendriers.values().toArray(new Calendrier[0]);
    }

    public void addCalendrier(Calendrier calendrier){
        if(calendriers.containsKey(calendrier.nom))
            throw new IllegalArgumentException("Le calendrier existe déjà");
        calendriers.put(calendrier.nom, calendrier);
    }


    @Override
    public String toString() {
        return "Releve{" +
                "zones=" + zones +
                ", nomBatiment='" + nomBatiment + '\'' +
                ", dateDeConstruction=" + dateDeConstruction +
                ", dateDeDerniereRenovation=" + dateDeDerniereRenovation +
                ", surfaceTotaleChauffe=" + surfaceTotaleChauffe +
                ", description='" + description + '\'' +
                ", adresse='" + adresse + '\'' +
                '}'+ "\n" +
                "calendriers=" + calendriers + "\n" +
                "chauffages=" + chauffages + "\n" +
                "climatisations=" + climatisations + "\n" +
                "ventilations=" + ventilations + "\n" +
                "ecs=" + ecs + "\n" +
                "approvisonnementEnergetique=" + approvisionnementEnergetiques + "}";

    }
}
