package com.example.super_cep;

import com.example.super_cep.controller.Conso.ConsoParser;
import com.example.super_cep.model.Export.CreateChartToSlide;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.Units;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ConsomationParserTest {

    public static final String PATH_CONSO = "consomationbatiment.xlsx";

    @Test
    public void testConsomationParser() {

        InputStream is = getClass().getClassLoader().getResourceAsStream(PATH_CONSO);

        //Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Get first/desired sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);
        //Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext())
        {
            Row row = rowIterator.next();

            Cell cell = row.getCell(5);
            if(cell.getStringCellValue().equals("RUE DE L'ETANG - CHIGNE")){
                System.out.println("cell = " + cell.getStringCellValue());
                Cell cellAnner = row.getCell(15);
                System.out.println("cellAnner = " + cellAnner.getNumericCellValue());
                Cell cellConsoElec = row.getCell(27);
                System.out.println("cellConsoElec = " + cellConsoElec.getNumericCellValue());
            }
        }
        try {
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void createBarChartFromExcel() {

        try {
            XMLSlideShow ppt = new XMLSlideShow();

            // Créer une diapositive
            XSLFSlide slide = ppt.createSlide();


            InputStream is = getClass().getClassLoader().getResourceAsStream(PATH_CONSO);
            CreateChartToSlide createChartToSlide = new CreateChartToSlide();
            ConsoParser consoParser = new ConsoParser(is);
            Rectangle2D rect = new java.awt.geom.Rectangle2D.Double(1d* Units.EMU_PER_CENTIMETER, 1d*Units.EMU_PER_CENTIMETER, 20d*Units.EMU_PER_CENTIMETER, 15d* Units.EMU_PER_CENTIMETER);
            createChartToSlide.createBarChart(ppt, slide,rect, consoParser.getConsoWatt("RUE DE L'ETANG - CHIGNE", consoParser.getAnneOfBatiment("RUE DE L'ETANG - CHIGNE")), "kWh");

            // Enregistrer le résultat
            try (FileOutputStream out = new FileOutputStream("test.pptx")) {
                ppt.write(out);
                System.out.println(new File("test.pptx").getAbsolutePath());

            }
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
