package com.yiyou.gamesdk.core.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.CoreManager;

/**
 * Created by chenshuide on 15/6/18.
 */
public class CommonAlertDialog extends Dialog implements View.OnClickListener {

    private TextView tv_title, tv_message, tv_ensure, tv_cancel;

    public CommonAlertDialog(Activity context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setOwnerActivity(context);
    }

    public CommonAlertDialog init() {
        //context 获取资源的context必须为重写之后的
        LayoutInflater inflater = LayoutInflater.from(CoreManager.getContext());
        View layout = inflater.inflate(R.layout.tt_sdk_dialog_common_alert, null);
        setContentView(layout);
        tv_title = (TextView) layout.findViewById(R.id.title);
        tv_message = (TextView) layout.findViewById(R.id.message);
        tv_ensure = (TextView) layout.findViewById(R.id.ensure);
        tv_ensure.setOnClickListener(this);
        tv_cancel = (TextView) layout.findViewById(R.id.cancel);
        tv_cancel.setOnClickListener(this);
        return this;
    }


    public CommonAlertDialog setTitleTip(int resid) {
        tv_title.setText(resid);
        return this;
    }


    public CommonAlertDialog setMessageTip(int resid) {
        tv_message.setText(resid);
        return this;
    }


    public CommonAlertDialog setEnsureText(int resid) {
        tv_ensure.setText(resid);
        return this;
    }

    public CommonAlertDialog setCancelText(int resid) {
        tv_cancel.setText(resid);
        return this;
    }

    @Override
    public void show() {
        if (!isShowing() && getOwnerActivity() != null) {
            super.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;

            case R.id.ensure:
                dismiss();
                if (listener != null)
                    listener.onEnusreClick();

                break;
        }
    }

    private DialogClickListener listener;

    public void setListener(DialogClickListener listener) {
        this.listener = listener;
    }

    public interface DialogClickListener {

        void onEnusreClick();


    }

}
