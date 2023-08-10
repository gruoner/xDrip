package com.eveningoutpost.dexdrip.models;

import android.provider.BaseColumns;
import com.activeandroid.model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import java.util.List;
import java.util.Objects;

@Table(name = "FoodProfiles", id = BaseColumns._ID)
public class FoodProfile extends Model {

    private static final String TAG = "gruoner " + FoodProfile.class.getSimpleName();
    private static boolean patched = false;

    @Column(name = "foodID")
    private String foodID;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String type;
    @Column(name = "gi")
    private String gi;
    @Column(name = "energy")
    private int energy;
    @Column(name = "protein")
    private int protein;
    @Column(name = "fat")
    private int fat;
    @Column(name = "carbs")
    private int carbs;
    @Column(name = "unit")
    private String unit;
    @Column(name = "portionSize")
    private int portionSize;
    @Column(name = "deleted")
    private Integer deleted;
    @Column(name = "favourite")
    private Integer favourite;
    @Column(name = "hidden")
    private Integer hidden;
    @Column(name = "ingredients")
    private String ingredients;
    @Column(name = "defaultPortion")
    private double defaultPortion;
    @Column(name = "portionIncrement")
    private double portionIncrement;
    @Column(name = "foodCategories")
    private String foodCategories;

    public FoodProfile() {
        super();
    }
    public FoodProfile(String id, String n, String type, String g, int e, int p, int f, int c, String u, int portion, double dP, double pI, Boolean del, Boolean h, Boolean fav, String i, String fC) {
        this.foodID = id;
        name = n;
        this.type = type;
        gi = g;
        energy = e;
        protein = p;
        fat = f;
        carbs = c;
        unit = u;
        this.portionSize = portion;
        if (del) deleted = 1;
        else deleted = 0;
        if (h) hidden = 1;
        else hidden = 0;
        ingredients = i;
        defaultPortion = dP;
        portionIncrement = pI;
        foodCategories = fC;
        if (fav)
            favourite = 1;
        else favourite = 0;
    }

    public static FoodProfile create(String id, String dn, String type, String g, int e, int p, int f, int c, String u, int portion, double dP, double pI, Boolean del, Boolean h, String i, String fC)
    {
        FoodProfile ret = new FoodProfile(id, dn, type, g, e, p, f, c, u, portion, dP, pI, del, h, false, i, fC);
        try { ret.save(); } catch (android.database.sqlite.SQLiteException ex) { fixUpTable(); ret.save(); }

        return ret;
    }

    public String getFoodID() { return Objects.toString(foodID, ""); }
    public String getName() {
        return Objects.toString(name, "");
    }
    public String getType() {
        return Objects.toString(type, "");
    }
    public String getGI() {
        return Objects.toString(gi, "");
    }
    public int getEnergy() {
        return energy;
    }
    public int getProtein() {
        return protein;
    }
    public int getFat() {
        return fat;
    }
    public int getCarbs() {
        return carbs;
    }
    public String getUnit() {
        return Objects.toString(unit, "");
    }
    public int getPortionSize() {
        return portionSize;
    }
    public double getDefaultPortion() { return defaultPortion; }
    public double getPortionIncrement() { return portionIncrement; }

    public Boolean isDeleted() {
        if (deleted == null) return false;
        return deleted != 0;
    }
    public Boolean isHidden() {
        if (hidden == null) return false;
        return hidden != 0;
    }
    public Boolean isFavourite() {
        if (favourite == null) return false;
        return favourite != 0;
    }
    public String getIngredients() {
        return Objects.toString(ingredients, "");
    }
    public String getFoodCategories() { return Objects.toString(foodCategories, ""); }

    public void setName(String n) {
        name = n;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setType(String t) {
        type = t;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setGI(String s) {
        gi = s;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setEnergy(int i) {
        energy = i;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setProtein(int i) {
        protein = i;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setFat(int i) {
        fat = i;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setCarbs(int i) {
        carbs = i;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setUnit(String n) {
        unit = n;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setPortionSize(int n) {
        portionSize = n;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setDefaultPortion(double n) {
        defaultPortion = n;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setPortionIncrement(double n) {
        portionIncrement = n;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setDeleted(Boolean del) {
        if (del) deleted = 1;
        else deleted = 0;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setFavourite(Boolean fav) {
        if (fav) favourite = 1;
        else favourite = 0;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setHidden(Boolean h) {
        if (h) hidden = 1;
        else hidden = 0;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setIngredients(String s) {
        ingredients = s;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }
    public void setFoodCategories(String s) {
        foodCategories = s;
        try { save(); } catch (android.database.sqlite.SQLiteException e) { fixUpTable(); save(); }
    }

    private static void fixUpTable() {
        if (patched) return;
        String[] patchup = {
                "CREATE TABLE FoodProfiles (_id INTEGER PRIMARY KEY AUTOINCREMENT);",
                "ALTER TABLE FoodProfiles ADD COLUMN foodID TEXT;",
                "ALTER TABLE FoodProfiles ADD COLUMN name TEXT;",
                "ALTER TABLE FoodProfiles ADD COLUMN type TEXT;",
                "ALTER TABLE FoodProfiles ADD COLUMN gi TEXT;",
                "ALTER TABLE FoodProfiles ADD COLUMN energy INTEGER DEFAULT 0;",
                "ALTER TABLE FoodProfiles ADD COLUMN protein INTEGER DEFAULT 0;",
                "ALTER TABLE FoodProfiles ADD COLUMN fat INTEGER DEFAULT 0;",
                "ALTER TABLE FoodProfiles ADD COLUMN carbs INTEGER DEFAULT 0;",
                "ALTER TABLE FoodProfiles ADD COLUMN unit TEXT DEFAULT 'g';",
                "ALTER TABLE FoodProfiles ADD COLUMN deleted INTEGER DEFAULT 0;",
                "ALTER TABLE FoodProfiles ADD COLUMN favourite INTEGER DEFAULT 0;",
                "ALTER TABLE FoodProfiles ADD COLUMN hidden INTEGER DEFAULT 0;",
                "ALTER TABLE FoodProfiles ADD COLUMN portionSize INTEGER DEFAULT 0;",
                "ALTER TABLE FoodProfiles ADD COLUMN ingredients TEXT;",
                "ALTER TABLE FoodProfiles ADD COLUMN foodCategories TEXT;",
                "ALTER TABLE FoodProfiles ADD COLUMN defaultPortion FLOAT DEFAULT 1;",
                "ALTER TABLE FoodProfiles ADD COLUMN portionIncrement FLOAT DEFAULT 0.1;",
                "CREATE UNIQUE INDEX index_FoodProfiles_id on FoodProfiles(id);",
                "DROP INDEX [IF EXISTS] index_FoodProfiles_name;"};

        for (String patch : patchup) {
            try {
                SQLiteUtils.execSql(patch);
                UserError.Log.e(TAG, "Processed patch should have succeeded!!: " + patch);
            } catch (Exception e) {
                UserError.Log.d(TAG, "Patch: " + patch + " generated exception as it should: " + e.toString());
            }
        }
        patched = true;
    }

    public static void recreateTable() {
        String[] patchup = {
                "DROP TABLE FoodProfiles;"};

        for (String patch : patchup) {
            try {
                SQLiteUtils.execSql(patch);
                UserError.Log.e(TAG, "Processed patch should have succeeded!!: " + patch);
            } catch (Exception e) {
                UserError.Log.d(TAG, "Patch: " + patch + " generated exception as it should: " + e.toString());
            }
        }
        patched = false;
        fixUpTable();
    }

    public static FoodProfile byFoodID(String id)
    {
        try {
            return new Select()
                    .from(FoodProfile.class)
                    .where("foodID = ?", id)
                    .executeSingle();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            return null;
        }
    }

    public static List<FoodProfile> all() {
        try {
            return new Select()
                    .from(FoodProfile.class)
                    .where("deleted = 0")
                    .execute();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            return null;
        }
    }
}
