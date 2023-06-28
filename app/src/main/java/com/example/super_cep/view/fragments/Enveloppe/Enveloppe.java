package com.example.super_cep.view.fragments.Enveloppe;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.databinding.FragmentEnveloppeBinding;
import com.example.super_cep.model.Releve.Enveloppe.Eclairage;
import com.example.super_cep.model.Releve.Enveloppe.Menuiserie;
import com.example.super_cep.model.Releve.Enveloppe.Mur;
import com.example.super_cep.model.Releve.Enveloppe.Sol;
import com.example.super_cep.model.Releve.Enveloppe.Toiture;
import com.example.super_cep.model.Releve.Zone;
import com.example.super_cep.model.Releve.ZoneElement;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.controller.ReleveViewModel;

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
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpNouvelleZone popUpNouvelleZone = new PopUpNouvelleZone(getContext(), new PopUpNouvelleZoneHandler() {
                    @Override
                    public void nouvelleZone(String nomZone) {
                        Enveloppe.this.nouvelleZone(nomZone);
                    }
                }, releveViewModel);
            }
        });
    }


    @Override
    public void voirZoneElement(Zone zone, ZoneElement zoneElement) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        if(zoneElement instanceof Mur){
            EnveloppeDirections.ActionNavEnveloppesToFragmentMur action = EnveloppeDirections.actionNavEnveloppesToFragmentMur(zone.nom, zoneElement.nom, null);
            navController.navigate(action);
        }else if(zoneElement instanceof Toiture){
            EnveloppeDirections.ActionNavEnveloppesToFragmentToitureOuFauxPlafond action = EnveloppeDirections.actionNavEnveloppesToFragmentToitureOuFauxPlafond(zone.nom, zoneElement.nom, null);
            navController.navigate(action);
        }else if(zoneElement instanceof Menuiserie){
            EnveloppeDirections.ActionNavEnveloppesToFragmentMenuiserie action = EnveloppeDirections.actionNavEnveloppesToFragmentMenuiserie(zone.nom, zoneElement.nom, null);
            navController.navigate(action);
        }else if(zoneElement instanceof Sol){
            EnveloppeDirections.ActionNavEnveloppesToFragmentSol action = EnveloppeDirections.actionNavEnveloppesToFragmentSol(zone.nom, zoneElement.nom, null);
            navController.navigate(action);
        }else if(zoneElement instanceof Eclairage){
            EnveloppeDirections.ActionNavEnveloppesToFragmentEclairage action = EnveloppeDirections.actionNavEnveloppesToFragmentEclairage(zone.nom, zoneElement.nom, null);
            navController.navigate(action);
        }else {
            throw new IllegalArgumentException("ZoneElement non reconnu");
        }
    }

    @Override
    public void deleteZone(Zone zone) {
        releveViewModel.deleteZone(zone.nom);
    }

    @Override
    public void moveZoneElement(String nomZoneElement, String nomPreviousZone, String nomNewZone) {
            //ask if the user want a copy or a move
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Déplacer ou copier ?");
            builder.setMessage("Voulez vous déplacer ou copier l'élément ?");
            builder.setPositiveButton("Déplacer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {

                    releveViewModel.moveZoneElement(nomZoneElement, nomPreviousZone, nomNewZone);
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Copier", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                    ZoneElement zoneElement = releveViewModel.getReleve().getValue().getZone(nomPreviousZone).getZoneElement(nomZoneElement);
                    if(zoneElement instanceof Mur){
                        EnveloppeDirections.ActionNavEnveloppesToFragmentMur action = EnveloppeDirections.actionNavEnveloppesToFragmentMur(nomNewZone, nomZoneElement, nomPreviousZone);
                        navController.navigate(action);
                    }else if(zoneElement instanceof Toiture){
                        EnveloppeDirections.ActionNavEnveloppesToFragmentToitureOuFauxPlafond action = EnveloppeDirections.actionNavEnveloppesToFragmentToitureOuFauxPlafond(nomNewZone, nomZoneElement, nomPreviousZone);
                        navController.navigate(action);
                    }else if(zoneElement instanceof Menuiserie){
                        EnveloppeDirections.ActionNavEnveloppesToFragmentMenuiserie action = EnveloppeDirections.actionNavEnveloppesToFragmentMenuiserie(nomNewZone, nomZoneElement, nomPreviousZone);
                        navController.navigate(action);
                    }else if(zoneElement instanceof Sol){
                        EnveloppeDirections.ActionNavEnveloppesToFragmentSol action = EnveloppeDirections.actionNavEnveloppesToFragmentSol(nomNewZone, nomZoneElement, nomPreviousZone);
                        navController.navigate(action);
                    }else if(zoneElement instanceof Eclairage){
                        EnveloppeDirections.ActionNavEnveloppesToFragmentEclairage action = EnveloppeDirections.actionNavEnveloppesToFragmentEclairage(nomNewZone, nomZoneElement, nomPreviousZone);
                        navController.navigate(action);
                    }else {
                        Toast.makeText(getContext(), "ZoneElement non reconnu", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.show();

    }


    @Override
    public void nouvelleElementZone(Zone zone) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        EnveloppeDirections.ActionNavEnveloppesToNavAjoutElementZone action =
                EnveloppeDirections.actionNavEnveloppesToNavAjoutElementZone(zone.nom);
        navController.navigate(action);
    }



    public void nouvelleZone(String toString) {
        try {
            releveViewModel.addZone(new Zone(toString));
        }catch (IllegalArgumentException e){
            Toast.makeText(getContext(), "une zone avec le même nom existe déjà", Toast.LENGTH_SHORT).show();
        }
    }
}