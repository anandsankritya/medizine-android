package com.medizine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medizine.R;
import com.medizine.adapter.AppointmentListAdapter;
import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.network.NetworkService;
import com.medizine.network.RetryOperator;
import com.medizine.network.RxNetwork;
import com.medizine.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AppointmentListActivity extends BaseActivity {
    private static final String TAG = AppointmentListActivity.class.getSimpleName();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private AppointmentListAdapter mAppointmentListAdapter;

    public static void launchAppointmentListActivity(Context context) {
        Intent intent = new Intent(context, AppointmentListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.appointments));
        }

        mAppointmentListAdapter = new AppointmentListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAppointmentListAdapter);

        if (Utils.isUserTypeNormal()) {
            fetchAllAppointmentsByUserId();
        } else if (Utils.isUserTypeDoctor()) {
            fetchAllAppointmentsByDoctorId();
        }
    }

    private void fetchAllAppointmentsByDoctorId() {
        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();
        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getAllAppointmentsByDoctorId(Utils.getDoctorID());
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            mAppointmentListAdapter.setList(response.getData());
                            hideProgressBar();
                            setProgressDialogMessage(getString(R.string.saving));
                        }, throwable -> {
                            hideProgressBar();
                            setProgressDialogMessage(getString(R.string.saving));
                            if (throwable instanceof NetworkUnavailableException) {
                                Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.logException(TAG, throwable);
                            }
                        }
                );
    }

    private void fetchAllAppointmentsByUserId() {
        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();
        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getAllAppointmentsByUserId(Utils.getUserID());
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            mAppointmentListAdapter.setList(response.getData());
                            hideProgressBar();
                            setProgressDialogMessage(getString(R.string.saving));
                        }, throwable -> {
                            hideProgressBar();
                            setProgressDialogMessage(getString(R.string.saving));
                            if (throwable instanceof NetworkUnavailableException) {
                                Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.logException(TAG, throwable);
                            }
                        }
                );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}