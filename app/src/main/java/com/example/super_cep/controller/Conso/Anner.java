package com.example.super_cep.controller.Conso;

import java.util.Map;

public class Anner {
    public int anner;

    public double total;
    public Map<Energie, Double> energies;
    public Anner(int anner, double total, Map<Energie, Double> energies) {
        this.anner = anner;
        this.energies = energies;
        this.total = total;
    }
    public double getEnergie(Energie energie){
        return this.energies.containsKey(energie) ? this.energies.get(energie) : 0;
    }
}
