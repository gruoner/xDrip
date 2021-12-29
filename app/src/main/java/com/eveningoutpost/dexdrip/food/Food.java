package com.eveningoutpost.dexdrip.food;

public class Food {
    public enum Types {unknown, Ingredient, Meal}

    private String ID;
    private String name;
    private Types type;
    private String unit;
    private int pSize;
    private Boolean hidden;
    private Boolean deleted;

    public Food(String id, String n, String t, String u, int pS, Boolean h, Boolean d) {
        ID = id;
        name = n;
        if (t.equalsIgnoreCase("food")) type = Types.Ingredient;
        else if (t.equalsIgnoreCase("quickpick")) type = Types.Meal;
        else type = Types.unknown;
        unit = u;
        pSize = pS;
        hidden = h;
        deleted = d;
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
