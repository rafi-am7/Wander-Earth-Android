package com.example.wanderearth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {
    //SharedPreferences sharedPreferences;
    //public static final String fileName = "login", userName = "username", pass = "password";
    private EditText emailEditText, passwordEditText;
    private Button logInButton;
    private TextView signUpButton, forgetPassButton, errorMassageText;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private UserDataType userDataType;
    private String flag;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static final String filename = "login", usernameSp = "user", memTypSp = "", passwordSp = "pass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        emailEditText = findViewById(R.id.userMobileNumber);
        passwordEditText = findViewById(R.id.userPasswordsignin);
        logInButton = findViewById(R.id.logInButton);
        signUpButton = findViewById(R.id.signUpButton);
        forgetPassButton = findViewById(R.id.forgetPassButton);
        errorMassageText = findViewById(R.id.error_message_text);
        progressBar = findViewById(R.id.marker_progress_signIn);
        mAuth = FirebaseAuth.getInstance();
        forgetPassButton = findViewById(R.id.forgetPassButton);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();


        if (sharedPreferences.contains("username")) {
            String memTyp = sharedPreferences.getString("membertype", "not found");
            // Toast.makeText(LogInActivity.this, sharedPreferences.getString("username", "not found"), Toast.LENGTH_SHORT).show();
            if (memTyp.equals("Hotel")) {
                startActivity(new Intent(LogInActivity.this, HotelActivity.class));
                finish();
            } else if (memTyp.equals("Consumer")) {
                startActivity(new Intent(LogInActivity.this, ConsumerActivity.class));
                finish();
            }

        }

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInUser();
            }
        });
        forgetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, ForgetPasswordActivity.class));
                finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
                finish();
            }
        });

    }

    public void logInUser() {
        String email, password, memType;
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
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
        if (password.isEmpty()) {
            passwordEditText.setError("Enter password!");
            passwordEditText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        //mAuth.confirmPasswordReset("hellw",password);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    if (!mAuth.getCurrentUser().isEmailVerified()) {
                        mAuth.getCurrentUser().sendEmailVerification();
                        errorMassageText.setText("Account not verified. Verification link sent to your email.");
                        errorMassageText.setTextColor(ContextCompat.getColor(LogInActivity.this, R.color.error));
                        errorMassageText.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        return;

                    }


                    //progressBar.setVisibility(View.GONE);
                    errorMassageText.setText("Log in successful!");
                    errorMassageText.setTextColor(ContextCompat.getColor(LogInActivity.this, R.color.success));
                    errorMassageText.setVisibility(View.VISIBLE);

                    //Toast.makeText(LogInActivity.this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference.child("password").setValue(password);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            UserDataType userDataType = snapshot.getValue(UserDataType.class);
                            flag = userDataType.getMemberType().trim();
                            if (flag != null) {
                                editor.putString("username", email);
                                // editor.putString("password",password);
                                editor.putString("membertype", flag);
                                editor.commit();
                                if (flag.equals("Hotel")) {
                                    startActivity(new Intent(LogInActivity.this, HotelActivity.class));
                                    finish();
                                } else if (flag.equals("Consumer")) {
                                    startActivity(new Intent(LogInActivity.this, ConsumerActivity.class));
                                    finish();
                                }
                            } else
                                Toast.makeText(LogInActivity.this, "error occured!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                } else {
                    //progressBar.setVisibility(View.GONE);
                    errorMassageText.setText("Wrong Email or Password!");
                    errorMassageText.setTextColor(ContextCompat.getColor(LogInActivity.this, R.color.error));
                    errorMassageText.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);


            }
        });

    }


    public void isHotel() {


        //return flag;

    }

}