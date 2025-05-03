package com.example.v4c;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp_Page extends AppCompatActivity {
    private TextView login;
    private Button signup;
    TextInputEditText editTextName, editTextEmail, editTextPassword;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);

        editTextEmail = findViewById(R.id.email);
        editTextName = findViewById(R.id.name);
        editTextPassword = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.textView4);
        progressBar = findViewById(R.id.loading);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        login.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp_Page.this, Login_Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        signup.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(SignUp_Page.this, "Enter Name", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(SignUp_Page.this, "Enter Email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(SignUp_Page.this, "Enter Password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("name", name);
                                userData.put("email", email);
                                userData.put("type", "user");
                                userData.put("uid", uid);
                                userData.put("profile", "");

                                db.collection("users")
                                        .document(uid)
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Firestore", "User data saved");
                                            // Go to homepage only after Firestore success
                                            Intent intent = new Intent(SignUp_Page.this, HomePage.class);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firestore", "Error saving user data", e);
                                            Toast.makeText(SignUp_Page.this, "Signup failed. Try again.", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(SignUp_Page.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
