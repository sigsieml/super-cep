package com.example.super_cep.model.SpinnerData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonConfigDataManager {

    private static ObjectMapper mapper = new ObjectMapper();


    public static String serialize(ConfigDataProvider releve) {
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(releve);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public static ConfigDataProvider deserialize(String jsonString) {
        ConfigDataProvider releve = null;
        try {
            releve = mapper.readValue(jsonString, ConfigDataProvider.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return releve;
    }

}
