package com.example.super_cep.view.fragments.Chauffages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.databinding.FragmentChauffagesBinding;
import com.example.super_cep.model.Releve.Chauffage.Chauffage;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.view.fragments.ECS.FragmentECSDirections;

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
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            FragmentChauffageDirections.ActionNavChauffagesToNavChauffageAjout action =
                    FragmentChauffageDirections.actionNavChauffagesToNavChauffageAjout(null);
            navController.navigate(action);
        });
        return binding.getRoot();
    }


    private void setupRecyclerView(Chauffage[] chauffages) {
        ChauffagesAdapter adapter = new ChauffagesAdapter(chauffages, new ChauffageViewHolderListener() {
            @Override
            public void onClick(Chauffage chauffage) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                FragmentChauffageDirections.ActionNavChauffagesToNavChauffageAjout action =
                        FragmentChauffageDirections.actionNavChauffagesToNavChauffageAjout(chauffage.nom);
                navController.navigate(action);
            }
        });
        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            adapter.update(releve.chauffages.values().toArray(new Chauffage[0]));
        });
        binding.recyclerViewChauffages.setAdapter(adapter);
        binding.recyclerViewChauffages.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}