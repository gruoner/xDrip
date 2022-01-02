package com.eveningoutpost.dexdrip.food;

import android.support.v4.util.Pair;
import java.util.ArrayList;
import java.util.List;

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
    private Boolean hidden;
    private Boolean deleted;
    private FoodIntake ingredients;

    public Food(FoodProfile p) {
        ID = p.getFoodID();
        name = p.getName();
        if (p.getType().equalsIgnoreCase("food")) type = Types.Ingredient;
        else if (p.getType().equalsIgnoreCase("quickpick")) type = Types.Meal;
        else type = Types.unknown;
        carbs = p.getCarbs();
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
}
