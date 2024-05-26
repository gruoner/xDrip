package com.eveningoutpost.dexdrip.services;

import static com.eveningoutpost.dexdrip.utilitymodels.Constants.REFRESH_RECORDING_SERVICE_ID;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

import com.eveningoutpost.dexdrip.models.AudioRecorder;
import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.UserError;
import com.eveningoutpost.dexdrip.utilitymodels.Constants;
import com.eveningoutpost.dexdrip.xdrip;

public class RefreshRecordingService extends IntentService {
    private static final String TAG = "RunEveryMinuteService";

    public RefreshRecordingService() {
        super("RefreshRecordingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        UserError.Log.d(TAG, "Running Refresh Recording SERVICE");
        final PendingIntent serviceIntent = PendingIntent.getService(xdrip.getAppContext(), REFRESH_RECORDING_SERVICE_ID, new Intent(xdrip.getAppContext(), RefreshRecordingService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        if (AudioRecorder.isActive())
        {
            if (AudioRecorder.calcMillisTillRestart() < Constants.MINUTE_IN_MS) {
                AudioRecorder.stopAudioRecorder();
                AudioRecorder.create(xdrip.getAppContext());
            }
            JoH.wakeUpIntent(xdrip.getAppContext(), AudioRecorder.calcMillisTillRestart(), serviceIntent);
        }
    }

    public static void startService() {
        UserError.Log.d("RunEveryMinuteService", "starting RunEveryMinuteService");
        if (PendingIntent.getService(xdrip.getAppContext(), REFRESH_RECORDING_SERVICE_ID, new Intent(xdrip.getAppContext(), RefreshRecordingService.class), PendingIntent.FLAG_NO_CREATE) == null)
            xdrip.getAppContext().startService(new Intent(xdrip.getAppContext(), RefreshRecordingService.class));
    }
}
