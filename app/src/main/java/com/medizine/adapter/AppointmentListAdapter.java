package com.medizine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medizine.R;
import com.medizine.activity.AppointmentDetailActivity;
import com.medizine.model.entity.Appointment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppointmentListAdapter extends CustomListAdapter<Appointment, AppointmentListAdapter.AppointmentViewHolder> {
    private static final String TAG = AppointmentListAdapter.class.getSimpleName();

    public AppointmentListAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public AppointmentListAdapter.AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentListAdapter.AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentListAdapter.AppointmentViewHolder holder, int position) {
        Appointment appointment = mList.get(position);
        holder.bind(appointment);
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvAppointmentDate)
        TextView tvAppointmentDate;
        @BindView(R.id.tvAppointmentStatus)
        TextView tvAppointmentStatus;

        Context mContext;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bind(Appointment appointment) {
            tvAppointmentDate.setText(mContext.getString(R.string.appointment_date, appointment.getFormattedAppointmentDate()));
            tvAppointmentStatus.setText(appointment.getStatus());
            itemView.setOnClickListener(view -> AppointmentDetailActivity.launchAppointmentDetailActivity(mContext, appointment.getId()));
        }
    }
}