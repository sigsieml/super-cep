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
/**
 * Module pour sérialiser/désérialiser les clés de type CalendrierDate.
 * <p>
 *     Ce module est utilisé dans {@link com.example.super_cep.model.Export.JsonReleveManager}
 *     pour sérialiser/désérialiser les clés de type CalendrierDate.
 * </p>
 */
public class CalendrierDateModule extends SimpleModule {
    /**
     * Constructeur pour le module CalendrierDateModule.
     * Ajoute un désérialiseur de clés et un sérialiseur de clés pour le type CalendrierDate.
     */
    public CalendrierDateModule() {
        super();
        addKeyDeserializer(CalendrierDate.class, new CalendrierDateKeyDeserializer());
        addKeySerializer(CalendrierDate.class, new CalendrierDateKeySerializer());
    }
    /**
     * Désérialiseur de clés pour le type CalendrierDate.
     */
    private class CalendrierDateKeyDeserializer extends KeyDeserializer {
        /**
         * Désérialise une clé de type String en une instance de CalendrierDate.
         * La clé est supposée être formatée comme "DayOfWeek-hour-minute".
         *
         * @param key La clé de type String à désérialiser.
         * @param ctxt Le contexte de désérialisation.
         * @return Une instance de CalendrierDate.
         */
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

    /**
     * Sérialiseur de clés pour le type CalendrierDate.
     */
    private class CalendrierDateKeySerializer extends JsonSerializer<CalendrierDate> {
        /**
         * Sérialise une instance de CalendrierDate en une clé de type String.
         * La clé est formatée comme "DayOfWeek-hour-minute".
         *
         * @param value L'instance de CalendrierDate à sérialiser.
         * @param gen Le générateur Json utilisé pour la sérialisation.
         * @param serializers Le fournisseur de sérialiseurs.
         * @throws IOException Si une erreur d'E/S se produit lors de l'écriture de la clé sérialisée.
         */
        @Override
        public void serialize(CalendrierDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeFieldName(value.jour.toString() + "-" + value.heure + "-" + value.minute);
        }
    }
}
