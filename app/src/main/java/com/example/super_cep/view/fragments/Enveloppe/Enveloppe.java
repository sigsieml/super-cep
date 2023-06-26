package com.example.super_cep.view.fragments.Enveloppe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.super_cep.databinding.FragmentEnveloppeBinding;
import com.example.super_cep.model.Releve.Enveloppe.Eclairage;
import com.example.super_cep.model.Releve.Enveloppe.Menuiserie;
import com.example.super_cep.model.Releve.Enveloppe.Mur;
import com.example.super_cep.model.Releve.Enveloppe.Sol;
import com.example.super_cep.model.Releve.Enveloppe.Toiture;
import com.example.super_cep.model.Releve.Enveloppe.Zone;
import com.example.super_cep.model.Releve.Enveloppe.ZoneElement;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.view.fragments.Enveloppe.AjoutElementsZone.AjoutElementZone;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElements.FragmentEclairage;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElements.FragmentMenuiserie;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElements.FragmentMur;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElements.FragmentSol;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElements.FragmentToitureOuFauxPlafond;

public class Enveloppe extends Fragment implements ZoneUiHandler {

    public Zone[] zones;

    private LiveData<Releve> releve;

    public FragmentEnveloppeBinding binding;
    private ReleveViewModel releveViewModel;

    private ZonesAdaptater zonesAdaptater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        releve = releveViewModel.getReleve();
        binding = FragmentEnveloppeBinding.inflate(inflater, container, false);
        setupFab();
        setupRecyclerView(releve.getValue().getZonesValues());
        releve.observe(getViewLifecycleOwner(), releve -> {
            zones = releve.getZonesValues();
            updateRecyclerView(zones);
        });
        return binding.getRoot();
    }

    private void setupRecyclerView(Zone[] zones){
        RecyclerView recyclerView = binding.RecyclerViewZones;
        zonesAdaptater = new ZonesAdaptater(zones, this);
        binding.searchViewZonesElements.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                zonesAdaptater.getFilter().filter(newText);
                return true;
            }
        });
        recyclerView.setAdapter(zonesAdaptater);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void updateRecyclerView(Zone[] zones) {
        zonesAdaptater.updateZone(zones);
    }

    private void setupFab() {
        final Enveloppe enveloppe = this;
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpNouvelleZone popUpNouvelleZone = new PopUpNouvelleZone(getContext(), enveloppe);
            }
        });
    }


    @Override
    public void voirZoneElement(Zone zone, ZoneElement zoneElement) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = getFragmentFromZoneElement(zone, zoneElement);
        fragmentTransaction.replace(((View)binding.getRoot().getParent()).getId(), fragment, fragment.toString());
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();

    }

    @Override
    public void deleteZone(Zone zone) {
        releveViewModel.deleteZone(zone);
    }

    @Override
    public void moveZoneElement(String nomZoneElement, String nomPreviousZone, String nomNewZone) {
        try {
            releveViewModel.moveZoneElement(nomZoneElement, nomPreviousZone, nomNewZone);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void nouvelleElementZone(Zone zone) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(((View)binding.getRoot().getParent()).getId(), AjoutElementZone.newInstance(zone.nom), "ajoutZoneElement");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
    }

    private Fragment getFragmentFromZoneElement(Zone zone, ZoneElement zoneElement){
        if(zoneElement instanceof Mur){
            return FragmentMur.newInstance(zone.nom,   zoneElement.getNom());
        }
        if(zoneElement instanceof Toiture){
            return FragmentToitureOuFauxPlafond.newInstance(zone.nom,   zoneElement.getNom());
        }
        if(zoneElement instanceof Menuiserie){
            return FragmentMenuiserie.newInstance(zone.nom,   zoneElement.getNom());
        }
        if(zoneElement instanceof Sol){
            return FragmentSol.newInstance(zone.nom,   zoneElement.getNom());
        }
        if(zoneElement instanceof Eclairage){
            return FragmentEclairage.newInstance(zone.nom,   zoneElement.getNom());
        }
        throw new IllegalArgumentException("ZoneElement non reconnu");

    }


    public void nouvelleZone(String toString) {
        try {
            releveViewModel.addZone(new Zone(toString));
        }catch (IllegalArgumentException e){
            Toast.makeText(getContext(), "une zone avec le même nom existe déjà", Toast.LENGTH_SHORT).show();
        }
    }
}