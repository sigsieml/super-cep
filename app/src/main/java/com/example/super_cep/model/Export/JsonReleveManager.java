package com.example.super_cep.model.Export;

import com.example.super_cep.model.Releve.Releve;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class JsonReleveManager {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        // Pour la gestion des objets de type Calendar.
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new CalendrierDateModule());
    }

    public static String serialize(Releve releve) {
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(releve);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public static Releve deserialize(String jsonString) {
        Releve releve = null;
        try {
            releve = mapper.readValue(jsonString, Releve.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return releve;
    }

}
