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
    private String gi;
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
        gi = p.getGI();
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
}
