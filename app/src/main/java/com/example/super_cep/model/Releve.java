package com.example.super_cep.model;

import com.example.super_cep.model.Enveloppe.Zone;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Releve {

    private Map<String, Zone> zones;

    public String nomBatiment = "";
    public Calendar dateDeConstruction = Calendar.getInstance();
    public Calendar dateDeDerniereRenovation = Calendar.getInstance();
    public float surfaceTotaleChauffe = 0;
    public String description = "";
    public String adresse = "";


    public Releve(){
        this.zones = new HashMap<>();
    }

    public void addZone(Zone zone){
        if(zones.containsKey(zone.nom))
            throw new IllegalArgumentException("La zone existe déjà");
        zones.put(zone.nom, zone);
    }


    public Map<String, Zone> getZones(){
        return zones;
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
