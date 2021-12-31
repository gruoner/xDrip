package com.eveningoutpost.dexdrip.Models;

import android.support.v4.util.Pair;
import com.eveningoutpost.dexdrip.food.Food;
import com.eveningoutpost.dexdrip.food.FoodManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

import static com.eveningoutpost.dexdrip.UtilityModels.Constants.HOUR_IN_MS;

public class FoodIntake {
    private List<Pair<Food, Double>> ingredients;

    public FoodIntake(final Food p, final double u) {
        ingredients = new ArrayList<>();
        ingredients.add(new Pair<Food, Double>(p, u));
    }
    public FoodIntake() {
        ingredients = new ArrayList<>();
    }
    public FoodIntake(String json) throws Exception {
        if (!fromJson(json))
            throw new Exception("JSON could not be parsed and loaded as FoodIntake.");
    }

    public void addIngredient(Food f, Double u) {
        ingredients.add(new Pair<Food, Double>(f, u));
    }

    public Food getProfile(String id) {
        for (Pair<Food, Double> s: ingredients)
            if (s.first.getID() == id)
                return s.first;
        return null;
    }
    public double getUnits(String id) {
        for (Pair<Food, Double> s: ingredients)
            if (s.first.getID() == id)
                return s.second;
        return -1;
    }

    public boolean hasIntakes() {
        if (ingredients == null) return false;
        if (ingredients.size() == 0) return false;
        return true;
    }

    public String getFoodIntakeShortString() {
        final StringBuilder sb = new StringBuilder();
        String bind = "";
        for (Pair<Food, Double> i : ingredients) {
            sb.append(bind);
            sb.append(i.first.getName());
            sb.append(" ");
            sb.append(String.format("%.2f portions", i.second));
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
            if (f == null)
                f = FoodManager.getFood(e.get("foodName").getAsString());
            Double p = e.get("portions").getAsDouble();
            if (f == null) ret = false;
            else if (p <= 0) ret = false;
            else ingredients.add(new Pair<Food, Double>(f, p));
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
}
