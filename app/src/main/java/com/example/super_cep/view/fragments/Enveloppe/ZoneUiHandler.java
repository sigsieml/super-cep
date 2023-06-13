package com.example.super_cep.view.fragments.Enveloppe;

import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;

public interface ZoneUiHandler {
    void nouvelleElementZone(Zone zone);
    void voirZoneElement(ZoneElement zoneElement);

    void deleteZone(Zone zone);
}
