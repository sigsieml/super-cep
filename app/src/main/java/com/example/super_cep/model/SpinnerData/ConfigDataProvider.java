package com.example.super_cep.model.SpinnerData;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.super_cep.model.Export.JsonReleveManager;
import com.example.super_cep.model.Releve.Releve;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.poi.util.IOUtils;
import org.apache.xmlbeans.impl.common.IOUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigDataProvider {

    public Map<String, List<String>> configData;

    public static final String CONFIG_FILE_NAME = "configData.json";


    @JsonCreator
    public ConfigDataProvider(@JsonProperty("configData") Map<String, List<String>> configData) {
        this.configData = configData;
    }

    public static ConfigDataProvider configDataProviderFromInputStream(InputStream inputStream){
        try (InputStream is = inputStream){
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
            return JsonConfigDataManager.deserialize(total.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
