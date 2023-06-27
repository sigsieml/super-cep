package com.example.super_cep.view.fragments.UsageEtOccupation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.databinding.FragmentUeoCalendrierBinding;
import com.example.super_cep.databinding.ViewUsageEtOccupationJourBinding;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Releve;

import java.util.List;

public class FragmentUsageEtOccupationCalendrier extends Fragment {

    private static final String ARG_CALENDRIER = "nomCalendrier";
    private String nomCalendrier;

    public FragmentUsageEtOccupationCalendrier() {
        // Required empty public constructor
    }

    public static FragmentUsageEtOccupationCalendrier create(String param1) {
        FragmentUsageEtOccupationCalendrier fragment = new FragmentUsageEtOccupationCalendrier();
        Bundle args = new Bundle();
        args.putString(ARG_CALENDRIER, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nomCalendrier = requireArguments().getString(ARG_CALENDRIER);
    }

    FragmentUeoCalendrierBinding binding;

    ReleveViewModel releveViewModel;

    private boolean modeChauffage = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUeoCalendrierBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        Releve releve = releveViewModel.getReleve().getValue();
        Calendrier calendrier = releve.calendriers.get(nomCalendrier);
        binding.textViewNomCalendrier.setText(calendrier.nom);
        binding.textViewZoneCalendrier.setText(zonesToString(calendrier.zones));

        setupToggleButton();

        setupCalendrier(calendrier);

        return binding.getRoot();
    }

    private void setupCalendrier(Calendrier calendrier) {
        for (float i = 0; i < 24; i += 0.5) {
            ViewUsageEtOccupationJourBinding bindingViewJour = ViewUsageEtOccupationJourBinding.inflate(getLayoutInflater());
            bindingViewJour.textViewHeur.setText(String.valueOf((int)Math.floor(i)) + "h" + String.valueOf((int)((i % 1) * 60)));
            bindingViewJour.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(modeChauffage){
                        bindingViewJour.getRoot().setBackground(getResources().getDrawable(R.drawable.border_green));
                    }else{
                        bindingViewJour.getRoot().setBackground(getResources().getDrawable(R.drawable.border_teal));
                    }
                }
            });

            binding.linearLayoutJours.addView(bindingViewJour.getRoot());
        }
    }


    private String zonesToString(List<String> zones){
        StringBuilder stringBuilder = new StringBuilder("zones : ");
        for (String zone : zones) {
            //check if zone is in zones
            stringBuilder.append(zone);
            stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }

    private void setupToggleButton() {
        binding.toggleButtonChauffage.setChecked(true);
        binding.toggleButtonChauffage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toggleButtonOccupation.setChecked(!binding.toggleButtonChauffage.isChecked());
                modeChauffage = binding.toggleButtonChauffage.isChecked();
            }
        });

        binding.toggleButtonOccupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toggleButtonChauffage.setChecked(!binding.toggleButtonOccupation.isChecked());
                modeChauffage = binding.toggleButtonChauffage.isChecked();
            }
        });


    }

}