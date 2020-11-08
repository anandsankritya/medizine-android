package com.medizine.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.medizine.Constants;
import com.medizine.R;
import com.medizine.ThrottleClick;
import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.model.ZoomMeeting;
import com.medizine.model.ZoomMeetingRequest;
import com.medizine.model.enums.ZoomMeetingStatus;
import com.medizine.network.NetworkService;
import com.medizine.network.RxNetwork;
import com.medizine.utils.Utils;
import com.medizine.utils.ZoomUtils;
import com.medizine.zoom.EmailLoginUserStartMeetingHelper;
import com.medizine.zoom.InitAuthSDKHelper;
import com.medizine.zoom.UserLoginCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import us.zoom.sdk.InMeetingAudioController;
import us.zoom.sdk.InMeetingChatMessage;
import us.zoom.sdk.InMeetingEventHandler;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.InMeetingServiceListener;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingEndReason;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class StartLiveStreamActivity extends BaseActivity implements
        ZoomSDKInitializeListener, ZoomSDKAuthenticationListener, UserLoginCallback.JainamZoomAuthenticationListener, MeetingServiceListener, InMeetingServiceListener {
    private static final String TAG = StartLiveStreamActivity.class.getSimpleName();
    private final static String IS_MEETING_ALREADY_EXISTS = "isZoomMeetingAlreadyExists";

    @BindView(R.id.btnStartMeeting)
    Button btnStartMeeting;
    @BindView(R.id.layoutStartMeeting)
    LinearLayout layoutStartMeeting;

    @Nullable
    private String mAppointmentId;
    @Nullable
    private ZoomMeeting zoomMeeting;
    @Nullable
    private String zoomMeetingObjectId;
    private boolean isJoinMeetingOnly = false;
    private boolean isZoomMeetingAlreadyExists = false;
    private int liveUserCount = 0;
    private ZoomSDK mZoomSDK;

    public static void joinZoomMeeting(Context context, String zoomMeetObjJson) {
        Intent intent = new Intent(context, StartLiveStreamActivity.class);
        intent.putExtra(Constants.ZOOM_MEET_OBJ_JSON, zoomMeetObjJson);
        ((Activity) context).startActivityForResult(intent, Constants.REQUEST_JOIN_ZOOM_MEETING);
    }

    public static void startZoomMeeting(Context context, String appointmentId) {
        Intent intent = new Intent(context, StartLiveStreamActivity.class);
        intent.putExtra(Constants.APPOINTMENT_ID, appointmentId);
        context.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.APPOINTMENT_ID, mAppointmentId);
        outState.putString(Constants.OBJECT_ID, zoomMeetingObjectId);
        outState.putString(Constants.ZOOM_MEET_OBJ_JSON, ZoomUtils.zoomMeetingToJson(zoomMeeting));
        outState.putBoolean(Constants.IS_MEETING_JOIN_ONLY, isJoinMeetingOnly);
        outState.putBoolean(IS_MEETING_ALREADY_EXISTS, isZoomMeetingAlreadyExists);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.zoom);
        }
        setContentView(R.layout.activity_start_live_stream);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (savedInstanceState != null) {
            mAppointmentId = savedInstanceState.getString(Constants.APPOINTMENT_ID);
            zoomMeetingObjectId = savedInstanceState.getString(Constants.OBJECT_ID);
            zoomMeeting = ZoomUtils.zoomMeetingFromJson(savedInstanceState.getString(Constants.ZOOM_MEET_OBJ_JSON));
            isJoinMeetingOnly = savedInstanceState.getBoolean(Constants.IS_MEETING_JOIN_ONLY, false);
            isZoomMeetingAlreadyExists = savedInstanceState.getBoolean(IS_MEETING_ALREADY_EXISTS, false);

        } else if (intent != null) {
            if (intent.hasExtra(Constants.ZOOM_MEET_OBJ_JSON)) {
                isJoinMeetingOnly = true;
                zoomMeeting = ZoomUtils.zoomMeetingFromJson(intent.getStringExtra(Constants.ZOOM_MEET_OBJ_JSON));
                if (zoomMeeting != null) {
                    mAppointmentId = zoomMeeting.getAppointmentId();
                }
            } else if (intent.hasExtra(Constants.APPOINTMENT_ID)) {
                isJoinMeetingOnly = false;
                mAppointmentId = intent.getStringExtra(Constants.APPOINTMENT_ID);
            }
        }
        initActivity();
    }

    private void initActivity() {
        invalidateOptionsMenu();
        mZoomSDK = ZoomSDK.getInstance();
        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();
        if (mZoomSDK.isInitialized()) {
            setupUI();
        } else {
            InitAuthSDKHelper.getInstance().initSDK(this, this);
        }
    }

    private void setupUI() {
        registerListener();
        hideProgressBar();
        invalidateOptionsMenu();
        if (isJoinMeetingOnly) {
            joinZoomMeeting();
            return;
        }
        if (mZoomSDK.isLoggedIn()) {
            initStartMeetingUI();
        } else {
            ZoomLoginActivity.launchZoomLoginActivityForResult(this);
        }
    }

    private void initStartMeetingUI() {
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        startMeeting();
        btnStartMeeting.setOnClickListener(new ThrottleClick() {
            @Override
            public void onClick() {
                startInstantMeeting();
            }
        });
    }

    private void navigateToModuleDetailScreen() {
        Intent intent = new Intent(StartLiveStreamActivity.this, AppointmentDetailActivity.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.APPOINTMENT_ID, mAppointmentId);
        intent.putExtra(Constants.ZOOM_MEETING_ENDED, true);
        startActivity(intent);
        finish();
        hideLeavingProgress();
    }

    private void startMeeting() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if (!zoomSDK.isInitialized()) {
            log("ZoomSDK has not been initialized successfully");
            return;
        }
        final MeetingService meetingService = zoomSDK.getMeetingService();
        if (meetingService.getMeetingStatus() != MeetingStatus.MEETING_STATUS_IDLE) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.zoom_pending_meeting_dialog)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        isZoomMeetingAlreadyExists = true;
                        meetingService.leaveCurrentMeeting(false);
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        }
    }

    private void joinZoomMeeting() {
        if (zoomMeeting == null) {
            return;
        }
        if (zoomMeeting.getMeetingNumber() != null) {
            long lMeetingNo;
            try {
                lMeetingNo = Long.parseLong(zoomMeeting.getMeetingNumber());
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.invalid_meeting_number, Toast.LENGTH_LONG).show();
                navigateToModuleDetailScreen();
                return;
            }
            MeetingService meetingService = mZoomSDK.getMeetingService();
            if (meetingService.getCurrentRtcMeetingNumber() == lMeetingNo) {
                meetingService.returnToMeeting(this);
                return;
            }
        }
        JoinMeetingOptions options = new JoinMeetingOptions();
        options.no_driving_mode = true;
        if (ZoomUtils.isHost(zoomMeeting)) {
            options.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID + MeetingViewsOptions.NO_TEXT_PASSWORD;
        } else {
            options.no_share = true;
            options.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID + MeetingViewsOptions.NO_TEXT_PASSWORD + MeetingViewsOptions.NO_BUTTON_SHARE;
        }
        JoinMeetingParams joinMeetingParams = new JoinMeetingParams();
        joinMeetingParams.displayName = Utils.getHostUserName();
        joinMeetingParams.meetingNo = zoomMeeting.getMeetingNumber();
        joinMeetingParams.password = zoomMeeting.getMeetingPassword();
        ZoomSDK.getInstance().getMeetingService().joinMeetingWithParams(this, joinMeetingParams, options);
    }

    private void startInstantMeeting() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if (!zoomSDK.isInitialized()) {
            log("ZoomSDK has not been initialized");
            return;
        }
        int result = EmailLoginUserStartMeetingHelper.getInstance().startInstantMeeting(this);
        log("startInstantMeeting, result = " + result);
    }

    private void endCurrentMeeting() {
        showLeavingProgress();
        if (zoomMeeting == null || Utils.isNullOrEmpty(zoomMeeting.getId())) {
            hideLeavingProgress();
            navigateToModuleDetailScreen();
            return;
        }
        ZoomMeetingRequest requestMeetingEnd = new ZoomMeetingRequest();
        requestMeetingEnd.setMeetingStatus(ZoomMeetingStatus.COMPLETED.name());
        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().patchZoomMeetingById(zoomMeeting.getId(), requestMeetingEnd);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(objectResponse -> {
                            ZoomMeeting meeting = objectResponse.getData();
                            if (meeting != null) {
                                navigateToModuleDetailScreen();
                                log(Utils.getPrettyPrintedJson(objectResponse.getData()));
                            } else {
                                hideLeavingProgress();
                            }
                        }, throwable -> {
                            ZoomSDK.getInstance().getMeetingService().leaveCurrentMeeting(true);
                            if (throwable instanceof NetworkUnavailableException) {
                                Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, getString(R.string.oops_something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                            hideLeavingProgress();
                            Utils.logException(TAG, throwable);
                        }
                );
    }

    private void createNewMeetingIfNotExists() {
        ZoomMeetingRequest request = new ZoomMeetingRequest();
        InMeetingService inMeetingService = mZoomSDK.getInMeetingService();
        request.setAppointmentId(mAppointmentId);
        request.setHostId(Utils.getDoctorID());
        request.setMeetingNumber(String.valueOf(inMeetingService.getCurrentMeetingNumber()));
        request.setMeetingPassword(inMeetingService.getMeetingPassword());
        request.setMeetingStatus(ZoomMeetingStatus.LIVE.name());
        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().createZoomMeetingIfNotExists(request);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(objectResponse -> {
                            if (objectResponse.getData() != null) {
                                zoomMeetingObjectId = String.valueOf(objectResponse.getData());
                                log("createZoomMeetingIfNotExists, Zoom Meeting Object Id : " + zoomMeetingObjectId);
                            }
                        }, throwable -> {
                            ZoomSDK.getInstance().getMeetingService().leaveCurrentMeeting(true);
                            if (throwable instanceof NetworkUnavailableException) {
                                Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, getString(R.string.oops_something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                            Utils.logException(TAG, throwable);
                        }
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_ZOOM_LOGIN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.signin_success, Toast.LENGTH_SHORT).show();
                initActivity();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        log("onZoomSDKInitializeResult, errorCode = " + errorCode + ", internalErrorCode = " + internalErrorCode);
        if (errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            log("Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode);
            finish();
        } else {
            log("Initialize Zoom SDK successfully.");
            ZoomUtils.customizeForegroundNotification(ZoomSDK.getInstance(), R.string.zoom_notification_text);
            if (mZoomSDK.tryAutoLoginZoom() == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
                UserLoginCallback.getInstance().addListener(this);
            } else {
                setupUI();
            }
        }
    }

    private void handleOnMeetingLeaveCompleted(long reason) {
        showLeavingProgress();
        log("handleOnMeetingLeaveCompleted : " + reason);
        switch ((int) reason) {
            case MeetingEndReason.END_BY_SELF:
                if (isJoinMeetingOnly) {
                    boolean isHost = ZoomUtils.isHost(zoomMeeting);

                    if (isHost && liveUserCount == 1) {
                        endCurrentMeeting();
                        break;
                    } else if (!isHost && liveUserCount == 1) {
                        endCurrentMeeting();
                        break;
                    }
                } else {
                    if (liveUserCount == 1) {
                        endZoomMeeting(zoomMeetingObjectId);
                        break;
                    }
                }
                navigateToModuleDetailScreen();
                break;
            case MeetingEndReason.KICK_BY_HOST:
            case MeetingEndReason.END_FOR_JBHTIMEOUT:
                navigateToModuleDetailScreen();
                break;
            case MeetingEndReason.END_BY_HOST:
            case MeetingEndReason.END_FOR_FREEMEET_TIMEOUT:
            case MeetingEndReason.END_FOR_NOATEENDEE:
            case MeetingEndReason.END_BY_HOST_START_ANOTHERMEETING:
                if (isJoinMeetingOnly) {
                    endCurrentMeeting();
                } else {
                    endZoomMeeting(zoomMeetingObjectId);
                }
                break;
        }
    }

    private void endZoomMeeting(@Nullable String id) {
        showLeavingProgress();
        if (Utils.isNullOrEmpty(id)) {
            hideLeavingProgress();
            return;
        }
        ZoomMeetingRequest request = new ZoomMeetingRequest();
        request.setMeetingStatus(ZoomMeetingStatus.COMPLETED.name());
        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().patchZoomMeetingById(id, request);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(objectResponse -> {
                            ZoomMeeting meeting = objectResponse.getData();
                            if (meeting != null) {
                                zoomMeeting = meeting;
                                navigateToModuleDetailScreen();
                                log(Utils.getPrettyPrintedJson(objectResponse.getData()));
                            } else {
                                hideLeavingProgress();
                            }
                        }, throwable -> {
                            ZoomSDK.getInstance().getMeetingService().leaveCurrentMeeting(true);
                            if (throwable instanceof NetworkUnavailableException) {
                                Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, getString(R.string.oops_something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                            hideLeavingProgress();
                            Utils.logException(TAG, throwable);
                        }
                );
    }

    private void updateInMeetingUserCount() {
        InMeetingService inMeetingService = mZoomSDK.getInMeetingService();
        if (inMeetingService == null) {
            return;
        }
        liveUserCount = inMeetingService.getInMeetingUserCount();
        log("updateInMeetingUserCount " + liveUserCount);
    }

    private void logoutZoom() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if (!zoomSDK.logoutZoom()) {
            log("ZoomSDK has not been initialized successfully");
        }
    }

    private void showLoginView() {
        if (ZoomSDK.getInstance().isInitialized()) {
            ZoomLoginActivity.launchZoomLoginActivity(StartLiveStreamActivity.this);
        }
        finish();
    }

    private void registerListener() {
        mZoomSDK.addAuthenticationListener(this);
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if (meetingService != null) {
            meetingService.addListener(this);
        }
        InMeetingService inMeetingService = mZoomSDK.getInMeetingService();
        if (inMeetingService != null) {
            inMeetingService.addListener(this);
        }
    }

    private void removeListener() {
        mZoomSDK.removeAuthenticationListener(this);
        if (mZoomSDK.isInitialized()) {
            MeetingService meetingService = mZoomSDK.getMeetingService();
            if (meetingService != null)
                meetingService.removeListener(this);
            InMeetingService inMeetingService = mZoomSDK.getInMeetingService();
            if (inMeetingService != null)
                inMeetingService.removeListener(this);
        }
        InitAuthSDKHelper.getInstance().reset();
    }

    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
        log("onMeetingStatusChanged, meetingStatus = " + meetingStatus + ", errorCode = " + errorCode + ", internalErrorCode=" + internalErrorCode);

        if (meetingStatus == MeetingStatus.MEETING_STATUS_FAILED) {
            if (errorCode == MeetingError.MEETING_ERROR_MEETING_NOT_EXIST)
                endCurrentMeeting();
            else
                navigateToModuleDetailScreen();
        } else if (meetingStatus == MeetingStatus.MEETING_STATUS_IDLE && isZoomMeetingAlreadyExists) {
            isZoomMeetingAlreadyExists = false;
            startMeeting();
        }

        if (meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING && !isJoinMeetingOnly) {
            createNewMeetingIfNotExists();

            InMeetingService inMeetingService = mZoomSDK.getInMeetingService();
            if (inMeetingService != null) {
                long myUserID = inMeetingService.getMyUserID();
                inMeetingService.changeName(Utils.getHostUserName(), myUserID);
                inMeetingService.getInMeetingWaitingRoomController().enableWaitingRoomOnEntry(false);
            }
        }

    }

    @Override
    public void onZoomSDKLoginResult(long result) {
        if ((int) result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            setupUI();
        }
    }

    @Override
    public void onZoomSDKLogoutResult(long result) {
        if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
            showLoginView();
        } else {
            log("Logout failed result code = " + result);
        }
    }

    @Override
    public void onZoomIdentityExpired() {
        if (mZoomSDK.isLoggedIn()) {
            mZoomSDK.logoutZoom();
        }
    }

    @Override
    public void onZoomAuthIdentityExpired() {
        log("onZoomAuthIdentityExpired");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_zoom_live, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuUnlink = menu.findItem(R.id.menuUnlink);
        if (layoutStartMeeting.getVisibility() == View.VISIBLE) {
            menuUnlink.setVisible(true);
        } else {
            menuUnlink.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menuUnlink:
                logoutZoom();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListener();
    }

    @Override
    public void onMeetingLeaveComplete(long l) {
        handleOnMeetingLeaveCompleted(l);
    }

    @Override
    public void onMeetingUserJoin(List<Long> list) {
        updateInMeetingUserCount();
    }

    @Override
    public void onMeetingUserLeave(List<Long> list) {
        updateInMeetingUserCount();
    }

    @Override
    public void onMeetingNeedPasswordOrDisplayName(boolean b, boolean b1, InMeetingEventHandler inMeetingEventHandler) {
    }

    @Override
    public void onWebinarNeedRegister() {
    }

    @Override
    public void onJoinWebinarNeedUserNameAndEmail(InMeetingEventHandler inMeetingEventHandler) {
    }

    @Override
    public void onMeetingNeedColseOtherMeeting(InMeetingEventHandler inMeetingEventHandler) {
    }

    @Override
    public void onMeetingFail(int i, int i1) {
    }

    @Override
    public void onMeetingUserUpdated(long l) {
    }

    @Override
    public void onMeetingHostChanged(long l) {
    }

    @Override
    public void onMeetingCoHostChanged(long l) {
    }

    @Override
    public void onActiveVideoUserChanged(long l) {
    }

    @Override
    public void onActiveSpeakerVideoUserChanged(long l) {
    }

    @Override
    public void onSpotlightVideoChanged(boolean b) {
    }

    @Override
    public void onUserVideoStatusChanged(long l) {
    }

    @Override
    public void onUserNetworkQualityChanged(long l) {
    }

    @Override
    public void onMicrophoneStatusError(InMeetingAudioController.MobileRTCMicrophoneError mobileRTCMicrophoneError) {
    }

    @Override
    public void onUserAudioStatusChanged(long l) {
    }

    @Override
    public void onHostAskUnMute(long l) {
    }

    @Override
    public void onHostAskStartVideo(long l) {
    }

    @Override
    public void onUserAudioTypeChanged(long l) {
    }

    @Override
    public void onMyAudioSourceTypeChanged(int i) {
    }

    @Override
    public void onLowOrRaiseHandStatusChanged(long l, boolean b) {
    }

    @Override
    public void onMeetingSecureKeyNotification(byte[] bytes) {
    }

    @Override
    public void onChatMessageReceived(InMeetingChatMessage inMeetingChatMessage) {
    }

    @Override
    public void onSilentModeChanged(boolean b) {
    }

    @Override
    public void onFreeMeetingReminder(boolean b, boolean b1, boolean b2) {
    }

    @Override
    public void onMeetingActiveVideo(long l) {
    }

    @Override
    public void onSinkAttendeeChatPriviledgeChanged(int i) {
    }

    @Override
    public void onSinkAllowAttendeeChatNotification(int i) {
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }

    private void showLeavingProgress() {
        setProgressDialogMessage(getString(R.string.leaving));
        showProgressBar();
    }

    private void hideLeavingProgress() {
        hideProgressBar();
        setProgressDialogMessage(getString(R.string.loading));
    }

}