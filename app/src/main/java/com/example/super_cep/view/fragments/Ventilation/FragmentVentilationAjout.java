package com.example.super_cep.view.fragments.Ventilation;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.ConfigDataViewModel;
import com.example.super_cep.databinding.FragmentVentilationAjoutBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.model.Export.PowerpointExporter;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.model.Releve.Ventilation;
import com.example.super_cep.view.Mode;
import com.example.super_cep.view.includeView.ViewPhoto;
import com.example.super_cep.view.includeView.ViewZoneSelector;

import java.util.ArrayList;
import java.util.List;

public class FragmentVentilationAjout extends Fragment {

    private static final String ARG_NOM_VENTILATION = "nomVentilation";

    private String nomVentilation;


    private Mode mode = Mode.Ajout;
    public FragmentVentilationAjout() {
        // Required empty public constructor
    }

    public static FragmentVentilationAjout newInstance() {
        return newInstance(null);
    }


    public static FragmentVentilationAjout newInstance(String nomVentilation) {
        FragmentVentilationAjout fragment = new FragmentVentilationAjout();
        Bundle args = new Bundle();
        args.putString(ARG_NOM_VENTILATION, nomVentilation);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getString(ARG_NOM_VENTILATION) != null){
            mode = Mode.Edition;
            nomVentilation = getArguments().getString(ARG_NOM_VENTILATION);
        }

    }

    private FragmentVentilationAjoutBinding binding;

    private ReleveViewModel releveViewModel;
    private ConfigDataViewModel configDataViewModel;


    private ViewZoneSelector viewZoneSelector;
    private ViewPhoto viewPhoto;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVentilationAjoutBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        configDataViewModel = new ViewModelProvider(requireActivity()).get(ConfigDataViewModel.class);
        viewPhoto = new ViewPhoto(binding.includeViewPhoto, this);
        viewPhoto.setupPhotoLaunchers();
        updateSpinner();
        viewZoneSelector = new ViewZoneSelector(binding.includeZoneSelection, releveViewModel);

        if(mode == Mode.Ajout){
            prefillVentilationName();
            addFooterAjout();
        }

        try {
            if(mode == Mode.Edition){
                Ventilation ventilation = releveViewModel.getReleve().getValue().ventilations.get(nomVentilation);
                setModeEdition(ventilation);
                addDataToView(ventilation);
            }
        }catch (Exception e){
            Log.e("Ajout ventilation", "onCreateView: ", e);
            Toast.makeText(getContext(), "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
            back();
        }

        return binding.getRoot();

    }

    private void addFooterAjout() {
        ViewFooterZoneElementBinding viewFooter = ViewFooterZoneElementBinding.inflate(getLayoutInflater());
        viewFooter.buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        viewFooter.buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVentilationToReleve();
            }
        });

        binding.linearLayoutAjoutVentilation.addView(viewFooter.getRoot());
    }

    private void setModeEdition(Ventilation ventilation) {
        binding.textViewTitleVentilation.setText(ventilation.nom);
        ViewFooterZoneElementConsultationBinding viewFooter = ViewFooterZoneElementConsultationBinding.inflate(getLayoutInflater());
        viewFooter.buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        viewFooter.buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editVentilation();
            }
        });

        viewFooter.buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releveViewModel.removeVentilation(nomVentilation);
                back();
            }
        });

        binding.linearLayoutAjoutVentilation.addView(viewFooter.getRoot());
    }


    private void addVentilationToReleve() {
        try {
            releveViewModel.addVentilation(getVentilationFromViews());
            back();

        }catch (Exception e){
            Log.e("Ventilation", "addVentilationToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    private void editVentilation(){
        try {
            releveViewModel.editVentilation(nomVentilation, getVentilationFromViews());
            back();
        } catch (Exception e) {
            Log.e("Ventilation", "addVentilationToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSpinner() {
        List<String> listTypeRegulation = new ArrayList<>();
        listTypeRegulation.add(PowerpointExporter.TEXT_ABSENCE_REGULATION);
        listTypeRegulation.addAll(configDataViewModel.getSpinnerData().getValue().get("regulationVentilation"));
        configDataViewModel.setAutoComplete(binding.autoCompleteTypeRegulation, "regulationVentilation",listTypeRegulation);
        configDataViewModel.setAutoComplete(binding.autoCompleteTypeVentilation, "typeVentilation");
    }

    private Ventilation getVentilationFromViews(){
        List<String> images = new ArrayList<>();
        for (Uri uri : viewPhoto.getUriImages()){
            images.add(uri.toString());
        }
        Ventilation ventilation = new Ventilation(
                binding.editTextNomVentilation.getText().toString(),
                binding.autoCompleteTypeVentilation.getText().toString(),
                binding.autoCompleteTypeRegulation.getText().toString(),
                viewZoneSelector.getSelectedZones(),
                images,
                binding.checkBoxAVerifierVentilation.isChecked(),
                binding.editTextMultilineNoteVentilation.getText().toString()
        );

        return ventilation;
    }

    private void addDataToView(Ventilation ventilation){
        binding.editTextNomVentilation.setText(ventilation.nom);
        binding.autoCompleteTypeVentilation.setText(ventilation.type);
        binding.autoCompleteTypeRegulation.setText(ventilation.regulation);
        binding.checkBoxAVerifierVentilation.setChecked(ventilation.aVerifier);
        binding.editTextMultilineNoteVentilation.setText(ventilation.note);
        viewZoneSelector.setSelectedZones(ventilation.zones);
        for (String uri : ventilation.images) {
            viewPhoto.addPhotoToView(Uri.parse(uri));
        }
    }

    private void prefillVentilationName() {
        int index = 1;
        String element  = "Ventilation ";
        Releve releve =releveViewModel.getReleve().getValue();
        while(releve.ventilations.containsKey(element + index)){
            index++;
        }
        binding.editTextNomVentilation.setText(element + index);

    }
    private void back(){
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.popBackStack();
    }
}