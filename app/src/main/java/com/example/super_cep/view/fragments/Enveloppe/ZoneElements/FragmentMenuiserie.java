package com.example.super_cep.view.fragments.Enveloppe.ZoneElements;

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
import com.example.super_cep.databinding.FragmentMenuiserieBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.databinding.ViewPhotoBinding;
import com.example.super_cep.model.Export.PowerpointExporter;
import com.example.super_cep.model.Releve.Enveloppe.Menuiserie;
import com.example.super_cep.model.Releve.ZoneElement;
import com.example.super_cep.view.Mode;
import com.example.super_cep.view.includeView.ViewPhoto;

import java.util.ArrayList;
import java.util.List;

public class FragmentMenuiserie extends Fragment {
    private static final String NOM_ZONE = "nomZone";
    private static final String NOM_ELEMENT = "nomElement";
    private String nomZone;
    private String nomElement;
    private Mode mode = Mode.Ajout;
    public FragmentMenuiserie() {}
    public static FragmentEclairage newInstance(String nomZone) {
        return newInstance(nomZone, null);
    }
    public static FragmentEclairage newInstance(String nomZone, String nomElement){
        FragmentEclairage fragment = new FragmentEclairage();
        Bundle args = new Bundle();
        args.putString(NOM_ZONE, nomZone);
        args.putString(NOM_ELEMENT, nomElement);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nomZone = requireArguments().getString(NOM_ZONE);
        if(getArguments().getString(NOM_ELEMENT) != null){
            mode = Mode.Edition;
            nomElement = getArguments().getString(NOM_ELEMENT);
        }
    }

    private FragmentMenuiserieBinding binding;
    private ReleveViewModel releveViewModel;
    private ConfigDataViewModel configDataViewModel;

    private ViewPhoto viewPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuiserieBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        updateSpinner();
        viewPhoto = new ViewPhoto(ViewPhotoBinding.bind(binding.includeViewPhoto.getRoot()), this);
        viewPhoto.setupPhotoLaunchers();


        if(mode  == Mode.Ajout){
            prefillZoneElementName();
            addFooterAjout();

        }

        try {
            if(mode == Mode.Edition){
                ZoneElement zoneElement = releveViewModel.getReleve().getValue().getZone(nomZone).getZoneElement(nomElement);
                setMondeConsultation(zoneElement);
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

        binding.linearLayoutAjoutMenuiserie.addView(viewFooter.getRoot());
    }

    private void setMondeConsultation(ZoneElement zoneElement) {
        binding.textViewTitleMenuiserie.setText(zoneElement.nom);
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

        binding.linearLayoutAjoutMenuiserie.addView(viewFooter.getRoot());
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
        configDataViewModel = new ViewModelProvider(requireActivity()).get(ConfigDataViewModel.class);
        configDataViewModel.setAutoComplete(binding.autoCompleteTypeMenuiserie, "typeMenuiserie");
        configDataViewModel.setAutoComplete(binding.autoCompleteMatRiau, "materiau");
        List<String> listProtectionsSolaires = new ArrayList<>();
        listProtectionsSolaires.add(PowerpointExporter.TEXT_AUCUNE_PROTECTION_SOLAIRE);
        listProtectionsSolaires.addAll(configDataViewModel.getSpinnerData().getValue().get("protectionsSolaires"));
        configDataViewModel.setAutoComplete(binding.autoCompleteProtectionsSolaires, "protectionsSolaires",listProtectionsSolaires);
        configDataViewModel.setAutoComplete(binding.autoCompleteTypeVitrage, "typeVitrage");
    }


    private ZoneElement getZoneElementFromViews() {
        List<String> images = new ArrayList<>();
        for(Uri uri : viewPhoto.getUriImages()){
            images.add(uri.toString());
        }
        Menuiserie menuiserie = new Menuiserie(
                binding.editTextNomMenuiserie.getText().toString(),
                binding.autoCompleteTypeMenuiserie.getText().toString(),
                binding.autoCompleteMatRiau.getText().toString(),
                binding.autoCompleteProtectionsSolaires.getText().toString(),
                binding.autoCompleteTypeVitrage.getText().toString(),
                binding.checkBoxAVerifierMenuiserie.isChecked(),
                binding.editTextMultilineNoteMenuiserie.getText().toString(),
                images
        );
        return menuiserie;
    }

    private void addDataToView(ZoneElement zoneElement){
        Menuiserie menuiserie = (Menuiserie) zoneElement;
        binding.editTextNomMenuiserie.setText(menuiserie.nom);
        binding.autoCompleteTypeMenuiserie.setText(menuiserie.typeMenuiserie);
        binding.autoCompleteMatRiau.setText(menuiserie.materiau);
        binding.autoCompleteProtectionsSolaires.setText(menuiserie.protectionsSolaires);
        binding.autoCompleteTypeVitrage.setText(menuiserie.typeVitrage);
        binding.checkBoxAVerifierMenuiserie.setChecked(menuiserie.aVerifier);
        binding.editTextMultilineNoteMenuiserie.setText(menuiserie.note);
        for (String uri : menuiserie.images) {
            viewPhoto.addPhotoToView(Uri.parse(uri));
        }
    }

    private void prefillZoneElementName() {
        binding.editTextNomMenuiserie.setText(releveViewModel.getNextNameForZoneElement( "Menuiserie "));
    }

}