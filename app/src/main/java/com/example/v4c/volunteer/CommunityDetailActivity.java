package com.example.v4c.volunteer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.v4c.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.gson.Gson;

public class CommunityDetailActivity extends AppCompatActivity {

    TextView name, place, about, whyJoin;
    TextView tvVolunteers, tvExperience, tvRating;
    ImageView mainImage;
    Button volunteerBtn;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        name = findViewById(R.id.detail_name);
        place = findViewById(R.id.detail_place);
        about = findViewById(R.id.detail_about);
        whyJoin = findViewById(R.id.detail_why_join);
        mainImage = findViewById(R.id.detail_main_image);
        volunteerBtn = findViewById(R.id.btn_volunteer);

        tvVolunteers = findViewById(R.id.tv_volunteers);
        tvExperience = findViewById(R.id.tv_experience);
        tvRating = findViewById(R.id.tv_rating);

        String json = getIntent().getStringExtra("ngo");
        CommunityModel communityModel = new Gson().fromJson(json, CommunityModel.class);

        name.setText(communityModel.name);
        place.setText(communityModel.place);
        about.setText(communityModel.about);
        whyJoin.setText(communityModel.why_join_us);
        tvVolunteers.setText(String.valueOf(communityModel.volunteers));
        tvExperience.setText(communityModel.experience + " Years");
        tvRating.setText(String.valueOf(communityModel.rating));
        Glide.with(this).load(communityModel.main_image).into(mainImage);

        volunteerBtn.setOnClickListener(v ->
                {
                    Toast.makeText(this, "Thank You for showing interest!", Toast.LENGTH_SHORT).show();

                    String userAddress = communityModel.email;
                    String userSubject = "Volunteer Application for " + communityModel.name;
                    String userBody = "Dear " + communityModel.name + " Team,\n\n"

                            + "I am writing to express my interest in volunteering for your organization. "
                            + "I am enthusiastic about the work you do and would be honored to contribute in any way I can.\n\n"
                            + "Please let me know how I can get involved or if there are any steps I should take to begin the process.\n\n"
                            + "Looking forward to your response.\n\n"
                            + "Best regards,\n";
//                            +  userData.name;
                    sendEmail(userAddress, userSubject, userBody);

                });

    }

    public void sendEmail(String userAddress, String userSubject,
                          String userBody) {
        String[] userAddresses = {userAddress};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, userAddresses);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, userSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, userBody);
        startActivity(Intent.createChooser(emailIntent, "Select an Email App"));
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
