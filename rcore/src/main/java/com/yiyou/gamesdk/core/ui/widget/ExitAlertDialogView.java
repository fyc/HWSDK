package com.yiyou.gamesdk.core.ui.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.ui.dialog.BaseViewController;


public class ExitAlertDialogView extends BaseViewController implements View.OnClickListener {

    private TextView tv_title, tv_message, tv_ensure, tv_cancel;

    public ExitAlertDialogView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.title);
        tv_message = (TextView) findViewById(R.id.message);
        tv_ensure = (TextView) findViewById(R.id.ensure);
        tv_ensure.setOnClickListener(this);
        tv_cancel = (TextView) findViewById(R.id.cancel);
        tv_cancel.setOnClickListener(this);

    }


    public ExitAlertDialogView setTitleTip(int resid) {
        tv_title.setText(resid);
        return this;
    }


    public ExitAlertDialogView setMessageTip(int resid) {
        tv_message.setText(resid);
        return this;
    }


    public ExitAlertDialogView setEnsureText(int resid) {
        tv_ensure.setText(resid);
        return this;
    }

    public ExitAlertDialogView setCancelText(int resid) {
        tv_cancel.setText(resid);
        return this;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.tt_sdk_exit_dialog_alert;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    @Override
    public void onDismiss() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                close();
                break;

            case R.id.ensure:
                close();
                if (listener != null)
                    listener.onEnsureClick();

                break;
        }
    }

    private DialogClickListener listener;

    public void setListener(DialogClickListener listener) {
        this.listener = listener;
    }

    public interface DialogClickListener {

        void onEnsureClick();


    }

}
