package fr.sieml.super_cep.model.Export;

import fr.sieml.super_cep.model.Releve.Releve;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * La classe JsonReleveManager gère la sérialisation et la désérialisation des objets Releve en JSON et vice versa.
 * <p>
 *     Elle utilise la bibliothèque Jackson pour sérialiser et désérialiser des objets Releve en JSON et vice versa.
 * </p>
 */
public class JsonReleveManager {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // Pour la gestion des objets de type Calendar.
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new CalendrierDateModule());
    }
    /**
     * Sérialise l'objet Releve en une chaîne JSON.
     *
     * @param releve L'objet Releve à sérialiser.
     * @return La chaîne JSON représentant l'objet Releve.
     */
    public static String serialize(Releve releve) {
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(releve);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
    /**
     * Désérialise une chaîne JSON en un objet Releve.
     *
     * @param jsonString La chaîne JSON représentant l'objet Releve.
     * @return L'objet Releve désérialisé à partir de la chaîne JSON.
     */
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
