package com.medizine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseUser;
import com.medizine.R;
import com.medizine.db.StorageService;
import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.model.entity.Doctor;
import com.medizine.model.entity.User;
import com.medizine.network.NetworkService;
import com.medizine.network.RetryOperator;
import com.medizine.network.RxNetwork;
import com.medizine.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.medizine.Constants.COUNTRY_CODE_IN;

public class HomeActivity extends NavigationActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    @BindView(R.id.appBar)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String phoneNumber = null;

    @NonNull
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static void launchHomeActivity(@NonNull Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setDrawerEnabled(true);
        phoneNumber = null;
        FirebaseUser user = getCurrentFirebaseUser();
        if (user != null && user.getPhoneNumber() != null && user.getPhoneNumber().startsWith(COUNTRY_CODE_IN)) {
            phoneNumber = user.getPhoneNumber().replace(COUNTRY_CODE_IN, "");
        }

        if (Utils.isNotEmpty(phoneNumber)) {
            if (Utils.isUserTypeNormal()) {
                validateUserProfile(phoneNumber);
            } else if (Utils.isUserTypeDoctor()) {
                validateDoctorProfile(phoneNumber);
            }
        } else {
            Utils.logOutUser();
        }
    }

    private void validateUserProfile(String phoneNumber) {
        //1. Check if a user already exists with {countryCode} and {phone} via network call
        //2. If doctor already exists fetch doctor profile via network call and render UI accordingly
        //3. If doctor not exists create a doctor using same {countryCode} and {phone} via network call

        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getUserByPhoneNumber(phoneNumber);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.getData() != null) {
                                StorageService.getInstance().getMedizineDatabase().userDao().insertOrUpdate((User) response.getData()).subscribeOn(Schedulers.io()).blockingAwait();
                            } else {
                                createUser(phoneNumber);
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

    private void createUser(String phoneNumber) {
        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();

        User user = new User();
        user.setCountryCode(COUNTRY_CODE_IN);
        user.setPhoneNumber(phoneNumber);

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().createUser(user);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.getData() != null) {
                                StorageService.getInstance().getMedizineDatabase().userDao().insertOrUpdate((User) response.getData()).subscribeOn(Schedulers.io()).blockingAwait();
                            } else {
                                Utils.logOutUser();
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

    private void validateDoctorProfile(String phoneNumber) {
        //1. Check is a doctor already exists with {countryCode} and {phone} via network call
        //2. If doctor already exists fetch doctor profile via network call and render UI accordingly
        //3. If doctor not exists create a doctor using same {countryCode} and {phone} via network call

        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getDoctorByPhoneNumber(phoneNumber);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.getData() != null) {
                                StorageService.getInstance().getMedizineDatabase().doctorDao().insertOrUpdate((Doctor) response.getData()).subscribeOn(Schedulers.io()).blockingAwait();
                            } else {
                                createDoctor(phoneNumber);
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

    private void createDoctor(String phoneNumber) {
        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();

        Doctor doctor = new Doctor();
        doctor.setCountryCode(COUNTRY_CODE_IN);
        doctor.setPhoneNumber(phoneNumber);

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().createDoctor(doctor);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.getData() != null) {
                                StorageService.getInstance().getMedizineDatabase().doctorDao().insertOrUpdate((Doctor) response.getData()).subscribeOn(Schedulers.io()).blockingAwait();
                            } else {
                                Utils.logOutUser();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        // Close drawer, if open
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}