package fr.sieml.super_cep.view.fragments.Chauffages;

import fr.sieml.super_cep.model.Releve.Chauffage.Chauffage;

public interface ZoneChauffageHandler {
    void nouvelleElementZone(String zone);
    void voirZoneElement(String zone, Chauffage chauffage);

    void moveZoneElement(String nomChauffage, String nomPreviousZone, String nomNewZone);

    void deleteZone(String zone);
}
