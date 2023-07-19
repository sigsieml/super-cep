package com.example.super_cep;

import static org.junit.Assert.assertTrue;

import com.example.super_cep.controller.Conso.ConsoParser;
import com.example.super_cep.model.Export.JsonReleveManager;
import com.example.super_cep.model.Export.PowerpointException;
import com.example.super_cep.model.Export.PowerpointExporter;
import com.example.super_cep.model.Releve.Releve;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CreateSlideShowReleveTest {

    public static final String PATH_RELEVE = "super bâtiment.json";
    public static final String PATH_GROS_RELEVE = "super bâtiment beaucoup de murs.json";
    public static final String PATH_POWERPOINT = "powerpointvierge.pptx";
    @Test
    public void testOpenFile(){
        try {
            // On ouvre le fichier
            InputStream is = getClass().getClassLoader().getResourceAsStream(PATH_POWERPOINT);
            // On vérifie que le fichier existe
            if (is == null) throw new AssertionError("Fichier non trouvé");
            is.close();

            InputStream is2 = getClass().getClassLoader().getResourceAsStream(PATH_RELEVE);
            // On vérifie que le fichier existe
            if (is2 == null) throw new AssertionError("Fichier non trouvé");
            is2.close();
        } catch (IOException e) {
            assertTrue(false);
        }
    }


    @Test
    public void createSlideShowReleveTest(){
        Releve releve = getReleve(PATH_RELEVE);
        // open file in assets
        PowerpointExporter exporter = new PowerpointExporter(new pcProvider(), new ConsoParser(getClass().getClassLoader().getResourceAsStream("consomationbatiment.xlsx")));
        InputStream is = getClass().getClassLoader().getResourceAsStream(PATH_POWERPOINT);
        try {
            exporter.export(is, new FileOutputStream("test.pptx").getFD(), releve, "TEST", List.of("2022"), "2022", 100);

            // display in console the path of the file of test.pptx to open it
            System.out.println(new File("test.pptx").getAbsolutePath());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (PowerpointException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createSlideShowGrosReleveTest(){
        Releve releve = getReleve(PATH_GROS_RELEVE);
        // open file in assets
        PowerpointExporter exporter = new PowerpointExporter(new pcProvider(), new ConsoParser(getClass().getClassLoader().getResourceAsStream("consomationbatiment.xlsx")));
        InputStream is = getClass().getClassLoader().getResourceAsStream(PATH_POWERPOINT);
        try {
            exporter.export(is, new FileOutputStream("test.pptx").getFD(), releve, "TEST", List.of("2022"), "2022", 100);

            // display in console the path of the file of test.pptx to open it
            System.out.println(new File("test.pptx").getAbsolutePath());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (PowerpointException e) {
            throw new RuntimeException(e);
        }
    }

    private Releve getReleve(String path) {
        Releve releve;
        //get text from file


        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(path);
            String text = new String(is.readAllBytes());
            releve = JsonReleveManager.deserialize(text);
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return releve;
    }


}
