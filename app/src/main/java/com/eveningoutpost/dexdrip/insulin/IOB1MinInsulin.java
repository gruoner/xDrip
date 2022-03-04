package com.eveningoutpost.dexdrip.insulin;

import com.google.gson.JsonArray;
import java.util.ArrayList;

public class IOB1MinInsulin extends Insulin {
    /// curvedata is a IOB value array with stepsize of 1 minute
    private ArrayList<Double> curve;

    public IOB1MinInsulin(String n, String dn, ArrayList<String> ppn, InsulinManager.insulinCurve curveData, String c, Boolean del) {
        super(n, dn, ppn, curveData, c, del);

        JsonArray l = curveData.data.get("list").getAsJsonArray();
        curve = new ArrayList<>();
        for (int i = 0; i < l.size(); i++)
            curve.add(l.get(i).getAsDouble());
        maxEffect = curve.size();
    }

    public double calculateIOB(double t) {
        if (t < 0)
            return 1.0;
        int index = (int) t;    // typecast / round down
        double remaining = t - index;   // remaining double to be approximated linearly

        if (index+1 >= curve.size())
            return curve.get(curve.size()-1);
        double valueIndex = curve.get(index);
        double valueNextIndex = curve.get(index + 1);
        double ret = valueIndex;
        ret += (valueNextIndex - valueIndex) * remaining;
        return ret;
    }

    public double calculateActivity(double t) {
        double stepBefore = calculateIOB(t-1);
//        double now = calculateIOB(t);
        double stepBehind = calculateIOB(t+1);
        double ret = (stepBehind - stepBefore) / 2.0;
        return ret;
    }

    public ArrayList<Double> getIOBList(int timesliceSize)
    {
        return curve;
    }
}
