package com.example.super_cep.model.Export;

import android.net.Uri;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

public class UriSerializer extends JsonSerializer<List<Uri>> {
    @Override
    public void serialize(List<Uri> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            String[] uriStrings = new String[value.size()];
            for (int i = 0; i < value.size(); i++) {
                uriStrings[i] = value.get(i).toString();
            }
            gen.writeArray(uriStrings, 0, uriStrings.length);
        }
    }
}