package com.example.super_cep.model.Export;

import java.util.List;

public class BarChartData {
    private int category;
    private double[] values;

    public BarChartData(int category, double[] values) {
        this.category = category;
        this.values = values;
    }


    public int getCategory() {
        return category;
    }
    public double[] getValues() {
        return values;
    }

}
