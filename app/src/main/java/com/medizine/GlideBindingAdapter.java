package com.medizine;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.medizine.utils.ImageUtils;

public class GlideBindingAdapter {
    @BindingAdapter("imageResource")
    public static void setImageResource(ImageView view, String imageUrl) {
        ImageUtils.loadPicInView(view.getContext(), imageUrl, view, R.drawable.ic_temple);
    }
}
