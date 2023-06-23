package com.example.super_cep.model.Calendrier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.DayOfWeek;

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
