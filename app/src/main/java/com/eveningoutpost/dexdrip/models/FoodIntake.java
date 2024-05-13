package com.eveningoutpost.dexdrip.models;

import android.util.Pair;
import com.eveningoutpost.dexdrip.food.Food;
import com.eveningoutpost.dexdrip.food.FoodManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.eveningoutpost.dexdrip.utilitymodels.Constants.HOUR_IN_MS;

public class FoodIntake {
    private List<Pair<Food, Double>> ingredients;

    public FoodIntake(final Food p, final double u) {
        ingredients = new ArrayList<>();
        ingredients.add(new Pair<>(p, u));
    }
    public FoodIntake() {
        ingredients = new ArrayList<>();
    }
    public FoodIntake(String json) throws Exception {
        if (!fromJson(json))
            throw new Exception("JSON could not be parsed and loaded as FoodIntake.");
    }

    public void addIngredient(Food f, Double u) {
        double units = getUnits(f.getID());
        if (units >= 0) {
            removeIngredient(f.getID());
            u = u + units;
        }
        ingredients.add(new Pair<>(f, u));
    }
    public void removeIngredient(String id) {
        for (Pair<Food, Double> s: ingredients)
            if (Objects.equals(s.first.getID(), id)) {
                ingredients.remove(s);
                break;
            }
    }

    public ArrayList<Food> getProfiles() {
        ArrayList<Food> ret = new ArrayList<>();
        for (Pair<Food, Double> s: ingredients)
            ret.add(s.first);
        return ret;
    }
    public Food getProfile(String id) {
        for (Pair<Food, Double> s: ingredients)
            if (Objects.equals(s.first.getID(), id))
                return s.first;
        return null;
    }
    public double getUnits(String id) {
        for (Pair<Food, Double> s: ingredients)
            if (Objects.equals(s.first.getID(), id))
                return s.second;
        return -1;
    }

    public boolean hasIntakes() {
        if (ingredients == null) return false;
        return ingredients.size() != 0;
    }

    public String getFoodIntakeShortString(double u) {
        final StringBuilder sb = new StringBuilder();
        String bind = "";
        for (Pair<Food, Double> i : ingredients) {
            sb.append(bind);
            sb.append(i.first.getDescription(u*i.second));
            if (bind.equalsIgnoreCase("")) bind = " mit ";
            else if (bind.equalsIgnoreCase(" mit ")) bind = " und ";
        }
        return sb.toString();
    }

    public String toJson() {
        String ret = "[\n";
        for (Pair<Food, Double> i: ingredients)
        {
            ret = ret + "  {\n" +
                    "    \"foodID\": \"" + i.first.getID() + "\",\n" +
                    "    \"foodName\": \"" + i.first.getName() + "\",\n" +
                    "    \"portions\": " + i.second + "\n" +
                        "  },";
        }
        return ret.substring(0, ret.length()-1) + "\n]";
    }
    public Boolean fromJson(String json) {
        Boolean ret = true;
        ingredients = new ArrayList<>();
        JsonArray o = JsonParser.parseString(json).getAsJsonArray();
        for (int i = 0; i < o.size(); i++)
        {
            JsonObject e = o.get(i).getAsJsonObject();
            Food f = FoodManager.getFood(e.get("foodID").getAsString());
            Double p = e.get("portions").getAsDouble();
            if (f == null) ret = false;
            else if (p <= 0) ret = false;
            else ingredients.add(new Pair<>(f, p));
        }
        return ret;
    }

    ///======================================
    // Calculation Functions
    ///======================================
    public double getMaxEffect() {
        double ret = 0;
        if (ingredients.size() > 0)
            ret = 6 * HOUR_IN_MS;   // thats the old formula from ioBForGraph_new
        return ret;
    }
    public long getCarbs() {
        double ret = 0;
        for (Pair<Food, Double> i : ingredients) {
            ret = ret + i.first.getCarbs() * i.second;
        }
        return Math.round(ret);
    }
    public long getEnergy() {
        double ret = 0;
        for (Pair<Food, Double> i : ingredients) {
            ret = ret + i.first.getEnergy() * i.second;
        }
        return Math.round(ret);
    }
    public long getFat() {
        double ret = 0;
        for (Pair<Food, Double> i : ingredients) {
            ret = ret + i.first.getFat() * i.second;
        }
        return Math.round(ret);
    }
    public long getProtein() {
        double ret = 0;
        for (Pair<Food, Double> i : ingredients) {
            ret = ret + i.first.getProtein() * i.second;
        }
        return Math.round(ret);
    }
}
