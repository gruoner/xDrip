package com.eveningoutpost.dexdrip.Recorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.eveningoutpost.dexdrip.G5Model.Ob1G5StateMachine;
import com.eveningoutpost.dexdrip.Home;
import com.eveningoutpost.dexdrip.LibreAlarmReceiver;
import com.eveningoutpost.dexdrip.Models.AudioRecorder;
import com.eveningoutpost.dexdrip.Models.Calibration;
import com.eveningoutpost.dexdrip.Models.JoH;
import com.eveningoutpost.dexdrip.Models.Sensor;
import com.eveningoutpost.dexdrip.R;
import com.eveningoutpost.dexdrip.StartNewSensor;
import com.eveningoutpost.dexdrip.UtilityModels.AlertPlayer;
import com.eveningoutpost.dexdrip.UtilityModels.CollectionServiceStarter;
import com.eveningoutpost.dexdrip.UtilityModels.Inevitable;
import com.eveningoutpost.dexdrip.UtilityModels.NanoStatus;
import com.eveningoutpost.dexdrip.calibrations.PluggableCalibration;
import com.eveningoutpost.dexdrip.ui.dialog.GenericConfirmDialog;
import com.eveningoutpost.dexdrip.utils.ActivityWithMenu;
import com.eveningoutpost.dexdrip.xdrip;

import lombok.val;

import static com.eveningoutpost.dexdrip.xdrip.getAppContext;
import static com.eveningoutpost.dexdrip.xdrip.gs;

public class ManageRecorder extends ActivityWithMenu {
   public Button AudioRecorderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoH.fixActionBar(this);
        setContentView(R.layout.activity_manage_recorder);
        AudioRecorderButton = (Button)findViewById(R.id.manage_audio_recorder);
        addListenerOnButton();
        if(AudioRecorder.isActive()) {
            AudioRecorderButton.setText("Recorder stoppen");
        } else {
            AudioRecorderButton.setText("Recorder starten");
        }
    }

    @Override
    public String getMenuName() {
        return "Recorder verwalten";
    }

    public void addListenerOnButton() {
        val activity = this;
        if (AudioRecorder.isActive())
            AudioRecorderButton.setOnClickListener(v -> GenericConfirmDialog.show(activity, gs(R.string.are_you_sure), "Do you want to stop this recorder?", () -> {
                AudioRecorderStop();
                JoH.startActivity(Home.class);
                finish();
            }));
        else
            AudioRecorderButton.setOnClickListener(v -> GenericConfirmDialog.show(activity, gs(R.string.are_you_sure), "Do you want to start this recorder?", () -> {
                AudioRecorderStart();
                JoH.startActivity(Home.class);
                finish();
            }));

    }

    public synchronized static void AudioRecorderStart() {
        AudioRecorder r = AudioRecorder.create(getAppContext());
        Home.staticRefreshBGCharts();
    }
    public synchronized static void AudioRecorderStop() {
        AudioRecorder.stopAudioRecorder();
        Home.staticRefreshBGCharts();
    }
}
