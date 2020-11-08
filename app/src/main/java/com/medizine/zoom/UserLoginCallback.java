package com.medizine.zoom;

import java.util.ArrayList;

import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;

public class UserLoginCallback implements ZoomSDKAuthenticationListener {

    private final static String TAG = UserLoginCallback.class.getSimpleName();

    private static UserLoginCallback mUserLoginCallback;

    private ArrayList<JainamZoomAuthenticationListener> mListenerList = new ArrayList<>();

    private UserLoginCallback() {
        ZoomSDK.getInstance().addAuthenticationListener(this);
    }

    public synchronized static UserLoginCallback getInstance() {
        mUserLoginCallback = new UserLoginCallback();
        return mUserLoginCallback;
    }

    public void addListener(JainamZoomAuthenticationListener listener) {
        if (!mListenerList.contains(listener))
            mListenerList.add(listener);
    }

    public void removeListener(JainamZoomAuthenticationListener listener) {
        mListenerList.remove(listener);
    }

    /**
     * Called on ZoomSDK login success or failed
     *
     * @param result {@link ZoomAuthenticationError}.ZOOM_AUTH_ERROR_SUCCESS for success
     */
    @Override
    public void onZoomSDKLoginResult(long result) {
        for (JainamZoomAuthenticationListener listener : mListenerList) {
            if (listener != null) {
                listener.onZoomSDKLoginResult(result);
            }
        }
    }

    /**
     * Called on ZoomSDK logout success or failed
     *
     * @param result {@link ZoomAuthenticationError}.ZOOM_AUTH_ERROR_SUCCESS for success
     */
    @Override
    public void onZoomSDKLogoutResult(long result) {
        for (JainamZoomAuthenticationListener listener : mListenerList) {
            if (listener != null) {
                listener.onZoomSDKLogoutResult(result);
            }
        }
    }

    /**
     * Zoom identity expired, please re-login or generate new zoom access token via REST api
     */
    @Override
    public void onZoomIdentityExpired() {
        for (JainamZoomAuthenticationListener listener : mListenerList) {
            if (listener != null) {
                listener.onZoomIdentityExpired();
            }
        }
    }

    /**
     * ZOOM jwt token is expired, please generate a new jwt token.
     */
    @Override
    public void onZoomAuthIdentityExpired() {
        for (JainamZoomAuthenticationListener listener : mListenerList) {
            if (listener != null) {
                listener.onZoomAuthIdentityExpired();
            }
        }
    }

    public interface JainamZoomAuthenticationListener {
        void onZoomSDKLoginResult(long result);

        void onZoomSDKLogoutResult(long result);

        void onZoomIdentityExpired();

        void onZoomAuthIdentityExpired();
    }
}

