package com.eveningoutpost.dexdrip.food;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.eveningoutpost.dexdrip.BaseAppCompatActivity;
import com.eveningoutpost.dexdrip.R;

/**
 * Created by gruoner on 28/07/2019.
 */

public class FoodProfileEditor extends BaseAppCompatActivity {

    private static final String TAG = "foodprofileeditor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodprofile_editor);


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void foodProfileCancelButton(View myview) {
        finish();
    }

    public void foodProfileSaveButton(View myview) {
        /// todo grt: save to DB
        finish();
    }

    public void foodProfileUndoButton(View myview) {
        /// todo grt: restore UI from last saved state
    }
}

