package com.example.v4c;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.v4c.ngo.NgoDashboard;
import com.example.v4c.volunteer.HomePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ImageView logo = findViewById(R.id.imageView7);
        TextView text = findViewById(R.id.textView);

        logo.post(() -> {
            float targetY = text.getY() - logo.getHeight() - text.getHeight() - 740f;

            // Jump + Bounce
            ObjectAnimator jump = ObjectAnimator.ofFloat(logo, "translationY", -300f, targetY);
            jump.setDuration(1000);
            jump.setInterpolator(new BounceInterpolator());

            // Fade-in of text
            text.setAlpha(0f);
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(text, "alpha", 0f, 1f);
            fadeIn.setDuration(800);
            fadeIn.setStartDelay(1200);

            jump.start();
            fadeIn.start();

            logo.postDelayed(this::proceedToNext, 2800);
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }



    private void proceedToNext() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(this, "VOLUNTEER SIDE", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, HomePage.class));
                        } else {
                            Toast.makeText(this, "NGO SIDE", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, NgoDashboard.class));
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MainActivity", "Firestore error: " + e.getMessage());
                        startActivity(new Intent(MainActivity.this, Welcome_Page.class));
                        finish();
                    });
        } else {
            startActivity(new Intent(MainActivity.this, Welcome_Page.class));
            finish();
        }
    }

}