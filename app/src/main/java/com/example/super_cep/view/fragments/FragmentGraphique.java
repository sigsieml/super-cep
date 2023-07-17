package com.example.super_cep.view.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.super_cep.R;
import com.example.super_cep.controller.Conso.ConsoConfigViewModel;
import com.example.super_cep.controller.Conso.ConsoProvider;
import com.example.super_cep.controller.Conso.ConsoProviderListener;
import com.example.super_cep.controller.Conso.Energie;
import com.example.super_cep.databinding.FragmentGraphiqueBinding;
import com.example.super_cep.controller.Conso.Anner;
import com.example.super_cep.controller.Conso.ConsoParser;

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
            if(consoParser == null) return;
            consoConfigViewModel.setConsoParser(consoParser);
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


    private void setupSpinner() {
        List<String> values = consoParser.getBatiments();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, values);
        binding.autoCompleteNomBatiment.setAdapter(adapter);

        binding.autoCompleteNomBatiment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onBatimentSelected(parent.getItemAtPosition(position).toString());
                // remove focus
                binding.autoCompleteNomBatiment.clearFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.autoCompleteNomBatiment.getWindowToken(), 0);
            }
        });

        binding.autoCompleteNomBatiment.setOnTouchListener((v, event) -> {
            //only if it's a click
            if (event.getAction() != android.view.MotionEvent.ACTION_UP)
                return false;
            binding.autoCompleteNomBatiment.showDropDown();
            return false;
        });



        defaultValueNomBatimentConso = binding.autoCompleteNomBatiment.getText().toString();
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
        updateDataTable(nomBatiment);
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
        updateDataTable(nomBatiment);
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

    private void updateDataTable(String nomBatiment){
        Drawable drawableBackground = getResources().getDrawable(R.drawable.border_black_thin);
        binding.linearlayoutConso.removeAllViews();
        List<String> annees = consoParser.getAnneOfBatiment(nomBatiment);
        List<Anner> anneesWatt = consoParser.getConsoWatt(nomBatiment, annees);
        List<Anner> annesEuro = consoParser.getConsoEuro(nomBatiment, annees);
        LinearLayout linearLayoutAnnees = new LinearLayout(getContext());
        linearLayoutAnnees.setOrientation(LinearLayout.VERTICAL);
        TextView textViewTitre = new TextView(getContext());
        textViewTitre.setText("kWh");
        linearLayoutAnnees.addView(textViewTitre);
        for(String anne : annees){
            TextView textViewAnne = new TextView(getContext());
            int widthInDp = 50;
            textViewAnne.setWidth( (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics()));
            textViewAnne.setText(anne);
            textViewAnne.setBackground(drawableBackground);
            linearLayoutAnnees.addView(textViewAnne);
        }
        TextView titreEuro = new TextView(getContext());
        titreEuro.setText("€");
        linearLayoutAnnees.addView(titreEuro);
        for(String anne : annees){
            TextView textViewAnne = new TextView(getContext());
            textViewAnne.setText(anne);
            int widthInDp = 50;
            textViewAnne.setWidth( (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics()));
            textViewAnne.setBackground(drawableBackground);
            linearLayoutAnnees.addView(textViewAnne);
        }
        binding.linearlayoutConso.addView(linearLayoutAnnees);

        for(Energie energie : Energie.values()){
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView textView = new TextView(getContext());
            textView.setText(energie.nomEnergie);
            linearLayout.addView(textView);
            for(Anner anner : anneesWatt){
                TextView textViewAnne = new TextView(getContext());
                int widthInDp = 120;
                textView.setWidth( (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics()));
                textViewAnne.setBackground(drawableBackground);
                String value = String.format("%.2f", anner.energies.get(energie));
                textViewAnne.setText(value);
                linearLayout.addView(textViewAnne);
            }
            TextView textViewEuro = new TextView(getContext());
            textViewEuro.setText("");
            linearLayout.addView(textViewEuro);
            for(Anner anner: annesEuro){
                TextView textViewAnne = new TextView(getContext());
                int widthInDp = 120;
                textView.setWidth( (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics()));
                textViewAnne.setBackground(drawableBackground);
                String value = String.format("%.2f", anner.energies.get(energie));
                textViewAnne.setText(value);
                linearLayout.addView(textViewAnne);
            }
            binding.linearlayoutConso.addView(linearLayout);

        }


    }



}