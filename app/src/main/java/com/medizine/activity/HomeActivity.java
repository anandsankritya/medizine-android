package com.medizine.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.medizine.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.tvUserInfo)
    TextView tvUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        if (getIntent() != null && getIntent().hasExtra("USER_INFO")) {
            String info = getIntent().getStringExtra("USER_INFO");
            tvUserInfo.setText(info);
        }
    }
}