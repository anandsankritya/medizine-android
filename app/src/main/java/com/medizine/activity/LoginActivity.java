package com.medizine.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medizine.R;

import java.util.Collections;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final int RC_SIGN_IN = 101;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        if (mFirebaseAuth.getCurrentUser() != null) {
            navigateToHoneActivity(mFirebaseAuth.getCurrentUser());
        } else {
            showLoginScreen();
        }
    }

    private void navigateToHoneActivity(@NonNull FirebaseUser user) {
        String userInfo = "Phone : " + user.getPhoneNumber();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("USER_INFO", userInfo);
        startActivity(intent);
        finish();
    }

    private void showLoginScreen() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.PhoneBuilder().build()))
                        .setTosAndPrivacyPolicyUrls("https://superapp.example.com/terms-of-service.html",
                                "https://superapp.example.com/privacy-policy.html")
                        .setTheme(R.style.FirebaseLoginUI)
                        .build(),
                RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    navigateToHoneActivity(user);
                } else {
                    Toast.makeText(this, "Oops something went wrong!", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "Sign in cancelled!", Toast.LENGTH_SHORT).show();
                } else if (response.getError() != null && response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Unknown error, please try again later!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Sign-in error: ", response.getError());
                }
            }
        }
    }
}