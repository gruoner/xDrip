package com.eveningoutpost.dexdrip.utilitymodels;

import android.os.AsyncTask;
import com.eveningoutpost.dexdrip.models.UserError.Log;
import com.eveningoutpost.dexdrip.xdrip;

/**
 * Created by gruoner on 24/05/20.
 */

// TODO unify treatment handling


public class DevicestatusUploaderTask extends AsyncTask<String, Void, Void> {
    public static Exception exception;
    private static final String TAG = DevicestatusUploaderTask.class.getSimpleName();

    public Void doInBackground(String... urls) {
        try {
            Log.d(TAG, "DevicestatusUploaderTask doInBackground called");
            if (NightscoutUploader.statusUploadEnabled() && NightscoutUploader.time2UploadStatus()) {
                final NightscoutUploader uploader = new NightscoutUploader(xdrip.getAppContext());
                uploader.doStatusUpload();
            } else {
                Log.e(TAG, "Skipping devicestatus upload to nightscout because disabled or not the time");
            }
        } catch (Exception e) {
            Log.e(TAG, "caught exception", e);
            exception = e;
            return null;
        }
        return null;
    }
}
