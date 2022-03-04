package com.eveningoutpost.dexdrip.food;

import java.util.ArrayList;
import java.util.Collections;

import com.eveningoutpost.dexdrip.Models.FoodIntake;
import com.eveningoutpost.dexdrip.Models.FoodProfile;
import com.google.common.base.Strings;

public class Food {
    public enum Types {unknown, Ingredient, Meal}

    private String ID;
    private String name;
    private Types type;
    private String unit;
    private int GIupperBound, GIlowerBound;
    private Boolean GIisRange;
    private int pSize;
    private int carbs;
    private long energy;
    private long fat;
    private long protein;
    private Boolean hidden;
    private Boolean deleted;
    private FoodIntake ingredients;
    private double defaultPortion;
    private double portionIncrement;
    private ArrayList<String> categories;
    private FoodProfile originalProfile;

    public Food(FoodProfile p) {
        originalProfile = p;
        ID = p.getFoodID();
        name = p.getName();
        if (p.getType().equalsIgnoreCase("food")) type = Types.Ingredient;
        else if (p.getType().equalsIgnoreCase("quickpick")) type = Types.Meal;
        else type = Types.unknown;
        carbs = p.getCarbs();
        energy = p.getEnergy();
        fat = p.getFat();
        protein = p.getProtein();
        unit = p.getUnit();
        pSize = p.getPortionSize();
        hidden = p.isHidden();
        deleted = p.isDeleted();
        ingredients = new FoodIntake();
        if (!Strings.isNullOrEmpty(p.getIngredients())) {
            String[] ingr = p.getIngredients().split("\\|");
            for (String i : ingr) {
                Food f = FoodManager.getFood(i.split(";")[0]);
                Double portions = Double.parseDouble(i.split(";")[1]);
                assert f != null;
                ingredients.addIngredient(f, portions);
            }
        }
        String gi = p.getGI();
        if (gi.equalsIgnoreCase("low")) {
            GIisRange = true;
            GIupperBound = 55;
            GIlowerBound = 0;
        } else if (gi.equalsIgnoreCase("med") || gi.equalsIgnoreCase("medium")) {
            GIisRange = true;
            GIupperBound = 69;
            GIlowerBound = 56;
        } else if (gi.equalsIgnoreCase("hi") || gi.equalsIgnoreCase("high")) {
            GIisRange = true;
            GIupperBound = 100;
            GIlowerBound = 70;
        } else if (gi.contains("-"))
        try
        {
            GIisRange = true;
            GIlowerBound = Integer.parseInt(gi.split("-")[0]);
            GIupperBound = Integer.parseInt(gi.split("-")[1]);
        } catch (Exception ex)
        {
            GIupperBound = 69;
            GIlowerBound = 56;
        }
        else
            try
            {
                GIisRange = false;
                GIlowerBound = Integer.parseInt(gi);
                GIupperBound = GIlowerBound;
            } catch (Exception ex)
            {
                GIupperBound = 62;
                GIlowerBound = 62;
            }
        defaultPortion = p.getDefaultPortion();
        portionIncrement = p.getPortionIncrement();
        categories = new ArrayList<>();
        if (!Strings.isNullOrEmpty(p.getFoodCategories())) {
            String[] cat = p.getFoodCategories().split("\\|");
            Collections.addAll(categories, cat);
        }
    }

    public String getID() {
        return ID;
    }
    public String getName() {
        return name;
    }
    public Types getType() {
        return type;
    }
    public String getUnit() {
        return unit;
    }
    public int getPSize() {
        return pSize;
    }
    public Boolean isHidden() {
        return hidden;
    }
    public Boolean isDeleted() {
        return deleted;
    }
    public Boolean isGIaRange()
    {
        return GIisRange;
    }
    public int getGI() {
        if (GIisRange)
            return (GIlowerBound+GIupperBound)/2;
        else return GIlowerBound;
    }
    public String getGIasRange() {
        return GIlowerBound + "-" + GIupperBound;
    }
    public double getDefaultPortion() { return defaultPortion; }
    public double getPortionIncrement() { return portionIncrement; }
    public Boolean isInCategory(String cat) {
        if (cat.equals("*")) return true;
        return categories.contains(cat);
    }

    public long getCarbs() {
        if (ingredients.hasIntakes())
            return ingredients.getCarbs();
        else return carbs;
    }
    public long getEnergy() {
        if (ingredients.hasIntakes())
            return ingredients.getEnergy();
        else return energy;
    }
    public long getFat() {
        if (ingredients.hasIntakes())
            return ingredients.getFat();
        else return fat;
    }
    public long getProtein() {
        if (ingredients.hasIntakes())
            return ingredients.getProtein();
        else return protein;
    }

    public String getDescription(double u) {
        if (ingredients.hasIntakes()) {
            return String.format("%.1f", u) + " port. " + name + " (" + ingredients.getFoodIntakeShortString(u) + ")";
        } else return Math.round(pSize*u) + " " + unit + " " + name;
    }

    public void setCategory(String cat) {
        if (!isInCategory(cat)) {
            categories.add(cat);
            if (Strings.isNullOrEmpty(originalProfile.getFoodCategories()))
                originalProfile.setFoodCategories(cat);
            else
                originalProfile.setFoodCategories(originalProfile.getFoodCategories() + "|" + cat);
        }
    }

    public void unsetCategory(String cat) {
        if (isInCategory(cat)) {
            categories.remove(cat);
            String sep = "";
            String result = "";
            for (String c: categories) {
                result = result + sep + c;
                sep = "|";
            }
            originalProfile.setFoodCategories(result);
        }
    }
}
