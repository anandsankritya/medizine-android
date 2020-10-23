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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.tvLabelBio)
    TextView tvLabelBio;
    @BindView(R.id.tvBio)
    TextView tvBio;
    @BindView(R.id.gender)
    TextView gender;
    @BindView(R.id.dateOfBirth)
    TextView dob;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.sect)
    TextView sect;
    @BindView(R.id.panth)
    TextView panth;
    @BindView(R.id.fatherName)
    TextView tvFatherName;
    @BindView(R.id.motherName)
    TextView tvMotherName;
    @BindView(R.id.fatherOccupation)
    TextView tvFatherOccupation;
    @BindView(R.id.motherOccupation)
    TextView tbMotherOccupation;
    @BindView(R.id.familyType)
    TextView tvFamilyType;
    @BindView(R.id.numberOfBrothers)
    TextView tvNumberOfBrothers;
    @BindView(R.id.noOfSisters)
    TextView tvNoOfSisters;
    @BindView(R.id.familyIncome)
    TextView tvFamilyIncome;
    @BindView(R.id.selfGotra)
    TextView tvSelfGotra;
    @BindView(R.id.mothersGotra)
    TextView tvMothersGotra;
    @BindView(R.id.dadijisGotra)
    TextView tvDadijisGotra;
    @BindView(R.id.nanijisGotra)
    TextView tvNanijisGotra;
    @BindView(R.id.idProof)
    ImageView idProof;
    @BindView(R.id.iv_profile)
    ImageView profilePic;
    @BindView(R.id.emptyIdProof)
    TextView emptyIdProof;
    @BindView(R.id.altMobile)
    TextView altMobile;
    @BindView(R.id.verifiedIdProof)
    ImageView verifiedIdProof;
    @BindView(R.id.locationWidget)
    LocationWidget locationWidget;
    @BindView(R.id.educationWidget)
    EducationWidget educationWidget;
    @BindView(R.id.identityProofWidget)
    SectionWidget identityProofWidget;
    @BindView(R.id.familyWidget)
    SectionWidget familyWidget;
    @BindView(R.id.whatsapp)
    TextView tvWhatsApp;
    @BindView(R.id.telegram)
    TextView tvTelegram;

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
        getSupportActionBar().setTitle(R.string.profile);

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
            locationWidget.setVisibility(View.GONE);
            educationWidget.setVisibility(View.GONE);
            identityProofWidget.setVisibility(View.GONE);
            familyWidget.setVisibility(View.GONE);
        }
        locationWidget.setTitle(getString(R.string.home_address));
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
                .compose(JainamOperators::jainamRetryWhen)
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

        if (Utils.isNotEmpty(user.getBioAsString())) {
            tvBio.setText(user.getBioAsString());
            tvLabelBio.setVisibility(View.VISIBLE);
            tvBio.setVisibility(View.VISIBLE);
        } else {
            tvLabelBio.setVisibility(View.GONE);
            tvBio.setVisibility(View.GONE);
        }

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

        // Set sect
        if (Utils.isNullOrEmpty(user.getSect()) || user.getSect().equals(Sect.UNKNOWN.toString())) {
            sect.setText("-");
        } else {
            sect.setText(Sect.valueOf(user.getSect()).getLocaleString(getApplicationContext()));
        }

        // Set panth
        if (Utils.isNullOrEmpty(user.getPanth()) || user.getPanth().equals(Panth.UNKNOWN.toString())) {
            panth.setText("-");
        } else {
            panth.setText(Panth.valueOf(user.getPanth()).getLocaleString(getApplicationContext()));
        }

        // Set alternative phone number
        if (Utils.isNullOrEmpty(user.getAltMobile())) {
            altMobile.setText("-");
        } else {
            if (Utils.isNullOrEmpty(user.getAltCountryCode())) {
                altMobile.setText(user.getAltMobile());
            } else {
                altMobile.setText(user.getAltCountryCode() + " " + user.getAltMobile());
            }
            altMobile.setOnClickListener(v -> {
                Utils.dialPhone(ProfileActivity.this, user.getAltCountryCode() + user.getAltMobile());
            });
        }

        UserFamilyInfo userFamilyInfo = user.getUserFamilyInfo();
        if (userFamilyInfo != null) {
            // Set FatherName
            if (Utils.isNullOrEmpty(userFamilyInfo.getFatherName())) {
                tvFatherName.setText("-");
            } else {
                tvFatherName.setText(userFamilyInfo.getFatherName());
            }

            // Set FatherOccupation
            if (Utils.isNullOrEmpty(userFamilyInfo.getFatherOccupation())) {
                tvFatherOccupation.setText("-");
            } else {
                tvFatherOccupation.setText(userFamilyInfo.getFatherOccupation());
            }

            // SetMotherName
            if (Utils.isNullOrEmpty(userFamilyInfo.getMotherName())) {
                tvMotherName.setText("-");
            } else {
                tvMotherName.setText(userFamilyInfo.getMotherName());
            }

            // Set MotherOccupation
            if (Utils.isNullOrEmpty(userFamilyInfo.getMotherOccupation())) {
                tbMotherOccupation.setText("-");
            } else {
                tbMotherOccupation.setText(userFamilyInfo.getMotherOccupation());
            }

            // Set FamilyIncome
            if (Utils.isNullOrEmpty(userFamilyInfo.getFamilyIncome())) {
                tvFamilyIncome.setText("-");
            } else {
                tvFamilyIncome.setText(userFamilyInfo.getFamilyIncome());
            }

            // Set FamilyType
            if (Utils.isNullOrEmpty(userFamilyInfo.getFamilyType())) {
                tvFamilyType.setText("-");
            } else {
                tvFamilyType.setText(userFamilyInfo.getFamilyType());
            }

            // Set BrotherCount
            if (Utils.isNullOrEmpty(userFamilyInfo.getNumberOfBrothers())) {
                tvNumberOfBrothers.setText("-");
            } else {
                tvNumberOfBrothers.setText(userFamilyInfo.getNumberOfBrothers());
            }

            // Set SisterCount
            if (Utils.isNullOrEmpty(userFamilyInfo.getNoOfSisters())) {
                tvNoOfSisters.setText("-");
            } else {
                tvNoOfSisters.setText(userFamilyInfo.getNoOfSisters());
            }

            // Set SelfGotra
            if (Utils.isNullOrEmpty(userFamilyInfo.getSelfGotra())) {
                tvSelfGotra.setText("-");
            } else {
                tvSelfGotra.setText(userFamilyInfo.getSelfGotra());
            }

            // Set MotherGotra
            if (Utils.isNullOrEmpty(userFamilyInfo.getMothersGotra())) {
                tvMothersGotra.setText("-");
            } else {
                tvMothersGotra.setText(userFamilyInfo.getMothersGotra());
            }

            // Set NaniGotra
            if (Utils.isNullOrEmpty(userFamilyInfo.getNanijisGotra())) {
                tvNanijisGotra.setText("-");
            } else {
                tvNanijisGotra.setText(userFamilyInfo.getNanijisGotra());
            }

            // Set DadiGotra
            if (Utils.isNullOrEmpty(userFamilyInfo.getDadijisGotra())) {
                tvDadijisGotra.setText("-");
            } else {
                tvDadijisGotra.setText(userFamilyInfo.getDadijisGotra());
            }
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

        //Set address
        locationWidget.setLocation(user.getLocation(), user.getAddress(), user.getName());

        //Set Education
        educationWidget.setMembers(user.getUserEducationInfo());

        if (user.getSocialLinks() != null) {
            //Set Telegram link
            String telegramId = user.getSocialLinks().getTelegram();
            if (Utils.isNotEmpty(telegramId)) {
                String telegramLink = Uri.parse(Constants.TELEGRAM_LINK_PREFIX + telegramId).toString();
                tvTelegram.setText(telegramLink);
                tvTelegram.setOnClickListener(view -> Utils.openTelegram(ProfileActivity.this, telegramId));
            } else {
                tvTelegram.setText("-");
            }
            //Set WhatsApp Link
            String whatsAppNumber = user.getSocialLinks().getWhatsApp();
            if (Utils.isNotEmpty(whatsAppNumber)) {
                String whatsAppLink = Uri.parse(Constants.WHATS_APP_LINK_PREFIX + whatsAppNumber).toString();
                tvWhatsApp.setText(whatsAppLink);
                tvWhatsApp.setOnClickListener(view -> Utils.openWhatsApp(ProfileActivity.this, whatsAppNumber));
            } else {
                tvWhatsApp.setText("-");
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //Show these menus when current user profile is visible else hide
        menu.findItem(R.id.menuLogout).setVisible(loadCurrentUser);
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
            case R.id.menuLogout:
                AnalyticsUtils.Events.sendContentSelectionEvent(item.getItemId(), AnalyticsConstants.Event.LOGOUT, AnalyticsConstants.ScreenView.EDIT_PROFILE_ACTIVITY);
                Utils.logOutUser();
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
