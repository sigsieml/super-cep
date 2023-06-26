package com.example.super_cep.view.fragments.UsageEtOccupation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.databinding.FragmentUsageEtOccupationBinding;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Enveloppe.Zone;

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
        CalendrierAdaptater calendrierAdaptater = new CalendrierAdaptater(calendriersValues, zones, new CalendrierViewHolderListener() {
            @Override
            public void onClick(Calendrier calendrier) {

                FragmentManager fragmentManager =  getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(((View)binding.getRoot().getParent()).getId(), FragmentUsageEtOccupationCalendrier.create(calendrier.nom), "fragment usage et occupation calendrier");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();

            }

            @Override
            public void onLongClick(Calendrier calendrier) {
                PopUpModificationCalendrier.create(getContext(), zones, calendrier, new PopUpModificationCalendrierListener() {
                    @Override
                    public void onValider(String oldName, Calendrier calendrier) {
                        try {
                            releveViewModel.updateCalendrier(oldName, calendrier);
                        }catch (Exception e){
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSupprimer(String nomCalendrier) {
                        releveViewModel.supprimerCalendrier(nomCalendrier);
                    }
                });

            }
        });

        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            calendrierAdaptater.updateValues(releve.getCalendriersValues(), releve.getZonesValues());
        });
        binding.recyclerViewCalendrier.setAdapter(calendrierAdaptater);
        binding.recyclerViewCalendrier.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
    }


}