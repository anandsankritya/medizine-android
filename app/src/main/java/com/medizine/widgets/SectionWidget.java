package com.medizine.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.jainam.android.R;
import com.jainam.android.utils.Utils;

public class SectionWidget extends LinearLayout {
    LinearLayout mContentLayout;
    TextView mTitleView;
    ImageView mEditButton;
    Button mSubscribeButton;
    ProgressBar mPbSubscribe;
    TextView mTvSubscriberCount;

    public SectionWidget(Context context) {
        this(context, null);
    }

    public SectionWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SectionWidget, 0, 0);
        String titleText = a.getString(R.styleable.SectionWidget_titleText);
        boolean editingAllowed = a.getBoolean(R.styleable.SectionWidget_editingAllowed, false);
        int editingSrc = a.getResourceId(R.styleable.SectionWidget_editingSrc, 0);
        boolean removeBottomPadding = a.getBoolean(R.styleable.SectionWidget_removeBottomPadding, false);
        int horizontalPadding = a.getDimensionPixelSize(R.styleable.SectionWidget_horizontalPadding, Utils.dpToPixels(18));
        a.recycle();

        if (titleText == null) {
            throw new RuntimeException("titleText not set for SectionWidget");
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_section, this, true);

        // Set orientation of Layout
        setOrientation(VERTICAL);

        // Set title
        mTitleView = findViewById(R.id.sectionTitle);
        mTitleView.setText(titleText);

        // Set padding
        int verticalPadding = Utils.dpToPixels(12);
        LinearLayout layout = findViewById(R.id.sectionTitleLayout);
        layout.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        mContentLayout = findViewById(R.id.contentLayout);
        mContentLayout.setPadding(horizontalPadding, 0, horizontalPadding, removeBottomPadding ? 0 : verticalPadding);

        // Set Edit Button
        mEditButton = findViewById(R.id.editButton);
        if (editingAllowed) {
            mEditButton.setVisibility(VISIBLE);
        } else {
            mEditButton.setVisibility(GONE);
        }

        if (editingSrc != 0) {
            mEditButton.setImageResource(editingSrc);
        }

        // Set Subscription UI
        mSubscribeButton = findViewById(R.id.btnSubscribe);
        mPbSubscribe = findViewById(R.id.pbSubscribe);
        mTvSubscriberCount = findViewById(R.id.tvSubscriberCount);
    }

    public void setEditingAllowed(boolean allowed, @DrawableRes int res) {
        mEditButton.setVisibility(allowed ? VISIBLE : GONE);
        if (allowed) {
            mEditButton.setImageResource(res);
        }
    }

    public void setEditButtonListener(OnClickListener onClickListener) {
        mEditButton.setOnClickListener(onClickListener);
    }

    public void setSubscribeEnabled(boolean enableSubscribeButton, boolean isSubscribed, int subscriberCount) {
        if (enableSubscribeButton) {
            mSubscribeButton.setVisibility(VISIBLE);
            mTvSubscriberCount.setVisibility(VISIBLE);
            mTvSubscriberCount.setText(getContext().getString(R.string.subscribers, String.valueOf(subscriberCount)));
            mSubscribeButton.setText(getContext().getString(isSubscribed ? R.string.subscribed : R.string.subscribe));
            mSubscribeButton.setTextColor(getResources().getColor(isSubscribed ? R.color.grey400 : R.color.redLight1));
        } else {
            mSubscribeButton.setVisibility(GONE);
            mTvSubscriberCount.setVisibility(GONE);
        }
    }

    public void setProgress(boolean show) {
        mSubscribeButton.setVisibility(show ? INVISIBLE : VISIBLE);
        mPbSubscribe.setVisibility(show ? VISIBLE : GONE);
    }

    public void setOnSubscribeListener(OnClickListener onClickListener) {
        mSubscribeButton.setOnClickListener(onClickListener);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        switch (child.getId()) {
            case R.id.line:
            case R.id.sectionTitleLayout:
            case R.id.contentLayout:
                super.addView(child, index, params);
                break;
            default:
                mContentLayout.addView(child, params);
        }
//
//        if (!(child instanceof MaterialCardView) && child.getId() != R.id.sectionTitleLayout && indexOfChild(child) == 1) {
//            child.setPadding(0, Utils.dpToPixels(50), 0, 0);
//        }
    }

    //    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//
//        // Set Margin
//        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
//        params.setMargins(mHorizontalPadding, 0, mHorizontalPadding, 0);
//        setLayoutParams(params);
//    }
    public void setEditButtonVisibility(boolean shouldBeVisible) {
        if (shouldBeVisible)
            mEditButton.setVisibility(View.VISIBLE);
        else
            mEditButton.setVisibility(View.INVISIBLE);
    }

    public void setButtonVisibility(boolean shouldBeVisible) {
        mSubscribeButton.setVisibility(shouldBeVisible ? View.VISIBLE : View.GONE);
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
