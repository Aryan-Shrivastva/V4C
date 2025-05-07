package com.example.v4c.volunteer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.example.v4c.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.example.v4c.volunteer.EventModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.gson.Gson;

public class EventDetailActivity extends AppCompatActivity {

    private ImageView eventImage, orgImage;
    private TextView eventTitle, eventDateTime, eventDescription;
//    private LinearLayout categoryContainer;
    private Button volunteerButton;
    private TextView orgName, orgLoc, orgDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail); // your fixed XML file

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//         Get views
        eventImage = findViewById(R.id.eventImage);
        eventTitle = findViewById(R.id.eventTitle);
        eventDateTime = findViewById(R.id.eventDateTime);
        eventDescription = findViewById(R.id.eventDescription);
        orgDescription = findViewById(R.id.orgDescription);
//        categoryContainer = findViewById(R.id.categoryContainer);
        volunteerButton = findViewById(R.id.volunteerButton);

        orgImage = findViewById(R.id.orgImage);
        orgName = findViewById(R.id.orgName);
        orgLoc = findViewById(R.id.orgLoc);
        orgDescription = findViewById(R.id.orgDescription);

        // Get Event object from Intent
        String eventJson = getIntent().getStringExtra("event");
        EventModel event = new Gson().fromJson(eventJson, EventModel.class);

        if (event != null) {
            // Load event image
            Glide.with(this)
                    .load(Uri.parse(event.getImageUrl()))
                    .into(eventImage);

            // Set data
            eventTitle.setText(event.getTitle());
            eventDateTime.setText(formatDate(event.getDate()) + " @ " + formatTime(event.getTime()));
            eventDescription.setText(event.getDescription());
            FirebaseFirestore.getInstance()
                    .collection("NGO")
                    .document(event.getOrganiserId()) // Assuming organiserId matches document ID
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String imageUrl = documentSnapshot.getString("sub_image");
                            String location = documentSnapshot.getString("place");
//                            String domain = documentSnapshot.getString("domain");
                            String description = documentSnapshot.getString("about");

                            orgName.setText(name);
                            orgLoc.setText(location);
                            orgDescription.setText(description);
                            Glide.with(this).load(imageUrl).into(orgImage);
                        }
                    });


//             Volunteer button;
            volunteerButton.setOnClickListener(v -> {
                // You can show a confirmation, navigate, or send email
                Intent intent = new Intent(Intent.ACTION_SENDTO);
//                intent.setData(Uri.parse("mailto:" + event.getEmail())); // example field
                intent.putExtra(Intent.EXTRA_SUBJECT, "Interested in Volunteering: " + event.getTitle());
                startActivity(intent);
            });
        }
    }



    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private String formatDate(String dateStr) {
        try {
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("dd-MM-yyyy");
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("MMMM d, yyyy");
            java.util.Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr; // fallback
        }
    }

    private String formatTime(String timeStr) {
        try {
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("HH:mm");
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("h a");
            java.util.Date date = inputFormat.parse(timeStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return timeStr; // fallback
        }
    }
}
