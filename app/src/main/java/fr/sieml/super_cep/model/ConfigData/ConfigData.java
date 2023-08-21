package fr.sieml.super_cep.model.ConfigData;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * La classe ConfigData représente les données de configuration de l'application.
 * <p>
 *     Elle est utilisée pour stocker les données de configuration de l'application.
 *     Elle est utilisée dans {@link JsonConfigDataManager}
 * </p>
 */
public class ConfigData {

    public Map<String, List<String>> map;

    public static final String CONFIG_FILE_NAME = "configData.json";


    @JsonCreator
    public ConfigData(@JsonProperty("configData") Map<String, List<String>> map) {
        this.map = map;
    }

    public static ConfigData configDataProviderFromInputStream(InputStream inputStream){
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
