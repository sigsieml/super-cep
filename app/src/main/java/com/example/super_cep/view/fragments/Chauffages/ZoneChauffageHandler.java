package com.example.super_cep.view.fragments.Chauffages;

import com.example.super_cep.model.Releve.Chauffage.Chauffage;
import com.example.super_cep.model.Releve.Zone;
import com.example.super_cep.model.Releve.ZoneElement;

public interface ZoneChauffageHandler {
    void nouvelleElementZone(String zone);
    void voirZoneElement(String zone, Chauffage chauffage);

    void moveZoneElement(String nomChauffage, String nomPreviousZone, String nomNewZone);

    void deleteZone(String zone);
}
