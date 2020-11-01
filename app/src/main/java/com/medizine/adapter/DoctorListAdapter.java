package com.medizine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medizine.R;
import com.medizine.ThrottleClick;
import com.medizine.model.entity.Doctor;
import com.medizine.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoctorListAdapter extends CustomListAdapter<Doctor, DoctorListAdapter.DoctorViewHolder> {

    public DoctorListAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = mList.get(position);
        holder.tvName.setText(Utils.capitalizeWords(doctor.getName()));
        holder.tvPhone.setText(doctor.getCountryCode() + doctor.getPhoneNumber());
        holder.tvAddress.setText(doctor.getEmailAddress());
    }

    public class DoctorViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvPhone)
        TextView tvPhone;
        @BindView(R.id.tvAddress)
        TextView tvAddress;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new ThrottleClick() {
                @Override
                public void onClick() {
                    //openDoctorProfileActivity(mContext, mList.get(getAdapterPosition()).getId());
                }
            });
        }
    }
}