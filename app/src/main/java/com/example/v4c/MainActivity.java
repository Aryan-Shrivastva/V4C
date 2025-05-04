package com.example.v4c;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.v4c.volunteer.HomePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent;

        try {
            if (currentUser != null) {
                intent = new Intent(MainActivity.this, HomePage.class);
            } else {
                intent = new Intent(MainActivity.this, Welcome_Page.class);
            }
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("MainActivity", "Navigation error: " + e.getMessage());
            // Fallback to Welcome_Page if there's an error
            startActivity(new Intent(MainActivity.this, Welcome_Page.class));
            finish();
        }
    }
}
