package com.medizine;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.medizine.utils.ImageUtils;
import com.medizine.utils.Utils;

public class CustomViewBindings {
    @BindingAdapter({"adapter", "layoutManager"})
    public static void bindRecyclerViewAdapter(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter, RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter("adapter")
    public static void bindViewPagerAdapter(ViewPager viewPager, PagerAdapter adapter) {
        viewPager.setAdapter(adapter);
    }

    @BindingAdapter("imageUrl")
    public static void bindImageViewUrl(ImageView imageView, String url) {
        if (!Utils.isNullOrEmpty(url)) {
            ImageUtils.loadPicInCircularView(imageView.getContext(), url, imageView);
        }
    }

    @BindingAdapter({"imageResource", "placeholder"})
    public static void bindImageViewUrlWithPlaceHolder(ImageView imageView, String url, int drawable) {
        ImageUtils.loadPicInCircularView(imageView.getContext(), url, imageView, drawable);
    }

    @BindingAdapter("refreshing")
    public static void bindSwipeRefreshLayoutMode(SwipeRefreshLayout swipeRefreshLayout, boolean isRefreshing) {
        swipeRefreshLayout.setRefreshing(isRefreshing);
    }
}
