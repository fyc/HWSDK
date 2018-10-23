package com.yiyou.gamesdk.core.ui.widget.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.CoreManager;


/**
 * Created by chenshuide on 16-3-3.
 */
public class SplashDialogView implements IDialogView {

    private View floatView;

    public SplashDialogView() {
        floatView = LayoutInflater.from(CoreManager.getContext()).inflate(R.layout.tt_sdk_splash_view, null);
    }

    @Override
    public View getView() {
        return floatView;
    }

    @Override
    public void attach(@NonNull Dialog dialog, OnDialogClickListener lisenter) {

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                close();
            }
        });

    }

    @Override
    public void close() {
        floatView = null;
    }


}
