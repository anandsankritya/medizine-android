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
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medizine.R;
import com.medizine.db.StorageService;
import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.model.MediaLink;
import com.medizine.model.Response;
import com.medizine.model.entity.User;
import com.medizine.model.enums.Gender;
import com.medizine.network.Connectivity;
import com.medizine.network.NetworkService;
import com.medizine.network.RetryOperator;
import com.medizine.network.RxNetwork;
import com.medizine.utils.ImageUtils;
import com.medizine.utils.Utils;
import com.medizine.widgets.ProfilePicEditWidget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditProfileActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private static final int REQUEST_CAMERA_ID_PROOF = 103;
    private static final int SELECT_FILE_ID_PROOF = 104;
    private static final String CAMERA_ID_PROOF_IMAGE_PATH = "cameraIdProofImagePath";
    private static final String USER_IMAGE = "userImage";
    private static final String USER_ID_PROOF_IMAGE = "userIdProofImage";
    private static final String PROFILE_CURRENT_STATE = "userPatch";
    private static final String ALTERNATIVE_VERIFIED = "alternativeVerified";
    private static final String USER_IMAGE_PATH = "userImagePath";
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
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
    String mUserImage;
    String userIdProofImageUrl;
    String userImagePath;
    private User mUser;
    private User mUserPatch;
    private boolean hasAlternativeVerified = false;
    private Gson gson = new Gson();
    private Type fileType = new TypeToken<File>() {
    }.getType();
    private Type userType = new TypeToken<User>() {
    }.getType();

    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PROFILE_CURRENT_STATE, gson.toJson(getCurrentState(), userType));
        outState.putString(CAMERA_ID_PROOF_IMAGE_PATH, gson.toJson(mCameraIdProofImagePath, fileType));
        outState.putString(USER_IMAGE, mUserImage);
        outState.putString(USER_ID_PROOF_IMAGE, userIdProofImageUrl);
        if (profilePicEditWidget.getData() != null) {
            userImagePath = profilePicEditWidget.getData();
        }
        outState.putString(USER_IMAGE_PATH, userImagePath);
        outState.putBoolean(ALTERNATIVE_VERIFIED, hasAlternativeVerified);
    }

    private User getCurrentState() {
        User currentUser = new User();

        String name = etName.getText().toString();
        currentUser.setName(name);

        if (rbFemale.getId() == genderGroup.getCheckedRadioButtonId()) {
            currentUser.setGender(Gender.FEMALE.toString());
        } else if (rbMale.getId() == genderGroup.getCheckedRadioButtonId()) {
            currentUser.setGender(Gender.MALE.toString());
        }

        String dOB = dob.getText().toString();
        currentUser.setDateOfBirth(dOB);

        String emailId = email.getText().toString();
        currentUser.setEmail(emailId);

        return currentUser;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.edit_profile);
        }

        mUser = StorageService.getInstance().getUser();

        profilePicEditWidget.init(this, rxPermissions);
        phone.setText(mUser.getCountryCode() + " " + mUser.getMobile());

        if (savedInstanceState != null) {
            mCameraIdProofImagePath = gson.fromJson(savedInstanceState.getString(CAMERA_ID_PROOF_IMAGE_PATH), fileType);
            mUserImage = savedInstanceState.getString(USER_IMAGE);
            userIdProofImageUrl = savedInstanceState.getString(USER_ID_PROOF_IMAGE);
            mUserPatch = gson.fromJson(savedInstanceState.getString(PROFILE_CURRENT_STATE), userType);
            hasAlternativeVerified = savedInstanceState.getBoolean(ALTERNATIVE_VERIFIED);
            userImagePath = savedInstanceState.getString(USER_IMAGE_PATH);
            initialize(mUserPatch);
        } else {
            mUserPatch = new User();
            initialize(mUser);
        }
    }

    private void initialize(User user) {
        etName.setText(user.getName());

        // Set DOB
        if (!Utils.isNullOrEmpty(user.getDateOfBirth())) {
            dob.setText(user.getDateOfBirth());
        }

        // Set Gender
        if (Gender.MALE.toString().equalsIgnoreCase(user.getGender())) {
            rbMale.setChecked(true);
        } else if (Gender.FEMALE.toString().equalsIgnoreCase(user.getGender())) {
            rbFemale.setChecked(true);
        }

        // Set Email
        if (!Utils.isNullOrEmpty(user.getEmail())) {
            email.setText(user.getEmail());
        }

        // Set Profile Pic
        if (!Utils.isNullOrEmpty(userImagePath)) {
            profilePicEditWidget.setData(userImagePath);
        } else if (!Utils.isNullOrEmpty(user.getProfilePicAsString())) {
            profilePicEditWidget.setData(user.getProfilePicAsString());
            userImagePath = user.getProfilePicAsString();
        }

        // Set Id Proof
        if (!Utils.isNullOrEmpty(userIdProofImageUrl)) {
            ImageUtils.loadPicInView(this, userIdProofImageUrl, idProof);
            emptyIdProof.setVisibility(View.GONE);
        } else if (!Utils.isNullOrEmpty(user.getIdProofAsString())) {
            ImageUtils.loadPicInView(this, user.getIdProofAsString(), idProof);
            userIdProofImageUrl = user.getIdProofAsString();
            emptyIdProof.setVisibility(View.GONE);
        }

        initializeListeners();
    }

    private void initializeListeners() {
        dob.setOnClickListener(v -> showDatePickerDialog());

        genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            rbMale.setError(null);
            switch (checkedId) {
                case R.id.rbMale:
                    mUserPatch.setGender(Gender.MALE.toString());
                    break;
                case R.id.rbFemale:
                    mUserPatch.setGender(Gender.FEMALE.toString());
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
        userImagePath = profilePicEditWidget.getData();
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA_ID_PROOF:
                    if (!mCameraIdProofImagePath.getAbsolutePath().isEmpty()) {
                        Uri imageUri = Uri.fromFile(mCameraIdProofImagePath);
                        if (imageUri != null) {
                            ImageUtils.loadPicInView(this, imageUri, idProof);
                            emptyIdProof.setVisibility(View.GONE);
                            userIdProofImageUrl = mCameraIdProofImagePath.getAbsolutePath();
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
                            userIdProofImageUrl = path;
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
        mUserPatch.setName(etName.getText().toString().trim());

        // Set DOB
        mUserPatch.setDateOfBirth(dob.getText().toString());

        // Set Email
        mUserPatch.setEmail(email.getText().toString());

        updateUser();

    }

    private void updateUser() {
        mUserImage = profilePicEditWidget.getData();
        if (mUser.getProfilePic() != null && mUser.getProfilePic().contains(mUserImage)) {
            mUserImage = null;
        }
        RxNetwork.observeNetworkConnectivity(this)
                .doOnSubscribe(__ -> {
                    showProgressBar();
                })
                .doFinally(() -> hideProgressBar())
                .flatMapSingle((Function<Connectivity, SingleSource<Response<MediaLink>>>) connectivity -> {
                    if (connectivity.isAvailable()) {
                        if (mUserImage != null) {
                            // Upload Profile Pic
                            File file = ImageUtils.processImageBeforeUpload(mUserImage);
                            InputStream is = new BufferedInputStream(new FileInputStream(file));
                            String mimeType = URLConnection.guessContentTypeFromStream(is);
                            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
                            final MultipartBody.Part user_image = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                            final RequestBody fileName = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(System.nanoTime()));
                            return NetworkService.getInstance().uploadMedia("image", user_image, fileName);
                        } else {
                            return Single.just(new Response<>());
                        }
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .flatMapSingle((Function<Response<MediaLink>, SingleSource<Response<MediaLink>>>) response -> {
                    if (response != null && response.getData() != null) {
                        mUserPatch.setProfilePicImageId(response.getData().getId());
                    }

                    if (mUser.getIdProof() != null && mUser.getIdProof().contains(userIdProofImageUrl)) {
                        userIdProofImageUrl = null;
                    }

                    if (userIdProofImageUrl != null) {
                        // Upload Id Proof
                        File file = ImageUtils.processImageBeforeUpload(userIdProofImageUrl);
                        InputStream is = new BufferedInputStream(new FileInputStream(file));
                        String mimeType = URLConnection.guessContentTypeFromStream(is);
                        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
                        final MultipartBody.Part user_image = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        final RequestBody fileName = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(System.nanoTime()));
                        return NetworkService.getInstance().uploadMedia("image", user_image, fileName);
                    }
                    return Single.just(new Response<>());
                })
                .flatMapSingle(response -> {
                    if (response != null && response.getData() != null) {
                        mUserPatch.setIdProofImageId(response.getData().getId());
                    }

                    return NetworkService.getInstance().updateUser(mUserPatch);
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    User user = response.getData();
                    StorageService.getInstance().updateUser(user);
                    setResult(RESULT_OK);
                    onBackPressed();
                }, throwable -> {
                    if (throwable instanceof NetworkUnavailableException) {
                        Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfileActivity.this, getString(R.string.oops_something_went_wrong), Toast.LENGTH_SHORT).show();
                        Utils.logException(TAG, throwable);
                    }
                });
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
}


