package com.example.super_cep.controller;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Map;

public class SpinnerDataViewModel extends ViewModel {


    MutableLiveData<Map<String, List<String>>> spinnerData;


    public SpinnerDataViewModel(){
        spinnerData = new MutableLiveData<>();
    }

    public void setSpinnerData(Map<String, List<String>> spinnerData){
        this.spinnerData.setValue(spinnerData);
    }


    public MutableLiveData<Map<String, List<String>>> getSpinnerData(){
        return spinnerData;
    }

    public void updateSpinnerData(Spinner spinner, String key) {
        if(!spinnerData.getValue().containsKey(key)){
            Snackbar.make(spinner, "Erreur lors de la récupération des données de " + key, Snackbar.LENGTH_LONG).show();
            return;
        }
        updateSpinnerData(spinner, spinnerData.getValue().get(key));
    }

    public void setAutoComplete(AutoCompleteTextView multiLineAutoCompleteTextView, String key){
        if(!spinnerData.getValue().containsKey(key)){
            Snackbar.make(multiLineAutoCompleteTextView, "Erreur lors de la récupération des données de " + key, Snackbar.LENGTH_LONG).show();
            return;
        }
        setAutoComplete(multiLineAutoCompleteTextView, spinnerData.getValue().get(key));
    }

    public void setAutoComplete(AutoCompleteTextView autoCompleteTextView, List<String> strings) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(autoCompleteTextView.getContext(), android.R.layout.simple_dropdown_item_1line, strings);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnTouchListener((v, event) -> {
            //only if it's a click
            if(event.getAction() != android.view.MotionEvent.ACTION_UP)
                return false;
            autoCompleteTextView.showDropDown();
            return false;
        });
    }

    public static void updateSpinnerData(Spinner spinner, List<String> values ){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void setSpinnerSelection(Spinner spinner, String value){
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(value)){
                spinner.setSelection(i);
                return;
            }
        }
    }
}
