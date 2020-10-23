package com.medizine.widgets;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jainam.android.R;
import com.jainam.android.activity.StartLiveStreamActivity;
import com.jainam.android.event.Event;
import com.jainam.android.exceptions.NetworkUnavailableException;
import com.jainam.android.model.ZoomMeeting;
import com.jainam.android.model.enums.Duration;
import com.jainam.android.model.enums.ZoomMeetingStatus;
import com.jainam.android.network.JainamOperators;
import com.jainam.android.network.NetworkService;
import com.jainam.android.network.RxNetwork;
import com.jainam.android.utils.RxBus;
import com.jainam.android.utils.Utils;
import com.jainam.android.utils.ZoomUtils;
import com.jainam.android.zoom.InitAuthSDKHelper;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class ZoomLiveWidget extends ConstraintLayout implements ZoomSDKInitializeListener {

    public static final String TAG = ZoomLiveWidget.class.getSimpleName();

    @BindView(R.id.tvMeetingTitle)
    TextView tvMeetingTitle;
    @BindView(R.id.tvInMeetingUserCount)
    TextView tvInMeetingUserCount;
    @BindView(R.id.tvMeetingStartedAt)
    TextView tvMeetingStartedAt;
    @BindView(R.id.tvMeetingEndDuration)
    TextView tvMeetingEndDuration;
    @BindView(R.id.btnJoinMeeting)
    Button btnJoinMeeting;

    private Context mContext;

    private String moduleId;
    private String moduleType;

    private ZoomMeeting mZoomMeeting;

    public ZoomLiveWidget(@NonNull Context context) {
        this(context, null);
    }

    public ZoomLiveWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomLiveWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_zoom_live, this, true);
        ButterKnife.bind(this, view);
        mContext = context;
    }

    public void initZoomLiveWidget(String moduleType, String moduleId) {
        this.moduleId = moduleId;
        this.moduleType = moduleType;

        Disposable disposable = RxBus.getInstance().toObservable().observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Event.ZoomMeetingEvent) {
                        Event.ZoomMeetingEvent zoomMeetingEvent = ((Event.ZoomMeetingEvent) o);

                        String id = zoomMeetingEvent.getModuleId();
                        String type = zoomMeetingEvent.getModuleType().toUpperCase();

                        if (Utils.isNotEmpty(id) && Utils.isNotEmpty(type) && id.equals(this.moduleId) && type.equals(this.moduleType)) {
                            refreshUI();
                        }
                    }
                }, throwable -> Utils.logException(TAG, throwable));

        checkLiveZoomMeeting();
    }

    public void refreshUI() {
        checkLiveZoomMeeting();
    }

    //TODO: Implement for other modules, currently only implemented for `TEMPLE` module.
    private void checkLiveZoomMeeting() {
        if (moduleId == null || mContext == null) {
            return;
        }

        Disposable disposable = RxNetwork.observeNetworkConnectivity(mContext)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getLiveMeetingByModuleIdAndType(moduleType, moduleId);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(JainamOperators::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> refreshZoomMeetingUI(response.getData()), throwable -> {
                    if (throwable instanceof NetworkUnavailableException) {
                        Toast.makeText(mContext, mContext.getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.logException(TAG, throwable);
                    }
                });
    }

    private void refreshZoomMeetingUI(@Nullable ZoomMeeting zoomMeeting) {
        this.mZoomMeeting = zoomMeeting;

        if (zoomMeeting == null || !ZoomMeetingStatus.LIVE.name().equals(zoomMeeting.getMeetingStatus())) {
            this.setVisibility(GONE);
            return;
        }

        if (ZoomSDK.getInstance().isInitialized()) {
            setupUI();
        } else {
            InitAuthSDKHelper.getInstance().initSDK(mContext, this);
        }
    }

    private void setupUI() {
        if (mZoomMeeting == null || mContext == null) {
            return;
        }

        String title = getMetaData(mContext.getString(R.string.title), mZoomMeeting.getTitleAsString());
        String liveUserCount = getContext().getString(R.string.watching_now, getUserCount(mZoomMeeting.getMeetingUserCount()));
        String meetingDuration = getMetaData(mContext.getString(R.string.duration), getMeetingDuration(mZoomMeeting.getMeetingDuration()));
        String meetingStartTime = getMetaData(mContext.getString(R.string.started_at), getFormattedStartTime(mZoomMeeting.getMeetingStartTime()));

        initTextView(liveUserCount, tvInMeetingUserCount);
        initTextView(title, tvMeetingTitle);
        initTextView(meetingDuration, tvMeetingEndDuration);
        initTextView(meetingStartTime, tvMeetingStartedAt);

        btnJoinMeeting.setOnClickListener(view -> StartLiveStreamActivity.joinZoomMeeting(mContext, new Gson().toJson(mZoomMeeting, new TypeToken<ZoomMeeting>() {
        }.getType())));

        this.setVisibility(VISIBLE);
    }

    private String getMetaData(String arg1, String arg2) {
        if (mContext == null || Utils.isNullOrEmpty(arg1) || Utils.isNullOrEmpty(arg2)) {
            return null;
        }
        return mContext.getString(R.string.zoom_meeting_meta_data, arg1, arg2);
    }

    private String getUserCount(String usrCount) {
        if (Utils.isNullOrEmpty(usrCount)) {
            return null;
        }
        try {
            Integer.parseInt(usrCount);
        } catch (Exception e) {
            return null;
        }
        return usrCount;
    }

    private String getMeetingDuration(String duration) {
        if (Utils.isNullOrEmpty(duration)) {
            return null;
        } else {
            return Duration.valueOf(duration).getLocaleString(mContext);
        }
    }

    private String getFormattedStartTime(String startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(startTime));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return Utils.convertTimeToAptFormat(hour + "." + minute, true);
    }

    private void initTextView(String text, TextView view) {
        if (Utils.isNotEmpty(text)) {
            view.setText(text);
            view.setVisibility(VISIBLE);
        } else {
            view.setVisibility(GONE);
        }
    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        Log.d(TAG, "onZoomSDKInitializeResult, errorCode = " + errorCode + ", internalErrorCode = " + internalErrorCode);

        if (errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            Log.d(TAG, "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode = " + internalErrorCode);
        } else {
            Log.d(TAG, "Initialize Zoom SDK successfully.");
            ZoomUtils.customizeForegroundNotification(ZoomSDK.getInstance(), R.string.zoom_notification_text);
            setupUI();
        }
    }

    @Override
    public void onZoomAuthIdentityExpired() {
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

}

