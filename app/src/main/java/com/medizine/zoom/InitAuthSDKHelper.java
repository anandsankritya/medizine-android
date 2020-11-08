package com.medizine.zoom;

import android.content.Context;
import android.util.Log;

import com.medizine.Constants;

import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;
import us.zoom.sdk.ZoomSDKRawDataMemoryMode;

/**
 * Init and auth zoom sdk first before using SDK interfaces
 */
public class InitAuthSDKHelper implements ZoomSDKInitializeListener {

    private final static String TAG = InitAuthSDKHelper.class.getSimpleName();

    private static InitAuthSDKHelper mInitAuthSDKHelper;

    private ZoomSDK mZoomSDK;

    private ZoomSDKInitializeListener mInitAuthSDKCallback;

    private InitAuthSDKHelper() {
        mZoomSDK = ZoomSDK.getInstance();
    }

    public synchronized static InitAuthSDKHelper getInstance() {
        mInitAuthSDKHelper = new InitAuthSDKHelper();
        return mInitAuthSDKHelper;
    }

    /**
     * init sdk method
     */
    public void initSDK(Context context, ZoomSDKInitializeListener callback) {
        if (!mZoomSDK.isInitialized()) {
            mInitAuthSDKCallback = callback;

            ZoomSDKInitParams initParams = new ZoomSDKInitParams();
            initParams.appKey = Constants.SDK_KEY;
            initParams.appSecret = Constants.SDK_SECRET;
            initParams.enableLog = true;
            initParams.logSize = 50;
            initParams.domain = Constants.WEB_DOMAIN;
            initParams.videoRawDataMemoryMode = ZoomSDKRawDataMemoryMode.ZoomSDKRawDataMemoryModeStack;
            mZoomSDK.initialize(context, this, initParams);
        }
    }

    /**
     * init sdk callback
     *
     * @param errorCode         defined in {@link us.zoom.sdk.ZoomError}
     * @param internalErrorCode Zoom internal error code
     */
    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        Log.d(TAG, "onZoomSDKInitializeResult, errorCode = " + errorCode + ", internalErrorCode = " + internalErrorCode);

        if (mInitAuthSDKCallback != null) {
            mInitAuthSDKCallback.onZoomSDKInitializeResult(errorCode, internalErrorCode);
        }
    }

    @Override
    public void onZoomAuthIdentityExpired() {
        Log.d(TAG, "onZoomAuthIdentityExpired");
    }

    public void reset() {
        mInitAuthSDKCallback = null;
    }
}

