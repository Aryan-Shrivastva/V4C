package com.example.v4c;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        signUp = findViewById(R.id.textView4);
        Login = findViewById(R.id.button2);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextText5);
        editTextPassword = findViewById(R.id.editTextText6);
        loading = findViewById(R.id.loading);

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
                            Intent intent = new Intent(Login_Page.this, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            handleAuthError(task.getException());
                        }
                    });
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Page.this, SignUp_Page.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
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
            Toast.makeText(this, "Login failed: " + exception.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}