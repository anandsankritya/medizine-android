package com.medizine.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medizine.R;
import com.medizine.db.StorageService;
import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.model.entity.Doctor;
import com.medizine.model.enums.Gender;
import com.medizine.network.NetworkService;
import com.medizine.network.RetryOperator;
import com.medizine.network.RxNetwork;
import com.medizine.utils.ImageUtils;
import com.medizine.utils.Utils;
import com.medizine.widgets.ProfilePicEditWidget;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EditDoctorProfileActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = EditDoctorProfileActivity.class.getSimpleName();
    private static final int REQUEST_CAMERA_ID_PROOF = 103;
    private static final int SELECT_FILE_ID_PROOF = 104;
    private static final String CAMERA_ID_PROOF_IMAGE_PATH = "cameraIdProofImagePath";
    private static final String USER_IMAGE = "doctorImage";
    private static final String USER_ID_PROOF_IMAGE = "doctorIdProofImage";
    private static final String PROFILE_CURRENT_STATE = "doctorPatch";
    private static final String ALTERNATIVE_VERIFIED = "alternativeVerified";
    private static final String USER_IMAGE_PATH = "doctorImagePath";
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Nullable
    protected CompositeDisposable networkDisposable;
    @BindView(R.id.name)
    EditText etName;
    @BindView(R.id.genderGroup)
    RadioGroup genderGroup;
    @BindView(R.id.rbMale)
    RadioButton rbMale;
    @BindView(R.id.rbFemale)
    RadioButton rbFemale;
    @BindView(R.id.dateOfBirth)
    EditText dob;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.idProof)
    ImageView idProof;
    @BindView(R.id.emptyIdProof)
    TextView emptyIdProof;
    @BindView(R.id.profilPicEditWidget)
    ProfilePicEditWidget profilePicEditWidget;
    File mCameraIdProofImagePath;
    String mDoctorImage;
    String doctorIdProofImageUrl;
    String doctorImagePath;
    private Doctor mDoctor;
    private Doctor mDoctorPatch;
    private boolean hasAlternativeVerified = false;
    private Gson gson = new Gson();
    private Type fileType = new TypeToken<File>() {
    }.getType();
    private Type doctorType = new TypeToken<Doctor>() {
    }.getType();

    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PROFILE_CURRENT_STATE, gson.toJson(getCurrentState(), doctorType));
        outState.putString(CAMERA_ID_PROOF_IMAGE_PATH, gson.toJson(mCameraIdProofImagePath, fileType));
        outState.putString(USER_IMAGE, mDoctorImage);
        outState.putString(USER_ID_PROOF_IMAGE, doctorIdProofImageUrl);
        if (profilePicEditWidget.getData() != null) {
            doctorImagePath = profilePicEditWidget.getData();
        }
        outState.putString(USER_IMAGE_PATH, doctorImagePath);
        outState.putBoolean(ALTERNATIVE_VERIFIED, hasAlternativeVerified);
    }

    private Doctor getCurrentState() {
        Doctor currentDoctor = new Doctor();

        String name = etName.getText().toString();
        currentDoctor.setName(name);

        if (rbFemale.getId() == genderGroup.getCheckedRadioButtonId()) {
            currentDoctor.setGender(Gender.FEMALE.toString());
        } else if (rbMale.getId() == genderGroup.getCheckedRadioButtonId()) {
            currentDoctor.setGender(Gender.MALE.toString());
        }

        String dOB = dob.getText().toString();
        currentDoctor.setDob(dOB);

        String emailId = email.getText().toString();
        currentDoctor.setEmailAddress(emailId);

        return currentDoctor;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor_profile);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.edit_profile));
        }

        mDoctor = StorageService.getInstance().getDoctor();

        profilePicEditWidget.init(this, rxPermissions);
        phone.setText(mDoctor.getCountryCode() + " " + mDoctor.getPhoneNumber());

        if (savedInstanceState != null) {
            mCameraIdProofImagePath = gson.fromJson(savedInstanceState.getString(CAMERA_ID_PROOF_IMAGE_PATH), fileType);
            mDoctorImage = savedInstanceState.getString(USER_IMAGE);
            doctorIdProofImageUrl = savedInstanceState.getString(USER_ID_PROOF_IMAGE);
            mDoctorPatch = gson.fromJson(savedInstanceState.getString(PROFILE_CURRENT_STATE), doctorType);
            hasAlternativeVerified = savedInstanceState.getBoolean(ALTERNATIVE_VERIFIED);
            doctorImagePath = savedInstanceState.getString(USER_IMAGE_PATH);
            initialize(mDoctorPatch);
        } else {
            mDoctorPatch = new Doctor();
            initialize(mDoctor);
        }
    }

    private void initialize(Doctor doctor) {
        etName.setText(doctor.getName());

        // Set DOB
        if (!Utils.isNullOrEmpty(doctor.getDob())) {
            dob.setText(doctor.getDob());
        }

        // Set Gender
        if (Gender.MALE.toString().equalsIgnoreCase(doctor.getGender())) {
            rbMale.setChecked(true);
        } else if (Gender.FEMALE.toString().equalsIgnoreCase(doctor.getGender())) {
            rbFemale.setChecked(true);
        }

        // Set Email
        if (!Utils.isNullOrEmpty(doctor.getEmailAddress())) {
            email.setText(doctor.getEmailAddress());
        }

        /*
         //Set Profile Pic
        if (!Utils.isNullOrEmpty(doctorImagePath)) {
            profilePicEditWidget.setData(doctorImagePath);
        } else if (!Utils.isNullOrEmpty(doctor.getProfilePicAsString())) {
            profilePicEditWidget.setData(doctor.getProfilePicAsString());
            doctorImagePath = doctor.getProfilePicAsString();
        }

         //Set Id Proof
        if (!Utils.isNullOrEmpty(doctorIdProofImageUrl)) {
            ImageUtils.loadPicInView(this, doctorIdProofImageUrl, idProof);
            emptyIdProof.setVisibility(View.GONE);
        } else if (!Utils.isNullOrEmpty(doctor.getIdProofAsString())) {
            ImageUtils.loadPicInView(this, doctor.getIdProofAsString(), idProof);
            doctorIdProofImageUrl = doctor.getIdProofAsString();
            emptyIdProof.setVisibility(View.GONE);
        }
         */

        initializeListeners();
    }

    private void initializeListeners() {
        dob.setOnClickListener(v -> showDatePickerDialog());

        genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            rbMale.setError(null);
            switch (checkedId) {
                case R.id.rbMale:
                    mDoctorPatch.setGender(Gender.MALE.toString());
                    break;
                case R.id.rbFemale:
                    mDoctorPatch.setGender(Gender.FEMALE.toString());
                    break;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @OnClick({R.id.idProof, R.id.emptyIdProof})
    public void editIdProof(View view) {
        mCameraIdProofImagePath = ImageUtils.showPhotoEditDialog(this, rxPermissions, REQUEST_CAMERA_ID_PROOF, SELECT_FILE_ID_PROOF, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profilePicEditWidget.onActivityResult(requestCode, resultCode, data);
        doctorImagePath = profilePicEditWidget.getData();
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA_ID_PROOF:
                    if (!mCameraIdProofImagePath.getAbsolutePath().isEmpty()) {
                        Uri imageUri = Uri.fromFile(mCameraIdProofImagePath);
                        if (imageUri != null) {
                            ImageUtils.loadPicInView(this, imageUri, idProof);
                            emptyIdProof.setVisibility(View.GONE);
                            doctorIdProofImageUrl = mCameraIdProofImagePath.getAbsolutePath();
                            ImageUtils.refreshGallery(this, imageUri.getPath());
                        }
                    }
                    break;
                case SELECT_FILE_ID_PROOF:
                    if (data != null && data.getData() != null) {
                        String path = ImageUtils.getPathFromGallaryResult(this, data.getData());
                        if (!Utils.isNullOrEmpty(path)) {
                            ImageUtils.loadPicInView(this, path, idProof);
                            emptyIdProof.setVisibility(View.GONE);
                            doctorIdProofImageUrl = path;
                        }
                    }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.done:
                if (validate()) {
                    uploadProfileDetail();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadProfileDetail() {
        // Set name
        mDoctorPatch.setName(etName.getText().toString().trim());

        // Set DOB
        //mDoctorPatch.setDob(dob.getText().toString());

        // Set Email
        mDoctorPatch.setEmailAddress(email.getText().toString());

        updateDoctor();
    }

    private void updateDoctor() {
        setProgressDialogMessage(getString(R.string.saving));
        showProgressBar();

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().patchDoctorById(mDoctorPatch);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            Doctor doctor = response.getData();
                            StorageService.getInstance().updateDoctor(doctor);
                            setResult(RESULT_OK);
                            hideProgressBar();
                            onBackPressed();
                        }, throwable -> {
                            hideProgressBar();
                            if (throwable instanceof NetworkUnavailableException) {
                                showToast(getString(R.string.internet_unavailable));
                            } else {
                                showToast(getString(R.string.oops_something_went_wrong));
                                Utils.logException(TAG, throwable);
                            }
                        }
                );
    }

    private boolean validate() {
        boolean valid = true;

        // Validate Name
        if (etName.getText().toString().trim().length() <= 3) {
            valid = false;
            etName.setError(getResources().getString(R.string.error_valid_name));
        }

        // Validate Gender
        if (genderGroup.getCheckedRadioButtonId() == -1) {
            valid = false;
            rbMale.setError(getResources().getString(R.string.error_select_gender));
            genderGroup.requestChildFocus(genderGroup, rbMale);
        }

        // Validate Email
        if (!email.getText().toString().isEmpty()) {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email.getText().toString().trim());
            if (!matcher.find()) {
                email.setError(getResources().getString(R.string.error_valid_email));
                valid = false;
            }
        }
        return valid;
    }

    public void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        //Set max limit
        final Calendar maxDate = Calendar.getInstance();
        maxDate.set(mYear - 13, mMonth, mDay);

        int defaultYear = 1980;
        int defaultMonth = Calendar.JANUARY;
        int defaultDay = 1;

        String currentDateString = dob.getText().toString();
        if (!Utils.isNullOrEmpty(currentDateString)) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = format.parse(currentDateString);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                defaultYear = cal.get(Calendar.YEAR);
                defaultMonth = cal.get(Calendar.MONTH);
                defaultDay = cal.get(Calendar.DAY_OF_MONTH);

            } catch (ParseException e) {
                Utils.logException(TAG, e);
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.app.AlertDialog.THEME_HOLO_LIGHT, this, defaultYear, defaultMonth, defaultDay);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateString = format.format(c.getTimeInMillis());
        dob.setText(currentDateString);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}


