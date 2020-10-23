package com.medizine.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return mFirebaseAuth.getCurrentUser();
    }

    public boolean isUserSignedIn() {
        return getCurrentFirebaseUser() != null;
    }
}