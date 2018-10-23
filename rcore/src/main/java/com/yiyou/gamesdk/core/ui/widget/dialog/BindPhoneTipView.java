package com.yiyou.gamesdk.core.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yiyou.gamesdk.R;

/**
 * Created by chenshuide on 15/8/7.
 */
public class BindPhoneTipView implements IDialogView, View.OnClickListener {

    private TextView tv_account;
    private View layout;
    private OnDialogClickListener listener;
    private Dialog dialog;

    public BindPhoneTipView(Context context) {
        initView(context);
    }

    private void initView(Context context) {
        layout = LayoutInflater.from(context).inflate(R.layout.tt_sdk_dialog_bindphone_tip, null);

        tv_account = (TextView) layout.findViewById(R.id.tv_account);
        layout.findViewById(R.id.cancel).setOnClickListener(this);
        layout.findViewById(R.id.ensure).setOnClickListener(this);


    }

    public void setTv_account(String account) {
        tv_account.setText(account);
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
                close();
                if (listener != null)
                    listener.onEnsure(this);

                break;
        }
    }

    @Override
    public void close() {
        if (dialog != null)
            dialog.dismiss();
    }


    @Override
    public View getView() {
        return layout;
    }

    @Override
    public void attach(@NonNull Dialog dialog, OnDialogClickListener lisenter) {
        this.listener = lisenter;
        this.dialog = dialog;
    }


}

