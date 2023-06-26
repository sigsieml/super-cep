package com.example.super_cep.view.fragments.ECS;

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
import com.example.super_cep.databinding.FragmentECSBinding;
import com.example.super_cep.model.Releve.ECS;
import com.example.super_cep.model.Releve.Releve;

public class FragmentECS extends Fragment {

    public FragmentECS() {
        // Required empty public constructor
    }

    private FragmentECSBinding binding;

    private ReleveViewModel releveViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentECSBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        Releve releve = releveViewModel.getReleve().getValue();

        setupRecyclerView(releve.ecs.values().toArray(new ECS[0]));

        binding.floatingActionButton2.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = FragmentECSAjout.newInstance();
            fragmentTransaction.replace(((View)binding.getRoot().getParent()).getId(), fragment, fragment.toString());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.commit();
        });
        return binding.getRoot();
    }


    private void setupRecyclerView(ECS[] ecs) {
        ECSAdapter adapter = new ECSAdapter(ecs, new ECSViewHolderListener() {

            @Override
            public void onClick(ECS ecs) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = FragmentECSAjout.newInstance(ecs.nom);
                fragmentTransaction.replace(((View)binding.getRoot().getParent()).getId(), fragment, fragment.toString());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
            }
        });
        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            adapter.update(releve.ecs.values().toArray(new ECS[0]));
        });
        binding.recyclerViewECS.setAdapter(adapter);
        binding.recyclerViewECS.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

}