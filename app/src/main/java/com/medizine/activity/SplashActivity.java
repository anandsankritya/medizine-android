package com.medizine.activity;

import android.os.Bundle;
import android.os.Handler;

import com.medizine.R;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            if (isUserSignedIn()) {
                HomeActivity.launchHomeActivity(this);
            } else {
                UserTypeSelectionActivity.launchUserTypeSelectionActivity(this);
            }
            finish();
        }, 1000);

    }
}