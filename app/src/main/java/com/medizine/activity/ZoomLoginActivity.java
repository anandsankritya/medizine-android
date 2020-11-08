package com.medizine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.medizine.Constants;
import com.medizine.R;
import com.medizine.zoom.EmailUserLoginHelper;
import com.medizine.zoom.UserLoginCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;

public class ZoomLoginActivity extends BaseActivity implements UserLoginCallback.JainamZoomAuthenticationListener {

    private final static String TAG = ZoomLoginActivity.class.getSimpleName();

    @BindView(R.id.zoomUserEmailId)
    EditText etZoomUserName;
    @BindView(R.id.zoomPassword)
    EditText etZoomPassword;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.layoutProgress)
    LinearLayout mLayoutProgress;

    public static void launchZoomLoginActivity(Context context) {
        Intent intent = new Intent(context, ZoomLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void launchZoomLoginActivityForResult(Context context) {
        Intent intent = new Intent(context, ZoomLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((Activity) context).startActivityForResult(intent, Constants.REQUEST_ZOOM_LOGIN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom_login_activity);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.link_zoom);
        }

        btnSignIn.setOnClickListener(view -> zoomLogin());
    }

    private void zoomLogin() {
        String userName = etZoomUserName.getText().toString().trim();
        String password = etZoomPassword.getText().toString().trim();

        if (!(userName.length() > 0 && password.length() > 0)) {
            Toast.makeText(this, R.string.zoom_id_pass_validation_msg, Toast.LENGTH_LONG).show();
            return;
        }

        if (!(EmailUserLoginHelper.getInstance().login(userName, password) == ZoomApiError.ZOOM_API_ERROR_SUCCESS)) {
            Log.d(TAG, "ZoomSDK has not been initialized successfully or sdk is logging in.");
        } else {
            btnSignIn.setVisibility(View.GONE);
            mLayoutProgress.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onZoomSDKLoginResult(long result) {
        if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            navigateToPreviousActivity();
        } else if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_USER_NOT_EXIST) {
            Toast.makeText(this, R.string.no_zoom_user_found, Toast.LENGTH_SHORT).show();
        } else if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_WRONG_PASSWORD) {
            Toast.makeText(this, R.string.zoom_id_pass_validation_msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.oops_something_went_wrong), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Login failed result code = " + result);
        }
        btnSignIn.setVisibility(View.VISIBLE);
        mLayoutProgress.setVisibility(View.GONE);
    }

    private void addListener() {
        UserLoginCallback.getInstance().addListener(this);
    }

    private void removeListener() {
        UserLoginCallback.getInstance().removeListener(this);
    }

    private void navigateToPreviousActivity() {
        removeListener();
        setResult(Activity.RESULT_OK, null);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener();
    }

    @Override
    public void onZoomSDKLogoutResult(long result) {
    }

    @Override
    public void onZoomIdentityExpired() {
    }

    @Override
    public void onZoomAuthIdentityExpired() {
    }

}
