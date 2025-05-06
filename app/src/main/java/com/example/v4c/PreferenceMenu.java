// MainActivity.java
package com.example.v4c;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

public class PreferenceMenu extends AppCompatActivity {

    private ImageButton plusButton;
    private LinearLayout preferencesLayout;
    private LinearLayout tagsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

//    private void showPreferenceMenu() {
//        // Create popup menu
//        PopupMenu popupMenu = new PopupMenu(this, plusButton);
//
//        // Inflate menu resource
//        popupMenu.getMenuInflater().inflate(R.menu.preferences_menu, popupMenu.getMenu());
//
//        // Set click listener for menu items
//        popupMenu.setOnMenuItemClickListener(item -> {
//            // Add the selected preference as a tag
//            addPreferenceTag(item.getTitle().toString());
//            return true;
//        });
//
//        // Show the popup menu
//        popupMenu.show();
//    }

//    private void addPreferenceTag(String preference) {
//        // Create a new button for the tag
//        Button tagButton = new Button(this);
//        tagButton.setText(preference);
//        tagButton.setAllCaps(true);
//        tagButton.setBackgroundColor(getResources().getColor(R.color.tag_red));
//        tagButton.setTextColor(getResources().getColor(R.color.white));
//
//        // Set layout parameters for the button
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(8, 8, 8, 8);
//        tagButton.setLayoutParams(params);
//
//        // Add button to the container
//        tagsContainer.addView(tagButton);
//
//        // Optional: Add a click listener to remove the tag if desired
//        tagButton.setOnClickListener(v -> tagsContainer.removeView(v));
//    }
}