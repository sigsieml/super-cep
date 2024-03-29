package fr.sieml.super_cep.view.fragments.Ventilation;

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
import fr.sieml.super_cep.databinding.FragmentVentilationBinding;
import fr.sieml.super_cep.model.Releve.Ventilation;
import fr.sieml.super_cep.model.Releve.Releve;

public class FragmentVentilation extends Fragment {

    public FragmentVentilation() {
        // Required empty public constructor
    }

    private FragmentVentilationBinding binding;

    private ReleveViewModel releveViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVentilationBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        Releve releve = releveViewModel.getReleve().getValue();

        setupRecyclerView(releve.ventilations.values().toArray(new Ventilation[0]));

        binding.floatingActionButton2.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            fr.sieml.super_cep.view.fragments.Ventilation.FragmentVentilationDirections.ActionNavVentilationToFragmentVentilationAjout action =
                    FragmentVentilationDirections.actionNavVentilationToFragmentVentilationAjout(null);
            navController.navigate(action);
        });
        return binding.getRoot();
    }


    private void setupRecyclerView(Ventilation[] ventilation) {
        VentilationAdapter adapter = new VentilationAdapter(ventilation, new VentilationViewHolderListener() {

            @Override
            public void onClick(Ventilation ventilation) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                fr.sieml.super_cep.view.fragments.Ventilation.FragmentVentilationDirections.ActionNavVentilationToFragmentVentilationAjout action =
                        FragmentVentilationDirections.actionNavVentilationToFragmentVentilationAjout(ventilation.nom);
                navController.navigate(action);
            }
        });
        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            adapter.update(releve.ventilations.values().toArray(new Ventilation[0]));
        });
        binding.recyclerViewVentilation.setAdapter(adapter);
        binding.recyclerViewVentilation.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}