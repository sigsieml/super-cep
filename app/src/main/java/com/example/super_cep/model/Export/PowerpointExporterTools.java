package com.example.super_cep.model.Export;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSheet;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import java.awt.Color;

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
}
