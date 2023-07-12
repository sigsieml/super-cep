package com.example.super_cep.model.Export;

public class Anner {
    public int anner;
    public double elec;
    public double gaz;
    public double fioul;

    public Anner(int anner, double elec, double gaz, double fioul) {
        this.anner = anner;
        this.elec = elec;
        this.gaz = gaz;
        this.fioul = fioul;
    }


    public double getEnergie(String nomEnergie){
        switch (nomEnergie){
            case "Elec":
                return elec;
            case "Gaz":
                return gaz;
            case "Fioul":
                return fioul;
            default:
                return 0;
        }
    }
}
