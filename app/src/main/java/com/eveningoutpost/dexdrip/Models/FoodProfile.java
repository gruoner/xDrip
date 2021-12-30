package com.eveningoutpost.dexdrip.Models;

import android.provider.BaseColumns;
import com.activeandroid.Model;
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
    @Column(name = "hidden")
    private Integer hidden;
    @Column(name = "ingredients")
    private String ingredients;

    public FoodProfile() {
        super();
    }
    public FoodProfile(String id, String n, String type, String g, int e, int p, int f, int c, String u, int portion, Boolean del, Boolean h, String i) {
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
    }

    public static FoodProfile create(String id, String dn, String type, String g, int e, int p, int f, int c, String u, int portion, Boolean del, Boolean h, String i)
    {
        FoodProfile ret = new FoodProfile(id, dn, type, g, e, p, f, c, u, portion, del, h, i);
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
    public int getfat() {
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
    public Boolean isDeleted() {
        if (deleted == null) return false;
        if (deleted == 0) return false;
        else return true;
    }
    public Boolean isHidden() {
        if (hidden == null) return false;
        if (hidden == 0) return false;
        else return true;
    }
    public String getIngredients() {
        return Objects.toString(ingredients, "");
    }

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
    public void setDeleted(Boolean del) {
        if (del) deleted = 1;
        else deleted = 0;
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

    // This shouldn't be needed but itportionSize seems it is
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
                "ALTER TABLE FoodProfiles ADD COLUMN hidden INTEGER DEFAULT 0;",
                "ALTER TABLE FoodProfiles ADD COLUMN portionSize INTEGER DEFAULT 0;",
                "ALTER TABLE FoodProfiles ADD COLUMN ingredients TEXT;",
                "CREATE UNIQUE INDEX index_FoodProfiles_id on FoodProfiles(id);",
                "CREATE UNIQUE INDEX index_FoodProfiles_name on FoodProfiles(name);"};

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

    public static FoodProfile byName(String n)
    {
        try {
            return new Select()
                    .from(FoodProfile.class)
                    .where("name = ?", n)
                    .executeSingle();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            return null;
        }
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
                    .execute();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            return null;
        }
    }
}
