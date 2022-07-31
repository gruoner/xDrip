package com.eveningoutpost.dexdrip.insulin;

import java.util.ArrayList;

public class LinearTrapezoidInsulin extends Insulin {
    /// curvedata and all timestamps are defined in minutes
    private long onset;  // when does profile activity starts
    private long t1;     // when does activity reaches max
    private long t2;     // when does activity leaves max
    private long t3;     // when does activity ends

    private double max;

    public LinearTrapezoidInsulin(String n, String dn, ArrayList<String> ppn, InsulinManager.insulinCurve curveData, Boolean del) {
        super(n, dn, ppn, curveData, del);

        onset = curveData.data.get("onset").getAsLong();
        if (curveData.data.get("peak").getAsString().contains("-")) {
            t1 = Integer.parseInt(curveData.data.get("peak").getAsString().split("-")[0]);
            t2 = Integer.parseInt(curveData.data.get("peak").getAsString().split("-")[1]);
        } else {
            t1 = Integer.parseInt(curveData.data.get("peak").getAsString());
            t2 = t1;
        }
        t3 = curveData.data.get("duration").getAsLong();

        max = 2.0 / (t2 - t1 + t3 - onset);
        maxEffect = t3;
    }

    public double calculateIOB(long t) {

		if ((0 <= t) && (t < onset))
			return 1.0;
		else if ((onset <= t) && (t < t1))
			return 1.0 - 0.5 * (t - onset)* (t - onset) * max / (t1 - onset);
        else if ((t1 <= t) && (t < t2))
            return 1.0 + 0.5 * max * (t1 - onset) - max * (t - onset);
        else if ((t2 <= t) && (t < t3))
            return 0.5 * (t3 - t) * (t3 - t) * max / (t3 - t2);
        else return 0;
    }

    public double calculateActivity(long t) {

        if ((0 <= t) && (t < onset))
            return 0.0;
        else if ((onset <= t) && (t < t1))
            return (t - onset) * max / (t1 - onset);
        else if ((t1 <= t) && (t < t2))
            return max;
        else if ((t2 <= t) && (t < t3))
            return (t - t3) * max / (t3 - t2);
        else return 0;
    }
}
