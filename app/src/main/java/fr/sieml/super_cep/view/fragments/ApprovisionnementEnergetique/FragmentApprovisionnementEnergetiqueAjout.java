package fr.sieml.super_cep.view.fragments.ApprovisionnementEnergetique;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.controller.ReleveViewModel;
import fr.sieml.super_cep.controller.ConfigDataViewModel;
import fr.sieml.super_cep.databinding.FragmentApprovisionnementEnergetiqueAjoutBinding;
import fr.sieml.super_cep.databinding.ViewApprovisionnementEnergetiqueElectriqueBinding;
import fr.sieml.super_cep.databinding.ViewFooterZoneElementBinding;
import fr.sieml.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import fr.sieml.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import fr.sieml.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueElectrique;
import fr.sieml.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueGaz;
import fr.sieml.super_cep.model.Releve.Releve;
import fr.sieml.super_cep.view.Mode;
import fr.sieml.super_cep.view.includeView.ViewPhoto;
import fr.sieml.super_cep.view.includeView.ViewZoneSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentApprovisionnementEnergetiqueAjout extends Fragment {
    private static final String ARG_NOM_APPROVISIONNEMENT_ENERGETIQUE = "nomApprovisionnementEnergetique";

    private String nomApprovisionnementEnergetique;

    private Mode mode = Mode.Ajout;
    public FragmentApprovisionnementEnergetiqueAjout() {
        // Required empty public constructor
    }

    public static FragmentApprovisionnementEnergetiqueAjout newInstance() {
        return newInstance(null);
    }


    public static FragmentApprovisionnementEnergetiqueAjout newInstance(String nomApprovisionnementEnergetique) {
        FragmentApprovisionnementEnergetiqueAjout fragment = new FragmentApprovisionnementEnergetiqueAjout();
        Bundle args = new Bundle();
        args.putString(ARG_NOM_APPROVISIONNEMENT_ENERGETIQUE, nomApprovisionnementEnergetique);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getString(ARG_NOM_APPROVISIONNEMENT_ENERGETIQUE) != null){
            mode = Mode.Edition;
            nomApprovisionnementEnergetique = getArguments().getString(ARG_NOM_APPROVISIONNEMENT_ENERGETIQUE);
        }

    }

    private FragmentApprovisionnementEnergetiqueAjoutBinding binding;

    private ReleveViewModel releveViewModel;
    private ConfigDataViewModel configDataViewModel;


    private ViewZoneSelector viewZoneSelector;
    private ViewPhoto viewPhoto;
    private LiveData<Map<String, List<String>>> spinnerLiveData;

    private ViewApprovisionnementEnergetiqueElectriqueBinding viewApprovisionnementEnergetiqueElectriqueBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentApprovisionnementEnergetiqueAjoutBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        configDataViewModel = new ViewModelProvider(requireActivity()).get(ConfigDataViewModel.class);
        spinnerLiveData = configDataViewModel.getSpinnerData();
        viewPhoto = new ViewPhoto(binding.includeViewPhoto, this);
        viewPhoto.setupPhotoLaunchers();
        updateSpinner();
        viewZoneSelector = new ViewZoneSelector(binding.includeZoneSelection, releveViewModel);

        setupViewStub();

        if(mode == Mode.Ajout){
            prefillApprovisionnementEnergetiqueName();
            addFooterAjout();
        }

        try {
            if(mode == Mode.Edition){
                ApprovisionnementEnergetique approvisionnementEnergetique = releveViewModel.getReleve().getValue().approvisionnementEnergetiques.get(nomApprovisionnementEnergetique);
                setModeEdition(approvisionnementEnergetique);
                addDataToView(approvisionnementEnergetique);
            }
        }catch (Exception e){
            Log.e("Ajout approvisionnementEnergetique", "onCreateView: ", e);
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
                addApprovisionnementEnergetiqueToReleve();
            }
        });

        binding.linearLayoutAjoutApprovisionnementEnergetique.addView(viewFooter.getRoot());
    }

    private void setModeEdition(ApprovisionnementEnergetique approvisionnementEnergetique) {
        binding.textViewTitleApprovisionnementEnergetique.setText(approvisionnementEnergetique.nom);
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
                editApprovisionnementEnergetique();
            }
        });

        viewFooter.buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releveViewModel.removeApprovisionnementEnergetique(nomApprovisionnementEnergetique);
                back();
            }
        });

        binding.linearLayoutAjoutApprovisionnementEnergetique.addView(viewFooter.getRoot());
    }


    private void addApprovisionnementEnergetiqueToReleve() {
        try {
            releveViewModel.addApprovisionnementEnergetique(getApprovisionnementEnergetiqueFromViews());
            back();

        }catch (Exception e){
            Log.e("ApprovisionnementEnergetique", "addApprovisionnementEnergetiqueToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    private void editApprovisionnementEnergetique(){
        try {
            releveViewModel.editApprovisionnementEnergetique(nomApprovisionnementEnergetique, getApprovisionnementEnergetiqueFromViews());
            back();
        } catch (Exception e) {
            Log.e("ApprovisionnementEnergetique", "addApprovisionnementEnergetiqueToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewStub() {
        View inflated = binding.viewStubElectricite.inflate(); // inflate the layout resource
        viewApprovisionnementEnergetiqueElectriqueBinding = ViewApprovisionnementEnergetiqueElectriqueBinding.bind(inflated);
        viewApprovisionnementEnergetiqueElectriqueBinding.getRoot().setVisibility(View.GONE);
        binding.spinnerEnergie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TypeApprovisionnement typeApprovisionnement = getTypeApprovisionnement();
                if(typeApprovisionnement == TypeApprovisionnement.ELECTRICITE){
                    viewApprovisionnementEnergetiqueElectriqueBinding.getRoot().setVisibility(View.VISIBLE);
                    configDataViewModel.setAutoComplete(viewApprovisionnementEnergetiqueElectriqueBinding.autoCompleteFormule, "formuleTarifaire");
                }else{
                    if(viewApprovisionnementEnergetiqueElectriqueBinding != null)
                        viewApprovisionnementEnergetiqueElectriqueBinding.getRoot().setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private TypeApprovisionnement getTypeApprovisionnement(){
        String value = binding.spinnerEnergie.getSelectedItem().toString();
        Map<String, List<String>> spinnerData = spinnerLiveData.getValue();
        if(spinnerData.get("energieElectrique").contains(value))
            return TypeApprovisionnement.ELECTRICITE;
        if(spinnerData.get("energieGaz").contains(value))
            return TypeApprovisionnement.GAZ;
        return TypeApprovisionnement.AUTRE;
    }

    private void updateSpinner() {
        List<String> customSpinnerData = new ArrayList<>();
        Map<String, List<String>> spinnerData = spinnerLiveData.getValue();
        customSpinnerData.addAll(spinnerData.get("energieElectrique"));
        customSpinnerData.addAll(spinnerData.get("energieGaz"));
        customSpinnerData.addAll(spinnerData.get("energieAutre"));
        configDataViewModel.updateSpinnerData(binding.spinnerEnergie, customSpinnerData);
    }

    private ApprovisionnementEnergetique getApprovisionnementEnergetiqueFromViews(){
        List<String> images = new ArrayList<>();
        for(Uri uri : viewPhoto.getUriImages()){
            images.add(uri.toString());
        }
        switch (getTypeApprovisionnement()){
            case GAZ:
                return new ApprovisionnementEnergetiqueGaz(
                        binding.editTextNomApprovisionnementEnergetique.getText().toString(),
                        binding.spinnerEnergie.getSelectedItem().toString(),
                        viewZoneSelector.getSelectedZones(),
                        images,
                        binding.checkBoxAVerifierApprovisionnementEnergetique.isChecked(),
                        binding.editTextMultilineNoteApprovisionnementEnergetique.getText().toString()
                );
                case ELECTRICITE:
                    String textPuissance = ((EditText) binding.getRoot().findViewById(R.id.editTextNumberSignedPuissance)).getText().toString();
                    return new ApprovisionnementEnergetiqueElectrique(
                            binding.editTextNomApprovisionnementEnergetique.getText().toString(),
                            binding.spinnerEnergie.getSelectedItem().toString(),
                            textPuissance.isEmpty() ? Float.NaN : Float.parseFloat(textPuissance),
                            ((AutoCompleteTextView) binding.getRoot().findViewById(R.id.autoCompleteFormule)).getText().toString(),
                            viewZoneSelector.getSelectedZones(),
                            images,
                            binding.checkBoxAVerifierApprovisionnementEnergetique.isChecked(),
                            binding.editTextMultilineNoteApprovisionnementEnergetique.getText().toString()
                    );
            default:
                return new ApprovisionnementEnergetique(
                        binding.editTextNomApprovisionnementEnergetique.getText().toString(),
                        binding.spinnerEnergie.getSelectedItem().toString(),
                        viewZoneSelector.getSelectedZones(),
                        images,
                        binding.checkBoxAVerifierApprovisionnementEnergetique.isChecked(),
                        binding.editTextMultilineNoteApprovisionnementEnergetique.getText().toString()
                );

        }
    }

    private void addDataToView(ApprovisionnementEnergetique approvisionnementEnergetique){
        binding.editTextNomApprovisionnementEnergetique.setText(approvisionnementEnergetique.nom);
        viewZoneSelector.setSelectedZones(approvisionnementEnergetique.zones);
        binding.checkBoxAVerifierApprovisionnementEnergetique.setChecked(approvisionnementEnergetique.aVerifier);
        binding.editTextMultilineNoteApprovisionnementEnergetique.setText(approvisionnementEnergetique.note);
        configDataViewModel.setSpinnerSelection(binding.spinnerEnergie, approvisionnementEnergetique.energie);

        if(approvisionnementEnergetique instanceof ApprovisionnementEnergetiqueElectrique){
            ApprovisionnementEnergetiqueElectrique approvisionnementEnergetiqueElectrique = (ApprovisionnementEnergetiqueElectrique) approvisionnementEnergetique;
            viewApprovisionnementEnergetiqueElectriqueBinding.editTextNumberSignedPuissance.setText(String.valueOf(approvisionnementEnergetiqueElectrique.puissance));
            viewApprovisionnementEnergetiqueElectriqueBinding.autoCompleteFormule.setText(approvisionnementEnergetiqueElectrique.formuleTarifaire);
        }

        for (String path : approvisionnementEnergetique.images) {
            viewPhoto.addPhotoToView(Uri.parse(path));
        }
    }

    private void prefillApprovisionnementEnergetiqueName() {
        int index = 1;
        String element  = "ApprovisionnementEnergetique ";
        Releve releve =releveViewModel.getReleve().getValue();
        while(releve.approvisionnementEnergetiques.containsKey(element + index)){
            index++;
        }
        binding.editTextNomApprovisionnementEnergetique.setText(element + index);

    }

    private void back(){
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.popBackStack();
    }
}