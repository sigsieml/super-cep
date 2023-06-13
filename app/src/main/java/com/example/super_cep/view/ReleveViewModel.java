package com.example.super_cep.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Releve;

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

    public void setDateDeConstruction(Date dateDeConstruction){
        releve.getValue().dateDeConstruction = dateDeConstruction;
        forceUpdateReleve();
    }

    public void setDateDeDerniereRenovation(Date dateDeDerniereRenovation){
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

}