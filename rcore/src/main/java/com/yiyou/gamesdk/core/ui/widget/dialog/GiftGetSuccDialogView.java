package com.yiyou.gamesdk.core.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.util.CommonUtils;

/**
 * Created by BM on 2017/11/16.
 * <p>
 * desc:
 */

public class GiftGetSuccDialogView implements IDialogView, View.OnClickListener {

    private View layout;
    private Dialog mDialog;
    private OnDialogClickListener mLisenter;
    private TextView mPackageCode;
    private Context mContext;

    public GiftGetSuccDialogView(Context context) {
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        layout = LayoutInflater.from(context).inflate(R.layout.tt_sdk_dialog_get_package, null);
        mPackageCode = (TextView) layout.findViewById(R.id.package_code);
        layout.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        layout.findViewById(R.id.dialog_confirm).setOnClickListener(this);
    }

    public void setPackageCode(String code) {
        mPackageCode.setText(mContext.getString(R.string.game_package_code, code));
    }


    @Override
    public View getView() {
        return layout;
    }

    @Override
    public void attach(@NonNull Dialog dialog, OnDialogClickListener lisenter) {
        mDialog = dialog;
        mLisenter = lisenter;
    }

    @Override
    public void close() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel:
                close();
                break;
            case R.id.dialog_confirm:
                CommonUtils.setClipboardText(mContext, mPackageCode.getText().toString());
                close();
                break;
        }
    }
}
