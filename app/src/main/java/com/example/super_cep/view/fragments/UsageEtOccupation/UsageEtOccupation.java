package com.example.super_cep.view.fragments.UsageEtOccupation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.databinding.FragmentUsageEtOccupationBinding;
import com.example.super_cep.model.Calendrier.Calendrier;
import com.example.super_cep.model.Enveloppe.Zone;

public class UsageEtOccupation extends Fragment {


    private ReleveViewModel releveViewModel;
    private FragmentUsageEtOccupationBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentUsageEtOccupationBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        setupRecyclerView(releveViewModel.getReleve().getValue().getCalendriersValues(), releveViewModel.getReleve().getValue().getZonesValues());

        binding.floatingActionButtonAjoutCalendrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAjoutCalendrier.create(getContext(),releveViewModel.getReleve().getValue().getZonesValues(), new PopUpAjoutCalendrierListener() {
                    @Override
                    public void onValider(Calendrier calendrier) {
                        releveViewModel.addCalendrier(calendrier);
                    }
                });
            }
        });
        return binding.getRoot();
    }

    private void setupRecyclerView(Calendrier[] calendriersValues, Zone[] zones) {
        CalendrierAdaptater calendrierAdaptater = new CalendrierAdaptater(calendriersValues, zones, new PopUpModificationCalendrierListener() {
            @Override
            public void onValider(String oldName, Calendrier calendrier) {
                releveViewModel.updateCalendrier(oldName, calendrier);
            }

            @Override
            public void onSupprimer(String nomCalendrier) {
                releveViewModel.supprimerCalendrier(nomCalendrier);
            }
        });
        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            calendrierAdaptater.updateValues(releve.getCalendriersValues(), releve.getZonesValues());
        });
        binding.recyclerViewCalendrier.setAdapter(calendrierAdaptater);
        binding.recyclerViewCalendrier.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
    }


}