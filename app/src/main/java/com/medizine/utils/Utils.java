package com.medizine.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.medizine.BuildConfig;
import com.medizine.Constants;
import com.medizine.FirebaseRemoteConfigUpdate;
import com.medizine.MedizineApp;
import com.medizine.R;
import com.medizine.activity.SplashActivity;
import com.medizine.db.PrefService;
import com.medizine.db.StorageService;
import com.medizine.model.entity.User;
import com.medizine.model.enums.UserType;
import com.medizine.network.NetworkService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;

public class Utils {
    private static final int UNCHECKED_ERROR_TYPE_CODE = -100;
    private static final String TAG = Utils.class.getSimpleName();

    public static int dpToPixels(float dpValue) {
        DisplayMetrics metrics = MedizineApp.getAppContext().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, metrics);
    }

    public static int spToPixels(float spValue) {
        DisplayMetrics metrics = MedizineApp.getAppContext().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, metrics);
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static String getDeviceId() {
        return Settings.Secure.getString(MedizineApp.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static Integer getAppCount() {
        return PrefService.getInstance().getInt(Constants.APP_OPEN_COUNT, Constants.APP_OPEN_DEFAULT_COUNT);
    }

    public static Long getFirstAppOpenDate() {
        return PrefService.getInstance().getLong(Constants.APP_FIRST_OPEN_DATE, Constants.APP_FIRST_OPEN_DEFAULT_VALUE);
    }

    public static Long getLastAppOpenDate() {
        return PrefService.getInstance().getLong(Constants.APP_LAST_OPENED_DATE, Constants.APP_LAST_OPENED_DEFAULT_DATE);
    }

    public static int getAndroidOs() {
        return Build.VERSION.SDK_INT;
    }

    public static Integer getVersionCode() {
        try {
            PackageInfo packageInfo = MedizineApp.getAppContext().getPackageManager().getPackageInfo(MedizineApp.getAppContext().getPackageName(), 0);
            Log.d(TAG, "versionCode:" + packageInfo.versionCode);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Utils.logException(TAG, e);
        }
        return null;
    }

    public static void dialPhone(Context context, String phoneNumber) {
        if (!phoneNumber.startsWith("0") && !phoneNumber.startsWith("+")) {
            phoneNumber = "0" + phoneNumber;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

//    public static void openTelegram(Activity activity, String telegramUserName) {
//        Intent intentToTelegram = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TELEGRAM_LINK_PREFIX + telegramUserName));
//        if (intentToTelegram.resolveActivity(activity.getPackageManager()) != null) {
//            activity.startActivity(intentToTelegram);
//        } else {
//            Toast.makeText(activity, R.string.telegram_app_not_found, Toast.LENGTH_SHORT).show();
//        }
//    }

//    public static void openWhatsApp(Activity activity, String phoneNumber) {
//        String countryCode = Utils.getCountryCodeFromString(phoneNumber);
//        String phone = Utils.getMobileNumberFromString(phoneNumber);
//
//        if (Utils.isNullOrEmpty(countryCode)) {
//            countryCode = "91";
//        } else {
//            countryCode.replace("+", "");
//        }
//
//        if (!Utils.isPhoneValid(phone)) {
//            Toast.makeText(MedizineApp.getAppContext(), R.string.whatsapp_validation_msg, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String whatsAppNumber = countryCode + phone;
//
//        Intent intentToWhatsApp = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WHATS_APP_LINK_PREFIX + whatsAppNumber));
//
//        if (intentToWhatsApp.resolveActivity(activity.getPackageManager()) != null) {
//            activity.startActivity(intentToWhatsApp);
//        } else {
//            Toast.makeText(activity, R.string.whatsapp_not_found, Toast.LENGTH_SHORT).show();
//        }
//    }

    public static String getTimeFromISOString(String t1) {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String t2 = new SimpleDateFormat(pattern).format(new Date());
        try {
            Date videoDate = format.parse(t1);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                videoDate = new Date(videoDate.getTime() - videoDate.getTimezoneOffset() * 1000 * 60);
            }
            Date currentDate = format.parse(t2);
            String date1 = videoDate.getDate() + "/" + videoDate.getMonth() + "/" + videoDate.getYear();
            String time1 = videoDate.getHours() + ":" + videoDate.getMinutes();
            String date2 = currentDate.getDate() + "/" + currentDate.getMonth() + "/" + currentDate.getYear();
            String finalDate = null;
            String[] splitTime = time1.split(":");
            String hours = splitTime[0];
            if (hours.length() == 1) {
                hours = "0" + hours;
            }
            String minutes = splitTime[1];
            if (minutes.length() == 1) {
                minutes = "0" + minutes;
            }
            String[] splitDate = date1.split("/");
            String date = splitDate[0];
            Boolean isUpcomingDate = Integer.parseInt(date) > Integer.parseInt(date2.split("/")[0]);
            if (date.length() == 1) {
                date = "0" + date;
            }
            String months = splitDate[1];
            months = String.valueOf(Integer.parseInt(months) + 1);
            if (months.length() == 1) {
                months = "0" + months;
            }
            String years = splitDate[2];
            years = "20" + years.substring(1);

            if (!date1.equals(date2) && isUpcomingDate) {
                finalDate = date + "/" + months + "/" + years;
            }
            return finalDate != null ? finalDate + ", " + convertTimeToAptFormat(hours + "." + minutes, true) : convertTimeToAptFormat(hours + "." + minutes, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

//    public static String getDiffTimeString(@Nullable String t1) {
//        if (t1 == null)
//            return null;
//
//        Context context = MedizineApp.getAppContext();
//
//        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
//        }
//
//        String t2 = new SimpleDateFormat(pattern).format(new Date());
//        SimpleDateFormat format = new SimpleDateFormat(pattern);
//
//        long diff = 0;
//        try {
//            Date videoDate = format.parse(t1);
//            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
//                videoDate = new Date(videoDate.getTime() - videoDate.getTimezoneOffset() * 1000 * 60);
//            }
//            Date currentDate = format.parse(t2);
//            diff = currentDate.getTime() - videoDate.getTime();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        long sec = diff / 1000;
//        if (sec < 60) {
//            return sec + " " + (sec == 1 ? context.getString(R.string.second) : context.getString(R.string.seconds));
//        }
//        long mins = sec / 60;
//        if (mins < 60) {
//            return mins + " " + (mins == 1 ? context.getString(R.string.minute) : context.getString(R.string.minutes));
//        }
//        long hours = mins / 60;
//        if (hours < 24) {
//            return hours + " " + (hours == 1 ? context.getString(R.string.hour) : context.getString(R.string.hours));
//        }
//        long days = hours / 24;
//        if (days < 30) {
//            return days + " " + (days == 1 ? context.getString(R.string.day) : context.getString(R.string.days));
//        }
//        long months = days / 30;
//        if (months < 12) {
//            return months + " " + (months == 1 ? context.getString(R.string.month) : context.getString(R.string.months));
//        }
//        long years = months / 12;
//        return years + " " + (years == 1 ? context.getString(R.string.year) : context.getString(R.string.years));
//    }

    public static String convertDateToString(long date) {
        return new SimpleDateFormat("d MMM", Locale.US).format(new Date(date));
    }

    public static String formatDateTodMMMMString(long date) {
        return new SimpleDateFormat("d MMMM", Locale.US).format(new Date(date));
    }

    public static String convertTimeToAptFormat(String time, boolean twelveHours) {
        if (isNullOrEmpty(time)) {
            return "";
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH.mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa");
        try {
            if (twelveHours == true) {
                Date date3 = sdf1.parse(time);
                return sdf2.format(date3);
            } else {
                Date date3 = sdf2.parse(time);
                return sdf1.format(date3);
            }
        } catch (ParseException e) {
            Utils.logException(TAG, e);
            return null;
        }
    }

    public static boolean compareTimeStrings(String t1, String t2) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date d1 = sdf.parse(t1);
            Date d2 = sdf.parse(t2);
            return d1.getTime() <= d2.getTime();
        } catch (ParseException e) {
            Utils.logException(TAG, e);
            return false;
        }
    }

    public static boolean isNullOrEmpty(@Nullable String str) {
        if (str == null) {
            return true;
        }

        return str.trim().isEmpty();
    }

    public static boolean isNotEmpty(@Nullable String str) {
        return !isNullOrEmpty(str);
    }

    public static String getVersionName() {
        try {
            PackageInfo packageInfo = MedizineApp.getAppContext().getPackageManager().getPackageInfo(MedizineApp.getAppContext().getPackageName(), 0);
            Log.d(TAG, " versionName: " + packageInfo.versionName);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Utils.logException(TAG, e);
        }
        return null;
    }

    public static void incrementAndSetAppCount() {
        Integer appOpenCount = PrefService.getInstance().getInt(Constants.APP_OPEN_COUNT, Constants.APP_OPEN_DEFAULT_COUNT);
        appOpenCount += 1;
        Log.d(TAG, "App open count: " + appOpenCount);
        PrefService.getInstance().saveData(Constants.APP_OPEN_COUNT, appOpenCount);
    }

    public static void setFirstAppOpenedDate() {
        Calendar calendar = Calendar.getInstance();
        PrefService.getInstance().saveData(Constants.APP_FIRST_OPEN_DATE, calendar.getTimeInMillis());
        Log.d(TAG, "Date in which the app was firstOpened: " + PrefService.getInstance().getLong(Constants.APP_FIRST_OPEN_DATE, Constants.APP_FIRST_OPEN_DEFAULT_VALUE));
    }

    public static void setLastOpenedAppDate() {
        Calendar calendar = Calendar.getInstance();
        PrefService.getInstance().saveData(Constants.APP_LAST_OPENED_DATE, PrefService.getInstance().getLong(Constants.APP_OPENED_DATE, Constants.APP_OPENED_DEFAULT_DATE));
        Long lastAppOpened = PrefService.getInstance().getLong(Constants.APP_LAST_OPENED_DATE, Constants.APP_LAST_OPENED_DEFAULT_DATE);
        Log.d(TAG, "App last opened: " + lastAppOpened);
        PrefService.getInstance().saveData(Constants.APP_OPENED_DATE, calendar.getTimeInMillis());
    }

    public static String getLocale() {
//        if (getUser() != null && getUser().getSettings() != null) {
//            return getUser().getSettings().getLanguage();
//        } else {
//            return "en";
//        }
        return "en";
    }

//    public static String getUserType() {
//        return getUser().getUserType();
//    }

    @NonNull
    public static String getBaseDirectory() {
        String directoryPath = Environment.getExternalStorageDirectory() + File.separator + Constants.APP_NAME;
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return directoryPath;
    }

//    @Nullable
//    public static Uri getLocalUri(@Nullable MediaLink mediaLink) {
//        if (mediaLink == null) {
//            return null;
//        }
//        return getLocalUri(mediaLink.getS3Links().get(0).getLink());
//    }

//    @Nullable
//    public static Uri getUri(@Nullable MediaLink mediaLink) {
//        if (mediaLink == null) {
//            return null;
//        }
//
//        Uri uri = getLocalUri(mediaLink);
//        if (uri == null) {
//            uri = Uri.parse(mediaLink.getS3Links().get(0).getLink());
//        }
//        return uri;
//    }
//
//    @Nullable
//    public static Uri getUri(@Nullable String url) {
//        if (url == null)
//            return null;
//
//        Uri uri = getLocalUri(url);
//        if (uri == null) {
//            uri = Uri.parse(url);
//        }
//        return uri;
//    }

    public static String getExtension(@NonNull String url) {
        String[] splits = url.split("/");
        String filename = splits[splits.length - 1];
        String[] fileSplits = filename.split(".");
        if (fileSplits.length > 0) {
            return "." + fileSplits[fileSplits.length - 1];
        } else {
            return "";
        }
    }

    public static boolean isMyServiceRunning(@NonNull Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) MedizineApp.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

//    public static void setUserFieldsToCrashlytics() {
//        User user = getUser();
//        try {
//            Crashlytics.setUserIdentifier(user.getMobile());
//            Crashlytics.setUserEmail(user.getId());
//            Crashlytics.setUserName(user.getName());
//        } catch (NullPointerException e) {
//            logOutUser();
//        }
//    }
//
//    public static void setPrivateKeyToCrashlytics(String apiKey) {
//        Crashlytics.setString("apiKey", apiKey);
//    }
//
//    public static void setUserIdToCrashlytics(String userId) {
//        Crashlytics.setString("userId", userId);
//    }
//
//    public static void logActivityStatusToCrashlytics(String tag, String lifecycleEvent) {
//        Crashlytics.log(Log.INFO, "Activity: " + tag, lifecycleEvent);
//    }
//
//    public static void logFragmentStatusToCrashlytics(String tag, String lifecycleEvent) {
//        Crashlytics.log(Log.INFO, "Fragment: " + tag, lifecycleEvent);
//    }
//
//    public static void logLocationStatus(String message) {
//        Crashlytics.log(Log.INFO, "Location", message);
//    }

    public static void reinitializeNetworkModule() {
        NetworkService.destroyInstance();
        MedizineApp.destroyNetComponent();
        MedizineApp.buildAndReturnNetComponent();
    }

    public static void logOutUser() {
        Context context = MedizineApp.getAppContext();
        // Blocking call to clear all tables
        Callable<Void> callable = () -> {
            StorageService.getInstance().getMedizineDatabase().clearAllTables();
            StorageService.getInstance().clearCache();
            PrefService.getInstance().deleteAll();
            reinitializeNetworkModule();
            return null;
        };
        Future<Void> future = Executors.newSingleThreadExecutor().submit(callable);
        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            Utils.logException(TAG, e);
        }

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

//    public static void verifyGooglePlayService(Context context) throws DeviceUnsupportedException,
//            GooglePlayServicesOutDatedException, GooglePlayServicesNotInstalledException, UnknownErrorException {
//
//        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
//        int status = googleAPI.isGooglePlayServicesAvailable(context);
//
//        if (status == ConnectionResult.SUCCESS) {
//            return;
//        }
//
//        if (googleAPI.isUserResolvableError(status)) {
//            switch (status) {
//                case ConnectionResult.SERVICE_MISSING: {
//                    throw new GooglePlayServicesNotInstalledException("Google Play Service not installed");
//                }
//                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED: {
//                    throw new GooglePlayServicesOutDatedException("Google play service is out date");
//                }
//                default: {
//                    throw new UnknownErrorException("UNKNOWN_ERROR_CODE( " + status + " )");
//                }
//            }
//        } else {
//            throw new DeviceUnsupportedException("Your device does not support Google Play Service");
//        }
//    }

    public static void logException(String tag, @NonNull Throwable e) {
        Log.e(tag, e.getMessage(), e);
        Utils.writeToLogFile(tag + " : " + e.toString());
    }

    public static void logException(String tag, String message, @NonNull Throwable e) {
        Log.e(tag, message, e);
        Utils.writeToLogFile(tag + " : " + message + " : " + e.toString());
    }

    public static Bitmap getBitmapFromVectorDrawable(@NonNull Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public static String generateAndReturnDeepLink(@NonNull String dataName, @NonNull String dataId, @Nullable String desc) {
        StringBuilder builder = new StringBuilder();
        String title = MedizineApp.getAppContext().getString(R.string.app_name);
        if (desc == null) {
            desc = "";
        }

        try {
            title = URLEncoder.encode(title, "utf-8");
            desc = URLEncoder.encode(desc, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Utils.logException(TAG, e);
        }

        return builder.append("https://z5nq4.app.goo.gl/?link=https://bodh.me/")
                .append(dataName).append("/").append(dataId)
                .append("&apn=" + BuildConfig.APPLICATION_ID)
                .append("&afl=" + getPlayStoreLink())
                .append("&st=" + title)
                .append("&sd=" + desc)
                .append("&si=" + "https://s3.ap-south-1.amazonaws.com/soul-media-prod/app_icon")
                .append("&utm_source=" + getUser().getId())
                .toString();
    }

    public static String getPlayStoreLink() {
        return "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    }

    public static int compareVersions(String version1, String version2) {

        String[] levels1 = version1.split("\\.");
        String[] levels2 = version2.split("\\.");

        int length = Math.max(levels1.length, levels2.length);
        for (int i = 0; i < length; i++) {
            Integer v1 = i < levels1.length ? Integer.parseInt(levels1[i]) : 0;
            Integer v2 = i < levels2.length ? Integer.parseInt(levels2[i]) : 0;
            int compare = v1.compareTo(v2);
            if (compare != 0) {
                return compare;
            }
        }

        return 0;
    }

    public static void redirectToPlayStore(@NonNull Context context) {
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void shareApp() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Hey, checkout this new cool app to connect with top doctors of your town, Download Medizine!");
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MedizineApp.getAppContext().startActivity(intent);
    }

    public static String getAuthenticationHeader() {
        String apiKey = StorageService.getInstance().getPrivateKey();
        long unixTime = System.currentTimeMillis();
        String requestTimeStamp = String.valueOf(unixTime);
        String nonce = UUID.randomUUID().toString();
        String signatureRawData = String.format("%s%s", requestTimeStamp, nonce);
        String authHeader = "";
        try {
            byte[] secretKeyByteArray = apiKey.getBytes(StandardCharsets.UTF_8);
            byte[] signature = signatureRawData.getBytes(StandardCharsets.UTF_8);
            Mac sha256_HMAC;
            final String HMAC_SHA256 = "HMACSHA256";
            sha256_HMAC = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(secretKeyByteArray, HMAC_SHA256);
            sha256_HMAC.init(keySpec);
            byte[] signatureBytes = sha256_HMAC.doFinal(signature);
            Formatter formatter = new Formatter();
            for (byte b : signatureBytes) {
                formatter.format("%02x", b);
            }
            String signatureHash = formatter.toString();
            authHeader = String.format("%s:%s:%s", signatureHash, nonce, requestTimeStamp);
        } catch (Exception e) {
            Utils.logException(TAG, e);
        }
        return authHeader;
    }

    public static String formatFirebaseString(String str) {
        return str.replace("\\n", "\n");
    }

    public static String capitalizeWords(@Nullable String str) {
        if (str == null) {
            return null;
        }

        str = str.trim();
        StringBuilder stringBuilder = new StringBuilder(str.length());
        str = str.replace("-", " - ");
        str = str.replace("(", "( ");
        str = str.replace(")", " )");
        String[] words = str.split("\\s");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].trim();
            if (words[i].length() > 1) {
                stringBuilder.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1).toLowerCase()).append(" ");
            } else if (words[i].length() == 1) {
                stringBuilder.append(Character.toUpperCase(words[i].charAt(0))).append(" ");
            }
        }

        String result = stringBuilder.toString().trim();
        result = result.replace("( ", "(");
        result = result.replace(" )", ")");
        return result;
    }

    public static boolean showOnboardingActivity() {
        return FirebaseRemoteConfigUpdate.getInstance().showOnboarding();
    }

    public static boolean showForceShareDialog() {
        Integer appOpenCount = PrefService.getInstance().getInt(Constants.APP_OPEN_COUNT, Constants.APP_OPEN_DEFAULT_COUNT);
        String sequence = FirebaseRemoteConfigUpdate.getInstance().getForceShareSequence();
        return false;
//        return !JainamApp.sharePopupShown &&
//               FirebaseRemoteConfigUpdate.getInstance().showForceShare() &&
//               Arrays.asList(sequence.split(",")).contains(appOpenCount + "");
    }

    public static boolean showShareDialogOnDownload() {
//        return !JainamApp.sharePopupShown && Utils.getDownloadedLecturesCount() > 0 && (Utils.getDownloadedLecturesCount() % 5 == 0);
        return false;
    }

    public static boolean showShareDialogOnPlay() {
//        return !JainamApp.sharePopupShown && Utils.getUniquePlayedLectures() > 0 && (Utils.getUniquePlayedLectures() % 5 == 0);
        return false;
    }

    public static String getFirstName(String name) {
        if (name.contains(" ")) {
            return name.split(" ")[0];
        } else {
            return name;
        }
    }

    // url = file path or whatever suitable URL you want.
    @Nullable
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

//    @NonNull
//    public static String compressImage(@NonNull String imagePath, float maxWidth, float maxHeight) {
//        Bitmap scaledBitmap = null;
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
//
//        int actualHeight = options.outHeight;
//        int actualWidth = options.outWidth;
//
//        float imgRatio = (float) actualWidth / (float) actualHeight;
//        float maxRatio = maxWidth / maxHeight;
//
//        if (actualHeight > maxHeight || actualWidth > maxWidth) {
//            if (imgRatio < maxRatio) {
//                imgRatio = maxHeight / actualHeight;
//                actualWidth = (int) (imgRatio * actualWidth);
//                actualHeight = (int) maxHeight;
//            } else if (imgRatio > maxRatio) {
//                imgRatio = maxWidth / actualWidth;
//                actualHeight = (int) (imgRatio * actualHeight);
//                actualWidth = (int) maxWidth;
//            } else {
//                actualHeight = (int) maxHeight;
//                actualWidth = (int) maxWidth;
//            }
//        }
//
//        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
//        options.inJustDecodeBounds = false;
//        options.inDither = false;
//        options.inPurgeable = true;
//        options.inInputShareable = true;
//        options.inTempStorage = new byte[16 * 1024];
//
//        try {
//            bmp = BitmapFactory.decodeFile(imagePath, options);
//        } catch (OutOfMemoryError exception) {
//            logException(TAG, exception);
//        }
//        try {
//            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
//        } catch (OutOfMemoryError exception) {
//            logException(TAG, exception);
//        }
//
//        float ratioX = actualWidth / (float) options.outWidth;
//        float ratioY = actualHeight / (float) options.outHeight;
//        float middleX = actualWidth / 2.0f;
//        float middleY = actualHeight / 2.0f;
//
//        Matrix scaleMatrix = new Matrix();
//        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
//
//        Canvas canvas = new Canvas(scaledBitmap);
//        canvas.setMatrix(scaleMatrix);
//        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
//
//        if (bmp != null) {
//            bmp.recycle();
//        }
//
//        ExifInterface exif;
//        try {
//            exif = new ExifInterface(imagePath);
//            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
//            Matrix matrix = new Matrix();
//            if (orientation == 6) {
//                matrix.postRotate(90);
//            } else if (orientation == 3) {
//                matrix.postRotate(180);
//            } else if (orientation == 8) {
//                matrix.postRotate(270);
//            }
//            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
//        } catch (IOException e) {
//            logException(TAG, e);
//        }
//        FileOutputStream out = null;
//        try {
//            out = new FileOutputStream(imagePath);
//
//            //write the compressed bitmap at the destination specified by filename.
//            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
//            out.close();
//        } catch (IOException e) {
//            logException(TAG, e);
//        }
//
//        return imagePath;
//    }

//    @NonNull
//    private static String getFilename() {
//        File mediaStorageDir = ImageUtils.getOutputMediaFile();
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            mediaStorageDir.mkdirs();
//        }
//        String uriString = mediaStorageDir.getAbsolutePath();
//        return uriString;
//    }


    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    @UiThread
    public static void selectDate(@NonNull Context context,
                                  @NonNull final TextView textView) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year, monthOfYear, dayOfMonth) -> textView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year),
                mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    public static FragmentActivity getActivity(Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof ContextWrapper) {
            if (context instanceof FragmentActivity) {
                return (FragmentActivity) context;
            } else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }
        return null;
    }

    public static boolean isListEmpty(@Nullable List list) {
        return list == null || list.isEmpty();
    }

    public static @NonNull
    List emptyIfNull(@Nullable List list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        } else {
            return list;
        }
    }


    public static String getInitialsOfName(@NonNull String name) {
        name = name.split("-")[0];
        name = capitalizeWords(name);
        String[] words = name.split("\\s");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(word.charAt(0));
        }

        return result.toString();
    }

    public static String convertTimeToReadableFormat(long timeInMillis) {
        return (String) DateUtils.getRelativeTimeSpanString(timeInMillis, System.currentTimeMillis(), MINUTE_IN_MILLIS);
    }


//    public static MultiLingual getMultiLingual(@Nullable String text) {
//        if (text == null || text.isEmpty()) {
//            return null;
//        }
//
//        MultiLingual multiLingual = new MultiLingual();
//
//        if (!Utils.isNullOrEmpty(text)) {
//            int englishCount = 0;
//            int hindiCount = 0;
//            for (char c : text.toCharArray()) {
//                if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.BASIC_LATIN) {
//                    englishCount++;
//                } else {
//                    hindiCount++;
//                }
//            }
//
//            if (englishCount > hindiCount) {
//                multiLingual.setEn(text);
//            } else {
//                multiLingual.setHi(text);
//            }
//        }
//        return multiLingual;
//    }

//    public static MultiLingual generateMultiLingual(@NonNull String en, @NonNull String hi) {
//        MultiLingual multiLingual = new MultiLingual();
//        multiLingual.setHi(hi);
//        multiLingual.setEn(en);
//        return multiLingual;
//    }
//
//    public static MediaLink getMediaLinkByUri(@Nullable String uri) {
//        if (Utils.isNullOrEmpty(uri)) {
//            return null;
//        }
//        MediaLink mediaLink = new MediaLink();
//        mediaLink.setUri(uri);
//        return mediaLink;
//    }

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static List<String> removeEmptyFromList(List<String> list) {
        if (list == null) {
            return null;
        }

        list.removeAll(Arrays.asList("", null));
        return list;
    }

    public static boolean compareStrings(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

//    public static boolean compareMultilinguals(MultiLingual lingual1, MultiLingual lingual2) {
//        return lingual1 == null ? lingual2 == null : lingual1.equals(lingual2);
//    }

    public static String getLocalString(@StringRes int resId) {
        return MedizineApp.getAppContext().getString(resId);
    }

    public static String getLocalString(@StringRes int resId, Object... formatArgs) {
        return MedizineApp.getAppContext().getString(resId, formatArgs);
    }

//    public static void showReviewPopup(Activity activity, boolean isUpdate, boolean isAdmin) {
//        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity, R.style.ReviewPopupDialog);
//        alertBuilder.setCancelable(false);
//
//        if (isAdmin) {
//            alertBuilder.setMessage(R.string.update_popup_message_admin);
//        } else {
//            if (isUpdate) {
//                alertBuilder.setMessage(R.string.update_popup_message);
//            } else {
//                alertBuilder.setMessage(R.string.add_popup_message);
//            }
//        }
//
//        alertBuilder.setPositiveButton(R.string.ok, (dialog, which) -> {
//            Intent intent = new Intent();
//            if (activity instanceof EditTempleActivity) {
//                activity.setResult(Constants.REQUEST_EDIT_TEMPLE, intent);
//            }
//            activity.finish();
//        });
//        alertBuilder.setNegativeButton(null, null);
//
//        AlertDialog alert = alertBuilder.create();
//        alert.show();
//    }

    public static User getUser() {
        return StorageService.getInstance().getUser();
    }

    @NonNull
    public static List<String> getIdList(@Nullable List<User> users) {
        List<String> ids = new ArrayList<>();
        if (!Utils.isListEmpty(users)) {
            for (User user : users) {
                ids.add(user.getId());
            }
        }
        return ids;
    }

//    public static void showConfirmationDialog(Activity activity) {
//        new AlertDialog.Builder(activity, R.style.ReviewPopupDialog)
//                .setMessage(activity.getString(R.string.go_back_dialog_message))
//                .setPositiveButton(R.string.discard, (dialog, which) -> activity.finish())
//                .setNegativeButton(R.string.continue_editing, (dialog, which) -> dialog.dismiss())
//                .create()
//                .show();
//    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getDateFromMiliseceonds(String time) {
        return new Date(Long.parseLong(time)).toString();
    }

//    public static MaterialShowcaseView createMaterialShowcaseView(Activity activity, View targetView, String title, String description, boolean dismissOnItemClick) {
//        return new MaterialShowcaseView.Builder(activity)
//                .setTarget(targetView)
//                .setTitleText(title)
//                .setContentText(description)
//                .setDismissText(activity.getResources().getString(R.string.dismiss_text))
//                .setTargetTouchable(dismissOnItemClick)
//                .setDismissOnTargetTouch(dismissOnItemClick)
//                .setMaskColour(activity.getResources().getColor(R.color.transparent_blue))
//                .build();
//    }

    //Check if it is a valid 10 digit number
    public static boolean isPhoneValid(@Nullable String phoneNumber) {
        if (Utils.isNullOrEmpty(phoneNumber)) {
            return false;
        } else if (!(phoneNumber.trim().length() == 10)) {
            return false;
        } else return Pattern.compile("^[0-9][0-9]{9}").matcher(phoneNumber.trim()).matches();
    }

    public static Spanned getSpannedText(@NonNull String text) {
        return Html.fromHtml(Html.fromHtml(TextUtils.htmlEncode(text)).toString());
    }

//    public static boolean isLanguageHindi() {
//        boolean isAppLanguageHindi = false;
//        boolean isDisplayLanguageHindi = !Locale.getDefault().getDisplayLanguage().equals(Locale.ENGLISH.getDisplayLanguage());
//        User user = getUser();
//        if (user != null && user.getSettings() != null && "hi".equals(user.getSettings().getLanguage())) {
//            isAppLanguageHindi = true;
//        }
//        return isAppLanguageHindi || isDisplayLanguageHindi;
//    }

    public static void disableClipOnParents(View v) {
        if (v.getParent() == null) {
            return;
        }

        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setClipChildren(false);
            ((ViewGroup) v).setClipToPadding(false);
        }

        if (v.getParent() instanceof View) {
            disableClipOnParents((View) v.getParent());
        }
    }

    public static void showOnBoarding(Context context) {
        PrefService.getInstance().saveData(Constants.PrefConstants.SHOW_ONBOARDING, true);
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

//    public static String getCountryCode() {
//        TelephonyManager telephonyManager = (TelephonyManager) JainamApp.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
//        String countryISO = telephonyManager.getSimCountryIso().toUpperCase();
//        for (CountryCode code : CountryCode.values()) {
//            if (code.name().equals(countryISO)) {
//                return CountryCode.valueOf(countryISO).getCountryCode();
//            }
//        }
//
//        return IN.getCountryCode();
//    }

//    public static String getCountryCodeWithoutPlusSign() {
//        return getCountryCode().replace("+", "");
//    }
//
//    public static boolean isValidCountryCode(String countryCode) {
//        return IN.getCountryCode().equals(countryCode) || US.getCountryCode().equals(countryCode) || GB.getCountryCode().equals(countryCode);
//    }
//
//    public static String getCountryCodeFromString(String phone) {
//        if (Utils.isNullOrEmpty(phone)) {
//            return null;
//        }
//        if (phone.startsWith(IN.getCountryCode())) {
//            return IN.getCountryCode();
//        } else if (phone.startsWith(US.getCountryCode())) {
//            return US.getCountryCode();
//        } else if (phone.startsWith(GB.getCountryCode())) {
//            return GB.getCountryCode();
//        } else {
//            return null;
//        }
//    }

//    public static String getMobileNumberFromString(String phone) {
//        if (Utils.isNullOrEmpty(phone)) {
//            return null;
//        }
//        if (phone.startsWith(IN.getCountryCode())) {
//            return phone.replace(IN.getCountryCode(), "");
//        } else if (phone.startsWith(US.getCountryCode())) {
//            return phone.replace(US.getCountryCode(), "");
//        } else if (phone.startsWith(GB.getCountryCode())) {
//            return phone.replace(GB.getCountryCode(), "");
//        } else {
//            return phone;
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context, NotificationManager notificationManager, String id, @StringRes int nameResource, @StringRes int descriptionResource, int importance) {
        NotificationChannel channel = new NotificationChannel(id, context.getString(nameResource), importance);
        channel.setDescription(context.getString(descriptionResource));
        channel.setShowBadge(true);
        channel.canShowBadge();
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
        notificationManager.createNotificationChannel(channel);
    }

    private static long getTotalInternalMemory() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    private static long totalRamMemorySize() {

        ActivityManager activityManager = (ActivityManager) MedizineApp.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;
    }

    private static String formatSize(long totalMemory) {
        double kb = totalMemory / 1024.0;
        double mb = totalMemory / 1048576.0;
        double gb = totalMemory / 1073741824.0;
        double tb = totalMemory / 1099511627776.0;
        String finalValue;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        if (tb > 1) {
            finalValue = twoDecimalForm.format(tb).concat("TB");
        } else if (gb > 1) {
            finalValue = twoDecimalForm.format(gb).concat("GB");
        } else if (mb > 1) {
            finalValue = twoDecimalForm.format(mb).concat("MB");
        } else if (kb > 1) {
            finalValue = twoDecimalForm.format(mb).concat("KB");
        } else {
            finalValue = twoDecimalForm.format(totalMemory).concat("B");
        }
        return finalValue;
    }

    public static String isPhoneRooted() {
        String[] binaryPaths = {"/data/local/", "/data/local/bin/", "/data/local/xbin/", "/sbin/", "/su/bin/", "/system/bin/", "/system/bin/.ext/",
                "/system/bin/failsafe/", "/system/sd/xbin/", "/system/usr/we-need-root/", "/system/xbin/", "/system/app/Superuser.apk",
                "/cache", "/data", "/dev"};

        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return "true";
        }
        String Name = "su";
        for (String path : binaryPaths) {
            if (new File(path + Name).exists()) {
                return "true";
            }
        }
        return "false";
    }

    public static String getPrettyPrintedJson(Object object) {
        Type objectType = new TypeToken<Object>() {
        }.getType();
        return new GsonBuilder().setPrettyPrinting().create().toJson(object, objectType);
    }

    public static String getJsonFrom(Object object) {
        Type objectType = new TypeToken<Object>() {
        }.getType();
        return new Gson().toJson(object, objectType);
    }

    public static Object getObjectFromJson(String json) {
        Type objectType = new TypeToken<Object>() {
        }.getType();
        return new Gson().fromJson(json, objectType);
    }

    public static String getUserName() {
        return StorageService.getInstance().getUser().getName();
    }

    public static String getUserID() {
        return StorageService.getInstance().getUser().getId();
    }

//    public static String getTimePastString(Context context, long pastTime) {
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - pastTime < 60 * 60 * 1000) {
//            return context.getString(R.string.some_time_ago);
//        } else if (currentTime - pastTime < 24 * 60 * 60 * 1000) {
//            int hours = (int) ((currentTime - pastTime) / (60 * 60 * 1000));
//            return context.getResources().getQuantityString(R.plurals.n_hours_ago, hours, hours);
//        } else if ((currentTime - pastTime) < (30 * 24 * 60 * 60 * 1000L)) {
//            int days = (int) ((currentTime - pastTime) / (24 * 60 * 60 * 1000));
//            return context.getResources().getQuantityString(R.plurals.n_days_ago, days, days);
//        } else if ((currentTime - pastTime) < (365 * 24 * 60 * 60 * 1000L)) {
//            int months = (int) ((currentTime - pastTime) / (24 * 60 * 60 * 1000));
//            return context.getResources().getQuantityString(R.plurals.n_months_ago, months, months);
//        } else {
//            int years = (int) ((currentTime - pastTime) / (365 * 24 * 60 * 60 * 1000L));
//            return context.getResources().getQuantityString(R.plurals.n_years_ago, years, years);
//        }
//    }

    public static void showDatePickerDialog(Context context, String currentDate, DatePickerDialog.OnDateSetListener listener) {
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        //Set min limit
        final Calendar minDate = Calendar.getInstance();
        minDate.set(1700, 0, 0);

        //Set max limit
        final Calendar maxDate = Calendar.getInstance();
        maxDate.set(mYear, mMonth, mDay);

        int defaultYear = 1980;
        int defaultMonth = Calendar.JANUARY;
        int defaultDay = 1;

        if (!Utils.isNullOrEmpty(currentDate)) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = format.parse(currentDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                defaultYear = cal.get(Calendar.YEAR);
                defaultMonth = cal.get(Calendar.MONTH);
                defaultDay = cal.get(Calendar.DAY_OF_MONTH);

            } catch (ParseException e) {
                Utils.logException(TAG, e);
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, android.app.AlertDialog.THEME_HOLO_LIGHT, listener, defaultYear, defaultMonth, defaultDay);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    public static void writeToLogFile(String text) {
        File logFile = new File(Constants.PATH + Constants.LOG_FILE_NAME);
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(Calendar.getInstance().getTime().toString() + ": " + text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Utils.logException(TAG, e);
        }
    }

    public static String readLogFile() {
        File file = new File(Constants.PATH + Constants.LOG_FILE_NAME);
        String data;
        StringBuilder logFile = new StringBuilder();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            while ((data = br.readLine()) != null)
                logFile.append(data).append("\n\n");
            return logFile.toString();
        } catch (IOException e) {
            Utils.logException(TAG, e);
        }
        return logFile.toString();
    }

    public static void deleteLogFile() {
        File fileDelete = new File(Constants.PATH + Constants.LOG_FILE_NAME);
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                Log.d("LogFile", "Deleted");
            } else {
                Log.d("LogFile", "Not Deleted");
            }
        }
    }


    public static String parseDateString(@NonNull String date, @NonNull SimpleDateFormat newFormat) {
        SimpleDateFormat oldFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return newFormat.format(oldFormat.parse(date));
        } catch (ParseException e) {
            Utils.logException(TAG, e);
        }
        return date;
    }

    public static String parseDateString(@NonNull Date date, @NonNull SimpleDateFormat formatter) {
        return formatter.format(date);
    }

    public static String getFormattedDate(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public static String getFormattedDate(Date date) {
        return getFormattedDate(date.getTime());
    }

    public static String getFormattedTime(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa");
        return formatter.format(date);
    }

    public static String toISO8601UTC(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }

    public static String getFormattedTimeFromIsoDateString(String isoDate) {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date;
        try {
            date = sdf.parse(isoDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        if (date != null) {
            return getFormattedTime(date.getTime());
        }
        return null;
    }

    public static String generateEventStartIsoDate(@NonNull String date, @NonNull String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        try {
            return Utils.toISO8601UTC(sdf.parse(date + " " + time));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static String getFormattedString(String s1, String s2) {
//        return MedizineApp.getAppContext().getApplicationContext().getString(R.string.meta_data, s1, s2);
//    }

    public static boolean isValidUrl(String url) {
        return Utils.isNotEmpty(url) && Patterns.WEB_URL.matcher(url.trim()).matches();
    }

//    public static void disableJainamSpinner(@NonNull JainamSpinner jainamSpinner) {
//        Spinner spinner = ((Spinner) jainamSpinner.getChildAt(0));
//        spinner.setEnabled(false);
//    }

    //Returns true if str contains any `hi` characters else returns false
    public static boolean containsHiChars(@NonNull String str) {
        int unicodeShift = 0x0900;
        int numberOfHindiCharacters = 128;

        StringBuilder hindiUniChars = new StringBuilder();

        for (int i = 0; i < numberOfHindiCharacters; i++) {
            hindiUniChars.append("\\u0").append(Integer.toHexString(unicodeShift + i));
        }

        return Pattern.compile("(?:|\\s)[" + hindiUniChars + "]+?(?:\\s|$)").matcher(str).find();
    }

    //Returns `true` if str contains any `en` characters else returns false
    public static boolean containsEnChars(@NonNull String str) {
        return Pattern.compile(".*[a-zA-Z].*").matcher(str).find();
    }

    public static boolean isValidEnString(@NonNull String str) {
        return containsEnChars(str) && !containsHiChars(str);
    }

    public static boolean isValidHiString(@NonNull String str) {
        return containsHiChars(str) && !containsEnChars(str);
    }

//    public static MediaLink getMediaLinkByUri(@Nullable String uri, String mediaType) {
//        if (Utils.isNullOrEmpty(uri)) {
//            return null;
//        }
//        MediaLink mediaLink = new MediaLink();
//        mediaLink.setUri(uri);
//        mediaLink.setMediaType(mediaType);
//        return mediaLink;
//    }

    public static MultipartBody.Part getMultiPartBody(File file, String mimeType) {
        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }

    public static RequestBody getRequestBody() {
        return RequestBody.create(MediaType.parse("text/plain"), String.valueOf(System.nanoTime()));
    }

    public static boolean isValidUserName(@NonNull String userName) {
        String validUserName = "[a-zA-Z0-9]*";
        return userName.matches(validUserName);
    }

    @Nullable
    private static String getCurrentUserType() {
        return PrefService.getInstance().getString(Constants.USER_TYPE, null);
    }

    public static boolean isUserTypeDoctor() {
        return UserType.DOCTOR.name().equals(getCurrentUserType());
    }

    public static boolean isUserTypeNormal() {
        return UserType.NORMAL.name().equals(getCurrentUserType());
    }

    public static String getFormattedDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd, EEE");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        return sdf.format(date);
    }
}
