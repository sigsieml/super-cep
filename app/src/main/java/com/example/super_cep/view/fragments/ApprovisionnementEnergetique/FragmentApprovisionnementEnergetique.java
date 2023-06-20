package com.example.super_cep.view.fragments.ApprovisionnementEnergetique;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.databinding.FragmentApprovisionnementEnergetiqueBinding;
import com.example.super_cep.model.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.Releve;
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
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = FragmentApprovisionnementEnergetiqueAjout.newInstance();
            fragmentTransaction.replace(((View)binding.getRoot().getParent()).getId(), fragment, fragment.toString());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.commit();
        });
        return binding.getRoot();
    }


    private void setupRecyclerView(ApprovisionnementEnergetique[] approvisionnementEnergetique) {
        ApprovisonnementEnergetiqueAdapter adapter = new ApprovisonnementEnergetiqueAdapter(approvisionnementEnergetique, new ApprovisionnementEnergetiqueViewHolderListener() {

            @Override
            public void onClick(ApprovisionnementEnergetique approvisonnementEnergetique) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = FragmentApprovisionnementEnergetiqueAjout.newInstance(approvisonnementEnergetique.nom);
                fragmentTransaction.replace(((View)binding.getRoot().getParent()).getId(), fragment, fragment.toString());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
            }
        });
        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            adapter.update(releve.approvisionnementEnergetiques.values().toArray(new ApprovisionnementEnergetique[0]));
        });
        binding.recyclerViewApprovisionnementEnergetique.setAdapter(adapter);
        binding.recyclerViewApprovisionnementEnergetique.setLayoutManager(new LinearLayoutManager(requireContext()));
    }


}