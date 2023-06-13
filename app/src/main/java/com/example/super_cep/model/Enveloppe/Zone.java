package com.example.super_cep.model.Enveloppe;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Zone {

    public String nom;
    public List<ZoneElement> zoneElements;

    public Zone(String nom){
        this(nom, new ArrayList<>());
    }

    public Zone(String nom, List<ZoneElement> zoneElements) {
        this.nom = nom;
        this.zoneElements = zoneElements;
    }

    public void addZoneElement(ZoneElement zoneElementToAdd){
        for (ZoneElement zoneElement: zoneElements) {
            if(zoneElement.getNom().equals(zoneElementToAdd.getNom())){
                throw new IllegalArgumentException("Un élément de la zone porte déjà ce nom");
            }
        }
        zoneElements.add(zoneElementToAdd);
    }

    @NonNull
    @Override
    public String toString() {
        return "Zone{" +
                "nom='" + nom + '\'' +'}';
    }
}
