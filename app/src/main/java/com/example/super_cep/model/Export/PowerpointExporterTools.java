package com.example.super_cep.model.Export;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.TextParagraph;
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
            targetCell.getTextParagraphs().get(i).setTextAlign(sourceCell.getTextParagraphs().get(i).getTextAlign());

            for (int j = 0; j < targetCell.getTextParagraphs().get(i).getTextRuns().size(); j++) {
                copyTextRunStyle(sourceCell.getTextParagraphs().get(i).getTextRuns().get(j), targetCell.getTextParagraphs().get(i).getTextRuns().get(j));
            }
        }

    }

    public static void copyRowStyle(XSLFTableRow sourceRow, XSLFTableRow targetRow) {
        // Assuming both rows have the same number of cells
        for (int i = 0; i < sourceRow.getCells().size(); i++) {
            copyCellStyle(sourceRow.getCells().get(i), targetRow.getCells().get(i));
        }
    }

}
