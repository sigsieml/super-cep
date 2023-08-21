package fr.sieml.super_cep.controller;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import fr.sieml.super_cep.model.ConfigData.ConfigData;
import fr.sieml.super_cep.model.ConfigData.JsonConfigDataManager;

import java.io.FileOutputStream;
import java.io.IOException;

public class ConfigDataProvider {

    private Context context;


    public ConfigDataProvider(Context context) {
        this.context = context;
    }

    public ConfigData getConfigData(){
        // check if file exist
        try {
            ConfigData configData;
            if(!context.getFileStreamPath(ConfigData.CONFIG_FILE_NAME).exists()){
                configData = ConfigData.configDataProviderFromInputStream(context.getAssets().open(ConfigData.CONFIG_FILE_NAME));
            }else{
                configData = ConfigData.configDataProviderFromInputStream(context.openFileInput(ConfigData.CONFIG_FILE_NAME));
            }
            return configData;
        }catch (Exception e){
            Log.e("ConfigDataProvider", "getConfigData: ", e);
            return null;
        }
    }


    public void saveConfigData(ConfigData configData){
        try {
            FileOutputStream outputStream = context.openFileOutput(ConfigData.CONFIG_FILE_NAME, MODE_PRIVATE);
            outputStream.write(JsonConfigDataManager.serialize(configData).getBytes());
            outputStream.close();
        } catch (IOException e) {
            Log.e("SettingsActivity", "onBackPressed: ", e);
            throw new RuntimeException(e);
        }
    }

    public ConfigData getDefaultConfigData() throws IOException {
        return ConfigData.configDataProviderFromInputStream(context.getAssets().open(ConfigData.CONFIG_FILE_NAME));
    }
}
