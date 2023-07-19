package com.example.super_cep.model.Export;

import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.Releve.Chauffage.Chauffage;
import com.example.super_cep.model.Releve.Climatisation;
import com.example.super_cep.model.Releve.ECS;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.model.Releve.Ventilation;
import com.example.super_cep.model.Releve.Zone;
import com.example.super_cep.model.Releve.ZoneElement;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public  class ArchiveExporter {


    public static void createArchive(OutputStream outputStream, Releve releve, PlatformProvider platformProvider, int quality) throws Exception {
        java.util.zip.ZipOutputStream out = new java.util.zip.ZipOutputStream(outputStream);

        // Compress the files
        byte[] data = new byte[1000];

        String releveJson = JsonReleveManager.serialize(releve);
        java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry("releve.json");
        out.putNextEntry(entry);
        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(releveJson.getBytes());
        int count;
        while ((count = bais.read(data, 0, 1000)) != -1) {
            out.write(data, 0, count);
        }
        bais.close();
        List<String> images = getAllImageOfReleve(releve, platformProvider);
        Set<String> entries = new HashSet<>();
        for(String image : images){
            byte[] imageByte = platformProvider.getImagesByteFromPath(image, quality);
            if(imageByte == null)
                continue;
            String suffix = "";
            int i = 0;
            while(entries.contains(new File(image).getName() + suffix + ".jpg")){
                suffix = "_" + i;
                i++;
            }
            entries.add(new File(image).getName() + suffix + ".jpg");
            entry = new java.util.zip.ZipEntry(new File(image).getName()+ suffix + ".jpg");
            out.putNextEntry(entry);
            bais = new java.io.ByteArrayInputStream(imageByte);

            while ((count = bais.read(data, 0, 1000)) != -1) {
                out.write(data, 0, count);
            }
            bais.close();
        }
        out.close();
    }
    private static List<String> getAllImageOfReleve(Releve releve, PlatformProvider platformProvider) {
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
        return images;
    }
}
