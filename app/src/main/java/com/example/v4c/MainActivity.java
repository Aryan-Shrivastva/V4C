package com.example.v4c;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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

        if (currentUser != null) {
            // User is signed in, go to HomePage
            intent = new Intent(MainActivity.this, HomePage.class);
        } else {
            // No user is signed in, go to Welcome_Page
            intent = new Intent(MainActivity.this, Welcome_Page.class);
        }

        startActivity(intent);
        finish(); // Finish MainActivity so it's not in the back stack
    }
}
