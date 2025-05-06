package com.example.v4c;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfilePage extends AppCompatActivity {

    private ImageButton btnAddPreference;
    private ChipGroup chipGroupPreferences;
    private List<String> preferences = new ArrayList<>();
    private List<String> availablePreferences = new ArrayList<>(Arrays.asList(
            "Education", "Environment", "Animal Welfare", "Health",
            "Arts & Culture", "Sports", "Community Service"
    ));

    private PieChart chart;
    private ValueLineChart lineChart;
    private BarChart barChart;
    private int i1 = 15;
    private int i2 = 40;
    private int i3 = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page);

        // Set up window insets
        View mainContainer = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainContainer, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize preference UI components
        setupPreferencesUI();

        // Initialize and setup charts
        chart = findViewById(R.id.pie_chart);
        addToPieChart();

        barChart = findViewById(R.id.barChart);
        addToBarChart();
    }

    private void setupPreferencesUI() {
        // Find the add preference button - use your actual button ID
        View addPreferenceButton = findViewById(R.id.add_preference_button);
        if (addPreferenceButton != null) {
            btnAddPreference = (ImageButton) addPreferenceButton;
            btnAddPreference.setOnClickListener(v -> showPreferencesMenu());
        }

        // Find the chip group - use your actual chip group ID
        View chipGroup = findViewById(R.id.preference_chips);
        if (chipGroup != null) {
            chipGroupPreferences = (ChipGroup) chipGroup;

            // Add default "Education" preference
            if (preferences.isEmpty()) {
                preferences.add("Education");
                addPreferenceChip("Education");
            }
        }
    }

    /**
     * Shows the popup menu with available preferences
     */
    private void showPreferencesMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnAddPreference);

        // Add menu items for preferences that aren't already selected
        for (String preference : availablePreferences) {
            if (!preferences.contains(preference)) {
                popupMenu.getMenu().add(preference);
            }
        }

        // Set click listener for menu items
        popupMenu.setOnMenuItemClickListener(item -> {
            String selectedPreference = item.getTitle().toString();
            addPreference(selectedPreference);
            return true;
        });

        popupMenu.show();
    }

    /**
     * Adds a preference to the preferences list and UI
     * @param preference The preference to add
     */
    private void addPreference(String preference) {
        if (!preferences.contains(preference)) {
            preferences.add(preference);
            addPreferenceChip(preference);

            // Show feedback to user
            Toast.makeText(this, "Added " + preference + " to preferences", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates and adds a new chip to the ChipGroup
     * @param preference The preference text for the chip
     */
    private void addPreferenceChip(String preference) {
        Chip chip = new Chip(this);
        chip.setText(preference);
        chip.setCloseIconVisible(true);
        chip.setClickable(true);
        chip.setCheckable(false);

        // Style the chip to match design
        chip.setChipBackgroundColor(ContextCompat.getColorStateList(this, android.R.color.holo_red_light));
        chip.setTextColor(ContextCompat.getColor(this, android.R.color.white));

        // Set close icon click listener to remove the preference
        chip.setOnCloseIconClickListener(v -> removePreference(preference));

        // Add chip to the group
        chipGroupPreferences.addView(chip);
    }

    /**
     * Removes a preference from the list and UI
     * @param preference The preference to remove
     */
    private void removePreference(String preference) {
        preferences.remove(preference);

        // Find and remove the chip from the ChipGroup
        for (int i = 0; i < chipGroupPreferences.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupPreferences.getChildAt(i);
            if (chip.getText().equals(preference)) {
                chipGroupPreferences.removeView(chip);
                break;
            }
        }

        // Show feedback to user
        Toast.makeText(this, "Removed " + preference + " from preferences", Toast.LENGTH_SHORT).show();
    }

    private void addToPieChart() {
        chart.addPieSlice(new PieModel(i1, Color.parseColor("#FFFFFF")));
        chart.addPieSlice(new PieModel(i2, Color.parseColor("#BA8DF3")));
        chart.addPieSlice(new PieModel(i3, Color.parseColor("#F8D6C0")));

        chart.startAnimation();
    }

    private void addToBarChart() {
        barChart.addBar(new BarModel("2023", 3.0f, Color.parseColor("#DB3D3D")));
        barChart.addBar(new BarModel("2023", 5.0f, Color.parseColor("#F5D6C0")));
        barChart.addBar(new BarModel("2024", 6.0f, Color.parseColor("#DB3D3D")));
        barChart.addBar(new BarModel("2024", 4.0f, Color.parseColor("#F5D6C0")));

        barChart.startAnimation();
    }
}