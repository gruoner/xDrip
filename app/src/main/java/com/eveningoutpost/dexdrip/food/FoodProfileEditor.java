package com.eveningoutpost.dexdrip.food;

import android.content.pm.ActivityInfo;
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
    HashMap<String, HashMap<Food, LinearLayout>> Checkboxes;
    HashMap<String, HashMap<Food, CheckBox>> catCheckboxes;
    HashMap<String, HashMap<Food, CheckBox>> favCheckboxes;
    final int offColor = Color.DKGRAY;
    final int onColor = Color.RED;
    Button catButton1, catButton2, catButton3, catButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodprofile_editor);
        Checkboxes = new HashMap<>();
        catCheckboxes = new HashMap<>();
        favCheckboxes = new HashMap<>();
        FoodView = (LinearLayout) findViewById(R.id.foodprofileeditor_view);
        selectedCat = "FoodCat1";
        setCatCheckboxes("FoodCat1");
        setCatCheckboxes("FoodCat2");
        setCatCheckboxes("FoodCat3");
        setCatCheckboxes("FoodCat4");
        catButton1 = (Button) findViewById(R.id.foodcat_button1);
        catButton2 = (Button) findViewById(R.id.foodcat_button2);
        catButton3 = (Button) findViewById(R.id.foodcat_button3);
        catButton4 = (Button) findViewById(R.id.foodcat_button4);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        updateTab();
    }

    public void setCatCheckboxes(String cat) {
        HashMap<Food, LinearLayout> lvs = new HashMap<>();
        HashMap<Food, CheckBox> cbsCat = new HashMap<>();
        HashMap<Food, CheckBox> cbsFav = new HashMap<>();
        for (Food f: FoodManager.getFood())
            if (!f.isDeleted() && !f.isHidden())
            {
                LinearLayout v = new LinearLayout(this);
                v.setOrientation(LinearLayout.HORIZONTAL);
                CheckBox cbFav = new CheckBox(this);
                cbFav.setChecked(f.isFavourite());
                cbFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean c = cbFav.isChecked();
                        for (String cat: catCheckboxes.keySet())
                            favCheckboxes.get(cat).get(f).setChecked(c);
                    }
                });
                cbsFav.put(f, cbFav);
                v.addView(cbFav);
                CheckBox cbCat = new CheckBox(this);
                cbCat.setText(f.getDescription(1));
                cbCat.setTextSize(15);
                cbCat.setTextColor(0xffffffff);
                cbCat.setChecked(f.isInCategory(cat));
                v.addView(cbCat);
                lvs.put(f, v);
                cbsCat.put(f, cbCat);
            }
        Checkboxes.put(cat, lvs);
        catCheckboxes.put(cat, cbsCat);
        favCheckboxes.put(cat, cbsFav);
    }

    public void getCatCheckboxes(String cat) {
        HashMap<Food, CheckBox> cbs = catCheckboxes.get(cat);
        assert cbs != null;
        for (Food f: cbs.keySet())
        {
            if (Objects.requireNonNull(cbs.get(f)).isChecked())
                f.setCategory(cat);
            else
                f.unsetCategory(cat);
        }
    }
    public void getFavCheckboxes(String cat) {
        HashMap<Food, CheckBox> cbs = favCheckboxes.get(cat);
        assert cbs != null;
        for (Food f: cbs.keySet())
        {
            if (Objects.requireNonNull(cbs.get(f)).isChecked())
                f.setFavourite();
            else
                f.unsetFavourite();
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
        HashMap<Food, LinearLayout> cbs = Checkboxes.get(selectedCat);
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
        getCatCheckboxes("FoodCat1");
        getCatCheckboxes("FoodCat2");
        getCatCheckboxes("FoodCat3");
        getCatCheckboxes("FoodCat4");
        getFavCheckboxes("FoodCat1");
        FoodManager.resetInitialization();
        finish();
    }
    public void foodProfileUndoButton(View myview) {
        Checkboxes = new HashMap<>();
        catCheckboxes = new HashMap<>();
        favCheckboxes = new HashMap<>();
        setCatCheckboxes("FoodCat1");
        setCatCheckboxes("FoodCat2");
        setCatCheckboxes("FoodCat3");
        setCatCheckboxes("FoodCat4");
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

