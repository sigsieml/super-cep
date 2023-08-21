package fr.sieml.super_cep.view.fragments.Climatisation;

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
import fr.sieml.super_cep.databinding.FragmentClimatisationBinding;
import fr.sieml.super_cep.model.Releve.Climatisation;
import fr.sieml.super_cep.model.Releve.Releve;

public class FragmentClimatisation extends Fragment {

    public FragmentClimatisation() {
        // Required empty public constructor
    }

    private FragmentClimatisationBinding binding;

    private ReleveViewModel releveViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClimatisationBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        Releve releve = releveViewModel.getReleve().getValue();

        setupRecyclerView(releve.climatisations.values().toArray(new Climatisation[0]));

        binding.floatingActionButton2.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(FragmentClimatisationDirections.actionNavClimatisationToFragmentClimatisationAjout(null));
        });
        return binding.getRoot();
    }


    private void setupRecyclerView(Climatisation[] climatisation) {
        ClimatisationAdapter adapter = new ClimatisationAdapter(climatisation, new ClimatisationViewHolderListener() {
            @Override
            public void onClick(Climatisation climatisation) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                fr.sieml.super_cep.view.fragments.Climatisation.FragmentClimatisationDirections.ActionNavClimatisationToFragmentClimatisationAjout action =
                        FragmentClimatisationDirections.actionNavClimatisationToFragmentClimatisationAjout(climatisation.nom);
                navController.navigate(action);
            }
        });
        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            adapter.update(releve.climatisations.values().toArray(new Climatisation[0]));
        });
        binding.recyclerViewClimatisation.setAdapter(adapter);
        binding.recyclerViewClimatisation.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}