package com.example.super_cep.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.example.super_cep.controller.Conso.ConsoConfigViewModel;
import com.example.super_cep.controller.Conso.ConsoProvider;
import com.example.super_cep.controller.Conso.ConsoProviderListener;
import com.example.super_cep.databinding.FragmentGraphiqueBinding;
import com.example.super_cep.model.Export.ConsoParser;

import java.util.ArrayList;
import java.util.List;

public class FragmentGraphique extends Fragment {

    private FragmentGraphiqueBinding binding;

    private String defaultValueNomBatimentConso;
    private ConsoConfigViewModel consoConfigViewModel;
    private ConsoParser consoParser;
    private ConsoProvider consoProvider;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentGraphiqueBinding.inflate(inflater,container,false);
        consoConfigViewModel = new ViewModelProvider(requireActivity()).get(ConsoConfigViewModel.class);
        loadConsoParser();
        if(consoParser == null) return binding.getRoot();
        setupFab();
        setupSpinner();
        if(consoConfigViewModel.getNomBatimentConso() != null){
            consoAlreadyExist(consoConfigViewModel.getNomBatimentConso());
        }

        return binding.getRoot();
    }

    private void loadConsoParser() {
        consoProvider = new ConsoProvider(this, new ConsoProviderListener() {
            @Override
            public void onConsoParserChanged(ConsoParser consoParser) {
                FragmentGraphique.this.consoParser = consoParser;
                consoConfigViewModel.setConsoParser(consoParser);
                setupSpinner();
            }
        });
        if(consoConfigViewModel.getConsoParser().getValue() == null){
            consoParser = consoProvider.getConsoParser();
        }else{
            consoParser = consoConfigViewModel.getConsoParser().getValue();
        }
    }

    private void setupFab() {
        binding.fabLoadConsoFile.setOnClickListener((view) -> {
            //request .xlsx file
            consoProvider.loadNewConso();
        } );
    }

    private String getNomBatimentConso(){
        return binding.spinnerNomBatimentConso.getSelectedItem().toString().equals(defaultValueNomBatimentConso) ? null : binding.spinnerNomBatimentConso.getSelectedItem().toString();
    }

    private void setupSpinner() {
        List<String> values = consoParser.getBatiments();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerNomBatimentConso.setAdapter(adapter);

        binding.spinnerNomBatimentConso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onBatimentSelected(parent.getItemAtPosition(position).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        defaultValueNomBatimentConso = binding.spinnerNomBatimentConso.getSelectedItem().toString();
        binding.spinnerMeilleurAnne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                consoConfigViewModel.setMeilleurAnne(binding.spinnerMeilleurAnne.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void onBatimentSelected(String nomBatiment){
        if(nomBatiment.equals(consoConfigViewModel.getNomBatimentConso())) return;
        List<String> anne = consoParser.getAnneOfBatiment(nomBatiment);
        consoConfigViewModel.setNomBatimentConso(nomBatiment);
        binding.linearLayoutAnne.removeAllViews();
        for(String s : anne){
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateSelectedYears();
            });
            checkBox.setText(s);
            binding.linearLayoutAnne.addView(checkBox);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, anne);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMeilleurAnne.setAdapter(adapter);
        if(anne.size() > 0){
            consoConfigViewModel.setMeilleurAnne(anne.get(anne.size()-1));
            setSpinnerSelection(binding.spinnerMeilleurAnne, anne.get(anne.size()-1));
        }

    }
    private void consoAlreadyExist(String nomBatiment){

        List<String> anne = consoParser.getAnneOfBatiment(nomBatiment);
        binding.linearLayoutAnne.removeAllViews();
        for(String s : anne){
            CheckBox checkBox = new CheckBox(getContext());
            if(consoConfigViewModel.getAnneesConso() != null && consoConfigViewModel.getAnneesConso().contains(s)){
                checkBox.setChecked(true);
            }
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateSelectedYears();
            });
            checkBox.setText(s);
            binding.linearLayoutAnne.addView(checkBox);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, anne);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMeilleurAnne.setAdapter(adapter);
        if(consoConfigViewModel.getMeilleurAnne() != null){
            setSpinnerSelection(binding.spinnerMeilleurAnne, consoConfigViewModel.getMeilleurAnne());
        }
    }

    public void updateSelectedYears(){
        List<String> years = getSelectedYears();
        consoConfigViewModel.setAnneesConso(years);
    }
    public List<String> getSelectedYears(){
        List<String> years = new ArrayList<>();
        for(int i = 0; i < binding.linearLayoutAnne.getChildCount(); i++){
            CheckBox checkBox = (CheckBox) binding.linearLayoutAnne.getChildAt(i);
            if(checkBox.isChecked()){
                years.add(checkBox.getText().toString());
            }
        }
        return years;
    }

    public void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }



}