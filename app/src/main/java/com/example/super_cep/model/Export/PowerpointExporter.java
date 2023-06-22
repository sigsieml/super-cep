package com.example.super_cep.model.Export;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.super_cep.model.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.ApprovionnementEnergetique.ApprovisionnementEnergetiqueElectrique;
import com.example.super_cep.model.ApprovionnementEnergetique.ApprovisionnementEnergetiqueGaz;
import com.example.super_cep.model.Calendrier.Calendrier;
import com.example.super_cep.model.Enveloppe.Menuiserie;
import com.example.super_cep.model.Enveloppe.Mur;
import com.example.super_cep.model.Enveloppe.Sol;
import com.example.super_cep.model.Enveloppe.Toiture;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;
import com.example.super_cep.model.Releve;
import com.example.super_cep.model.Remarque;

import org.apache.poi.common.usermodel.fonts.FontGroup;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.ss.usermodel.Row;
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
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
    private Releve releve;
    private Map<String, String> remplacements;

    private Context context;
    private BufferedImage img;

    public PowerpointExporter(Context context) {
        this.context = context;
        img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    }

    public void export(InputStream powerpointVierge, FileDescriptor file, Releve releve) {
        this.releve = releve;
        setupReleve();
        try {
            InputStream is = powerpointVierge;
            XMLSlideShow ppt = new XMLSlideShow(is);

            List<XSLFSlide> slides = ppt.getSlides();
            XSLFSlide slideDescriptifEnveloppeThermique = slides.get(3);
            slideBatiment(slides.get(0));
            slideEnergieEtConsomations(slides.get(1));
            slideUsageEtOccupationDuBatiment(ppt, slides.get(2));
            slideDescriptifEnveloppeThermique(ppt,slideDescriptifEnveloppeThermique);


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
        if(releve.dateDeConstruction != null) remplacements.put("dateDeConstruction",  formateDate(releve.dateDeConstruction));
        if(releve.dateDeDerniereRenovation != null) remplacements.put("dateDeDerniereRenovation", formateDate(releve.dateDeDerniereRenovation));
        if(releve.surfaceTotaleChauffe != 0) remplacements.put("surfaceTotaleChauffe", releve.surfaceTotaleChauffe + "");
        if(releve.description != null) remplacements.put("description", releve.description);
        if(releve.adresse != null) remplacements.put("adresse", releve.adresse);


        //remarques :
        if(releve.remarques != null){
            for (Map.Entry<String, Remarque> entry : releve.remarques.entrySet()) {
                remplacements.put("remarque" + entry.getKey(), entry.getValue().description);
            }
        }
    }



    private void slideBatiment(XSLFSlide slide) {
        for (XSLFShape shape : slide) {
            if (shape instanceof XSLFTextShape) {
                PowerpointExporterTools.replaceTextInTextShape(remplacements,(XSLFTextShape) shape);
            }
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
                }
                table.removeRow(2);
            }
        }
    }






    private void slideUsageEtOccupationDuBatiment(XMLSlideShow ppt, XSLFSlide slide) {
        Calendrier[] calendriers = releve.getCalendriersValues();
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
                    StringBuilder builder = new StringBuilder("zones : ");
                    for (String zone : calendrier.zones) {
                        builder.append(zone).append(", ");
                    }
                    builder.delete(builder.length() - 2, builder.length());
                    textShape.getTextBody().setText(builder.toString());
                }
            }

        }
    }



    private void slideDescriptifEnveloppeThermique(XMLSlideShow ppt, XSLFSlide slide) {
        List<String> tablesNames = List.of("tableauMur", "tableauToiture", "tableauSols", "tableauMenuiseries");
        Map<String, XSLFTable> tables = new HashMap<>();
        for (XSLFShape shape : slide.getShapes()){

            if(tablesNames.contains(shape.getShapeName())){
                tables.put(shape.getShapeName(), (XSLFTable) shape);
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



        final Color[] colors = new Color[]{new Color(191, 143,0), new Color(31, 78,120), new Color(255, 43, 43)};
        for (Zone zone : releve.getZonesValues()){
            Color colorZoneName = colors[indexColorZone % colors.length];
            indexColorZone++;

            for (ZoneElement zoneElement : zone.getZoneElementsValues()){
                //get a random color based on the zone name :
                if(zoneElement instanceof Mur){
                    Mur mur = (Mur) zoneElement;
                    XSLFTableRow row = tableauMur.addRow();
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
                    Toiture toiture = (Toiture) zoneElement;
                    XSLFTableRow row = tableauToiture.addRow();
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
                    Menuiserie menuiserie = (Menuiserie) zoneElement;
                    XSLFTableRow row = tableauMenuiseries.addRow();
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
                    Sol sol = (Sol) zoneElement;
                    XSLFTableRow row = tableauSols.addRow();
                    PowerpointExporterTools.copyNumberOfCells(tableauSols.getRows().get(2), row);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(0), zone.nom);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(1),sol.typeSol);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(2),sol.typeIsolant);
                    PowerpointExporterTools.addTextToCell(row.getCells().get(3),sol.niveauIsolation);

                    PowerpointExporterTools.copyRowStyle(tableauSols.getRows().get(2), row);
                    PowerpointExporterTools.setCellTextColor(row.getCells().get(0), colorZoneName);

                    mergeCellsIfSameZone(zoneElementTableauSols, zone.nom, tableauSols);
                }

            }
        }

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
