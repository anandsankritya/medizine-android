package com.medizine.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medizine.R;
import com.medizine.model.Resource;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.disposables.CompositeDisposable;

public class BaseActivity extends AppCompatActivity {

    @Nullable
    protected CompositeDisposable rxBusEventDisposable;
    @Nullable
    protected CompositeDisposable networkDisposable;
    protected RxPermissions rxPermissions;
    @Nullable
    private ProgressDialog progressDialog;
    private boolean progressDialogVisibile = false;
    private Handler handler;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxBusEventDisposable = new CompositeDisposable();
        networkDisposable = new CompositeDisposable();

        handler = new Handler(Looper.getMainLooper());
        rxPermissions = new RxPermissions(this);

        // Setup Progress Bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.setCancelable(false);
        if (savedInstanceState != null) {
            progressDialogVisibile = savedInstanceState.getBoolean("progressDialogVisibile");
        } else {
            progressDialogVisibile = false;
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    protected FirebaseUser getCurrentFirebaseUser() {
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth.getCurrentUser();
    }

    protected boolean isUserSignedIn() {
        return getCurrentFirebaseUser() != null;
    }

    @Override
    protected void onDestroy() {
        if (rxBusEventDisposable != null) {
            rxBusEventDisposable.clear();

            rxBusEventDisposable = null;
        }
        if (networkDisposable != null) {
            networkDisposable.clear();
            networkDisposable = null;
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        super.onDestroy();
    }

    protected void showProgressBar() {
        toggleProgressDialog(true);
    }

    protected void showProgressBar(Resource.Status status) {
        switch (status) {
            case DELETING:
                progressDialog.setMessage(getString(R.string.deleting));
                break;
            case EDITING:
                progressDialog.setMessage(getString(R.string.editing));
                break;
            case UPLOADING:
                progressDialog.setMessage(getString(R.string.uploading));
        }
        showProgressBar();
    }

    protected void hideProgressBar() {
        toggleProgressDialog(false);
    }

    protected void setProgressDialogMessage(String message) {
        if (progressDialog != null)
            progressDialog.setMessage(message);
    }

    private void toggleProgressDialog(final boolean enable) {
        handler.post(() -> {
            if (progressDialog != null) {
                if (enable) {
                    progressDialog.show();
                    progressDialogVisibile = true;
                } else {
                    progressDialog.dismiss();
                    progressDialogVisibile = false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (progressDialogVisibile && progressDialog != null) {
            progressDialog.show();
        }
    }

    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("progressDialogVisibile", progressDialogVisibile);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}