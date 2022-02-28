package com.eveningoutpost.dexdrip.food;

import com.eveningoutpost.dexdrip.UtilityModels.Pref;

public class MultipleCarbs {
    public static boolean isEnabled() {
        return Pref.getBooleanDefaultFalse("multiple_carbs_types");
    }

    public static boolean isAvailable() {
        if (!isEnabled()) return false;
        return true;   // todo: return true when fooddatabase download from nightscout is enabled;
    }

    public static boolean useExtendedCarbs4Prediction() {
        return Pref.getBooleanDefaultFalse("multiple_carbs_use_for_prediction");
    }
}
