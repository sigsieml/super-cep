package com.example.super_cep.model.Export;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import org.apache.harmony.luni.util.NotImplementedException;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.draw.DrawTableShape;
import org.apache.poi.sl.draw.DrawTextShape;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSheet;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
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
import java.util.Map;

public class PowerpointExporterTools {


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

    public static void copyCellStyle(XSLFTableCell sourceCell, XSLFTableCell targetCell) {

        // Copy fill color
        targetCell.setFillColor(sourceCell.getFillColor());

        // Copy text alignment
        for (int i = 0; i < targetCell.getTextParagraphs().size(); i++) {
            targetCell.getTextParagraphs().get(i).setTextAlign(sourceCell.getTextParagraphs().get(0).getTextAlign());
            for (int j = 0; j < targetCell.getTextParagraphs().get(i).getTextRuns().size(); j++) {
                copyTextRunStyle(sourceCell.getTextParagraphs().get(0).getTextRuns().get(j), targetCell.getTextParagraphs().get(i).getTextRuns().get(j));
            }
        }

    }

    public static void copyRowStyle(XSLFTableRow sourceRow, XSLFTableRow targetRow) {
        targetRow.setHeight(sourceRow.getHeight());
        // Assuming both rows have the same number of cells
        for (int i = 0; i < sourceRow.getCells().size(); i++) {
            copyCellStyle(sourceRow.getCells().get(i), targetRow.getCells().get(i));
        }
    }

    public static void copyNumberOfCells(XSLFTableRow sourceRow, XSLFTableRow targetRow) {
        // Assuming both rows have the same number of cells
        for (int i = 0; i < sourceRow.getCells().size(); i++) {
            if(targetRow.getCells().size() < sourceRow.getCells().size()){
                targetRow.addCell();
            }
        }
    }

    public static XSLFSlide  duplicateSlide(XMLSlideShow ppt, XSLFSlide slide) {
        // Get the slide that you want to duplicate
        XSLFSlide oldSlide = slide;

        // Get the layout of the old slide
        XSLFSlideLayout layout = oldSlide.getSlideLayout();

        // Create a new slide with the layout of the old slide
        XSLFSlide newSlide = ppt.createSlide(layout);
        newSlide.importContent(slide);
        return newSlide;
    }

    public static void setCellTextColor(XSLFTableCell cell, Color color) {
        for (XSLFTextParagraph paragraph : cell.getTextParagraphs()) {
            for (XSLFTextRun run : paragraph.getTextRuns()) {
                run.setFontColor(color);
            }
        }
    }


    public static void setCellTextAlign(XSLFTableCell cell, TextParagraph.TextAlign center) {
        for(XSLFTextParagraph paragraph : cell.getTextParagraphs()) {
            paragraph.setTextAlign(center);
        }
    }

    public static void addLineToCell(XSLFTableCell xslfTextParagraphs) {
        XSLFTextRun textRun = xslfTextParagraphs.getTextParagraphs().get(0).getTextRuns().get(0);
        textRun.setText("\n" + textRun.getRawText());

    }

    public static void addTextToCell(XSLFTableCell cell, String text) {
        cell.addNewTextParagraph().addNewTextRun().setText(text);
        cell.setLeftInset(0);
        cell.setRightInset(0);
        cell.setTopInset(0);
        cell.setBottomInset(0);
    }

    public static void updateCellAnchor(XSLFTable tableau, float rowHeight ) {
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
        DrawFactory df = DrawFactory.getInstance(null);

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
//                DrawTextShape dts = df.getDrawable(tc);
                //maxHeight = Math.max(maxHeight, getTextHeight(tc));
                maxHeight = rowHeight;
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
                    nextX += colWidths[col]+ DrawTableShape.borderSize;
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
                        Log.w("apachePOI", "invalid table span - rendering result is probably wrong");
                    }
                    mergedBounds.add(tc2.getAnchor());
                }
                for (int row2=row+1; row2<row+tc.getRowSpan(); row2++) {
                    assert(row2 < rows);
                    XSLFTableCell tc2 = tableau.getCell(row2, col);
                    if (tc2.getGridSpan() != 1 || tc2.getRowSpan() != 1) {
                        Log.w("apachePOI", "invalid table span - rendering result is probably wrong");
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


    public static PictureData.PictureType getPictureTypeFromFileExtension(String path){
        String extension = path.substring(path.lastIndexOf(".") + 1);
        switch (extension){
            case "jpg":
                return PictureData.PictureType.JPEG;
            case "png":
                return PictureData.PictureType.PNG;
            case "gif":
                return PictureData.PictureType.GIF;
            case "bmp":
                return PictureData.PictureType.BMP;
            case "emf":
                return PictureData.PictureType.EMF;
            case "wmf":
                return PictureData.PictureType.WMF;
            case "pict":
                return PictureData.PictureType.PICT;
            case "dib":
                return PictureData.PictureType.DIB;
            case "tiff":
                return PictureData.PictureType.TIFF;
            case "eps":
                return PictureData.PictureType.EPS;
            case "wpg":
                return PictureData.PictureType.WPG;
            default:
                return PictureData.PictureType.JPEG;
        }
    }

    public static void setAverfierStyleToRow(XSLFTableRow row){
        for(XSLFTableCell cell : row.getCells()){
            for(XSLFTextParagraph paragraph : cell.getTextParagraphs()){
                for(XSLFTextRun run : paragraph.getTextRuns()){
                    run.setUnderlined(true);
                }
            }
        }
    }

    public static  PictureData.PictureType getPictureTypeFromBytes(byte[] buffer) {
            if (buffer[0] == (byte) 0x89 && buffer[1] == (byte) 0x50 && buffer[2] == (byte) 0x4E && buffer[3] == (byte) 0x47) {
                return PictureData.PictureType.PNG;
            } else if (buffer[0] == (byte) 0xff && buffer[1] == (byte) 0xd8) {
                return PictureData.PictureType.JPEG;
            } else if (buffer[0] == (byte) 0x42 && buffer[1] == (byte) 0x4d) {
                return PictureData.PictureType.BMP;

            } else if (buffer[0] == (byte) 0x47 && buffer[1] == (byte) 0x49 && buffer[2] == (byte) 0x46) {
                return PictureData.PictureType.GIF;
            }else{
                throw new NotImplementedException("Format non supporté");
            }
    }
}
