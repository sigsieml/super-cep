package com.example.super_cep.model;

import java.util.List;

public class Zone {

    public String nom;
    public List<ZoneElement> zoneElements;

    public Zone(String nom, List<ZoneElement> zoneElements) {
        this.nom = nom;
        this.zoneElements = zoneElements;
    }
    
}
