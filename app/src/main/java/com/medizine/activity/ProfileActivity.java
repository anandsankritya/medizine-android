package com.medizine.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.medizine.R;
import com.medizine.db.StorageService;
import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.model.entity.User;
import com.medizine.network.NetworkService;
import com.medizine.network.RetryOperator;
import com.medizine.network.RxNetwork;
import com.medizine.utils.ImageUtils;
import com.medizine.utils.Utils;
import com.medizine.widgets.SectionWidget;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.medizine.Constants.LOAD_CURRENT_USER;
import static com.medizine.Constants.MODULE_ID;
import static com.medizine.Constants.PROFILE_VISIBLE_TO_PAGE_ADMIN;
import static com.medizine.Constants.REQUEST_EDIT_PROFILE;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.gender)
    TextView gender;
    @BindView(R.id.dateOfBirth)
    TextView dob;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.idProof)
    ImageView idProof;
    @BindView(R.id.iv_profile)
    ImageView profilePic;
    @BindView(R.id.emptyIdProof)
    TextView emptyIdProof;
    @BindView(R.id.verifiedIdProof)
    ImageView verifiedIdProof;
    @BindView(R.id.identityProofWidget)
    SectionWidget identityProofWidget;

    @Nullable
    private String userId;
    private boolean loadCurrentUser;
    private boolean isProfileVisibleToPageAdmin = false;

    public static void openProfileActivity(Context context, boolean loadCurrentUser, @Nullable String userId, boolean isProfileVisibleToPageAdmin) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(MODULE_ID, userId);
        intent.putExtra(LOAD_CURRENT_USER, loadCurrentUser);
        intent.putExtra(PROFILE_VISIBLE_TO_PAGE_ADMIN, isProfileVisibleToPageAdmin);
        context.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MODULE_ID, userId);
        outState.putBoolean(LOAD_CURRENT_USER, loadCurrentUser);
        outState.putBoolean(PROFILE_VISIBLE_TO_PAGE_ADMIN, isProfileVisibleToPageAdmin);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        if (savedInstanceState != null) {
            userId = savedInstanceState.getString(MODULE_ID);
            loadCurrentUser = savedInstanceState.getBoolean(LOAD_CURRENT_USER);
        } else {
            loadCurrentUser = getIntent().getBooleanExtra(LOAD_CURRENT_USER, false);
            userId = getIntent().getStringExtra(MODULE_ID);
            isProfileVisibleToPageAdmin = getIntent().getBooleanExtra(PROFILE_VISIBLE_TO_PAGE_ADMIN, false);
        }

        if (isProfileVisibleToPageAdmin) {
            getSupportActionBar().setTitle(R.string.member_profile);
            identityProofWidget.setVisibility(View.GONE);
        }
        fetchUserData();
    }

    private void fetchUserData() {
        if (loadCurrentUser) {
            renderData(StorageService.getInstance().getUser());
        } else {
            loadUserById();
        }
    }

    private void loadUserById() {
        RxNetwork.observeNetworkConnectivity(this)
                .flatMapSingle(connectivity -> {
                    if (connectivity.isAvailable()) {
                        return NetworkService.getInstance().getUserProfile(userId);
                    } else {
                        throw new NetworkUnavailableException();
                    }
                })
                .compose(RetryOperator::jainamRetryWhen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.getData() != null) {
                                renderData(response.getData());
                            }
                        }, throwable -> {
                            if (throwable instanceof NetworkUnavailableException) {
                                Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.logException(TAG, throwable);
                            }
                        }

                );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    public void renderData(User user) {
        name.setText(user.getName());

        phone.setText(user.getCountryCode() + " " + user.getMobile());
        phone.setOnClickListener(v -> {
            Utils.dialPhone(ProfileActivity.this, user.getCountryCode() + user.getMobile());
        });

        // Set DOB
        if (Utils.isNullOrEmpty(user.getDateOfBirth())) {
            dob.setText("-");
        } else {
            dob.setText(user.getDateOfBirth());
        }

        // Set gender
        if (Utils.isNullOrEmpty(user.getGender())) {
            gender.setText("-");
        } else {
            gender.setText(user.getGender());
        }

        // Set Email
        if (Utils.isNullOrEmpty(user.getEmail())) {
            email.setText("-");
        } else {
            email.setText(user.getEmail());
        }

        // Set ID Proof
        if (!Utils.isNullOrEmpty(user.getIdProofAsString())) {
            ImageUtils.loadPicInView(this, user.getIdProofAsString(), idProof);
            if (user.getIdProofVerified()) {
                verifiedIdProof.setVisibility(View.VISIBLE);
            }
            emptyIdProof.setVisibility(View.GONE);
        } else {
            emptyIdProof.setVisibility(View.VISIBLE);
        }

        // Set Profile Pic
        if (!Utils.isNullOrEmpty(user.getProfilePicAsString())) {
            ImageUtils.loadPicInBorderedCircularView(this, user.getProfilePicAsString(), profilePic, 0, Utils.dpToPixels(2.0f), getResources().getColor(R.color.white));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //Show these menus when current user profile is visible else hide
        menu.findItem(R.id.menuEdit).setVisible(loadCurrentUser);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menuEdit:
                Intent intent = new Intent(this, EditProfileActivity.class);
                startActivityForResult(intent, REQUEST_EDIT_PROFILE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_PROFILE) {
            if (resultCode == RESULT_OK) {
                fetchUserData();
            }
        }
    }
}
