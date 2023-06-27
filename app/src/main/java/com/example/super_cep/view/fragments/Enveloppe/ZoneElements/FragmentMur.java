package com.example.super_cep.view.fragments.Enveloppe.ZoneElements;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.databinding.FragmentMurBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.databinding.ViewImageZoneElementBinding;
import com.example.super_cep.databinding.ViewPhotoBinding;
import com.example.super_cep.model.Releve.Enveloppe.Mur;
import com.example.super_cep.model.Releve.Enveloppe.ZoneElement;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.view.Mode;
import com.example.super_cep.view.includeView.ViewPhoto;

import java.util.ArrayList;
import java.util.List;

public class FragmentMur extends Fragment {

    private static final String NOM_ZONE = "nomZone";

    private static final String NOM_ELEMENT = "nomElement";

    private static final String NOM_ANCIENNE_ZONE = "nomAncienneZone";
    private String nomZone;
    private String nomElement;

    private String nomAncienneZone;

    private Mode mode = Mode.Ajout;
    private PhotoManager photoManager;
    public FragmentMur() {
        // Required empty public constructor
    }

    public static FragmentMur newInstance(String nomZone) {
        return newInstance(nomZone, null, null);
    }

    public static FragmentMur newInstance(String nomZone, String nomElement){
        return newInstance(nomZone, null, nomElement);
    }

    public static FragmentMur newInstance(String nouvelleZone, String ancienneZone, String nomElement) {
        FragmentMur fragment = new FragmentMur();
        Bundle args = new Bundle();
        args.putString(NOM_ZONE, nouvelleZone);
        args.putString(NOM_ELEMENT, nomElement);
        args.putString(NOM_ANCIENNE_ZONE, ancienneZone);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nomZone = requireArguments().getString(NOM_ZONE);
        if(getArguments().getString(NOM_ANCIENNE_ZONE) != null){
            mode = Mode.Edition;
            nomElement = getArguments().getString(NOM_ELEMENT);
            nomAncienneZone = getArguments().getString(NOM_ANCIENNE_ZONE);
        }else if(getArguments().getString(NOM_ELEMENT) != null){
            mode = Mode.Consultation;
            nomElement = getArguments().getString(NOM_ELEMENT);
        }

    }

    private FragmentMurBinding binding;

    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;

    private ActivityResultLauncher<Intent>  launcherGetPhoto;
    private ActivityResultLauncher<Intent> launcherCapturePhoto;

    private ViewPhoto viewPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMurBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        photoManager = new PhotoManager(getContext());
        updateSpinner();
        viewPhoto = new ViewPhoto(ViewPhotoBinding.bind(binding.includeViewPhoto.getRoot()), this);
        viewPhoto.setupPhotoLaunchers();

        if(mode == Mode.Ajout){
            prefillZoneElementName();
        }

        if(mode == Mode.Ajout || mode == Mode.Edition){
            addFooterAjout();
        }

        try {
            if(mode == Mode.Consultation){
                ZoneElement zoneElement = releveViewModel.getReleve().getValue().getZone(nomZone).getZoneElement(nomElement);
                setMondeConsultation(zoneElement);
                addDataToView(zoneElement);
            }
            if(mode == Mode.Edition){
                ZoneElement zoneElement = releveViewModel.getReleve().getValue().getZone(nomAncienneZone).getZoneElement(nomElement);
                addDataToView(zoneElement);
            }
        }catch (Exception e){
            Log.e("AjoutZoneElement", "onCreateView: ", e);
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
                addZoneElementToReleve();
            }
        });

        binding.linearLayoutAjoutMur.addView(viewFooter.getRoot());
    }

    private void setMondeConsultation(ZoneElement zoneElement) {
        binding.textViewTitleMur.setText(zoneElement.getNom());
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
                editZoneElement();
            }
        });

        viewFooter.buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releveViewModel.removeZoneElement(nomZone, nomElement);
                back();
            }
        });

        binding.linearLayoutAjoutMur.addView(viewFooter.getRoot());
    }




    private void addZoneElementToReleve() {
        try {
            releveViewModel.getReleve().getValue().getZone(nomZone).addZoneElement(getZoneElementFromViews());
            releveViewModel.forceUpdateReleve();
            back();
            back();

        }catch (Exception e){
            Toast.makeText(getContext(), "Impossible d'ajouter l'élément", Toast.LENGTH_SHORT).show();
        }
    }

    private void editZoneElement(){
        try {
            releveViewModel.editZoneElement(nomElement, nomZone, getZoneElementFromViews());
            back();
        } catch (Exception e) {
            Log.e("AjoutZoneElement", "editZoneElement: ", e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void back(){
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.popBackStack();
    }

    private void updateSpinner() {
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);
        spinnerDataViewModel.setAutoComplete(binding.autoCompleteTypeMur, "typeMur");
        spinnerDataViewModel.setAutoComplete(binding.autoCompleteTypeDeMiseEnOeuvre, "typeDeMiseEnOeuvre");

        binding.autoCompleteTypeDeMiseEnOeuvre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("Aucun")){
                    binding.tableRowIsolant.setVisibility(View.GONE);
                    binding.constraintLayoutNiveauIsolation.setVisibility(View.GONE);
                }else{
                    binding.tableRowIsolant.setVisibility(View.VISIBLE);
                    binding.constraintLayoutNiveauIsolation.setVisibility(View.VISIBLE);
                }
            }
        });
        spinnerDataViewModel.setAutoComplete(binding.autoCompleteTypeIsolant, "typeIsolant");
        spinnerDataViewModel.setAutoComplete(binding.autoCompleteNiveauIsolation, "niveauIsolation");
    }


    private ZoneElement getZoneElementFromViews() {
        List<String> images = new ArrayList<>();
        for (Uri uri : viewPhoto.getUriImages()) {
            images.add(uri.toString());
        }
        Mur mur = new Mur(
                binding.editTextNomMur.getText().toString(),
                binding.autoCompleteTypeMur.getText().toString(),
                binding.autoCompleteTypeDeMiseEnOeuvre.getText().toString(),
                binding.autoCompleteTypeIsolant.getText().toString(),
                binding.autoCompleteNiveauIsolation.getText().toString(),
                Float.parseFloat(binding.editTextNumberEpaisseurIsolant.getText().toString()),
                binding.checkBoxAVerifierMur.isChecked(),
                binding.editTextMultilineNoteMur.getText().toString(),
                images
        );
        return mur;
    }

    private void addDataToView(ZoneElement zoneElement){
        Mur mur = (Mur) zoneElement;
        binding.editTextNomMur.setText(mur.getNom());
        binding.editTextNumberEpaisseurIsolant.setText(String.valueOf(mur.epaisseurIsolant));
        binding.autoCompleteTypeMur.setText(mur.typeMur);
        binding.autoCompleteTypeDeMiseEnOeuvre.setText(mur.typeMiseEnOeuvre);
        binding.autoCompleteTypeIsolant.setText(mur.typeIsolant);
        binding.autoCompleteNiveauIsolation.setText(mur.niveauIsolation);
        binding.checkBoxAVerifierMur.setChecked(mur.aVerifier);
        binding.editTextMultilineNoteMur.setText(mur.note);

        for (String uri : mur.images) {
            viewPhoto.addPhotoToView(Uri.parse(uri));
        }
    }


    private void prefillZoneElementName() {
        int index = 1;
        String element  = "Mur ";
        Releve releve =releveViewModel.getReleve().getValue();
        while(releve.getZone(nomZone).getZoneElement(element + index) != null){
            index++;
        }
        binding.editTextNomMur.setText(element + index);

    }

}