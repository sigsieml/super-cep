package fr.sieml.super_cep.controller;

import android.location.Address;

public interface LocalisationProviderListener {
    void onLocalisationChanged(Address address);
}
