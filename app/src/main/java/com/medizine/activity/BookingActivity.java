package com.medizine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
import com.medizine.model.entity.Slot;
import com.medizine.model.request.SlotBookingRequest;
import com.medizine.network.NetworkService;
import com.medizine.network.RetryOperator;
import com.medizine.network.RxNetwork;
import com.medizine.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BookingActivity extends BaseActivity implements SlotListAdapter.OnSlotBookedListener {
    public static final String TAG = BookingActivity.class.getSimpleName();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btnDayOne)
    Button btnDayOne;
    @BindView(R.id.btnDayTwo)
    Button btnDayTwo;
    @BindView(R.id.btnDayThree)
    Button btnDayThree;

    @Nullable
    private String mDoctorId;
    private SlotListAdapter mSlotListAdapter;
    private Date selectedDate = null;

    public static void launchBookingActivity(@NonNull Context context, @NonNull String doctorId) {
        Intent intent = new Intent(context, BookingActivity.class);
        intent.putExtra(Constants.DOCTOR_ID, doctorId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.available_slots));
        }
        if (getIntent() != null && getIntent().hasExtra(Constants.DOCTOR_ID)) {
            mDoctorId = getIntent().getStringExtra(Constants.DOCTOR_ID);
        }
        mSlotListAdapter = new SlotListAdapter(this, false, this, null, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mSlotListAdapter);
        btnDayOne.setOnClickListener(new ThrottleClick() {
            @Override
            public void onClick() {
                loadUIForThreeDates(0);
            }
        });
        btnDayTwo.setOnClickListener(new ThrottleClick() {
            @Override
            public void onClick() {
                loadUIForThreeDates(1);
            }
        });
        btnDayThree.setOnClickListener(new ThrottleClick() {
            @Override
            public void onClick() {
                loadUIForThreeDates(2);
            }
        });
        loadUIForThreeDates(0);
    }

    private void loadUIForThreeDates(int selectedDay) {
        ArrayList<Date> dates = new ArrayList<>();
        Calendar calendarDay = Calendar.getInstance();
        for (int i = 0; i < 3; i++) {
            dates.add(calendarDay.getTime());
            calendarDay.add(Calendar.DAY_OF_WEEK, 1);
        }

        btnDayOne.setText(Utils.getFormattedDay(dates.get(0)));
        btnDayTwo.setText(Utils.getFormattedDay(dates.get(1)));
        btnDayThree.setText(Utils.getFormattedDay(dates.get(2)));

        mSlotListAdapter.setList(null);

        if (selectedDay == 0) {
            renderSlotsForDoctor(mDoctorId, dates.get(0));
            updateSelectedButtonBackground(R.id.btnDayOne);
            selectedDate = dates.get(0);
        } else if (selectedDay == 1) {
            renderSlotsForDoctor(mDoctorId, dates.get(1));
            updateSelectedButtonBackground(R.id.btnDayTwo);
            selectedDate = dates.get(1);
        } else if (selectedDay == 2) {
            renderSlotsForDoctor(mDoctorId, dates.get(2));
            updateSelectedButtonBackground(R.id.btnDayThree);
            selectedDate = dates.get(2);
        }
    }

    private void updateSelectedButtonBackground(int selectedBtnId) {
        btnDayOne.setBackgroundColor(getResources().getColor(R.id.btnDayOne == selectedBtnId ? R.color.transparent_black_light : R.color.transparent));
        btnDayTwo.setBackgroundColor(getResources().getColor(R.id.btnDayTwo == selectedBtnId ? R.color.transparent_black_light : R.color.transparent));
        btnDayThree.setBackgroundColor(getResources().getColor(R.id.btnDayThree == selectedBtnId ? R.color.transparent_black_light : R.color.transparent));
    }

    private void renderSlotsForDoctor(String doctorId, Date date) {
        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getLiveSlotStatus(Utils.toISO8601UTC(date), doctorId, StorageService.getInstance().getUser().getId());
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            mSlotListAdapter.setList(response.getData());
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
    public void onSlotBooked(Context context, Slot slot) {
        mSlotListAdapter.setList(null);
        setProgressDialogMessage(getString(R.string.booking_in_progress));
        showProgressBar();

        if (selectedDate == null) {
            showToast(getString(R.string.oops_something_went_wrong));
            return;
        }

        SlotBookingRequest slotBookingRequest = new SlotBookingRequest();
        slotBookingRequest.setBookingDate(Utils.toISO8601UTC(selectedDate));
        slotBookingRequest.setDoctorId(slot.getDoctorId());
        slotBookingRequest.setSlotId(slot.getId());
        slotBookingRequest.setUserId(Utils.getUserID());

        Disposable disposable = RxNetwork.observeNetworkConnectivity(context)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().bookAppointment(slotBookingRequest);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    loadUIForThreeDates(0);
                    hideProgressBar();
                            setProgressDialogMessage(getString(R.string.saving));
                        }, throwable -> {
                            hideProgressBar();
                            setProgressDialogMessage(getString(R.string.saving));
                            if (throwable instanceof NetworkUnavailableException) {
                                showToast(getString(R.string.internet_unavailable));
                            } else {
                                showToast(getString(R.string.oops_something_went_wrong));
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
}