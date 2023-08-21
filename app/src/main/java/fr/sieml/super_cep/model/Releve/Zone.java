package fr.sieml.super_cep.model.Releve;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Zone {

    public String nom;
    private final Map<String, ZoneElement> zoneElements;

    @JsonCreator
    public Zone(@JsonProperty("nom") String nom){
        this(nom, new ArrayList<>());
    }

    public Zone(String nom, List<ZoneElement> zoneElements) {
        this.nom = nom;

        this.zoneElements = new HashMap<>();
        for (ZoneElement zoneElement: zoneElements) {
            this.zoneElements.put(zoneElement.nom, zoneElement);
        }
    }

    public Map<String, ZoneElement> getZoneElements(){
        return zoneElements;
    }

    @JsonIgnore
    public ZoneElement[] getZoneElementsValues(){
        return zoneElements.values().toArray(new ZoneElement[0]);
    }

    public void addZoneElement(ZoneElement zoneElementToAdd){
        for (ZoneElement zoneElement: zoneElements.values()) {
            if(zoneElement.nom.equals(zoneElementToAdd.nom)){
                throw new IllegalArgumentException("Un élément de la zone porte déjà ce nom");
            }
        }
        zoneElements.put(zoneElementToAdd.nom, zoneElementToAdd);
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
                "nom='" + nom + '\'' +
                ", zoneElements=" + zoneElements +
                '}';
    }
}
