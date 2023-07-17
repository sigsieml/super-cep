package com.example.super_cep.controller.Conso;

import java.util.Map;

public class Anner {

    public int anner;
    public Map<Energie, Double> energies;

    public Anner(int anner, Map<Energie, Double> energies) {
        this.anner = anner;
        this.energies = energies;
    }

    public double getEnergie(String nomEnergie){
        return energies.containsKey(Energie.valueOf(nomEnergie)) ? energies.get(Energie.valueOf(nomEnergie)) : 0;
    }


}
