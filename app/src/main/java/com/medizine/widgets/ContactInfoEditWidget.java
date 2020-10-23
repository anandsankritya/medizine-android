package com.medizine.widgets;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jainam.android.R;
import com.jainam.android.model.PhoneNumber;
import com.jainam.android.model.SocialLinks;
import com.jainam.android.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ContactInfoEditWidget extends FrameLayout {
    private final String youtubeRegex = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)(\\.com)?\\/.+";

    private EditText etEmail;
    private EditText etYoutube;
    private EditText etWebsite;
    private EditText etCountryCode;
    private EditText etMobileNumber;
    private LinearLayout linearLayoutPhone;
    private ImageView ivAddPhone;
    private EditText etWhatsAppCountryCode;
    private EditText etWhatsAppMobileNumber;
    private EditText etTelegramUserName;

    private List<PhoneNumber> mPhoneNumberEtList;
    private LayoutInflater mLayoutInflater;

    public ContactInfoEditWidget(Context context) {
        this(context, null);
    }

    public ContactInfoEditWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactInfoEditWidget(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutInflater.inflate(R.layout.widget_edit_contact_info, this, true);
        etEmail = findViewById(R.id.etEmail);
        etYoutube = findViewById(R.id.etYoutube);
        etWebsite = findViewById(R.id.etWebsite);
        etCountryCode = findViewById(R.id.etCountryCode);
        etCountryCode.setHint(Utils.getCountryCode());
        etMobileNumber = findViewById(R.id.etMobileNumber);
        linearLayoutPhone = findViewById(R.id.linearLayoutPhone);
        ivAddPhone = findViewById(R.id.ivAddPhone);
        etWhatsAppCountryCode = findViewById(R.id.etWhatsAppCountryCode);
        etWhatsAppCountryCode.setHint(Utils.getCountryCode());
        etWhatsAppMobileNumber = findViewById(R.id.etWhatsAppMobileNumber);
        etTelegramUserName = findViewById(R.id.etTelegramUserName);
        mPhoneNumberEtList = new ArrayList<>();

        ivAddPhone.setOnClickListener(v -> {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 24, 0, 0);
            final View addPhone = mLayoutInflater.inflate(R.layout.layout_phone, null);
            final EditText etCountryCode = addPhone.findViewById(R.id.etCountryCode);
            etCountryCode.setHint(Utils.getCountryCode());
            final EditText etPhoneNumber = addPhone.findViewById(R.id.etPhoneNumber);
            PhoneNumber phoneNumber = new PhoneNumber(etCountryCode, etPhoneNumber);
            mPhoneNumberEtList.add(phoneNumber);
            addPhone.setLayoutParams(params);
            linearLayoutPhone.addView(addPhone);

            ImageView ivRemovePhone = addPhone.findViewById(R.id.ivRemovePhone);
            ivRemovePhone.setOnClickListener(view -> {
                linearLayoutPhone.removeView(addPhone);
                mPhoneNumberEtList.remove(phoneNumber);
            });
        });
    }

    public void setData(@Nullable SocialLinks socialLinks, @Nullable List<String> phoneNumbers) {
        if (socialLinks != null) {
            etYoutube.setText(socialLinks.getYoutube());
            etEmail.setText(socialLinks.getEmail());
            etWebsite.setText(socialLinks.getWebsite());
            etWhatsAppCountryCode.setText(Utils.getCountryCodeFromString(socialLinks.getWhatsApp()));
            etWhatsAppMobileNumber.setText(Utils.getMobileNumberFromString(socialLinks.getWhatsApp()));
            etTelegramUserName.setText(socialLinks.getTelegram());
        }

        if (!Utils.isListEmpty(phoneNumbers)) {
            etCountryCode.setText(Utils.getCountryCodeFromString(phoneNumbers.get(0)));
            etMobileNumber.setText(Utils.getMobileNumberFromString(phoneNumbers.get(0)));

            mPhoneNumberEtList.clear();
            linearLayoutPhone.removeAllViews();
            for (int i = 1; i < phoneNumbers.size(); i++) {
                final View addPhone = mLayoutInflater.inflate(R.layout.layout_phone, null);

                final EditText etCountryCode = addPhone.findViewById(R.id.etCountryCode);
                final EditText etPhoneNumber = addPhone.findViewById(R.id.etPhoneNumber);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 24, 0, 0);
                addPhone.setLayoutParams(params);
                etCountryCode.setText(Utils.getCountryCodeFromString(phoneNumbers.get(i)));
                etPhoneNumber.setText(Utils.getMobileNumberFromString(phoneNumbers.get(i)));
                PhoneNumber phoneNumber = new PhoneNumber(etCountryCode, etPhoneNumber);
                mPhoneNumberEtList.add(phoneNumber);
                linearLayoutPhone.addView(addPhone);

                ImageView ivRemovePhone = addPhone.findViewById(R.id.ivRemovePhone);
                ivRemovePhone.setOnClickListener(v -> {
                    linearLayoutPhone.removeView(addPhone);
                    mPhoneNumberEtList.remove(phoneNumber);
                });
            }
        }
    }

    public boolean validateCountryCode() {
        boolean isValid = true;

        if (!etCountryCode.getText().toString().startsWith("+")) {
            isValid = setError(etCountryCode, getContext().getString(R.string.enter_country_code));
        } else if (!Utils.isValidCountryCode(etCountryCode.getText().toString())) {
            isValid = setError(etCountryCode, getContext().getString(R.string.enter_country_code));
        }

        for (PhoneNumber phoneNumber : mPhoneNumberEtList) {
            EditText editText = phoneNumber.getCountryCode();
            if (!editText.getText().toString().startsWith("+")) {
                isValid = setError(editText, getContext().getString(R.string.enter_country_code));
            } else if (!Utils.isValidCountryCode(editText.getText().toString())) {
                isValid = setError(editText, getContext().getString(R.string.enter_country_code));
            }
        }

        return isValid;
    }


    public boolean validatePhoneNumbers() {
        boolean isValid = true;

        if (!Utils.isPhoneValid(etMobileNumber.getText().toString())) {
            isValid = setError(etMobileNumber, getContext().getString(R.string.phone_validation_msg));
        }

        for (PhoneNumber phoneNumber : mPhoneNumberEtList) {
            EditText editText = phoneNumber.getMobileNumber();
            if (!Utils.isPhoneValid(editText.getText().toString())) {
                isValid = setError(editText, getContext().getString(R.string.phone_validation_msg));
            }
        }

        return isValid;
    }

    public boolean validateSocialLinks() {
        boolean isValid = true;
        if (!etYoutube.getText().toString().isEmpty() && !etYoutube.getText().toString().trim().matches(youtubeRegex)) {
            etYoutube.requestFocus();
            etYoutube.setError(getContext().getString(R.string.youtube_url_validation_msg));
            showToast(getContext().getString(R.string.youtube_url_validation_msg));
            isValid = false;
        }
        if (!etEmail.getText().toString().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            etEmail.requestFocus();
            etEmail.setError(getContext().getString(R.string.temple_email_validation_msg));
            showToast(getContext().getString(R.string.temple_email_validation_msg));
            isValid = false;
        }
        if (!etWebsite.getText().toString().isEmpty() && !Patterns.WEB_URL.matcher(etWebsite.getText().toString().trim()).matches()) {
            etWebsite.requestFocus();
            etWebsite.setError(getContext().getString(R.string.website_url_validation_msg));
            showToast(getContext().getString(R.string.website_url_validation_msg));
            isValid = false;
        }

        if (!etWhatsAppCountryCode.getText().toString().isEmpty()) {
            if (!etWhatsAppCountryCode.getText().toString().startsWith("+")) {
                isValid = setError(etWhatsAppCountryCode, getContext().getString(R.string.whatsapp_country_code_validation_msg));
            } else if (!Utils.isValidCountryCode(etWhatsAppCountryCode.getText().toString())) {
                isValid = setError(etWhatsAppCountryCode, getContext().getString(R.string.whatsapp_country_code_validation_msg));
            }
        }

        if (!etWhatsAppMobileNumber.getText().toString().isEmpty()) {
            if (!Utils.isPhoneValid(etWhatsAppMobileNumber.getText().toString())) {
                isValid = setError(etWhatsAppMobileNumber, getContext().getString(R.string.whatsapp_validation_msg));
            }
        }

        if (!etTelegramUserName.getText().toString().isEmpty() && !Utils.isValidUserName(etTelegramUserName.getText().toString())) {
            etTelegramUserName.requestFocus();
            etTelegramUserName.setError(getContext().getString(R.string.telegram_user_name_validation_msg));
            showToast(getContext().getString(R.string.telegram_user_name_validation_msg));
            isValid = false;
        }

        return isValid;
    }

    @Nullable
    public List<String> getPhoneNumbers() {
        List<String> listPhoneNumber = new ArrayList<>();

        String phone = getPhoneNumber(etCountryCode, etMobileNumber);
        if (Utils.isNotEmpty(phone)) {
            listPhoneNumber.add(phone);
        }

        for (PhoneNumber phoneNumber : mPhoneNumberEtList) {
            String phoneString = getPhoneNumber(phoneNumber.getCountryCode(), phoneNumber.getMobileNumber());
            if (Utils.isNotEmpty(phoneString)) {
                listPhoneNumber.add(phoneString);
            }
        }
        return listPhoneNumber.isEmpty() ? null : listPhoneNumber;
    }

    public SocialLinks getSocialLinks() {
        String email = etEmail.getText().toString().trim();
        String website = etWebsite.getText().toString().trim();
        String youtube = etYoutube.getText().toString().trim();
        String whatsApp = getPhoneNumber(etWhatsAppCountryCode, etWhatsAppMobileNumber);
        String telegram = etTelegramUserName.getText().toString().trim();
        if (!Utils.isNullOrEmpty(youtube)) {
            String[] tokens = youtube.split("/");
            youtube = "https://youtu.be/" + youtube.split("/")[tokens.length - 1];
        }

        if (Utils.isNullOrEmpty(email) && Utils.isNullOrEmpty(website) && Utils.isNullOrEmpty(youtube) && Utils.isNullOrEmpty(whatsApp) && Utils.isNullOrEmpty(telegram)) {
            return null;
        }

        SocialLinks socialLinks = new SocialLinks();
        socialLinks.setEmail(email);
        socialLinks.setWebsite(website);
        socialLinks.setYoutube(youtube);
        socialLinks.setWhatsApp(whatsApp);
        socialLinks.setTelegram(telegram);
        return socialLinks;
    }

    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    @Nullable
    private String getPhoneNumber(EditText etCountryCode, EditText etMobileNumber) {
        String countryCode = etCountryCode.getText().toString().trim();
        String mobile = etMobileNumber.getText().toString().trim();

        if (!Utils.isNullOrEmpty(countryCode) && !Utils.isNullOrEmpty(mobile)) {
            return countryCode + mobile;
        } else if (!Utils.isNullOrEmpty(mobile)) {
            return mobile;
        } else {
            return null;
        }
    }

    private boolean setError(EditText editText, String errorMessage) {
        editText.requestFocus();
        editText.setError(errorMessage);
        return false;
    }

}
