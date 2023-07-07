package com.example.super_cep.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Chauffage.Chauffage;
import com.example.super_cep.model.Releve.Chauffage.ChauffageCentraliser;
import com.example.super_cep.model.Releve.Chauffage.ChauffageDecentraliser;
import com.example.super_cep.model.Releve.Climatisation;
import com.example.super_cep.model.Releve.ECS;
import com.example.super_cep.model.Releve.Zone;
import com.example.super_cep.model.Releve.ZoneElement;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.model.Releve.Remarque;
import com.example.super_cep.model.Releve.Ventilation;

import java.util.Calendar;
import java.util.List;

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

    public void deleteZone(String nomZone) {
        if(!releve.getValue().zones.containsKey(nomZone)){
            throw new IllegalArgumentException("La zone n'existe pas");
        }
        Releve releve = this.releve.getValue();
        releve.zones.remove(nomZone);
        for (Ventilation ventilation : releve.ventilations.values()) {
            ventilation.zones.remove(nomZone);
        }
        for (Climatisation climatisation : releve.climatisations.values()) {
            climatisation.zones.remove(nomZone);
        }
        for (Chauffage chauffage : releve.chauffages.values().toArray(new Chauffage[0])) {
            if(chauffage instanceof ChauffageCentraliser){
                ((ChauffageCentraliser) chauffage).zones.remove(nomZone);
            }
            if(chauffage instanceof ChauffageDecentraliser){
                if(((ChauffageDecentraliser) chauffage).zone.equals(nomZone)){
                    releve.chauffages.remove(chauffage.nom);
                }
            }
        }
        setReleve(releve);
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

    public void setAdresse(String adresse){
        releve.getValue().adresse = adresse;
        forceUpdateReleve();
    }

    public void setLocalisation(double[] localisation){
        releve.getValue().localisation = localisation;
        forceUpdateReleve();
    }
    public void editZoneElement(String oldNameZoneElement,String nomZone, ZoneElement zoneElement){
        Releve releve = this.releve.getValue();
        if(!oldNameZoneElement.equals(zoneElement.nom) && isZoneElementNameAlereadyUsed(zoneElement.nom)){
            throw new IllegalArgumentException("Un élément porte déjà ce nom");
        }
        releve.getZone(nomZone).removeZoneElement(oldNameZoneElement);
        releve.getZone(nomZone).addZoneElement(zoneElement);
        forceUpdateReleve();
    }
    public void removeZoneElement(String nomZone, String nomZoneElement){
        getReleve().getValue().getZone(nomZone).removeZoneElement(nomZoneElement);
        forceUpdateReleve();
    }

    public void addCalendrier(Calendrier calendrier){
        if(releve.getValue().calendriers.containsKey(calendrier.nom)){
            throw new IllegalArgumentException("Un calendrier porte déjà ce nom");
        }
        releve.getValue().addCalendrier(calendrier);
        forceUpdateReleve();
    }

    public void addZoneElement(String nomZone, ZoneElement zoneElementFromViews) {
        Releve releve = this.releve.getValue();
        if(isZoneElementNameAlereadyUsed(zoneElementFromViews.nom)){
            throw new IllegalArgumentException("Un élément porte déjà ce nom");
        }
        releve.getZone(nomZone).addZoneElement(zoneElementFromViews);
        setReleve(releve);
    }

    public void moveZoneElement(String nomZoneElement, String nomPreviousZone, String nomNewZone) {
        Releve releve = this.releve.getValue();
        Zone previousZone = releve.getZone(nomPreviousZone);
        Zone newZone = releve.getZone(nomNewZone);
        ZoneElement zoneElement = previousZone.getZoneElement(nomZoneElement);

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
            throw new IllegalArgumentException("Un chauffage porte déjà ce nom : " + chauffage.nom);
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

    public void removeVentilation(String ventilation) {
        releve.getValue().ventilations.remove(ventilation);
        forceUpdateReleve();
    }

    public void addVentilation(Ventilation ventilationFromViews) {
        if(releve.getValue().ventilations.containsKey(ventilationFromViews.nom)){
            throw new IllegalArgumentException("Une ventilation porte déjà ce nom : " + ventilationFromViews.nom);
        }
        releve.getValue().ventilations.put(ventilationFromViews.nom, ventilationFromViews);
        forceUpdateReleve();
    }

    public void editVentilation(String oldName, Ventilation ventilation) {
        if(!ventilation.nom.equals(oldName) && releve.getValue().ventilations.containsKey(ventilation.nom)){
            throw new IllegalArgumentException("Une ventilation porte déjà ce nom : " + ventilation.nom);
        }
        releve.getValue().ventilations.remove(oldName);
        releve.getValue().ventilations.put(ventilation.nom, ventilation);
        forceUpdateReleve();
    }

    public void removeECS(String nomECS) {
        releve.getValue().ecs.remove(nomECS);
        forceUpdateReleve();
    }

    public void addECS(ECS ecsFromViews) {
        if(releve.getValue().ecs.containsKey(ecsFromViews.nom)){
            throw new IllegalArgumentException("Un ECS porte déjà ce nom : " + ecsFromViews.nom);
        }
        releve.getValue().ecs.put(ecsFromViews.nom, ecsFromViews);
        forceUpdateReleve();
    }

    public void editECS(String nomECS, ECS ecsFromViews) {
        if(!ecsFromViews.nom.equals(nomECS) && releve.getValue().ecs.containsKey(ecsFromViews.nom)){
            throw new IllegalArgumentException("Un ECS porte déjà ce nom : " + ecsFromViews.nom);
        }
        releve.getValue().ecs.remove(nomECS);
        releve.getValue().ecs.put(ecsFromViews.nom, ecsFromViews);
        forceUpdateReleve();
    }

    public void removeApprovisionnementEnergetique(String nomApprovisionnementEnergetique) {
        releve.getValue().approvisionnementEnergetiques.remove(nomApprovisionnementEnergetique);
        forceUpdateReleve();
    }

    public void addApprovisionnementEnergetique(ApprovisionnementEnergetique approvisionnementEnergetiqueFromViews) {
        if(releve.getValue().approvisionnementEnergetiques.containsKey(approvisionnementEnergetiqueFromViews.nom)){
            throw new IllegalArgumentException("Un approvisionnement énergétique porte déjà ce nom : " + approvisionnementEnergetiqueFromViews.nom);
        }
        releve.getValue().approvisionnementEnergetiques.put(approvisionnementEnergetiqueFromViews.nom, approvisionnementEnergetiqueFromViews);
        forceUpdateReleve();
    }

    public void editApprovisionnementEnergetique(String nomApprovisionnementEnergetique, ApprovisionnementEnergetique approvisionnementEnergetiqueFromViews) {
        if(!approvisionnementEnergetiqueFromViews.nom.equals(nomApprovisionnementEnergetique) && releve.getValue().approvisionnementEnergetiques.containsKey(approvisionnementEnergetiqueFromViews.nom)){
            throw new IllegalArgumentException("Un approvisionnement énergétique porte déjà ce nom : " + approvisionnementEnergetiqueFromViews.nom);
        }
        releve.getValue().approvisionnementEnergetiques.remove(nomApprovisionnementEnergetique);
        releve.getValue().approvisionnementEnergetiques.put(approvisionnementEnergetiqueFromViews.nom, approvisionnementEnergetiqueFromViews);
        forceUpdateReleve();
    }

    public void removeRemarque(Remarque remarque) {
        releve.getValue().remarques.remove(remarque.nom);
        forceUpdateReleve();
    }

    public void addRemarque(Remarque remarque) {
        if(releve.getValue().remarques.containsKey(remarque)){
            throw new IllegalArgumentException("Une remarque identique existe déjà");
        }
        releve.getValue().remarques.put(remarque.nom, remarque);
        forceUpdateReleve();
    }

    public void editRemarque(String oldName, Remarque remarque) {
        if(!remarque.nom.equals(oldName) && releve.getValue().remarques.containsKey(remarque.nom)){
            throw new IllegalArgumentException("Une remarque porte déjà ce nom : " + remarque.nom);
        }
        releve.getValue().remarques.remove(oldName);
        releve.getValue().remarques.put(remarque.nom, remarque);
        forceUpdateReleve();
    }

    public void deletePreconisation(String preconisation) {
        releve.getValue().preconisations.remove(preconisation);
        forceUpdateReleve();
    }

    public void editPreconisation(String last, String preconisation) {
        if(!preconisation.equals(last) && releve.getValue().preconisations.contains(preconisation)){
            throw new IllegalArgumentException("La préconisation (\"" + preconisation + "\") existe déjà");
        }
        releve.getValue().preconisations.remove(last);
        releve.getValue().preconisations.add(preconisation);
        forceUpdateReleve();
    }

    public void addPreconisations(List<String> preconisation) {
        for(String p : preconisation){
            if(releve.getValue().preconisations.contains(p)){
                throw new IllegalArgumentException("La préconisation (\"" + p + "\") existe déjà");
            }
        }
        releve.getValue().preconisations.addAll(preconisation);
        forceUpdateReleve();
    }

    public void editZone(String oldNomZone, String newNomZone) {
        Releve releve = this.releve.getValue();
        if(!releve.zones.containsKey(oldNomZone)){
            throw new IllegalArgumentException("La zone n'existe pas");
        }
        if(oldNomZone.equals(newNomZone)){
            return;
        }
        if(releve.zones.containsKey(newNomZone)){
            throw new IllegalArgumentException("Une zone porte déjà ce nom : " + newNomZone);
        }
        Zone zone = releve.zones.get(oldNomZone);
        zone.nom = newNomZone;
        releve.zones.remove(oldNomZone);
        releve.zones.put(newNomZone, zone);
        for (Ventilation ventilation : releve.ventilations.values()) {
            for (String zoneVentilation : ventilation.zones.toArray(new String[0])) {
                if(zoneVentilation.equals(oldNomZone)){
                    ventilation.zones.remove(oldNomZone);
                    ventilation.zones.add(newNomZone);
                }
            }
        }
        for (Climatisation climatisation : releve.climatisations.values()) {
            for (String zoneClimatisation : climatisation.zones.toArray(new String[0])) {
                if(zoneClimatisation.equals(oldNomZone)){
                    climatisation.zones.remove(oldNomZone);
                    climatisation.zones.add(newNomZone);
                }
            }

        }
        for (Chauffage chauffage : releve.chauffages.values().toArray(new Chauffage[0])) {
            if(chauffage instanceof ChauffageCentraliser){
                ChauffageCentraliser chauffageCentraliser =  ((ChauffageCentraliser) chauffage);
                for (String zoneChauffage : chauffageCentraliser.zones.toArray(new String[0])) {
                    if(zoneChauffage.equals(oldNomZone)){
                        chauffageCentraliser.zones.remove(oldNomZone);
                        chauffageCentraliser.zones.add(newNomZone);
                    }
                }
            }
            if(chauffage instanceof ChauffageDecentraliser){
                if(((ChauffageDecentraliser) chauffage).zone.equals(oldNomZone)){
                    ((ChauffageDecentraliser) chauffage).zone = newNomZone;
                }
            }
        }
        setReleve(releve);
    }

    public void moveChauffage(String nomChauffage, String nomPreviousZone, String nomNewZone) {
        Releve releve = this.releve.getValue();

        Chauffage chauffage = releve.chauffages.get(nomChauffage);
        if(chauffage instanceof ChauffageCentraliser){
            throw new IllegalArgumentException("Un chauffage centralisé ne peut pas être déplacé");
        }
        ChauffageDecentraliser chauffageDecentraliser = (ChauffageDecentraliser) chauffage;
        chauffageDecentraliser.zone= nomNewZone;
        setReleve(releve);
    }



    public String getNextNameForZoneElement(String prefix){
        int index = 1;
        while(true){
            String name = prefix + index;
            if(!isZoneElementNameAlereadyUsed(name)){
                return name;
            }
            index++;
        }
    }

    public String getNextNameForCalendrier(){
        Releve releve = this.releve.getValue();
        int index = 1;
        while(true){
            String name = "Calendrier " + index;
            if(!releve.calendriers.containsKey(name)){
                return name;
            }
            index++;
        }
    }

    private boolean isZoneElementNameAlereadyUsed(String zoneElementName){
        Releve releve = this.releve.getValue();
        for(Zone zone : releve.zones.values()){
            if(zone.getZoneElements().containsKey(zoneElementName)){
                return true;
            }
        }
        return false;
    }

    public void copieZoneElement(String nomZoneElement,String nomPreviousZone,  String nomNewZone) {
         ZoneElement zoneElement = getReleve().getValue().zones.get(nomPreviousZone).getZoneElement(nomZoneElement).clone();
        zoneElement.nom = getNextNameForZoneElement(zoneElement.nom + " copie ");
        addZoneElement(nomNewZone, zoneElement);
    }

    public void copieChauffage(String nomChauffage, String nomNewZone) {
        Releve releve = getReleve().getValue();
        Chauffage chauffage = releve.chauffages.get(nomChauffage).clone();
        int index = 1;
        while(true){
            if(!releve.chauffages.containsKey(chauffage.nom + " (copie" + index + ")")){
                break;
            }
            index++;
        }
        chauffage.nom = chauffage.nom + " (copie" + index + ")";
        if(chauffage instanceof ChauffageCentraliser){
            addChauffage(chauffage);
        }else{
            ((ChauffageDecentraliser)chauffage).zone = nomNewZone;
            addChauffage(chauffage);
        }
    }
}
