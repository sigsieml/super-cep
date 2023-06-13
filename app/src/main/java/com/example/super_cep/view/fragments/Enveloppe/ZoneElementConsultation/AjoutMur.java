package com.example.super_cep.view.fragments.Enveloppe.ZoneElementConsultation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.super_cep.R;
import com.example.super_cep.databinding.FragmentAjoutMurBinding;
import com.example.super_cep.model.Enveloppe.Mur;
import com.example.super_cep.view.SharedViewModelReleve;
import com.example.super_cep.view.SpinnerDataViewModel;
import com.example.super_cep.view.fragments.Enveloppe.AjoutElementZone;
import com.example.super_cep.view.fragments.Enveloppe.Enveloppe;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class AjoutMur extends Fragment {

    private static final String NOM_ZONE = "param1";
    private String nomZone;
    private AjoutMur() {
        // Required empty public constructor
    }

    public static AjoutMur newInstance(String nomZone) {
        AjoutMur fragment = new AjoutMur();
        Bundle args = new Bundle();
        args.putString(NOM_ZONE, nomZone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nomZone = requireArguments().getString(NOM_ZONE);
    }

    private FragmentAjoutMurBinding binding;

    private SharedViewModelReleve releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAjoutMurBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModelReleve.class);
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeMur, "typeMur");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeDeMiseEnOeuvre, "typeDeMiseEnOeuvre");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeIsolant, "typeIsolant");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerNiveauIsolation, "niveauIsolation");

        binding.buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        binding.buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMurToReleve();
            }
        });

        return binding.getRoot();

    }

    private void addMurToReleve() {
        try {

            Mur mur = new Mur(
                    binding.editTextNomMur.getText().toString(),
                    binding.spinnerTypeMur.getSelectedItem().toString(),
                    binding.spinnerTypeDeMiseEnOeuvre.getSelectedItem().toString(),
                    binding.spinnerTypeIsolant.getSelectedItem().toString(),
                    binding.spinnerNiveauIsolation.getSelectedItem().toString(),
                    Float.parseFloat(binding.editTextNumberEpaisseurIsolant.getText().toString()),
                    binding.checkBoxAVerifierMur.isChecked(),
                    binding.editTextMultilineNoteMur.getText().toString()
                    );

            releveViewModel.getReleve().getValue().getZone(nomZone).addZoneElement(mur);
            releveViewModel.forceUpdateReleve();
            getParentFragmentManager().popBackStack();
            getParentFragmentManager().popBackStack();

        }catch (Exception e){
            Snackbar.make(binding.getRoot(), "impossible d'ajouter le mur " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }



    }


}