package com.eveningoutpost.dexdrip.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eveningoutpost.dexdrip.Models.AudioRecorder;
import com.eveningoutpost.dexdrip.Models.UserError;

// gruoner

public class RebootReceiver extends BroadcastReceiver {

    private static final String TAG = "Reboot Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        if (AudioRecorder.isActive())
            AudioRecorder.stopAudioRecorder();

        try {
            UserError.Log.ueh(TAG, "Device send to shutdown or reboot - stopping pending actions: " + intent.getAction());
        } catch (Exception e) {
            //
        }

    }
}
