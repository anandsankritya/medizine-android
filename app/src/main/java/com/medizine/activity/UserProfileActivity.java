package com.medizine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.medizine.Constants;
import com.medizine.R;
import com.medizine.db.StorageService;
import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.model.entity.User;
import com.medizine.network.NetworkService;
import com.medizine.network.RetryOperator;
import com.medizine.network.RxNetwork;
import com.medizine.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.medizine.Constants.REQUEST_EDIT_USER_PROFILE;

public class UserProfileActivity extends BaseActivity {
    private static final String TAG = UserProfileActivity.class.getSimpleName();

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

    private String mExternalUserId;

    public static void launchUserProfileActivity(Context context, @Nullable String externalUserId) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        if (Utils.isNotEmpty(externalUserId)) {
            intent.putExtra(Constants.USER_ID, externalUserId);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.profile));
        if (getIntent() != null && getIntent().hasExtra(Constants.USER_ID)) {
            mExternalUserId = getIntent().getStringExtra(Constants.USER_ID);
        }
        fetchUserData();
    }

    private void fetchUserData() {
        if (Utils.isNotEmpty(mExternalUserId)) {
            fetchDoctorDataFromNetwork(mExternalUserId);
        } else {
            renderData(StorageService.getInstance().getUser());
        }
    }

    private void fetchDoctorDataFromNetwork(@NonNull String externalUserId) {
        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getUserById(externalUserId);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.getData() != null) {
                                renderData((User) response.getData());
                            }
                            hideProgressBar();
                            setProgressDialogMessage(getString(R.string.saving));
                        }, throwable -> {
                            hideProgressBar();
                            setProgressDialogMessage(getString(R.string.saving));
                            if (throwable instanceof NetworkUnavailableException) {
                                Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.logException(TAG, throwable);
                            }
                        }
                );
    }

    public void renderData(User user) {
        name.setText(user.getName());
        phone.setText(user.getCountryCode() + " " + user.getPhoneNumber());
        phone.setOnClickListener(v -> Utils.dialPhone(UserProfileActivity.this, user.getCountryCode() + user.getPhoneNumber()));
        // Set DOB
        if (Utils.isNullOrEmpty(user.getDob())) {
            dob.setText("-");
        } else {
            dob.setText(user.getDob());
        }
        // Set gender
        if (Utils.isNullOrEmpty(user.getGender())) {
            gender.setText("-");
        } else {
            gender.setText(user.getGender());
        }
        // Set Email
        if (Utils.isNullOrEmpty(user.getEmailAddress())) {
            email.setText("-");
        } else {
            email.setText(user.getEmailAddress());
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
                Intent intent = new Intent(this, EditUserProfileActivity.class);
                startActivityForResult(intent, REQUEST_EDIT_USER_PROFILE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_USER_PROFILE) {
            if (resultCode == RESULT_OK) {
                fetchUserData();
            }
        }
    }
}
