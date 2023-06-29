package com.example.super_cep.model.Export;

import com.example.super_cep.model.Releve.Calendrier.CalendrierDate;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.time.DayOfWeek;

public class CalendrierDateModule extends SimpleModule {

    public CalendrierDateModule() {
        super();
        addKeyDeserializer(CalendrierDate.class, new CalendrierDateKeyDeserializer());
        addKeySerializer(CalendrierDate.class, new CalendrierDateKeySerializer());
    }

    private class CalendrierDateKeyDeserializer extends KeyDeserializer {
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


    private class CalendrierDateKeySerializer extends JsonSerializer<CalendrierDate> {
        @Override
        public void serialize(CalendrierDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeFieldName(value.jour.toString() + "-" + value.heure + "-" + value.minute);
        }
    }
}
