package com.medizine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.medizine.Constants;
import com.medizine.R;
import com.medizine.StateLayoutViewMode;
import com.medizine.ThrottleClick;
import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.model.Response;
import com.medizine.model.ZoomMeeting;
import com.medizine.model.entity.Appointment;
import com.medizine.model.entity.Doctor;
import com.medizine.model.entity.Slot;
import com.medizine.model.entity.User;
import com.medizine.model.enums.StatusType;
import com.medizine.model.enums.ZoomMeetingStatus;
import com.medizine.network.NetworkService;
import com.medizine.network.RetryOperator;
import com.medizine.network.RxNetwork;
import com.medizine.utils.Utils;
import com.medizine.utils.ZoomUtils;
import com.medizine.widgets.StateLayout;
import com.medizine.zoom.InitAuthSDKHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitializeListener;

import static com.medizine.Constants.REQUEST_JOIN_ZOOM_MEETING;

public class AppointmentDetailActivity extends BaseActivity implements ZoomSDKInitializeListener {
    public static final String TAG = AppointmentDetailActivity.class.getSimpleName();

    @BindView(R.id.layoutAppointment)
    LinearLayout layoutAppointment;
    @BindView(R.id.tvAppointmentDate)
    TextView tvAppointmentDate;
    @BindView(R.id.tvAppointmentStatus)
    TextView tvAppointmentStatus;

    @BindView(R.id.layoutSlot)
    ConstraintLayout layoutSlot;
    @BindView(R.id.tvTiming)
    TextView tvTiming;

    @BindView(R.id.doctorUI)
    ConstraintLayout doctorUI;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.tvAddress)
    TextView tvAddress;

    @BindView(R.id.userUI)
    ConstraintLayout userUI;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvUserPhone)
    TextView tvUserPhone;
    @BindView(R.id.tvUserAddress)
    TextView tvUserAddress;

    @BindView(R.id.tvMeetingStatusInfo)
    TextView tvMeetingStatusInfo;
    @BindView(R.id.btnJoin)
    TextView btnJoin;
    @BindView(R.id.btnStart)
    TextView btnStart;

    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    private String mAppointmentId;
    private Appointment mAppointment;
    private Doctor mDoctor;
    private User mUser;
    private List<Slot> mSlots;
    private ZoomMeeting mZoomMeeting;

    public static void launchAppointmentDetailActivity(Context context, String appointmentId) {
        Intent intent = new Intent(context, AppointmentDetailActivity.class);
        intent.putExtra(Constants.APPOINTMENT_ID, appointmentId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.appointment_details));
        }
        if (getIntent() != null && getIntent().hasExtra(Constants.APPOINTMENT_ID)) {
            mAppointmentId = getIntent().getStringExtra(Constants.APPOINTMENT_ID);
        }
        stateLayout.setMode(StateLayoutViewMode.PROGRESS);
        btnJoin.setOnClickListener(new ThrottleClick() {
            @Override
            public void onClick() {
                if (mZoomMeeting != null) {
                    StartLiveStreamActivity.joinZoomMeeting(AppointmentDetailActivity.this, ZoomUtils.zoomMeetingToJson(mZoomMeeting));
                }
            }
        });
        btnStart.setOnClickListener(new ThrottleClick() {
            @Override
            public void onClick() {
                if (mAppointmentId != null) {
                    StartLiveStreamActivity.startZoomMeeting(AppointmentDetailActivity.this, mAppointmentId);
                }
            }
        });
        mSlots = new ArrayList<>();
        //Init Zoom Sdk
        if (!ZoomSDK.getInstance().isInitialized()) {
            InitAuthSDKHelper.getInstance().initSDK(this, this);
            return;
        }
        fetchAppointmentDetails(mAppointmentId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getIntent() != null && getIntent().hasExtra(Constants.ZOOM_MEETING_ENDED)) {
            if (getIntent().getBooleanExtra(Constants.ZOOM_MEETING_ENDED, false)) {
                fetchZoomMeetingStatus();
            }
        }
    }

    private void renderUI() {
        if (mAppointment != null && (mDoctor != null || mUser != null) && !Utils.isListEmpty(mSlots)) {
            Slot slot = null;
            for (Slot s : mSlots) {
                if (s.getId().equals(mAppointment.getSlotId())) {
                    slot = s;
                    break;
                }
            }
            initAppointmentUi(mAppointment);
            initSlotUI(slot);
            initDoctorUI(mDoctor);
            initUserUI(mUser);
            layoutAppointment.setVisibility(View.VISIBLE);
            layoutAppointment.setVisibility(View.VISIBLE);
            stateLayout.setMode(StateLayoutViewMode.CONTENT);
            fetchZoomMeetingStatus();
        } else {
            finish();
        }
    }

    private void fetchZoomMeetingStatus() {
        Disposable disposable = Observable.interval(1, TimeUnit.SECONDS)
                .flatMap((Function<Long, ObservableSource<?>>) aLong -> Observable.just(new Response<>()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> fetchZoomMeetingFromNetwork());
        networkDisposable.add(disposable);
    }

    private void fetchZoomMeetingFromNetwork() {
        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getZoomMeetingByAppointmentId(mAppointmentId);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<ZoomMeeting>>() {
                               @Override
                               public void accept(Response<ZoomMeeting> response) throws Exception {
                                   AppointmentDetailActivity.this.refreshJoinMeetingStatus(response.getData());
                               }
                           }, throwable -> {
                            stateLayout.setMode(StateLayoutViewMode.ERROR);
                            if (throwable instanceof NetworkUnavailableException) {
                                Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.logException(TAG, throwable);
                            }
                        }
                );
        networkDisposable.add(disposable);
    }

    private void refreshJoinMeetingStatus(ZoomMeeting zoomMeeting) {
        if (zoomMeeting != null) {
            mZoomMeeting = zoomMeeting;
            tvMeetingStatusInfo.setVisibility(View.GONE);
            if (StatusType.ACTIVE.name().equals(mAppointment.getStatus())) {
                tvMeetingStatusInfo.setVisibility(View.GONE);
                if (Utils.isUserTypeNormal()) {
                    btnStart.setVisibility(View.GONE);
                    btnJoin.setVisibility(View.VISIBLE);
                    if (ZoomMeetingStatus.LIVE.name().equalsIgnoreCase(zoomMeeting.getMeetingStatus())) {
                        btnJoin.setBackgroundColor(getResources().getColor(R.color.primary));
                        btnJoin.setText(getString(R.string.join));
                        btnJoin.setEnabled(true);
                    } else {
                        btnJoin.setBackgroundColor(getResources().getColor(R.color.grey400));
                        btnJoin.setText(getString(R.string.zoom_status_completed));
                        btnJoin.setEnabled(false);
                    }
                } else if (Utils.isUserTypeDoctor()) {
                    btnStart.setVisibility(View.VISIBLE);
                    btnJoin.setVisibility(View.GONE);
                    if (ZoomMeetingStatus.LIVE.name().equalsIgnoreCase(zoomMeeting.getMeetingStatus())) {
                        btnStart.setBackgroundColor(getResources().getColor(R.color.primary));
                        btnStart.setText(getString(R.string.join));
                        btnStart.setEnabled(true);
                    } else if (ZoomMeetingStatus.COMPLETED.name().equalsIgnoreCase(zoomMeeting.getMeetingStatus())) {
                        btnStart.setBackgroundColor(getResources().getColor(R.color.grey400));
                        btnStart.setText(getString(R.string.zoom_status_completed));
                        btnStart.setEnabled(false);
                    } else {
                        btnStart.setBackgroundColor(getResources().getColor(R.color.primary));
                        btnStart.setText(getString(R.string.start));
                        btnStart.setEnabled(true);
                    }
                }
            } else {
                btnJoin.setBackgroundColor(getResources().getColor(R.color.grey400));
                btnJoin.setText(getString(R.string.zoom_status_completed));
                btnJoin.setEnabled(false);
                btnStart.setVisibility(View.GONE);
                tvMeetingStatusInfo.setVisibility(View.GONE);
            }
        } else {
            if (Utils.isUserTypeNormal()) {
                btnJoin.setVisibility(View.GONE);
                btnStart.setVisibility(View.GONE);
                tvMeetingStatusInfo.setVisibility(View.VISIBLE);
            } else if (Utils.isUserTypeDoctor()) {
                btnStart.setVisibility(View.VISIBLE);
                btnJoin.setVisibility(View.GONE);
                tvMeetingStatusInfo.setVisibility(View.GONE);
            }
        }
        if (Utils.isUserTypeDoctor()) {
            tvMeetingStatusInfo.setVisibility(View.GONE);
        }
        stateLayout.setMode(StateLayoutViewMode.CONTENT);
    }

    private void initUserUI(User mUser) {
        if (mUser != null) {
            tvUserName.setText(Utils.capitalizeWords(mUser.getName()));
            tvUserPhone.setText(mUser.getCountryCode() + mUser.getPhoneNumber());
            tvUserAddress.setText(mUser.getEmailAddress());
            userUI.setVisibility(View.VISIBLE);
        } else {
            userUI.setVisibility(View.GONE);
        }
    }

    private void initDoctorUI(Doctor mDoctor) {
        if (mDoctor != null) {
            tvName.setText(Utils.capitalizeWords(mDoctor.getName()));
            tvPhone.setText(mDoctor.getCountryCode() + mDoctor.getPhoneNumber());
            tvAddress.setText(mDoctor.getEmailAddress());
            doctorUI.setVisibility(View.VISIBLE);
        } else {
            doctorUI.setVisibility(View.GONE);
        }
    }

    private void initSlotUI(Slot slot) {
        if (slot != null) {
            tvTiming.setText(getString(R.string.slot_timing, slot.getFormattedStartTime(), slot.getFormattedEndTime()));
            if (Utils.isUserTypeNormal()) {
                tvMeetingStatusInfo.setText(getString(R.string.meeting_status_info, slot.getFormattedStartTime(), slot.getFormattedEndTime()));
                tvMeetingStatusInfo.setVisibility(View.VISIBLE);
            }
            layoutSlot.setVisibility(View.VISIBLE);
        } else {
            tvMeetingStatusInfo.setVisibility(View.GONE);
            layoutSlot.setVisibility(View.GONE);
        }
    }

    private void initAppointmentUi(Appointment appointment) {
        tvAppointmentDate.setText(appointment.getFormattedAppointmentDate());
        tvAppointmentStatus.setText(appointment.getStatus());
    }

    private void fetchAppointmentDetails(String appointmentId) {
        stateLayout.setMode(StateLayoutViewMode.PROGRESS);
        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getAppointmentById(appointmentId);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    mAppointment = response.getData();
                    fetchSlots(mAppointment);
                }, throwable -> {
                    stateLayout.setMode(StateLayoutViewMode.ERROR);
                    if (throwable instanceof NetworkUnavailableException) {
                        Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.logException(TAG, throwable);
                    }
                });
    }


    private void fetchUserDetails(Appointment appointment) {
        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getUserById(appointment.getUserId());
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            mUser = response.getData();
                            renderUI();
                        }, throwable -> {
                            stateLayout.setMode(StateLayoutViewMode.ERROR);
                            if (throwable instanceof NetworkUnavailableException) {
                                Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.logException(TAG, throwable);
                            }
                        }
                );
    }

    private void fetchDoctorDetails(Appointment appointment) {
        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getDoctorById(appointment.getDoctorId());
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            mDoctor = response.getData();
                            renderUI();
                        }, throwable -> {
                            stateLayout.setMode(StateLayoutViewMode.ERROR);
                            if (throwable instanceof NetworkUnavailableException) {
                                Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.logException(TAG, throwable);
                            }
                        }
                );
    }

    private void fetchSlots(Appointment appointment) {
        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getAllSlotsByDoctorId(appointment.getDoctorId());
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            mSlots = response.getData();
                            if (Utils.isUserTypeDoctor()) {
                                fetchUserDetails(appointment);
                            } else if (Utils.isUserTypeNormal()) {
                                fetchDoctorDetails(appointment);
                            }
                        }, throwable -> {
                            stateLayout.setMode(StateLayoutViewMode.ERROR);
                            if (throwable instanceof NetworkUnavailableException) {
                                Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.logException(TAG, throwable);
                            }
                        }
                );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_JOIN_ZOOM_MEETING) {
            fetchZoomMeetingFromNetwork();
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        Log.d(TAG, "onZoomSDKInitializeResult, errorCode = " + errorCode + ", internalErrorCode = " + internalErrorCode);

        if (errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            Log.d(TAG, "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode = " + internalErrorCode);
        } else {
            Log.d(TAG, "Initialize Zoom SDK successfully.");
            ZoomUtils.customizeForegroundNotification(ZoomSDK.getInstance(), R.string.zoom_notification_text);
            if (!this.isDestroyed())
                fetchAppointmentDetails(mAppointmentId);
        }
    }

    @Override
    public void onZoomAuthIdentityExpired() {
    }

}