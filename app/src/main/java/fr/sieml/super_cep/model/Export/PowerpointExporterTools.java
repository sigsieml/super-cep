package fr.sieml.super_cep.model.Export;


import org.apache.harmony.luni.util.NotImplementedException;
import org.apache.poi.sl.draw.DrawTableShape;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;
/**
 * Classe d'utilitaires pour exporter des données vers PowerPoint.
 * <p>
 *     Cette classe contient des méthodes utilitaires pour exporter des données vers PowerPoint.
 *     Elle est utilisée par {@link PowerpointExporter}.
 *     Elle contient des méthodes pour exporter des tableaux, des images, des textes, etc.
 *     Elle contient aussi des méthodes pour copier le style d'un élément vers un autre.
 * </p>
 */
public class PowerpointExporterTools {

    /**
     * Copie le style d'un bloc de texte source dans un bloc de texte cible.
     *
     * @param sourceRun Le bloc de texte source dont le style doit être copié.
     * @param targetRun Le bloc de texte cible où le style sera appliqué.
     */
    public static void copyTextRunStyle(XSLFTextRun sourceRun, XSLFTextRun targetRun) {
        // Copy font color
        targetRun.setFontColor(sourceRun.getFontColor());

        // Copy font size
        targetRun.setFontSize(sourceRun.getFontSize());

        // Copy font family
        targetRun.setFontFamily(sourceRun.getFontFamily());

        // Copy font bold style
        targetRun.setBold(sourceRun.isBold());
    }
    /**
     * Copie le style d'une cellule source dans une cellule cible.
     *
     * @param sourceCell La cellule source dont le style doit être copié.
     * @param targetCell La cellule cible où le style sera appliqué.
     */
    public static void copyCellStyle(XSLFTableCell sourceCell, XSLFTableCell targetCell) {

        // Copy fill color
        targetCell.setFillColor(sourceCell.getFillColor());

        // Copy text alignment
        for (int i = 0; i < targetCell.getTextParagraphs().size(); i++) {
            targetCell.getTextParagraphs().get(i).setTextAlign(sourceCell.getTextParagraphs().get(0).getTextAlign());
            for (int j = 0; j < targetCell.getTextParagraphs().get(i).getTextRuns().size(); j++) {
                copyTextRunStyle(sourceCell.getTextParagraphs().get(0).getTextRuns().get(0), targetCell.getTextParagraphs().get(i).getTextRuns().get(j));
            }
        }

    }
    /**
     * Copie le style d'une ligne source dans une ligne cible.
     *
     * @param sourceRow La ligne source dont le style doit être copié.
     * @param targetRow La ligne cible où le style sera appliqué.
     */
    public static void copyRowStyle(XSLFTableRow sourceRow, XSLFTableRow targetRow) {
        targetRow.setHeight(sourceRow.getHeight());
        // Assuming both rows have the same number of cells
        for (int i = 0; i < sourceRow.getCells().size(); i++) {
            copyCellStyle(sourceRow.getCells().get(i), targetRow.getCells().get(i));
        }
    }
    /**
     * Assure que le nombre de cellules dans la ligne cible correspond à celui de la ligne source.
     *
     * @param sourceRow La ligne source utilisée pour déterminer le nombre de cellules nécessaires.
     * @param targetRow La ligne cible où le nombre de cellules sera ajusté.
     */
    public static void copyNumberOfCells(XSLFTableRow sourceRow, XSLFTableRow targetRow) {
        // Assuming both rows have the same number of cells
        for (int i = 0; i < sourceRow.getCells().size(); i++) {
            if(targetRow.getCells().size() < sourceRow.getCells().size()){
                targetRow.addCell();
            }
        }
    }
    /**
     * Duplique une diapositive dans une présentation PowerPoint.
     *
     * @param ppt   La présentation où la diapositive sera dupliquée.
     * @param slide La diapositive à dupliquer.
     * @return La nouvelle diapositive créée.
     */
    public static XSLFSlide  duplicateSlide(XMLSlideShow ppt, XSLFSlide slide) {
        // Get the layout of the old slide
        XSLFSlideLayout layout = slide.getSlideLayout();

        // Create a new slide with the layout of the old slide
        XSLFSlide newSlide = ppt.createSlide(layout);
        newSlide.importContent(slide);
        return newSlide;
    }
    /**
     * Modifie la couleur de texte d'une cellule.
     *
     * @param cell  La cellule dont la couleur de texte doit être modifiée.
     * @param color La nouvelle couleur à appliquer au texte.
     */
    public static void setCellTextColor(XSLFTableCell cell, Color color) {
        for (XSLFTextParagraph paragraph : cell.getTextParagraphs()) {
            for (XSLFTextRun run : paragraph.getTextRuns()) {
                run.setFontColor(color);
            }
        }
    }
    /**
     * Ajoute une ligne de texte à une cellule.
     *
     * @param xslfTextParagraphs La cellule où ajouter une ligne.
     */
    public static void addLineToCell(XSLFTableCell xslfTextParagraphs) {
        XSLFTextRun textRun = xslfTextParagraphs.getTextParagraphs().get(0).getTextRuns().get(0);
        textRun.setText("\n" + textRun.getRawText());

    }
    /**
     * Ajoute du texte à une cellule.
     *
     * @param cell La cellule où ajouter le texte.
     * @param text Le texte à ajouter.
     */
    public static void addTextToCell(XSLFTableCell cell, String text) {
        cell.addNewTextParagraph().addNewTextRun().setText(text);
        cell.setLeftInset(0);
        cell.setRightInset(0);
        cell.setTopInset(0);
        cell.setBottomInset(0);
    }
    /**
     * Met à jour les ancres d'une cellule en fonction de la taille du texte.
     *
     * @param platformProvider Un fournisseur de plateforme.
     * @param tableau          Le tableau contenant la cellule à mettre à jour.
     */
    public static void updateCellAnchor(PlatformProvider platformProvider, XSLFTable tableau) {
        int rows = tableau.getNumberOfRows();
        int cols = tableau.getNumberOfColumns();

        double[] colWidths = new double[cols];
        double[] rowHeights = new double[rows];

        for (int row=0; row<rows; row++) {
            rowHeights[row] = tableau.getRowHeight(row);
        }
        for (int col=0; col<cols; col++) {
            colWidths[col] = tableau.getColumnWidth(col);
        }

        Rectangle2D tblAnc = tableau.getAnchor();

        double nextY = tblAnc.getY();
        double nextX = tblAnc.getX();

        // #1 pass - determine row heights, the height values might be too low or 0 ...
        for (int row=0; row<rows; row++) {
            double maxHeight = 0;
            for (int col=0; col<cols; col++) {
                XSLFTableCell tc = tableau.getCell(row, col);
                if (tc == null || tc.getGridSpan() != 1 || tc.getRowSpan() != 1) {
                    continue;
                }
                // need to set the anchor before height calculation
                tc.setAnchor(new Rectangle2D.Double(0,0,colWidths[col],0));
                int textHeight = platformProvider.getTextHeight(tc);
                maxHeight = Math.max(maxHeight, textHeight);
            }
            rowHeights[row] = Math.max(rowHeights[row],maxHeight);
        }

        // #2 pass - init properties
        for (int row=0; row<rows; row++) {
            nextX = tblAnc.getX();
            for (int col=0; col<cols; col++) {
                Rectangle2D bounds = new Rectangle2D.Double(nextX, nextY, colWidths[col], rowHeights[row]);
                XSLFTableCell tc = tableau.getCell(row, col);
                if (tc != null) {
                    tc.setAnchor(bounds);
                    nextX += colWidths[col]+DrawTableShape.borderSize;
                }
            }
            nextY += rowHeights[row]+DrawTableShape.borderSize;
        }

        // #3 pass - update merge info
        for (int row=0; row<rows; row++) {
            for (int col=0; col<cols; col++) {
                XSLFTableCell tc = tableau.getCell(row, col);
                if (tc == null) {
                    continue;
                }
                Rectangle2D mergedBounds = tc.getAnchor();
                for (int col2=col+1; col2<col+tc.getGridSpan(); col2++) {
                    assert(col2 < cols);
                    XSLFTableCell tc2 = tableau.getCell(row, col2);
                    if (tc2.getGridSpan() != 1 || tc2.getRowSpan() != 1) {
                        System.out.println("Invalid table span - rendering result is probably wrong");
                    }
                    mergedBounds.add(tc2.getAnchor());
                }
                for (int row2=row+1; row2<row+tc.getRowSpan(); row2++) {
                    assert(row2 < rows);
                    XSLFTableCell tc2 = tableau.getCell(row2, col);
                    if (tc2.getGridSpan() != 1 || tc2.getRowSpan() != 1) {
                        System.out.println("invalid table span - rendering result is probably wrong");
                    }
                    mergedBounds.add(tc2.getAnchor());
                }
                tc.setAnchor(mergedBounds);
            }
        }

        tableau.setAnchor(new Rectangle2D.Double(tblAnc.getX(),tblAnc.getY(),
                nextX-tblAnc.getX(),
                nextY-tblAnc.getY()));

    }


    /**
     * Remplace le texte dans un objet de forme texte.
     *
     * @param remplacements Un dictionnaire avec les remplacements à effectuer.
     * @param shape         La forme texte où effectuer les remplacements.
     */
    public static void replaceTextInTextShape(Map<String, String> remplacements, XSLFTextShape shape) {
        if(!remplacements.containsKey(shape.getShapeName()))
            return;


        double widthText = shape.getTextParagraphs().get(0).getTextRuns().get(0).getFontSize() * 0.5 * remplacements.get(shape.getShapeName()).length();

        double actualHeight = shape.getAnchor().getHeight();
        double actualWidth = shape.getAnchor().getWidth();

        int ratioWidth = (int)Math.ceil(widthText / actualWidth) ;

        Rectangle rectangle = new Rectangle((int)shape.getAnchor().getX(), (int)shape.getAnchor().getY(),
                (int) actualWidth, (int) (actualHeight * ratioWidth));
        shape.setAnchor(rectangle);
        shape.getTextBody().setText(remplacements.get(shape.getShapeName()));

    }

    /**
     * Applique un style "À vérifier" à une ligne.
     *
     * @param row La ligne où appliquer le style.
     */
    public static void setAverfierStyleToRow(XSLFTableRow row){
        for(XSLFTableCell cell : row.getCells()){
            for(XSLFTextParagraph paragraph : cell.getTextParagraphs()){
                for(XSLFTextRun run : paragraph.getTextRuns()){
                    run.setItalic(true);
                    if(run.getFontColor() instanceof PaintStyle.SolidPaint){
                        PaintStyle.SolidPaint solidPaint = (PaintStyle.SolidPaint) run.getFontColor();
                        float[] rbg = new float[4];
                        solidPaint.getSolidColor().getColor().getRGBColorComponents(rbg);
                        Color color = new Color(rbg[0], rbg[1], rbg[2], 0.8f);
                        run.setFontColor(color);
                    }
                }
            }
        }
    }
    /**
     * Détermine le type d'une image à partir de ses premiers octets.
     *
     * @param buffer Les premiers octets de l'image.
     * @return Le type de l'image.
     */
    public static  PictureData.PictureType getPictureTypeFromBytes(byte[] buffer) {
        if (buffer.length >= 4 && buffer[0] == (byte) 0x89 && buffer[1] == (byte) 0x50 && buffer[2] == (byte) 0x4E && buffer[3] == (byte) 0x47) {
            return PictureData.PictureType.PNG;
        } else if (buffer.length >= 2 && buffer[0] == (byte) 0xff && buffer[1] == (byte) 0xd8) {
            return PictureData.PictureType.JPEG;
        } else if (buffer.length >= 2 && buffer[0] == (byte) 0x42 && buffer[1] == (byte) 0x4d) {
            return PictureData.PictureType.BMP;
        } else if (buffer.length >= 3 && buffer[0] == (byte) 0x47 && buffer[1] == (byte) 0x49 && buffer[2] == (byte) 0x46) {
            return PictureData.PictureType.GIF;
        } else if (buffer.length >= 12 && buffer[0] == (byte) 0x52 && buffer[1] == (byte) 0x49 && buffer[2] == (byte) 0x46 && buffer[3] == (byte) 0x46
                && buffer[8] == (byte) 0x57 && buffer[9] == (byte) 0x45 && buffer[10] == (byte) 0x42 && buffer[11] == (byte) 0x50) {
            return PictureData.PictureType.JPEG;
        } else {
            throw new NotImplementedException("Format non supporté");
        }
    }
    /**
     * Met à jour les ancres d'un tableau dans une diapositive.
     *
     * @param platformProvider Un fournisseur de plateforme.
     * @param slide            La diapositive contenant le tableau.
     * @param tables           Le tableau à mettre à jour.
     * @return Renvoie false si le tableau dépasse de la diapositive.
     */
    public static boolean updateTableauAnchor(PlatformProvider platformProvider, XSLFSlide slide,  XSLFTable[] tables) {
        return updateTableauAnchor(platformProvider, tables, slide.getSlideShow().getPageSize().getHeight());
    }

    /**
     * Met à jour les ancres d'un tableau en fonction de la hauteur maximale spécifiée.
     *
     * @param platformProvider Un fournisseur de plateforme.
     * @param tables           Le tableau à mettre à jour.
     * @param maxHeight        La hauteur maximale pour les ancres.
     * @return Renvoie false si le tableau dépasse de la diapositive.
     */
    public static boolean updateTableauAnchor(PlatformProvider platformProvider, XSLFTable[] tables, double maxHeight) {
        for (XSLFTable table : tables) {
            PowerpointExporterTools.updateCellAnchor(platformProvider, table);
        }

        XSLFTable firstTableau = tables[0];
        for (int i = 1; i < tables.length; i++) {
            XSLFTable tableau = tables[i];
            tableau.setAnchor(new Rectangle2D.Double(firstTableau.getAnchor().getX(),
                    firstTableau.getAnchor().getY() + firstTableau.getAnchor().getHeight(),
                    tableau.getAnchor().getWidth(),
                    tableau.getAnchor().getHeight())
            );
            if(tableau.getAnchor().getY() + tableau.getAnchor().getHeight() > maxHeight){
                return false;
            }
            firstTableau = tableau;

        }
        return true;
    }


}
