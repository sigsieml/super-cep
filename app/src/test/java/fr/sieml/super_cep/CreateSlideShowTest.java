package fr.sieml.super_cep;

import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateSlideShowTest {
    @Test
    public void createSlideAndAddText() {
        XMLSlideShow slideShow = new XMLSlideShow();
        XSLFSlide slide =  slideShow.createSlide();
        XSLFTextBox textBox = slide.createTextBox();
        textBox.appendText("salut", true);
        System.out.println("textBox.getText() = " + textBox.getText());
        Assert.assertEquals(textBox.getText(), "salut");

    }

    @Test
    public void exportSlideShow(){
        try (XMLSlideShow ppt = new XMLSlideShow()) {
            // XSLFSlide#createSlide() with no arguments creates a blank slide
            /*XSLFSlide blankSlide =*/
            ppt.createSlide();


            XSLFSlideMaster master = ppt.getSlideMasters().get(0);

            XSLFSlideLayout layout1 = master.getLayout(SlideLayout.TITLE);
            XSLFSlide slide1 = ppt.createSlide(layout1);
            XSLFTextShape[] ph1 = slide1.getPlaceholders();
            XSLFTextShape titlePlaceholder1 = ph1[0];
            titlePlaceholder1.setText("This is a title");
            XSLFTextShape subtitlePlaceholder1 = ph1[1];
            subtitlePlaceholder1.setText("this is a subtitle");

            XSLFSlideLayout layout2 = master.getLayout(SlideLayout.TITLE_AND_CONTENT);
            XSLFSlide slide2 = ppt.createSlide(layout2);
            XSLFTextShape[] ph2 = slide2.getPlaceholders();
            XSLFTextShape titlePlaceholder2 = ph2[0];
            titlePlaceholder2.setText("This is a title");
            XSLFTextShape bodyPlaceholder = ph2[1];
            // we are going to add text by paragraphs. Clear the default placehoder text before that
            bodyPlaceholder.clearText();
            XSLFTextParagraph p1 = bodyPlaceholder.addNewTextParagraph();
            p1.setIndentLevel(0);
            p1.addNewTextRun().setText("Level1 text");
            XSLFTextParagraph p2 = bodyPlaceholder.addNewTextParagraph();
            p2.setIndentLevel(1);
            p2.addNewTextRun().setText("Level2 text");
            XSLFTextParagraph p3 = bodyPlaceholder.addNewTextParagraph();
            p3.setIndentLevel(2);
            p3.addNewTextRun().setText("Level3 text");

            try (FileOutputStream out = new FileOutputStream("slides.pptx")) {
                ppt.write(out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createSlideWithTable() {
        try {
            XMLSlideShow ppt = new XMLSlideShow();

            // Créer une diapositive
            XSLFSlide slide = ppt.createSlide();

            // Créer un tableau
            XSLFTable tbl = slide.createTable();
            tbl.setAnchor(new java.awt.Rectangle(50, 50, 450, 300));

            // Créer une ligne de titre
            XSLFTableRow headerRow = tbl.addRow();
            headerRow.addCell().setText("Nom");
            headerRow.addCell().setText("Age");

            // Ajouter des données d'exemples
            XSLFTableRow row1 = tbl.addRow();
            row1.addCell().setText("Alice");
            row1.addCell().setText("25");

            XSLFTableRow row2 = tbl.addRow();
            row2.addCell().setText("Bob");
            row2.addCell().setText("30");

            //set all border of all cells


            // Enregistrer le résultat
            try (FileOutputStream out = new FileOutputStream("test.pptx")) {
                ppt.write(out);
            }
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(true);

        }
    }

    @Test
    public void createSlideWithColoredTable() {
        try {
            XMLSlideShow ppt = new XMLSlideShow();

            // Créer une diapositive
            XSLFSlide slide = ppt.createSlide();

            // Créer un tableau
            XSLFTable tbl = slide.createTable();
            tbl.setAnchor(new java.awt.Rectangle(50, 50, 450, 300));

            // Créer une ligne de titre
            XSLFTableRow headerRow = tbl.addRow();
            headerRow.addCell().setText("Nom");
            headerRow.addCell().setText("Age");

            // Ajouter des données d'exemples
            XSLFTableRow row1 = tbl.addRow();
            XSLFTableCell cell1 = row1.addCell();
            cell1.setText("Alice");
            cell1.setFillColor(Color.RED);

            XSLFTableCell cell2 = row1.addCell();
            cell2.setText("25");
            cell2.setFillColor(Color.BLUE);

            XSLFTableRow row2 = tbl.addRow();
            XSLFTableCell cell3 = row2.addCell();
            cell3.setText("Bob");
            cell3.setFillColor(Color.RED);

            XSLFTableCell cell4 = row2.addCell();
            cell4.setText("30");
            cell4.setFillColor(Color.BLUE);

            // Enregistrer le résultat
            try (FileOutputStream out = new FileOutputStream("test.pptx")) {
                ppt.write(out);
            }

            Assert.assertTrue(true);

        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(true);

        }
    }


}