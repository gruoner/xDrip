package com.eveningoutpost.dexdrip.services;

import static com.eveningoutpost.dexdrip.utilitymodels.Constants.EVERYMINUTE_RETRY_ID;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.UserError.Log;
import com.eveningoutpost.dexdrip.utilitymodels.Constants;
import com.eveningoutpost.dexdrip.utilitymodels.DevicestatusUploaderTask;
import com.eveningoutpost.dexdrip.xdrip;

public class RunEveryMinuteService extends IntentService {
    private static final String TAG = "RunEveryMinuteService";

    public RunEveryMinuteService() {
        super("RunEveryMinuteService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Running Run-Every-Minute SERVICE");
        final PendingIntent serviceIntent = PendingIntent.getService(xdrip.getAppContext(), EVERYMINUTE_RETRY_ID, new Intent(xdrip.getAppContext(), RunEveryMinuteService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        JoH.wakeUpIntent(xdrip.getAppContext(), Constants.MINUTE_IN_MS, serviceIntent);
        doIt();
    }

    private void doIt() {
        doDevicestatusUpload();
    }

    private void doDevicestatusUpload() {
        final DevicestatusUploaderTask task = new DevicestatusUploaderTask();
        task.executeOnExecutor(xdrip.executor);
    }

    public static void startService() {
        Log.d("RunEveryMinuteService", "starting RunEveryMinuteService");
        if (PendingIntent.getService(xdrip.getAppContext(), EVERYMINUTE_RETRY_ID, new Intent(xdrip.getAppContext(), RunEveryMinuteService.class), PendingIntent.FLAG_NO_CREATE) == null)
            xdrip.getAppContext().startService(new Intent(xdrip.getAppContext(), RunEveryMinuteService.class));
    }
}
