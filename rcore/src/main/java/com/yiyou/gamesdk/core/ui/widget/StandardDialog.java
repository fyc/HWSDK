package com.yiyou.gamesdk.core.ui.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yiyou.gamesdk.R;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;

/**
 * Created by levyyoung on 15/7/29.
 */
public class StandardDialog extends AlertDialog implements View.OnClickListener{

    private TextView tv_title, tv_message, tv_ensure, tv_cancel;

    public StandardDialog(Context context) {
        super(context);
        init();
    }

    public StandardDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public StandardDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private View contentView;
    private void init() {
        LayoutInflater inflater = LayoutInflater.from(ResourceHelper.getContextHolder().get());
        contentView = inflater.inflate(R.layout.tt_sdk_dialog_common_alert, null);
        tv_title = (TextView) contentView.findViewById(R.id.title);
        tv_message = (TextView) contentView.findViewById(R.id.message);
        tv_ensure = (TextView) contentView.findViewById(R.id.ensure);
        tv_ensure.setOnClickListener(this);
        tv_cancel = (TextView) contentView.findViewById(R.id.cancel);
        tv_cancel.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();
        //avoid android.util.AndroidRuntimeException: requestFeature() must be called before adding content
        this.setContentView(contentView);
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
                    listener.onEnsureClick();

                break;
        }
    }

    public StandardDialog setTitleTip(int resid) {
        tv_title.setText(resid);
        return this;
    }


    public StandardDialog setMessageTip(int resid) {
        tv_message.setText(resid);
        return this;
    }

    public StandardDialog setMessageTip(String msg) {
        tv_message.setText(msg);
        return this;
    }


    public StandardDialog setEnsureText(int resid) {
        tv_ensure.setText(resid);
        return this;
    }

    public StandardDialog setCancelText(int resid) {
        tv_cancel.setText(resid);
        return this;
    }

    private DialogClickListener listener;

    public void setListener(DialogClickListener listener) {
        this.listener = listener;
    }

    public interface DialogClickListener {

        void onEnsureClick();

    }
    
}
