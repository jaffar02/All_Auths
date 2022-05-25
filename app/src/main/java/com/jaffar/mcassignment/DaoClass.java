package com.jaffar.mcassignment;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DaoClass {
    private static final String TAG = "Dao";
    private final DatabaseReference db;

    public DaoClass(){
        db = FirebaseDatabase.getInstance().getReference();
        db.child("test");
    }

    public Task<Void> insert(User user) {
        String key = db.push().getKey();
        user.setKey(key);
        return  db.child("Users").child(key).setValue(user);
    }

}
