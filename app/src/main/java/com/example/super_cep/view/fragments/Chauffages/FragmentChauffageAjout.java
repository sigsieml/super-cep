package com.example.super_cep.view.fragments.Chauffages;

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
import com.example.super_cep.databinding.FragmentChauffageAjoutBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.model.Export.PowerpointExporter;
import com.example.super_cep.model.Releve.Chauffage.CategorieChauffage;
import com.example.super_cep.model.Releve.Chauffage.Chauffage;
import com.example.super_cep.model.Releve.Chauffage.ChauffageCentraliser;
import com.example.super_cep.model.Releve.Chauffage.ChauffageDecentraliser;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.view.Mode;
import com.example.super_cep.view.includeView.ViewPhoto;
import com.example.super_cep.view.includeView.ViewZoneSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentChauffageAjout extends Fragment {


    private static final String ARG_NOM_CHAUFFAGE = "NOM_CHAUFFAGE";
    private static final String ARG2_NOM_ZONE = "NOM_ZONE";
    private String nomChauffage;
    private boolean isCentraliser = true;
    private String nomZone;
    private Mode mode = Mode.Ajout;
    public FragmentChauffageAjout() {}

    public static FragmentChauffageAjout newInstance() {
        return newInstance(null,null);
    }
    public static FragmentChauffageAjout newInstance(String nomZone, String nomChauffage) {
        FragmentChauffageAjout fragment = new FragmentChauffageAjout();
        Bundle args = new Bundle();
        args.putString(ARG_NOM_CHAUFFAGE, nomChauffage);
        args.putString(ARG2_NOM_ZONE, nomZone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getString(ARG_NOM_CHAUFFAGE) != null){
            mode = Mode.Edition;
            nomChauffage = getArguments().getString(ARG_NOM_CHAUFFAGE);
        }else{
            mode = Mode.Ajout;
            if(getArguments().getString(ARG2_NOM_ZONE) != null){
                nomZone = getArguments().getString(ARG2_NOM_ZONE);
                isCentraliser = false;
            }
        }

    }
    private FragmentChauffageAjoutBinding binding;
    private ReleveViewModel releveViewModel;
    private ConfigDataViewModel configDataViewModel;
    List<String> typeChauffageProducteur = new ArrayList<>();
    List<String> typeChauffageEmetteur = new ArrayList<>();
    List<String> typeChauffageProducteurEmetteur = new ArrayList<>();
    private ViewPhoto viewPhoto;
    private ViewZoneSelector viewZoneSelector;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChauffageAjoutBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        configDataViewModel = new ViewModelProvider(requireActivity()).get(ConfigDataViewModel.class);
        Map<String, List<String>> spinnerLiveData = configDataViewModel.getSpinnerData().getValue();
        if(!spinnerLiveData.containsKey("typeChauffageEmetteur") || !spinnerLiveData.containsKey("typeChauffageProducteur")){
            Toast.makeText(getContext(), "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
            back();
            return binding.getRoot();
        }
        typeChauffageEmetteur = spinnerLiveData.get("typeChauffageEmetteur");
        typeChauffageProducteur = spinnerLiveData.get("typeChauffageProducteur");
        typeChauffageProducteurEmetteur = spinnerLiveData.get("typeChauffageProducteurEmetteur");
        viewPhoto = new ViewPhoto(binding.includeViewPhoto, this);
        viewPhoto.setupPhotoLaunchers();
        viewZoneSelector = new ViewZoneSelector(binding.includeZoneSelection, releveViewModel);


        if(mode == Mode.Ajout){
            prefillChauffageName();
            addFooterAjout();
        }

        try {
            if(mode == Mode.Edition){
                Chauffage chauffage = releveViewModel.getReleve().getValue().chauffages.get(nomChauffage);
                isCentraliser = chauffage instanceof ChauffageCentraliser;
                if(!isCentraliser) nomZone = ((ChauffageDecentraliser) chauffage).zone;
                setModeEdition(chauffage);
                addDataToView(chauffage);

            }
        }catch (Exception e){
            Log.e("Ajout chauffage", "onCreateView: ", e);
            Toast.makeText(getContext(), "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
            back();
        }
        if(!isCentraliser) {
            binding.includeZoneSelection.getRoot().setVisibility(View.GONE);
            if(mode == Mode.Ajout){
                binding.textViewZoneConcerne.setText(binding.textViewZoneConcerne.getText().toString() + " : " + nomZone);
            }
        }
        updateSpinner();

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
                addChauffageToReleve();
            }
        });

        binding.linearLayoutAjoutChauffage.addView(viewFooter.getRoot());
    }

    private void setModeEdition(Chauffage chauffage) {
        binding.textViewTitleChauffage.setText(chauffage.nom);
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
                editChauffage();
            }
        });

        viewFooter.buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releveViewModel.removeChauffage(nomChauffage);
                back();
            }
        });

        binding.linearLayoutAjoutChauffage.addView(viewFooter.getRoot());
    }


    private void addChauffageToReleve() {
        try {
            releveViewModel.addChauffage(getChauffageFromViews());
            back();

        }catch (Exception e){
            Log.e("Chauffage", "addChauffageToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void editChauffage(){
        try {
            releveViewModel.editChauffage(nomChauffage, getChauffageFromViews());
            back();
        } catch (Exception e) {
            Log.e("Chauffage", "addChauffageToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }


    private void updateSpinner() {
        List<String> customList = new ArrayList<>();
        if(isCentraliser){
            customList.addAll(typeChauffageProducteur);
        }else{
            customList.addAll(typeChauffageEmetteur);
            customList.addAll(typeChauffageProducteurEmetteur);
        }
        configDataViewModel.setAutoComplete(binding.autoCompleteTypeChauffage,"typeChauffageProducteurEmetteur", customList);
        List<String> listRegulation = new ArrayList<>();
        listRegulation.add(PowerpointExporter.TEXT_ABSENCE_REGULATION);
        listRegulation.addAll(configDataViewModel.getSpinnerData().getValue().get("regulationChauffage"));
        configDataViewModel.setAutoComplete(binding.autoCompleteRegulations, "regulationChauffage", listRegulation);

        binding.autoCompleteTypeChauffage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                switch (getCategorieChauffage()){
                    case Emetteur:
                        configDataViewModel.setAutoComplete(binding.autoCompleteMarque, "marqueEmetteur");
                        break;
                    case Producteur:
                        configDataViewModel.setAutoComplete(binding.autoCompleteMarque, "marqueProducteur");
                        break;
                    case ProducteurEmetteur:
                        configDataViewModel.setAutoComplete(binding.autoCompleteMarque, "marqueProducteurEmetteur");
                        break;
                    default:
                        throw new IllegalArgumentException("Categorie de chauffage inconnue");
                }
            }

        });
    }


    private CategorieChauffage getCategorieChauffage(){
        if(typeChauffageProducteur.contains(binding.autoCompleteTypeChauffage.getText().toString())){
            return CategorieChauffage.Producteur;
        }else if(typeChauffageEmetteur.contains(binding.autoCompleteTypeChauffage.getText().toString())){
            return CategorieChauffage.Emetteur;
        }else{
            return CategorieChauffage.ProducteurEmetteur;
        }
    }

    private Chauffage getChauffageFromViews(){
        List<String> images = new ArrayList<>();
        for(Uri uri : viewPhoto.getUriImages()){
            images.add(uri.toString());
        }
        if(isCentraliser){
            if(viewZoneSelector.getSelectedZones().isEmpty()){
                throw new IllegalArgumentException("Veuillez sélectionner au moins une zone");
            }
            Chauffage chauffage = new ChauffageCentraliser(
                    binding.editTextNomChauffage.getText().toString(),
                    binding.autoCompleteTypeChauffage.getText().toString(),
                    binding.editTextNumberPuissance.getText().toString().isEmpty() ? 0 : Float.parseFloat(binding.editTextNumberPuissance.getText().toString()),
                    binding.editTextNumberQuantite.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.editTextNumberQuantite.getText().toString()),
                    binding.autoCompleteMarque.getText().toString(),
                    binding.editTextModele.getText().toString(),
                    viewZoneSelector.getSelectedZones(),
                    getCategorieChauffage(),
                    binding.autoCompleteRegulations.getText().toString(),
                    images,
                    binding.checkBoxAVerifierChauffage.isChecked(),
                    binding.editTextMultilineNoteChauffage.getText().toString()
            );
            return chauffage;
        }else{
            Chauffage chauffage = new ChauffageDecentraliser(
                    binding.editTextNomChauffage.getText().toString(),
                    binding.autoCompleteTypeChauffage.getText().toString(),
                    binding.editTextNumberPuissance.getText().toString().isEmpty() ? 0 : Float.parseFloat(binding.editTextNumberPuissance.getText().toString()),
                    binding.editTextNumberQuantite.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.editTextNumberQuantite.getText().toString()),
                    binding.autoCompleteMarque.getText().toString(),
                    binding.editTextModele.getText().toString(),
                    nomZone,
                    getCategorieChauffage(),
                    binding.autoCompleteRegulations.getText().toString(),
                    images,
                    binding.checkBoxAVerifierChauffage.isChecked(),
                    binding.editTextMultilineNoteChauffage.getText().toString()
            );
            return chauffage;
        }


    }

    private void addDataToView(Chauffage chauffage){
        binding.editTextNomChauffage.setText(chauffage.nom);
        binding.autoCompleteTypeChauffage.setText(chauffage.type);
        binding.editTextNumberPuissance.setText(String.valueOf(chauffage.puissance));
        binding.editTextNumberQuantite.setText(String.valueOf(chauffage.quantite));
        binding.autoCompleteMarque.setText(chauffage.marque);
        binding.editTextModele.setText(chauffage.modele);
        binding.autoCompleteRegulations.setText(chauffage.regulation);
        binding.checkBoxAVerifierChauffage.setChecked(chauffage.aVerifier);
        binding.editTextMultilineNoteChauffage.setText(chauffage.note);

        if(isCentraliser){
            if(chauffage instanceof ChauffageCentraliser){
                viewZoneSelector.setSelectedZones(((ChauffageCentraliser) chauffage).zones);
            }
        }

        for (String uri : chauffage.images) {
            viewPhoto.addPhotoToView(Uri.parse(uri));
        }
    }
    private void prefillChauffageName() {
        int index = 1;
        String element  = "Chauffage ";
        Releve releve =releveViewModel.getReleve().getValue();
        while(releve.chauffages.containsKey(element + index)){
            index++;
        }
        binding.editTextNomChauffage.setText(element + index);

    }

    private void back(){
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.popBackStack();
    }
}