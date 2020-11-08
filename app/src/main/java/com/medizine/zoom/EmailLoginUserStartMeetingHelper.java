package com.medizine.zoom;

import android.content.Context;

import us.zoom.sdk.InstantMeetingOptions;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.StartMeetingParams4NormalUser;
import us.zoom.sdk.ZoomSDK;

public class EmailLoginUserStartMeetingHelper {
    private final static String TAG = EmailLoginUserStartMeetingHelper.class.getSimpleName();

    private static EmailLoginUserStartMeetingHelper mEmailLoginUserStartMeetingHelper;

    private ZoomSDK mZoomSDK;

    private EmailLoginUserStartMeetingHelper() {
        mZoomSDK = ZoomSDK.getInstance();
    }

    public synchronized static EmailLoginUserStartMeetingHelper getInstance() {
        mEmailLoginUserStartMeetingHelper = new EmailLoginUserStartMeetingHelper();
        return mEmailLoginUserStartMeetingHelper;
    }

    public int startMeetingWithNumber(Context context, String meetingNo) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if (meetingService == null) {
            return ret;
        }

        StartMeetingParams4NormalUser params = new StartMeetingParams4NormalUser();
        params.meetingNo = meetingNo;
        return meetingService.startMeetingWithParams(context, params);
    }

    public int startMeetingWithVanityId(Context context, String vanityId) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if (meetingService == null) {
            return ret;
        }

        StartMeetingParams4NormalUser params = new StartMeetingParams4NormalUser();
        params.vanityID = vanityId;
        return meetingService.startMeetingWithParams(context, params);
    }

    public int startInstantMeeting(Context context) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if (meetingService == null) {
            return ret;
        }

        InstantMeetingOptions opts = new InstantMeetingOptions();

        opts.no_share = true;
        opts.no_driving_mode = true;
        opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID + MeetingViewsOptions.NO_TEXT_PASSWORD;

        return meetingService.startInstantMeeting(context, opts);
    }
}

