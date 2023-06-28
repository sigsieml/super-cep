package com.example.super_cep.view.fragments.Enveloppe;

import com.example.super_cep.model.Releve.Zone;
import com.example.super_cep.model.Releve.ZoneElement;

public interface ZoneUiHandler {
    void nouvelleElementZone(Zone zone);
    void voirZoneElement(Zone zone, ZoneElement zoneElement);

    void deleteZone(Zone zone);

    void moveZoneElement(String nomZoneElement, String nomPreviousZone, String nomNewZone);

}
