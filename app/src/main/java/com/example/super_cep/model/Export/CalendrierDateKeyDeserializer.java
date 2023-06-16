package com.example.super_cep.model.Export;

import com.example.super_cep.model.Calendrier.CalendrierDate;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import java.time.DayOfWeek;
public class CalendrierDateKeyDeserializer extends KeyDeserializer {


    @Override
    public CalendrierDate deserializeKey(String key, DeserializationContext ctxt) {
        String[] parts = key.split("-"); // Assume that the key is formatted as "DayOfWeek-hour-minute"
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid key format");
        }
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(parts[0]);
        int hour = Integer.parseInt(parts[1]);
        int minute = Integer.parseInt(parts[2]);
        return new CalendrierDate(dayOfWeek, hour, minute);
    }

}
