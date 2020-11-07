package com.medizine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.medizine.R;
import com.medizine.model.entity.Slot;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SlotListAdapter extends CustomListAdapter<Slot, SlotListAdapter.SlotViewHolder> {
    private static final String TAG = SlotListAdapter.class.getSimpleName();

    private boolean mShowDoctorLayout;
    private OnSlotBookedListener mOnSlotBookedListener;

    public SlotListAdapter(Context context, boolean showDoctorLayout, OnSlotBookedListener onSlotBookedListener) {
        super(context);
        this.mShowDoctorLayout = showDoctorLayout;
        this.mOnSlotBookedListener = onSlotBookedListener;
    }

    @NonNull
    @Override
    public SlotListAdapter.SlotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_slot, parent, false);
        return new SlotListAdapter.SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotListAdapter.SlotViewHolder holder, int position) {
        Slot slot = mList.get(position);
        holder.bind(slot);
    }

    public interface OnSlotBookedListener {
        void onSlotBooked(Context context, Slot slot);
    }

    public class SlotViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layoutSlot)
        ConstraintLayout layoutSlot;
        @BindView(R.id.tvTiming)
        TextView tvTiming;
        @BindView(R.id.btnBookSlot)
        Button btnBookSlot;
        @BindView(R.id.ivBooked)
        ImageView ivBooked;
        @BindView(R.id.tvBookedByCurrentUser)
        TextView tvBookedByCurrentUser;

        Context mContext;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bind(Slot slot) {
            tvTiming.setText(mContext.getString(R.string.slot_timing, slot.getFormattedStartTime(), slot.getFormattedEndTime()));
            btnBookSlot.setOnClickListener(view -> {
                if (mOnSlotBookedListener != null) {
                    mOnSlotBookedListener.onSlotBooked(mContext, slot);
                }
            });
            if (mShowDoctorLayout) {
                btnBookSlot.setVisibility(View.GONE);
                ivBooked.setVisibility(View.GONE);
                tvBookedByCurrentUser.setVisibility(View.GONE);
            } else {
                if (slot.getBookedBySameUser() != null && slot.getBookedBySameUser()) {
                    layoutSlot.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_black_light));
                    updateViewsVisibility(R.id.tvBookedByCurrentUser);
                } else if (slot.getBooked() != null && slot.getBooked()) {
                    layoutSlot.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_black_light));
                    updateViewsVisibility(R.id.ivBooked);
                } else {
                    layoutSlot.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                    updateViewsVisibility(R.id.btnBookSlot);
                }
            }
        }

        public void updateViewsVisibility(int id) {
            btnBookSlot.setVisibility(R.id.btnBookSlot == id ? View.VISIBLE : View.GONE);
            ivBooked.setVisibility(R.id.ivBooked == id ? View.VISIBLE : View.GONE);
            tvBookedByCurrentUser.setVisibility(R.id.tvBookedByCurrentUser == id ? View.VISIBLE : View.GONE);
        }
    }
}