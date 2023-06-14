package com.example.super_cep;

import static org.junit.Assert.assertTrue;

import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;
import com.example.super_cep.model.Export.PowerpointExporter;
import com.example.super_cep.model.Releve;

import org.junit.Test;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class CreateSlideShowReleveTest {

    @Test
    public void testOpenFile(){


        try {
            // On ouvre le fichier
            InputStream is = getClass().getClassLoader().getResourceAsStream("powerpointvierge.pptx");

            // On vérifie que le fichier existe
            if (is == null) throw new AssertionError("Fichier non trouvé");



            is.close();
        } catch (IOException e) {
            assertTrue(false);
        }
    }


    @Test
    public void createSlideShowReleveTest(){
        Releve releve = createTestReleve();
        // open file in assets
        PowerpointExporter exporter = new PowerpointExporter(null);
        InputStream is = getClass().getClassLoader().getResourceAsStream("powerpointvierge.pptx");
        try {
            exporter.export(is, new FileOutputStream("test.pptx").getFD(), releve);

            // display in console the path of the file of test.pptx to open it
            System.out.println(new File("test.pptx").getAbsolutePath());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public  Releve createTestReleve() {
        Releve releve = new Releve();

        releve.nomBatiment = "Batiment Test";
        releve.dateDeConstruction.set(2000, Calendar.JANUARY, 1);
        releve.dateDeDerniereRenovation.set(2015, Calendar.JANUARY, 1);
        releve.surfaceTotaleChauffe = 5000.0f;
        releve.description = "Batiment de test pour l'application Super CEP";
        releve.adresse = "123 Rue de Test, Ville Test, 10000";

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
