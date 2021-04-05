package com.eveningoutpost.dexdrip.food;

public class Carbohydrat {

    private String name;
    private int GL;

    public Carbohydrat(String n, int gl)
    {
        name = n;
        GL = gl;
    }

    public String getName() {
        return name;
    }

    public int getGL() {
        return GL;
    }

    public double calculateCOB(double time) {
        return -1;
    }

    public double calculateActivity(double time) {
        return -1;
    }
}
