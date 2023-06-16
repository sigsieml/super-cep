package com.example.super_cep;

import android.net.Uri;

import com.example.super_cep.model.Enveloppe.Mur;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;
import com.example.super_cep.model.Export.JsonExporter;
import com.example.super_cep.model.Releve;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;

public class SerialiseJsonReleve {


    @Test
    public void serialiseJsonReleve() {
        Releve releve = createTestReleve();
        String jsonReleve = JsonExporter.serialize(releve);
        Releve deserializedReleve = JsonExporter.deserialize(jsonReleve);
        System.out.println("deserializedReleve = " + deserializedReleve);

    }


    public Releve createTestReleve() {
        Releve releve = new Releve();

        releve.nomBatiment = "Batiment Test";
        releve.dateDeConstruction.set(2000, Calendar.JANUARY, 1);
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
        ZoneElement zoneElement1 = new ZoneElement("Mur Est");
        ZoneElement zoneElement2 = new ZoneElement("Mur Ouest");
        zone1.addZoneElement(zoneElement1);
        zone1.addZoneElement(zoneElement2);
        releve.addZone(zone1);

        Zone zone2 = new Zone("Zone 2");
        ZoneElement zoneElement3 = new ZoneElement("Mur Nord");
        ZoneElement zoneElement4 = new ZoneElement("Mur Sud");
        zone2.addZoneElement(zoneElement3);
        zone2.addZoneElement(zoneElement4);
        releve.addZone(zone2);

        // Ajoutez autant de zones et d'éléments de zone que nécessaire

        return releve;
    }
}
