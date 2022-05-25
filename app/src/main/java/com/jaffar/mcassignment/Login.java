package com.jaffar.mcassignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Login extends AppCompatActivity {

    private static final String TAG = "Auth";
    TextView gotoSignup;
    TextInputEditText emailLogin, passLogin;
    MaterialButton submitLogin;
    FirebaseAuth mAuth;
    ProgressBar pg;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    ImageButton googleSignInBtn, facebookSignInBtn;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gotoSignup = findViewById(R.id.gotoSignup);
        submitLogin = findViewById(R.id.loginBtn);
        emailLogin = findViewById(R.id.enterEmailLogin);
        passLogin = findViewById(R.id.enterPassLogin);
        mAuth = FirebaseAuth.getInstance();
        pg = findViewById(R.id.progressLogin);
        googleSignInBtn = findViewById(R.id.googleSignInBtn);
        facebookSignInBtn = findViewById(R.id.facebookSignInBtn);
        callbackManager = CallbackManager.Factory.create();

        gotoSignup.setOnClickListener(view -> {
            Log.d(TAG, "onCreate: Clicked");
            startActivity(new Intent(getApplicationContext(), Signup.class));
        });

        googleSignInBtn.setOnClickListener(v->{
            pg.setVisibility(View.VISIBLE);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 109);
        });

        facebookSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("email", "public_profile", "user_friends"));
            }
        });

        submitLogin.setOnClickListener(view -> {

            if (emailLogin.getText().toString().isEmpty() || passLogin.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter all fields!!", Toast.LENGTH_SHORT).show();
            } else {
                pg.setVisibility(View.VISIBLE);
                String email = emailLogin.getText().toString();
                String password = passLogin.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    pg.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),
                                            "Login Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }else {
                                    pg.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),
                                            "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pg.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),
                                        "Wrong credentials!!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Action cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onError: "+exception.getMessage());
                    }
                });
        //jaffarkhan089@gmail.com
        //12345
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 109){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            pg.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Sign Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            Log.d(TAG, "onStart: Already singin with custom");
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        else if(GoogleSignIn.getLastSignedInAccount(this)!=null){
            Log.d(TAG, "onStart: Already singin with google");
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        else if (isLoggedIn){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

    }
}