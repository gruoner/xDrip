package com.eveningoutpost.dexdrip.food;

import android.util.Log;

import com.eveningoutpost.dexdrip.R;
import com.eveningoutpost.dexdrip.UtilityModels.Pref;
import com.eveningoutpost.dexdrip.cgm.nsfollow.NightscoutFollow;
import com.eveningoutpost.dexdrip.insulin.Insulin;
import com.eveningoutpost.dexdrip.insulin.InsulinManager;
import com.eveningoutpost.dexdrip.insulin.LinearTrapezoidInsulin;
import com.eveningoutpost.dexdrip.xdrip;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CarboManager {
    private static final String TAG = "CarboManager";
    private static ArrayList<Carbohydrat> profiles;
    private static Boolean loadConfigFromNightscout;

    public static class carboCurve {
        public String type;
        public JsonObject data;

        public boolean isEqual(CarboManager.carboCurve c) {
            if (!this.type.equalsIgnoreCase(c.type))
                return false;
            if (!this.data.toString().equals(c.data.toString()))
                return false;
            return true;
        }
    }
    static class insulinData {
        public String displayName;
        public String name;
        public ArrayList<String> PPN;
        public String concentration;
        public InsulinManager.insulinCurve Curve;
    }
    static class insulinDataWrapper {
        public ArrayList<InsulinManager.insulinData> profiles;
        public String defaultBolus;

        insulinDataWrapper() {
            defaultBolus = null;
            profiles = new ArrayList<InsulinManager.insulinData>();
        }
    }

    public static Boolean updateFromNightscout(List<NightscoutFollow.NightscoutFoodStructure> p)
    {
        Log.d(TAG, "Initialize insulin profiles from Nightscout");
        Boolean somethingChanged = false;
        for (NightscoutFollow.NightscoutFoodStructure profile: p)
        {
            CarboManager.carboCurve c = new CarboManager.carboCurve();
            c.type = "IOB1Min";
            c.data = new JsonObject();
            JsonArray a = new JsonArray();
            for (int i = 0; i < profile.IOB1Min.size(); i++)
                a.add(profile.IOB1Min.get(i));
            c.data.add("list", a);
            Boolean deleted = false;
            if (profile.enabled.equalsIgnoreCase("deleted"))
                deleted = true;
            if (InsulinProfile.byName(profile.name) == null)   // its a new profile --> create it
            {
                InsulinProfile.create(profile.name, profile.displayName, profile.pharmacyProductNumber, c, deleted);
                somethingChanged = true;
            } else {        // its a known profile --> update it
                InsulinProfile o = InsulinProfile.byName(profile.name);
                if (o.isDeleted() != deleted)
                {
                    o.setDeleted(deleted);
                    somethingChanged = true;
                }
                if (!profile.displayName.equals(o.getDisplayName()))
                {
                    o.setDisplayName(profile.displayName);
                    somethingChanged = true;
                }
                if (!o.getPharmacyProductNumber().containsAll(profile.pharmacyProductNumber) || !profile.pharmacyProductNumber.containsAll(o.getPharmacyProductNumber()))
                {
                    o.setPharmacyProductNumber(profile.pharmacyProductNumber);
                    somethingChanged = true;
                }
                if (!c.isEqual(o.getCurve()))
                {
                    o.setCurve(c);
                    somethingChanged = true;
                }
            }
        }
        if (somethingChanged) profiles = getInsulinProfiles();
        if (loadConfigFromNightscout) {
            for (NightscoutFollow.NightscoutInsulinStructure profile : p) {
                if (profile.enabled.equalsIgnoreCase("false") && (countEnabledProfiles() > 1))
                    getProfile(profile.name).disable();
                if (profile.enabled.equalsIgnoreCase("true") && (countEnabledProfiles() < 3))
                    getProfile(profile.name).enable();
                if (profile.type != null) {
                    if (profile.type.equalsIgnoreCase("basal"))
                        setBasalProfile(getProfile(profile.name));
                    if (profile.type.equalsIgnoreCase("bolus"))
                        setBolusProfile(getProfile(profile.name));
                }
            }
            saveDisabledProfilesToPrefs();
        } else LoadDisabledProfilesFromPrefs();
        Log.d(TAG, "InsulinManager initialized from nightscout");
        return somethingChanged;
    }

    private static ArrayList<Insulin> getFood() {
        ArrayList<Insulin> ret = new ArrayList<Insulin>();
        for (InsulinProfile d : InsulinProfile.all()) {
            Insulin insulin;
            switch (d.getCurve().type.toLowerCase()) {
                case "linear trapezoid":
                    insulin = new LinearTrapezoidInsulin(d.getName(), d.getDisplayName(), d.getPharmacyProductNumber(), d.getCurve(), d.isDeleted());
                    Log.d(TAG, "initialized linear trapezoid insulin " + d.getDisplayName());
                    break;
                case "iob1min":
                    insulin = new IOB1MinInsulin(d.getName(), d.getDisplayName(), d.getPharmacyProductNumber(), d.getCurve(), d.isDeleted());
                    Log.d(TAG, "initialized IOB1Min insulin " + d.getDisplayName());
                    break;
                default:
                    Log.d(TAG, "UNKNOWN Curve-Type " + d.getCurve().type);
                    return null;
            }
            ret.add(insulin);
        }
        return ret;
    }
    private static void checkInitialized() {
        if (profiles == null) {
            getDefaultInstance();
        }
    }

    // populate the data set with predefined resource as otherwise the static reference could be lost
    // as we are not really safely handling it
    public static ArrayList<Insulin> getDefaultInstance() {
        profiles = getInsulinProfiles();
        updateFromiDWStream(xdrip.getAppContext().getResources().openRawResource(R.raw.insulin_profiles));
        LoadDisabledProfilesFromPrefs();
        return profiles;
    }

    public static Boolean getLoadConfigFromNightscout() {
        return loadConfigFromNightscout;
    }
    public static void setLoadConfigFromNightscout(Boolean l) {
        loadConfigFromNightscout = l;
    }

    public static ArrayList<Insulin> getAllFood() {
        checkInitialized();
        return profiles;
    }

    public static Insulin getProfile(int i) {
        checkInitialized();
        if (profiles == null) {
            Log.d(TAG, "InsulinManager seems not load Profiles beforehand");
            return null;
        }
        ArrayList<Insulin> t = new ArrayList<Insulin>();
        for (Insulin ins : profiles)
            if (isProfileEnabled(ins))
                t.add(ins);
        if (i >= t.size())
            return null;
        return t.get(i);
    }

    public static Insulin getProfile(String name) {
        checkInitialized();
        if (profiles == null) {
            Log.d(TAG, "InsulinManager seems not load Profiles beforehand");
            return null;
        }
        name = name.toLowerCase();
        // TODO consider hashmap maybe? how many could be iterated here?
        for (Insulin i : profiles) {
            if (i.getName().toLowerCase().equals(name))
                return i;
        }
        return null;
    }

    public static long getMaxEffect(Boolean enabled) {
        checkInitialized();
        long max = 0;
        for (Insulin i : profiles)
            if (!enabled || i.isEnabled())
                if (max < i.getMaxEffect())
                    max = i.getMaxEffect();
        return max;
    }

    public static Boolean isProfileEnabled(Insulin i) {
        return i.isEnabled();
    }

    public static void disableProfile(Insulin i) {
        if (isProfileEnabled(i) && (countEnabledProfiles() > 1))
            i.disable();
    }

    public static void enableProfile(Insulin i) {
        if (!isProfileEnabled(i) && (countEnabledProfiles() < 3))
            i.enable();
    }

    private static int countEnabledProfiles() {
        checkInitialized();
        int ret = 0;
        for (Insulin ins : profiles)
            if (isProfileEnabled(ins))
                ret++;
        return ret;
    }

    public static void LoadDisabledProfilesFromPrefs() {
        checkInitialized();
        String json = Pref.getString("saved_enabled_insulinprofiles_json", "[" + bolusProfile.getName() + "]");
        Log.d(TAG, "Loaded enabled Insulin Profiles from Prefs: " + json);
        String[] enabled = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(json, String[].class);
        for (String d : enabled) {
            Insulin ins = getProfile(d);
            if (ins != null)
                enableProfile(ins);
        }
        String prof = Pref.getString("saved_basal_insulinprofiles", "");
        Log.d(TAG, "Loaded basal Insulin Profiles from Prefs: " + prof);
        basalProfile = getProfile(prof);
        if (basalProfile == null)
            basalProfile = profiles.get(0);
        prof = Pref.getString("saved_bolus_insulinprofiles", bolusProfile.getName());
        Log.d(TAG, "Loaded bolus Insulin Profiles from Prefs: " + prof);
        bolusProfile = getProfile(prof);
        if (bolusProfile == null)
            bolusProfile = profiles.get(0);

        prof = Pref.getString("saved_load_insulinprofilesconfig_from_ns", "false");
        Log.d(TAG, "Loaded Insulin Profiles ConfigFromNS from Prefs: " + prof);
        if (prof.equalsIgnoreCase("true"))
            loadConfigFromNightscout = true;
        else loadConfigFromNightscout = false;
    }

    public static void saveDisabledProfilesToPrefs() {
        checkInitialized();
        ArrayList<String> enabled = new ArrayList<String>();
        for (Insulin i : profiles)
            if (isProfileEnabled(i))
                enabled.add(i.getName());
        String json = new GsonBuilder().create().toJson(enabled);
        Pref.setString("saved_enabled_insulinprofiles_json", json);
        Log.d(TAG, "saved enabled Insulin Profiles to Prefs: " + json);
        if (basalProfile != null) {
            Pref.setString("saved_basal_insulinprofiles", basalProfile.getName());
            Log.d(TAG, "saved basal Insulin Profiles to Prefs: " + basalProfile.getName());
        }
        if (bolusProfile != null) {
            Pref.setString("saved_bolus_insulinprofiles", bolusProfile.getName());
            Log.d(TAG, "saved bolus Insulin Profiles to Prefs: " + bolusProfile.getName());
        }
        Pref.setString("saved_load_insulinprofilesconfig_from_ns", loadConfigFromNightscout.toString());
    }
}
