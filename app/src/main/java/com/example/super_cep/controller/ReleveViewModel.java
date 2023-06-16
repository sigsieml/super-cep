package com.example.super_cep.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;
import com.example.super_cep.model.Releve;

import java.util.Calendar;
import java.util.Date;

public class ReleveViewModel extends ViewModel {

    private final MutableLiveData<Releve> releve = new MutableLiveData<>();

    public void setReleve(Releve releve){
        this.releve.setValue(releve);
    }

    public LiveData<Releve> getReleve(){
        return releve;
    }

    public void addZone(Zone zone){
        releve.getValue().addZone(zone);
        forceUpdateReleve();
    }

    public void forceUpdateReleve(){
        releve.setValue(releve.getValue());
    }

    public void deleteZone(Zone zone) {
        releve.getValue().removeZone(zone);
        forceUpdateReleve();
    }

    public void setNomBatiment(String nomBatiment){
        releve.getValue().nomBatiment = nomBatiment;
        forceUpdateReleve();
    }

    public void setDateDeConstruction(Calendar dateDeConstruction){
        releve.getValue().dateDeConstruction = dateDeConstruction;
        forceUpdateReleve();
    }

    public void setDateDeDerniereRenovation(Calendar dateDeDerniereRenovation){
        releve.getValue().dateDeDerniereRenovation = dateDeDerniereRenovation;
        forceUpdateReleve();
    }

    public void setSurfaceTotaleChauffe(float surfaceTotaleChauffe){
        releve.getValue().surfaceTotaleChauffe = surfaceTotaleChauffe;
        forceUpdateReleve();
    }

    public void setDescription(String description){
        releve.getValue().description = description;
        forceUpdateReleve();
    }

    public void setAdresse(String adresse){
        releve.getValue().adresse = adresse;
        forceUpdateReleve();
    }

    public void editZoneElement(String oldNomZoneElement,String nomZone, ZoneElement zoneElement){
        getReleve().getValue().getZone(nomZone).removeZoneElement(oldNomZoneElement);
        getReleve().getValue().getZone(nomZone).addZoneElement(zoneElement);
        forceUpdateReleve();
    }
    public void removeZoneElement(String nomZone, String nomZoneElement){
        getReleve().getValue().getZone(nomZone).removeZoneElement(nomZoneElement);
        forceUpdateReleve();
    }

    public void moveZoneElement(String nomZoneElement, String nomPreviousZone, String nomNewZone) {
        Releve releve = this.releve.getValue();
        Zone previousZone = releve.getZone(nomPreviousZone);
        Zone newZone = releve.getZone(nomNewZone);
        ZoneElement zoneElement = previousZone.getZoneElement(nomZoneElement);

        if(newZone.getZoneElement(nomZoneElement) != null){
            throw new IllegalArgumentException("Un élément de la zone porte déjà ce nom");
        }

        previousZone.removeZoneElement(nomZoneElement);
        newZone.addZoneElement(zoneElement);


        forceUpdateReleve();

    }
}
