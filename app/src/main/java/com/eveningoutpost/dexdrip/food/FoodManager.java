package com.eveningoutpost.dexdrip.food;

import android.util.Log;
import com.eveningoutpost.dexdrip.models.FoodProfile;
import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.cgm.nsfollow.NightscoutFollow;
import com.eveningoutpost.dexdrip.utilitymodels.Constants;
import com.eveningoutpost.dexdrip.utilitymodels.PersistentStore;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoodManager {
    public enum SortTypes {unsorted, favourites, alphanummeric, usage, favalpha}

    private static final String TAG = "FoodManager";
    private static List<Food> profiles = new ArrayList<>();
    private static Boolean initialized = false;
    static final String LAST_FOOD_DOWNLOAD_STORE_COUNTER = "nightscout-rest-food-download-time";
    public static final String NAME4nsupload_food_downloadRATE = "ns-food-download";
    public static final String NAME4nsfollow_food_downloadRATE = "ns-food-download";

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
            String foodCat = "";
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
            if (!Strings.isNullOrEmpty(profile.xdripCategories))
                foodCat = profile.xdripCategories;

            if (FoodProfile.byFoodID(profile._id) == null)  // its a new profile --> create it
            {
                FoodProfile.create(profile._id, profile.name, profile.type, profile.gi, energy, protein, fat, carbs, profile.unit, portion, defaultPortion, portionIncrement, false, hidden, ingredients, foodCat);
                Log.d(TAG, "created " + profile.name + " with ID " + profile._id);
                somethingChanged = true;
            } else {        // its a known profile --> update it
                FoodProfile o = FoodProfile.byFoodID(profile._id);
                if (!o.getName().equals(profile.name)) {   o.setName(profile.name); somethingChanged = true; }
                if (!o.getType().equals(profile.type)) {   o.setType(profile.type); somethingChanged = true; }
// disabled because foodCategories can be changed by ProfileEditor and thus shall not be updated every time food database will be retrieved from NS server
                //if (!o.getFoodCategories().equals(foodCat)) {   o.setFoodCategories(foodCat); somethingChanged = true; }
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
        if (somethingChanged) getDefaultInstance(true);
//        LoadDisabledProfilesFromPrefs();
        Log.d(TAG, "FoodManager initialized from nightscout");
        return somethingChanged;
    }

    // populate the data set with predefined resource as otherwise the static reference could be lost
    // as we are not really safely handling it
    public static List<Food> getDefaultInstance(Boolean forced) {
        if (!initialized || forced) {
            profiles = new ArrayList<>();
            for (FoodProfile d : FoodProfile.all())
                getFood(d.getFoodID());
            initialized = true;
        }
        return profiles;
    }

    public static void resetInitialization() {
        initialized = false;
    }

    public static List<Food> getFood() {
        return profiles;
    }
    public static List<Food> getFood(boolean hidden, boolean deleted ) {
        List<Food> ret = new ArrayList<>();
        for (Food f: profiles)
            if ((f.isHidden() == hidden) && (f.isDeleted() == deleted))
                ret.add(f);
        return ret;
    }
    public static List<Food> getFood(SortTypes sorting, boolean hidden, boolean deleted) {
        switch (sorting)
        {
            case unsorted:
                return getFood(hidden, deleted);
            case favalpha:
            case favourites:
                ArrayList<Food> favs = new ArrayList<>();
                ArrayList<Food> nonfavs = new ArrayList<>();
                for (Food f: getFood(hidden, deleted))
                    if (f.isFavourite())
                        favs.add(f);
                    else nonfavs.add(f);
                    if (sorting == SortTypes.favourites)
                        favs.addAll(nonfavs);
                    else {
                        favs = sortAlphanumeric(favs);
                        favs.addAll(sortAlphanumeric(nonfavs));
                    }
                    return favs;
            case alphanummeric:
                return sortAlphanumeric(getFood(hidden, deleted));
        }
        JoH.static_toast_long("sorting " + sorting.name() + " isn't implemented yet!!");
        return new ArrayList<Food>();
    }
    public static ArrayList<Food> sortAlphanumeric(List<Food> l) {
        ArrayList<Food> ret = new ArrayList<>(l);
        Collections.sort(ret, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        return ret;
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

    public static long lastFoodDownloaded() {
        return PersistentStore.getLong(LAST_FOOD_DOWNLOAD_STORE_COUNTER);
    }
    public static boolean time2DownloadFood() {
        if (PersistentStore.getLong(LAST_FOOD_DOWNLOAD_STORE_COUNTER) > JoH.tsl() - Constants.DAY_IN_MS)
            return false;
        else return true;
    }
    public static void setLastFoodDownload() {
        PersistentStore.setLong(LAST_FOOD_DOWNLOAD_STORE_COUNTER, JoH.tsl());
    }
    public static void resetLastFoodDownload() {
        PersistentStore.setLong(LAST_FOOD_DOWNLOAD_STORE_COUNTER, 0);
    }
}
