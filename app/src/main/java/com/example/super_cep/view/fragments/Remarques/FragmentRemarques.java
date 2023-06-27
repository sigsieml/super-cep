package com.example.super_cep.view.fragments.Remarques;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.databinding.FragmentRemarquesBinding;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.model.Releve.Remarque;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FragmentRemarques extends Fragment {


    private FragmentRemarquesBinding binding;
    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;
    Releve releve;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRemarquesBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);
        setupRecyclerView(releveViewModel.getReleve().getValue().remarques.values().toArray(new Remarque[0]));
        setupFabs();
        return binding.getRoot();
    }
    private void setupRecyclerView(Remarque[] remarques) {
        // order the remarques in the alphabetical order
        List<Remarque> orderedRemarque =Arrays.asList(remarques);
        Collections.sort(orderedRemarque);
        RemarqueAdapter remarqueAdapter = new RemarqueAdapter(orderedRemarque.toArray(new Remarque[0]), new RemarqueViewHolderListener() {
            @Override
            public void onClick(Remarque remarque) {
                // Alert Dialog to delete the remarque
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Supprimer la remarque");
                builder.setMessage("Voulez-vous vraiment supprimer cette remarque ?");
                builder.setPositiveButton("Oui", (dialog, which) -> {
                    releveViewModel.removeRemarque(remarque);

                });
                builder.setNegativeButton("Non", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.show();
            }

            @Override
            public void onRemarqueEdited(String oldName, Remarque remarque) {
                releveViewModel.editRemarque(oldName,remarque);
            }


        });
        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            binding.recyclerViewRemarque.post(new Runnable() {
                @Override
                public void run() {
                    List<Remarque> orderedRemarque = new ArrayList<>(releve.remarques.values());
                    Collections.sort(orderedRemarque);
                    remarqueAdapter.update(orderedRemarque.toArray(new Remarque[0]));
                }
            });
        });

        binding.recyclerViewRemarque.setAdapter(remarqueAdapter);
        binding.recyclerViewRemarque.setLayoutManager(new LinearLayoutManager(getContext()));


    }


    private void setupFabs() {
        binding.floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupNouvelleRemarque.create(getContext(),releveViewModel, spinnerDataViewModel , new PopupNouvelleRemarqueListener() {
                    @Override
                    public void onValidate(Remarque remarque) {
                        try {
                            releveViewModel.addRemarque(remarque);
                        }catch (Exception e) {
                            Log.e("FragmentRemarques", "onValidate: ", e);
                            Toast.makeText(getContext(), "Erreur lors de l'ajout de la remarque : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

}