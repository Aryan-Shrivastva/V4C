package com.example.v4c;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent;

        try {
            if (currentUser != null) {
                db.collection("users").document(currentUser.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String type = documentSnapshot.getString("type");
                                if ("user".equals(type)) {
                                    Toast.makeText(this, "VOLUNTEER SIDE", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, HomePage.class));
                                }
                                else {
                                    Toast.makeText(this, "NGO SIDE", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, NgoDashboard.class));
                                }
                                finish();
                            } else {
                                startActivity( new Intent(MainActivity.this, Welcome_Page.class));
                            }
                        });
            }
            finish();
        } catch (Exception e) {
            Log.e("MainActivity", "Navigation error: " + e.getMessage());
            // Fallback to Welcome_Page if there's an error
            startActivity(new Intent(MainActivity.this, Welcome_Page.class));
            finish();
        }
    }
}
