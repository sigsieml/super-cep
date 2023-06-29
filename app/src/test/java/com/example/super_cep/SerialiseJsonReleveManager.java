package com.example.super_cep;

import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Calendrier.CalendrierDate;
import com.example.super_cep.model.Releve.Calendrier.ChaufferOccuper;
import com.example.super_cep.model.Releve.Chauffage.CategorieChauffage;
import com.example.super_cep.model.Releve.Chauffage.ChauffageCentraliser;
import com.example.super_cep.model.Releve.Chauffage.ChauffageDecentraliser;
import com.example.super_cep.model.Releve.Enveloppe.Mur;
import com.example.super_cep.model.Releve.Zone;
import com.example.super_cep.model.Export.JsonReleveManager;
import com.example.super_cep.model.Releve.Releve;

import org.junit.Test;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class SerialiseJsonReleveManager {


    @Test
    public void serialiseJsonReleve() {
        Releve releve = createTestReleve();
        String jsonReleve = JsonReleveManager.serialize(releve);
        System.out.println("jsonReleve = " + jsonReleve);
        Releve deserializedReleve = JsonReleveManager.deserialize(jsonReleve);
        System.out.println("deserializedReleve = " + deserializedReleve);

    }


    public Releve createTestReleve() {
        Releve releve = new Releve();

        releve.nomBatiment = "Batiment Test";
        releve.dateDeConstruction = Calendar.getInstance();
        releve.dateDeConstruction.set(2000, Calendar.JANUARY, 1);
        releve.dateDeDerniereRenovation = Calendar.getInstance();
        releve.dateDeDerniereRenovation.set(2015, Calendar.JANUARY, 1);
        releve.surfaceTotaleChauffe = 5000.0f;
        releve.description = "Batiment de test pour l'application Super CEP";
        releve.adresse = "123 Rue de Test, Ville Test, 10000";

        Zone superZone = new Zone("Super Zone");
        Mur mur = new Mur("Mur 1",
                "type de mur",
                "type de mise en oeuvre ",
                "type d'isolation",
                "niveau isolation",
                2,
                true,
                "note",
                null
                );
        superZone.addZoneElement(mur);
        releve.addZone(superZone);

        Zone zone1 = new Zone("Zone 1");
        releve.addZone(zone1);

        Zone zone2 = new Zone("Zone 2");
        releve.addZone(zone2);

        releve.chauffages.put("chauffage 1", new ChauffageCentraliser(
                "chauffage 1",
                "type de chauffage",
                12.0f,
                12,
                "Mitachi",
                "X14",
                List.of("zone 1", "zone 2"),
                CategorieChauffage.ProducteurEmetteur,
                "regulation",
                new ArrayList<>(),
                false,
                ""
        ));
        releve.chauffages.put("chauffage 2", new ChauffageDecentraliser(
                "chauffage 2",
                "type de chauffage",
                12.0f,
                12,
                "Mitachi",
                "X14",
                "zone 2",
                CategorieChauffage.ProducteurEmetteur,
                "regulation",
                new ArrayList<>(),
                false,
                ""
        ));

        releve.calendriers.put("calendrier", new Calendrier(
                "calendrier",
                List.of("zone 1", "zone 2"),
                Map.of(new CalendrierDate(DayOfWeek.MONDAY,4,30) , ChaufferOccuper.CHAUFFER_OCCUPER,
                        new CalendrierDate(DayOfWeek.TUESDAY,4,30) , ChaufferOccuper.CHAUFFER,
                        new CalendrierDate(DayOfWeek.WEDNESDAY,4,30) , ChaufferOccuper.OCCUPER,
                        new CalendrierDate(DayOfWeek.THURSDAY,4,30) , ChaufferOccuper.CHAUFFER_OCCUPER,
                        new CalendrierDate(DayOfWeek.FRIDAY,4,30) , ChaufferOccuper.CHAUFFER_OCCUPER,
                        new CalendrierDate(DayOfWeek.SATURDAY,4,30) , ChaufferOccuper.CHAUFFER_OCCUPER,
                        new CalendrierDate(DayOfWeek.SUNDAY,4,30) , ChaufferOccuper.CHAUFFER_OCCUPER


                )

        ));


        releve.imageBatiment = "C:\\Users\\TLB\\Pictures\\cropped-cropped-Logo-Sieml-110-1.png";

        releve.approvisionnementEnergetiques.put("fioul", new ApprovisionnementEnergetique("fioul", "fioul", new ArrayList<>(), new ArrayList<>(), false, ""));

        // Ajoutez autant de zones et d'éléments de zone que nécessaire

        return releve;
    }
}
