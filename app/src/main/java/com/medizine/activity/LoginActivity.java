package com.medizine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.medizine.R;

import java.util.Collections;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 101;

    public static void launchLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showOtpVerificationUI();
    }

    private void showOtpVerificationUI() {
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
                if (getCurrentFirebaseUser() != null) {
                    HomeActivity.launchHomeActivity(this);
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
                    Log.d(TAG, "Sign-in error: ", response.getError());
                }
            }
        }
    }
}