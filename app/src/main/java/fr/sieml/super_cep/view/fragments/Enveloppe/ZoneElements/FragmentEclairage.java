package fr.sieml.super_cep.view.fragments.Enveloppe.ZoneElements;

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

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.controller.PhotoManager;
import fr.sieml.super_cep.controller.ReleveViewModel;
import fr.sieml.super_cep.controller.ConfigDataViewModel;
import fr.sieml.super_cep.databinding.FragmentEclairageBinding;
import fr.sieml.super_cep.databinding.ViewFooterZoneElementBinding;
import fr.sieml.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import fr.sieml.super_cep.databinding.ViewPhotoBinding;
import fr.sieml.super_cep.model.Export.PowerpointExporter;
import fr.sieml.super_cep.model.Releve.Enveloppe.Eclairage;
import fr.sieml.super_cep.model.Releve.ZoneElement;
import fr.sieml.super_cep.view.Mode;
import fr.sieml.super_cep.view.includeView.ViewPhoto;

import java.util.ArrayList;
import java.util.List;

public class FragmentEclairage extends Fragment {

    private static final String NOM_ZONE = "nomZone";
    private static final String NOM_ELEMENT = "nomElement";
    private String nomZone;
    private String nomElement;
    private Mode mode = Mode.Ajout;
    private PhotoManager photoManager;
    public FragmentEclairage() {}
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

    private FragmentEclairageBinding binding;

    private ReleveViewModel releveViewModel;
    private ConfigDataViewModel configDataViewModel;

    private ViewPhoto viewPhoto;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEclairageBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        photoManager = new PhotoManager(getContext());
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

        binding.linearLayoutAjoutEclairage.addView(viewFooter.getRoot());
    }

    private void setMondeConsultation(ZoneElement zoneElement) {
        binding.textViewTitleEclairage.setText(zoneElement.nom);
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

        binding.linearLayoutAjoutEclairage.addView(viewFooter.getRoot());
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
        configDataViewModel.setAutoComplete(binding.autoCompleteTypeEclairage, "typeEclairage");
        List<String> typeRegulation = new ArrayList<>();
        typeRegulation.add(PowerpointExporter.TEXT_ABSENCE_REGULATION);
        typeRegulation.addAll(configDataViewModel.getSpinnerData().getValue().get("typeRegulation"));
        configDataViewModel.setAutoComplete(binding.autoCompleteTypeRegulation, "typeRegulation",typeRegulation);
    }


    private ZoneElement getZoneElementFromViews() {
        List<String> images = new ArrayList<>();
        for (Uri uri : viewPhoto.getUriImages()) {
            images.add(uri.toString());
        }
        Eclairage eclairage = new Eclairage(
                binding.editTextNomEclairage.getText().toString(),
                binding.autoCompleteTypeEclairage.getText().toString(),
                binding.autoCompleteTypeRegulation.getText().toString(),
                binding.checkBoxAVerifierEclairage.isChecked(),
                binding.editTextMultilineNoteEclairage.getText().toString(),
                images
        );
        return eclairage;
    }

    private void addDataToView(ZoneElement zoneElement){
        Eclairage eclairage = (Eclairage) zoneElement;
        binding.editTextNomEclairage.setText(eclairage.nom);
        binding.autoCompleteTypeEclairage.setText(eclairage.typeEclairage);
        binding.autoCompleteTypeRegulation.setText(eclairage.typeDeRegulation);
        binding.checkBoxAVerifierEclairage.setChecked(eclairage.aVerifier);
        binding.editTextMultilineNoteEclairage.setText(eclairage.note);

        for (String uri : eclairage.images) {
            viewPhoto.addPhotoToView(Uri.parse(uri));
        }
    }

    private void prefillZoneElementName() {
        binding.editTextNomEclairage.setText(releveViewModel.getNextNameForZoneElement( "Eclairage "));
    }
}