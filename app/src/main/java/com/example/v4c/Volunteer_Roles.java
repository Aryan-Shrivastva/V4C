
package com.example.v4c;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.v4c.ngo.NgoDashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Volunteer_Roles extends AppCompatActivity {

    private LinearLayout rolesContainer;
    private int roleCount = 0;

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_roles);

        rolesContainer = findViewById(R.id.rolesContainer);

        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Button addAnotherRoleButton = findViewById(R.id.addAnotherRole);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button createButton = findViewById(R.id.createButton);

        addAnotherRoleButton.setOnClickListener(v -> {
            addNewRoleSection();
        });

        cancelButton.setOnClickListener(v -> {
            // Delete the event from Firebase when cancel is clicked
            if (eventId != null && !eventId.isEmpty()) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();

                if (auth.getCurrentUser() != null) {
                    String uid = auth.getCurrentUser().getUid();

                    // Disable the cancel button to prevent multiple clicks
                    cancelButton.setEnabled(false);

                    // Show a toast message
                    Toast.makeText(Volunteer_Roles.this, "Cancelling event...", Toast.LENGTH_SHORT).show();

                    // Delete the event document
                    db.collection("events").document(eventId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                // Remove the event ID from the NGO document
                                db.collection("NGO").document(uid)
                                        .update("events", FieldValue.arrayRemove(eventId))
                                        .addOnCompleteListener(task -> {
                                            // Finish the activity regardless of whether the update succeeded
                                            Toast.makeText(Volunteer_Roles.this, "Event cancelled", Toast.LENGTH_SHORT).show();
                                            intent.setClass(Volunteer_Roles.this, NgoDashboard.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                // Re-enable the cancel button if deletion fails
                                cancelButton.setEnabled(true);
                                Toast.makeText(Volunteer_Roles.this, "Failed to cancel event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // User is not logged in
                    Toast.makeText(Volunteer_Roles.this, "User not logged in", Toast.LENGTH_SHORT).show();
                    finish();
                }

            } else {
                // No event ID
                finish();
            }
        });

        createButton.setOnClickListener(v -> saveVolunteerRoles());

        addNewRoleSection();
    }

    private void addNewRoleSection() {
        roleCount++;
        LayoutInflater inflater = LayoutInflater.from(this);
        View roleView = inflater.inflate(R.layout.role_item, null);

        TextView roleNumberTextView = roleView.findViewById(R.id.roleNumber);
        roleNumberTextView.setText("Role " + roleCount);

        rolesContainer.addView(roleView, rolesContainer.getChildCount() - 1);
    }

    private void saveVolunteerRoles() {
        try {
            if (eventId == null || eventId.isEmpty()) {
                Toast.makeText(this, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            ArrayList<Map<String, Object>> rolesList = new ArrayList<>();

            int roleViewCount = rolesContainer.getChildCount();
            if (roleViewCount <= 1) {
                Toast.makeText(this, "Please add at least one role", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < roleViewCount - 1; i++) {
                View roleView = rolesContainer.getChildAt(i);
                if (roleView == null) continue;

                EditText titleEdit = roleView.findViewById(R.id.title);
                EditText descriptionEdit = roleView.findViewById(R.id.RoleDesc);
                EditText skillsEdit = roleView.findViewById(R.id.RequiredSkills);
                EditText durationEdit = roleView.findViewById(R.id.roleDuration);
                EditText positionsEdit = roleView.findViewById(R.id.NoOfPos);

                if (titleEdit == null || descriptionEdit == null || skillsEdit == null ||
                        durationEdit == null || positionsEdit == null) {
                    continue;
                }

                String roleTitle = titleEdit.getText() != null ? titleEdit.getText().toString().trim() : "";
                String roleDescription = descriptionEdit.getText() != null ? descriptionEdit.getText().toString().trim() : "";
                String roleSkills = skillsEdit.getText() != null ? skillsEdit.getText().toString().trim() : "";
                String roleDuration = durationEdit.getText() != null ? durationEdit.getText().toString().trim() : "";
                String positionsStr = positionsEdit.getText() != null ? positionsEdit.getText().toString().trim() : "";

                if (roleTitle.isEmpty()) {
                    Toast.makeText(this, "Role title cannot be empty", Toast.LENGTH_SHORT).show();
                    titleEdit.requestFocus();
                    return;
                }

                if (positionsStr.isEmpty()) {
                    Toast.makeText(this, "Number of positions cannot be empty", Toast.LENGTH_SHORT).show();
                    positionsEdit.requestFocus();
                    return;
                }

                int positions;
                try {
                    positions = Integer.parseInt(positionsStr);
                    if (positions <= 0) {
                        Toast.makeText(this, "Number of positions must be greater than 0", Toast.LENGTH_SHORT).show();
                        positionsEdit.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid number of positions", Toast.LENGTH_SHORT).show();
                    positionsEdit.requestFocus();
                    return;
                }

                Map<String, Object> role = new HashMap<>();
                role.put("role_title", roleTitle);
                role.put("role_description", roleDescription);
                role.put("skills", roleSkills);
                role.put("role_duration", roleDuration);
                role.put("positions", positions);
                rolesList.add(role);
            }

            if (rolesList.isEmpty()) {
                Toast.makeText(this, "Please add at least one role", Toast.LENGTH_SHORT).show();
                return;
            }

            Button createButton = findViewById(R.id.createButton);
            createButton.setEnabled(false);

            Toast.makeText(this, "Adding roles to event...", Toast.LENGTH_SHORT).show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("events").document(eventId)
                    .update("roles", rolesList)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Roles added successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Volunteer_Roles.this, NgoDashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add roles: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        createButton.setEnabled(true);
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();

            Button createButton = findViewById(R.id.createButton);
            if (createButton != null) {
                createButton.setEnabled(true);
            }
        }
    }
}


