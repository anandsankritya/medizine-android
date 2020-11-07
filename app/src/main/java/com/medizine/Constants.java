package com.medizine;

import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Constants {

    public static final String WEBSITE_LINK = "https://www.medizine.app/";
    public static final String CLICK_ACTION_LINK = "https://medizine.app/";
    public static final String APP_NAME = "Medizine";
    public static final String BOTH = "BOTH";
    public static final String UNKNOWN = "UNKNOWN";

    public static final String USER_TYPE = "userType";

    public static final String CATEGORY_TAG = "category";

    public static final String SELECTED_TAB_ONE = "selectedTabOne";
    public static final String SELECTED_TAB_TWO = "selectedTabTwo";

    public static final String SEPARATOR = "::::";

    public static final String MODULE_ID = "moduleId";
    public static final String MODULE_TYPE = "moduleType";

    public static final int REQUEST_LOCATION = 2;
    public static final int REQUEST_RESOLUTION_ONE = 201;

    public static final String OBJECT_ID = "objectId";
    public static final String OBJECT_TYPE = "objectType";
    public static final String HAS_PERMISSION = "hasPermission";
    public static final String USER_LIST = "userList";
    public static final int REQUEST_EDIT_USER_PROFILE = 1010;
    public static final int REQUEST_EDIT_DOCTOR_PROFILE = 1011;
    public static final int REQUEST_JOIN_ZOOM_MEETING = 501;
    public static final int REQUEST_GET_PDF = 9;
    public static final String IS_ADMIN = "is_admin";
    public static final String DELETED = "deleted";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ADDRESS = "address";
    public static final String ADMIN = "ADMIN";

    public static final String BROWSE_URL = "browse_url";

    public static final String ORIGINAL = "original";
    public static final String THUMBNAIL = "thumbnail";
    public static final String MAP16BY9 = "map_16by9";
    public static final String TRANSFORMATION = "transformation";


    public static final String PUSH_NOTIFICATION_DATA_TYPE_ID = "id";
    public static final String FCM_DEFAULT_NOTIFICATION_CHANNEL = "default";
    public static final String PUSH_NOTIFICATION_CLICK_ACTION = "click_action";

    public static final String FCM_OTHER_CHANNEL = "Others";

    //Used when sending deepLink from console
    public static final String FCM_DEEP_LINK = "deepLink";
    public static final String FCM_RENDER_NOTIFICATION = "renderNotification";
    public static final String RENDER_NOTIFICATION = "true";
    public static final String FCM_TITLE = "title";
    public static final String FCM_BODY = "body";
    public static final String FCM_CHANNEL_ID = "androidChannelId";
    public static final String FCM_IMAGE = "image";
    public static final String FCM_TAG = "tag";
    public static final String FCM_CATEGORY = "category";

    public static final int PENDING_REQUEST_ACTION = 6001;

    public static final int REWIND_TIME = 5000;
    public static final int FAST_FORWARD_TIME = 15000;

    public static final int REQUEST_PHONE_HINT = 111;
    public static final int DEEPLINK_DESC_MAX_LENGTH = 200;

    public static final int FCM_NOTIFICATION_REQUEST = 1005;
    public static final String NOTIFICATION_ID = "notificationId";

    public static final String LAST_CONTACT_PERMISSION_SHOWN = "lastContactPermissionShown";
    public static final int CONTACT_DELAY_DAYS = 10;

    public static final String LAST_LOCATION_UPLOADED_TIME = "lastLocationUploadedTime";
    public static final int LOCATION_UPLOAD_TIME_INTERVAL_IN_HRS = 24;

    public static final String AUTH_PRIVATE_KEY = "authPrivateKey";

    public static final String EMPTY_OBJECT_EXCEPTION = "Object is empty";

    public static final String IS_SHOWCASE_FORCE_QUIT = "isShowCaseForceQuit";

    public static final String DOWNLOAD_IN_PROGRESS = "downloadInProgress";
    public static final String NOT_DOWNLOADED = "notDownloaded";
    public static final String DOWNLOAD_FINISHED = "downloadFinished";
    public static final String CATEGORY = "category";
    public static final String POSITION = "position";
    public static final String IMAGE_LIST = "imageList";
    public static final String REFRESH_GALLERY = "refreshGallery";
    public static final String APP_FIRST_OPEN_DATE = "firstAppOpened";
    public static final long APP_FIRST_OPEN_DEFAULT_VALUE = 0;
    public static final String APP_OPEN_COUNT = "count";
    public static final Integer APP_OPEN_DEFAULT_COUNT = 0;
    public static final String APP_OPENED_DATE = "AppOpened";
    public static final Integer APP_OPENED_DEFAULT_DATE = 0;
    public static final String APP_LAST_OPENED_DATE = "lastOpened";
    public static final long APP_LAST_OPENED_DEFAULT_DATE = 0;
    public static final String SHOW_RATING_DIALOG = "show_rating_dialog";
    public static final String LAST_RATING_DIALOG_SHOWN = "last_rating_dialog_shown";
    public static final String SUSPEND_RATING_DIALOG_DAYS = "suspend_rating_dialog_days";
    public static final String LANG_EN = "en";
    public static final String LANG_HI = "hi";
    public static final float MAX_COMPRESS_WIDTH = 1600;
    public static final float MAX_COMPRESS_HEIGHT = 1600;

    public static final String MAP_BASE_URI = "geo:0,0?q=";
    public static final String TERMS_AND_CONDITIONS_URL = "https://medizine.app/termsandcondition/";
    public static final String PRIVACY_POLICY_URL = "https://medizine.app/privacypolicy/";
    public static final float THUMB_SIZE = 0.15f;
    public static final int MAX_ALLOWED_RESOLUTION = 1440;
    public static final int MIN_ALLOWED_RESOLUTION = 720;
    public static final String IS_NOTIFICATION_CHANNEL_SET = "isNotificationChannelSet";
    public static final String SECT = "sect";
    public static final String EVENT_ID = "Event_ID";
    public static final String INTERACTION_TYPE = "interactionType";
    //Zoom Feature Related Constants
    public static final int REQUEST_ZOOM_LOGIN = 1111;
    public static final String MEET_ID = "meetId";
    public static final String MEET_PASS = "meetPass";
    public static final String HOST_USER_ID = "hostUserId";
    public static final String ZOOM_OBJECT_ID = "zoomObjectId";
    public static final String ZOOM_SELECTED_TEMPLE_ID = "zoomSelectedTempleId";
    public static final String ZOOM_MEET_OBJ_JSON = "zoomMeetObjJson";
    public static final String IS_MEETING_JOIN_ONLY = "isJoinMeetingOnly";
    public static final String LIVE_USER_COUNT = "liveUserCount";
    public static final String ZOOM_MEETING_ENDED = "zoomMeetingEnded";
    public static final String ZOOM_LIVE_STREAMING_TAG = "zoomLiveStreamingTag";
    public static final String WEB_DOMAIN = "zoom.us";
    public static final String SDK_KEY = "";
    public static final String SDK_SECRET = "";
    public static final String TELEGRAM_LINK_PREFIX = "https://t.me/";
    public static final String WHATS_APP_LINK_PREFIX = "https://wa.me/";
    public static final String COUNTRY_CODE_IN = "+91";
    public static String PATH = MedizineApp.getAppContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/";
    public static String LOG_FILE_NAME = "logfile.txt";
    public static int SENSOR_SENSITIVITY = 20;
    public static String GITLAB_ISSUE_LABEL = "Production Bug";
    @NonNull
    public static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Documented
    @StringDef({DOWNLOAD_IN_PROGRESS, NOT_DOWNLOADED, DOWNLOAD_FINISHED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DownloadStatus {
    }

    public static class FirebaseConfigProperties {
        public static final String FORCE_SHARE_SEQUENCE = "force_share_sequence";
        public static final String SHARE_MSG = "share_msg";
        public static final String SHOW_ON_BOARDING = "show_onboarding";
        public static final String SHOW_FORCE_SHARE = "show_force_share";
        public static final String CONTACT_NUMBER = "contact_number";
        public static final String SHOW_LIVE_STREAM = "show_live_stream";
        public static final String TITLE_HOME = "title_home";
        public static final String FESTIVAL_TAG = "festival_tag";
        public static final String FESTIVAL_WISH_TEXT = "festival_wish_text";
        public static final String NORMAL_UPDATE_BODY = "normal_update_body";
        public static final String FORCE_UPDATE_BODY = "force_update_body";
        public static final String NORMAL_UPDATE_TYPE = "normal";
        public static final String FORCE_UPDATE_TYPE = "force";

    }

    public static class PrefConstants {
        public static final String SHOW_ONBOARDING = "show_onboarding";
    }

    public static class UserProperties {
        public static final String ID = "UserId";
        public static final String MOBILE_NUMBER = "UserMobile";
        public static final String LANGUAGE = "Language";
        public static final String RATING = "app_rating";
    }

    public static final String DOCTOR_ID = "doctorId";
    public static final String DOCTOR_MOBILE_NUMBER = "doctorMobileNumber";

    public static final String USER_ID = "userId";
    public static final String USER_MOBILE_NUMBER = "userMobileNumber";

    public static final int REQUEST_ADD_SLOT = 1001;

}
