package com.example.super_cep;

import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueElectrique;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Calendrier.CalendrierDate;
import com.example.super_cep.model.Releve.Calendrier.ChaufferOccuper;
import com.example.super_cep.model.Releve.Chauffage.CategorieChauffage;
import com.example.super_cep.model.Releve.Chauffage.ChauffageCentraliser;
import com.example.super_cep.model.Releve.Chauffage.ChauffageDecentraliser;
import com.example.super_cep.model.Releve.Enveloppe.Mur;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.model.Releve.Remarque;
import com.example.super_cep.model.Releve.Zone;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class SerialiseCsvReleve {
    private void releveToCsv(FileWriter fileWriter, Releve releve) throws IOException {

        final String ENTETE = "adresse;dateDeConostruction;dateDeDerniereRenovation;imageFacadeBatiment;imagePlanBatiment;localisationLatitude;localisationLongitude;surfaceTotale;surfaceTotaleChauffe";
        final String ENTETEApprovisionnementEnergetique = "puissanceElectriqueTotal";
        final String ENETETEChauffage = "puissanceChauffageTotal;nbChauffageCentralise;nbChauffageDecentralise";
        final String ENTETECLIMATISATION = "puissanceClimatisationTotal";
        final String ENTETEECS = "volumeTotalECS";
        fileWriter.append(ENTETE +";" +
                ENTETEApprovisionnementEnergetique + ";"+
                ENETETEChauffage + ";" +
                ENTETECLIMATISATION + ";" +
                ENTETEECS +
                '\n');

        fileWriter.append(releve.adresse).append(';');
        String formatedDate = releve.dateDeConstruction.get(Calendar.DAY_OF_MONTH) + "/" + releve.dateDeConstruction.get(Calendar.MONTH) + "/" + releve.dateDeConstruction.get(Calendar.YEAR);
        fileWriter.append(formatedDate).append(';');
        formatedDate = releve.dateDeDerniereRenovation.get(Calendar.DAY_OF_MONTH) + "/" + releve.dateDeDerniereRenovation.get(Calendar.MONTH) + "/" + releve.dateDeDerniereRenovation.get(Calendar.YEAR);
        fileWriter.append(formatedDate).append(';');
        fileWriter.append(releve.imageFacadeBatiment).append(';');
        fileWriter.append(releve.imagePlanBatiment).append(';');
        fileWriter.append(releve.localisation[0] + "").append(';');
        fileWriter.append(releve.localisation[1] + "").append(';');
        fileWriter.append(releve.surfaceTotale + "").append(';');
        fileWriter.append(releve.surfaceTotaleChauffe + "").append(';');

        //approvisionnementEnergetique
        float puissanceElectriqueTotal = releve.approvisionnementEnergetiques.values().stream().map(approvisionnementEnergetique -> approvisionnementEnergetique instanceof ApprovisionnementEnergetiqueElectrique ? ((ApprovisionnementEnergetiqueElectrique) approvisionnementEnergetique).puissance : 0.0f).reduce(0.0f, Float::sum);
        fileWriter.append(puissanceElectriqueTotal + "").append(';');


        //chauffage
        float puissanceChauffageTotal = releve.chauffages.values().stream().map(chauffage -> chauffage.puissance).reduce(0.0f, Float::sum);
        fileWriter.append(puissanceChauffageTotal + "").append(';');

        int nbChauffageCentralise = (int) releve.chauffages.values().stream().filter(chauffage -> chauffage instanceof ChauffageCentraliser).count();
        fileWriter.append(nbChauffageCentralise + "").append(';');
        int nbChauffageDecentralise = (int) releve.chauffages.values().stream().filter(chauffage -> chauffage instanceof ChauffageDecentraliser).count();
        fileWriter.append(nbChauffageDecentralise + "").append(';');

        //climatisation
        float puissanceClimatisationTotal = releve.climatisations.values().stream().map(climatisation -> climatisation.puissance * climatisation.quantite).reduce(0.0f, Float::sum);
        fileWriter.append(puissanceClimatisationTotal + "").append(';');

        //ECS
        float volumeTotalECS = releve.ecs.values().stream().map(ecs -> ecs.volume * ecs.quantite).reduce(0.0f, Float::sum);
        fileWriter.append(volumeTotalECS + "");

        fileWriter.flush();
    }
    @Test
    public void serialiseCsvReleve() {
        Releve releve = createTestReleve();

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("releve.csv");
            releveToCsv(fileWriter, releve);
            fileWriter.close();

            String absolutePathFile = new File("releve.csv").getAbsolutePath();
            System.out.println("File created at: " + absolutePathFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public Releve createTestReleve() {
        Releve releve = new Releve();

        releve.nomBatiment = "Batiment Test";
        releve.dateDeConstruction = Calendar.getInstance();
        releve.dateDeConstruction.set(2000, Calendar.JANUARY, 1);
        releve.dateDeDerniereRenovation = Calendar.getInstance();
        releve.dateDeDerniereRenovation.set(2015, Calendar.JANUARY, 1);
        releve.surfaceTotaleChauffe = 5000.0f;
        releve.adresse = "123 Rue de Test, Ville Test, 10000";
        releve.localisation = new double[]{12.0, 12.0};

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
        releve.zones.put(superZone.nom, superZone);

        Zone zone1 = new Zone("Zone 1");
        releve.zones.put(zone1.nom, zone1);

        Zone zone2 = new Zone("Zone 2");
        releve.zones.put(zone2.nom, zone2);

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

        releve.preconisations.add("preconisation 1");
        releve.preconisations.add("preconisation 2");
        releve.remarques.put("remarque 1", new Remarque("remarque 1", "note 1", false));


        releve.imageFacadeBatiment = "C:\\Users\\TLB\\Pictures\\cropped-cropped-Logo-Sieml-110-1.png";

        releve.approvisionnementEnergetiques.put("fioul", new ApprovisionnementEnergetique("fioul", "fioul", new ArrayList<>(), new ArrayList<>(), false, ""));

        // Ajoutez autant de zones et d'éléments de zone que nécessaire

        return releve;
    }
}
