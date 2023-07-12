package com.example.super_cep.controller.Conso;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.super_cep.model.Export.ConsoParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConsoProvider {


    private static final String CONSO_FILE_NAME = "conso.xlsx";
    private Context context;
    private Fragment fragment;

    private ActivityResultLauncher<String[]> openFileLauncher;

    public ConsoProvider(Fragment fragment, ConsoProviderListener consoProviderListener) {
        this.context = fragment.getContext();
        this.fragment = fragment;

        openFileLauncher = fragment.registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if(uri == null) return;
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                saveDataConso(inputStream);
                inputStream.close();
                inputStream = context.getContentResolver().openInputStream(uri);
                consoProviderListener.onConsoParserChanged(new ConsoParser(inputStream));
                inputStream.close();
            } catch (IOException e) {
                Log.e("SettingsActivity", "onActivityResult: ", e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void loadNewConso(){
        openFileLauncher.launch(new String[]{"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
    }

    public ConsoParser getConsoParser(){
        // check if file exist
        try {
            ConsoParser consoParser;
            if(!context.getFileStreamPath(CONSO_FILE_NAME).exists()){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Un fichier excel(\".xlsx\") de consommation est requis");
                builder.setTitle("Fichier de consommation introuvable");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    openFileLauncher.launch(new String[]{"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
                });
                builder.setNegativeButton("Annuler", (dialog, which) -> {
                    dialog.dismiss();
                    fragment.requireActivity().onBackPressed();
                });
                builder.create().show();
                return null;
            }else{
                consoParser = new ConsoParser(context.openFileInput(CONSO_FILE_NAME));
            }
            return consoParser;
        }catch (Exception e){
            Log.e("ConfigDataProvider", "getConfigData: ", e);
            return null;
        }
    }


    public void saveDataConso(InputStream consoData){
        try {
            FileOutputStream outputStream = context.openFileOutput(CONSO_FILE_NAME, MODE_PRIVATE);
            // copy consoData to outputStream
            byte[] buffer = new byte[1024];
            int length;
            while ((length = consoData.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
        } catch (IOException e) {
            Log.e("SettingsActivity", "onBackPressed: ", e);
            throw new RuntimeException(e);
        }
    }
}
