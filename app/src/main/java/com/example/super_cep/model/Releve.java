package com.example.super_cep.model;

import com.example.super_cep.model.Calendrier.Calendrier;
import com.example.super_cep.model.Enveloppe.Zone;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.ObjectInputStream;
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
    public Calendar dateDeConstruction = Calendar.getInstance();
    public Calendar dateDeDerniereRenovation = Calendar.getInstance();
    public float surfaceTotaleChauffe = 0;
    public String description = "";
    public String adresse = "";
    public Map<String, ECS> ecs;


    public Releve(){
        this.zones = new HashMap<>();
        this.calendriers = new HashMap<>();
        this.chauffages = new HashMap<>();
        this.climatisations = new HashMap<>();
        this.ventilations = new HashMap<>();
        this.ecs = new HashMap<>();
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
                '}';
    }
}
