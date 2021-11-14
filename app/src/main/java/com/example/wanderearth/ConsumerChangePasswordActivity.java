package com.example.wanderearth;

import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConsumerChangePasswordActivity extends AppCompatActivity {

    private EditText oldPassET, newPassET, newPassReenterET;
    private Button updateButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_change_password);
        oldPassET = findViewById(R.id.oldPasswordCusET);
        newPassET = findViewById(R.id.newPasswordCusET);
        newPassReenterET = findViewById(R.id.newRentPasswordCusET);
        updateButton = findViewById(R.id.updateUpPassCus);
        progressBar = findViewById(R.id.marker_progress_uppass_cus);
        errorMessage = findViewById(R.id.message_view_cus_uppass);
        mAuth = FirebaseAuth.getInstance();


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessage.setVisibility(View.GONE);
                String oldp, newp, rnewp;
                oldp = oldPassET.getText().toString();
                newp = newPassET.getText().toString();
                rnewp = newPassReenterET.getText().toString();
                if (oldp.isEmpty()) {
                    oldPassET.setError("Enter current password");
                    oldPassET.requestFocus();
                    return;
                }
                if (newp.isEmpty()) {
                    newPassET.setError("Enter new password");
                    newPassET.requestFocus();
                    return;
                }
                if (rnewp.isEmpty()) {
                    newPassReenterET.setError("Reenter new password");
                    newPassReenterET.requestFocus();
                    return;
                }
                if (newp.length() < 6) {
                    newPassET.setError("Minimum password length is 6 characters!");
                    newPassET.requestFocus();
                    return;
                }
                if (!newp.equals(rnewp)) {
                    newPassReenterET.setError("Please reenter new password correctly!");
                    newPassReenterET.requestFocus();
                    return;
                }
                if (newp.equals(oldp)) {
                    newPassET.setError("Old and new password cannot be same!");
                    newPassET.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDataType userDataType = snapshot.getValue(UserDataType.class);
                        if (oldp.equals(userDataType.getPassword())) {

                            mAuth.getCurrentUser().updatePassword(newp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        reference.child("password").setValue(newp);
                                        errorMessage.setTextColor(ContextCompat.getColor(ConsumerChangePasswordActivity.this, R.color.success));
                                        errorMessage.setText("Password has changed successfully!");
                                        errorMessage.setVisibility(View.VISIBLE);

                                    } else {
                                        errorMessage.setTextColor(ContextCompat.getColor(ConsumerChangePasswordActivity.this, R.color.error));
                                        errorMessage.setText("Password change Failed!");
                                        errorMessage.setVisibility(View.VISIBLE);

                                    }
                                }
                            });
                        } else {
                            oldPassET.setError("Wrong password!");
                            oldPassET.requestFocus();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                progressBar.setVisibility(View.GONE);
                //  startActivity(new Intent(ConsumerChangePasswordActivity.this,ConsumerActivity.class));

            }
        });


    }
}