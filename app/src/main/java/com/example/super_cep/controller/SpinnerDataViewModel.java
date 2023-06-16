package com.example.super_cep.controller;

import android.content.Context;
import android.widget.ArrayAdapter;
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

    public void updateSpinnerData(Spinner spinnerTypeMur, String key) {
        if(!spinnerData.getValue().containsKey(key)){
            Snackbar.make(spinnerTypeMur, "Erreur lors de la récupération des données de " + key, Snackbar.LENGTH_LONG).show();
            return;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(spinnerTypeMur.getContext(), android.R.layout.simple_spinner_item, spinnerData.getValue().get(key));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeMur.setAdapter(adapter);

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
