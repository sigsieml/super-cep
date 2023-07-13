package com.example.super_cep.controller.Conso;

import com.example.super_cep.controller.Conso.Anner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
                Map<Energie, Double> energies = new HashMap<>(12);
                energies.put(Energie.ELECTRICITE, row.getCell(27) != null ? row.getCell(27).getNumericCellValue() : 0);
                energies.put(Energie.GAZ_NATUREL, row.getCell(32) != null ? row.getCell(32).getNumericCellValue() : 0);
                energies.put(Energie.FIOUL, row.getCell(37) != null ? row.getCell(37).getNumericCellValue() : 0);
                energies.put(Energie.PROPANE, row.getCell(42) != null ? row.getCell(42).getNumericCellValue() : 0);
                energies.put(Energie.BUTANE, row.getCell(48) != null ? row.getCell(48).getNumericCellValue() : 0);
                energies.put(Energie.BOIS_GRANULES, row.getCell(53) != null ? row.getCell(53).getNumericCellValue() : 0);
                energies.put(Energie.BOIS_PLAQUETTES, row.getCell(58) != null ? row.getCell(58).getNumericCellValue() : 0);
                energies.put(Energie.BOIS_BUCHES, row.getCell(63) != null ? row.getCell(63).getNumericCellValue() : 0);
                energies.put(Energie.RESEAU_DE_CHALEUR, row.getCell(68) != null ? row.getCell(68).getNumericCellValue() : 0);
                energies.put(Energie.RESEAU_DE_FROID, row.getCell(73) != null ? row.getCell(73).getNumericCellValue() : 0);
                energies.put(Energie.ESSENCE, row.getCell(78) != null ? row.getCell(78).getNumericCellValue() : 0);
                energies.put(Energie.GAZOLE, row.getCell(83) != null ? row.getCell(83).getNumericCellValue() : 0);
                anners.add(new Anner((int)cellAnner.getNumericCellValue(), energies));
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
                Map<Energie, Double> energies = new HashMap<>(12);
                energies.put(Energie.ELECTRICITE, row.getCell(30) != null ? row.getCell(30).getNumericCellValue() : 0);
                energies.put(Energie.GAZ_NATUREL, row.getCell(35) != null ? row.getCell(35).getNumericCellValue() : 0);
                energies.put(Energie.FIOUL, row.getCell(40) != null ? row.getCell(40).getNumericCellValue() : 0);
                energies.put(Energie.PROPANE, row.getCell(45) != null ? row.getCell(45).getNumericCellValue() : 0);
                energies.put(Energie.BUTANE, row.getCell(49) != null ? row.getCell(49).getNumericCellValue() : 0);
                energies.put(Energie.BOIS_GRANULES, row.getCell(55) != null ? row.getCell(55).getNumericCellValue() : 0);
                energies.put(Energie.BOIS_PLAQUETTES, row.getCell(60) != null ? row.getCell(60).getNumericCellValue() : 0);
                energies.put(Energie.BOIS_BUCHES, row.getCell(65) != null ? row.getCell(65).getNumericCellValue() : 0);
                energies.put(Energie.RESEAU_DE_CHALEUR, row.getCell(70) != null ? row.getCell(70).getNumericCellValue() : 0);
                energies.put(Energie.RESEAU_DE_FROID, row.getCell(75) != null ? row.getCell(75).getNumericCellValue() : 0);
                energies.put(Energie.ESSENCE, row.getCell(80) != null ? row.getCell(80).getNumericCellValue() : 0);
                energies.put(Energie.GAZOLE, row.getCell(85) != null ? row.getCell(85).getNumericCellValue() : 0);
                anners.add(new Anner((int)cellAnner.getNumericCellValue(), energies));
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
