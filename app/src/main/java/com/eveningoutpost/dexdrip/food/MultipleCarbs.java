package com.eveningoutpost.dexdrip.food;

import com.eveningoutpost.dexdrip.R;
import com.eveningoutpost.dexdrip.UtilityModels.Pref;
import com.eveningoutpost.dexdrip.utils.DexCollectionType;
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

    public static boolean useExtendedCarbs4Prediction() {
        return Pref.getBooleanDefaultFalse("multiple_carbs_use_for_prediction");
    }
}
