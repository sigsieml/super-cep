package com.example.super_cep.model.Export;

import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueElectrique;
import com.example.super_cep.model.Releve.Chauffage.Chauffage;
import com.example.super_cep.model.Releve.Chauffage.ChauffageCentraliser;
import com.example.super_cep.model.Releve.Chauffage.ChauffageDecentraliser;
import com.example.super_cep.model.Releve.Climatisation;
import com.example.super_cep.model.Releve.ECS;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.model.Releve.Ventilation;
import com.example.super_cep.model.Releve.Zone;
import com.example.super_cep.model.Releve.ZoneElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classe permettant de créer une archive compressée des données de Releve.
 * <p>
 *     Cette classe est utilisée pour créer une archive compressée des données de Releve.
 * </p>
 */
public  class ArchiveExporter {

    /**
     * Crée une archive compressée des données de Releve et l'écrit dans le OutputStream donné.
     *
     * @param outputStream Le OutputStream où écrire l'archive.
     * @param releve Les données de Releve à archiver.
     * @param platformProvider Le fournisseur de la plateforme utilisée.
     * @param quality La qualité de compression. de 0 à 100. 100 étant la meilleure qualité.
     * @param jsonOrCsv true si l'archive doit être en json, false si elle doit être en csv.
     * @throws Exception En cas d'erreur lors de la création de l'archive.
     */
    public static void createArchive(OutputStream outputStream, Releve releve, PlatformProvider platformProvider, int quality, boolean jsonOrCsv) throws Exception {
        java.util.zip.ZipOutputStream out = new java.util.zip.ZipOutputStream(outputStream);

        // Compress the files
        byte[] data = new byte[1000];

        String releveText = "";
        if(jsonOrCsv) {
            releveText = JsonReleveManager.serialize(releve);
        }else{
            releveText = releveToCsv(releve, platformProvider);
        }
        java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry("releve.csv");
        out.putNextEntry(entry);
        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(releveText.getBytes());
        int count;
        while ((count = bais.read(data, 0, 1000)) != -1) {
            out.write(data, 0, count);
        }
        bais.close();
        String[] images = getAllImageOfReleve(releve, platformProvider);
        String[] imagesNames = getAllImageNameOfReleve(releve, platformProvider);
        for(int index = 0; index < images.length; index++){
            byte[] imageByte = platformProvider.getImagesByteFromPath(images[index], quality);
            if(imageByte == null)
                continue;
            entry = new java.util.zip.ZipEntry(imagesNames[index]);
            out.putNextEntry(entry);
            bais = new java.io.ByteArrayInputStream(imageByte);

            while ((count = bais.read(data, 0, 1000)) != -1) {
                out.write(data, 0, count);
            }
            bais.close();
        }
        out.close();
    }
    private static String[] getAllImageOfReleve(Releve releve, PlatformProvider platformProvider) {
        List<String> images = new ArrayList<>();
        images.add(releve.imageFacadeBatiment);
        images.add(releve.imagePlanBatiment);
        for(Zone zone: releve.zones.values()){
            for(ZoneElement zoneElement : zone.getZoneElementsValues()){
                if(zoneElement.images != null)
                    images.addAll(zoneElement.images);
            }
        }
        for(Chauffage chauffage : releve.chauffages.values()){
            if(chauffage.images != null)
                images.addAll(chauffage.images);
        }
        for(Climatisation climatisation : releve.climatisations.values()){
            if(climatisation.images != null)
                images.addAll(climatisation.images);
        }
        for(Ventilation ventilation : releve.ventilations.values()){
            if(ventilation.images != null)
                images.addAll(ventilation.images);
        }
        for(ECS ecs : releve.ecs.values()){
            if(ecs.images != null)
                images.addAll(ecs.images);
        }
        for(ApprovisionnementEnergetique approvisionnementEnergetique : releve.approvisionnementEnergetiques.values()){
            if(approvisionnementEnergetique.images != null)
                images.addAll(approvisionnementEnergetique.images);
        }
        for(String preconisation : releve.preconisations){
            if(platformProvider.isStringAPath(preconisation))
                images.add(preconisation);
        }
        return images.toArray(new String[images.size()]);
    }
    private static void addToSetWithSuffix(Set<String> set, String string){
        if(string == null)
            return;
        string = new File(string).getName() + ".jpg";
        String suffix = "";
        int i = 0;
        while(set.contains(string + suffix)){
            suffix = "_" + i;
            i++;
        }
        set.add(string + suffix);
    }
    private static void addAllToSetWithSuffix(Set<String> set, List<String> strings){
        for(String string : strings){
            addToSetWithSuffix(set, string);
        }
    }
    private static String[] getAllImageNameOfReleve(Releve releve, PlatformProvider platformProvider) {
        String[] imagesPath = getAllImageOfReleve(releve, platformProvider);
        Set<String> images = new HashSet<>();
        addAllToSetWithSuffix(images, Arrays.asList(imagesPath));

        return images.toArray(new String[images.size()]);
    }

    private static String releveToCsv( Releve releve, PlatformProvider platformProvider) {
        StringBuilder stringBuilder = new StringBuilder();

        final String ENTETE = "adresse;dateDeConostruction;dateDeDerniereRenovation;imageFacadeBatiment;imagePlanBatiment;localisationLatitude;localisationLongitude;surfaceTotale;surfaceTotaleChauffe";
        final String ENTETEApprovisionnementEnergetique = "puissanceElectriqueTotal";
        final String ENETETEChauffage = "puissanceChauffageTotal;nbChauffageCentralise;nbChauffageDecentralise";
        final String ENTETECLIMATISATION = "puissanceClimatisationTotal";
        final String ENTETEECS = "volumeTotalECS";
        stringBuilder.append(ENTETE +";" +
                ENTETEApprovisionnementEnergetique + ";"+
                ENETETEChauffage + ";" +
                ENTETECLIMATISATION + ";" +
                ENTETEECS +
                '\n');

        stringBuilder.append(releve.adresse).append(';');
        String formatedDate = "";
        if(releve.dateDeConstruction != null){
            formatedDate = releve.dateDeConstruction.get(Calendar.DAY_OF_MONTH) + "/" + releve.dateDeConstruction.get(Calendar.MONTH) + "/" + releve.dateDeConstruction.get(Calendar.YEAR);
        }
        stringBuilder.append(formatedDate).append(';');
        formatedDate = "";
        if(releve.dateDeDerniereRenovation != null){
            formatedDate = releve.dateDeDerniereRenovation.get(Calendar.DAY_OF_MONTH) + "/" + releve.dateDeDerniereRenovation.get(Calendar.MONTH) + "/" + releve.dateDeDerniereRenovation.get(Calendar.YEAR);
        }
        stringBuilder.append(formatedDate).append(';');

        String[] imagesNames = getAllImageNameOfReleve(releve, platformProvider);
        String imageNameFacadeBatiment = imagesNames[0];
        String imageNamePlanBatiment = imagesNames[1];
        stringBuilder.append(imageNameFacadeBatiment).append(';');
        stringBuilder.append(imageNamePlanBatiment).append(';');
        if(releve.localisation != null && releve.localisation.length == 2){
            stringBuilder.append(releve.localisation[0] + "").append(';');
            stringBuilder.append(releve.localisation[1] + "").append(';');
        }
        stringBuilder.append(releve.surfaceTotale + "").append(';');
        stringBuilder.append(releve.surfaceTotaleChauffe + "").append(';');

        //approvisionnementEnergetique
        float puissanceElectriqueTotal = releve.approvisionnementEnergetiques.values().stream().map(approvisionnementEnergetique -> approvisionnementEnergetique instanceof ApprovisionnementEnergetiqueElectrique ? ((ApprovisionnementEnergetiqueElectrique) approvisionnementEnergetique).puissance : 0.0f).reduce(0.0f, Float::sum);
        stringBuilder.append(puissanceElectriqueTotal + "").append(';');


        //chauffage
        float puissanceChauffageTotal = releve.chauffages.values().stream().map(chauffage -> chauffage.puissance).reduce(0.0f, Float::sum);
        stringBuilder.append(puissanceChauffageTotal + "").append(';');

        int nbChauffageCentralise = (int) releve.chauffages.values().stream().filter(chauffage -> chauffage instanceof ChauffageCentraliser).count();
        stringBuilder.append(nbChauffageCentralise + "").append(';');
        int nbChauffageDecentralise = (int) releve.chauffages.values().stream().filter(chauffage -> chauffage instanceof ChauffageDecentraliser).count();
        stringBuilder.append(nbChauffageDecentralise + "").append(';');

        //climatisation
        float puissanceClimatisationTotal = releve.climatisations.values().stream().map(climatisation -> climatisation.puissance * climatisation.quantite).reduce(0.0f, Float::sum);
        stringBuilder.append(puissanceClimatisationTotal + "").append(';');

        //ECS
        float volumeTotalECS = releve.ecs.values().stream().map(ecs -> ecs.volume * ecs.quantite).reduce(0.0f, Float::sum);
        stringBuilder.append(volumeTotalECS + "");


        return stringBuilder.toString();

    }

}
