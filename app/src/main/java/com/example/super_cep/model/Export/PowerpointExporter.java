package com.example.super_cep.model.Export;

import com.example.super_cep.controller.Conso.Anner;
import com.example.super_cep.controller.Conso.ConsoParser;
import com.example.super_cep.controller.Conso.Energie;
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
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.util.Units;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Classe pour exporter un PowerPoint.
 * <p>
 *     Cette classe permet d'exporter un PowerPoint à partir d'un modèle.
 *     Elle permet de remplacer des variables dans le modèle par des valeurs.
 * </p>
 */
public class PowerpointExporter {

    // Constantes
    public static final String POWERPOINT_VIERGE_NAME = "powerpointvierge.pptx";
    public static final Color[] colors = new Color[]{new Color(191, 143, 0), new Color(31, 78, 120), new Color(255, 43, 43)};
    public static final String TEXT_AUCUNE_PROTECTION_SOLAIRE = "Aucune";
    public static final String TEXT_ABSENCE_REGULATION = "En fonctionnement continu";
    public static final String TEXT_AUCUN_ISOLANT = "Aucun";

    // Variables d'instance
    private Releve releve;
    private Map<String, String> remplacements;
    private final PlatformProvider platformProvider;
    private final Map<String, PaintStyle> zonesColors;
    private final List<PaintStyle> colorsForZones;
    private final ConsoParser consoParser;
    private int colorForNewZoneIndex = 0;
    private final int quality;

    /**
     * Constructeur pour la classe PowerpointExporter.
     *
     * @param platformProvider Une instance de PlatformProvider.
     * @param consoParser Une instance de ConsoParser.
     * @param quality La qualité des photo à exporter. de 0 à 100. 100 étant la meilleure qualité.
     */
    public PowerpointExporter(PlatformProvider platformProvider, ConsoParser consoParser, int quality) {
        this.platformProvider = platformProvider;
        this.zonesColors = new HashMap<>();
        this.colorsForZones = new ArrayList<>();
        this.consoParser = consoParser;
        this.quality = quality;
    }

    /**
     * Méthode pour créer un PowerPoint.
     *
     * @param powerpointVierge Un InputStream vers le PowerPoint vierge qui sert de modèle.
     * @param file Un FileDescriptor pour le fichier à créer.
     * @param releve Une instance de Releve avec les données à exporter.
     * @param nomBatimentConso Le nom du bâtiment.
     * @param annees Une liste des années à considérer.
     * @param meilleurAnne L'année la plus pertinente qui sert à faire le ratio conso / surface .
     * @param pourcentageBatiment Le pourcentage du bâtiment. de 0 à 100. Au cas où on ne veut pas prendre en compte toutes la conso du bâtiment.
     * @throws PowerpointException Si une erreur se produit lors de l'exportation. Contient dans le message l'étape où l'erreur s'est produite.
     */
    public void export(InputStream powerpointVierge, FileDescriptor file, Releve releve, String nomBatimentConso, List<String> annees, String meilleurAnne, float pourcentageBatiment) throws PowerpointException {
        this.releve = releve;
        setupReleve();
        String slideActuelle = "initialisation";
        try (InputStream is = powerpointVierge;
             XMLSlideShow ppt = new XMLSlideShow(is)) {

            List<XSLFSlide> slides = ppt.getSlides();
            XSLFSlide slideDescriptifEnveloppeThermique = slides.get(4);
            XSLFSlide slideDescriptifDesSystem = slides.get(5);
            XSLFSlide slideDescriptifDuChauffage = slides.get(6);
            XSLFSlide slidePreconisations = slides.get(7);


            slideActuelle = "slide titre";
            slideTitre(ppt, slides.get(0));
            slideActuelle = "slide bâtiment";
            slideBatiment(ppt, slides.get(1));
            slideActuelle = "slide énergie et consommations";
            slideEnergieEtConsomations(ppt,slides.get(2), nomBatimentConso, annees, meilleurAnne, pourcentageBatiment);
            slideActuelle = "slide usage et occupation du bâtiment";
            slideUsageEtOccupationDuBatiment(ppt, slides.get(3));
            slideActuelle = "slide descriptif enveloppe thermique";
            slideDescriptifEnveloppeThermique(ppt, slideDescriptifEnveloppeThermique, null);
            slideActuelle = "slide descriptif des system";
            slideDescriptifDesSystem(ppt, slideDescriptifDesSystem, null, null, null);
            slideActuelle = "slide descriptif du chauffage";
            slideDescriptifDuChauffage(ppt, slideDescriptifDuChauffage);
            slideActuelle = "slide preconisations";
            slidePreconisations(ppt, slidePreconisations);
            slideActuelle = "clean up";
            cleanUp(ppt);


            try (FileOutputStream out = new FileOutputStream(file)) {
                ppt.write(out);

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("erreur à la " + slideActuelle + " : " +  e.getMessage());
            throw new PowerpointException("erreur à la " + slideActuelle + " : " +  e.getMessage());
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
        if (releve.imageFacadeBatiment != null) {
            addImagesToSlide(ppt, slide, List.of(releve.imageFacadeBatiment), rectangle2DImageBatiment);
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

                XSLFTableCell surfaceTotalCell = table.getCell(1, 1);
                surfaceTotalCell.getTextBody().setText(releve.surfaceTotale != 0 ? new DecimalFormat("0").format(releve.surfaceTotale)  + " m²" : "Inconnue");

                XSLFTableCell surfaceTotalChauffeCell = table.getCell(2, 1);
                surfaceTotalChauffeCell.getTextBody().setText(releve.surfaceTotaleChauffe != 0 ? new DecimalFormat("0").format(releve.surfaceTotaleChauffe)  + " m²" : "Inconnue");

                XSLFTableCell cellDateConstruction = table.getCell(3, 1);
                cellDateConstruction.getTextBody().setText(releve.dateDeConstruction != null ? formateDate(releve.dateDeConstruction) : "Inconnue");

                XSLFTableCell cellDateDeRenovation = table.getCell(4, 1);
                cellDateDeRenovation.getTextBody().setText(releve.dateDeDerniereRenovation != null ? formateDate(releve.dateDeDerniereRenovation) : "Inconnue");

                XSLFTableCell cellDateDeVisite = table.getCell(5, 1);
                cellDateDeVisite.getTextBody().setText(releve.dateDeVisite != null ? new SimpleDateFormat("dd/MM/yyyy").format(releve.dateDeVisite.getTime()) : "Inconnue");


            }
        }
        if (rectangle2DImageBatiment == null) {
            return;
        }
        if (releve.imagePlanBatiment != null) {
            addImagesToSlide(ppt, slide, List.of(releve.imagePlanBatiment), rectangle2DImageBatiment);
        }

    }
    private double oldAnchorToNew(double oldAnchorValue){
        return Units.EMU_PER_POINT * oldAnchorValue;
    }

    private void slideEnergieEtConsomations(XMLSlideShow ppt, XSLFSlide slide, String nomBatimentConso, List<String> annees, String meilleurAnne, float pourcentageBatiment) {
        Rectangle2D rectangle2DImages = null;
        List<String> images = new ArrayList<>();
        List<Anner> consoWatt = null;
        List<Anner> consoEuro = null;
        if(nomBatimentConso != null){
            consoWatt = applyPourcentage(consoParser.getConsoWatt(nomBatimentConso, annees), pourcentageBatiment);
            consoEuro = applyPourcentage(consoParser.getConsoEuro(nomBatimentConso, annees), pourcentageBatiment);
        }
        XSLFTextShape textShapeRatioWatt = null;
        XSLFTextShape textShapeRatioEuro = null;
        XSLFAutoShape ellipseRatioWatt = null;
        XSLFAutoShape ellipseRatioEuro = null;
        for (XSLFShape shape : slide) {
            if (shape instanceof XSLFTextShape) {
                PowerpointExporterTools.replaceTextInTextShape(remplacements, (XSLFTextShape) shape);
            }

            if(nomBatimentConso != null && shape.getShapeName().equals("graphique1")){
                CreateChartToSlide wb = new CreateChartToSlide();
                Rectangle2D oldAnchor = shape.getAnchor();
                Rectangle2D rect = new java.awt.geom.Rectangle2D.Double(oldAnchorToNew(oldAnchor.getX()), oldAnchorToNew(oldAnchor.getY()),
                         oldAnchorToNew(oldAnchor.getWidth()), oldAnchorToNew(oldAnchor.getHeight()));
                wb.createBarChart(ppt, slide,rect, consoWatt, "kWh");
            }
            if(nomBatimentConso != null && shape.getShapeName().equals("graphiqueEuro")){
                CreateChartToSlide wb = new CreateChartToSlide();
                Rectangle2D oldAnchor = shape.getAnchor();
                Rectangle2D rect = new java.awt.geom.Rectangle2D.Double(oldAnchorToNew(oldAnchor.getX()), oldAnchorToNew(oldAnchor.getY()),
                        oldAnchorToNew(oldAnchor.getWidth()), oldAnchorToNew(oldAnchor.getHeight()));
                wb.createBarChart(ppt, slide,rect, consoEuro, "€");
            }
            if(nomBatimentConso != null && shape.getShapeName().equals("kwhmElec") && meilleurAnne != null && !meilleurAnne.isEmpty() && releve.surfaceTotaleChauffe != 0){
                textShapeRatioWatt = (XSLFTextShape) shape;
            }
            if(nomBatimentConso != null && shape.getShapeName().equals("euromElec") && meilleurAnne != null && !meilleurAnne.isEmpty() && releve.surfaceTotaleChauffe != 0){
                textShapeRatioEuro = (XSLFTextShape) shape;
            }
            if(shape.getShapeName().equals("ellipseRatioWatt")){
                ellipseRatioWatt = (XSLFAutoShape) shape;
            }
            if(shape.getShapeName().equals("ellipseRatioEuro")){
                ellipseRatioEuro = (XSLFAutoShape) shape;
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

        if(nomBatimentConso != null && !nomBatimentConso.isEmpty() && releve.surfaceTotaleChauffe > 0){
            XSLFTextShape lastTextShape = null;
            XSLFAutoShape lastEllipse = null;
            int anne = Integer.parseInt(meilleurAnne);
            for (int i = 0; i < consoWatt.size(); i++) {
                if(consoWatt.get(i).anner == anne){
                    for(Map.Entry<Energie, Double> energie : consoWatt.get(i).energies.entrySet()){
                        if(energie.getValue() > 0){
                            double kwhEnergie = energie.getValue() / releve.surfaceTotaleChauffe;
                            if(lastEllipse == null){
                                textShapeRatioWatt.getTextBody().setText(String.format("%.1f", kwhEnergie) + " kwh/m²");
                                ellipseRatioWatt.setFillColor(energie.getKey().color);
                                lastEllipse = ellipseRatioWatt;
                                lastTextShape = textShapeRatioWatt;
                            }else{
                                //copy text shape
                                XSLFAutoShape textShape = slide.createAutoShape();
                                textShape.setAnchor(new Rectangle2D.Double(
                                        lastTextShape.getAnchor().getX(),
                                        lastTextShape.getAnchor().getY() + lastTextShape.getAnchor().getHeight(),
                                        lastTextShape.getAnchor().getWidth(),
                                        lastTextShape.getAnchor().getHeight()
                                ));
                                XSLFTextRun textRun = textShape.addNewTextParagraph().addNewTextRun();
                                textRun.setText(String.format("%.1f", kwhEnergie) + " kwh/m²");
                                textRun.setFontSize(14.0);
                                lastTextShape = textShape;

                                lastEllipse =   createCircle(slide, new Rectangle2D.Double(
                                        lastEllipse.getAnchor().getX(),
                                        lastEllipse.getAnchor().getY() + lastEllipse.getAnchor().getHeight(),
                                        lastEllipse.getAnchor().getWidth(),
                                        lastEllipse.getAnchor().getHeight()
                                ), energie.getKey().color);

                            }
                        }
                    }
                }
            }
            lastTextShape = null;
            lastEllipse = null;
            for (int i = 0; i < consoEuro.size(); i++) {
                if(consoEuro.get(i).anner == anne){
                    for(Map.Entry<Energie, Double> energie : consoEuro.get(i).energies.entrySet()){
                        if(energie.getValue() > 0){
                            double euroEnergie = energie.getValue() / releve.surfaceTotaleChauffe;
                            if(lastEllipse == null){
                                textShapeRatioEuro.getTextBody().setText(String.format("%.1f", euroEnergie) + " €/m²");
                                ellipseRatioEuro.setFillColor(energie.getKey().color);
                                lastEllipse = ellipseRatioEuro;
                                lastTextShape = textShapeRatioEuro;
                            }else{
                                //copy text shape
                                XSLFAutoShape textShape = slide.createAutoShape();
                                textShape.setAnchor(new Rectangle2D.Double(
                                        lastTextShape.getAnchor().getX(),
                                        lastTextShape.getAnchor().getY() + lastTextShape.getAnchor().getHeight(),
                                        lastTextShape.getAnchor().getWidth(),
                                        lastTextShape.getAnchor().getHeight()
                                ));
                                XSLFTextRun textRun = textShape.addNewTextParagraph().addNewTextRun();
                                textRun.setText(String.format("%.1f", euroEnergie) + " €/m²");
                                textRun.setFontSize(14.0);
                                lastTextShape = textShape;
                                lastEllipse =   createCircle(slide, new Rectangle2D.Double(
                                        lastEllipse.getAnchor().getX(),
                                        lastEllipse.getAnchor().getY() + lastEllipse.getAnchor().getHeight(),
                                        lastEllipse.getAnchor().getWidth(),
                                        lastEllipse.getAnchor().getHeight()
                                ), energie.getKey().color);
                            }
                        }
                    }
                }
            }
        }
    }

    private List<Anner> applyPourcentage(List<Anner> anners, float pourcentageBatiment) {
        for(Anner anner : anners){
            anner.total = anner.total * pourcentageBatiment / 100;
            anner.energies.replaceAll((e, v) -> anner.energies.get(e) * pourcentageBatiment / 100);
        }
        return anners;
    }

    private XSLFAutoShape createCircle(XSLFSlide slide, Rectangle2D rectangle2D, Color color){
        // Create a shape group

        // Add the circle to the shape group
        XSLFAutoShape shape = slide.createAutoShape();
        shape.setShapeType(ShapeType.ELLIPSE);
        shape.setAnchor(rectangle2D);

        // Fill the circle with color
        shape.setFillColor(color);
        return shape;
    }


    private void slideUsageEtOccupationDuBatiment(XMLSlideShow ppt, XSLFSlide slide) {
        Calendrier[] calendriers = releve.calendriers.values().toArray(new Calendrier[0]);
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
                        ((XSLFTextShape) shape).getTextBody().setText(" ");
                    }
                }
                if (name.equals("nomCalendrier")) {
                    ((XSLFTextShape) shape).getTextBody().setText("  ");

                }
                if (name.equals("nomZones")) {
                    ((XSLFTextShape) shape).getTextBody().setText("   ");
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

    private class ZoneAndZoneElement{
        public Zone zone;
        public ZoneElement zoneElement;
        public ZoneAndZoneElement(Zone zone, ZoneElement zoneElement){
            this.zone = zone;
            this.zoneElement = zoneElement;
        }
    }

    private void slideDescriptifEnveloppeThermique(XMLSlideShow ppt, XSLFSlide slide, Map<String, Zone> zones) {
        //copy slide in case of overflow
        XSLFSlide slideCopy = PowerpointExporterTools.duplicateSlide(ppt, slide);
        ppt.setSlideOrder(slideCopy, slide.getSlideNumber());


        // Get shapes of slide to complete
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

        List<ZoneAndZoneElement> zonesElements = getZonesElementsOrdered(zones);

        // iterate over zones and fill tables
        for (ZoneAndZoneElement zoneAndZoneElement : zonesElements) {
            Zone zone = zoneAndZoneElement.zone;
            ZoneElement zoneElement = zoneAndZoneElement.zoneElement;

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
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3), "Aucun isolant");
                    PowerpointExporterTools.addTextToCell(row.getCells().get(4), " ");
                }else{
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3), mur.niveauIsolation.isEmpty() ? " " : mur.niveauIsolation);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(4), "(" +
                            mur.typeIsolant +
                            (Float.isNaN(mur.epaisseurIsolant) ? "" :  " ; " + new DecimalFormat("0.#").format(mur.epaisseurIsolant) + " cm")
                            + ")");
                }

                PowerpointExporterTools.copyRowStyle(tableauMur.getRows().get(2), row);
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
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2), "Aucun isolant");
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3), " ");
                }else{
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2), toiture.niveauIsolation.isEmpty() ? " " : toiture.niveauIsolation);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3), "(" +
                            toiture.typeIsolant +
                            (Float.isNaN(toiture.epaisseurIsolant) ? "" :  " ; " + new DecimalFormat("0.#").format(toiture.epaisseurIsolant) + " cm")
                            + ")");
                }
                PowerpointExporterTools.copyRowStyle(tableauToiture.getRows().get(2), row);
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
                }else{
                    PowerpointExporterTools.addTextToCell(row.getCells().get(4), "Sans protection solaire");
                }
                PowerpointExporterTools.copyRowStyle(tableauMenuiseries.getRows().get(2), row);
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
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2), "Aucun isolant");
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3), " ");
                }else{
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2), sol.niveauIsolation.isEmpty() ? " " : sol.niveauIsolation);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3), "(" +
                            sol.typeIsolant +
                            (Float.isNaN(sol.epaisseurIsolant) ? "" :  " ; " + new DecimalFormat("0.#").format(sol.epaisseurIsolant) + " cm")
                            + ")");
                }
                PowerpointExporterTools.copyRowStyle(tableauSols.getRows().get(2), row);
                mergeCellsIfSameZone(zoneElementTableauSols, zone.nom, tableauSols);
                setColorOfTextZone(row.getCells().get(0));
            }


            if (row != null && zoneElement.aVerifier)
                PowerpointExporterTools.setAverfierStyleToRow(row);
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

    private List<ZoneAndZoneElement> getZonesElementsOrdered(Map<String, Zone> zones) {
        List<ZoneAndZoneElement> mur = new ArrayList<>();
        List<ZoneAndZoneElement> toiture = new ArrayList<>();
        List<ZoneAndZoneElement> menuiserie = new ArrayList<>();
        List<ZoneAndZoneElement> sol = new ArrayList<>();

        for(Zone zone : zones.values()){
            for(ZoneElement zoneElement : zone.getZoneElementsValues()){
                if(zoneElement instanceof Mur){
                    mur.add(new ZoneAndZoneElement(zone, zoneElement));
                }
                if(zoneElement instanceof Toiture){
                    toiture.add(new ZoneAndZoneElement(zone, zoneElement));
                }
                if(zoneElement instanceof Menuiserie){
                    menuiserie.add(new ZoneAndZoneElement(zone, zoneElement));
                }
                if(zoneElement instanceof Sol){
                    sol.add(new ZoneAndZoneElement(zone, zoneElement));
                }
            }
        }
        List<ZoneAndZoneElement> finalList = new ArrayList<>();
        finalList.addAll(mur);
        finalList.addAll(toiture);
        finalList.addAll(menuiserie);
        finalList.addAll(sol);
        return finalList;
    }


    private void slideDescriptifDesSystem(XMLSlideShow ppt, XSLFSlide slide, Map<String, Zone> eclairages, Ventilation[] ventilations, ECS[] ecss) {
        // copy slide in case of overflow
        XSLFSlide slideCopy = PowerpointExporterTools.duplicateSlide(ppt, slide);
        ppt.setSlideOrder(slideCopy, slide.getSlideNumber());

        // Get shapes of slide to complete
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




        // iterate over zones and fill tables
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
        Rectangle2D anchor = null;
        Rectangle2D anchorTextBox = null;
        for (XSLFShape shape : slide.getShapes()) {
            if (shape.getShapeName().equals("tableauPreconisations")) {
                anchorTextBox = shape.getAnchor();
            }
            if (shape.getShapeName().equals("photo")) {
                anchor = shape.getAnchor();
            }
        }

        if (anchor == null || anchorTextBox == null) {
            return;
        }
        List<String> images = new ArrayList<>();
        double bulletPointStartY = anchorTextBox.getY();
        for (String preconisation : releve.preconisations) {
            if (platformProvider.isStringAPath(preconisation)) {
                images.add(preconisation);
                continue;
            }

            XSLFTextBox textBox = slide.createTextBox();
            textBox.setAnchor(new java.awt.Rectangle((int)anchorTextBox.getX(), (int)bulletPointStartY, (int) anchorTextBox.getWidth(), 75));
            XSLFTextParagraph p = textBox.addNewTextParagraph();
            p.setBullet(true);
            XSLFTextRun r = p.addNewTextRun();
            r.setText(preconisation);
            bulletPointStartY += 75; // Adjust bullet point vertical spacing here
        }

        addImagesToSlide(ppt, slide, images, anchor);
    }

    private void cleanUp(XMLSlideShow ppt) {
        List<String> shapeNamesToRemove = List.of(
                "ZoneTexte Couleurs pour zones"
        );
        XSLFSlide slide = ppt.getSlides().get(0);
        List<XSLFShape> shapesToRemove = new ArrayList<>();
        for(XSLFShape shape : new ArrayList<>(slide.getShapes())){
            if(shapeNamesToRemove.contains(shape.getShapeName())){
                shapesToRemove.add(shape);
            }
            if(shape.getShapeName().startsWith("colorZone")){
                slide.removeShape(shape);
            }
        }
        for(XSLFShape shape : shapesToRemove){
            slide.removeShape(shape);
        }

        if(!ppt.getCTPresentation().isSetDefaultTextStyle()){
            ppt.getCTPresentation().addNewDefaultTextStyle();
        }
        if(!ppt.getCTPresentation().getDefaultTextStyle().isSetDefPPr()){
            ppt.getCTPresentation().getDefaultTextStyle().addNewDefPPr();
        }
        if(!ppt.getCTPresentation().getDefaultTextStyle().getDefPPr().isSetDefRPr()){
            ppt.getCTPresentation().getDefaultTextStyle().getDefPPr().addNewDefRPr();
        }
        ppt.getCTPresentation().getDefaultTextStyle().getDefPPr().getDefRPr().setLang("fr-FR");
        // set all text shape locale to fr-FR
        for (XSLFSlide s : ppt.getSlides()) {
            for (XSLFShape shape : s.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    for(XSLFTextParagraph paragraph : textShape.getTextParagraphs()){
                        if(!paragraph.getXmlObject().isSetEndParaRPr()){
                            paragraph.getXmlObject().addNewEndParaRPr();
                        }
                        paragraph.getXmlObject().getEndParaRPr().setLang("fr-FR");
                    }
                }
            }
        }
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

            futures[index] = executor.submit(() -> platformProvider.getImagesByteFromPath(imagePath, quality));
            index++;
        }


        for (int i = 0; i < numImages; i++) {
            // Calculate the position of this image in the grid
            int row = i / numCols;
            int col = i % numCols;
            double x = anchor.getX() + cellWidth * col;
            double y = anchor.getY() + cellHeight * row;


            byte[] pictureBytes;
            try {
                pictureBytes = futures[i].get();
            } catch (ExecutionException | InterruptedException e) {
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
