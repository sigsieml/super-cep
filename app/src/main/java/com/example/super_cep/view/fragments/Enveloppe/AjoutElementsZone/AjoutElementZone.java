package com.example.super_cep.view.fragments.Enveloppe.AjoutElementsZone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.super_cep.R;
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
                back();
            }
        });

        binding.layoutMur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentMur(nomZone, null, null));
            }
        });

        binding.layoutToitureEtFauxPlafond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentToitureOuFauxPlafond(nomZone, null, null));
            }
        });

        binding.layoutMenuiserie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentMenuiserie(nomZone, null, null));
            }
        });

        binding.layoutSols.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentSol(nomZone, null, null));
            }
        });

        binding.layoutEclairage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentEclairage(nomZone, null, null));
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
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(getNavDirectionFromZoneElement(ancienneZone, zoneElement));
    }

    private void OuvrirFragmentAjout(NavDirections navDirections){
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(navDirections);
    }

    private NavDirections getNavDirectionFromZoneElement(Zone ancienneZone, ZoneElement zoneElement){
        if(zoneElement instanceof Mur){
            return AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentMur(nomZone,ancienneZone.nom, zoneElement.getNom());
        }
        if(zoneElement instanceof Toiture){
            return AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentToitureOuFauxPlafond(nomZone,ancienneZone.nom, zoneElement.getNom());
        }
        if(zoneElement instanceof Menuiserie){
            return AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentMenuiserie(nomZone,ancienneZone.nom, zoneElement.getNom());
        }
        if(zoneElement instanceof Sol){
            return AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentSol(nomZone,ancienneZone.nom, zoneElement.getNom());
        }
        if(zoneElement instanceof Eclairage){
            return AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentEclairage(nomZone,ancienneZone.nom, zoneElement.getNom());
        }
        throw new IllegalArgumentException("ZoneElement non reconnu");

    }

    private void back(){
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.popBackStack();
    }

}