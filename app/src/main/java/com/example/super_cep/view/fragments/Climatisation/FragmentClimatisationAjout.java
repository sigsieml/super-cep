package com.example.super_cep.view.fragments.Climatisation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.databinding.FragmentClimatisationAjoutBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.databinding.ViewImageZoneElementBinding;
import com.example.super_cep.model.Climatisation;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.view.Mode;
import com.example.super_cep.view.includeView.ViewPhoto;
import com.example.super_cep.view.includeView.ViewZoneSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentClimatisationAjout extends Fragment {


    private static final String ARG_NOM_CLIMATISATION = "param2";

    private String nomClimatisation;


    private Mode mode = Mode.Ajout;
    public FragmentClimatisationAjout() {
        // Required empty public constructor
    }

    public static FragmentClimatisationAjout newInstance() {
        return newInstance(null);
    }


    public static FragmentClimatisationAjout newInstance(String nomClimatisation) {
        FragmentClimatisationAjout fragment = new FragmentClimatisationAjout();
        Bundle args = new Bundle();
        args.putString(ARG_NOM_CLIMATISATION, nomClimatisation);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getString(ARG_NOM_CLIMATISATION) != null){
            mode = Mode.Edition;
            nomClimatisation = getArguments().getString(ARG_NOM_CLIMATISATION);
        }

    }

    private FragmentClimatisationAjoutBinding binding;

    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;

    private ViewPhoto viewPhoto;
    private ViewZoneSelector viewZoneSelector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClimatisationAjoutBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);

        viewPhoto = new ViewPhoto(binding.includeViewPhoto, this);
        viewPhoto.setupPhotoLaunchers();
        viewZoneSelector = new ViewZoneSelector(binding.includeZoneSelection, releveViewModel);
        updateSpinner();

        if(mode == Mode.Ajout){
            addFooterAjout();
        }

        try {
            if(mode == Mode.Edition){
                Climatisation climatisation = releveViewModel.getReleve().getValue().climatisations.get(nomClimatisation);
                setModeEdition(climatisation);
                addDataToView(climatisation);
            }
        }catch (Exception e){
            Log.e("Ajout climatisation", "onCreateView: ", e);
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
                addClimatisationToReleve();
            }
        });

        binding.linearLayoutAjoutClimatisation.addView(viewFooter.getRoot());
    }

    private void setModeEdition(Climatisation climatisation) {
        binding.textViewTitleClimatisation.setText(climatisation.nom);
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
                editClimatisation();
            }
        });

        viewFooter.buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releveViewModel.removeClimatisation(nomClimatisation);
                getParentFragmentManager().popBackStack();
            }
        });

        binding.linearLayoutAjoutClimatisation.addView(viewFooter.getRoot());
    }






    private void addClimatisationToReleve() {
        try {
            releveViewModel.addClimatisation(getClimatisationFromViews());
            getParentFragmentManager().popBackStack();

        }catch (Exception e){
            Log.e("Climatisation", "addClimatisationToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    private void editClimatisation(){
        try {
            releveViewModel.editClimatisation(nomClimatisation, getClimatisationFromViews());
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Log.e("Climatisation", "addClimatisationToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




    private void updateSpinner() {
        spinnerDataViewModel.updateSpinnerData(binding.spinnerRegulations, "regulationClimatisation");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeClimatisation, "typeClimatisation");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerMarque, "marqueClimatisation");
    }

    private Climatisation getClimatisationFromViews(){
        List<String> images = new ArrayList<>();
        for(Uri uri : viewPhoto.getUriImages()){
            images.add(uri.toString());
        }
        Climatisation climatisation = new Climatisation(
                binding.editTextNomClimatisation.getText().toString(),
                binding.spinnerTypeClimatisation.getSelectedItem().toString(),
                binding.editTextNumberPuissance.getText().toString().isEmpty() ? 0 : Float.parseFloat(binding.editTextNumberPuissance.getText().toString()),
                binding.editTextNumberQuantite.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.editTextNumberQuantite.getText().toString()),
                binding.spinnerMarque.getSelectedItem().toString(),
                binding.editTextModele.getText().toString(),
                binding.spinnerRegulations.getSelectedItem().toString(),
                viewZoneSelector.getSelectedZones(),
                images,
                binding.checkBoxAVerifierClimatisation.isChecked(),
                binding.editTextMultilineNoteClimatisation.getText().toString()
        );

        return climatisation;
    }

    private void addDataToView(Climatisation climatisation){
        binding.editTextNomClimatisation.setText(climatisation.nom);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeClimatisation, climatisation.type);
        binding.editTextNumberPuissance.setText(String.valueOf(climatisation.puissance));
        binding.editTextNumberQuantite.setText(String.valueOf(climatisation.quantite));
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerMarque, climatisation.marque);
        binding.editTextModele.setText(climatisation.modele);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerRegulations, climatisation.regulation);
        binding.checkBoxAVerifierClimatisation.setChecked(climatisation.aVerifier);
        binding.editTextMultilineNoteClimatisation.setText(climatisation.note);
        viewZoneSelector.setSelectedZones(climatisation.zones);


        for (String uri : climatisation.images) {
            viewPhoto.addPhotoToView(Uri.parse(uri));
        }
    }
}