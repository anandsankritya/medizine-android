package com.medizine.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomListAdapter<E extends Object, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    Context mContext;
    List<E> mList = new ArrayList<>();

    public CustomListAdapter(Context context) {
        this.mContext = context;
    }

    public void setList(@Nullable List<E> list) {
        if (list == null) {
            mList = new ArrayList<>();
        } else {
            mList = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
