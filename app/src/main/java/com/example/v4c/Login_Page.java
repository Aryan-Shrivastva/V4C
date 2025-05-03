package com.example.v4c;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

public class Login_Page extends AppCompatActivity {

    private TextView signUp;
    private FirebaseAuth mAuth;
    private Button Login;
    TextInputEditText editTextEmail,editTextPassword;
    ProgressBar loading;

    @SuppressLint("WrongViewCast")
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

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login_Page.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login_Page.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loading.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(Login_Page.this, HomePage.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login_Page.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });




            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Page.this, SignUp_Page.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }
}