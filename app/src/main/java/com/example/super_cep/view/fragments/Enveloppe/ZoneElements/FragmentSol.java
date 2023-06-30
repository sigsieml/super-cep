package com.example.super_cep.view.fragments.Enveloppe.ZoneElements;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.databinding.FragmentSolBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.databinding.ViewPhotoBinding;
import com.example.super_cep.model.Releve.Enveloppe.Sol;
import com.example.super_cep.model.Releve.ZoneElement;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.view.Mode;
import com.example.super_cep.view.includeView.ViewPhoto;

import java.util.ArrayList;
import java.util.List;

public class FragmentSol extends Fragment {

    private static final String NOM_ZONE = "nomZone";

    private static final String NOM_ELEMENT = "nomElement";

    private static final String NOM_ANCIENNE_ZONE = "nomAncienneZone";
    private String nomZone;
    private String nomElement;

    private String nomAncienneZone;

    private Mode mode = Mode.Ajout;
    private PhotoManager photoManager;
    public FragmentSol() {
        // Required empty public constructor
    }

    public static FragmentSol newInstance(String nomZone) {
        return newInstance(nomZone, null, null);
    }

    public static FragmentSol newInstance(String nomZone, String nomElement){
        return newInstance(nomZone, null, nomElement);
    }

    public static FragmentSol newInstance(String nouvelleZone,String ancienneZone, String nomElement) {
        FragmentSol fragment = new FragmentSol();
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

    private FragmentSolBinding binding;

    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;

    private ViewPhoto viewPhoto;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSolBinding.inflate(inflater, container, false);
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

        binding.linearLayoutAjoutSol.addView(viewFooter.getRoot());
    }

    private void setMondeConsultation(ZoneElement zoneElement) {
        binding.textViewTitleSol.setText(zoneElement.nom);
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

        binding.linearLayoutAjoutSol.addView(viewFooter.getRoot());
    }



    private void addZoneElementToReleve() {
        try {
            releveViewModel.addZoneElement(nomZone, getZoneElementFromViews());
            back();
            if(mode == Mode.Ajout){
                back();
            }
        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
        spinnerDataViewModel.setAutoComplete(binding.autoCompleteTypeSol, "typeSol");
        spinnerDataViewModel.setAutoComplete(binding.autoCompleteNiveauIsolation, "niveauIsolation");
        binding.autoCompleteNiveauIsolation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("Non isolé")){
                    binding.constraintLayoutNiveauIsolant.setVisibility(View.GONE);
                }else{
                    binding.constraintLayoutNiveauIsolant.setVisibility(View.VISIBLE);
                }
            }
        });
        spinnerDataViewModel.setAutoComplete(binding.autoCompleteIsolant, "typeIsolant");
    }


    private ZoneElement getZoneElementFromViews() {
        List<String> images = new ArrayList<>();
        for (Uri uri : viewPhoto.getUriImages()) {
            images.add(uri.toString());
        }
        Sol sol = new Sol(
                binding.editTextNomSol.getText().toString(),
                binding.autoCompleteTypeSol.getText().toString(),
                binding.autoCompleteNiveauIsolation.getText().toString(),
                binding.autoCompleteIsolant.getText().toString(),
                binding.editTextNumberSignedEpaisseurIsolant.length() == 0  ? Float.NaN : Float.parseFloat(binding.editTextNumberSignedEpaisseurIsolant.getText().toString()),
                binding.checkBoxAVerifierSol.isChecked(),
                binding.editTextMultilineNoteSol.getText().toString(),
                images
        );
        return sol;
    }

    private void addDataToView(ZoneElement zoneElement){
        Sol sol = (Sol) zoneElement;
        binding.editTextNomSol.setText(sol.nom);
        binding.editTextNumberSignedEpaisseurIsolant.setText(String.valueOf(sol.epaisseurIsolant));
        binding.autoCompleteTypeSol.setText(sol.typeSol);
        binding.autoCompleteNiveauIsolation.setText(sol.niveauIsolation);
        binding.autoCompleteIsolant.setText(sol.typeIsolant);
        binding.checkBoxAVerifierSol.setChecked(sol.aVerifier);
        binding.editTextMultilineNoteSol.setText(sol.note);

        for (String uri : sol.images) {
            viewPhoto.addPhotoToView(Uri.parse(uri));
        }
    }



    private void prefillZoneElementName() {
        binding.editTextNomSol.setText(releveViewModel.getNextNameForZoneElement( "Sol "));
    }
}