package com.medizine.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.jainam.android.Constants;
import com.jainam.android.R;
import com.jainam.android.activity.ImageViewerActivity;
import com.jainam.android.model.enums.Module;
import com.jainam.android.utils.ImageUtils;
import com.jainam.android.utils.Utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jainam.android.Constants.IMAGE_LIST;
import static com.jainam.android.Constants.OBJECT_ID;
import static com.jainam.android.Constants.OBJECT_TYPE;
import static com.jainam.android.Constants.POSITION;
import static com.jainam.android.utils.Utils.getActivity;

public class ImageSliderWidget extends LinearLayout {
    @BindView(R.id.vpImages)
    ViewPager vpImages;
    @BindView(R.id.pageIndicatior)
    LinearLayout pageIndicator;

    private Module module;
    private String moduleId;
    private ArrayList<String> mImageList;
    private Timer timer;
    private Handler handler;
    private Runnable autoScroll;

    public ImageSliderWidget(@NonNull Context context) {
        this(context, null);
    }

    public ImageSliderWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageSliderWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.image_slider_widget, this, true);
        ButterKnife.bind(this, view);
    }

    public void init(@Nullable ArrayList<String> mImageList, @NonNull Module module, @NonNull String moduleId) {
        this.module = module;
        this.moduleId = moduleId;
        this.mImageList = mImageList == null ? new ArrayList<>() : mImageList;

        handler = new Handler();
        autoScroll = () -> {
            if (vpImages.getCurrentItem() == mImageList.size() - 1) {
                vpImages.setCurrentItem(0);
            } else {
                vpImages.setCurrentItem(vpImages.getCurrentItem() + 1, true);
            }
        };
        autoSwipeImages();
        vpImages.setOffscreenPageLimit(3);
        vpImages.setAdapter(new ImageSliderWidget.ImageSliderWidgetAdapter());
        refreshPageIndicator(0);
        vpImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                resetTimer();
            }

            @Override
            public void onPageSelected(int position) {
                refreshPageIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void refreshPageIndicator(int currentPage) {
        ImageView[] dots = new ImageView[Utils.isListEmpty(mImageList) ? 0 : mImageList.size()];
        int size = getResources().getDimensionPixelSize(R.dimen._5sdp);
        int margin = getResources().getDimensionPixelSize(R.dimen._2sdp);
        pageIndicator.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(getContext());
            dots[i].setLayoutParams(new LayoutParams(size, size));
            ((LayoutParams) dots[i].getLayoutParams()).setMargins(margin, margin, margin, margin);
            dots[i].setImageResource(i == currentPage ? R.drawable.date_circle_bg : R.drawable.all_circle_white_bg);
            pageIndicator.addView(dots[i]);
        }
    }

    private void autoSwipeImages() {
        timer = new Timer(); // creates a new Thread
        long PERIOD_MS = 10000;
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(autoScroll);
            }
        }, PERIOD_MS, PERIOD_MS);
    }

    private void resetTimer() {
        timer.cancel();
        autoSwipeImages();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (timer != null)
            timer.cancel();
        super.onDetachedFromWindow();
    }

    private class ImageSliderWidgetAdapter extends PagerAdapter {


        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup parent, int position) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_full_screen_image, parent, false);

            ImageView imageView = itemView.findViewById(R.id.imgDisplay);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            ImageUtils.loadPicInView(parent.getContext(), mImageList.get(position), imageView, R.color.grey300);

            imageView.setOnClickListener(view -> {
                Intent i = new Intent(parent.getContext(), ImageViewerActivity.class);
                i.putExtra(OBJECT_ID, moduleId);
                i.putExtra(OBJECT_TYPE, Constants.Fundraiser.FUNDRAISER_TAG);
                i.putExtra(POSITION, position);
                i.putStringArrayListExtra(IMAGE_LIST, mImageList);
                getActivity(parent.getContext()).startActivity(i);
            });

            parent.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup parent, int position, Object object) {
            parent.removeView((View) object);
        }

        @Override
        public int getCount() {
            if (Utils.isListEmpty(mImageList)) {
                return 0;
            }
            return mImageList.size();
        }

    }
}
