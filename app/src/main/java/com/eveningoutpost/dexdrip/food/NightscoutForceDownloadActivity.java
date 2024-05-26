package com.eveningoutpost.dexdrip.food;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.eveningoutpost.dexdrip.Home;
import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.utilitymodels.NightscoutUploader;

/**
 * Created by gruoner on 12/03/2022.
 * <p>
 * Manage and kick off a food download request
 */

public class NightscoutForceDownloadActivity extends AppCompatActivity {

    private final static String TAG = "ForceDownloadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoH.clearRatelimit(FoodManager.NAME4nsfollow_food_downloadRATE);
        FoodManager.resetLastFoodDownload();
        JoH.static_toast_long("triggered a complete food download from nightscout");
        JoH.startActivity(Home.class);
        finish();
        if (MultipleCarbs.isDownloadableByUploader())
            NightscoutUploader.launchDownloadRest();
    }
}
