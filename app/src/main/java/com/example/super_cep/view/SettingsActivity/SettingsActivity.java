package com.example.super_cep.view.SettingsActivity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.super_cep.controller.ConfigDataProvider;
import com.example.super_cep.databinding.ActivitySettingsBinding;
import com.example.super_cep.model.ConfigData.ConfigData;
import com.example.super_cep.model.ConfigData.JsonConfigDataManager;
import com.example.super_cep.view.ReleveActivity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements SettingViewHolderListener{


    private static final String NOM_FICHIER_SAUVEGARDE = "super-cep-config.json";
    private ActivitySettingsBinding binding;

    private ConfigData configData;

    private ConfigDataProvider configDataProvider;

    private boolean isModified = false;

    private ActivityResultLauncher<String> createFileLauncher;

    private ActivityResultLauncher<String[]> openFileLauncher;

    private SettingAdapter settingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configDataProvider = new ConfigDataProvider(this);
        configData = configDataProvider.getConfigData();
        setupRecyclerView();

        setupActivityResult();

        binding.fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alert dialog with 3 options : 1) reset to default 2) save to a file 3) load from a file
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Options");
                builder.setItems(new String[]{"Réinitialiser", "Sauvegarder dans un fichier", "Charger depuis un fichier"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            try {
                                isModified = true;
                                configData = configDataProvider.getDefaultConfigData();
                                settingAdapter.update(configData.map);
                            } catch (IOException e) {
                                Log.e("SettingsActivity", "onClick: ", e);
                                Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            SettingsActivity.this.saveToAFileConfigData();
                            break;
                        case 2:
                            SettingsActivity.this.loadFromAFileConfigData();
                            break;
                    }
                });
                builder.show();
            }
        });
    }

    private void loadFromAFileConfigData() {
        openFileLauncher.launch(new String[]{"application/json"});
    }

    private void setupActivityResult() {
       createFileLauncher = registerForActivityResult( new ActivityResultContracts.CreateDocument("application/json"), uri -> {
            if(uri == null) return;
            try {
                String json = JsonConfigDataManager.serialize(configData);
                getContentResolver().openOutputStream(uri).write(json.getBytes());
            } catch (IOException e) {
                Log.e("SettingsActivity", "onActivityResult: ", e);
                Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

       openFileLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
           if(uri == null) return;
           try {
               configData = ConfigData.configDataProviderFromInputStream(getContentResolver().openInputStream(uri));
               settingAdapter.update(configData.map);
           } catch (IOException e) {
               Log.e("SettingsActivity", "onActivityResult: ", e);
               Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
           }
       });
    }

    private void saveToAFileConfigData() {
        createFileLauncher.launch(NOM_FICHIER_SAUVEGARDE);
    }

    private void setupRecyclerView() {
        settingAdapter = new SettingAdapter(configData.map, this);
        binding.recyclerViewSetting.setAdapter(settingAdapter);
        binding.recyclerViewSetting.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        if(!isModified) {
            finish();
            return;
        }
        //ask if user want to save
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sauvegarder ?");
        builder.setMessage("Voulez-vous sauvegarder les modifications ?");
        builder.setPositiveButton("Oui", (dialog, which) -> {
            configDataProvider.saveConfigData(configData);
            finish();
        });
        builder.setNegativeButton("Non", (dialog, which) -> {
            finish();
        });
        builder.show();
    }

    @Override
    public void onSettingClicked(String key) {
        PopUpModificationSetting.create(this, key, configData.map.get(key), new PopUpModificationSettingListener() {
            @Override
            public void onValidate(String key, List<String> values) {
                isModified = true;
                configData.map.put(key, values);
                settingAdapter.update(configData.map);
            }
        });
    }
}