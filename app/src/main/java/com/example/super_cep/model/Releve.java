package com.example.super_cep.model;

import com.example.super_cep.model.Enveloppe.Eclairage;
import com.example.super_cep.model.Enveloppe.Menuiserie;
import com.example.super_cep.model.Enveloppe.Mur;
import com.example.super_cep.model.Enveloppe.Sol;
import com.example.super_cep.model.Enveloppe.Toiture;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Releve {

    private Map<String, Zone> zones;


    public Releve(){
        this.zones = new HashMap<>();
    }

    public void addZone(Zone zone){
        zones.put(zone.nom, zone);
    }

    public Zone[] getZones() {
        return zones.values().toArray(new Zone[0]);
    }

    public void removeZone(Zone zone) {
        zones.remove(zone.nom);
    }

    public Zone getZone(String name){
        if(!zones.containsKey(name))
            throw new IllegalArgumentException("La zone n'existe pas");
        return zones.get(name);
    }



}
