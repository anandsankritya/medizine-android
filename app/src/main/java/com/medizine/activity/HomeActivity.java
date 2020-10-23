package com.medizine.activity;

import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.medizine.Constants;
import com.medizine.R;
import com.medizine.db.PrefService;
import com.medizine.model.enums.UserType;
import com.medizine.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.tvUserInfo)
    TextView tvUserInfo;

    public static void launchHomeActivity(@NonNull Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        if (Utils.isUserTypeNormal()) {
            validateUserProfile();
        } else if (Utils.isUserTypeDoctor()) {
            validateDoctorProfile();
        } else {
            Utils.logOutUser();
        }
    }

    private void validateDoctorProfile() {
        //1. Check is a doctor already exists with {countryCode} and {phone} via network call
        //2. If doctor already exists fetch doctor profile via network call and render UI accordingly
        //3. If doctor not exists create a doctor using same {countryCode} and {phone} via network call
    }

    private void validateUserProfile() {
        //1. Check is a doctor already exists with {countryCode} and {phone} via network call
        //2. If doctor already exists fetch doctor profile via network call and render UI accordingly
        //3. If doctor not exists create a doctor using same {countryCode} and {phone} via network call
    }
}