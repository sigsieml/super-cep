package com.example.super_cep.view.fragments.ApprovisionnementEnergetique;

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
import com.example.super_cep.databinding.FragmentApprovisionnementEnergetiqueBinding;
import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.Releve.Releve;
public class FragmentApprovisionnementEnergetique extends Fragment {
    public FragmentApprovisionnementEnergetique() {
        // Required empty public constructor
    }

    private FragmentApprovisionnementEnergetiqueBinding binding;

    private ReleveViewModel releveViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentApprovisionnementEnergetiqueBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        Releve releve = releveViewModel.getReleve().getValue();

        setupRecyclerView(releve.approvisionnementEnergetiques.values().toArray(new ApprovisionnementEnergetique[0]));

        binding.floatingActionButton2.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            com.example.super_cep.view.fragments.ApprovisionnementEnergetique.FragmentApprovisionnementEnergetiqueDirections.ActionNavApprovisionnementEnergetiqueToFragmentApprovisionnementEnergetiqueAjout action =
                    FragmentApprovisionnementEnergetiqueDirections.actionNavApprovisionnementEnergetiqueToFragmentApprovisionnementEnergetiqueAjout(null);
            navController.navigate(action);
        });
        return binding.getRoot();
    }


    private void setupRecyclerView(ApprovisionnementEnergetique[] approvisionnementsEnergetique) {
        ApprovisonnementEnergetiqueAdapter adapter = new ApprovisonnementEnergetiqueAdapter(approvisionnementsEnergetique, new ApprovisionnementEnergetiqueViewHolderListener() {

            @Override
            public void onClick(ApprovisionnementEnergetique approvisionnementEnergetique) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                com.example.super_cep.view.fragments.ApprovisionnementEnergetique.FragmentApprovisionnementEnergetiqueDirections.ActionNavApprovisionnementEnergetiqueToFragmentApprovisionnementEnergetiqueAjout action =
                        FragmentApprovisionnementEnergetiqueDirections.actionNavApprovisionnementEnergetiqueToFragmentApprovisionnementEnergetiqueAjout(approvisionnementEnergetique.nom);
                navController.navigate(action);
            }
        });
        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            adapter.update(releve.approvisionnementEnergetiques.values().toArray(new ApprovisionnementEnergetique[0]));
        });
        binding.recyclerViewApprovisionnementEnergetique.setAdapter(adapter);
        binding.recyclerViewApprovisionnementEnergetique.setLayoutManager(new LinearLayoutManager(requireContext()));
    }


}