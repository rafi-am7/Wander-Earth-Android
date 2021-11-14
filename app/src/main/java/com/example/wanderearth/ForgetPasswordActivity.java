package com.example.wanderearth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    private TextView signUpButton, logInButton, errorMassage;
    private Button submitButton;
    private EditText emailEditText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        signUpButton = findViewById(R.id.signUpForgetPass);
        logInButton = findViewById(R.id.signinForgetPass);
        errorMassage = findViewById(R.id.error_message_text_forget_pass);
        emailEditText = findViewById(R.id.userEmailForgetPass);
        submitButton = findViewById(R.id.submitButtonForgetPass);
        progressBar = findViewById(R.id.marker_progress_forgetpass);
        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPasswordActivity.this, SignUpActivity.class));
                finish();
            }
        });
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPasswordActivity.this, LogInActivity.class));
                finish();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPass();
            }
        });


    }

    public void resetPass() {
        String email;
        email = emailEditText.getText().toString();
        if (email.isEmpty()) {
            emailEditText.setError("Enter email!");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter valid email!");
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    errorMassage.setText("Password reset link sent to your email.");
                    errorMassage.setTextColor(ContextCompat.getColor(ForgetPasswordActivity.this, R.color.success));
                    errorMassage.setVisibility(View.VISIBLE);

                } else {
                    errorMassage.setText("Account not exit. Please sign up.");
                    errorMassage.setTextColor(ContextCompat.getColor(ForgetPasswordActivity.this, R.color.error));
                    errorMassage.setVisibility(View.VISIBLE);
                }
            }
        });
        progressBar.setVisibility(View.GONE);


    }

}