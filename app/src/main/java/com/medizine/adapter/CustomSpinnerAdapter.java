package com.medizine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.medizine.R;

import java.lang.reflect.Method;
import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter {

    private List list;
    private LayoutInflater inflater;
    private Class mClass;

    public CustomSpinnerAdapter(@NonNull Context context, @NonNull Class aClass, @NonNull List list) {
        super(context, R.id.spinnerTextView, list);
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mClass = aClass;
    }

    @Override
    @NonNull
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_row_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = convertView.findViewById(R.id.spinnerTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Using reflection
        try {
            Method method = mClass.getMethod("getLocaleString", Context.class);
            String localeString = (String) method.invoke(list.get(position), getContext());
            viewHolder.textView.setText(localeString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View itemView = inflater.inflate(R.layout.item_spinner, parent, false);
        TextView textView = itemView.findViewById(R.id.tvName);

        //Using reflection
        try {
            Method method = mClass.getMethod("getLocaleString", Context.class);
            String localeString = (String) method.invoke(list.get(position), getContext());
            textView.setText(localeString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemView;
    }

    public static class ViewHolder {
        TextView textView;
    }
}