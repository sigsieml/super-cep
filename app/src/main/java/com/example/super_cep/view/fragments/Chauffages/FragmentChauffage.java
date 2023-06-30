package com.example.super_cep.view.fragments.Chauffages;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.databinding.FragmentChauffagesBinding;
import com.example.super_cep.model.Releve.Chauffage.Chauffage;
import com.example.super_cep.model.Releve.Chauffage.ChauffageCentraliser;
import com.example.super_cep.model.Releve.Chauffage.ChauffageDecentraliser;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.model.Releve.Zone;
import com.example.super_cep.model.Releve.ZoneElement;
import com.example.super_cep.view.fragments.Enveloppe.PopUpZone;
import com.example.super_cep.view.fragments.Enveloppe.PopUpZoneHandler;

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

        setupRecyclerView();

        binding.fabZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpZone.show(getContext(), new PopUpZoneHandler() {
                    @Override
                    public void nouvelleZone(String nomZone) {
                        try {
                            releveViewModel.addZone(new Zone(nomZone));
                        }catch (IllegalArgumentException e){
                            Toast.makeText(getContext(), "une zone avec le même nom existe déjà", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void editZone(String oldNomZone, String newNomZone) {
                        throw new UnsupportedOperationException();
                    }
                }, releveViewModel);
            }
        });

        binding.fabCentralise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                FragmentChauffageDirections.ActionNavChauffagesToNavChauffageAjout action =
                        FragmentChauffageDirections.actionNavChauffagesToNavChauffageAjout(null,null);
                navController.navigate(action);
            }
        });


        return binding.getRoot();
    }


    private void setupRecyclerView() {
        ChauffagesAdapter adapter = new ChauffagesAdapter(getViewLifecycleOwner(), releveViewModel, new ZoneChauffageHandler() {
            @Override
            public void nouvelleElementZone(String zone) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                FragmentChauffageDirections.ActionNavChauffagesToNavChauffageAjout action =
                        FragmentChauffageDirections.actionNavChauffagesToNavChauffageAjout(zone,null);
                navController.navigate(action);
            }

            @Override
            public void voirZoneElement(String zone, Chauffage chauffage) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                FragmentChauffageDirections.ActionNavChauffagesToNavChauffageAjout action =
                        FragmentChauffageDirections.actionNavChauffagesToNavChauffageAjout(null,chauffage.nom);
                navController.navigate(action);
            }

            @Override
            public void moveZoneElement(String nomChauffage, String nomPreviousZone, String nomNewZone) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Déplacer ou copier ?");
                builder.setMessage("Voulez vous déplacer ou copier l'élément ?");
                builder.setPositiveButton("Déplacer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            releveViewModel.moveChauffage(nomChauffage, nomPreviousZone, nomNewZone);
                        }catch (Exception e){
                            Log.e("moveZoneElement", e.getMessage());
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Copier", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        releveViewModel.copieChauffage(nomChauffage,  nomNewZone);
                    }
                });
                builder.show();


            }

            @Override
            public void deleteZone(String zone) {
                releveViewModel.deleteZone(zone);
            }
        });

        binding.recyclerViewChauffages.setAdapter(adapter);
        binding.recyclerViewChauffages.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}