package com.example.super_cep.model.Export;

import android.content.Context;

import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueElectrique;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Calendrier.CalendrierDate;
import com.example.super_cep.model.Releve.Calendrier.ChaufferOccuper;
import com.example.super_cep.model.Releve.Chauffage.Chauffage;
import com.example.super_cep.model.Releve.Chauffage.ChauffageCentraliser;
import com.example.super_cep.model.Releve.Chauffage.ChauffageDecentraliser;
import com.example.super_cep.model.Releve.Climatisation;
import com.example.super_cep.model.Releve.ECS;
import com.example.super_cep.model.Releve.Enveloppe.Eclairage;
import com.example.super_cep.model.Releve.Enveloppe.Menuiserie;
import com.example.super_cep.model.Releve.Enveloppe.Mur;
import com.example.super_cep.model.Releve.Enveloppe.Sol;
import com.example.super_cep.model.Releve.Enveloppe.Toiture;
import com.example.super_cep.model.Releve.Zone;
import com.example.super_cep.model.Releve.ZoneElement;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.model.Releve.Remarque;
import com.example.super_cep.model.Releve.Ventilation;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PowerpointExporter {

    public static final String POWERPOINT_VIERGE_NAME = "powerpointvierge.pptx";
    public static final Color[] colors = new Color[]{new Color(191, 143, 0), new Color(31, 78, 120), new Color(255, 43, 43)};
    public static final String TEXT_AUCUNE_PROTECTION_SOLAIRE = "Aucune";
    public static final String TEXT_ABSENCE_REGULATION = "En fonctionnement continu";
    public static final String TEXT_AUCUN_ISOLANT = "Aucun";
    private Releve releve;
    private Map<String, String> remplacements;

    private Context context;

    private PlatformProvider platformProvider;

    private Map<String, PaintStyle> zonesColors;
    private List<PaintStyle> colorsForZones;

    private int colorForNewZoneIndex = 0;

    public PowerpointExporter(PlatformProvider platformProvider) {
        this.platformProvider = platformProvider;
        this.zonesColors = new HashMap<>();
        this.colorsForZones = new ArrayList<>();
    }

    public void export(InputStream powerpointVierge, FileDescriptor file, Releve releve) {
        this.releve = releve;
        setupReleve();

        try (InputStream is = powerpointVierge;
             XMLSlideShow ppt = new XMLSlideShow(is)) {

            List<XSLFSlide> slides = ppt.getSlides();
            XSLFSlide slideDescriptifEnveloppeThermique = slides.get(4);
            XSLFSlide slideDescriptifDesSystem = slides.get(5);
            XSLFSlide slideDescriptifDuChauffage = slides.get(6);
            XSLFSlide slidePreconisations = slides.get(7);


            slideTitre(ppt, slides.get(0));
            slideBatiment(ppt, slides.get(1));
            slideEnergieEtConsomations(ppt,slides.get(2));
            slideUsageEtOccupationDuBatiment(ppt, slides.get(3));
            slideDescriptifEnveloppeThermique(ppt, slideDescriptifEnveloppeThermique, null);
            slideDescriptifDesSystem(ppt, slideDescriptifDesSystem, null, null, null);
            slideDescriptifDuChauffage(ppt, slideDescriptifDuChauffage);
            slidePreconisations(ppt, slidePreconisations);

            CreateChartToSlide wb = new CreateChartToSlide();
            XSLFSlide slide = ppt.createSlide();


            try (FileOutputStream out = new FileOutputStream(file)) {
                ppt.write(out);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setupReleve() {
        remplacements = new HashMap<>();
        if (releve.nomBatiment != null) remplacements.put("nomBatiment", releve.nomBatiment);


        //remarques :
        if (releve.remarques != null) {
            for (Map.Entry<String, Remarque> entry : releve.remarques.entrySet()) {
                remplacements.put(entry.getKey(), entry.getValue().description);
            }
        }
    }


    private void slideTitre(XMLSlideShow ppt, XSLFSlide slide) {
        Rectangle2D rectangle2DImageBatiment = null;
        for (XSLFShape shape : slide) {
            if (shape instanceof XSLFTextShape) {
                PowerpointExporterTools.replaceTextInTextShape(remplacements, (XSLFTextShape) shape);
            }

            if (shape.getShapeName().startsWith("colorZone")) {
                XSLFTextShape textShape = (XSLFTextShape) shape;
               PaintStyle color = textShape.getTextParagraphs().get(0).getTextRuns().get(0).getFontColor();
                colorsForZones.add(color);
            }
            if (shape.getShapeName().equals("photoBatiment")) {
                rectangle2DImageBatiment = shape.getAnchor();
            }
        }
        if (rectangle2DImageBatiment == null) {
            return;
        }
        if (releve.imageBatiment != null) {
            addImagesToSlide(ppt, slide, List.of(releve.imageBatiment), rectangle2DImageBatiment);
        }

    }

    private void slideBatiment(XMLSlideShow ppt, XSLFSlide slide) {
        Rectangle2D rectangle2DImageBatiment = null;
        for (XSLFShape shape : slide) {
            if (shape instanceof XSLFTextShape) {
                PowerpointExporterTools.replaceTextInTextShape(remplacements, (XSLFTextShape) shape);
            }

            if (shape.getShapeName().equals("photoBatiment")) {
                rectangle2DImageBatiment = shape.getAnchor();
            }
            if (shape.getShapeName().equals("tableauBatiment")) {
                XSLFTable table = (XSLFTable) shape;
                XSLFTableCell cellAddress = table.getCell(0, 1);
                cellAddress.getTextBody().setText(releve.adresse != null && !releve.adresse.isEmpty() ? releve.adresse : "Inconnue");

                XSLFTableCell surfaceTotaleChauffe = table.getCell(1, 1);
                surfaceTotaleChauffe.getTextBody().setText(releve.surfaceTotaleChauffe != 0 ? releve.surfaceTotaleChauffe + " m²" : "Inconnue");

                XSLFTableCell cellDateConstruction = table.getCell(2, 1);
                cellDateConstruction.getTextBody().setText(releve.dateDeConstruction != null ? formateDate(releve.dateDeConstruction) : "Inconnue");

                XSLFTableCell cellDateDeRenovation = table.getCell(3, 1);
                cellDateDeRenovation.getTextBody().setText(releve.dateDeDerniereRenovation != null ? formateDate(releve.dateDeDerniereRenovation) : "Inconnue");


            }
        }
        if (rectangle2DImageBatiment == null) {
            return;
        }
        if (releve.imageBatiment != null) {
            addImagesToSlide(ppt, slide, List.of(releve.imageBatiment), rectangle2DImageBatiment);
        }


    }

    private void slideEnergieEtConsomations(XMLSlideShow ppt, XSLFSlide slide) {
        Rectangle2D rectangle2DImages = null;
        List<String> images = new ArrayList<>();
        for (XSLFShape shape : slide) {
            if (shape instanceof XSLFTextShape) {
                PowerpointExporterTools.replaceTextInTextShape(remplacements, (XSLFTextShape) shape);
            }

            if(shape.getShapeName().equals("photoApprovisionnement")){
                rectangle2DImages = shape.getAnchor();
            }
            if (shape.getShapeName().equals("tableauApprovisionnementEnergetique")) {

                XSLFTable table = (XSLFTable) shape;
                XSLFTableRow tableName = table.getRows().get(0);
                XSLFTableRow tableHeader = table.getRows().get(1);

                XSLFTableRow rowExemple = table.getRows().get(2);


                for (ApprovisionnementEnergetique approvisionnementEnergetique : releve.approvisionnementEnergetiques.values()) {
                    if(approvisionnementEnergetique.images != null)
                        images.addAll(approvisionnementEnergetique.images);
                    XSLFTableRow rowAppro = table.addRow();
                    rowAppro.setHeight(rowExemple.getHeight());

                    for (int i = 0; i < rowExemple.getCells().size(); i++) {
                        rowAppro.addCell();
                    }
                    List<XSLFTableCell> cells = rowAppro.getCells();
                    cells.get(0).setText(approvisionnementEnergetique.energie);

                    cells.get(1).setText(approvisionnementEnergetique.nom);
                    if (approvisionnementEnergetique instanceof ApprovisionnementEnergetiqueElectrique) {
                        cells.get(2).setText(new DecimalFormat("0.#").format(((ApprovisionnementEnergetiqueElectrique) approvisionnementEnergetique).puissance) + " kVA");
                        cells.get(3).setText(((ApprovisionnementEnergetiqueElectrique) approvisionnementEnergetique).formuleTarifaire);
                    }
                    PowerpointExporterTools.copyRowStyle(rowExemple, rowAppro);

                    if (approvisionnementEnergetique.aVerifier)
                        PowerpointExporterTools.setAverfierStyleToRow(rowAppro);


                }
                table.removeRow(2);
            }
        }
        if(images.size() > 0){
            addImagesToSlide(ppt, slide, images, rectangle2DImages);
        }
    }


    private void slideUsageEtOccupationDuBatiment(XMLSlideShow ppt, XSLFSlide slide) {
        Calendrier[] calendriers = releve.getCalendriersValues();
        if (calendriers.length == 0) {
            for (XSLFShape shape : slide.getShapes().toArray(new XSLFShape[0])) {
                String name = shape.getShapeName();
                if (name.equals("tableauUsageEtOccupation")) {
                    slide.removeShape(shape);
                }
                if (name.equals("Usage et occupation du bâtiment")) {
                    if (remplacements.containsKey("Usage et occupation du bâtiment")) {
                        PowerpointExporterTools.replaceTextInTextShape(remplacements, (XSLFTextShape) shape);
                    } else {
                        ((XSLFTextShape) shape).getTextBody().setText("Aucune remarque");
                    }
                }
                if (name.equals("nomCalendrier")) {
                    ((XSLFTextShape) shape).getTextBody().setText("Aucun calendrier");

                }
                if (name.equals("nomZones")) {
                    ((XSLFTextShape) shape).getTextBody().setText("Aucune zone");
                }

            }

            return;
        }
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
                if (shape instanceof XSLFTextShape) {
                    PowerpointExporterTools.replaceTextInTextShape(remplacements, (XSLFTextShape) shape);
                }
                if (shape.getShapeName().equals("tableauUsageEtOccupation")) {
                    setupCalendrier((XSLFTable) shape, calendrier.calendrierDateChaufferOccuperMap);
                }

                if (shape.getShapeName().equals("nomCalendrier")) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    textShape.getTextBody().setText(calendrier.nom);
                }
                if (shape.getShapeName().equals("nomZones")) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    setTextZoneofCell(textShape,calendrier.zones);
                    setColorOfTextZone(textShape);
                }
            }

        }
    }



    private void slideDescriptifEnveloppeThermique(XMLSlideShow ppt, XSLFSlide slide, Map<String, Zone> zones) {
        XSLFSlide slideCopy = PowerpointExporterTools.duplicateSlide(ppt, slide);
        ppt.setSlideOrder(slideCopy, slide.getSlideNumber());


        Rectangle2D rectangle2DImage = null;
        List<String> tablesNames = List.of("tableauMur", "tableauToiture", "tableauSols", "tableauMenuiseries");
        Map<String, XSLFTable> tablesMap = new HashMap<>();
        double maxTableauHeight = ppt.getPageSize().getHeight() - 100;
        for (XSLFShape shape : slide.getShapes()) {
            if (shape.getShapeName().equals("photo")) {
                rectangle2DImage = shape.getAnchor();
            }

            if (tablesNames.contains(shape.getShapeName())) {
                tablesMap.put(shape.getShapeName(), (XSLFTable) shape);
            }

            if (shape instanceof XSLFTextShape) {
                PowerpointExporterTools.replaceTextInTextShape(remplacements, (XSLFTextShape) shape);
            }


            if (shape.getShapeName().equals("Descriptif de l'enveloppe thermique")) {
                maxTableauHeight = shape.getAnchor().getY();
            }

        }
        XSLFTable tableauMur = tablesMap.get("tableauMur");
        XSLFTable tableauToiture = tablesMap.get("tableauToiture");
        XSLFTable tableauSols = tablesMap.get("tableauSols");
        XSLFTable tableauMenuiseries = tablesMap.get("tableauMenuiseries");

        XSLFTable[] tables = new XSLFTable[]{tableauMur, tableauToiture, tableauMenuiseries, tableauSols};

        int indexColorZone = 0;

        ZoneElementTableauData zoneElementTableauMur = new ZoneElementTableauData(3, 2, "");
        ZoneElementTableauData zoneElementTableauToiture = new ZoneElementTableauData(3, 2, "");
        ZoneElementTableauData zoneElementTableauSols = new ZoneElementTableauData(3, 2, "");
        ZoneElementTableauData zoneElementTableauMenuiseries = new ZoneElementTableauData(3, 2, "");


        List<String> images = new ArrayList<>();

        if (zones == null) {
            zones = releve.zones;
        }
        Map<String, Zone> zonesOverflow = new HashMap<>();

        for (Zone zone : zones.values()) {
            Color colorZoneName = colors[indexColorZone % colors.length];
            indexColorZone++;

            for (ZoneElement zoneElement : zone.getZoneElementsValues()) {
                if (!PowerpointExporterTools.updateTableauAnchor(platformProvider, tables, maxTableauHeight)) {
                    if (zonesOverflow.containsKey(zone.nom)) {
                        zonesOverflow.get(zone.nom).addZoneElement(zoneElement);
                    } else {
                        zonesOverflow.put(zone.nom, new Zone(zone.nom, List.of(zoneElement)));
                    }
                    continue;
                }
                XSLFTableRow row = null;
                //get a random color based on the zone name :
                if (zoneElement instanceof Mur) {
                    if (zoneElement.images != null)
                        images.addAll(zoneElement.images);

                    Mur mur = (Mur) zoneElement;
                    row = tableauMur.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauMur.getRows().get(2), row);
                    setTextZoneofCell(row.getCells().get(0), List.of(zone.nom.isEmpty() ? " " : zone.nom));
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1), "Mur");
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2), mur.typeMur.isEmpty() ? " " : mur.typeMur);
                    if(mur.typeMiseEnOeuvre.equals(TEXT_AUCUN_ISOLANT)){
                        PowerpointExporterTools.addTextToCell(row.getCells().get(3), " ");
                        PowerpointExporterTools.addTextToCell(row.getCells().get(4), " ");
                    }else{
                        PowerpointExporterTools.addTextToCell(row.getCells().get(3), mur.niveauIsolation.isEmpty() ? " " : mur.niveauIsolation);
                        PowerpointExporterTools.addTextToCell(row.getCells().get(4), "(" + mur.typeIsolant + ";" + new DecimalFormat("0.#").format(mur.epaisseurIsolant) + " cm)");
                    }

                    PowerpointExporterTools.copyRowStyle(tableauMur.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colorZoneName);

                    mergeCellsIfSameZone(zoneElementTableauMur, zone.nom, tableauMur);

                    setColorOfTextZone(row.getCells().get(0));
                }

                if (zoneElement instanceof Toiture) {
                    if (zoneElement.images != null)
                        images.addAll(zoneElement.images);

                    Toiture toiture = (Toiture) zoneElement;
                    row = tableauToiture.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauToiture.getRows().get(2), row);
                    setTextZoneofCell(row.getCells().get(0), List.of(zone.nom.isEmpty() ? " " : zone.nom));
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1), toiture.typeToiture.isEmpty() ? " " : toiture.typeToiture);
                    if(toiture.typeMiseEnOeuvre.equals(TEXT_AUCUN_ISOLANT)){
                        PowerpointExporterTools.addTextToCell(row.getCells().get(2), " ");
                        PowerpointExporterTools.addTextToCell(row.getCells().get(3), " ");
                    }else{
                        PowerpointExporterTools.addTextToCell(row.getCells().get(2), toiture.niveauIsolation.isEmpty() ? " " : toiture.niveauIsolation);
                        PowerpointExporterTools.addTextToCell(row.getCells().get(3), "(" + toiture.typeIsolant + ";" + new DecimalFormat("0.#").format(toiture.epaisseurIsolant) + " cm)");
                    }
                    PowerpointExporterTools.copyRowStyle(tableauToiture.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colorZoneName);

                    mergeCellsIfSameZone(zoneElementTableauToiture, zone.nom, tableauToiture);
                    setColorOfTextZone(row.getCells().get(0));
                }

                if (zoneElement instanceof Menuiserie) {
                    if (zoneElement.images != null)
                        images.addAll(zoneElement.images);

                    Menuiserie menuiserie = (Menuiserie) zoneElement;
                    row = tableauMenuiseries.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauMenuiseries.getRows().get(2), row);
                    setTextZoneofCell(row.getCells().get(0), List.of(zone.nom.isEmpty() ? " " : zone.nom));
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1), menuiserie.typeMenuiserie.isEmpty() ? " " : menuiserie.typeMenuiserie);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2), menuiserie.materiau.isEmpty() ? " " : menuiserie.materiau);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3), menuiserie.typeVitrage.isEmpty() ? " " : menuiserie.typeVitrage);
                    if (!menuiserie.protectionsSolaires.equals(TEXT_AUCUNE_PROTECTION_SOLAIRE)) {
                        PowerpointExporterTools.addTextToCell(row.getCells().get(4), "Avec " + menuiserie.protectionsSolaires);
                    }
                    PowerpointExporterTools.copyRowStyle(tableauMenuiseries.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colorZoneName);

                    mergeCellsIfSameZone(zoneElementTableauMenuiseries, zone.nom, tableauMenuiseries);
                    setColorOfTextZone(row.getCells().get(0));
                }

                if (zoneElement instanceof Sol) {
                    if (zoneElement.images != null)
                        images.addAll(zoneElement.images);

                    Sol sol = (Sol) zoneElement;
                    row = tableauSols.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauSols.getRows().get(2), row);
                    setTextZoneofCell(row.getCells().get(0), List.of(zone.nom.isEmpty() ? " " : zone.nom));
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1), sol.typeSol.isEmpty() ? " " : sol.typeSol);
                    if(sol.niveauIsolation.equals(TEXT_AUCUN_ISOLANT)){
                        PowerpointExporterTools.addTextToCell(row.getCells().get(2), " ");
                        PowerpointExporterTools.addTextToCell(row.getCells().get(3), " ");
                    }else{
                        PowerpointExporterTools.addTextToCell(row.getCells().get(2), sol.niveauIsolation.isEmpty() ? " " : sol.niveauIsolation);
                        PowerpointExporterTools.addTextToCell(row.getCells().get(3), "(" + sol.typeIsolant + ";" + new DecimalFormat("0.#").format(sol.epaisseurIsolant) + " cm)");
                    }
                    PowerpointExporterTools.copyRowStyle(tableauSols.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colorZoneName);

                    mergeCellsIfSameZone(zoneElementTableauSols, zone.nom, tableauSols);
                    setColorOfTextZone(row.getCells().get(0));
                }


                if (row != null && zoneElement.aVerifier)
                    PowerpointExporterTools.setAverfierStyleToRow(row);

            }
        }
        addImagesToSlide(ppt, slide, images, rectangle2DImage);


        PowerpointExporterTools.updateTableauAnchor(platformProvider, slide, tables);

        tableauMur.removeRow(2);
        tableauToiture.removeRow(2);
        tableauMenuiseries.removeRow(2);
        tableauSols.removeRow(2);


        if (zonesOverflow.size() > 0) {
            slideDescriptifEnveloppeThermique(ppt, slideCopy, zonesOverflow);
        } else {
            ppt.removeSlide(slideCopy.getSlideNumber() - 1);
        }
    }


    private void slideDescriptifDesSystem(XMLSlideShow ppt, XSLFSlide slide, Map<String, Zone> eclairages, Ventilation[] ventilations, ECS[] ecss) {
        XSLFSlide slideCopy = PowerpointExporterTools.duplicateSlide(ppt, slide);
        ppt.setSlideOrder(slideCopy, slide.getSlideNumber());


        Rectangle2D rectangle2DImages = null;
        List<String> tablesNames = List.of("tableauEclairage", "tableauVentilation", "tableauECS", "tableauMenuiseries");
        Map<String, XSLFTable> tablesMap = new HashMap<>();
        for (XSLFShape shape : slide.getShapes()) {
            if (shape.getShapeName().equals("photo")) {
                rectangle2DImages = shape.getAnchor();
            }

            if (tablesNames.contains(shape.getShapeName())) {
                tablesMap.put(shape.getShapeName(), (XSLFTable) shape);
            }

            if (shape instanceof XSLFTextShape) {
                PowerpointExporterTools.replaceTextInTextShape(remplacements, (XSLFTextShape) shape);
            }
        }
        XSLFTable tableauEclairage = tablesMap.get("tableauEclairage");
        XSLFTable tableauVentilation = tablesMap.get("tableauVentilation");
        XSLFTable tableauECS = tablesMap.get("tableauECS");

        XSLFTable[] tables = new XSLFTable[]{tableauEclairage, tableauVentilation, tableauECS};

        int indexColorZone = 0;

        ZoneElementTableauData zoneElementTableauEclairage = new ZoneElementTableauData(3, 2, "");
        ZoneElementTableauData zoneElementTableauVentilation = new ZoneElementTableauData(3, 2, "");
        ZoneElementTableauData zoneElementTableauECS = new ZoneElementTableauData(3, 2, "");

        List<String> images = new ArrayList<>();

        if (eclairages == null) {
            eclairages = releve.zones;
            ventilations = releve.ventilations.values().toArray(new Ventilation[0]);
            ecss = releve.ecs.values().toArray(new ECS[0]);
        }

        Map<String, Zone> eclairageOverflow = new HashMap<>();
        List<Ventilation> ventilationOverflow = new ArrayList<>();
        List<ECS> ecsOverflow = new ArrayList<>();

        for (Zone zone : eclairages.values()) {
            Color colorZoneName = colors[indexColorZone % colors.length];
            indexColorZone++;

            for (ZoneElement zoneElement : zone.getZoneElementsValues()) {
                if (!PowerpointExporterTools.updateTableauAnchor(platformProvider, slide, tables)) {
                    if (eclairageOverflow.containsKey(zone.nom)) {
                        eclairageOverflow.get(zone.nom).addZoneElement(zoneElement);
                    } else {
                        eclairageOverflow.put(zone.nom, new Zone(zone.nom, List.of(zoneElement)));
                    }
                    continue;
                }

                //get a random color based on the zone name :
                if (zoneElement instanceof Eclairage) {
                    if (zoneElement.images != null)
                        images.addAll(zoneElement.images);

                    Eclairage eclairage = (Eclairage) zoneElement;
                    XSLFTableRow row = tableauEclairage.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauEclairage.getRows().get(2), row);

                    setTextZoneofCell(row.getCells().get(0), List.of(zone.nom));
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1), eclairage.typeEclairage);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2), eclairage.typeDeRegulation.isEmpty() ? " " :  eclairage.typeDeRegulation.equals(TEXT_ABSENCE_REGULATION) ? eclairage.typeDeRegulation : "Régulé par " + eclairage.typeDeRegulation);

                    PowerpointExporterTools.copyRowStyle(tableauEclairage.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colorZoneName);

                    mergeCellsIfSameZone(zoneElementTableauEclairage, zone.nom, tableauEclairage);

                    setColorOfTextZone(row.getCells().get(0));
                    if (zoneElement.aVerifier)
                        PowerpointExporterTools.setAverfierStyleToRow(row);

                }
            }
        }

        for (Ventilation ventilation : ventilations) {
            if (!PowerpointExporterTools.updateTableauAnchor(platformProvider, slide, tables)) {
                ventilationOverflow.add(ventilation);
                continue;
            }

            if (ventilation.images != null)
                images.addAll(ventilation.images);

            XSLFTableRow row = tableauVentilation.addRow();
            PowerpointExporterTools.copyNumberOfCells(tableauVentilation.getRows().get(2), row);

            setTextZoneofCell(row.getCells().get(0),ventilation.zones);
            PowerpointExporterTools.addTextToCell(row.getCells().get(1), ventilation.type);
            PowerpointExporterTools.addTextToCell(row.getCells().get(2),ventilation.regulation.isEmpty() ? " " :  ventilation.regulation.equals(TEXT_ABSENCE_REGULATION) ? ventilation.regulation : "Régulé par " + ventilation.regulation);
            PowerpointExporterTools.copyRowStyle(tableauVentilation.getRows().get(2), row);

            mergeCellsIfSameZone(zoneElementTableauVentilation, row.getCells().get(0).getText(), tableauVentilation);

            setColorOfTextZone(row.getCells().get(0));
            if (ventilation.aVerifier)
                PowerpointExporterTools.setAverfierStyleToRow(row);

        }

        for (ECS ecs : ecss) {
            if (!PowerpointExporterTools.updateTableauAnchor(platformProvider, slide, tables)) {
                ecsOverflow.add(ecs);
                continue;
            }

            if (ecs.images != null)
                images.addAll(ecs.images);
            XSLFTableRow row = tableauECS.addRow();
            PowerpointExporterTools.copyNumberOfCells(tableauECS.getRows().get(2), row);

            setTextZoneofCell(row.getCells().get(0),ecs.zones);
            PowerpointExporterTools.addTextToCell(row.getCells().get(1), ecs.type);
            PowerpointExporterTools.addTextToCell(row.getCells().get(2), "( " + ecs.marque + " : " + new DecimalFormat("0.#").format(ecs.volume) + " L )");
            PowerpointExporterTools.copyRowStyle(tableauECS.getRows().get(2), row);

            mergeCellsIfSameZone(zoneElementTableauECS, row.getCells().get(0).getText(), tableauECS);

            setColorOfTextZone(row.getCells().get(0));

            if (ecs.aVerifier)
                PowerpointExporterTools.setAverfierStyleToRow(row);


        }
        addImagesToSlide(ppt, slide, images, rectangle2DImages);

        tableauEclairage.removeRow(2);
        tableauVentilation.removeRow(2);
        tableauECS.removeRow(2);

        if (eclairageOverflow.size() > 0 || ventilationOverflow.size() > 0 || ecsOverflow.size() > 0) {
            slideDescriptifDesSystem(ppt, slideCopy, eclairageOverflow, ventilationOverflow.toArray(new Ventilation[0]), ecsOverflow.toArray(new ECS[0]));
        } else {
            ppt.removeSlide(slideCopy.getSlideNumber() - 1);
        }

    }



    private void slideDescriptifDuChauffage(XMLSlideShow ppt, XSLFSlide slide) {
        Rectangle2D rectangle2DPhoto = null;

        XSLFTable tableauEmetteurs = null;
        XSLFTable tableauProduction = null;
        for (XSLFShape shape : slide.getShapes()) {
            if (shape.getShapeName().equals("photo")) {
                rectangle2DPhoto = shape.getAnchor();
            }

            if (shape.getShapeName().equals("tableauEmetteurs")) {
                tableauEmetteurs = (XSLFTable) shape;
            }
            if (shape.getShapeName().equals("tableauProduction")) {
                tableauProduction = (XSLFTable) shape;
            }


            if (shape instanceof XSLFTextShape) {
                PowerpointExporterTools.replaceTextInTextShape(remplacements, (XSLFTextShape) shape);
            }
        }
        if (tableauEmetteurs == null || tableauProduction == null) {
            return;
        }

        List<String> photo = new ArrayList<>();

        List<Chauffage> producteurs = new ArrayList<>();
        List<Chauffage> emetteur = new ArrayList<>();
        List<Chauffage> producteursEmetteurs = new ArrayList<>();
        for (Chauffage chauffage : releve.chauffages.values()) {
            if (chauffage.images != null)
                photo.addAll(chauffage.images);
            XSLFTableRow row = null;
            switch (chauffage.categorie) {
                case Producteur:
                    producteurs.add(chauffage);
                    row = tableauProduction.addRow();
                    break;
                case Emetteur:
                    emetteur.add(chauffage);
                    row = tableauEmetteurs.addRow();
                    break;
                case ProducteurEmetteur:
                    producteursEmetteurs.add(chauffage);
                    row = tableauEmetteurs.addRow();
                    break;
            }
            if (row == null) continue;
            PowerpointExporterTools.copyNumberOfCells(tableauProduction.getRows().get(2), row);
            if(chauffage instanceof ChauffageCentraliser){
                setTextZoneofCell(row.getCells().get(0), ((ChauffageCentraliser)chauffage).zones);
            }else{
                 setTextZoneofCell(row.getCells().get(0), List.of(((ChauffageDecentraliser)chauffage).zone));
            }
            PowerpointExporterTools.addTextToCell(row.getCells().get(1), chauffage.quantite == 0 ? " " : chauffage.quantite + "");
            PowerpointExporterTools.addTextToCell(row.getCells().get(2), chauffage.type);
            StringBuilder sb = new StringBuilder("(");
            if (chauffage.marque != null && !chauffage.marque.isEmpty())
                sb.append(chauffage.marque);
            if (chauffage.marque != null && !chauffage.marque.isEmpty() && chauffage.puissance != 0)
                sb.append(" : ");
            if (chauffage.puissance != 0)
                sb.append(new DecimalFormat("0.#").format(chauffage.puissance)).append(" kW");
            sb.append(")");
            PowerpointExporterTools.addTextToCell(row.getCells().get(3), sb.toString());


            switch (chauffage.categorie) {
                case Producteur:
                    PowerpointExporterTools.copyRowStyle(tableauProduction.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colors[1]);
                    break;
                case Emetteur:
                case ProducteurEmetteur:
                    PowerpointExporterTools.copyRowStyle(tableauEmetteurs.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colors[0]);
                    break;
            }
            setColorOfTextZone(row.getCells().get(0));

            if (chauffage.aVerifier)
                PowerpointExporterTools.setAverfierStyleToRow(row);

        }

        for (Climatisation climatisation : releve.climatisations.values()) {
            if (climatisation.images != null)
                photo.addAll(climatisation.images);
            XSLFTableRow row = tableauEmetteurs.addRow();
            PowerpointExporterTools.copyNumberOfCells(tableauEmetteurs.getRows().get(2), row);

            setTextZoneofCell(row.getCells().get(0),climatisation.zones);
            PowerpointExporterTools.addTextToCell(row.getCells().get(1), "" + climatisation.quantite);
            PowerpointExporterTools.addTextToCell(row.getCells().get(2), climatisation.type);
            StringBuilder sb = new StringBuilder("(");
            if (climatisation.marque != null && !climatisation.marque.isEmpty())
                sb.append(climatisation.marque);
            if (climatisation.marque != null && !climatisation.marque.isEmpty() && climatisation.puissance != 0)
                sb.append(" : ");
            if (climatisation.puissance != 0)
                sb.append(new DecimalFormat("0.#").format(climatisation.puissance)).append(" kW");
            sb.append(")");
            PowerpointExporterTools.addTextToCell(row.getCells().get(3), sb.toString());

            PowerpointExporterTools.copyRowStyle(tableauEmetteurs.getRows().get(2), row);
            setColorOfTextZone(row.getCells().get(0));

            if (climatisation.aVerifier)
                PowerpointExporterTools.setAverfierStyleToRow(row);

        }
        addImagesToSlide(ppt, slide, photo, rectangle2DPhoto);

        PowerpointExporterTools.updateCellAnchor(platformProvider, tableauEmetteurs);
        PowerpointExporterTools.updateCellAnchor(platformProvider, tableauProduction);

        tableauProduction.setAnchor(new Rectangle2D.Double(tableauEmetteurs.getAnchor().getX(),
                tableauEmetteurs.getAnchor().getY() + tableauEmetteurs.getAnchor().getHeight() + 2,
                tableauProduction.getAnchor().getWidth(),
                tableauProduction.getAnchor().getHeight())
        );

        tableauEmetteurs.removeRow(2);
        tableauProduction.removeRow(2);


        if (producteurs.size() == 0) {
            slide.removeShape(tableauProduction);
        }

    }


    private void slidePreconisations(XMLSlideShow ppt, XSLFSlide slide) {
        if (releve.preconisations.size() == 0) {
            slide.getShapes().clear();
            return;
        }

        XSLFTable tableauPreconisations = null;
        Rectangle2D rectangle2DPhoto = null;
        for (XSLFShape shape : slide) {
            if (shape.getShapeName().equals("tableauPreconisations")) {
                tableauPreconisations = (XSLFTable) shape;
            }
            if (shape.getShapeName().equals("photo")) {
                rectangle2DPhoto = shape.getAnchor();
            }
        }
        if (tableauPreconisations == null) {
            return;
        }

        List<String> images = new ArrayList<>();
        for (String preconisation : releve.preconisations) {
            if (platformProvider.isStringAPath(preconisation)) {
                images.add(preconisation);
                continue;
            }
            XSLFTableRow row = tableauPreconisations.addRow();
            PowerpointExporterTools.copyNumberOfCells(tableauPreconisations.getRows().get(0), row);

            PowerpointExporterTools.addTextToCell(row.getCells().get(0), preconisation);
            PowerpointExporterTools.copyRowStyle(tableauPreconisations.getRows().get(0), row);
            row.getCells().get(0).setFillColor(new Color(0, 0, 0, 0));
            row.getCells().get(0).setBorderColor(TableCell.BorderEdge.left, colors[1]);
            row.getCells().get(0).setBorderColor(TableCell.BorderEdge.right, colors[1]);

        }

        addImagesToSlide(ppt, slide, images, rectangle2DPhoto);


        tableauPreconisations.removeRow(0);

        PowerpointExporterTools.updateCellAnchor(platformProvider, tableauPreconisations);


        if (tableauPreconisations.getNumberOfRows() == 0) {
            slide.removeShape(tableauPreconisations);
            return;
        }

        tableauPreconisations.getCell(0, 0).setBorderColor(TableCell.BorderEdge.top, colors[1]);
        tableauPreconisations.getCell(tableauPreconisations.getNumberOfRows() - 1, 0).setBorderColor(TableCell.BorderEdge.bottom, colors[1]);


    }

    private void mergeCellsIfSameZone(ZoneElementTableauData zoneElementTableauData, String nomZone, XSLFTable table) {
        //if the cell above have the same zone name merge the cell zone name
        if (zoneElementTableauData.lastZoneName.equals(nomZone)) {
            table.mergeCells(zoneElementTableauData.indexRowSameZone, zoneElementTableauData.indexRow, 0, 0);
            if (zoneElementTableauData.indexRow % 2 == 0) {
                PowerpointExporterTools.addLineToCell(table.getRows().get(zoneElementTableauData.indexRowSameZone).getCells().get(0));
            }
        } else {
            zoneElementTableauData.indexRowSameZone = zoneElementTableauData.indexRow;
        }
        zoneElementTableauData.indexRow++;
        zoneElementTableauData.lastZoneName = nomZone;
    }


    private String formateDate(Calendar calendar) {
        String pattern = "yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("fr", "FR"));
        return simpleDateFormat.format(calendar.getTime());
    }


    public void addImagesToSlide(XMLSlideShow ppt, XSLFSlide slide, List<String> imagePaths, Rectangle2D anchor) {
        // Calculate the number of rows and columns based on the number of images
        int numImages = imagePaths.size();
        int numRows = (int) Math.round(Math.sqrt(numImages));
        int numCols = (int) Math.ceil((double) numImages / numRows);

        // Calculate the size of each cell in the grid
        double cellWidth = anchor.getWidth() / numCols;
        double cellHeight = anchor.getHeight() / numRows;


        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Future<byte[]>[] futures = new Future[imagePaths.size()];
        int index = 0;
        for (String imagePath : imagePaths) {

            futures[index] = executor.submit(new Callable<byte[]>() {
                @Override
                public byte[] call() throws Exception {
                    return platformProvider.getImagesByteFromPath(imagePath);
                }
            });
            index++;
        }


        for (int i = 0; i < numImages; i++) {
            // Calculate the position of this image in the grid
            int row = i / numCols;
            int col = i % numCols;
            double x = anchor.getX() + cellWidth * col;
            double y = anchor.getY() + cellHeight * row;


            byte[] pictureBytes = null;
            try {
                pictureBytes = futures[i].get();
            } catch (ExecutionException e) {
                continue;
            } catch (InterruptedException e) {
                continue;
            }
            if (pictureBytes == null) {
                continue;
            }
            // Add the image to the slideshow
            //get pictureType from the extension of the file
            XSLFPictureData pictureData = ppt.addPicture(pictureBytes, PowerpointExporterTools.getPictureTypeFromBytes(pictureBytes));
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

            //center the image in the cell
            x += (cellWidth - targetWidth) / 2;

            // Resize the image to fit within its cell in the grid, while maintaining its aspect ratio
            picture.setAnchor(new java.awt.geom.Rectangle2D.Double(x, y, targetWidth, targetHeight));
        }
        executor.shutdown(); //

    }
    private void setColorOfTextZone(XSLFTextShape shape){
       setColorOfTextZoneParagraph(shape.getTextParagraphs().get(0));
    }

    private void setColorOfTextZoneParagraph(XSLFTextParagraph paragraph) {
        for (XSLFTextRun run : paragraph.getTextRuns()) {
            String zone = run.getRawText();
            if (!zonesColors.containsKey(zone)) {
                zonesColors.put(zone, colorsForZones.get(colorForNewZoneIndex++ % colorsForZones.size()));
            }
            run.setFontColor(zonesColors.get(zone));
        }
    }
    private void setColorOfTextZone(XSLFTableCell cell) {
        setColorOfTextZoneParagraph(cell.getTextParagraphs().get(0));
    }

    private void setTextZoneofCell(XSLFTableCell cell, List<String> zones) {
        // delete cell text
        cell.setText(null);
        XSLFTextParagraph paragraph = cell.addNewTextParagraph();
        XSLFTextRun lastRun = null;
        for (String zone : zones) {
            XSLFTextRun run = paragraph.addNewTextRun();
            run.setText(zone);
            XSLFTextRun run2 = paragraph.addNewTextRun();
            run2.setText(", ");
            lastRun = run2;
        }
        //remove last text run
        if(lastRun != null)
            paragraph.removeTextRun(lastRun);
    }
    private void setTextZoneofCell(XSLFTextShape shape, List<String> zones) {
        // delete cell text
        while(shape.getTextParagraphs().size() > 0){
            shape.removeTextParagraph(shape.getTextParagraphs().get(0));
        }
        XSLFTextParagraph paragraph = shape.addNewTextParagraph();
        paragraph.addNewTextRun().setText("Zones : ");
        XSLFTextRun lastRun = null;
        for (String zone : zones) {
            XSLFTextRun run = paragraph.addNewTextRun();
            run.setText(zone);
            XSLFTextRun run2 = paragraph.addNewTextRun();
            run2.setText(", ");
            lastRun = run2;
        }
        //remove last text run
        if(lastRun != null)
            paragraph.removeTextRun(lastRun);
    }
    private void setupCalendrier(XSLFTable table, Map<CalendrierDate, ChaufferOccuper> calendrierDateChaufferOccuperMap) {
        if (calendrierDateChaufferOccuperMap == null) {
            return;
        }
        XSLFTableRow tableHeader = table.getRows().get(0);

        XSLFTableRow rowExemple = table.getRows().get(1);


        for (float i = 0; i < 24; i += 0.5) {
            XSLFTableRow rowAppro = table.addRow();
            rowAppro.setHeight(rowExemple.getHeight());

            PowerpointExporterTools.copyNumberOfCells(rowExemple, rowAppro);

            List<XSLFTableCell> cells = rowAppro.getCells();
            PowerpointExporterTools.addTextToCell(cells.get(0), ((int) i) + "h" + (i % 1 == 0 ? "00" : "30"));
            for (int j = 1; j < 8; j++) {
                PowerpointExporterTools.addTextToCell(cells.get(j), " ");
            }

            PowerpointExporterTools.copyRowStyle(rowExemple, rowAppro);
        }

        table.removeRow(1);


        for (CalendrierDate calendrierDate : calendrierDateChaufferOccuperMap.keySet()) {

            List<DayOfWeek> dayOfWeeks = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            int column = dayOfWeeks.indexOf(calendrierDate.jour) + 1;
            int row = ((calendrierDate.heure * 2) + calendrierDate.minute / 30) + 1;

            ChaufferOccuper chaufferOccuper = calendrierDateChaufferOccuperMap.get(calendrierDate);
            if (chaufferOccuper == ChaufferOccuper.CHAUFFER_OCCUPER) {
                XSLFTableCell cell = table.getCell(row, column);
                setCellChaufferOccuper(cell);
            } else if (chaufferOccuper == ChaufferOccuper.CHAUFFER) {
                XSLFTableCell cell = table.getCell(row, column);
                setCellChauffer(cell);
            } else if (chaufferOccuper == ChaufferOccuper.OCCUPER) {
                XSLFTableCell cell = table.getCell(row, column);
                setCellOccuper(cell);
            }

        }
    }

    private void setCellChauffer(XSLFTableCell cell) {
        Color color = new Color(0xCE0000);
        cell.setFillColor(color);
        cell.getTextBody().setText("🔥");
    }

    private void setCellOccuper(XSLFTableCell cell) {
        Color color = new Color(0x44546a);
        cell.setFillColor(color);
    }

    private void setCellChaufferOccuper(XSLFTableCell cell) {
        Color color = new Color(0x44546a);
        cell.setFillColor(color);
        cell.getTextBody().setText("🔥");
    }


    private class ZoneElementTableauData {
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
