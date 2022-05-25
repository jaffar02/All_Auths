package com.jaffar.mcassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "main";
    Spinner state;
    EditText fname, lname, address, city, zipcode, email, phoneNum;
    MaterialButton submit, logout;
    DaoClass db;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        state = findViewById(R.id.state);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        zipcode = findViewById(R.id.zipcode);
        email = findViewById(R.id.email);
        phoneNum = findViewById(R.id.phoneNum);
        submit = findViewById(R.id.submit);
        db = new DaoClass();
        logout = findViewById(R.id.logoutBtn);

        setStateList();

        submit.setOnClickListener(v->{
            String firstName = fname.getText().toString().toLowerCase(Locale.ROOT);
            String lastName = lname.getText().toString().toLowerCase(Locale.ROOT);
            String uAddress = address.getText().toString().toLowerCase(Locale.ROOT);
            String uCity = city.getText().toString().toLowerCase(Locale.ROOT);
            String uEmail = email.getText().toString().toLowerCase(Locale.ROOT);

            long uZip = 0;
            if (!zipcode.getText().toString().isEmpty()) {
                uZip = Long.parseLong(zipcode.getText().toString());
            }

            long uPhone = 0;
            if (!phoneNum.getText().toString().isEmpty()) {
                uPhone = Long.parseLong(phoneNum.getText().toString());
            }

            if (firstName.isEmpty()){
                fname.setError("fill this fields!");
            }
            if (lastName.isEmpty()){
                lname.setError("fill this field!");
            }
            if (uAddress.isEmpty()){
                address.setError("fill this field!");
            }
            if (uCity.isEmpty()){
                city.setError("fill this field!");
            }
            if (uEmail.isEmpty()){
                email.setError("fill this field!");
            }
            if (uPhone==0){
                phoneNum.setError("fill this field!");
            }
            if (uZip==0){
                zipcode.setError("fill this field!");
            }
            else{
                User user = new User(firstName, lastName, uAddress, uCity, state.getSelectedItem().toString(), uZip
                , uEmail, uPhone);
                db.insert(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Transaction Successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Transaction failed!", Toast.LENGTH_SHORT).show();
                    }
                });
                emptyAllField();
            }
        });

        logout.setOnClickListener(v->{
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

            if(mAuth.getCurrentUser()!=null) {
                Log.d(TAG, "onCreate: custom signout");
                mAuth.signOut();
                finish();
            }
            if(GoogleSignIn.getLastSignedInAccount(getApplicationContext())!=null) {
                mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onCreate: google signout");
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Logout failed!", Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
          if(isLoggedIn) {
              LoginManager.getInstance().logOut();
              startActivity(new Intent(getApplicationContext(), Login.class));
          }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setStateList(){
        String[] statesList = {"Afghanistan","Albania","Algeria","Andorra","Angola","Anguilla","Antigua &amp; Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bosnia &amp; Herzegovina","Botswana","Brazil","British Virgin Islands","Brunei","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Cape Verde","Cayman Islands","Chad","Chile","China","Colombia","Congo","Cook Islands","Costa Rica","Cote D Ivoire","Croatia","Cruise Ship","Cuba","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Estonia","Ethiopia","Falkland Islands","Faroe Islands","Fiji","Finland","France","French Polynesia","French West Indies","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guam","Guatemala","Guernsey","Guinea","Guinea Bissau","Guyana","Haiti","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Isle of Man","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kuwait","Kyrgyz Republic","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macau","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Mauritania","Mauritius","Mexico","Moldova","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Namibia","Nepal","Netherlands","Netherlands Antilles","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","Norway","Oman","Pakistan","Palestine","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal","Puerto Rico","Qatar","Reunion","Romania","Russia","Rwanda","Saint Pierre &amp; Miquelon","Samoa","San Marino","Satellite","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia","Slovenia","South Africa","South Korea","Spain","Sri Lanka","St Kitts &amp; Nevis","St Lucia","St Vincent","St. Lucia","Sudan","Suriname","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Timor L'Este","Togo","Tonga","Trinidad &amp; Tobago","Tunisia","Turkey","Turkmenistan","Turks &amp; Caicos","Uganda","Ukraine","United Arab Emirates","United Kingdom","Uruguay","Uzbekistan","Venezuela","Vietnam","Virgin Islands (US)","Yemen","Zambia","Zimbabwe"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item
                , statesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(adapter);
    }

    private void emptyAllField(){
       fname.setText("");
       lname.setText("");
       address.setText("");
       city.setText("");
       email.setText("");
       zipcode.setText("");
       phoneNum.setText("");
    }
}