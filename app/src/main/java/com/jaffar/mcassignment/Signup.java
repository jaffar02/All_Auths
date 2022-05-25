package com.jaffar.mcassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {
    private static final String TAG = "SignUpAuth";
    TextInputEditText emailSignup, passSignup, confPassSignup;
    MaterialButton submitSignup;
    FirebaseAuth mAuth;
    ProgressBar pg;
    ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        submitSignup = findViewById(R.id.SignupBtn);
        emailSignup = findViewById(R.id.enterEmailSignup);
        passSignup = findViewById(R.id.enterPassSignup);
        confPassSignup = findViewById(R.id.enterConfPassSignUp);
        mAuth = FirebaseAuth.getInstance();
        pg = findViewById(R.id.progressSignup);
        backBtn = findViewById(R.id.backBtn);

        submitSignup.setOnClickListener(view -> {
            if (emailSignup.getText().toString().isEmpty() || passSignup.getText().toString().isEmpty() ||
                    confPassSignup.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter all fields!!", Toast.LENGTH_SHORT).show();
            }else{
                String email = emailSignup.getText().toString();
                String password = passSignup.getText().toString();
                String ConfPassword = confPassSignup.getText().toString();
                if(password.equals(ConfPassword)) {
                    pg.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                pg.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Login.class));
                            }else{
                                pg.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Registration Failed!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pg.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }


        });

        backBtn.setOnClickListener(v->{
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        });
    }
}