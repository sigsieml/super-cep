package fr.sieml.super_cep.view.fragments.Enveloppe.ZoneElements;

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

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.databinding.FragmentMurBinding;
import fr.sieml.super_cep.databinding.ViewFooterZoneElementBinding;
import fr.sieml.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import fr.sieml.super_cep.databinding.ViewPhotoBinding;
import fr.sieml.super_cep.model.Export.PowerpointExporter;
import fr.sieml.super_cep.model.Releve.Enveloppe.Mur;
import fr.sieml.super_cep.model.Releve.ZoneElement;
import fr.sieml.super_cep.controller.ReleveViewModel;
import fr.sieml.super_cep.controller.ConfigDataViewModel;
import fr.sieml.super_cep.view.Mode;
import fr.sieml.super_cep.view.includeView.ViewPhoto;

import java.util.ArrayList;
import java.util.List;

public class FragmentMur extends Fragment {

    private static final String NOM_ZONE = "nomZone";
    private static final String NOM_ELEMENT = "nomElement";
    private String nomZone;
    private String nomElement;
    private Mode mode = Mode.Ajout;
    public FragmentMur() {}
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

    private FragmentMurBinding binding;

    private ReleveViewModel releveViewModel;
    private ConfigDataViewModel configDataViewModel;
    private ViewPhoto viewPhoto;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMurBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        updateSpinner();
        viewPhoto = new ViewPhoto(ViewPhotoBinding.bind(binding.includeViewPhoto.getRoot()), this);
        viewPhoto.setupPhotoLaunchers();

        if(mode == Mode.Ajout){
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

        binding.linearLayoutAjoutMur.addView(viewFooter.getRoot());
    }

    private void setMondeConsultation(ZoneElement zoneElement) {
        binding.textViewTitleMur.setText(zoneElement.nom);
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
        configDataViewModel.setAutoComplete(binding.autoCompleteTypeMur, "typeMur");

        List<String> listTypeMiseEnOeuvre = new ArrayList<>();
        listTypeMiseEnOeuvre.add(PowerpointExporter.TEXT_AUCUN_ISOLANT);
        listTypeMiseEnOeuvre.addAll(configDataViewModel.getSpinnerData().getValue().get("typeDeMiseEnOeuvre"));
        configDataViewModel.setAutoComplete(binding.autoCompleteTypeDeMiseEnOeuvre, "typeDeMiseEnOeuvre",listTypeMiseEnOeuvre);

        binding.autoCompleteTypeDeMiseEnOeuvre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(PowerpointExporter.TEXT_AUCUN_ISOLANT)){
                    binding.tableRowIsolant.setVisibility(View.GONE);
                    binding.constraintLayoutNiveauIsolation.setVisibility(View.GONE);
                }else{
                    binding.tableRowIsolant.setVisibility(View.VISIBLE);
                    binding.constraintLayoutNiveauIsolation.setVisibility(View.VISIBLE);
                }
            }
        });
        configDataViewModel.setAutoComplete(binding.autoCompleteTypeIsolant, "typeIsolant");
        configDataViewModel.setAutoComplete(binding.autoCompleteNiveauIsolation, "niveauIsolation");
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
                binding.editTextNumberEpaisseurIsolant.getText().length() == 0 ? Float.NaN : Float.parseFloat(binding.editTextNumberEpaisseurIsolant.getText().toString()),
                binding.checkBoxAVerifierMur.isChecked(),
                binding.editTextMultilineNoteMur.getText().toString(),
                images
        );
        return mur;
    }

    private void addDataToView(ZoneElement zoneElement){
        Mur mur = (Mur) zoneElement;
        binding.editTextNomMur.setText(mur.nom);
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
        binding.editTextNomMur.setText(releveViewModel.getNextNameForZoneElement( "Mur "));
    }

}