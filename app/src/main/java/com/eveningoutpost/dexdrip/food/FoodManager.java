package com.eveningoutpost.dexdrip.food;

import android.util.Log;
import com.eveningoutpost.dexdrip.Models.FoodProfile;
import com.eveningoutpost.dexdrip.cgm.nsfollow.NightscoutFollow;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

public class FoodManager {
    private static final String TAG = "FoodManager";
    private static List<Food> profiles = new ArrayList<>();
    private static Boolean loadConfigFromNightscout;

    public static Boolean updateFromNightscout(List<NightscoutFollow.NightscoutFoodStructure> p) {
        Log.d(TAG, "updating food profiles from Nightscout");
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
                hidden = profile.hidden.toUpperCase().equals("TRUE");
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

            if (FoodProfile.byFoodID(profile._id) == null)  // its a new profile --> create it
            {
                FoodProfile.create(profile._id, profile.name, profile.type, profile.gi, energy, protein, fat, carbs, profile.unit, portion, defaultPortion, portionIncrement, false, hidden, ingredients);
                Log.d(TAG, "created " + profile.name + " with ID " + profile._id);
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
                Log.d(TAG, "updating " + profile.name + " (identified by ID: " + profile._id + ")");
            }
        }
        for (FoodProfile toDel: FoodProfile.all())
            if (!profilesGot.contains(toDel.getFoodID())) {
                toDel.setDeleted(true);
                Log.d(TAG, "deleting " + toDel.getName() + " (with ID: " + toDel.getFoodID() + ")");
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
        return profiles;
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
        FoodProfile p = FoodProfile.byFoodID(id);
        if (p != null)
        {
            Food food = new Food(p);
            Log.d(TAG, "initialized Food " + food.getName() + "(deleted: " + food.isDeleted() + "; hidden: " + food.isHidden() + ")");
            profiles.add(food);
            return food;
        }
        return null;
    }
}
