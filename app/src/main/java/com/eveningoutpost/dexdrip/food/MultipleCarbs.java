package com.eveningoutpost.dexdrip.food;

import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.utilitymodels.Pref;
import com.eveningoutpost.dexdrip.utils.DexCollectionType;
import com.eveningoutpost.dexdrip.utils.HomeWifi;
import com.google.common.base.Strings;
import static com.eveningoutpost.dexdrip.utils.DexCollectionType.NSFollow;

public class MultipleCarbs {
    public static boolean isEnabled() {
        return Pref.getBooleanDefaultFalse("multiple_carbs_types");
    }

    public static boolean isAvailable() {
        if (!isEnabled()) return false;
        if (FoodManager.getDefaultInstance(false).size() == 0) return false;
        return true;
    }

    public static boolean isDownloadableByUploader() {
        if (Pref.getBooleanDefaultFalse("cloud_storage_api_enable") && Pref.getBooleanDefaultFalse("cloud_storage_api_download_enable")) return true;
        return false;
    }
    public static boolean isDownloadableByFollower() {
        if (DexCollectionType.getDexCollectionType() == NSFollow) return true;
        return false;
    }
    public static boolean isDownloadAllowed()
    {
        if (isDownloadableByFollower() || isDownloadableByUploader())
        {
            String p = Pref.getStringDefaultBlank("download_food_just_when_in_wifi");
            if (Strings.isNullOrEmpty(p))
                return true;
            int decision = 99;
            try {
                decision = Integer.parseInt(p);
            } catch (Exception e) {};
            switch (decision)
            {
                case 1:
                    if (Strings.isNullOrEmpty(JoH.getWifiSSID()))
                        return false;
                    else return true;
                case 2:
                    return HomeWifi.isSet() && HomeWifi.isConnected();
                case 99:    // case when not parsable Pref
                    return false;
            }
            return true;    // catchall
        }
        return false;
    }

    public static boolean useExtendedCarbs4Prediction() {
        return Pref.getBooleanDefaultFalse("multiple_carbs_use_for_prediction");
    }
}
