package com.example.super_cep.view.fragments.Ventilation;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.databinding.FragmentVentilationAjoutBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.model.Releve.Ventilation;
import com.example.super_cep.view.Mode;
import com.example.super_cep.view.includeView.ViewPhoto;
import com.example.super_cep.view.includeView.ViewZoneSelector;

import java.util.ArrayList;
import java.util.List;

public class FragmentVentilationAjout extends Fragment {

    private static final String ARG_NOM_VENTILATION = "param2";

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
    private SpinnerDataViewModel spinnerDataViewModel;


    private ViewZoneSelector viewZoneSelector;
    private ViewPhoto viewPhoto;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVentilationAjoutBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);
        viewPhoto = new ViewPhoto(binding.includeViewPhoto, this);
        viewPhoto.setupPhotoLaunchers();
        updateSpinner();
        viewZoneSelector = new ViewZoneSelector(binding.includeZoneSelection, releveViewModel);

        if(mode == Mode.Ajout){
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
            getParentFragmentManager().popBackStack();
        }

        return binding.getRoot();

    }

    private void addFooterAjout() {
        ViewFooterZoneElementBinding viewFooter = ViewFooterZoneElementBinding.inflate(getLayoutInflater());
        viewFooter.buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
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
                getParentFragmentManager().popBackStack();
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
                getParentFragmentManager().popBackStack();
            }
        });

        binding.linearLayoutAjoutVentilation.addView(viewFooter.getRoot());
    }


    private void addVentilationToReleve() {
        try {
            releveViewModel.addVentilation(getVentilationFromViews());
            getParentFragmentManager().popBackStack();

        }catch (Exception e){
            Log.e("Ventilation", "addVentilationToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    private void editVentilation(){
        try {
            releveViewModel.editVentilation(nomVentilation, getVentilationFromViews());
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Log.e("Ventilation", "addVentilationToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSpinner() {
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeRegulation, "regulationVentilation");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeVentilation, "typeVentilation");
    }

    private Ventilation getVentilationFromViews(){
        List<String> images = new ArrayList<>();
        for (Uri uri : viewPhoto.getUriImages()){
            images.add(uri.toString());
        }
        Ventilation ventilation = new Ventilation(
                binding.editTextNomVentilation.getText().toString(),
                binding.spinnerTypeVentilation.getSelectedItem().toString(),
                binding.spinnerTypeRegulation.getSelectedItem().toString(),
                viewZoneSelector.getSelectedZones(),
                images,
                binding.checkBoxAVerifierVentilation.isChecked(),
                binding.editTextMultilineNoteVentilation.getText().toString()
        );

        return ventilation;
    }

    private void addDataToView(Ventilation ventilation){
        binding.editTextNomVentilation.setText(ventilation.nom);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeVentilation, ventilation.type);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeRegulation, ventilation.regulation);
        binding.checkBoxAVerifierVentilation.setChecked(ventilation.aVerifier);
        binding.editTextMultilineNoteVentilation.setText(ventilation.note);
        viewZoneSelector.setSelectedZones(ventilation.zones);
        for (String uri : ventilation.images) {
            viewPhoto.addPhotoToView(Uri.parse(uri));
        }
    }
}