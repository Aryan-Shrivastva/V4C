package com.example.v4c;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Volunteer_Roles extends AppCompatActivity {

    private LinearLayout rolesContainer;
    private int roleCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_roles);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rolesContainer = findViewById(R.id.rolesContainer);

        Button addAnotherRoleButton = findViewById(R.id.addAnotherRole);



        addAnotherRoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roleCount++;
                addNewRoleSection();
            }
        });

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVolunteerRoles();
            }
        });
    }

    private void addNewRoleSection() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View roleView = inflater.inflate(R.layout.role_item, null);

        TextView roleNumberTextView = roleView.findViewById(R.id.roleNumber);
        roleNumberTextView.setText("Role " + roleCount);

        rolesContainer.addView(roleView, rolesContainer.getChildCount() - 2);
    }

    private void saveVolunteerRoles() {

        finish();
    }
}