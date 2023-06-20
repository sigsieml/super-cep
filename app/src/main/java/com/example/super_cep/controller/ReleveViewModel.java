package com.example.super_cep.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.super_cep.model.Calendrier.Calendrier;
import com.example.super_cep.model.Chauffage;
import com.example.super_cep.model.Climatisation;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;
import com.example.super_cep.model.Releve;

import java.util.Arrays;
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

    public void addCalendrier(Calendrier calendrier){
        releve.getValue().addCalendrier(calendrier);
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

    public void updateCalendrier(String oldName, Calendrier calendrier) {
        Releve releve = this.releve.getValue();
        if(!calendrier.nom.equals(oldName) && releve.calendriers.containsKey(calendrier.nom)){
            throw new IllegalArgumentException("Un calendrier porte déjà ce nom : " + calendrier.nom);
        }
        releve.calendriers.remove(oldName);
        releve.addCalendrier(calendrier);
        forceUpdateReleve();
    }


    public void supprimerCalendrier(String nomCalendrier) {
        releve.getValue().calendriers.remove(nomCalendrier);
        forceUpdateReleve();
    }

    public void removeChauffage(String nomChauffage) {
        releve.getValue().chauffages.remove(nomChauffage);
        forceUpdateReleve();
    }

    public void addChauffage(Chauffage chauffage) {
        if(releve.getValue().chauffages.containsKey(chauffage.nom)){
            throw new IllegalArgumentException("Un chauffage porte déjà ce nom : " + chauffage.nom);
        }
        releve.getValue().chauffages.put(chauffage.nom, chauffage);
        forceUpdateReleve();
    }

    public void editChauffage(String oldName, Chauffage chauffage) {

        if(!chauffage.nom.equals(oldName) && releve.getValue().chauffages.containsKey(chauffage.nom)){
            throw new IllegalArgumentException("Un calendrier porte déjà ce nom : " + chauffage.nom);
        }
        releve.getValue().chauffages.remove(oldName);
        releve.getValue().chauffages.put(chauffage.nom, chauffage);
        forceUpdateReleve();
    }

    public void removeClimatisation(String nomClimatisation) {
        releve.getValue().climatisations.remove(nomClimatisation);
        forceUpdateReleve();
    }

    public void addClimatisation(Climatisation climatisationFromViews) {
        if(releve.getValue().climatisations.containsKey(climatisationFromViews.nom)){
            throw new IllegalArgumentException("Une climatisation porte déjà ce nom : " + climatisationFromViews.nom);
        }
        releve.getValue().climatisations.put(climatisationFromViews.nom, climatisationFromViews);
        forceUpdateReleve();
    }

    public void editClimatisation(String oldName, Climatisation climatisation) {
        if(!climatisation.nom.equals(oldName) && releve.getValue().climatisations.containsKey(climatisation.nom)){
            throw new IllegalArgumentException("Un calendrier porte déjà ce nom : " + climatisation.nom);
        }
        releve.getValue().climatisations.remove(oldName);
        releve.getValue().climatisations.put(climatisation.nom, climatisation);
        forceUpdateReleve();
    }
}
