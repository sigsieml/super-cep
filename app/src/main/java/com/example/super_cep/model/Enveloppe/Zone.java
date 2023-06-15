package com.example.super_cep.model.Enveloppe;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Zone {

    public String nom;
    private Map<String, ZoneElement> zoneElements;

    public Zone(String nom){
        this(nom, new ArrayList<>());
    }

    public Zone(String nom, List<ZoneElement> zoneElements) {
        this.nom = nom;

        this.zoneElements = new HashMap<>();
        for (ZoneElement zoneElement: zoneElements) {
            this.zoneElements.put(zoneElement.getNom(), zoneElement);
        }
    }

    public ZoneElement[] getZoneElements(){
        return zoneElements.values().toArray(new ZoneElement[0]);
    }

    public void addZoneElement(ZoneElement zoneElementToAdd){
        for (ZoneElement zoneElement: zoneElements.values()) {
            if(zoneElement.getNom().equals(zoneElementToAdd.getNom())){
                throw new IllegalArgumentException("Un élément de la zone porte déjà ce nom");
            }
        }
        zoneElements.put(zoneElementToAdd.getNom(), zoneElementToAdd);
    }

    public void removeZoneElement(String nomZoneElement){
        zoneElements.remove(nomZoneElement);
    }

    public ZoneElement getZoneElement(String nomZoneElement){
        return zoneElements.get(nomZoneElement);
    }

    @NonNull
    @Override
    public String toString() {
        return "Zone{" +
                "nom='" + nom + '\'' +'}';
    }
}
