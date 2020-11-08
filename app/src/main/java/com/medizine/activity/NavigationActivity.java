package com.medizine.activity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medizine.R;
import com.medizine.adapter.DoctorListAdapter;
import com.medizine.adapter.DrawerMenuListAdapter;
import com.medizine.db.StorageService;
import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.model.entity.Doctor;
import com.medizine.model.entity.User;
import com.medizine.model.enums.DrawerMenuItem;
import com.medizine.network.NetworkService;
import com.medizine.network.RetryOperator;
import com.medizine.network.RxNetwork;
import com.medizine.utils.ImageUtils;
import com.medizine.utils.Utils;
import com.medizine.widgets.DrawerLocker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("Registered")
public class NavigationActivity extends BaseActivity implements DrawerLocker {

    public static final String TAG = NavigationActivity.class.getSimpleName();

    protected ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.leftDrawerLv)
    ListView mDrawerList;
    @BindView(R.id.usernameTv)
    TextView usernameTv;
    @BindView(R.id.profilePic)
    ImageView profilePic;
    @BindView(R.id.drawerRoot)
    ScrollView drawerRoot;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<DrawerMenuItem> mMenuItems;
    private DoctorListAdapter doctorListAdapter;

    protected void onCreateDrawer() {
        setupDrawer();
        if (Utils.isUserTypeNormal()) {
            LiveData<User> userLiveData = StorageService.getInstance().getMedizineDatabase().userDao().getLiveData();
            userLiveData.observe(this, user -> {
                if (user != null) {
                    usernameTv.setText(Utils.isNullOrEmpty(user.getName()) ? user.getCountryCode() + user.getPhoneNumber() : user.getName());
                    ImageUtils.loadPicInBorderedCircularView(NavigationActivity.this, "", profilePic, R.drawable.profile_pic_white_border_circle,
                            Utils.dpToPixels(1.0f), getResources().getColor(R.color.white));

                    fetchAllDoctors();
                }
            });
        } else if (Utils.isUserTypeDoctor()) {
            LiveData<Doctor> doctorLiveData = StorageService.getInstance().getMedizineDatabase().doctorDao().getLiveData();
            doctorLiveData.observe(this, doctor -> {
                if (doctor != null) {
                    usernameTv.setText(Utils.isNullOrEmpty(doctor.getName()) ? doctor.getCountryCode() + doctor.getPhoneNumber() : doctor.getName());
                    ImageUtils.loadPicInBorderedCircularView(NavigationActivity.this, "", profilePic, R.drawable.profile_pic_white_border_circle,
                            Utils.dpToPixels(1.0f), getResources().getColor(R.color.white));
                    //fetchAllAppointments();
                }
            });
        } else {
            Utils.logOutUser();
        }
    }

    private void fetchAllDoctors() {
        setProgressDialogMessage(getString(R.string.loading));
        showProgressBar();

        Disposable disposable = RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getAllDoctors();
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            List<Doctor> doctors = new ArrayList<>();
                            if (response.getData() != null) {
                                doctors = response.getData();
                            }
                            doctorListAdapter.setList(doctors);
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

    private void setupDrawer() {
        // Decide which items to show
        mMenuItems = new ArrayList<>();
        mMenuItems.add(DrawerMenuItem.APPOINTMENTS);
        mMenuItems.add(DrawerMenuItem.CONTACT_US);
        mMenuItems.add(DrawerMenuItem.SHARE_APP);
        mMenuItems.add(DrawerMenuItem.LOG_OUT);

        mDrawerList.setAdapter(new DrawerMenuListAdapter(this, mMenuItems));

        mDrawerList.setOnItemClickListener(new NavigationActivity.DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_closed) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        adjustDrawerWidth();
    }

    private void adjustDrawerWidth() {
        float screenWidth = (float) Utils.getScreenWidth(NavigationActivity.this);
        int requiredWidth = (int) (screenWidth * ((float) 7 / 10));

        ViewGroup.LayoutParams layoutParams = drawerRoot.getLayoutParams();
        layoutParams.width = requiredWidth;
        drawerRoot.setLayoutParams(layoutParams);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        onCreateDrawer();
        //Init Rv Doctor
        doctorListAdapter = new DoctorListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(doctorListAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.profilePic, R.id.usernameTv})
    public void profileViewClicked() {
        if (Utils.isUserTypeNormal()) {
            UserProfileActivity.launchUserProfileActivity(NavigationActivity.this, null);
        } else if (Utils.isUserTypeDoctor()) {
            DoctorProfileActivity.launchDoctorProfileActivity(NavigationActivity.this, null);
        }
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setDrawerEnabled(boolean enabled) {
        if (enabled) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
        }
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        mDrawerLayout.setDrawerLockMode(lockMode);
        mDrawerToggle.setDrawerIndicatorEnabled(enabled);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (mMenuItems.get(position)) {
                case APPOINTMENTS:
                    AppointmentListActivity.launchAppointmentListActivity(NavigationActivity.this);
                    break;
                case CONTACT_US:
                    ContactUsActivity.launchContactUsActivity(NavigationActivity.this);
                    break;
                case SHARE_APP:
                    Utils.shareApp();
                    break;
                case LOG_OUT:
                    Utils.logOutUser();
            }
            mDrawerLayout.closeDrawers();
        }
    }

}
