package com.eveningoutpost.dexdrip.food;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.eveningoutpost.dexdrip.Home;
import com.eveningoutpost.dexdrip.Models.FoodProfile;
import com.eveningoutpost.dexdrip.Models.JoH;
import com.eveningoutpost.dexdrip.UtilityModels.NightscoutUploader;

/**
 * Created by gruoner on 12/03/2022.
 * <p>
 * drops AA table foodprofiles, creates a new one and kick off a food download request
 */

public class DropFoodDBActivity extends AppCompatActivity {

    private final static String TAG = "DropFoodDBActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FoodProfile.recreateTable();
        JoH.static_toast_long("dropped and recreated food database");

        JoH.clearRatelimit("ns-food-download");
        JoH.static_toast_long("triggered a complete food download from nightscout");
        JoH.startActivity(Home.class);
        finish();
        if (MultipleCarbs.isDownloadableByUploader())
            NightscoutUploader.launchDownloadRest();
    }
}
