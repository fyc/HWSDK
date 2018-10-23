package com.yiyou.gamesdk.core.ui.widget.dialog;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by shui on 15-8-27.
 */
public interface IDialogView {

    View getView();

    void attach(@NonNull Dialog dialog, OnDialogClickListener lisenter);

    void close();

}
