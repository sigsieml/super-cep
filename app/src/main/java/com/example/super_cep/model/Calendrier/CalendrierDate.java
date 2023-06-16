package com.example.super_cep.model.Calendrier;

import com.example.super_cep.model.Export.CalendrierDateKeyDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;

import java.time.DayOfWeek;
import java.util.Calendar;

public class CalendrierDate  {

    public DayOfWeek jour;
    public int heure;
    public int minute;


    @JsonCreator
    public CalendrierDate(@JsonProperty("jour") DayOfWeek jour ,@JsonProperty("heure") int heure ,@JsonProperty("minute") int minute) {
        this.jour = jour;
        this.heure = heure;
        this.minute = minute;
    }
}
