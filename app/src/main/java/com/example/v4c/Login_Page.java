package com.example.v4c;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login_Page extends AppCompatActivity {

    private TextView signUp;
    private FirebaseAuth mAuth;
    private Button Login;
    EditText editTextEmail, editTextPassword;
    ProgressBar loading;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        signUp = findViewById(R.id.textView4);
        Login = findViewById(R.id.button2);
        loading = findViewById(R.id.loading);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextText5);
        editTextPassword = findViewById(R.id.editTextText6);

        Login.setOnClickListener(view -> {
            loading.setVisibility(View.VISIBLE);
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (!validateInputs(email, password)) {
                loading.setVisibility(View.GONE);
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        loading.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            if (user != null) {
                                Intent intent = new Intent(Login_Page.this, loadingActivity.class);

                                intent.putExtra("fromLogin", true);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            handleAuthError(task.getException());
                        }
                    });
        });

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login_Page.this, SignUp_Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
    }

    private boolean validateInputs(String email, String password) {
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
        return true;
    }

    private void handleAuthError(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidUserException) {
            editTextEmail.setError("Email not registered");
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            editTextPassword.setError("Invalid password");
        } else {
            Toast.makeText(this, "Login failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
