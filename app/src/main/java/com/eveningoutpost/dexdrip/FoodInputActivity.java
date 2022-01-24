package com.eveningoutpost.dexdrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import com.eveningoutpost.dexdrip.Models.FoodIntake;
import com.eveningoutpost.dexdrip.Models.JoH;
import com.eveningoutpost.dexdrip.Models.Treatments;
import com.eveningoutpost.dexdrip.UtilityModels.Constants;
import com.eveningoutpost.dexdrip.UtilityModels.PersistentStore;
import com.eveningoutpost.dexdrip.food.Food;
import com.eveningoutpost.dexdrip.food.FoodManager;
import com.eveningoutpost.dexdrip.ui.dialog.GenericConfirmDialog;
import org.apache.commons.lang3.time.DateUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static com.eveningoutpost.dexdrip.xdrip.gs;

/**
 * Adapted from PhoneKeypadInputActivity
 */

// gruoner xdrip plus

public class FoodInputActivity extends BaseActivity {

    private TextView timeTextView, carbsTextView, energyTextView, submitTextView;
    private Button catButton1, catButton2, catButton3, catButton4, catButtonAll;
    private ImageButton timetabbutton;
    private Activity meMyselfAndI;
    final int offColor = Color.DKGRAY;
    final int onColor = Color.RED;

    private String currentcat;
    private Boolean catChanged;
    private static final String LAST_TAB_STORE = "food-treatment-last-tab";
    private static final String TAG = "FoodInput";
    private static FoodIntake intake = new FoodIntake();
    private LinearLayout selectedFoodView = null;
    private LinearLayout allFoodView = null;
    private Date intakeTimestamp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        meMyselfAndI = this;
        setContentView(R.layout.food_activity_onphone);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        final int refdpi = 320;
        Log.d(TAG, "Width height: " + width + " " + height + " DPI:" + dm.densityDpi);
        getWindow().setLayout((int) Math.min(((750 * dm.densityDpi) / refdpi), width), (int) Math.min((1000 * dm.densityDpi) / refdpi, height));
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.5f;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        timeTextView = (TextView) findViewById(R.id.time_textview);
        carbsTextView = (TextView) findViewById(R.id.carbs_textview);
        energyTextView = (TextView) findViewById(R.id.energy_textview);
        submitTextView = (TextView) findViewById(R.id.submit_textview);
        timetabbutton = (ImageButton) findViewById(R.id.timebutton);
        catButton1 = (Button) findViewById(R.id.cat_button1);
        catButton2 = (Button) findViewById(R.id.cat_button2);
        catButton3 = (Button) findViewById(R.id.cat_button3);
        catButton4 = (Button) findViewById(R.id.cat_button4);
        catButtonAll = (Button) findViewById(R.id.cat_buttonall);
        selectedFoodView = (LinearLayout) findViewById(R.id.food_selected_view);
        allFoodView = (LinearLayout) findViewById(R.id.all_food_view);

        submitTextView.setText("");
        submitTextView.setOnClickListener(v -> submitAll());

        catButton1.setOnClickListener(v -> {
            currentcat = "FoodCat1";
            catChanged = true;
            updateTab();
        });

        catButton2.setOnClickListener(v -> {
            currentcat = "FoodCat2";
            catChanged = true;
            updateTab();
        });

        catButton3.setOnClickListener(v -> {
            currentcat = "FoodCat3";
            catChanged = true;
            updateTab();
        });

        catButton4.setOnClickListener(v -> {
            currentcat = "FoodCat4";
            catChanged = true;
            updateTab();
        });

        catButtonAll.setOnClickListener(v -> {
            currentcat = "*";
            catChanged = true;
            updateTab();
        });

        timetabbutton.setOnClickListener(v -> {
            Date now = new Date();
            TimePickerDialog dia = new TimePickerDialog(v.getContext(), AlertDialog.THEME_HOLO_DARK, (timePicker, i, i1) -> {
                Date n = new Date();
                n.setHours(i);
                n.setMinutes(i1);
                n.setSeconds(0);
                if (n.getTime()-now.getTime() > Constants.HOUR_IN_MS)
                    intakeTimestamp = DateUtils.addDays(n, -1);
                else intakeTimestamp = n;
                updateTab();
            }, now.getHours(), now.getMinutes(), true);
            dia.show();
        });

        currentcat = "FoodCat1";
        catChanged = true;
        updateTab();
    }

    private void createNumberPickerDialog(Food f, double u, Boolean update, Activity c) {
        ArrayList<String> portions = new ArrayList<>();
        for (double p=0.1; p < 10; p += f.getPortionIncrement())
            portions.add(f.getDescription(p));
        String[] pA = portions.toArray(new String[0]);
        final AlertDialog.Builder d = new AlertDialog.Builder(c, AlertDialog.THEME_HOLO_DARK);
        LayoutInflater inflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
        d.setTitle("Wie viel davon?");
        d.setView(dialogView);
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
        numberPicker.setMaxValue(portions.size()-1);
        numberPicker.setMinValue(0);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setDisplayedValues(pA);
        numberPicker.setValue((int) (Math.round(u/f.getPortionIncrement()) - 1));
        d.setPositiveButton("Eintragen", (dialogInterface, i) -> {
            double v = (numberPicker.getValue() + 1) * f.getPortionIncrement();
            if (update) intake.removeIngredient(f.getID());
            intake.addIngredient(Objects.requireNonNull(FoodManager.getFood(f.getID())), v);
            updateTab();
        });
        if (update)
            d.setNegativeButton("Löschen", (dialogInterface, i) -> {
                intake.removeIngredient(f.getID());
                updateTab();
            });
        d.setNeutralButton("Abbrechen", (dialogInterface, i) -> updateTab());

        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }

    private void submitAll() {
        if (intakeTimestamp == null)
            intakeTimestamp = new Date();
        DateFormat dateFormat = new SimpleDateFormat("E dd.MM.yyyy 'um' HH:mm");
        String mystring = String.format("Am %s %dg Kohlenhydrate (%s) gegessen.", dateFormat.format(intakeTimestamp), intake.getCarbs(), intake.getFoodIntakeShortString(1));

        GenericConfirmDialog.show(this, gs(R.string.are_you_sure), mystring, () -> {
            Treatments.create(intake.getCarbs(), intake, 0, null, intakeTimestamp.getTime());
            intakeTimestamp = null;
            intake = new FoodIntake();
            JoH.startActivity(Home.class);
            finish();
        });
    }

    private void updateTab() {
        catButton1.setBackgroundColor(offColor);
        catButton2.setBackgroundColor(offColor);
        catButton3.setBackgroundColor(offColor);
        catButton4.setBackgroundColor(offColor);
        catButtonAll.setBackgroundColor(offColor);
        submitTextView.getBackground().setAlpha(0);

        switch (currentcat) {
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
            case "*":
                catButtonAll.setBackgroundColor(onColor);
                break;
        }
        if (catChanged) {
            allFoodView.removeAllViews();
            for (Food f : FoodManager.getFood())
                if (!f.isDeleted() && !f.isHidden() && f.isInCategory(currentcat)) {
                    TextView t = new TextView(this);
                    t.setText(f.getDescription(1));
                    t.setTextSize(15);
                    t.setTextColor(0xffffffff);
                    t.setOnClickListener(v -> createNumberPickerDialog(f, f.getDefaultPortion(), false, meMyselfAndI));
                    allFoodView.addView(t);
                }
            catChanged = false;
        }

        selectedFoodView.removeAllViews();
        for (Food f: intake.getProfiles())
        {
            TextView t = new TextView(this);
            t.setText(f.getDescription(intake.getUnits(f.getID())));
            t.setTextSize(15);
            t.setTextColor(0xffffffff);
            t.setOnClickListener(v -> {
                createNumberPickerDialog(f, intake.getUnits(f.getID()), true, meMyselfAndI);
                updateTab();
            });
            selectedFoodView.addView(t);
        }
        carbsTextView.setText(intake.getCarbs() + " g");
        energyTextView.setText(intake.getEnergy() + " kJ");

        if (intakeTimestamp != null)
            timeTextView.setText(new SimpleDateFormat("HH:mm").format(intakeTimestamp));
        else timeTextView.setText("");
        if (intake.hasIntakes()) submitTextView.getBackground().setAlpha(255);
    }

    @Override
    protected void onResume() {
        final String savedtab = PersistentStore.getString(LAST_TAB_STORE);
        if (savedtab.length() > 0) currentcat = savedtab;
        catChanged = true;
        updateTab();
        super.onResume();
    }

    @Override
    protected void onPause() {
        PersistentStore.setString(LAST_TAB_STORE, currentcat);
        super.onPause();
    }
}
