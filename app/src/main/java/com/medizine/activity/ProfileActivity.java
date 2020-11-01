package com.medizine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.medizine.R;
import com.medizine.db.StorageService;
import com.medizine.model.entity.User;
import com.medizine.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

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
//    @BindView(R.id.idProof)
//    ImageView idProof;
//    @BindView(R.id.iv_profile)
//    ImageView profilePic;
//    @BindView(R.id.emptyIdProof)
//    TextView emptyIdProof;
//    @BindView(R.id.verifiedIdProof)
//    ImageView verifiedIdProof;
//    @BindView(R.id.identityProofWidget)
//    SectionWidget identityProofWidget;

    public static void launchProfileActivity(Context context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.profile));

        fetchUserData();
    }

    private void fetchUserData() {
        renderData(StorageService.getInstance().getUser());
    }

    public void renderData(User user) {
        name.setText(user.getName());

        phone.setText(user.getCountryCode() + " " + user.getPhoneNumber());
        phone.setOnClickListener(v -> Utils.dialPhone(ProfileActivity.this, user.getCountryCode() + user.getPhoneNumber()));

        // Set DOB
        if (Utils.isNullOrEmpty(user.getDob())) {
            dob.setText("-");
        } else {
            dob.setText(user.getDob());
        }

        // Set gender
        if (Utils.isNullOrEmpty(user.getGender())) {
            gender.setText("-");
        } else {
            gender.setText(user.getGender());
        }

        // Set Email
        if (Utils.isNullOrEmpty(user.getEmailAddress())) {
            email.setText("-");
        } else {
            email.setText(user.getEmailAddress());
        }

        /*
        //Set ID Proof
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
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
