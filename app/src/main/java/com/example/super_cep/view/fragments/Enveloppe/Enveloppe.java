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

import com.example.super_cep.databinding.FragmentEnveloppeBinding;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;
import com.example.super_cep.model.Releve;
import com.example.super_cep.view.ReleveViewModel;
import com.google.android.material.snackbar.Snackbar;

public class Enveloppe extends Fragment implements ZoneUiHandler {

    public Zone[] zones;

    private LiveData<Releve> releve;

    public FragmentEnveloppeBinding binding;
    private ReleveViewModel releveViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        releve = releveViewModel.getReleve();
        binding = FragmentEnveloppeBinding.inflate(inflater, container, false);
        setupFab();
        releve.observe(getViewLifecycleOwner(), releve -> {
            zones = releve.getZones();
            updateRecyclerView(zones);
        });
        updateRecyclerView(releve.getValue().getZones());
        return binding.getRoot();
    }

    private void updateRecyclerView(Zone[] zones) {
        RecyclerView recyclerView = binding.RecyclerViewZones;
        recyclerView.setAdapter(new ZonesAdaptater(zones, this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
    public void voirZoneElement(ZoneElement zoneElement) {
        Snackbar.make(binding.getRoot(), "Voir zone element" + zoneElement.toString(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void deleteZone(Zone zone) {
        releveViewModel.deleteZone(zone);
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

    public void nouvelleZone(String toString) {
        releveViewModel.addZone(new Zone(toString));
    }
}