package com.example.super_cep.model.ConfigData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonConfigDataManager {

    private static ObjectMapper mapper = new ObjectMapper();


    public static String serialize(ConfigData releve) {
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(releve);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public static ConfigData deserialize(String jsonString) {
        ConfigData releve = null;
        try {
            releve = mapper.readValue(jsonString, ConfigData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return releve;
    }

}
