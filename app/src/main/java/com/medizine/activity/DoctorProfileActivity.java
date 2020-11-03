package com.medizine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.medizine.R;
import com.medizine.db.StorageService;
import com.medizine.model.entity.Doctor;
import com.medizine.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.medizine.Constants.REQUEST_EDIT_DOCTOR_PROFILE;

public class DoctorProfileActivity extends BaseActivity {
    private static final String TAG = DoctorProfileActivity.class.getSimpleName();

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.gender)
    TextView gender;
    @BindView(R.id.dateOfBirth)
    TextView dob;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.email)
    TextView email;

    public static void launchDoctorProfileActivity(Context context) {
        Intent intent = new Intent(context, DoctorProfileActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.profile));

        fetchDoctorData();
    }

    private void fetchDoctorData() {
        renderData(StorageService.getInstance().getDoctor());
    }

    public void renderData(Doctor doctor) {
        name.setText(doctor.getName());
        phone.setText(doctor.getCountryCode() + " " + doctor.getPhoneNumber());
        phone.setOnClickListener(v -> Utils.dialPhone(DoctorProfileActivity.this, doctor.getCountryCode() + doctor.getPhoneNumber()));
        // Set DOB
        if (Utils.isNullOrEmpty(doctor.getDob())) {
            dob.setText("-");
        } else {
            dob.setText(doctor.getDob());
        }
        // Set gender
        if (Utils.isNullOrEmpty(doctor.getGender())) {
            gender.setText("-");
        } else {
            gender.setText(doctor.getGender());
        }
        // Set Email
        if (Utils.isNullOrEmpty(doctor.getEmailAddress())) {
            email.setText("-");
        } else {
            email.setText(doctor.getEmailAddress());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menuEdit:
                Intent intent = new Intent(this, EditDoctorProfileActivity.class);
                startActivityForResult(intent, REQUEST_EDIT_DOCTOR_PROFILE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_DOCTOR_PROFILE) {
            if (resultCode == RESULT_OK) {
                fetchDoctorData();
            }
        }
    }
}
