package com.medizine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medizine.Constants;
import com.medizine.R;
import com.medizine.ThrottleClick;
import com.medizine.adapter.SlotListAdapter;
import com.medizine.db.StorageService;
import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.model.entity.Doctor;
import com.medizine.network.NetworkService;
import com.medizine.network.RetryOperator;
import com.medizine.network.RxNetwork;
import com.medizine.utils.Utils;
import com.medizine.widgets.SectionWidget;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.medizine.Constants.REQUEST_EDIT_DOCTOR_PROFILE;

public class DoctorProfileActivity extends BaseActivity implements SlotListAdapter.OnSlotRemoved {
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
    @BindView(R.id.tvBook)
    TextView tvBook;
    @BindView(R.id.sectionSlots)
    SectionWidget sectionSlots;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private String mExternalDoctorId;
    private SlotListAdapter mSlotListAdapter;

    public static void launchDoctorProfileActivity(Context context, String externalDoctorId) {
        Intent intent = new Intent(context, DoctorProfileActivity.class);
        if (Utils.isNotEmpty(externalDoctorId)) {
            intent.putExtra(Constants.DOCTOR_ID, externalDoctorId);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        if (getIntent() != null && getIntent().hasExtra(Constants.DOCTOR_ID)) {
            mExternalDoctorId = getIntent().getStringExtra(Constants.DOCTOR_ID);
        }
        fetchDoctorData();
    }

    private void fetchDoctorData() {
        if (Utils.isNotEmpty(mExternalDoctorId)) {
            fetchDoctorDataFromNetwork(mExternalDoctorId);
            renderSlotsForDoctor(mExternalDoctorId);
        } else {
            renderData(StorageService.getInstance().getDoctor());
            renderSlotsForDoctor(StorageService.getInstance().getDoctor().getId());
        }
        boolean showRemoveSlotIcon = Utils.isUserTypeDoctor() && Utils.getDoctorID().equals(StorageService.getInstance().getDoctor().getId());
        mSlotListAdapter = new SlotListAdapter(this, true, null, this::onSlotRemoved, showRemoveSlotIcon);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mSlotListAdapter);
    }

    private void renderSlotsForDoctor(String doctorId) {
        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getAllSlotsByDoctorId(doctorId);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            mSlotListAdapter.setList(response.getData());
                            sectionSlots.setVisibility(Utils.isListEmpty(response.getData()) ? View.GONE : View.VISIBLE);
                            tvBook.setVisibility(Utils.isNullOrEmpty(mExternalDoctorId) ? View.GONE : View.VISIBLE);
                            tvBook.setBackgroundColor(getResources().getColor(Utils.isListEmpty(response.getData()) ? R.color.grey400 : R.color.primary));
                            tvBook.setEnabled(!Utils.isListEmpty(response.getData()));
                            tvBook.setOnClickListener(new ThrottleClick() {
                                @Override
                                public void onClick() {
                                    BookingActivity.launchBookingActivity(DoctorProfileActivity.this, mExternalDoctorId);
                                }
                            });
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

    private void fetchDoctorDataFromNetwork(@NonNull String externalDoctorId) {
        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getDoctorById(externalDoctorId);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.getData() != null) {
                                renderData(response.getData());
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

    public void renderData(Doctor doctor) {
        getSupportActionBar().setTitle(doctor.getName());

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
    public boolean onPrepareOptionsMenu(@androidx.annotation.NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Doctor doctor = StorageService.getInstance().getDoctor();
        boolean condition = false;
        if (doctor != null) {
            if (Utils.isNotEmpty(mExternalDoctorId)) {
                condition = mExternalDoctorId.equals(doctor.getId());
            } else {
                condition = Utils.isNotEmpty(doctor.getId());
            }
        }
        menu.findItem(R.id.menuEditProfile).setVisible(condition);
        menu.findItem(R.id.menuAddSlot).setVisible(condition);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doctor_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menuEditProfile:
                Intent intent = new Intent(this, EditDoctorProfileActivity.class);
                startActivityForResult(intent, REQUEST_EDIT_DOCTOR_PROFILE);
                return true;
            case R.id.menuAddSlot:
                addNewSlot();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewSlot() {
        EditSlotActivity.launchEditSlotActivityForResult(this, StorageService.getInstance().getDoctor().getId(), Constants.REQUEST_ADD_SLOT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_DOCTOR_PROFILE) {
            if (resultCode == RESULT_OK) {
                fetchDoctorData();
            }
        } else if (requestCode == Constants.REQUEST_ADD_SLOT) {
            if (resultCode == RESULT_OK) {
                fetchDoctorData();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onSlotRemoved(Context context, String slotId) {
        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().deleteSlotById(slotId);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.getData() != null) {
                                mSlotListAdapter.setList(response.getData());
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
}
