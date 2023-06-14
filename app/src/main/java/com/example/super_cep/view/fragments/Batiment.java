package com.example.super_cep.view.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.super_cep.databinding.FragmentBatimentBinding;
import com.example.super_cep.model.Releve;
import com.example.super_cep.view.ReleveViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Batiment extends Fragment {

    public Batiment() {
        // Required empty public constructor
    }

    FragmentBatimentBinding binding;

    ReleveViewModel releveViewModel;

    LiveData<Releve> releve;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBatimentBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        releve = releveViewModel.getReleve();
        releve.observe(getViewLifecycleOwner(), new Observer<Releve>() {
            @Override
            public void onChanged(Releve rlv) {
                binding.editTextDateDeRenovation.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(rlv.dateDeDerniereRenovation.getTime()));
                binding.editTextDateDeConstruction.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(rlv.dateDeConstruction.getTime()));
                binding.editTextNomBatiment.setText(rlv.nomBatiment);
                binding.editTextNumberDecimalSurfaceTotalChauffe.setText(String.valueOf(rlv.surfaceTotaleChauffe));
                binding.editTextMultiLineDescriptionBatiment.setText(rlv.description);
                binding.editTextMultiLineAdresse.setText(rlv.adresse);
                releve.removeObserver(this);
            }
        });
        setupCalendar();
        return binding.getRoot();
    }

    private void setupCalendar() {
        binding.editTextDateDeConstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();

                final Calendar calendar = releve.getValue().dateDeConstruction;
                new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,day);
                        updateTextBox(binding.editTextDateDeConstruction, calendar);
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        binding.editTextDateDeRenovation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();

                final Calendar calendar = releve.getValue().dateDeDerniereRenovation;
                new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,day);
                        updateTextBox(binding.editTextDateDeRenovation, calendar);
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateTextBox(EditText editTextDateDeConstruction, Calendar calendar) {
        editTextDateDeConstruction.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(calendar.getTime()));
    }


    private void saveData(){
        String newText;

        newText = binding.editTextMultiLineAdresse.getText().toString();
        if (!newText.equals(releve.getValue().adresse)) {
            releveViewModel.setAdresse(newText);
        }

        newText = binding.editTextMultiLineDescriptionBatiment.getText().toString();
        if (!newText.equals(releve.getValue().description)) {
            releveViewModel.setDescription(newText);
        }

        newText = binding.editTextNumberDecimalSurfaceTotalChauffe.getText().toString();
        if (!newText.equals(Float.toString(releve.getValue().surfaceTotaleChauffe)) && !newText.equals("")) {
            releveViewModel.setSurfaceTotaleChauffe(Float.parseFloat(newText));
        }

        newText = binding.editTextNomBatiment.getText().toString();
        if (!newText.equals(releve.getValue().nomBatiment)) {
            releveViewModel.setNomBatiment(newText);
        }
    }

    @Override
    public void onPause() {
        saveData();
        super.onPause();
    }

}