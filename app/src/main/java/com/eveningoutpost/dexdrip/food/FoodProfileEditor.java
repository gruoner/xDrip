package com.eveningoutpost.dexdrip.food;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import com.eveningoutpost.dexdrip.BaseAppCompatActivity;
import com.eveningoutpost.dexdrip.R;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by gruoner on 23/01/2022.
 */

public class FoodProfileEditor extends BaseAppCompatActivity {

    private static final String TAG = "foodprofileeditor";
    private String selectedCat;
    LinearLayout FoodView;
    HashMap<String, HashMap<Food, CheckBox>> checkboxes;
    final int offColor = Color.DKGRAY;
    final int onColor = Color.RED;
    Button catButton1, catButton2, catButton3, catButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodprofile_editor);
        checkboxes = new HashMap<>();
        FoodView = (LinearLayout) findViewById(R.id.foodprofileeditor_view);
        selectedCat = "FoodCat1";
        setCheckboxes("FoodCat1");
        setCheckboxes("FoodCat2");
        setCheckboxes("FoodCat3");
        setCheckboxes("FoodCat4");
        catButton1 = (Button) findViewById(R.id.foodcat_button1);
        catButton2 = (Button) findViewById(R.id.foodcat_button2);
        catButton3 = (Button) findViewById(R.id.foodcat_button3);
        catButton4 = (Button) findViewById(R.id.foodcat_button4);
        updateTab();
    }

    public void setCheckboxes(String cat) {
        HashMap<Food, CheckBox> cbs = new HashMap<>();
        for (Food f: FoodManager.getFood())
            if (!f.isDeleted() && !f.isHidden())
            {
                CheckBox cb = new CheckBox(this);
                cb.setText(f.getDescription(1));
                cb.setTextSize(15);
                cb.setTextColor(0xffffffff);
                cb.setChecked(f.isInCategory(cat));
                cbs.put(f, cb);
            }
        checkboxes.put(cat, cbs);
    }

    public void getCheckboxes(String cat) {
        HashMap<Food, CheckBox> cbs = checkboxes.get(cat);
        assert cbs != null;
        for (Food f: cbs.keySet())
        {
            if (Objects.requireNonNull(cbs.get(f)).isChecked())
                f.setCategory(cat);
            else
                f.unsetCategory(cat);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    public void updateTab() {
        catButton1.setBackgroundColor(offColor);
        catButton2.setBackgroundColor(offColor);
        catButton3.setBackgroundColor(offColor);
        catButton4.setBackgroundColor(offColor);

        switch (selectedCat) {
            case "FoodCat1":
                catButton1.setBackgroundColor(onColor);
                break;
            case "FoodCat2":
                catButton2.setBackgroundColor(onColor);
                break;
            case "FoodCat3":
                catButton3.setBackgroundColor(onColor);
                break;
            case "FoodCat4":
                catButton4.setBackgroundColor(onColor);
                break;
        }
        HashMap<Food, CheckBox> cbs = checkboxes.get(selectedCat);
        FoodView.removeAllViews();
        for (Food f: FoodManager.getFood()) {
            assert cbs != null;
            if (cbs.get(f) != null)
                FoodView.addView(cbs.get(f));
        }
    }

    public void foodProfileCancelButton(View myview) {
        finish();
    }
    public void foodProfileSaveButton(View myview) {
        getCheckboxes("FoodCat1");
        getCheckboxes("FoodCat2");
        getCheckboxes("FoodCat3");
        getCheckboxes("FoodCat4");
        finish();
    }
    public void foodProfileUndoButton(View myview) {
        checkboxes = new HashMap<>();
        setCheckboxes("FoodCat1");
        setCheckboxes("FoodCat2");
        setCheckboxes("FoodCat3");
        setCheckboxes("FoodCat4");
        updateTab();
    }

    public void foodCat1Button(View myview)
    { selectedCat = "FoodCat1"; updateTab();  }
    public void foodCat2Button(View myview)
    { selectedCat = "FoodCat2"; updateTab();  }
    public void foodCat3Button(View myview)
    { selectedCat = "FoodCat3"; updateTab();  }
    public void foodCat4Button(View myview)
    { selectedCat = "FoodCat4"; updateTab();  }
}

