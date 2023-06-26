package com.example.super_cep.model.Export;

import android.content.Context;

import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueElectrique;
import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueGaz;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Chauffage.Chauffage;
import com.example.super_cep.model.Releve.Climatisation;
import com.example.super_cep.model.Releve.ECS;
import com.example.super_cep.model.Releve.Enveloppe.Eclairage;
import com.example.super_cep.model.Releve.Enveloppe.Menuiserie;
import com.example.super_cep.model.Releve.Enveloppe.Mur;
import com.example.super_cep.model.Releve.Enveloppe.Sol;
import com.example.super_cep.model.Releve.Enveloppe.Toiture;
import com.example.super_cep.model.Releve.Enveloppe.Zone;
import com.example.super_cep.model.Releve.Enveloppe.ZoneElement;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.model.Releve.Remarque;
import com.example.super_cep.model.Releve.Ventilation;

import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PowerpointExporter {

    public static final String POWERPOINT_VIERGE_NAME = "powerpointvierge.pptx";
    public static  final Color[] colors = new Color[]{new Color(191, 143,0), new Color(31, 78,120), new Color(255, 43, 43)};
    private Releve releve;
    private Map<String, String> remplacements;

    private Context context;

    private PowerpointPlatformProvider platformProvider;

    public PowerpointExporter(PowerpointPlatformProvider platformProvider) {
        this.platformProvider = platformProvider;

    }

    public void export(InputStream powerpointVierge, FileDescriptor file, Releve releve) {
        this.releve = releve;
        setupReleve();

        try (InputStream is = powerpointVierge;
             XMLSlideShow ppt = new XMLSlideShow(is)){

            List<XSLFSlide> slides = ppt.getSlides();
            XSLFSlide slideDescriptifEnveloppeThermique = slides.get(3);
            XSLFSlide slideDescriptifDesSystem = slides.get(4);
            XSLFSlide slideDescriptifDuChauffage = slides.get(5);
            XSLFSlide slidePreconisations = slides.get(6);

            slideBatiment(ppt, slides.get(0));
            slideEnergieEtConsomations(slides.get(1));
            slideUsageEtOccupationDuBatiment(ppt, slides.get(2));
            slideDescriptifEnveloppeThermique(ppt,slideDescriptifEnveloppeThermique);
            slideDescriptifDesSystem(ppt, slideDescriptifDesSystem);
            slideDescriptifDuChauffage(ppt, slideDescriptifDuChauffage);
            slidePreconisations(ppt, slidePreconisations);

            try (FileOutputStream out = new FileOutputStream(file)) {
                ppt.write(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void setupReleve() {
        remplacements = new HashMap<>();
        if(releve.nomBatiment != null) remplacements.put("nomBatiment", releve.nomBatiment);
        if(releve.dateDeConstruction != null) {
            remplacements.put("dateDeConstruction",  formateDate(releve.dateDeConstruction));
        }else{
            remplacements.put("dateDeConstruction",  "Inconnue");
        }
        if(releve.dateDeDerniereRenovation != null){
            remplacements.put("dateDeDerniereRenovation", formateDate(releve.dateDeDerniereRenovation));
        }else{
            remplacements.put("dateDeDerniereRenovation", "Inconnue");
        }
        if(releve.surfaceTotaleChauffe != 0){
            remplacements.put("surfaceTotaleChauffe", releve.surfaceTotaleChauffe + "");
        }else{
            remplacements.put("surfaceTotaleChauffe", "Inconnue");
        }
        if(releve.description != null) remplacements.put("description", releve.description);
        if(releve.adresse != null) remplacements.put("adresse", releve.adresse);


        //remarques :
        if(releve.remarques != null){
            for (Map.Entry<String, Remarque> entry : releve.remarques.entrySet()) {
                remplacements.put("remarque" + entry.getKey(), entry.getValue().description);
            }
        }
    }

    private void slideBatiment(XMLSlideShow ppt, XSLFSlide slide) {
        Rectangle2D rectangle2DImageBatiment = null;
        for (XSLFShape shape : slide) {
            if (shape instanceof XSLFTextShape) {
                PowerpointExporterTools.replaceTextInTextShape(remplacements,(XSLFTextShape) shape);
            }

            if(shape.getShapeName().equals("photoBatiment")){
                rectangle2DImageBatiment = shape.getAnchor();
            }
        }
        if(rectangle2DImageBatiment == null){
            return;
        }
        if(releve.imageBatiment != null){
            addImagesToSlide(ppt, slide, List.of(releve.imageBatiment), rectangle2DImageBatiment);
        }


    }

    private void slideEnergieEtConsomations(XSLFSlide slide) {
        for (XSLFShape shape : slide) {
            if(shape instanceof XSLFTextShape){
                PowerpointExporterTools.replaceTextInTextShape(remplacements,(XSLFTextShape) shape);
            }

            if(shape.getShapeName().equals("tableauApprovisionnementEnergetique")){

                XSLFTable table = (XSLFTable) shape;
                XSLFTableRow tableName = table.getRows().get(0);
                XSLFTableRow tableHeader = table.getRows().get(1);

                XSLFTableRow rowExemple = table.getRows().get(2);


                for (ApprovisionnementEnergetique approvisionnementEnergetique : releve.approvisionnementEnergetiques.values()) {
                    XSLFTableRow rowAppro = table.addRow();
                    rowAppro.setHeight(rowExemple.getHeight());

                    for (int i = 0; i < rowExemple.getCells().size(); i++) {
                        rowAppro.addCell();
                    }
                    List<XSLFTableCell> cells = rowAppro.getCells();
                    cells.get(0).setText(approvisionnementEnergetique.energie);

                    if(approvisionnementEnergetique instanceof ApprovisionnementEnergetiqueGaz){
                        cells.get(1).setText(((ApprovisionnementEnergetiqueGaz) approvisionnementEnergetique).numeroRAE);
                    }

                    if(approvisionnementEnergetique instanceof ApprovisionnementEnergetiqueElectrique){
                        cells.get(1).setText(((ApprovisionnementEnergetiqueElectrique) approvisionnementEnergetique).numeroPDL);
                        cells.get(2).setText(((ApprovisionnementEnergetiqueElectrique) approvisionnementEnergetique).puissance + " kVA");
                        cells.get(3).setText(((ApprovisionnementEnergetiqueElectrique) approvisionnementEnergetique).formuleTarifaire);
                    }
                    PowerpointExporterTools.copyRowStyle(rowExemple,rowAppro);

                    if(approvisionnementEnergetique.aVerifier)
                        PowerpointExporterTools.setAverfierStyleToRow(rowAppro);


                }
                table.removeRow(2);
            }
        }
    }


    private void slideUsageEtOccupationDuBatiment(XMLSlideShow ppt, XSLFSlide slide) {

        Calendrier[] calendriers = releve.getCalendriersValues();
        if(calendriers.length == 0)
            return;
        XSLFSlide[] slidesCalendrier = new XSLFSlide[calendriers.length];
        slidesCalendrier[0] = slide;
        for (int i = 1; i < calendriers.length; i++) {
            slidesCalendrier[i] = PowerpointExporterTools.duplicateSlide(ppt, slide);
            ppt.setSlideOrder(slidesCalendrier[i], slidesCalendrier[i - 1].getSlideNumber());
        }
        for (int i = 0; i < calendriers.length; i++) {
            XSLFSlide slideCalendrier = slidesCalendrier[i];
            Calendrier calendrier = calendriers[i];

            for (XSLFShape shape : slideCalendrier) {
                if(shape instanceof XSLFTextShape){
                    PowerpointExporterTools.replaceTextInTextShape(remplacements,(XSLFTextShape) shape);
                }
                if(shape.getShapeName().equals("nomCalendrier")){
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    textShape.getTextBody().setText(calendrier.nom);
                }
                if(shape.getShapeName().equals("nomZones")){
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    textShape.getTextBody().setText("zones : " + getZonesText(calendrier.zones));
                }
            }

        }
    }


    private void slideDescriptifEnveloppeThermique(XMLSlideShow ppt, XSLFSlide slide) {


        Rectangle2D rectangle2DImage = null;
        List<String> tablesNames = List.of("tableauMur", "tableauToiture", "tableauSols", "tableauMenuiseries");
        Map<String, XSLFTable> tables = new HashMap<>();
        for (XSLFShape shape : slide.getShapes()){
            if(shape.getShapeName().equals("photo")){
                rectangle2DImage = shape.getAnchor();
            }

            if(tablesNames.contains(shape.getShapeName())){
                tables.put(shape.getShapeName(), (XSLFTable) shape);
            }

            if(shape instanceof XSLFTextShape){
                PowerpointExporterTools.replaceTextInTextShape(remplacements,(XSLFTextShape) shape);
            }

        }
        XSLFTable tableauMur = tables.get("tableauMur");
        XSLFTable tableauToiture = tables.get("tableauToiture");
        XSLFTable tableauSols = tables.get("tableauSols");
        XSLFTable tableauMenuiseries = tables.get("tableauMenuiseries");


        int indexColorZone = 0;

        ZoneElementTableauData zoneElementTableauMur = new ZoneElementTableauData(3, 2, "");
        ZoneElementTableauData zoneElementTableauToiture = new ZoneElementTableauData(3, 2, "");
        ZoneElementTableauData zoneElementTableauSols = new ZoneElementTableauData(3, 2, "");
        ZoneElementTableauData zoneElementTableauMenuiseries = new ZoneElementTableauData(3, 2, "");


        List<String> images = new ArrayList<>();
        for (Zone zone : releve.getZonesValues()){
            Color colorZoneName = colors[indexColorZone % colors.length];
            indexColorZone++;

            for (ZoneElement zoneElement : zone.getZoneElementsValues()){

                XSLFTableRow row = null;
                //get a random color based on the zone name :
                if(zoneElement instanceof Mur){
                    if(zoneElement.images != null)
                        images.addAll(zoneElement.images);

                    Mur mur = (Mur) zoneElement;
                    row = tableauMur.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauMur.getRows().get(2), row);

                    PowerpointExporterTools.addTextToCell(row.getCells().get(0),zone.nom);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1),"Mur");
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2),mur.typeMur);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3),mur.niveauIsolation);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(4),"(" + mur.typeIsolant  + ";" + mur.epaisseurIsolant +" cm)");

                    PowerpointExporterTools.copyRowStyle(tableauMur.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colorZoneName);

                    mergeCellsIfSameZone(zoneElementTableauMur, zone.nom, tableauMur);

                }

                if(zoneElement instanceof Toiture){
                    if(zoneElement.images != null)
                        images.addAll(zoneElement.images);

                    Toiture toiture = (Toiture) zoneElement;
                    row = tableauToiture.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauToiture.getRows().get(2), row);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(0),zone.nom);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1),toiture.typeToiture);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2),toiture.niveauIsolation);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3),"(" + toiture.typeIsolant  + ";" + toiture.epaisseurIsolant +" cm)");
                    PowerpointExporterTools.copyRowStyle(tableauToiture.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colorZoneName);

                    mergeCellsIfSameZone(zoneElementTableauToiture, zone.nom, tableauToiture);
                }

                if(zoneElement instanceof Menuiserie){
                    if(zoneElement.images != null)
                        images.addAll(zoneElement.images);

                    Menuiserie menuiserie = (Menuiserie) zoneElement;
                    row = tableauMenuiseries.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauMenuiseries.getRows().get(2), row);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(0), zone.nom);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1),menuiserie.typeMenuiserie);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2),menuiserie.materiau);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3),menuiserie.typeVitrage);
                    if(!menuiserie.protectionsSolaires.equals("aucune")){
                        PowerpointExporterTools.addTextToCell(row.getCells().get(4),"Avec");
                        PowerpointExporterTools.addTextToCell(row.getCells().get(5),menuiserie.protectionsSolaires);
                    }
                    PowerpointExporterTools.copyRowStyle(tableauMenuiseries.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colorZoneName);

                    mergeCellsIfSameZone(zoneElementTableauMenuiseries, zone.nom, tableauMenuiseries);
                }

                if(zoneElement instanceof Sol){
                    if(zoneElement.images != null)
                        images.addAll(zoneElement.images);

                    Sol sol = (Sol) zoneElement;
                    row = tableauSols.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauSols.getRows().get(2), row);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(0), zone.nom);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1),sol.typeSol);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2),sol.typeIsolant);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3),sol.niveauIsolation);

                    PowerpointExporterTools.copyRowStyle(tableauSols.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colorZoneName);

                    mergeCellsIfSameZone(zoneElementTableauSols, zone.nom, tableauSols);

                }


                if(row != null && zoneElement.aVerifier)
                    PowerpointExporterTools.setAverfierStyleToRow(row);

            }
        }
        addImagesToSlide(ppt, slide, images, rectangle2DImage);


        PowerpointExporterTools.updateCellAnchor(tableauMur, 20);
        PowerpointExporterTools.updateCellAnchor(tableauMenuiseries, 20);
        PowerpointExporterTools.updateCellAnchor(tableauSols, 20);
        PowerpointExporterTools.updateCellAnchor(tableauToiture, 20);

        //set tableauToiture below tableauMur
        tableauToiture.setAnchor(new Rectangle2D.Double(tableauMur.getAnchor().getX(),
                tableauMur.getAnchor().getY() + tableauMur.getAnchor().getHeight() + 2,
                tableauToiture.getAnchor().getWidth(),
                tableauToiture.getAnchor().getHeight())
        );

        //set tableauMenuiseries below tableauToiture
        tableauMenuiseries.setAnchor(new Rectangle2D.Double(tableauToiture.getAnchor().getX(),
                tableauToiture.getAnchor().getY() + tableauToiture.getAnchor().getHeight(),
                tableauMenuiseries.getAnchor().getWidth(),
                tableauMenuiseries.getAnchor().getHeight())
        );

        //set tableauSols below tableauMenuiseries
        tableauSols.setAnchor(new Rectangle2D.Double(tableauMenuiseries.getAnchor().getX(),
                tableauMenuiseries.getAnchor().getY() + tableauMenuiseries.getAnchor().getHeight(),
                tableauSols.getAnchor().getWidth(),
                tableauSols.getAnchor().getHeight())
        );


        tableauMur.removeRow(2);
        tableauToiture.removeRow(2);
        tableauMenuiseries.removeRow(2);
        tableauSols.removeRow(2);
    }


    private void slideDescriptifDesSystem(XMLSlideShow ppt, XSLFSlide slide) {

        Rectangle2D rectangle2DImages = null;
        List<String> tablesNames = List.of("tableauEclairage", "tableauVentilation", "tableauECS", "tableauMenuiseries");
        Map<String, XSLFTable> tables = new HashMap<>();
        for (XSLFShape shape : slide.getShapes()){
            if(shape.getShapeName().equals("photo")){
                rectangle2DImages = shape.getAnchor();
            }

            if(tablesNames.contains(shape.getShapeName())){
                tables.put(shape.getShapeName(), (XSLFTable) shape);
            }

            if(shape instanceof XSLFTextShape){
                PowerpointExporterTools.replaceTextInTextShape(remplacements,(XSLFTextShape) shape);
            }
        }
        XSLFTable tableauEclairage = tables.get("tableauEclairage");
        XSLFTable tableauVentilation = tables.get("tableauVentilation");
        XSLFTable tableauECS = tables.get("tableauECS");


        int indexColorZone = 0;

        ZoneElementTableauData zoneElementTableauEclairage = new ZoneElementTableauData(3, 2, "");
        ZoneElementTableauData zoneElementTableauVentilation = new ZoneElementTableauData(3, 2, "");
        ZoneElementTableauData zoneElementTableauECS = new ZoneElementTableauData(3, 2, "");

        List<String> images = new ArrayList<>();

        for (Zone zone : releve.getZonesValues()){
            Color colorZoneName = colors[indexColorZone % colors.length];
            indexColorZone++;

            for (ZoneElement zoneElement : zone.getZoneElementsValues()){
                //get a random color based on the zone name :
                if(zoneElement instanceof Eclairage){
                    if(zoneElement.images != null)
                        images.addAll(zoneElement.images);

                    Eclairage eclairage = (Eclairage) zoneElement;
                    XSLFTableRow row = tableauEclairage.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauEclairage.getRows().get(2), row);

                    PowerpointExporterTools.addTextToCell(row.getCells().get(0),zone.nom);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1),eclairage.typeEclairage);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2),"réguler par");
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3),eclairage.typeDeRegulation);

                    PowerpointExporterTools.copyRowStyle(tableauEclairage.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colorZoneName);

                    mergeCellsIfSameZone(zoneElementTableauEclairage, zone.nom, tableauEclairage);

                    if(zoneElement.aVerifier)
                        PowerpointExporterTools.setAverfierStyleToRow(row);

                }
            }
        }

        for(Ventilation ventilation : releve.ventilations.values()){
            if(ventilation.images != null)
                images.addAll(ventilation.images);

            XSLFTableRow row = tableauVentilation.addRow();
            PowerpointExporterTools.copyNumberOfCells(tableauVentilation.getRows().get(2), row);

            PowerpointExporterTools.addTextToCell(row.getCells().get(0), getZonesText(ventilation.zones));
            PowerpointExporterTools.addTextToCell(row.getCells().get(1),ventilation.type);
            if(ventilation.regulation.equals("Absence de régulation")){
                PowerpointExporterTools.addTextToCell(row.getCells().get(2),"Absence de régulation");
            }else{
                PowerpointExporterTools.addTextToCell(row.getCells().get(2),"réguler par");
                PowerpointExporterTools.addTextToCell(row.getCells().get(3),ventilation.regulation);
            }
            PowerpointExporterTools.copyRowStyle(tableauVentilation.getRows().get(2), row);
            PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colors[0]);

            if(ventilation.aVerifier)
                PowerpointExporterTools.setAverfierStyleToRow(row);

        }

        for(ECS ecs : releve.ecs.values()){
            if(ecs.images != null)
                images.addAll(ecs.images);
            XSLFTableRow row = tableauECS.addRow();
            PowerpointExporterTools.copyNumberOfCells(tableauECS.getRows().get(2), row);

            PowerpointExporterTools.addTextToCell(row.getCells().get(0), getZonesText(ecs.zones));
            PowerpointExporterTools.addTextToCell(row.getCells().get(1),ecs.type);
            PowerpointExporterTools.addTextToCell(row.getCells().get(2),"( " + ecs.marque + " : " + ecs.volume + " L )");

            PowerpointExporterTools.copyRowStyle(tableauECS.getRows().get(2), row);
            PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colors[0]);

            if(ecs.aVerifier)
                PowerpointExporterTools.setAverfierStyleToRow(row);

        }
        addImagesToSlide(ppt, slide, images, rectangle2DImages);


        PowerpointExporterTools.updateCellAnchor(tableauEclairage, 20);
        PowerpointExporterTools.updateCellAnchor(tableauECS, 20);
        PowerpointExporterTools.updateCellAnchor(tableauVentilation, 20);

        tableauVentilation.setAnchor(new Rectangle2D.Double(tableauEclairage.getAnchor().getX(),
                tableauEclairage.getAnchor().getY() + tableauEclairage.getAnchor().getHeight() + 2,
                tableauVentilation.getAnchor().getWidth(),
                tableauVentilation.getAnchor().getHeight())
        );

        tableauECS.setAnchor(new Rectangle2D.Double(tableauVentilation.getAnchor().getX(),
                tableauVentilation.getAnchor().getY() + tableauVentilation.getAnchor().getHeight(),
                tableauECS.getAnchor().getWidth(),
                tableauECS.getAnchor().getHeight())
        );



        tableauEclairage.removeRow(2);
        tableauVentilation.removeRow(2);
        tableauECS.removeRow(2);
    }



    private void slideDescriptifDuChauffage(XMLSlideShow ppt, XSLFSlide slide) {
        Rectangle2D rectangle2DPhoto = null;

        XSLFTable tableauEmetteurs = null;
        XSLFTable tableauProduction = null;
        for (XSLFShape shape : slide.getShapes()){
            if(shape.getShapeName().equals("photo")){
                rectangle2DPhoto = shape.getAnchor();
            }

            if(shape.getShapeName().equals("tableauEmetteurs")){
                tableauEmetteurs = (XSLFTable) shape;
            }
            if(shape.getShapeName().equals("tableauProduction")){
                tableauProduction = (XSLFTable) shape;
            }


            if(shape instanceof XSLFTextShape){
                PowerpointExporterTools.replaceTextInTextShape(remplacements,(XSLFTextShape) shape);
            }
        }
        if(tableauEmetteurs == null || tableauProduction == null){
            return;
        }

        List<String> photo = new ArrayList<>();

        List<Chauffage> producteurs = new ArrayList<>();
        List<Chauffage> emetteur = new ArrayList<>();
        List<Chauffage> producteursEmetteurs = new ArrayList<>();
        for(Chauffage chauffage : releve.chauffages.values()){
            if(chauffage.images != null)
                photo.addAll(chauffage.images);
            XSLFTableRow row = null;
            switch (chauffage.categorie){
                case Producteur:
                    producteurs.add(chauffage);
                    row = tableauProduction.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauProduction.getRows().get(2), row);

                    PowerpointExporterTools.addTextToCell(row.getCells().get(0), getZonesText(chauffage.zones));
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1),""+chauffage.quantite);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2),chauffage.type);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3),"(" + chauffage.marque + " : " + chauffage.puissance + " kW )");

                    PowerpointExporterTools.copyRowStyle(tableauProduction.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colors[1]);

                    break;
                case Emetteur:
                    emetteur.add(chauffage);

                    row = tableauEmetteurs.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauEmetteurs.getRows().get(2), row);

                    PowerpointExporterTools.addTextToCell(row.getCells().get(0), getZonesText(chauffage.zones));
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1),""+chauffage.quantite);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2),chauffage.type);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3),"(" + chauffage.marque + " : " + chauffage.puissance + " kW )");

                    PowerpointExporterTools.copyRowStyle(tableauEmetteurs.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colors[0]);

                    break;
                case ProducteurEmetteur:
                    producteursEmetteurs.add(chauffage);

                    row = tableauEmetteurs.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauEmetteurs.getRows().get(2), row);

                    PowerpointExporterTools.addTextToCell(row.getCells().get(0), getZonesText(chauffage.zones));
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1),""+chauffage.quantite);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2),chauffage.type);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3),"(" + chauffage.marque + " : " + chauffage.puissance + " kW )");

                    PowerpointExporterTools.copyRowStyle(tableauEmetteurs.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colors[0]);
                    break;
            }
            if(row != null && chauffage.aVerifier)
                PowerpointExporterTools.setAverfierStyleToRow(row);

        }

        for(Climatisation climatisation : releve.climatisations.values()){
            if(climatisation.images != null)
                photo.addAll(climatisation.images);
            XSLFTableRow row = tableauEmetteurs.addRow();
            PowerpointExporterTools.copyNumberOfCells(tableauEmetteurs.getRows().get(2), row);

            PowerpointExporterTools.addTextToCell(row.getCells().get(0), getZonesText(climatisation.zones));
            PowerpointExporterTools.addTextToCell(row.getCells().get(1),""+climatisation.quantite);
            PowerpointExporterTools.addTextToCell(row.getCells().get(2),climatisation.type);
            PowerpointExporterTools.addTextToCell(row.getCells().get(3),"(" + climatisation.marque + " : " + climatisation.puissance + " kW )");

            PowerpointExporterTools.copyRowStyle(tableauEmetteurs.getRows().get(2), row);
            PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colors[0]);

            if(climatisation.aVerifier)
                PowerpointExporterTools.setAverfierStyleToRow(row);

        }
        addImagesToSlide(ppt, slide, photo, rectangle2DPhoto);

        PowerpointExporterTools.updateCellAnchor(tableauEmetteurs, 20);
        PowerpointExporterTools.updateCellAnchor(tableauProduction, 20);

        tableauProduction.setAnchor(new Rectangle2D.Double(tableauEmetteurs.getAnchor().getX(),
                tableauEmetteurs.getAnchor().getY() + tableauEmetteurs.getAnchor().getHeight() + 2,
                tableauProduction.getAnchor().getWidth(),
                tableauProduction.getAnchor().getHeight())
        );

        tableauEmetteurs.removeRow(2);
        tableauProduction.removeRow(2);


        if(producteurs.size() == 0){
            slide.removeShape(tableauProduction);
        }

    }



    private void slidePreconisations(XMLSlideShow ppt, XSLFSlide slide) {
        if(releve.preconisations.size() == 0){
            slide.getShapes().clear();
            return;
        }

        XSLFTable tableauPreconisations = null;
        Rectangle2D rectangle2DPhoto = null;
        for(XSLFShape shape : slide){
            if(shape.getShapeName().equals("tableauPreconisations")){
                tableauPreconisations = (XSLFTable) shape;
            }
            if(shape.getShapeName().equals("photo")){
                rectangle2DPhoto = shape.getAnchor();
            }
        }
        if(tableauPreconisations == null){
            return;
        }

        List<String> images = new ArrayList<>();
        for(String preconisation : releve.preconisations){
            if(platformProvider.isStringAPath(preconisation)) {
                images.add(preconisation);
                continue;
            }
            XSLFTableRow row = tableauPreconisations.addRow();
            PowerpointExporterTools.copyNumberOfCells(tableauPreconisations.getRows().get(0), row);

            PowerpointExporterTools.addTextToCell(row.getCells().get(0), preconisation);
            PowerpointExporterTools.copyRowStyle(tableauPreconisations.getRows().get(0), row);
            row.getCells().get(0).setFillColor( new Color(0,0,0,0) );
            row.getCells().get(0).setBorderColor(TableCell.BorderEdge.left, colors[1]);
            row.getCells().get(0).setBorderColor(TableCell.BorderEdge.right, colors[1]);

        }

        addImagesToSlide(ppt, slide, images, rectangle2DPhoto);


        tableauPreconisations.removeRow(0);

        PowerpointExporterTools.updateCellAnchor(tableauPreconisations, 20);


        if(tableauPreconisations.getNumberOfRows() == 0){
            slide.removeShape(tableauPreconisations);
            return;
        }

        tableauPreconisations.getCell(0, 0).setBorderColor(TableCell.BorderEdge.top, colors[1]);
        tableauPreconisations.getCell(tableauPreconisations.getNumberOfRows() - 1, 0).setBorderColor(TableCell.BorderEdge.bottom, colors[1]);





    }

    private void mergeCellsIfSameZone(ZoneElementTableauData zoneElementTableauData, String nomZone, XSLFTable tableauMur){
        //if the cell above have the same zone name merge the cell zone name
        if(zoneElementTableauData.lastZoneName.equals(nomZone)){
            tableauMur.mergeCells(zoneElementTableauData.indexRowSameZone, zoneElementTableauData.indexRow, 0, 0);
            PowerpointExporterTools.addLineToCell(tableauMur.getRows().get(zoneElementTableauData.indexRowSameZone).getCells().get(0));
        }else{
            zoneElementTableauData.indexRowSameZone++;
        }
        zoneElementTableauData.indexRow++;
        zoneElementTableauData.lastZoneName = nomZone;
    }


    private String formateDate(Calendar calendar){
        String pattern = "dd MMMMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("fr", "FR"));
        return simpleDateFormat.format(calendar.getTime());
    }


    public void addImagesToSlide(XMLSlideShow ppt, XSLFSlide slide, List<String> imagePaths, Rectangle2D anchor) {
        // Calculate the number of rows and columns based on the number of images
        int numImages = imagePaths.size();
        int numRows = (int)Math.round(Math.sqrt(numImages));
        int numCols = (int)Math.ceil((double)numImages / numRows);

        // Calculate the size of each cell in the grid
        double cellWidth = anchor.getWidth() / numCols;
        double cellHeight = anchor.getHeight() / numRows;

        for (int i = 0; i < numImages; i++) {
            // Calculate the position of this image in the grid
            int row = i / numCols;
            int col = i % numCols;
            double x = anchor.getX() + cellWidth * col;
            double y = anchor.getY() + cellHeight * row;

            byte[] pictureBytes = platformProvider.getImagesByteFromPath(imagePaths.get(i));
            if(pictureBytes == null){
                continue;
            }
            // Add the image to the slideshow
            //get pictureType from the extension of the file
            XSLFPictureData pictureData = ppt.addPicture(pictureBytes, PowerpointExporterTools.getPictureTypeFromFileExtension(imagePaths.get(i)));
            XSLFPictureShape picture = slide.createPicture(pictureData);


            // Calculate the appropriate dimensions for the image
            double originalWidth = picture.getPictureData().getImageDimension().getWidth();
            double originalHeight = picture.getPictureData().getImageDimension().getHeight();
            double targetWidth, targetHeight;
            if (originalWidth / originalHeight > cellWidth / cellHeight) {
                // If the image is relatively wider than the cell, adjust its width to the cell width and scale its height to maintain the aspect ratio
                targetWidth = cellWidth;
                targetHeight = (originalHeight / originalWidth) * cellWidth;
            } else {
                // If the image is relatively taller than the cell, adjust its height to the cell height and scale its width to maintain the aspect ratio
                targetHeight = cellHeight;
                targetWidth = (originalWidth / originalHeight) * cellHeight;
            }

            // Resize the image to fit within its cell in the grid, while maintaining its aspect ratio
            picture.setAnchor(new java.awt.geom.Rectangle2D.Double(x, y, targetWidth, targetHeight));
        }
    }


    private String getZonesText(List<String> zones){
        StringBuilder builder = new StringBuilder("  ");
        for (String zone : zones) {
            builder.append(zone).append(", ");
        }
        builder.delete(Math.max(0,builder.length() - 2) , builder.length());
        return builder.toString();
    }


    private class ZoneElementTableauData{
        public int indexRow;
        public int indexRowSameZone;
        public String lastZoneName;

        public ZoneElementTableauData(int indexRow, int indexRowSameZone, String lastZoneName) {
            this.indexRow = indexRow;
            this.indexRowSameZone = indexRowSameZone;
            this.lastZoneName = lastZoneName;
        }
    }

}
