package com.medizine.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.medizine.MedizineApp;
import com.medizine.R;
import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.model.Feedback;
import com.medizine.network.NetworkService;
import com.medizine.network.RxNetwork;
import com.medizine.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

public class ContactUsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ContactUsActivity.class.getSimpleName();

    @BindView(R.id.callButton)
    Button callButton;
    @BindView(R.id.requestButton)
    Button requestButton;
    @BindView(R.id.etRequest)
    EditText etRequest;

    private CompositeDisposable networkDisposable;

    public static void launchContactUsActivity(@NonNull Context context) {
        Intent intent = new Intent(context, ContactUsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        ButterKnife.bind(this);

        setTitle(getString(R.string.title_contact_us));
        networkDisposable = new CompositeDisposable();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        callButton.setOnClickListener(this);
        requestButton.setOnClickListener(this);

        Drawable leftDrawable = AppCompatResources.getDrawable(this, R.drawable.call);
        callButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
    }

    private void clearRequestArea() {
        etRequest.setText(null);
    }

    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.requestButton:
                if (etRequest.getText().toString().isEmpty()) {
                    Toast.makeText(this, getResources().getString(R.string.please_enter_request), Toast.LENGTH_SHORT).show();
                } else {
                    sendRequest(etRequest.getText().toString());
                }
                break;
            case R.id.callButton:
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+917979800075"));
                startActivity(callIntent);
                break;
        }
    }

    private void sendRequest(@NonNull String request) {
        Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
        clearRequestArea();

        /*
        final Feedback requestFeedback = new Feedback(request);

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().sendFeedback(requestFeedback);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .retryWhen(throwableObservable -> throwableObservable.zipWith(Observable.range(1, 3),
                        (BiFunction<Throwable, Integer, Observable<?>>) (throwable, integer) -> {
                            if (throwable instanceof NetworkUnavailableException || integer > 2) {
                                throw new NetworkUnavailableException();
                            }
                            return Observable.just("");
                        }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.getData() != null) {
                        Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
                        clearRequestArea();
                    } else {
                        Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.request_not_sent), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    if (throwable instanceof NetworkUnavailableException) {
                        Log.d(TAG, "throwable " + throwable);
                        Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.logException(TAG, throwable);
                    }
                });
        networkDisposable.add(disposable);
        */
    }
}
