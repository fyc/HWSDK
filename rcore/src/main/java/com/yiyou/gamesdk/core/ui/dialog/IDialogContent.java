package com.yiyou.gamesdk.core.ui.dialog;

import android.view.View;

/**
 * Created by Orange on 15/6/9.
 */
public interface IDialogContent {
    View getContentView();

    void onShow();

    void onHide();

    void onDismiss();

    boolean onBackPressed();
}
