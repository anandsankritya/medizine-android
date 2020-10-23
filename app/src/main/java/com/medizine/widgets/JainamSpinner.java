package com.medizine.widgets;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jainam.android.R;
import com.jainam.android.adapter.JainamSpinnerAdapter;

import java.util.Arrays;
import java.util.List;

public class JainamSpinner extends RelativeLayout {
    Spinner mSpinner;
    JainamSpinnerAdapter mSpinnerAdapter;
    List values;
    Class mClass;

    public JainamSpinner(@NonNull Context context) {
        this(context, null);
    }

    public JainamSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JainamSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_jainam_spinner, this, true);

        mSpinner = findViewById(R.id.spinner);
    }

    public void setList(Class aClass, Object[] array) {
        mClass = aClass;
        values = Arrays.asList(array);
        mSpinnerAdapter = new JainamSpinnerAdapter(getContext(), aClass, values);
        mSpinner.setAdapter(mSpinnerAdapter);
    }

    public void setSelection(@Nullable String value) {
        if (value != null) {
            for (int i = 0; i < values.size(); i++) {
                if (Enum.valueOf(mClass, value.toUpperCase()).equals(values.get(i))) {
                    mSpinner.setSelection(i);
                    return;
                }
            }
        }
    }

    public String getSelectedValue() {
        if (values == null) {
            return null;
        } else {
            return values.get(mSpinner.getSelectedItemPosition()).toString();
        }
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mSpinner.setOnItemSelectedListener(listener);
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
