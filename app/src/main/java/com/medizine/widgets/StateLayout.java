package com.medizine.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.medizine.ConnectivityListener;
import com.medizine.R;
import com.medizine.StateLayoutViewMode;
import com.medizine.event.Event;
import com.medizine.network.Connectivity;
import com.medizine.receiver.NetworkReceiver;
import com.medizine.utils.RxBus;
import com.medizine.utils.Utils;

public class StateLayout extends FrameLayout implements ConnectivityListener {

    private static final String TAG = StateLayout.class.getSimpleName();
    private boolean isConnected = false;
    private boolean mConnectivityAware = false;
    private Handler mHandler;
    // error
    private int mErrorLayout = R.layout.layout_error;
    // empty
    private int mEmptyLayout = R.layout.layout_empty;
    private int mEmptyText = R.string.text_empty;
    private int mEmptyTextColor = R.color.onSurfaceSubtitle;
    // progress
    private int mProgressLayout = R.layout.layout_progress;
    private Context mContext;
    private FrameLayout contentView;
    private FrameLayout emptyView;
    private FrameLayout progressView;
    private FrameLayout errorView;
    private TextView textError;
    private TextView textEmpty;
    private Button retryButton;
    private TextView connectivityText;
    private NetworkReceiver mReceiver;
    @StateLayoutViewMode.State
    private int mMode;

    public StateLayout(@NonNull Context context) {
        super(context);
        mContext = context;
        init(null, 0);
    }

    public StateLayout(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs, 0);
    }

    public StateLayout(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public StateLayout(@NonNull Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {

        mHandler = new Handler(Looper.getMainLooper());
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StateLayout);
        try {
            // connectivity
            mConnectivityAware = a.getBoolean(R.styleable.StateLayout_isConnectivityAware, mConnectivityAware);

            // error
            mErrorLayout = a.getResourceId(R.styleable.StateLayout_errorLayout, mErrorLayout);

            // empty
            mEmptyLayout = a.getResourceId(R.styleable.StateLayout_emptyLayout, mEmptyLayout);
            mEmptyText = a.getResourceId(R.styleable.StateLayout_emptyText, mEmptyText);
            mEmptyTextColor = a.getResourceId(R.styleable.StateLayout_emptyTextColor, mEmptyTextColor);

            // progress
            mProgressLayout = a.getResourceId(R.styleable.StateLayout_progressLayout, mProgressLayout);
        } finally {
            a.recycle();
        }
        postInit();
    }

    /**
     * Inflate the default layouts
     */
    private void postInit() {
        isConnected = Connectivity.create().isAvailable();
        // inflate view
        LayoutInflater.from(mContext).inflate(R.layout.layout_state, this);

        contentView = findViewById(R.id.contentView);
        emptyView = findViewById(R.id.emptyView);
        progressView = findViewById(R.id.progressView);
        errorView = findViewById(R.id.errorView);
        connectivityText = findViewById(R.id.connectivityText);

        inflateLayouts();
    }


    private void inflateLayouts() {
        inflateEmptyView();
        inflateProgressView();
        inflateErrorView();
        if (mConnectivityAware) {
            initConnectivity();
        }
    }

    private void inflateErrorView() {
        errorView.removeAllViewsInLayout();
        LayoutInflater.from(mContext).inflate(mErrorLayout, errorView);

        if (mErrorLayout == R.layout.layout_error) {
            // default view but maybe different text, colors
            textError = errorView.findViewById(R.id.textError);
            retryButton = errorView.findViewById(R.id.retryButton);
//            inflateErrorText();
            retryButton.setOnClickListener(v -> RxBus.getInstance().sendEvent(new Event.RetryButtonEvent()));
        }
    }

    private void inflateProgressView() {
        // inflate view
        progressView.removeAllViewsInLayout();
        LayoutInflater.from(mContext).inflate(mProgressLayout, progressView);
    }


    /**
     * Set the empty view text color
     */
    private void inflateEmptyTextColor() {
        if (mEmptyTextColor != R.color.onSurfaceSubtitle) {
            if (mEmptyLayout != R.layout.layout_empty) {
                throw new RuntimeException("Cannot assign the emptyTextColor attribute. " +
                        "You already overridden the entire empty view, no need to specify " +
                        "custom color or custom text message");
            } else {
                //textEmpty.setTextColor(ColorUtil.getColorWrapper(mContext, mEmptyTextColor));
            }
        }
    }

    /**
     * Set the empty view text message
     */
    private void inflateEmptyText() {
        if (mEmptyText != R.string.text_empty) {
            if (mEmptyLayout != R.layout.layout_empty) {
                throw new RuntimeException("Cannot assign the EmptyText attribute. " +
                        "You already overridden the entire emptyLayout, no need to specify " +
                        "custom color or custom text message");
            } else {
                textEmpty.setText(mEmptyText);
            }
        }
    }

    /**
     * Set a custom empty view, this can be done via XML or programmatically
     * using setEmptyLayout(int layoutId)
     * If not custom empty view is assigned, the default view will be inflated
     */
    private void inflateEmptyView() {
        // inflate view
        emptyView.removeAllViewsInLayout();
        LayoutInflater.from(mContext).inflate(mEmptyLayout, emptyView);

        if (mEmptyLayout == R.layout.layout_empty) {
            // default view but maybe different text, colors
            textEmpty = emptyView.findViewById(R.id.textEmpty);
            inflateEmptyText();
            inflateEmptyTextColor();
        }
    }

    /**
     * check if the view is declared to be aware of the connectivity.
     * if yes, start a broadcast receiver to get udpates.
     */
    private void initConnectivity() {
        if (mReceiver == null) {
            // register broadcast receiver
            mReceiver = new NetworkReceiver(this);
            mContext.registerReceiver(mReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    /**
     * check if a broadcast receiver have been already created, if yes unregister it.
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mReceiver != null) {
            try {
                mContext.unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                Utils.logException(TAG, e);
            }
        }
    }

    /**
     * Add the main content of the RelativeLayout custom view to the subView contentView.
     * This is the main content that will contains the childs of StateLayout.
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (contentView == null) {
            super.addView(child, index, params);
        } else {
            //Forward these calls to the content view
            contentView.addView(child, index, params);
        }
    }


    /**
     * set a custom empty view programmatically
     *
     * @param mEmptyLayout an id representing the layout resource.
     */
    public void setEmptyLayout(int mEmptyLayout) {
        this.mEmptyLayout = mEmptyLayout;
        inflateEmptyView();
    }

    /**
     * set the empty text to appear in the empty view
     *
     * @param mEmptyText an id representing the string resource.
     */
    public void setEmptyText(int mEmptyText) {
        this.mEmptyText = mEmptyText;
        inflateEmptyText();
    }

    /**
     * set the text color to display in the empty view
     *
     * @param mEmptyTextColor an id representing the color resource.
     */
    public void setEmptyTextColor(int mEmptyTextColor) {
        this.mEmptyTextColor = mEmptyTextColor;
        inflateEmptyTextColor();
    }

    /**
     * @return the connectivity status : true if connected, false otherwise.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * the callback called by the broadcast receiver when a connectivity status change is detected
     */
    @Override
    public void onChanged(boolean status) {
        isConnected = status;
        inflateConnectivity();
    }

    /**
     * check connectivity and inflate connectivity view accordingly
     */
    private void inflateConnectivity() {
        if (isConnected) {
            showConnected();
        } else {
            showDisconnected();
        }
    }

    /**
     * show connectivity view in disconnected mode.
     */
    private void showDisconnected() {
        connectivityText.postDelayed(() -> {
            if (!isConnected && connectivityText.getVisibility() == GONE) {
                connectivityText.setVisibility(View.VISIBLE);
                TranslateAnimation animate = new TranslateAnimation(0, 0, -connectivityText.getHeight(), 0);
                animate.setDuration(500);
                animate.setFillAfter(true);
                connectivityText.startAnimation(animate);
            }
        }, 1000);
    }

    /**
     * show connectivity view in connected mode.
     */
    private void showConnected() {
        connectivityText.postDelayed(() -> {
            if (isConnected && connectivityText.getVisibility() == VISIBLE) {
                TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -connectivityText.getHeight());
                animate.setDuration(500);
                connectivityText.startAnimation(animate);
                connectivityText.setVisibility(View.GONE);
            }
        }, 2000);
    }

    public int getMode() {
        return mMode;
    }

    /**
     * set the mode of the StateLayout.
     * This is called usually from the activity/fragment to change the status of the view
     * Example:
     * // before loading data :
     * StateLayout.setMode(StateLayoutViewMode.PROGRESS);
     * <p>
     * // if data is loaded correctly and is not empty
     * StateLayout.setMode(StateLayoutViewMode.CONTENT);
     * <p>
     * // otherwise
     * StateLayout.setMode(StateLayoutViewMode.EMPTY);
     */
    public void setMode(@StateLayoutViewMode.State int mode) {
        if (mMode != mode) {
            mMode = mode;
            mHandler.post(() -> {
                switch (mMode) {
                    case StateLayoutViewMode.PROGRESS:
                        setEmpty(GONE);
                        setContent(GONE);
                        setError(GONE);
                        fadeIn(progressView, true).setProgress(VISIBLE);
                        break;
                    case StateLayoutViewMode.EMPTY:
                        setProgress(GONE);
                        setContent(GONE);
                        setError(GONE);
                        fadeIn(emptyView, true).setEmpty(VISIBLE);
                        break;
                    case StateLayoutViewMode.CONTENT:
                        setProgress(GONE);
                        setEmpty(GONE);
                        setError(GONE);
                        fadeIn(contentView, true).setContent(VISIBLE);
                        break;
                    case StateLayoutViewMode.ERROR:
                        setProgress(GONE);
                        setEmpty(GONE);
                        setContent(GONE);
                        fadeIn(errorView, true).setError(VISIBLE);
                        break;
                }
            });
        }
    }

    @NonNull
    private StateLayout fadeIn(@Nullable final View view, final boolean animate) {
        if (view != null) {
            if (animate) {
                view.startAnimation(AnimationUtils.loadAnimation(getContext(),
                        android.R.anim.fade_in));
            } else {
                view.clearAnimation();
            }
        }
        return this;
    }


    private void setContent(int visibility) {
        contentView.setVisibility(visibility);
    }


    private void setEmpty(int visibility) {
        emptyView.setVisibility(visibility);
    }


    private void setProgress(int visibility) {
        progressView.setVisibility(visibility);
    }


    private void setError(int visibility) {
        errorView.setVisibility(visibility);
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    public void setRetryButtonListener(OnClickListener listener) {
        retryButton.setOnClickListener(listener);
    }
}