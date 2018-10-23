package com.yiyou.gamesdk.core.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import com.yiyou.gamesdk.R;

/**
 * Created by chenshuide on 15/6/19.
 */
public class MultiStateButton extends Button {
    private int enableRes, disableRes, press_selectorRes;
    private boolean enable = true;

    public MultiStateButton(Context context) {
        this(context, null);
    }

    public MultiStateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MultiStateButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MultiStateButton);
        disableRes = a.getResourceId(R.styleable.MultiStateButton_disable_bg, R.drawable.tt_sdk_account_button_disable);
        enableRes = a.getResourceId(R.styleable.MultiStateButton_enable_bg, R.drawable.tt_sdk_account_button_enable);
        press_selectorRes = a.getResourceId(R.styleable.MultiStateButton_press_selector, R.drawable.tt_sdk_account_button_selector);

        a.recycle();

        setEnabled(false);
    }


    @Override
    public void setEnabled(boolean enabled) {
        if (enable == enabled)
            return;
        enable = enabled;
        setBackgroundResource(enabled ? press_selectorRes : disableRes);
        super.setEnabled(enabled);
    }
}
