package com.example.super_cep.model.Releve.Calendrier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.DayOfWeek;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "CalendrierDate{" +
                "jour=" + jour +
                ", heure=" + heure +
                ", minute=" + minute +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendrierDate that = (CalendrierDate) o;
        return heure == that.heure && minute == that.minute && jour == that.jour;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jour, heure, minute);
    }
}
