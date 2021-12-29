package com.eveningoutpost.dexdrip.food;

import android.util.Log;
import com.eveningoutpost.dexdrip.Models.FoodProfile;
import com.eveningoutpost.dexdrip.cgm.nsfollow.NightscoutFollow;
import java.util.ArrayList;
import java.util.List;

public class FoodManager {
    private static final String TAG = "FoodManager";
    private static List<Food> profiles;
    private static Boolean loadConfigFromNightscout;

    public static Boolean updateFromNightscout(List<NightscoutFollow.NightscoutFoodStructure> p) {
        Log.d(TAG, "Initialize carbo profiles from Nightscout");
        Boolean somethingChanged = false;
        ArrayList<String> profilesGot = new ArrayList<>();
        for (NightscoutFollow.NightscoutFoodStructure profile: p)
        {
            profilesGot.add(profile._id);

            int portion = 1;
            if (profile.portion != null)
                portion = Integer.parseInt(profile.portion);
            boolean hidden = false;
            if (profile.hidden != null)
            {
                if (profile.hidden.toUpperCase().equals("true")) hidden = true;
                else hidden = false;
            }

            if (FoodProfile.byFoodID(profile._id) == null)   // its a new profile --> create it
            {
                FoodProfile.create(profile._id, profile.name, profile.type, profile.unit, portion, false, hidden);
                somethingChanged = true;
            } else {        // its a known profile --> update it
                FoodProfile o = FoodProfile.byFoodID(profile._id);
                if (!o.getName().equals(profile.name)) {   o.setName(profile.name); somethingChanged = true; }
                if (!o.getType().equals(profile.type)) {   o.setType(profile.type); somethingChanged = true; }
                if (!o.getUnit().equals(profile.unit)) {   o.setUnit(profile.unit); somethingChanged = true; }
                if (o.getPortionSize() != portion) {   o.setPortionSize(portion); somethingChanged = true; }
                if (o.isHidden() != hidden) {   o.setHidden(hidden); somethingChanged = true; }
                if (o.isDeleted()) { o.setDeleted(false); somethingChanged = true; }
            }
        }
        for (FoodProfile toDel: FoodProfile.all())
            if (!profilesGot.contains(toDel.getFoodID())) {
                toDel.setDeleted(true);
                somethingChanged = true;
            }
        if (somethingChanged) profiles = getFood();
//        LoadDisabledProfilesFromPrefs();
        Log.d(TAG, "FoodManager initialized from nightscout");
        return somethingChanged;
    }

    private static ArrayList<Food> getFood() {
        ArrayList<Food> ret = null;
        for (FoodProfile d : FoodProfile.all()) {
            Food food = new Food(d.getFoodID(), d.getName(), d.getType(), d.getUnit(), d.getPortionSize(), d.isHidden(), d.isDeleted());
            Log.d(TAG, "initialized Food " + d.getName());
            ret.add(food);
        }
        return ret;
    }
    private static void checkInitialized() {
        if (profiles == null)
           getDefaultInstance();
    }

    // populate the data set with predefined resource as otherwise the static reference could be lost
    // as we are not really safely handling it
    public static List<Food> getDefaultInstance() {
        profiles = getFood();
        //LoadDisabledProfilesFromPrefs();
        return profiles;
    }

    public static Boolean getLoadConfigFromNightscout() {
        return loadConfigFromNightscout;
    }
    public static void setLoadConfigFromNightscout(Boolean l) {
        loadConfigFromNightscout = l;
    }

    public static Food getFood(String id) {
        checkInitialized();
        if (profiles == null) {
            Log.d(TAG, "FoodManager seems not load Profiles beforehand");
            return null;
        }
        for (Food f : profiles)
            if (f.getID().equalsIgnoreCase(id))
                return f;
        for (Food f : profiles)
            if (f.getName().equalsIgnoreCase(id))
                return f;
        return null;
    }

/*    public static void LoadDisabledProfilesFromPrefs() {
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
    }*/
}
