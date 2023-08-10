package com.example.super_cep.model.Releve;

import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Chauffage.Chauffage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Classe représentant un relevé d'un bâtiment.
 * Elle contient des informations sur les zones, calendriers, chauffages, climatisations, ventilations,
 * approvisionnements énergétiques et autres caractéristiques d'un bâtiment.
 */
public class Releve {

    public Map<String, Zone> zones;

    public Map<String, Calendrier> calendriers;
    public Map<String, Chauffage> chauffages;
    public Map<String, Climatisation> climatisations;
    public Map<String, Ventilation> ventilations;

    public String nomBatiment = "";
    public Calendar dateDeConstruction;
    public Calendar dateDeDerniereRenovation;
    public Calendar dateDeVisite;

    public float surfaceTotale = 0;
    public float surfaceTotaleChauffe = 0;
    public String adresse = "";
    public Map<String, ECS> ecs;
    public Map<String, ApprovisionnementEnergetique> approvisionnementEnergetiques;
    public Map<String, Remarque> remarques;
    public List<String> preconisations;

    public String imageFacadeBatiment;
    public String imagePlanBatiment;

    public double[] localisation;
    /**
     * Constructeur de la classe Releve. Initialise toutes les Map avec des HashMap vides.
     */
    public Releve(){
        this.zones = new HashMap<>();
        this.calendriers = new HashMap<>();
        this.chauffages = new HashMap<>();
        this.climatisations = new HashMap<>();
        this.ventilations = new HashMap<>();
        this.ecs = new HashMap<>();
        this.approvisionnementEnergetiques = new HashMap<>();
        this.remarques = new HashMap<>();
        this.preconisations = new ArrayList<>();
        dateDeVisite = Calendar.getInstance();
    }

    /**
     * Retourne la zone correspondant au nom fourni.
     * @param name Le nom de la zone.
     * @return La zone correspondante.
     * @throws IllegalArgumentException Si aucune zone avec ce nom n'existe.
     */
    public Zone getZone(String name){
        if(!zones.containsKey(name))
            throw new IllegalArgumentException("La zone n'existe pas + " + name);
        return zones.get(name);
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères de cet objet Releve.
     * @return Une représentation de cet objet sous forme de chaîne de caractères.
     */
    @Override
    public String toString() {
        return "Releve{" +
                "zones=" + zones +
                ", nomBatiment='" + nomBatiment + '\'' +
                ", dateDeConstruction=" + dateDeConstruction +
                ", dateDeDerniereRenovation=" + dateDeDerniereRenovation +
                ", surfaceTotaleChauffe=" + surfaceTotaleChauffe +
                ", adresse='" + adresse + '\'' +
                '}'+ "\n" +
                "calendriers=" + calendriers + "\n" +
                "chauffages=" + chauffages + "\n" +
                "climatisations=" + climatisations + "\n" +
                "ventilations=" + ventilations + "\n" +
                "ecs=" + ecs + "\n" +
                "approvisonnementEnergetique=" + approvisionnementEnergetiques + "}";

    }
}
