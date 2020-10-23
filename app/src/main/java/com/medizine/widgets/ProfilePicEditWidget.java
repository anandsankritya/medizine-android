package com.medizine.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.jainam.android.R;
import com.jainam.android.utils.ImageUtils;
import com.jainam.android.utils.Utils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

public class ProfilePicEditWidget extends FrameLayout {
    private static final int REQUEST_CAMERA_PROFILE_PIC = 601;
    private static final int SELECT_FILE_PROFILE_PIC = 602;

    LayoutInflater mInflater;
    ImageView ivProfile;
    ImageView ivEditPicBg;
    ImageView ivEditPic;

    File mCameraProfilePicImagePath;
    RxPermissions rxPermissions;
    Activity mActivity;
    String mImage;

    public ProfilePicEditWidget(Context context) {
        this(context, null);
    }

    public ProfilePicEditWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfilePicEditWidget(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.widget_edit_profile_pic, this, true);

        ivProfile = findViewById(R.id.iv_profile);
        ivEditPicBg = findViewById(R.id.iv_edit_pic_bg);
        ivEditPic = findViewById(R.id.iv_edit_pic);

        ivProfile.setOnClickListener(v -> editPhoto());
        ivEditPicBg.setOnClickListener(v -> editPhoto());
        ivEditPic.setOnClickListener(v -> editPhoto());
    }

    public void init(Activity activity, RxPermissions rxPermissions) {
        this.mActivity = activity;
        this.rxPermissions = rxPermissions;
    }

    public String getData() {
        return mImage;
    }

    public void setData(String url) {
        mImage = url;
        ImageUtils.loadPicInBorderedCircularView(mActivity, url, ivProfile, R.drawable.profile_pic_white_border_circle, Utils.dpToPixels(2.0f), getResources().getColor(R.color.white));
    }

    public void editPhoto() {
        mCameraProfilePicImagePath = ImageUtils.showPhotoEditDialog(mActivity, rxPermissions, REQUEST_CAMERA_PROFILE_PIC, SELECT_FILE_PROFILE_PIC, null);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA_PROFILE_PIC:
                    if (mCameraProfilePicImagePath != null && !mCameraProfilePicImagePath.getAbsolutePath().isEmpty()) {
                        Uri imageUri = Uri.fromFile(mCameraProfilePicImagePath);
                        if (imageUri != null) {
                            ImageUtils.loadPicInBorderedCircularView(mActivity, imageUri, ivProfile, 0, Utils.dpToPixels(2.0f), getResources().getColor(R.color.white));
                            mImage = mCameraProfilePicImagePath.getAbsolutePath();
                            ImageUtils.refreshGallery(mActivity, imageUri.getPath());
                        }
                    }
                    break;
                case SELECT_FILE_PROFILE_PIC:
                    if (data != null && data.getData() != null) {
                        String path = ImageUtils.getPathFromGallaryResult(mActivity, data.getData());
                        if (!Utils.isNullOrEmpty(path)) {
                            ImageUtils.loadPicInBorderedCircularView(mActivity, path, ivProfile, 0, Utils.dpToPixels(2.0f), getResources().getColor(R.color.white));
                            mImage = path;
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

}
