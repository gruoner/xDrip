package com.eveningoutpost.dexdrip.utils;

import android.databinding.generated.callback.OnCheckedChangeListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.eveningoutpost.dexdrip.BaseAppCompatActivity;
import com.eveningoutpost.dexdrip.R;
import com.eveningoutpost.dexdrip.insulin.Insulin;
import com.eveningoutpost.dexdrip.insulin.InsulinManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gruoner on 26/06/2022.
 */

public class ConfigureImportExport extends BaseAppCompatActivity {

    private static final String TAG = "ConfigureExportExportSettingsDialog";

    // TODO are these buttons actually used?
    private Button cancelBtn;
    private Button saveBtn;
    private Button undoBtn;
    private Switch localStorageSwitch;
    private Switch webdavStorageSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configureimportexportsettings_editor);

        undoBtn = (Button) findViewById(R.id.importexportUndoBtn);
        saveBtn = (Button) findViewById(R.id.importexportSaveBtn);
        cancelBtn = (Button) findViewById(R.id.importexportCancelbtn);
        localStorageSwitch = (Switch) findViewById(R.id.localstorageswitch);
        localStorageSwitch.setOnCheckedChangeListener( (v, p) -> {
            if (localStorageSwitch.isChecked())
                ((LinearLayout)findViewById(R.id.localstoragelayout)).setVisibility(View.VISIBLE);
            else ((LinearLayout)findViewById(R.id.localstoragelayout)).setVisibility(View.INVISIBLE);
        });
        webdavStorageSwitch = (Switch) findViewById(R.id.webdavstorageswitch);
        webdavStorageSwitch.setOnCheckedChangeListener( (v, p) -> {
            if (webdavStorageSwitch.isChecked())
                ((LinearLayout)findViewById(R.id.webdavstoragelayout)).setVisibility(View.VISIBLE);
            else ((LinearLayout)findViewById(R.id.webdavstoragelayout)).setVisibility(View.INVISIBLE);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void CancelButton(View myview) {
        finish();
    }

    public void SaveButton(View myview) {
        finish();
    }

    public void UndoButton(View myview) {
        finish();
    }

}

