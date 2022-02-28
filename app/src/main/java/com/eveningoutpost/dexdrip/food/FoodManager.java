package com.eveningoutpost.dexdrip.food;

import android.util.Log;
import com.eveningoutpost.dexdrip.Models.FoodProfile;
import com.eveningoutpost.dexdrip.cgm.nsfollow.NightscoutFollow;
import com.google.common.base.Strings;

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

            int energy = 0;
            int protein = 0;
            int fat = 0;
            int carbs = 0;
            int portion = 1;
            double defaultPortion = 1;
            double portionIncrement = 0.1;
            try {
                if (!Strings.isNullOrEmpty(profile.energy)) energy = Integer.parseInt(profile.energy);
            } catch (Exception ignored) { }
            try {
                if (!Strings.isNullOrEmpty(profile.protein)) protein = Integer.parseInt(profile.protein);
            } catch (Exception ignored) { }
            try {
                if (!Strings.isNullOrEmpty(profile.fat)) fat = Integer.parseInt(profile.fat);
            } catch (Exception ignored) { }
            try {
                if (!Strings.isNullOrEmpty(profile.carbs)) carbs = Integer.parseInt(profile.carbs);
            } catch (Exception ignored) { }
            try {
                if (!Strings.isNullOrEmpty(profile.portion)) portion = Integer.parseInt(profile.portion);
            } catch (Exception ignored) { }
            try {
                if (!Strings.isNullOrEmpty(profile.defaultPortion)) defaultPortion = Double.parseDouble(profile.defaultPortion);
            } catch (Exception ignored) { }
            try {
                if (!Strings.isNullOrEmpty(profile.portionIncrement)) portionIncrement = Double.parseDouble(profile.portionIncrement);
            } catch (Exception ignored) { }
            boolean hidden = false;
            if (profile.hidden != null)
            {
                hidden = profile.hidden.toUpperCase().equals("true");
            }
            String ingredients = "";
            if (profile.foods != null)
            {
                String sep = "";
                for (NightscoutFollow.NightscoutFoodStructure i: profile.foods)
                {
                    ingredients = ingredients + sep + i._id + ";" + i.portions;
                    sep = "|";
                }
            }

            if (FoodProfile.byFoodID(profile._id) == null)   // its a new profile --> create it
            {
                FoodProfile.create(profile._id, profile.name, profile.type, profile.gi, energy, protein, fat, carbs, profile.unit, portion, defaultPortion, portionIncrement, false, hidden, ingredients);
                somethingChanged = true;
            } else {        // its a known profile --> update it
                FoodProfile o = FoodProfile.byFoodID(profile._id);
                if (!o.getName().equals(profile.name)) {   o.setName(profile.name); somethingChanged = true; }
                if (!o.getType().equals(profile.type)) {   o.setType(profile.type); somethingChanged = true; }
                if (o.getEnergy() != energy) {   o.setEnergy(energy); somethingChanged = true; }
                if (o.getFat() != fat) {   o.setFat(fat); somethingChanged = true; }
                if (o.getProtein() != protein) {   o.setProtein(protein); somethingChanged = true; }
                if (o.getCarbs() != carbs) {   o.setCarbs(carbs); somethingChanged = true; }
                if (!Strings.isNullOrEmpty(profile.gi) && !o.getGI().equals(profile.gi)) {   o.setGI(profile.gi); somethingChanged = true; }
                if (!Strings.isNullOrEmpty(profile.unit) && !o.getUnit().equals(profile.unit)) {   o.setUnit(profile.unit); somethingChanged = true; }
                if (o.getPortionSize() != portion) {   o.setPortionSize(portion); somethingChanged = true; }
                if (o.isHidden() != hidden) {   o.setHidden(hidden); somethingChanged = true; }
                if (o.isDeleted()) { o.setDeleted(false); somethingChanged = true; }
                if (!o.getIngredients().equals(ingredients)) {   o.setIngredients(ingredients); somethingChanged = true; }
                if (o.getDefaultPortion() != defaultPortion) {   o.setDefaultPortion(defaultPortion); somethingChanged = true; }
                if (o.getPortionIncrement() != portionIncrement) {   o.setPortionIncrement(portionIncrement); somethingChanged = true; }
            }
        }
        for (FoodProfile toDel: FoodProfile.all())
            if (!profilesGot.contains(toDel.getFoodID()) && !toDel.isDeleted()) {
                toDel.setDeleted(true);
                somethingChanged = true;
            }
        if (somethingChanged) getDefaultInstance();
//        LoadDisabledProfilesFromPrefs();
        Log.d(TAG, "FoodManager initialized from nightscout");
        return somethingChanged;
    }

    // populate the data set with predefined resource as otherwise the static reference could be lost
    // as we are not really safely handling it
    public static List<Food> getDefaultInstance() {
        profiles = new ArrayList<>();
        for (FoodProfile d : FoodProfile.all())
            getFood(d.getFoodID());
        //LoadDisabledProfilesFromPrefs();
        return profiles;
    }

    public static Boolean getLoadConfigFromNightscout() {
        return loadConfigFromNightscout;
    }
    public static void setLoadConfigFromNightscout(Boolean l) {
        loadConfigFromNightscout = l;
    }

    public static List<Food> getFood() {
        return profiles;
    }
    public static Food getFood(String id) {
        if (profiles == null)
            profiles = new ArrayList<>();
        for (Food f : profiles)
            if (f.getID().equalsIgnoreCase(id))
                return f;
        for (Food f : profiles)
            if (f.getName().equalsIgnoreCase(id))
                return f;
        FoodProfile p = FoodProfile.byFoodID(id);
        if (p == null)
            p = FoodProfile.byName(id);
        if (p != null)
        {
            Food food = new Food(p);
            Log.d(TAG, "initialized Food " + food.getName());
            profiles.add(food);
            return food;
        }
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
