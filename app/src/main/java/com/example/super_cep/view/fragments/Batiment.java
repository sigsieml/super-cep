package com.example.super_cep.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.super_cep.databinding.FragmentBatimentBinding;
import com.example.super_cep.view.ReleveViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Batiment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Batiment extends Fragment {

    public Batiment() {
        // Required empty public constructor
    }

    FragmentBatimentBinding binding;

    ReleveViewModel releve;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBatimentBinding.inflate(inflater, container, false);
        releve = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        binding.editTextNomBatiment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                releve.setNomBatiment(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.editTextNumberDecimalSurfaceTotalChauffe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    releve.setSurfaceTotaleChauffe(0);
                    return;
                }
                try {
                    releve.setSurfaceTotaleChauffe(Float.parseFloat(s.toString()));
                }catch (Exception e){
                    Snackbar.make(binding.getRoot(), "Veuillez entrer un nombre valide", Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.editTextMultiLineDescriptionBatiment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                releve.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.editTextMultiLineAdresse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                releve.setAdresse(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return binding.getRoot();
    }


}