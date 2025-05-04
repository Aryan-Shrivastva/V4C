package com.example.v4c;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.v4c.ngo.NgoDashboard;
import com.example.v4c.volunteer.HomePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class loadingActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    LottieAnimationView animationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        animationView = findViewById(R.id.bottomBarAnimation);
        animationView.playAnimation();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

//        checkUserTypeAndRedirect();
//        delay of 2 seconds
        new Handler().postDelayed(this::checkUserTypeAndRedirect, 2000);
    }

    private void checkUserTypeAndRedirect() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String type = documentSnapshot.getString("type");
                            if ("user".equals(type)) {
                                Toast.makeText(this, "VOLUNTEER SIDE", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(loadingActivity.this, HomePage.class));
                            }
                            else {
                                Toast.makeText(this, "NGO SIDE", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(loadingActivity.this, NgoDashboard.class));
                            }
                            finish();
                        } else {
                            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to fetch user type", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }
}
