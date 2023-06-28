package com.example.super_cep.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.super_cep.model.Export.JsonReleveManager;
import com.example.super_cep.model.Releve.Releve;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ReleveSaver {


    private static final String RELEVE_FOLDER = "releves";

    private Context context;

    public ReleveSaver(Context context) {
        this.context = context;
    }

    public  void saveReleve(Releve releve) throws IllegalArgumentException{
        if(releve.nomBatiment == null || releve.nomBatiment.isEmpty())
            throw new IllegalArgumentException("Le nom du batiment ne peut pas être vide");

        String releveJson = JsonReleveManager.serialize(releve);

        // verify folder exists
        File directory = context.getDir(RELEVE_FOLDER, Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        //open file in folder
        File file = new File(directory, releve.nomBatiment + ".json");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(releveJson.getBytes());
        } catch (IOException e) {
            Log.e("ReleveSaver", "saveReleve: " + e.getMessage() );
            Toast.makeText(context, "Erreur lors de la sauvegarde du relevé", Toast.LENGTH_SHORT).show();
        }
    }

    public String[] getAllReleve(){
        String[] releves =  context.getDir(RELEVE_FOLDER, Context.MODE_PRIVATE).list();
        //filter only json file
        List<String> releveList = new java.util.ArrayList<>(releves.length);
        for (String releve : releves) {
            if(releve.endsWith(".json"))
                releveList.add(releve);
        }
        return releveList.toArray(new String[0]);
    }

    public void deleteReleve(String relevePath) {
        File file = new File(context.getDir(RELEVE_FOLDER, Context.MODE_PRIVATE), relevePath);
        file.delete();
    }

    public Releve readReleve(String relevePath){
        File releveFile = new File(context.getDir(RELEVE_FOLDER, Context.MODE_PRIVATE), relevePath);
        Releve releve;
        try {
            byte[] bytes = Files.readAllBytes(releveFile.toPath());
            String json = new String(bytes);
            releve =  JsonReleveManager.deserialize(json);
            if(releve == null)
                throw new Exception("Impossible de récupérer le relevé");
        } catch (Exception e) {
            Log.e("ReleveActivity", "setupReleve: ", e);
            Toast.makeText(context, "Erreur lors de la lecture du fichier", Toast.LENGTH_SHORT).show();
            releve = new Releve();
        }
        return releve;
    }
}
