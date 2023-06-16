package com.example.super_cep.model.Export;

import android.net.Uri;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UriDeserializer extends JsonDeserializer<List<Uri>> {
    @Override
    public List<Uri> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<Uri> uris = new ArrayList<>();
        String[] uriStrings = p.readValueAs(String[].class);
        for (String uriString : uriStrings) {
            uris.add(Uri.parse(uriString));
        }
        return uris;
    }
}