package com.eveningoutpost.dexdrip.Models;

import android.provider.BaseColumns;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import com.eveningoutpost.dexdrip.food.FoodManager;
import com.google.gson.Gson;
import java.util.List;

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
    @Column(name = "unit")
    private String unit;
    @Column(name = "portionSize")
    private int portionSize;
    @Column(name = "deleted")
    private Integer deleted;
    @Column(name = "hidden")
    private Integer hidden;

    public FoodProfile() {
        super();
    }
    public FoodProfile(String id, String n, String type, String u, int portion, Boolean del, Boolean h) {
        this.foodID = id;
        name = n;
        this.type = type;

        unit = u;
        this.portionSize = portion;
        if (del) deleted = 1;
        else deleted = 0;
        if (h) hidden = 1;
        else hidden = 0;
    }

    public static FoodProfile create(String id, String dn, String type, String u, int portion, Boolean del, Boolean h)
    {
        FoodProfile ret = new FoodProfile(id, dn, type, u, portion, del, h);
        try {
            ret.save();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            ret.save();
        }
        return ret;
    }

    public String getFoodID() {
        return foodID;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getUnit() {
        return unit;
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

    public void setName(String n) {
        name = n;
        try {
            save();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            save();
        }
    }
    public void setType(String t) {
        type = t;
        try {
            save();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            save();
        }
    }
    public void setUnit(String n) {
        unit = n;
        try {
            save();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            save();
        }
    }
    public void setPortionSize(int n) {
        portionSize = n;
        try {
            save();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            save();
        }
    }
    public void setDeleted(Boolean del) {
        if (del) deleted = 1;
        else deleted = 0;
        try {
            save();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            save();
        }
    }
    public void setHidden(Boolean h) {
        if (h) hidden = 1;
        else hidden = 0;
        try {
            save();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            save();
        }
    }

    // This shouldn't be needed but itportionSize seems it is
    private static void fixUpTable() {
        if (patched) return;
        String[] patchup = {
                "CREATE TABLE FoodProfiles (_id INTEGER PRIMARY KEY AUTOINCREMENT);",
                "ALTER TABLE FoodProfiles ADD COLUMN foodID TEXT;",
                "ALTER TABLE FoodProfiles ADD COLUMN name TEXT;",
                "ALTER TABLE FoodProfiles ADD COLUMN type TEXT;",
                "ALTER TABLE FoodProfiles ADD COLUMN unit TEXT;",
                "ALTER TABLE FoodProfiles ADD COLUMN deleted INTEGER DEFAULT 0;",
                "ALTER TABLE FoodProfiles ADD COLUMN hidden INTEGER DEFAULT 0;",
                "ALTER TABLE FoodProfiles ADD COLUMN portionSize INTEGER DEFAULT 0;",
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
