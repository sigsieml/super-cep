package fr.sieml.super_cep.controller.Conso;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;

public class ConsoProvider {


    private static final String PREFS_NAME = "ConsoPrefs";
    private static final String CONSO_URI_KEY = "conso_uri";

    private Context context;
    private Fragment fragment;
    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<String[]> openFileLauncher;

    public ConsoProvider(Fragment fragment, ConsoProviderListener consoProviderListener) {
        this.context = fragment.getContext();
        this.fragment = fragment;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        openFileLauncher = fragment.registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if(uri == null) return;
            try {
                saveDataConso(uri);
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
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
        String uriString = sharedPreferences.getString(CONSO_URI_KEY, null);
        if (uriString == null) {
            promptForNewFile();
            return null;
        }
        Uri uri = Uri.parse(uriString);
        try {
            return new ConsoParser(context.getContentResolver().openInputStream(uri));
        } catch (Exception e) {
            Log.e("ConfigDataProvider", "getConfigData: ", e);
            return null;
        }
    }

    private void promptForNewFile() {
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
    }

    public void editFileInExcel() {
        String uriString = sharedPreferences.getString(CONSO_URI_KEY, null);
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Aucune application pour ouvrir le fichier Excel n'est installée", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Aucun fichier Excel n'est disponible pour l'édition", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveDataConso(Uri consoDataUri){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CONSO_URI_KEY, consoDataUri.toString());
        editor.apply();
    }
}
