package com.example.super_cep.model.Export;

public class BarChartData {
    private int category;
    private double high;
    private double medium;
    private double low;

    public BarChartData(int category, double high, double medium, double low) {
        this.category = category;
        this.high = high;
        this.medium = medium;
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public int getCategory() {
        return category;
    }

    public double getMedium() {
        return medium;
    }

    public double getLow() {
        return low;
    }
}
