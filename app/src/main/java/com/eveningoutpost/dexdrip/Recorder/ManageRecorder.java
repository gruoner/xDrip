package com.eveningoutpost.dexdrip.Recorder;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.eveningoutpost.dexdrip.Home;
import com.eveningoutpost.dexdrip.models.AudioRecorder;
import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.R;

import com.eveningoutpost.dexdrip.services.RefreshRecordingService;
import com.eveningoutpost.dexdrip.utilitymodels.Pref;
import com.eveningoutpost.dexdrip.ui.dialog.GenericConfirmDialog;
import com.eveningoutpost.dexdrip.utils.ActivityWithMenu;

import static com.eveningoutpost.dexdrip.xdrip.getAppContext;
import static com.eveningoutpost.dexdrip.xdrip.gs;

public class ManageRecorder extends ActivityWithMenu {
   public Button AudioRecorderButton;
   private Spinner refreshRecordingPeriodeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoH.fixActionBar(this);
        setContentView(R.layout.activity_manage_recorder);
        AudioRecorderButton = (Button)findViewById(R.id.manage_audio_recorder);
        refreshRecordingPeriodeSpinner = (Spinner)findViewById(R.id.refresh_recording_periode_spinner);
        addListenerOnButton();
        refreshRecordingPeriodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Pref.setInt("refresh_recording_periode", i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        refreshRecordingPeriodeSpinner.setSelection(Pref.getInt("refresh_recording_periode", 0));
        if(AudioRecorder.isActive() && AudioRecorder.isRunning()) {
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
        ManageRecorder activity = this;
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
        Pref.setBoolean("audio_recorder_started", true);
        Home.staticRefreshBGCharts();
        RefreshRecordingService.startService();
    }
    public synchronized static void AudioRecorderStop() {
        Pref.setBoolean("audio_recorder_started", false);
        AudioRecorder.stopAudioRecorder();
        Home.staticRefreshBGCharts();
    }
}
