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
import com.google.gson.Gson;
import com.example.v4c.volunteer.EventModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.gson.Gson;

public class EventDetailActivity extends AppCompatActivity {

    private ImageView eventImage;
    private TextView eventTitle, eventDateTime, eventDescription, orgDescription;
//    private LinearLayout categoryContainer;
    private Button volunteerButton;

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
            eventDateTime.setText(event.getDate() + ", " + event.getTime());
            eventDescription.setText(event.getDescription());
            orgDescription.setText(event.getDescription());


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
}
