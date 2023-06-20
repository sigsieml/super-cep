package com.example.super_cep.view.fragments.Chauffages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.databinding.FragmentChauffagesBinding;
import com.example.super_cep.model.Chauffage;
import com.example.super_cep.model.Releve;

public class FragmentChauffage extends Fragment {

    public FragmentChauffage() {
        // Required empty public constructor
    }

    private FragmentChauffagesBinding binding;
    private ReleveViewModel releveViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChauffagesBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        Releve releve = releveViewModel.getReleve().getValue();

        setupRecyclerView(releve.chauffages.values().toArray(new Chauffage[0]));

        binding.floatingActionButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = FragmentChauffageAjout.newInstance();
            fragmentTransaction.replace(((View)binding.getRoot().getParent()).getId(), fragment, fragment.toString());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.commit();
        });
        return binding.getRoot();
    }


    private void setupRecyclerView(Chauffage[] chauffages) {
        ChauffagesAdapter adapter = new ChauffagesAdapter(chauffages, new ChauffageViewHolderListener() {
            @Override
            public void onClick(Chauffage chauffage) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = FragmentChauffageAjout.newInstance(chauffage.nom);
                fragmentTransaction.replace(((View)binding.getRoot().getParent()).getId(), fragment, fragment.toString());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
            }
        });
        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            adapter.update(releve.chauffages.values().toArray(new Chauffage[0]));
        });
        binding.recyclerViewChauffages.setAdapter(adapter);
        binding.recyclerViewChauffages.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}