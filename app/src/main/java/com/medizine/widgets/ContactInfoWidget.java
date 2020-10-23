package com.medizine.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.jainam.android.R;
import com.jainam.android.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ContactInfoWidget extends FrameLayout {
    String email = null;
    String website = null;
    String youtube = null;
    String whatsApp = null;
    String telegram = null;

    List<String> phoneNumbers = new ArrayList<>();

    ArrayList<View> views = new ArrayList<>();

    boolean isEmailSet, isWebsiteSet, isYoutubeSet, isWhatsAppSet, isTelegramSet, isPhoneNumbersSet;

    @Nullable
    ImageView phoneIcon;
    @Nullable
    ImageView websiteIcon;
    @Nullable
    ImageView emailIcon;
    @Nullable
    ImageView youtubeIcon;
    @Nullable
    ImageView whatsAppIcon;
    @Nullable
    ImageView telegramIcon;

    LinearLayout layoutContactInfo;

    public ContactInfoWidget(Context context) {
        this(context, null);
    }

    public ContactInfoWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactInfoWidget(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_contact_info, this, true);

        layoutContactInfo = findViewById(R.id.layoutContactInfo);
        layoutContactInfo.getLayoutParams().width = Utils.getScreenWidth(getContext());

        initViews();
    }

    public void setPhoneList(List<String> list) {
        if (list == null || list.isEmpty()) {
            phoneNumbers = new ArrayList<>();
            phoneIcon.setEnabled(false);
        } else {
            phoneNumbers = list;
            phoneIcon.setEnabled(true);
        }
        isPhoneNumbersSet = true;
        rearrangeViews();
    }

    public void setEmail(String value) {
        if (Utils.isNullOrEmpty(value)) {
            email = null;
            emailIcon.setEnabled(false);
        } else {
            email = value;
            emailIcon.setEnabled(true);
        }
        isEmailSet = true;
        rearrangeViews();
    }

    public void setWebsite(String value) {
        if (Utils.isNullOrEmpty(value)) {
            website = null;
            websiteIcon.setEnabled(false);
        } else {
            if (!value.startsWith("http://") && !value.startsWith("https://")) {
                website = "http://" + value;
            } else {
                website = value;
            }
            websiteIcon.setEnabled(true);
        }
        isWebsiteSet = true;
        rearrangeViews();
    }

    public void setYoutube(String value) {
        if (Utils.isNullOrEmpty(value)) {
            youtube = null;
            youtubeIcon.setEnabled(false);
        } else {
            youtube = value;
            youtubeIcon.setEnabled(true);
        }
        isYoutubeSet = true;
        rearrangeViews();
    }

    public void setWhatsApp(String value) {
        if (Utils.isNullOrEmpty(value)) {
            whatsApp = null;
            whatsAppIcon.setEnabled(false);
        } else {
            whatsApp = value;
            whatsAppIcon.setEnabled(true);
        }
        isWhatsAppSet = true;
        rearrangeViews();
    }

    public void setTelegram(String value) {
        if (Utils.isNullOrEmpty(value)) {
            telegram = null;
            telegramIcon.setEnabled(false);
        } else {
            telegram = value;
            telegramIcon.setEnabled(true);
        }
        isTelegramSet = true;
        rearrangeViews();
    }

    private View getPhoneIcon(boolean isEnabled) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_contact_info, null);
        TextView phoneText = view.findViewById(R.id.tvContactInfoLabel);
        phoneIcon = view.findViewById(R.id.ivContactInfoIcon);

        phoneText.setText(getContext().getString(R.string.phone));

        phoneIcon.setBackground(getContext().getDrawable(R.drawable.selector_phone));
        phoneIcon.setEnabled(isEnabled);
        phoneIcon.setOnClickListener(v -> {
            if (phoneNumbers.size() == 1) {
                Utils.dialPhone(getContext(), phoneNumbers.get(0));
            } else if (phoneNumbers.size() > 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                String[] array = new String[phoneNumbers.size()];
                for (int i = 0; i < phoneNumbers.size(); i++) {
                    array[i] = phoneNumbers.get(i);
                }

                builder.setItems(array, (dialog, position) -> {
                    Utils.dialPhone(getContext(), phoneNumbers.get(position));
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return addWidth(view);
    }

    private View getEmailIcon(boolean isEnabled) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_contact_info, null);
        TextView emailText = view.findViewById(R.id.tvContactInfoLabel);
        emailIcon = view.findViewById(R.id.ivContactInfoIcon);

        emailText.setText(getContext().getString(R.string.email));

        emailIcon.setBackground(getContext().getDrawable(R.drawable.selector_email));
        emailIcon.setEnabled(isEnabled);
        emailIcon.setOnClickListener(v -> {
            if (email != null) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", email, null));
                getContext().startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        return addWidth(view);
    }

    private View getWebsiteIcon(boolean isEnabled) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_contact_info, null);
        TextView websiteText = view.findViewById(R.id.tvContactInfoLabel);
        websiteIcon = view.findViewById(R.id.ivContactInfoIcon);

        websiteText.setText(getContext().getString(R.string.website));

        websiteIcon.setBackground(getContext().getDrawable(R.drawable.selector_website));
        websiteIcon.setEnabled(isEnabled);
        websiteIcon.setOnClickListener(v -> {
            if (website != null) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(website));
                getContext().startActivity(i);
            }
        });

        return addWidth(view);
    }

    private View getYouTubeIcon(boolean isEnabled) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_contact_info, null);
        TextView youtubeText = view.findViewById(R.id.tvContactInfoLabel);
        youtubeIcon = view.findViewById(R.id.ivContactInfoIcon);

        youtubeText.setText(getContext().getString(R.string.youtube));

        youtubeIcon.setBackground(getContext().getDrawable(R.drawable.selector_youtube));

        youtubeIcon.setEnabled(isEnabled);
        youtubeIcon.setOnClickListener(v -> {
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtube)));
        });

        return addWidth(view);
    }

    private View getWhatsAppIcon(boolean isEnabled) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_contact_info, null);
        TextView whatsAppText = view.findViewById(R.id.tvContactInfoLabel);
        whatsAppIcon = view.findViewById(R.id.ivContactInfoIcon);

        whatsAppText.setText(getContext().getString(R.string.whatsapp_number));

        whatsAppIcon.setBackground(getContext().getDrawable(R.drawable.selector_whatsapp));
        whatsAppIcon.setEnabled(isEnabled);
        whatsAppIcon.setOnClickListener(v -> {
            Utils.openWhatsApp((Activity) getContext(), whatsApp);
        });

        return addWidth(view);
    }

    private View getTelegramIcon(boolean isEnabled) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_contact_info, null);
        TextView telegramText = view.findViewById(R.id.tvContactInfoLabel);
        telegramIcon = view.findViewById(R.id.ivContactInfoIcon);

        telegramText.setText(getContext().getString(R.string.telegram));

        telegramIcon.setBackground(getContext().getDrawable(R.drawable.selecter_telegram));
        telegramIcon.setEnabled(isEnabled);
        telegramIcon.setOnClickListener(v -> Utils.openTelegram((Activity) getContext(), telegram));

        return addWidth(view);
    }

    private void rearrangeViews() {

        if (!isAllDataSet()) {
            return;
        }

        ArrayList<View> enabledViews = new ArrayList<>();
        ArrayList<View> disabledViews = new ArrayList<>();

        if (phoneIcon != null) {
            View view = getPhoneIcon(phoneIcon.isEnabled());
            if (phoneIcon.isEnabled()) {
                enabledViews.add(view);
            } else {
                disabledViews.add(view);
            }
        }
        if (whatsAppIcon != null) {
            View view = getWhatsAppIcon(whatsAppIcon.isEnabled());
            if (whatsAppIcon.isEnabled()) {
                enabledViews.add(view);
            } else {
                disabledViews.add(view);
            }
        }
        if (telegramIcon != null) {
            View view = getTelegramIcon(telegramIcon.isEnabled());
            if (telegramIcon.isEnabled()) {
                enabledViews.add(view);
            } else {
                disabledViews.add(view);
            }
        }
        if (youtubeIcon != null) {
            View view = getYouTubeIcon(youtubeIcon.isEnabled());
            if (youtubeIcon.isEnabled()) {
                enabledViews.add(view);
            } else {
                disabledViews.add(view);
            }
        }
        if (emailIcon != null) {
            View view = getEmailIcon(emailIcon.isEnabled());
            if (emailIcon.isEnabled()) {
                enabledViews.add(view);
            } else {
                disabledViews.add(view);
            }
        }
        if (websiteIcon != null) {
            View view = getWebsiteIcon(websiteIcon.isEnabled());
            if (websiteIcon.isEnabled()) {
                enabledViews.add(view);
            } else {
                disabledViews.add(view);
            }
        }

        enabledViews.addAll(disabledViews);

        layoutContactInfo.removeAllViews();

        for (View view : enabledViews) {
            layoutContactInfo.addView(view);
        }
    }

    private void initViews() {
        views.clear();

        views.add(getPhoneIcon(false));
        views.add(getWhatsAppIcon(false));
        views.add(getTelegramIcon(false));
        views.add(getYouTubeIcon(false));
        views.add(getEmailIcon(false));
        views.add(getWebsiteIcon(false));

        layoutContactInfo.removeAllViews();

        for (View view : views) {
            layoutContactInfo.addView(view);
        }
    }

    private boolean isAllDataSet() {
        return isEmailSet && isWebsiteSet && isYoutubeSet && isWhatsAppSet && isTelegramSet && isPhoneNumbersSet;
    }

    private View addWidth(View view) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Utils.dpToPixels(100), LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        return view;
    }
}
