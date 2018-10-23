package com.yiyou.gamesdk.core.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.ui.widget.DrawableEditText;
import com.yiyou.gamesdk.util.ViewUtils;

/**
 * Created by Nekomimi on 2017/4/25.
 */

public class EditInputDialogView implements IDialogView, View.OnClickListener  {

    private static final String TAG = "EditInputDialogView";

    private TextView titleTv, confirmTv, cancelTv;
    private DrawableEditText inputEdt;
    private Drawable delDrawable;
    private View layout;
    private OnDialogClickListener listener;
    private Dialog dialog;

    public EditInputDialogView(Context context) {
        initView(context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                if (listener != null)
                    listener.onCancel();

                close();
                break;

            case R.id.ensure:
                if (listener != null)
                    listener.onEnsure(this);

                break;
        }
    }

    @Override
    public View getView() {
        return layout;
    }

    public EditInputDialogView setTitleTip(int resid) {
        titleTv.setText(resid);
        return this;
    }


    public EditInputDialogView setMessageTip(int resid) {
        inputEdt.setHint(resid);
        return this;
    }


    public EditInputDialogView setEnsureText(int resid) {
        confirmTv.setText(resid);
        return this;
    }

    public EditInputDialogView setCancelText(int resid) {
        cancelTv.setText(resid);
        return this;
    }

    public void setHint(int resid){
        inputEdt.setHint(resid);
    }

    public void setHint(String hint){
        inputEdt.setHint(hint);
    }

    public void setInputType(int type){
        Log.d(TAG, "setInputType: ");
        inputEdt.setInputType(type);
    }

    public String getInput() {
        return inputEdt.getText().toString();
    }

    private void initView(Context context) {

        layout = LayoutInflater.from(context).inflate(R.layout.tt_sdk_dialog_edittext_alert, null);
        delDrawable = layout.getContext().getResources().getDrawable(R.drawable.icon_all_del);

        titleTv = (TextView) layout.findViewById(R.id.title);
        inputEdt = (DrawableEditText) layout.findViewById(R.id.edt_child_username);
        confirmTv = (TextView) layout.findViewById(R.id.ensure);
        confirmTv.setOnClickListener(this);
        cancelTv = (TextView) layout.findViewById(R.id.cancel);
        cancelTv.setOnClickListener(this);
        inputEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputEdt.setDrawableRight( s.length() > 0 ? R.drawable.icon_all_del : 0);
            }
        });
        inputEdt.setOnRightDrawableClickListener(new DrawableEditText.OnDrawableClickListener() {
            @Override
            public void onPressed(DrawableEditText editText) {

            }

            @Override
            public void onCancel(DrawableEditText editText) {

            }

            @Override
            public void onClick(DrawableEditText editText) {
                inputEdt.setText("");
            }
        });
        ViewUtils.bindEditWithButton(inputEdt,confirmTv);
    }

    @Override
    public void attach(@NonNull Dialog dialog, OnDialogClickListener lisenter) {
        this.dialog = dialog;
        this.listener = lisenter;
    }

    @Override
    public void close() {
        if (dialog != null)
            dialog.dismiss();
    }
}
