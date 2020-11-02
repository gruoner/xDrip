package com.eveningoutpost.dexdrip.Models;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import com.eveningoutpost.dexdrip.insulin.Insulin;
import com.eveningoutpost.dexdrip.insulin.InsulinManager;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Table(name = "InsulinProfiles", id = BaseColumns._ID)
public class InsulinProfile extends Model {

    private static final String TAG = "gruoner " + Insulin.class.getSimpleName();
    private static boolean patched = false;

    @Column(name = "lastupdate")
    private long lastupdate;
    @Column(name = "displayName")
    private String displayName;
    @Column(name = "name")
    private String name;
    @Column(name = "pharmacyProductNumber")
    private String pharmacyProductNumber;
    @Column(name = "curve")
    private String curve;
    @Column(name = "deleted")
    private Integer deleted;

    public InsulinProfile() {
        super();
    }
    public InsulinProfile(String n, String dn, List<String> ppn, InsulinManager.insulinCurve curveData, Boolean del) {
        name = n;
        displayName = dn;
        Gson gson = new Gson();
        pharmacyProductNumber = gson.toJson(ppn, List.class);
        curve = gson.toJson(curveData, InsulinManager.insulinCurve.class);
        if (del) deleted = 1;
        else deleted = 0;
    }

    public static InsulinProfile create(String n, String d, List<String> ppn, InsulinManager.insulinCurve cu, Boolean del)
    {
        InsulinProfile ret = new InsulinProfile(n, d, ppn, cu, del);
        ret.setLastupdate(JoH.tsl());
        try {
            ret.save();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            ret.save();
        }
        return ret;
    }

    public String getName() {
        return name;
    }
    public String getDisplayName() {
        return displayName;
    }
    public ArrayList<String> getPharmacyProductNumber() {
        Gson gson = new Gson();
        try {
            ArrayList<String> ret = gson.fromJson(pharmacyProductNumber, (Type) List.class);
            return ret;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    public InsulinManager.insulinCurve getCurve() {
        Gson gson = new Gson();
        InsulinManager.insulinCurve ret = gson.fromJson(curve, InsulinManager.insulinCurve.class);
        return ret;
    }
    public Boolean isDeleted() {
        if (deleted == null) return false;
        if (deleted == 0) return false;
        else return true;
    }

    public void setLastupdate(long lastupdate) {
        this.lastupdate = lastupdate;
    }
    public void setDisplayName(String dn) {
        displayName = dn;
        setLastupdate(JoH.tsl());
        try {
            save();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            save();
        }
    }
    public void setPharmacyProductNumber(List<String> ppn) {
        Gson gson = new Gson();
        pharmacyProductNumber = gson.toJson(ppn, List.class);
        setLastupdate(JoH.tsl());
        try {
            save();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            save();
        }
    }
    public void setCurve(InsulinManager.insulinCurve c) {
        Gson gson = new Gson();
        this.curve = gson.toJson(c, InsulinManager.insulinCurve.class);
        setLastupdate(JoH.tsl());
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
        setLastupdate(JoH.tsl());
        try {
            save();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            save();
        }
    }

    // This shouldn't be needed but it seems it is
    private static void fixUpTable() {
        if (patched) return;
        String[] patchup = {
                "CREATE TABLE InsulinProfiles (_id INTEGER PRIMARY KEY AUTOINCREMENT);",
                "ALTER TABLE InsulinProfiles ADD COLUMN lastupdate INTEGER;",
                "ALTER TABLE InsulinProfiles ADD COLUMN name TEXT;",
                "ALTER TABLE InsulinProfiles ADD COLUMN displayName TEXT;",
                "ALTER TABLE InsulinProfiles ADD COLUMN concentration TEXT;",
                "ALTER TABLE InsulinProfiles ADD COLUMN pharmacyProductNumber TEXT;",
                "ALTER TABLE InsulinProfiles ADD COLUMN curve TEXT;",
                "ALTER TABLE InsulinProfiles ADD COLUMN deleted INTEGER DEFAULT 0;",
                "CREATE INDEX index_InsulinProfiles_lastupdate on InsulinProfiles(lastupdate);",
                "CREATE UNIQUE INDEX index_InsulinProfiles_name on InsulinProfiles(name);"};

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

    public static InsulinProfile byName(String n)
    {
        try {
            return new Select()
                    .from(InsulinProfile.class)
                    .where("name = ?", n)
                    .executeSingle();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            return null;
        }
    }

    public static List<InsulinProfile> all() {
        try {
            return new Select()
                    .from(InsulinProfile.class)
                    .execute();
        } catch (android.database.sqlite.SQLiteException e) {
            fixUpTable();
            return null;
        }
    }
}
