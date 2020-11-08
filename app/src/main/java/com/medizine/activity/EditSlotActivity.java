package com.medizine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.medizine.Constants;
import com.medizine.R;
import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.model.entity.Slot;
import com.medizine.network.NetworkService;
import com.medizine.network.RetryOperator;
import com.medizine.network.RxNetwork;
import com.medizine.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EditSlotActivity extends BaseActivity {

    private static final String TAG = EditSlotActivity.class.getSimpleName();

    @BindView(R.id.etStartTime)
    EditText etStartTime;
    @BindView(R.id.etEndTime)
    EditText etEndTime;

    private String mDoctorId;
    private boolean isStartTime;

    public static void launchEditSlotActivityForResult(@NonNull Context context, @NonNull String doctorId, @NonNull int requestCode) {
        Intent intent = new Intent(context, EditSlotActivity.class);
        intent.putExtra(Constants.DOCTOR_ID, doctorId);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_slot);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.add_slot));
        }

        if (getIntent() != null && getIntent().hasExtra(Constants.DOCTOR_ID)) {
            mDoctorId = getIntent().getStringExtra(Constants.DOCTOR_ID);
        }

        etStartTime.setOnClickListener(view -> {
            isStartTime = true;
            showTimePickerDialog(etStartTime.getText().toString());
        });
        etEndTime.setOnClickListener(view -> {
            if (etStartTime.getText().toString().isEmpty()) {
                setError(etStartTime, R.string.error_invalid_time);
                return;
            }
            isStartTime = false;
            showTimePickerDialog(etStartTime.getText().toString());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.done:
                if (validate()) {
                    addSlot();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addSlot() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String formattedDate = Utils.getFormattedDate(calendar.getTime());
        String isoStartTimeString = Utils.generateEventStartIsoDate(formattedDate, etStartTime.getText().toString());
        String isoEndDateString = Utils.generateEventStartIsoDate(formattedDate, etEndTime.getText().toString());

        Slot slot = new Slot();
        slot.setStartTime(isoStartTimeString);
        slot.setEndTime(isoEndDateString);
        slot.setDoctorId(mDoctorId);

        setProgressDialogMessage(getString(R.string.saving));
        showProgressBar();

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().createSlot(slot);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
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
        boolean isValid = true;
        String startTime = etStartTime.getText().toString();
        String endTime = etEndTime.getText().toString();
        if (startTime.isEmpty()) {
            isValid = setError(etStartTime, R.string.error_invalid_time);
        }
        if (endTime.isEmpty()) {
            isValid = setError(etEndTime, R.string.error_invalid_time);
        }
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa");
            try {
                Date date1 = formatter.parse(startTime);
                Date date2 = formatter.parse(endTime);
                long timeDiff = date2.getTime() - date1.getTime();
                long fifteenMilliSecs = 15 * 60 * 1000;
                long thirtyMilliSecs = 30 * 60 * 1000;
                if (!(timeDiff >= fifteenMilliSecs && timeDiff <= thirtyMilliSecs)) {
                    showToast(getString(R.string.invalid_slot_time_range));
                    isValid = false;
                }
            } catch (ParseException e) {
                showToast(getString(R.string.invalid_slot_time_range));
                isValid = false;
            }
        }
        return isValid;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
        String convertedTime = Utils.convertTimeToAptFormat(selectedHour + "." + selectedMinute, true);
        if (isStartTime) {
            etStartTime.setText(convertedTime);
        } else {
            etEndTime.setText(convertedTime);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}