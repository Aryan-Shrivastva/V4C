package com.example.v4c;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
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

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
//        submitButton = findViewById(R.id.submitButton);
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
            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();


            TextView dragDropText = findViewById(R.id.dragDropText);
            TextView recommendedSizeText = findViewById(R.id.recommendedSizeText);
            Button selectImageButton = findViewById(R.id.selectImageButton);

            String fileName = getFileName(selectedImageUri);

            if (dragDropText != null) {
                dragDropText.setText("Selected: " + fileName);
            }

            if (selectImageButton != null) {
                selectImageButton.setText("Change Image");
            }

//             ImageView imagePreview = findViewById(R.id.imagePreview);
//             try {
//                 Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
//                 imagePreview.setVisibility(View.VISIBLE);
//                 imagePreview.setImageBitmap(bitmap);
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }
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
                    // Save event details
                    saveEventDetails();
                    Intent intent = new Intent(CreateEvent.this, Volunteer_Roles.class);
                    startActivity(intent);
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





    private void saveEventDetails() {
        String title = eventTitle.getText().toString().trim();
        String description = eventDesc.getText().toString().trim();
        String date = eventDate.getText().toString().trim();
        String time = eventTime.getText().toString().trim();
        String location = eventLoc.getText().toString().trim();
        String category = eventCat.getSelectedItem().toString();
        boolean isOfflineEvent = eventSwitch.isChecked();


        Event event = new Event(title, description, date, time, location, category, isOfflineEvent, selectedImageUri);
        boolean success = saveEventToDatabase(event);

        if (success) {
            Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to create event. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean saveEventToDatabase(Event event) {
        // This is a placeholder method. You would implement your database saving logic here
        // For example, using Firebase, Room, or SQLite

        // For now, just return true to simulate successful saving
        return true;
    }


    private class Event {
        private String title;
        private String description;
        private String date;
        private String time;
        private String location;
        private String category;
        private boolean isOfflineEvent;
        private Uri imageUri;

        public Event(String title, String description, String date, String time, String location,
                     String category, boolean isOfflineEvent, Uri imageUri) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.time = time;
            this.location = location;
            this.category = category;
            this.isOfflineEvent = isOfflineEvent;
            this.imageUri = imageUri;
        }

        // Getters and setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public boolean isOfflineEvent() {
            return isOfflineEvent;
        }

        public void setOfflineEvent(boolean offlineEvent) {
            isOfflineEvent = offlineEvent;
        }

        public Uri getImageUri() {
            return imageUri;
        }

        public void setImageUri(Uri imageUri) {
            this.imageUri = imageUri;
        }
    }




}