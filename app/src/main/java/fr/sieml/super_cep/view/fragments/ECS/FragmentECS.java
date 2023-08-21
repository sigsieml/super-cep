package fr.sieml.super_cep.view.fragments.ECS;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.controller.ReleveViewModel;
import fr.sieml.super_cep.databinding.FragmentECSBinding;
import fr.sieml.super_cep.model.Releve.ECS;
import fr.sieml.super_cep.model.Releve.Releve;

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
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            FragmentECSDirections.ActionNavEcsToNavEcsAjout action =
                    FragmentECSDirections.actionNavEcsToNavEcsAjout(null);
            navController.navigate(action);
        });
        return binding.getRoot();
    }


    private void setupRecyclerView(ECS[] ecs) {
        ECSAdapter adapter = new ECSAdapter(ecs, new ECSViewHolderListener() {

            @Override
            public void onClick(ECS ecs) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                FragmentECSDirections.ActionNavEcsToNavEcsAjout action =
                        FragmentECSDirections.actionNavEcsToNavEcsAjout(ecs.nom);
                navController.navigate(action);
            }
        });
        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            adapter.update(releve.ecs.values().toArray(new ECS[0]));
        });
        binding.recyclerViewECS.setAdapter(adapter);
        binding.recyclerViewECS.setLayoutManager(new LinearLayoutManager(requireContext()));
    }



}