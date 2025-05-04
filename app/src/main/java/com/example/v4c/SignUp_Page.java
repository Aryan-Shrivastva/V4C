package com.example.v4c;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.v4c.volunteer.HomePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import android.util.Patterns;

public class SignUp_Page extends AppCompatActivity {
    private TextView login;
    private Button signup;
    EditText editTextName, editTextEmail, editTextPassword;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        login.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp_Page.this, Login_Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        signup.setOnClickListener(view -> {
            if (!validateSignUpInputs()) {
                return;
            }

            Intent intent = new Intent(SignUp_Page.this, loadingActivity.class);
            intent.putExtra("fromSignup", true);
            startActivity(intent);
            finish();

            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            saveUserDataToFirestore(name, email);
                        } else {
                            handleSignUpError(task.getException());
                        }
                    });
        });
    }

    private boolean validateSignUpInputs() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    private void saveUserDataToFirestore(String name, String email) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("email", email);
            userData.put("type", "user");
            userData.put("uid", uid);
            userData.put("profile", "");

            db.collection("users").document(uid).set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Intent intent = new Intent(SignUp_Page.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUp_Page.this, "Failed to save user data",
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void handleSignUpError(Exception exception) {
        String errorMessage = "Registration failed";
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            errorMessage = "Password is too weak";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessage = "Invalid email format";
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            errorMessage = "Email already registered";
        }
        Toast.makeText(SignUp_Page.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
