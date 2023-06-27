package com.example.super_cep.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.super_cep.model.Export.JsonReleveManager;
import com.example.super_cep.model.Releve.Releve;

import java.io.FileOutputStream;
import java.io.IOException;

public class ReleveSaver {


    private Context context;

    public ReleveSaver(Context context) {
        this.context = context;
    }

    public  void saveReleve(Releve releve){
        String releveJson = JsonReleveManager.serialize(releve);

        try (FileOutputStream fos = context.openFileOutput(releve.nomBatiment + ".json", Context.MODE_PRIVATE)) {
            fos.write(releveJson.getBytes());

        } catch (IOException e) {
            Log.e("ReleveSaver", "saveReleve: " + e.getMessage() );
            Toast.makeText(context, "Erreur lors de la sauvegarde du relevé", Toast.LENGTH_SHORT).show();
        }
    }
}
