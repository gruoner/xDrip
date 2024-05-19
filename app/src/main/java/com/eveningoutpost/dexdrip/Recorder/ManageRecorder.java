package com.eveningoutpost.dexdrip.Recorder;

import android.os.Bundle;
import android.widget.Button;

import com.eveningoutpost.dexdrip.Home;
import com.eveningoutpost.dexdrip.models.AudioRecorder;
import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.R;

import com.eveningoutpost.dexdrip.utilitymodels.Pref;
import com.eveningoutpost.dexdrip.ui.dialog.GenericConfirmDialog;
import com.eveningoutpost.dexdrip.utils.ActivityWithMenu;
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
    }
    public synchronized static void AudioRecorderStop() {
        Pref.setBoolean("audio_recorder_started", false);
        AudioRecorder.stopAudioRecorder();
        Home.staticRefreshBGCharts();
    }
}
