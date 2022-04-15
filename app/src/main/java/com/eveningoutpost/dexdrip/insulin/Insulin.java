package com.eveningoutpost.dexdrip.insulin;

import android.graphics.Color;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;

public abstract class Insulin {
    private static final String TAG = "gruoner " + Insulin.class.getSimpleName();
    private Boolean enabled;
    protected long maxEffect;

    private long lastupdate;
    private String displayName;
    private String name;
    private ArrayList<String> pharmacyProductNumber;
    private InsulinManager.insulinCurve curve;
    private String color;
    private Boolean deleted;

    public Insulin() {
        name = null;
        displayName = null;
        pharmacyProductNumber = null;
        curve = null;
        color = "";
        enabled = false;
        deleted = false;
    }

    public Insulin(String n, String dn, List<String> ppn, InsulinManager.insulinCurve curveData, String c, Boolean del) {
        name = n;
        displayName = dn;
        pharmacyProductNumber = (ArrayList<String>) ppn;
        maxEffect = 0;
        enabled = false;
        curve = curveData;
        deleted = del;
        color = c;
    }

    public String getName() {
        return name;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getColorStr() {
        return color;
    }
    public int getColor() {
        if (Strings.isNullOrEmpty(color))
            return Color.WHITE;
            else return Color.parseColor(color);
    }
    public ArrayList<String> getPharmacyProductNumber() {
        return pharmacyProductNumber;
    }
    public InsulinManager.insulinCurve getCurve() {
        return curve;
    }
    public void enable() {
        enabled = true;
    }
    public void disable() {
        enabled = false;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public boolean isDeleted() { return deleted; }

    public long getMaxEffect() {
        return maxEffect;
    }

    public double calculateIOB(double time) {
        return -1;
    }

    public double calculateActivity(double time) {
        return -1;
    }

    public ArrayList<Double> getIOBList(int timesliceSize)
    {
        ArrayList<Double> ret = new ArrayList<>();
        double time = 0;
        double iob = 1;
        while (iob > 1.0/1000000)
        {
            iob = calculateIOB(time);
            ret.add(iob);
            time = time + timesliceSize;
        }
        return ret;
    }
}
