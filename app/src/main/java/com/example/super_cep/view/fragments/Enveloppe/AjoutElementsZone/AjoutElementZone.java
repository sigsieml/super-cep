package com.example.super_cep.view.fragments.Enveloppe.AjoutElementsZone;

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

import com.example.super_cep.databinding.FragmentAjoutElementZoneBinding;
import com.example.super_cep.model.Releve.Enveloppe.Eclairage;
import com.example.super_cep.model.Releve.Enveloppe.Menuiserie;
import com.example.super_cep.model.Releve.Enveloppe.Mur;
import com.example.super_cep.model.Releve.Enveloppe.Sol;
import com.example.super_cep.model.Releve.Enveloppe.Toiture;
import com.example.super_cep.model.Releve.Enveloppe.Zone;
import com.example.super_cep.model.Releve.Enveloppe.ZoneElement;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElements.FragmentEclairage;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElements.FragmentMenuiserie;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElements.FragmentMur;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElements.FragmentSol;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElements.FragmentToitureOuFauxPlafond;

public class AjoutElementZone extends Fragment {

    private static final String ARG_PARAM1 = "nomZone";
    private String nomZone;
    public AjoutElementZone() {}
    public static AjoutElementZone newInstance(String nomZone) {
        AjoutElementZone fragment = new AjoutElementZone();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, nomZone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nomZone = requireArguments().getString(ARG_PARAM1);
    }

    FragmentAjoutElementZoneBinding binding;
    private ReleveViewModel releveViewModel;
    private LiveData<Releve> releve;
    public Zone[] zones;

    private ZonesCopieAdaptater zonesCopieAdaptater;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAjoutElementZoneBinding.inflate(inflater, container, false);
        binding.textViewNomZone.setText("nouvel élément dans la zone " + nomZone);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        releve = releveViewModel.getReleve();
        setupRecyclerView(releve.getValue().getZonesValues());
        releve.observe(getViewLifecycleOwner(), releve -> {
            zones = releve.getZonesValues();
            updateRecyclerView(zones);
        });


        binding.buttonAnnulerAjoutElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        binding.layoutMur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(FragmentMur.newInstance(nomZone));
            }
        });

        binding.layoutToitureEtFauxPlafond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(FragmentToitureOuFauxPlafond.newInstance(nomZone));
            }
        });

        binding.layoutMenuiserie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(FragmentMenuiserie.newInstance(nomZone));
            }
        });

        binding.layoutSols.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(FragmentSol.newInstance(nomZone));
            }
        });

        binding.layoutEclairage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(FragmentEclairage.newInstance(nomZone));
            }
        });

        return binding.getRoot();
    }


    private void setupRecyclerView(Zone[] zones) {
        RecyclerView recyclerView = binding.recyclerViewCopieZoneElement;
        zonesCopieAdaptater = new ZonesCopieAdaptater(zones, new ElementZoneCopieHandler() {
            @Override
            public void onClick(Zone zone, ZoneElement zoneElement) {
                OuvrirFragmentCopie( zone,  zoneElement);
            }
        });


        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                zonesCopieAdaptater.getFilter().filter(newText);
                return true;
            }
        });

        recyclerView.setAdapter(zonesCopieAdaptater);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void updateRecyclerView(Zone[] zones){
        zonesCopieAdaptater.updateZones(zones);
    }

    private void OuvrirFragmentCopie(Zone ancienneZone, ZoneElement zoneElement){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = getFragmentFromZoneElement(ancienneZone, zoneElement);
        fragmentTransaction.replace(((View)binding.getRoot().getParent()).getId(), fragment, fragment.toString());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
    }

    private void OuvrirFragmentAjout(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(((View)binding.getRoot().getParent()).getId(), fragment, fragment.toString());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
    }

    private Fragment getFragmentFromZoneElement(Zone ancienneZone, ZoneElement zoneElement){
        if(zoneElement instanceof Mur){
            return FragmentMur.newInstance(nomZone, ancienneZone.nom,  zoneElement.getNom());
        }
        if(zoneElement instanceof Toiture){
            return FragmentToitureOuFauxPlafond.newInstance(nomZone, ancienneZone.nom,  zoneElement.getNom());
        }
        if(zoneElement instanceof Menuiserie){
            return FragmentMenuiserie.newInstance(nomZone, ancienneZone.nom,  zoneElement.getNom());
        }
        if(zoneElement instanceof Sol){
            return FragmentSol.newInstance(nomZone, ancienneZone.nom,  zoneElement.getNom());
        }
        if(zoneElement instanceof Eclairage){
            return FragmentEclairage.newInstance(nomZone, ancienneZone.nom,  zoneElement.getNom());
        }
        throw new IllegalArgumentException("ZoneElement non reconnu");

    }
}