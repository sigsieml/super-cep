package com.example.super_cep.model.Export;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.super_cep.model.Enveloppe.Mur;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;
import com.example.super_cep.model.Releve;
import com.example.super_cep.model.Remarque;

import org.apache.poi.common.usermodel.fonts.FontGroup;
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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
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
            slideBatiment(slides.get(0));
            slideEnergieEtConsomations(slides.get(1));
            slideUsageEtOccupationDuBatiment(slides.get(2));
            slideDescriptifEnveloppeThermique(ppt,slides.get(3));

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
                replaceTextInTextShape((XSLFTextShape) shape);
            }
        }

    }

    private void slideEnergieEtConsomations(XSLFSlide slide) {

    }


    private void slideUsageEtOccupationDuBatiment(XSLFSlide slide) {
    }



    private void slideDescriptifEnveloppeThermique(XMLSlideShow ppt, XSLFSlide slide) {
        if(context == null) return;
        for (Zone zone : releve.getZonesValues()){
            for (ZoneElement zoneElement : zone.getZoneElementsValues()){
                if(zoneElement instanceof Mur){
                    Mur mur = (Mur) zoneElement;
                    for (Uri uri : mur.uriImages){

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                            // Convertir le bitmap en array d'octets
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] imageBytes = stream.toByteArray();

                            // Ajouter les octets de l'image en tant que image à la présentation
                            XSLFPictureData pictureData = ppt.addPicture(imageBytes, XSLFPictureData.PictureType.PNG);

                            // Créer une forme d'image sur le slide
                            XSLFPictureShape pictureShape = slide.createPicture(pictureData);

                            // Définir la position et la taille de l'image (ajuster selon les besoins)
                            pictureShape.setAnchor(new java.awt.Rectangle(100, 100, 300, 300));

                        } catch (FileNotFoundException e) {

                        } catch (IOException e) {
                        }


                    }
                }

            }
        }
    }



    private String formateDate(Calendar calendar){
        String pattern = "dd MMMMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("fr", "FR"));
        return simpleDateFormat.format(calendar.getTime());
    }

    private void replaceTextInTextShape(XSLFTextShape shape) {
        if(!remplacements.containsKey(shape.getShapeName()))
            return;

        XSLFTextRun run = shape.getTextParagraphs().get(0).getTextRuns().get(0);
        String fontFamily = run.getFontFamily();
        double fontSize = run.getFontSize();
        boolean bold = run.isBold();
        boolean italic = run.isItalic();

        // Création d'une police Java avec les informations de police de la shape
        int style = (bold ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0);
        Font font = new Font(fontFamily, style, (int) fontSize);

        FontMetrics fm = img.getGraphics().getFontMetrics(font);
        int width = fm.stringWidth(remplacements.get(shape.getShapeName()));

        double actualHeight = shape.getAnchor().getHeight();
        double actualWidth = shape.getAnchor().getWidth();

        int ratioWidth = (int)Math.ceil(width / actualWidth) ;



        Rectangle rectangle = new Rectangle((int)shape.getAnchor().getX(), (int)shape.getAnchor().getY(),
                (int) actualWidth, (int) (actualHeight * ratioWidth));
        shape.setAnchor(rectangle);
        shape.getTextBody().setText(remplacements.get(shape.getShapeName()));
    }
}
