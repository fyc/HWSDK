package com.yiyou.gamesdk.core.interceptor;

import com.yiyou.gamesdk.core.ui.widget.dialog.CommDialog;

/**
 *
 * splash interceptor
 * Created by shui on 4/11/16.
 */
public class DismissSplashInterceptor implements InitInterceptor {

    private CommDialog splashDialog;

    public DismissSplashInterceptor(CommDialog splashDialog) {
        this.splashDialog = splashDialog;
    }

    @Override
    public void intercept(final Chain<InitParams> chain) {


        dismissSplash(new CommDialog.DialogMissCallback() {
            @Override
            public void done() {
                chain.proceed(chain.getData());

            }
        });


    }


    private void dismissSplash(CommDialog.DialogMissCallback dialogCallback) {

        if (splashDialog.isShowing()) {

            splashDialog.dismiss(3000, dialogCallback);
        }

    }

}
