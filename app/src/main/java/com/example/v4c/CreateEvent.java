
package com.example.v4c;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.HashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateEvent extends AppCompatActivity {

    private EditText eventTitle;
    private EditText eventDesc;
    private EditText eventDate;
    private EditText eventTime;
    private EditText eventLoc;

    private Spinner eventCat;
    private SwitchCompat eventSwitch;
    private Button selectImageButton;
    private FloatingActionButton nextButton;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private String cloudinaryUrl;

    private static final String TAG = "CreateEvent";
    private boolean isCloudinaryConfigured = false;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        initializeViews();

        setupCategorySpinner();

        setupDatePicker();

        setupTimePicker();

        setupImageSelection();

        setupNextButton();

        // Initialize Cloudinary
        initCloudinary();

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initCloudinary() {
        try {
            if (!isCloudinaryConfigured) {
                // Configure Cloudinary
                Map<String, String> config = new HashMap<>();
                config.put("cloud_name", "dv7dxn2ku"); // Your cloud name
                config.put("api_key", "185228787423927"); // Your API key
                config.put("api_secret", "PESM4x7CPh0DLnibl6oZBHwBplk");

                MediaManager.init(this, config);
                isCloudinaryConfigured = true;
                Log.d(TAG, "Cloudinary initialized successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Cloudinary: " + e.getMessage());
            Toast.makeText(this, "Failed to initialize image upload service", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        eventTitle = findViewById(R.id.eventTitle);
        eventDesc = findViewById(R.id.eventDesc);
        eventDate= findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);
        eventLoc = findViewById(R.id.eventLoc);
        eventCat = findViewById(R.id.eventCat);
        eventSwitch = findViewById(R.id.eventSwitch);
        selectImageButton = findViewById(R.id.selectImageButton);
        nextButton = findViewById(R.id.nextButton);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_categories, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        eventCat.setAdapter(adapter);
    }

    private void setupDatePicker() {
        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEvent.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String formattedDate = String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year);
                                eventDate.setText(formattedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void setupTimePicker() {
        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEvent.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                                eventTime.setText(formattedTime);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });
    }

    private void setupImageSelection() {
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        View imageUploadCard = findViewById(R.id.imageUpload);
        if (imageUploadCard != null) {
            imageUploadCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFileChooser();
                }
            });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Image from"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Toast.makeText(this, "Image selected, uploading...", Toast.LENGTH_SHORT).show();

            TextView dragDropText = findViewById(R.id.dragDropText);
            Button selectImageButton = findViewById(R.id.selectImageButton);

            String fileName = getFileName(selectedImageUri);

            if (dragDropText != null) {
                dragDropText.setText("Selected: " + fileName);
            }

            if (selectImageButton != null) {
                selectImageButton.setText("Uploading...");
                selectImageButton.setEnabled(false);
            }

            // Upload the image to Cloudinary
            uploadImageToCloudinary(selectedImageUri);
        }
    }

    private void uploadImageToCloudinary(Uri imageUri) {
        if (!isCloudinaryConfigured) {
            Toast.makeText(this, "Image upload service not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String requestId = MediaManager.get().upload(imageUri)
                    .option("folder", "v4c_events")
                    .option("resource_type", "auto")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.d(TAG, "Upload started");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            double progress = (double) bytes / totalBytes;
                            Log.d(TAG, "Upload progress: " + (int) (progress * 100) + "%");
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            cloudinaryUrl = (String) resultData.get("url");
                            Log.d(TAG, "Upload successful: " + cloudinaryUrl);

                            runOnUiThread(() -> {
                                Toast.makeText(CreateEvent.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                Button selectImageButton = findViewById(R.id.selectImageButton);
                                if (selectImageButton != null) {
                                    selectImageButton.setText("Change Image");
                                    selectImageButton.setEnabled(true);
                                }
                            });
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.e(TAG, "Upload error: " + error.getDescription());

                            runOnUiThread(() -> {
                                Toast.makeText(CreateEvent.this, "Failed to upload image: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                                Button selectImageButton = findViewById(R.id.selectImageButton);
                                if (selectImageButton != null) {
                                    selectImageButton.setText("Select Image");
                                    selectImageButton.setEnabled(true);
                                }
                            });
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            Log.e(TAG, "Upload rescheduled: " + error.getDescription());
                        }
                    })
                    .dispatch();

        } catch (Exception e) {
            Log.e(TAG, "Error uploading to Cloudinary: " + e.getMessage());
            Toast.makeText(this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Button selectImageButton = findViewById(R.id.selectImageButton);
            if (selectImageButton != null) {
                selectImageButton.setText("Select Image");
                selectImageButton.setEnabled(true);
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex >= 0) {
                        result = cursor.getString(columnIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    private void setupNextButton() {
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate form fields
                if (validateForm()) {
                    // Save event details to Firebase
                    saveEventToFirebase();
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        if (eventTitle.getText().toString().trim().isEmpty()) {
            eventTitle.setError("Event title is required");
            valid = false;
        }

        if (eventDate.getText().toString().trim().isEmpty()) {
            eventDate.setError("Event date is required");
            valid = false;
        }

        if (eventTime.getText().toString().trim().isEmpty()) {
            eventTime.setError("Event time is required");
            valid = false;
        }

        if (eventLoc.getText().toString().trim().isEmpty()) {
            eventLoc.setError("Event location is required");
            valid = false;
        }

        return valid;
    }

    private void saveEventToFirebase() {
        // Show loading indicator
        Toast.makeText(CreateEvent.this, "Creating event...", Toast.LENGTH_SHORT).show();
        nextButton.setEnabled(false);

        // Get event details
        String title = eventTitle.getText().toString().trim();
        String description = eventDesc.getText().toString().trim();
        String date = eventDate.getText().toString().trim();
        String time = eventTime.getText().toString().trim();
        String location = eventLoc.getText().toString().trim();
        String category = eventCat.getSelectedItem().toString();
        boolean isOfflineEvent = eventSwitch.isChecked();

        // Get current user ID
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(CreateEvent.this, "You must be logged in to create an event", Toast.LENGTH_SHORT).show();
            nextButton.setEnabled(true);
            return;
        }
        String uid = auth.getCurrentUser().getUid();

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create event data map according to the specified structure
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", title);
        eventData.put("description", description);
        eventData.put("date", date);
        eventData.put("time", time);
        eventData.put("loc", location);
        eventData.put("category", category);
        eventData.put("offline", isOfflineEvent);
        eventData.put("imageUrl", cloudinaryUrl != null ? cloudinaryUrl : (selectedImageUri != null ? selectedImageUri.toString() : ""));
        eventData.put("organiserId", uid);
        eventData.put("createdAt", FieldValue.serverTimestamp());
        eventData.put("roles", new ArrayList<>()); // Initialize with empty roles array

        // Save to Firestore
        db.collection("events").add(eventData).addOnSuccessListener(docRef -> {
            String eventId = docRef.getId();

            // Update the NGO document to include this event ID
            db.collection("NGO").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    // Document exists, update it
                    db.collection("NGO").document(uid)
                            .update("events", FieldValue.arrayUnion(eventId))
                            .addOnSuccessListener(aVoid -> {
                                // Navigate to Volunteer_Roles page with event ID
                                Intent intent = new Intent(CreateEvent.this, Volunteer_Roles.class);
                                intent.putExtra("eventId", eventId);
                                startActivity(intent);
                                nextButton.setEnabled(true);
                            })
                            .addOnFailureListener(e -> {
                                // Continue anyway, we can update the NGO document later
                                Intent intent = new Intent(CreateEvent.this, Volunteer_Roles.class);
                                intent.putExtra("eventId", eventId);
                                startActivity(intent);
                                nextButton.setEnabled(true);
                                Toast.makeText(CreateEvent.this, "Note: Failed to update profile", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // Document doesn't exist, create it
                    Map<String, Object> ngoData = new HashMap<>();
                    ArrayList<String> events = new ArrayList<>();
                    events.add(eventId);
                    ngoData.put("events", events);

                    db.collection("NGO").document(uid).set(ngoData)
                            .addOnSuccessListener(aVoid -> {
                                // Navigate to Volunteer_Roles page with event ID
                                Intent intent = new Intent(CreateEvent.this, Volunteer_Roles.class);
                                intent.putExtra("eventId", eventId);
                                startActivity(intent);
                                nextButton.setEnabled(true);
                            })
                            .addOnFailureListener(e -> {
                                // Continue anyway, we can update the NGO document later
                                Intent intent = new Intent(CreateEvent.this, Volunteer_Roles.class);
                                intent.putExtra("eventId", eventId);
                                startActivity(intent);
                                nextButton.setEnabled(true);
                                Toast.makeText(CreateEvent.this, "Note: Failed to update profile", Toast.LENGTH_SHORT).show();
                            });
                }
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(CreateEvent.this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            nextButton.setEnabled(true);
        });
    }
}

