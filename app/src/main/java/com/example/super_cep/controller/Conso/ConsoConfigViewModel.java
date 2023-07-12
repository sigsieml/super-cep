package com.example.super_cep.controller.Conso;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.super_cep.model.Export.ConsoParser;

import java.util.ArrayList;
import java.util.List;

public class ConsoConfigViewModel extends ViewModel {


    private final MutableLiveData<ConsoParser> consoParser = new MutableLiveData<>();
    private final MutableLiveData<String> nomBatimentConso = new MutableLiveData<>();
    private final MutableLiveData<List<String>> anneesConso = new MutableLiveData<>();
    private final MutableLiveData<String> meilleurAnne = new MutableLiveData<>();

    public ConsoConfigViewModel(){
        this.anneesConso.setValue(new ArrayList<>());
        this.nomBatimentConso.setValue(null);
    }
    public void setConsoParser(ConsoParser consoParser) {
        this.consoParser.setValue(consoParser);
    }

    public LiveData<ConsoParser> getConsoParser() {
        return consoParser;
    }

    public void setNomBatimentConso(String nomBatimentConso) {
        this.nomBatimentConso.setValue(nomBatimentConso);
    }
    public String getNomBatimentConso() {
        return nomBatimentConso.getValue();
    }

    public void setAnneesConso(List<String> anneesConso) {
        this.anneesConso.setValue(anneesConso);
    }

    public List<String> getAnneesConso() {
        return anneesConso.getValue();
    }

    public void setMeilleurAnne(String toString) {
        this.meilleurAnne.setValue(toString);
    }
    public String getMeilleurAnne() {
        return this.meilleurAnne.getValue();
    }
}
