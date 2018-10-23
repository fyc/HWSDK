package com.yiyou.gamesdk.core.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiyou.gamesdk.R;

/**
 * Created by chenshuide on 15/6/15.
 */
public class AccountLayout extends RelativeLayout {
    private TextView leftTv, rightTv;
    private ImageView iv_redpoint;
    private View divider;
    private TextView leftSmallTv;

    public AccountLayout(Context context) {
        this(context, null);
    }

    public AccountLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccountLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AccountLayout);
        String lefttext = a.getString(R.styleable.AccountLayout_left_text);
        String righttext = a.getString(R.styleable.AccountLayout_right_text);
        int right_text_color = a.getColor(R.styleable.AccountLayout_right_text_color, getResources().getColor(R.color.item_text_dark));
        int left_text_color = a.getColor(R.styleable.AccountLayout_left_text_color, getResources().getColor(R.color.item_text_normal));
        int textsize = 15;
        boolean isShowDisvider = a.getBoolean(R.styleable.AccountLayout_divider, false);
        a.recycle();

        leftTv.setText(lefttext);
        rightTv.setText(righttext);
        leftTv.setTextColor(left_text_color);
        rightTv.setTextColor(right_text_color);
        divider.setVisibility(isShowDisvider ? View.VISIBLE : View.GONE);
    }

    private void init() {
        View layout = inflate(getContext(), R.layout.tt_sdk_view_account_layout, this);
        leftTv = (TextView) layout.findViewById(R.id.left_text);
        rightTv = (TextView) layout.findViewById(R.id.right_text);
        iv_redpoint = (ImageView) layout.findViewById(R.id.iv_redpoint);
        leftSmallTv = (TextView) layout.findViewById(R.id.small_left_text);
        divider = layout.findViewById(R.id.divider);

    }

    public void setRedPointVisibility(int visibility) {
        iv_redpoint.setVisibility(visibility);
    }

    public void setRightTextColor(int color) {
        rightTv.setTextColor(color);
    }


    public void setLeftText(String text) {
        leftTv.setText(text);
    }

    public void setLeftSmallText(String text) {
        leftSmallTv.setText(text);
    }

    public void setLeftText(int resid) {
        leftTv.setText(resid);
    }

    public void setRightText(String text) {
        rightTv.setText(text);
    }

    public void setRightText(int resid) {
        rightTv.setText(resid);
    }


}
