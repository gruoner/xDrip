package com.eveningoutpost.dexdrip.insulin;

import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.utilitymodels.PersistentStore;
import com.eveningoutpost.dexdrip.utils.DexCollectionType;
import com.eveningoutpost.dexdrip.utils.HomeWifi;
import com.google.common.base.Strings;
import static com.eveningoutpost.dexdrip.utils.DexCollectionType.NSFollow;
import com.eveningoutpost.dexdrip.utilitymodels.Pref;
import org.json.JSONObject;

public class MultipleInsulins {

    public static boolean isEnabled() {
        return Pref.getBooleanDefaultFalse("multiple_insulin_types");
    }

    public static boolean useBasalActivity() {
        return Pref.getBooleanDefaultFalse("multiple_insulin_use_basal_activity");
    }

    public static boolean useProfilespecificColoring() {
        return Pref.getBooleanDefaultFalse("multiple_insulin_use_profilespecific_coloring");
    }

    public static boolean isNightscoutInsulinAPIavailable(String url) {
        try {
            final String store_marker = "nightscout-status-poll-" + url;
            final JSONObject status = new JSONObject(PersistentStore.getString(store_marker));
            String ia = status.getString("insulinAvailable");
            if (ia.equalsIgnoreCase("true"))
                return true;
        } catch (Exception e) {
        }
        return false;
    }
    public static boolean isDownloadableByUploader() {
        if (Pref.getBooleanDefaultFalse("cloud_storage_api_enable") && Pref.getBooleanDefaultFalse("cloud_storage_api_download_enable")) return true;
        return false;
    }
    public static boolean isDownloadableByFollower() {
        if (DexCollectionType.getDexCollectionType() == NSFollow) return true;
        return false;
    }
    public static boolean isDownloadAllowed(String url)
    {
        if ((isDownloadableByFollower() || isDownloadableByUploader()) && isNightscoutInsulinAPIavailable(url))
        {
            String p = Pref.getStringDefaultBlank("download_insulin_just_when_in_wifi");
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
                case 3:
                    return false;
                case 99:    // case when not parsable Pref
                    return false;
            }
            return true;    // catchall
        }
        return false;
    }
}
