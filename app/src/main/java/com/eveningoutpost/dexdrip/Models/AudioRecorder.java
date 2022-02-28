package com.eveningoutpost.dexdrip.Models;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import com.eveningoutpost.dexdrip.Home;
import com.eveningoutpost.dexdrip.Models.UserError.Log;
import com.eveningoutpost.dexdrip.UtilityModels.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.internal.bind.DateTypeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Table(name = "AudioRecorder", id = BaseColumns._ID)
public class AudioRecorder extends Model {
    private final static String TAG = AudioRecorder.class.getSimpleName();
    private static MediaRecorder recorder;
    private static boolean patched = false;

    @Expose
    @Column(name = "started_at", index = true)
    public long started_at;

    @Expose
    @Column(name = "stopped_at")
    public long stopped_at;

    @Expose
    @Column(name = "uuid", index = true)
    public String uuid;

    @Expose
    @Column(name = "audio_file")
    public String audio_file;

    public static AudioRecorder create(Context c, long started_at) {
        AudioRecorder r = new AudioRecorder();
        r.started_at = started_at;
        r.uuid = UUID.randomUUID().toString();
//        r.audio_file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
        r.audio_file = c.getFilesDir() +
                "/audiorecord-" + JoH.dateTimeText(JoH.tsl()).replace(" ", "_") + ".3gp";
        r.save();
        /// todo: hier muss der Recordercode zum Starten rein
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(r.audio_file);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        } catch (Exception e)
        {
            Log.wtf(TAG, "Failed to create audiorecorder - init failed");
            UserError.Log.e(TAG, "Got exception in init audiorecorder: " + e);
            r.delete();
        }
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.wtf(TAG, "Failed to create audiorecorder - prepare failed");
            UserError.Log.e(TAG, "Got exception in preparing audiorecorder: " + e);
            r.delete();
        }
        recorder.start();
        return r;
    }
    public static AudioRecorder create(Context c) {
        return AudioRecorder.create(c, JoH.tsl());
    }

    public synchronized static void stopAudioRecorder() {
        final AudioRecorder r = currentAudioRecorder();
        if (r == null) {
            return;
        }
        r.stopped_at = JoH.tsl();
        r.save();
        if (currentAudioRecorder() != null) {
            Log.wtf(TAG, "Failed to update audiorecorder stop in database");
        }
/// todo: hier muss der StopCode für den Audiorecorder rein
        if (recorder != null) {
            recorder.stop();
            recorder.release();
        }
        JoH.clearCache();
    }

    public String toS() {//KS
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .serializeSpecialFloatingPointValues()
                .create();
        Log.d("AUDIORECORDER", "AudioRecorder toS uuid=" + this.uuid + " started_at=" + this.started_at + " active=" + this.isActive() + " location=" + this.audio_file + " stopped_at=" + this.stopped_at);
        return gson.toJson(this);
    }

    public static AudioRecorder currentAudioRecorder() {
        fixUpTable();
        AudioRecorder r = new Select()
                .from(AudioRecorder.class)
                .where("started_at != 0")
                .where("stopped_at = 0")
                .orderBy("_ID desc")
                .limit(1)
                .executeSingle();
        return r;
    }

    public static boolean isActive() {
        fixUpTable();
        AudioRecorder r = new Select()
                .from(AudioRecorder.class)
                .where("started_at != 0")
                .where("stopped_at = 0")
                .orderBy("_ID desc")
                .limit(1)
                .executeSingle();
        if(r == null) {
            return false;
        } else {
            return true;
        }
    }

    public static AudioRecorder getByUuid(String xDrip_audiorecorder_uuid) {
        if(xDrip_audiorecorder_uuid == null) {
            Log.e("AUDIORECORDER", "xDrip_audiorecorder_uuid is null");
            return null;
        }
        Log.d("AUDIORECORDER", "xDrip_audiorecorder_uuid is " + xDrip_audiorecorder_uuid);

        fixUpTable();
        return new Select()
                .from(AudioRecorder.class)
                .where("uuid = ?", xDrip_audiorecorder_uuid)
                .executeSingle();
    }

    public static void updateAudioFile(String audio_file) {
        AudioRecorder r = currentAudioRecorder();
        if (r == null) {
            Log.e("AUDIORECORDER:", "updateAudioFile called but AudioRecorder is null");
            return;
        }
        r.audio_file = audio_file;
        r.save();
    }

    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("started_at", started_at);
            jsonObject.put("stopped_at", stopped_at);
            jsonObject.put("uuid", uuid);
            jsonObject.put("audio_file", audio_file);
            return jsonObject.toString();
        } catch (JSONException e) {
            Log.e(TAG,"Got JSONException handeling AudioRecorder", e);
            return "";
        }
    }
    
    public static AudioRecorder fromJSON(String json) {
        if (json.length()==0) {
            Log.d(TAG,"Empty json received in AudioRecorder fromJson");
            return null;
        }
        try {
            Log.d(TAG, "Processing incoming json: " + json);
           return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(json, AudioRecorder.class);
        } catch (Exception e) {
            Log.d(TAG, "Got exception parsing AudioRecorder json: " + e.toString());
            Home.toaststaticnext("Error on AudioRecorder sync.");
            return null;
        }
    }

    private static void fixUpTable() {
        if (patched) return;
        String[] patchup = {
                "CREATE TABLE AudioRecorder (_id INTEGER PRIMARY KEY AUTOINCREMENT);",
                "ALTER TABLE AudioRecorder ADD COLUMN uuid TEXT;",
                "ALTER TABLE AudioRecorder ADD COLUMN started_at INTEGER;",
                "ALTER TABLE AudioRecorder ADD COLUMN stopped_at INTEGER;",
                "ALTER TABLE AudioRecorder ADD COLUMN audio_file TEXT;",
                "CREATE INDEX index_AudioRecorder_timestamp on AudioRecorder(timestamp);",
                "CREATE UNIQUE INDEX index_AudioRecorder_uuid on AudioRecorder(uuid);"};

        for (String patch : patchup) {
            try {
                SQLiteUtils.execSql(patch);
                //Log.e(TAG, "Processed patch should not have succeeded!!: " + patch);
            } catch (Exception e) {
                // Log.d(TAG, "Patch: " + patch + " generated exception as it should: " + e.toString());
            }
        }
        patched = true;
    }

}

