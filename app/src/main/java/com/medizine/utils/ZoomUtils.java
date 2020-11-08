package com.medizine.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medizine.Constants;
import com.medizine.R;
import com.medizine.activity.ZoomMeetingActivity;
import com.medizine.event.Event;
import com.medizine.model.ZoomMeeting;

import java.lang.reflect.Type;

import us.zoom.sdk.CustomizedNotificationData;
import us.zoom.sdk.InMeetingNotificationHandle;
import us.zoom.sdk.ZoomSDK;

public class ZoomUtils {
    private static final Gson gson = new Gson();
    private static final Type zoomMeetingType = new TypeToken<ZoomMeeting>() {
    }.getType();

    public static String zoomMeetingToJson(@Nullable ZoomMeeting zoomMeeting) {
        if (zoomMeeting == null) {
            return null;
        }
        return gson.toJson(zoomMeeting, zoomMeetingType);
    }

    public static ZoomMeeting zoomMeetingFromJson(@Nullable String zoomMeetingJson) {
        if (Utils.isNullOrEmpty(zoomMeetingJson)) {
            return null;
        }
        return gson.fromJson(zoomMeetingJson, zoomMeetingType);
    }

    public static boolean isHost(@Nullable ZoomMeeting zoomMeeting) {
        if (zoomMeeting == null) {
            Log.d("isHost", "zoomMeeting == null");
            return false;
        }
        if (Utils.isUserTypeNormal()) {
            return Utils.getUserID().equals(zoomMeeting.getHostId());
        } else if (Utils.isUserTypeDoctor()) {
            return Utils.getDoctorID().equals(zoomMeeting.getHostId());
        }
        return false;
    }

    public static void sendEventByDeepLink(@Nullable String deeplink) {
        if (Utils.isNullOrEmpty(deeplink)) {
            return;
        }
        String baseUrl = null;
        Uri deepLinkUri = Uri.parse(deeplink);
        if (deepLinkUri.toString().startsWith(Constants.WEBSITE_LINK)) {
            baseUrl = Constants.WEBSITE_LINK;
        } else if (deepLinkUri.toString().startsWith(Constants.CLICK_ACTION_LINK)) {
            baseUrl = Constants.CLICK_ACTION_LINK;
        }
        if (!Utils.isNullOrEmpty(baseUrl)) {
            String suffix = deepLinkUri.toString().substring(baseUrl.length());
            String[] splits = suffix.split("/");
            if (splits.length == 2) {
                sendEventByModuleIdAndType(splits[1], splits[0]);
            }
        }
    }

    public static void sendEventByModuleIdAndType(@Nullable String moduleId, @Nullable String moduleType) {
        if (Utils.isNotEmpty(moduleId) && Utils.isNotEmpty(moduleType)) {
            RxBus.getInstance().sendEvent(new Event.ZoomMeetingEvent(moduleId, moduleType));
        }
    }

    private static boolean isValidZoomUrl(@NonNull String url) {
        return url.startsWith("https://") && url.contains("zoom.us");
    }

    public static boolean isInvalidZoomMeetingNumber(@Nullable String zoomMeetingNumber) {
        return Utils.isNullOrEmpty(zoomMeetingNumber) || zoomMeetingNumber.length() < 9 || zoomMeetingNumber.length() > 11;
    }

    @Nullable
    public static String getMeetingNumber(@Nullable String zoomMeetingUrl) {
        if (zoomMeetingUrl == null || !isValidZoomUrl(zoomMeetingUrl)) {
            return null;
        }
        String meetingNumber;
        String password = Uri.parse(zoomMeetingUrl).getQueryParameter("pwd");
        if (password == null) {
            meetingNumber = zoomMeetingUrl.split("zoom.us/j/")[1];
        } else {
            meetingNumber = zoomMeetingUrl.split("\\?pwd=")[0].split("zoom.us/j/")[1];
        }
        if (meetingNumber != null && (meetingNumber.length() == 9 || meetingNumber.length() == 10 || meetingNumber.length() == 11)) {
            return meetingNumber;
        }
        return null;
    }

    @Nullable
    public static String getMeetingPassword(@Nullable String zoomMeetingUrl) {
        if (zoomMeetingUrl == null || !isValidZoomUrl(zoomMeetingUrl)) {
            return null;
        }
        return Uri.parse(zoomMeetingUrl).getQueryParameter("pwd");
    }


    public static void customizeForegroundNotification(ZoomSDK zoomSDK, int contentTextId) {
        CustomizedNotificationData customizedNotificationData = new CustomizedNotificationData();
        customizedNotificationData.setContentTitleId(R.string.app_name);
        customizedNotificationData.setContentTextId(contentTextId);
        customizedNotificationData.setSmallIconId(R.drawable.ic_notification_icon);
        customizedNotificationData.setLargeIconId(R.drawable.logo);
        customizedNotificationData.setSmallIconForLorLaterId(R.drawable.ic_notification_icon);

        InMeetingNotificationHandle inMeetingNotificationHandle = (context, intent) -> {
            intent = new Intent(context, ZoomMeetingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.setAction(InMeetingNotificationHandle.ACTION_RETURN_TO_CONF);
            context.startActivity(intent);
            return true;
        };

        if (zoomSDK.getMeetingSettingsHelper() != null) {
            //zoomSDK.getMeetingSettingsHelper().disableShowMeetingNotification(true);
            zoomSDK.getMeetingSettingsHelper().setAutoConnectVoIPWhenJoinMeeting(true);
            zoomSDK.getMeetingSettingsHelper().disableAutoShowSelectJoinAudioDlgWhenJoinMeeting(true);
            zoomSDK.getMeetingSettingsHelper().setCustomizedNotificationData(customizedNotificationData, inMeetingNotificationHandle);
        }

    }
}
