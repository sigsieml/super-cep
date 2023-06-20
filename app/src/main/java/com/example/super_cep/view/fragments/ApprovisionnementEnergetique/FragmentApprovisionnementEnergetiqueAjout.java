package com.example.super_cep.view.fragments.ApprovisionnementEnergetique;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.databinding.FragmentApprovisionnementEnergetiqueAjoutBinding;
import com.example.super_cep.databinding.ViewApprovisionnementEnergetiqueElectriqueBinding;
import com.example.super_cep.databinding.ViewApprovisionnementEnergetiqueGazBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.databinding.ViewholderApprovisionnementEnergetiqueBinding;
import com.example.super_cep.model.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.ApprovionnementEnergetique.ApprovisionnementEnergetiqueElectrique;
import com.example.super_cep.model.ApprovionnementEnergetique.ApprovisionnementEnergetiqueGaz;
import com.example.super_cep.view.Mode;
import com.example.super_cep.view.includeView.ViewPhoto;
import com.example.super_cep.view.includeView.ViewZoneSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentApprovisionnementEnergetiqueAjout extends Fragment {
    private static final String ARG_NOM_APPROVISIONNEMENT_ENERGETIQUE = "param2";

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
    private SpinnerDataViewModel spinnerDataViewModel;


    private ViewZoneSelector viewZoneSelector;
    private ViewPhoto viewPhoto;
    private LiveData<Map<String, List<String>>> spinnerLiveData;

    private ViewApprovisionnementEnergetiqueElectriqueBinding viewApprovisionnementEnergetiqueElectriqueBinding;
    private ViewApprovisionnementEnergetiqueGazBinding viewApprovisionnementEnergetiqueGazBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentApprovisionnementEnergetiqueAjoutBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);
        spinnerLiveData = spinnerDataViewModel.getSpinnerData();
        viewPhoto = new ViewPhoto(binding.includeViewPhoto, this);
        viewPhoto.setupPhotoLaunchers();
        updateSpinner();
        viewZoneSelector = new ViewZoneSelector(binding.includeZoneSelection, releveViewModel);

        setupViewStub();

        if(mode == Mode.Ajout){
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
                getParentFragmentManager().popBackStack();
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
                getParentFragmentManager().popBackStack();
            }
        });

        binding.linearLayoutAjoutApprovisionnementEnergetique.addView(viewFooter.getRoot());
    }


    private void addApprovisionnementEnergetiqueToReleve() {
        try {
            releveViewModel.addApprovisionnementEnergetique(getApprovisionnementEnergetiqueFromViews());
            getParentFragmentManager().popBackStack();

        }catch (Exception e){
            Log.e("ApprovisionnementEnergetique", "addApprovisionnementEnergetiqueToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    private void editApprovisionnementEnergetique(){
        try {
            releveViewModel.editApprovisionnementEnergetique(nomApprovisionnementEnergetique, getApprovisionnementEnergetiqueFromViews());
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Log.e("ApprovisionnementEnergetique", "addApprovisionnementEnergetiqueToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewStub() {
        binding.spinnerEnergie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TypeApprovisionnement typeApprovisionnement = getTypeApprovisionnement();
                if(typeApprovisionnement == TypeApprovisionnement.ELECTRICITE){
                    if(viewApprovisionnementEnergetiqueElectriqueBinding == null){
                        View inflated = binding.viewStubElectricite.inflate(); // inflate the layout resource
                        viewApprovisionnementEnergetiqueElectriqueBinding = ViewApprovisionnementEnergetiqueElectriqueBinding.bind(inflated);
                    }
                    viewApprovisionnementEnergetiqueElectriqueBinding.getRoot().setVisibility(View.VISIBLE);
                    spinnerDataViewModel.updateSpinnerData(viewApprovisionnementEnergetiqueElectriqueBinding.spinnerFormule, "formuleTarifaire");
                }else{
                    if(viewApprovisionnementEnergetiqueElectriqueBinding != null)
                        viewApprovisionnementEnergetiqueElectriqueBinding.getRoot().setVisibility(View.GONE);
                }

                if(typeApprovisionnement == TypeApprovisionnement.GAZ){
                    if(viewApprovisionnementEnergetiqueGazBinding == null){
                        View inflated = binding.viewStubGaz.inflate(); // inflate the layout resource
                        viewApprovisionnementEnergetiqueGazBinding = ViewApprovisionnementEnergetiqueGazBinding.bind(inflated);
                    }
                    viewApprovisionnementEnergetiqueGazBinding.getRoot().setVisibility(View.VISIBLE);
                }else{
                    if(viewApprovisionnementEnergetiqueGazBinding != null)
                        viewApprovisionnementEnergetiqueGazBinding.getRoot().setVisibility(View.GONE);
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
        spinnerDataViewModel.updateSpinnerData(binding.spinnerEnergie, customSpinnerData);
    }

    private ApprovisionnementEnergetique getApprovisionnementEnergetiqueFromViews(){
        switch (getTypeApprovisionnement()){
            case GAZ:
                return new ApprovisionnementEnergetiqueGaz(
                        binding.editTextNomApprovisionnementEnergetique.getText().toString(),
                        binding.spinnerEnergie.getSelectedItem().toString(),
                        ((EditText)binding.getRoot().findViewById(R.id.editTextRAE)).getText().toString(),
                        viewZoneSelector.getSelectedZones(),
                        viewPhoto.getUriImages(),
                        binding.checkBoxAVerifierApprovisionnementEnergetique.isChecked(),
                        binding.editTextMultilineNoteApprovisionnementEnergetique.getText().toString()
                );
                case ELECTRICITE:
                    String textPuissance = ((EditText) binding.getRoot().findViewById(R.id.editTextNumberSignedPuissance)).getText().toString();
                    return new ApprovisionnementEnergetiqueElectrique(
                            binding.editTextNomApprovisionnementEnergetique.getText().toString(),
                            binding.spinnerEnergie.getSelectedItem().toString(),
                            textPuissance.isEmpty() ? 0 : Float.parseFloat(textPuissance),
                            ((Spinner) binding.getRoot().findViewById(R.id.spinnerFormule)).getSelectedItem().toString(),
                            ((EditText) binding.getRoot().findViewById(R.id.editTextNumeroPDL)).getText().toString(),
                            viewZoneSelector.getSelectedZones(),
                            viewPhoto.getUriImages(),
                            binding.checkBoxAVerifierApprovisionnementEnergetique.isChecked(),
                            binding.editTextMultilineNoteApprovisionnementEnergetique.getText().toString()
                    );
            default:
                return new ApprovisionnementEnergetique(
                        binding.editTextNomApprovisionnementEnergetique.getText().toString(),
                        binding.spinnerEnergie.getSelectedItem().toString(),
                        viewZoneSelector.getSelectedZones(),
                        viewPhoto.getUriImages(),
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

        for (Uri uri : approvisionnementEnergetique.uriImages) {
            viewPhoto.addPhotoToView(uri);
        }
    }
}