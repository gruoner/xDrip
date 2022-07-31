package com.eveningoutpost.dexdrip.food;

import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.eveningoutpost.dexdrip.Home;
import com.eveningoutpost.dexdrip.Models.BgReading;
import com.eveningoutpost.dexdrip.Models.JoH;
import com.eveningoutpost.dexdrip.Models.Treatments;
import com.eveningoutpost.dexdrip.NavigationDrawerFragment;
import com.eveningoutpost.dexdrip.R;
import com.eveningoutpost.dexdrip.Services.SyncService;
import com.eveningoutpost.dexdrip.UtilityModels.Constants;
import com.eveningoutpost.dexdrip.UtilityModels.NightscoutUploader;
import com.eveningoutpost.dexdrip.UtilityModels.PersistentStore;
import com.eveningoutpost.dexdrip.UtilityModels.UploaderQueue;
import com.eveningoutpost.dexdrip.UtilityModels.UploaderTask;
import com.eveningoutpost.dexdrip.profileeditor.DatePickerFragment;
import com.eveningoutpost.dexdrip.profileeditor.ProfileAdapter;
import com.eveningoutpost.dexdrip.xdrip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.eveningoutpost.dexdrip.xdrip.gs;

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
        JoH.clearRatelimit("ns-food-download");
        JoH.static_toast_long("triggered a complete food download from nightscout");
        JoH.startActivity(Home.class);
        finish();
        if (MultipleCarbs.isDownloadableByUploader())
            NightscoutUploader.launchDownloadRest();
    }
}
