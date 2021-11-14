package com.example.wanderearth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {
    private String[] genderOptions, idTypeOptions;
    private Spinner idTypeSpinner, genderSpinner;
    private Button signUpButton;
    private TextView logInButton, errorMassageText;
    private EditText fullNameEditText, passwordEditText, emailEditText;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        genderOptions = getResources().getStringArray(R.array.gender_options);
        idTypeOptions = getResources().getStringArray(R.array.id_type_options);
        idTypeSpinner = findViewById(R.id.id_type_spinner);
        genderSpinner = findViewById(R.id.gender_spinner);
        signUpButton = findViewById(R.id.signUpButton);
        fullNameEditText = findViewById(R.id.userFullName);
        passwordEditText = findViewById(R.id.userPasswordsignup);
        emailEditText = findViewById(R.id.userMobileNumber);
        errorMassageText = findViewById(R.id.error_message_text);
        logInButton = findViewById(R.id.signInButton);
        progressBar = findViewById(R.id.marker_progress_signUp);
        mAuth = FirebaseAuth.getInstance();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_signup_list_item, R.id.spinner_optinos_textview, genderOptions);
        genderSpinner.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this, R.layout.spinner_signup_list_item, R.id.spinner_optinos_textview, idTypeOptions);
        idTypeSpinner.setAdapter(adapter);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
                //genderSpinner.getSelectedItem().toString();
                // idTypeSpinner.getSelectedItem().toString();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                finish();
            }
        });

    }

    public void registerUser() {
        String email, fullname, password, gender, membertype;
        email = emailEditText.getText().toString().trim();//trim spaces may be
        fullname = fullNameEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        gender = genderSpinner.getSelectedItem().toString().trim();
        membertype = idTypeSpinner.getSelectedItem().toString().trim();

        if (fullname.isEmpty()) {
            fullNameEditText.setError("Please enter full name!");
            fullNameEditText.requestFocus(); // showing automatically
            return;
        }
        if (email.isEmpty()) {
            emailEditText.setError("Please enter email!");
            emailEditText.requestFocus(); // showing automatically
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter valid email!");
            emailEditText.requestFocus(); // showing automatically
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Minimum password length is 6 characters!");
            passwordEditText.requestFocus();
            return;
        }
        if (gender.equals("Select Gender") || membertype.equals("Select Member Type")) {
            Toast.makeText(this, "Please select gender and membertype.", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        //   Toast.makeText(this,gender+" "+membertype,Toast.LENGTH_SHORT).show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    UserDataType userObj = new UserDataType(fullname, email, gender, membertype, password);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification();
                                errorMassageText.setText("Verification link has sent to your email.");
                                errorMassageText.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.success));
                                errorMassageText.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                               /* startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                                finish();*/
                            } else {
                                progressBar.setVisibility(View.GONE);
                                errorMassageText.setText("Registration Failed!");
                                errorMassageText.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.error));
                                progressBar.setVisibility(View.GONE);
                                errorMassageText.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } else {
                    progressBar.setVisibility(View.GONE);
                    errorMassageText.setText("You are already registered!");
                    errorMassageText.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.error));
                    errorMassageText.setVisibility(View.VISIBLE);
                }
            }
        });


    }
}