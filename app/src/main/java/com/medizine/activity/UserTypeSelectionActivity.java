package com.medizine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.medizine.Constants;
import com.medizine.R;
import com.medizine.db.PrefService;
import com.medizine.model.enums.UserType;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserTypeSelectionActivity extends BaseActivity {

    @BindView(R.id.btnDoctorUserType)
    Button btnDoctorUserType;
    @BindView(R.id.btnNormalUserType)
    Button btnNormalUserType;

    public static void launchUserTypeSelectionActivity(@NonNull Context context) {
        Intent intent = new Intent(context, UserTypeSelectionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_selection);
        ButterKnife.bind(this);

        btnDoctorUserType.setOnClickListener(view -> {
            PrefService.getInstance().saveData(Constants.USER_TYPE, UserType.DOCTOR.name());
            LoginActivity.launchLoginActivity(UserTypeSelectionActivity.this);
        });

        btnNormalUserType.setOnClickListener(view -> {
            PrefService.getInstance().saveData(Constants.USER_TYPE, UserType.NORMAL.name());
            LoginActivity.launchLoginActivity(UserTypeSelectionActivity.this);
        });
    }
}