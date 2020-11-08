package com.medizine.activity;

import android.util.Log;

import us.zoom.sdk.MeetingActivity;

public class ZoomMeetingActivity extends MeetingActivity {
    public static final String TAG = ZoomMeetingActivity.class.getSimpleName();

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        onClickLeave();
    }
}
