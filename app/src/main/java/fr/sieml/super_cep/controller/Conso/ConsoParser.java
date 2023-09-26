package fr.sieml.super_cep.controller.Conso;

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
                energies.put(Energie.BUTANE, row.getCell(47) != null ? row.getCell(47).getNumericCellValue() : 0);
                energies.put(Energie.BOIS_GRANULES, row.getCell(52) != null ? row.getCell(52).getNumericCellValue() : 0);
                energies.put(Energie.BOIS_PLAQUETTES, row.getCell(57) != null ? row.getCell(57).getNumericCellValue() : 0);
                energies.put(Energie.BOIS_BUCHES, row.getCell(62) != null ? row.getCell(62).getNumericCellValue() : 0);
                energies.put(Energie.RESEAU_DE_CHALEUR, row.getCell(67) != null ? row.getCell(67).getNumericCellValue() : 0);
                energies.put(Energie.RESEAU_DE_FROID, row.getCell(72) != null ? row.getCell(72).getNumericCellValue() : 0);
                energies.put(Energie.ESSENCE, row.getCell(77) != null ? row.getCell(77).getNumericCellValue() : 0);
                energies.put(Energie.GAZOLE, row.getCell(82) != null ? row.getCell(82).getNumericCellValue() : 0);
                anners.add(new Anner(
                        (int)cellAnner.getNumericCellValue(),
                        row.getCell(22) != null ? row.getCell(22).getNumericCellValue() : 0, // conso total en watt
                        energies
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
                Map<Energie, Double> energies = new HashMap<>(12);
                energies.put(Energie.ELECTRICITE, row.getCell(29) != null ? row.getCell(29).getNumericCellValue() : 0);
                energies.put(Energie.GAZ_NATUREL, row.getCell(34) != null ? row.getCell(34).getNumericCellValue() : 0);
                energies.put(Energie.FIOUL, row.getCell(39) != null ? row.getCell(39).getNumericCellValue() : 0);
                energies.put(Energie.PROPANE, row.getCell(44) != null ? row.getCell(44).getNumericCellValue() : 0);
                energies.put(Energie.BUTANE, row.getCell(49) != null ? row.getCell(49).getNumericCellValue() : 0);
                energies.put(Energie.BOIS_GRANULES, row.getCell(54) != null ? row.getCell(54).getNumericCellValue() : 0);
                energies.put(Energie.BOIS_PLAQUETTES, row.getCell(59) != null ? row.getCell(59).getNumericCellValue() : 0);
                energies.put(Energie.BOIS_BUCHES, row.getCell(64) != null ? row.getCell(64).getNumericCellValue() : 0);
                energies.put(Energie.RESEAU_DE_CHALEUR, row.getCell(69) != null ? row.getCell(69).getNumericCellValue() : 0);
                energies.put(Energie.RESEAU_DE_FROID, row.getCell(74) != null ? row.getCell(74).getNumericCellValue() : 0);
                energies.put(Energie.ESSENCE, row.getCell(79) != null ? row.getCell(79).getNumericCellValue() : 0);
                energies.put(Energie.GAZOLE, row.getCell(84) != null ? row.getCell(84).getNumericCellValue() : 0);
                anners.add(new Anner(
                        (int)cellAnner.getNumericCellValue(),
                        row.getCell(24) != null ? row.getCell(24).getNumericCellValue() : 0, // conso total en euro
                        energies));
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
