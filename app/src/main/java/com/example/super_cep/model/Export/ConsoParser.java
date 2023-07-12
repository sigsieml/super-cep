package com.example.super_cep.model.Export;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConsoParser {


    private XSSFWorkbook workbook;


    public ConsoParser(InputStream inputStream) {
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<String> getBatiments(){
        XSSFSheet sheet = workbook.getSheetAt(0);
        List<String> batiments = new ArrayList<>();
        //Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext())
        {
            Row row = rowIterator.next();

            Cell cell = row.getCell(5);
            if(!batiments.contains(cell.getStringCellValue())){
                batiments.add(cell.getStringCellValue());
            }
        }
        return batiments;
    }

    public List<Anner> getConsoWatt(XSSFSheet sheet, String nomBatiment, List<String> annees){

        List<Anner> anners = new ArrayList<>();
        //Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext())
        {
            Row row = rowIterator.next();

            Cell cell = row.getCell(5);
            if(cell.getStringCellValue().equals(nomBatiment)){
                Cell cellAnner = row.getCell(15);
                if(cellAnner.getCellType() != CellType.NUMERIC || !annees.contains(String.valueOf((int)cellAnner.getNumericCellValue()))) continue;
                anners.add(new Anner((int)cellAnner.getNumericCellValue(),
                        row.getCell(27) != null ? row.getCell(27).getNumericCellValue() : 0,
                       row.getCell(32) != null ? row.getCell(32).getNumericCellValue() : 0,
                        row.getCell(37) != null ? row.getCell(37).getNumericCellValue() : 0
                ));
            }
        }

        return anners;
    }

    public List<Anner> getConsoWatt(String nomBatiment, List<String> annees){
        XSSFSheet sheet = workbook.getSheetAt(0);
        return getConsoWatt(sheet, nomBatiment, annees);
    }

    public List<Anner> getConsoEuro(XSSFSheet sheet, String nomBatiment, List<String> annees){
        List<Anner> anners = new ArrayList<>();
        //Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext())
        {
            Row row = rowIterator.next();

            Cell cell = row.getCell(5);
            if(cell.getStringCellValue().equals(nomBatiment)){
                Cell cellAnner = row.getCell(15);
                if(cellAnner.getCellType() != CellType.NUMERIC || !annees.contains(String.valueOf((int)cellAnner.getNumericCellValue()))) continue;
                anners.add(new Anner((int)cellAnner.getNumericCellValue(),
                        row.getCell(30) != null ? row.getCell(30).getNumericCellValue() : 0,
                        row.getCell(35) != null ? row.getCell(35).getNumericCellValue() : 0,
                        row.getCell(40) != null ? row.getCell(40).getNumericCellValue() : 0
                ));
            }
        }
        return anners;
    }
    public List<Anner> getConsoEuro(String nomBatiment, List<String> annees){
        XSSFSheet sheet = workbook.getSheetAt(0);
        return getConsoEuro(sheet, nomBatiment, annees);
    }

    public List<String> getAnneOfBatiment(String value) {
        XSSFSheet sheet = workbook.getSheetAt(0);
        List<String> anners = new ArrayList<>();
        //Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext())
        {
            Row row = rowIterator.next();

            Cell cell = row.getCell(5);
            if(cell.getStringCellValue().equals(value)){
                Cell cellAnner = row.getCell(15);
                if(cellAnner != null && cellAnner.getCellType() == CellType.NUMERIC)
                    anners.add(String.valueOf((int)cellAnner.getNumericCellValue()));
            }
        }
        return anners;
    }

}
