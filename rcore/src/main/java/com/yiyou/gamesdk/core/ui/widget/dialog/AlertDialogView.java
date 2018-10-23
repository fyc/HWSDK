package com.yiyou.gamesdk.core.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yiyou.gamesdk.R;

/**
 * Created by chenshuide on 15/7/23.
 */
public class AlertDialogView implements IDialogView, View.OnClickListener {

    private TextView tv_title, tv_message, tv_ensure, tv_cancel;
    private View layout;
    private OnDialogClickListener listener;
    private Dialog dialog;

    public AlertDialogView(Context context) {
        initView(context);
    }

    private void initView(Context context) {

        layout = LayoutInflater.from(context).inflate(R.layout.tt_sdk_dialog_common_alert, null);

        tv_title = (TextView) layout.findViewById(R.id.title);
        tv_message = (TextView) layout.findViewById(R.id.message);
        tv_ensure = (TextView) layout.findViewById(R.id.ensure);
        tv_ensure.setOnClickListener(this);
        tv_cancel = (TextView) layout.findViewById(R.id.cancel);
        tv_cancel.setOnClickListener(this);

    }


    public AlertDialogView setTitleTip(int resid) {
        if (resid <= 0){
            tv_title.setVisibility(View.GONE);
        }else {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(resid);
        }
        return this;
    }


    public AlertDialogView setMessageTip(int resid) {
        tv_message.setText(resid);
        return this;
    }


    public AlertDialogView setEnsureText(int resid) {
        tv_ensure.setText(resid);
        return this;
    }

    public AlertDialogView setCancelText(int resid) {
        tv_cancel.setText(resid);
        return this;
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
        this.dialog = dialog;
        this.listener = lisenter;
    }


}
