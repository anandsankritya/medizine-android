package com.medizine;

import android.view.View;

public abstract class ThrottleClick implements View.OnClickListener {
    private static final long CLICK_TIME_DELTA = 1000;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        if (isSingleClick()) {
            onClick();
        }
    }

    public abstract void onClick();

    private boolean isSingleClick() {
        long clickTime = System.currentTimeMillis();
        long throttleTime = clickTime - lastClickTime;
        lastClickTime = clickTime;
        return throttleTime > CLICK_TIME_DELTA;
    }
}
