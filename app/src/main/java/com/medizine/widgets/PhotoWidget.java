package com.medizine.widgets;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jainam.android.R;
import com.jainam.android.adapter.PhotoAdapter;

import java.util.List;

public class PhotoWidget extends FrameLayout {
    private PhotoAdapter adapter;

    public PhotoWidget(Context context) {
        this(context, null);
    }

    public PhotoWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_photo, this, true);

        adapter = new PhotoAdapter(context, PhotoAdapter.PhotoAddButtonVisibility.START);
        RecyclerView recyclerView = findViewById(R.id.rvPhoto);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    public void setImages(List<String> images) {
        adapter.setImageList(images);
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
