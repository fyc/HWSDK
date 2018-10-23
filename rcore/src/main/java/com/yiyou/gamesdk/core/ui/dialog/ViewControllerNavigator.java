package com.yiyou.gamesdk.core.ui.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import com.mobilegamebar.rsdk.outer.event.IDialogParam;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.ui.dialog.biz.LoginViewController;
import com.yiyou.gamesdk.core.ui.dialog.biz.RegisterViewController;
import com.yiyou.gamesdk.core.ui.dialog.biz.ResetPasswordViewController;
import com.yiyou.gamesdk.core.ui.widget.ExitAlertDialogView;
import com.yiyou.gamesdk.core.ui.widget.GameDownloadDialogView;
import com.yiyou.gamesdk.model.GameUpdateInfo;

/**
 * Created by Win on 15/6/10.
 */
public class ViewControllerNavigator {


    private static final String TAG = "RSDK:ViewControllerNavigator ";

    private static ViewControllerNavigator _instance;

    private TTMainDialog mDialog;//do not access directly

    private ViewControllerNavigator(){

    }

    public static synchronized ViewControllerNavigator getInstance() {
        if (_instance == null) {
            _instance = new ViewControllerNavigator();
        }
        return _instance;
    }


    public void uninit(){
//        mDialog = null;
        _instance = null;
    }

    // ************ Interface ************** //

    /**
     * 宝爷说先不要纠结。做了再说。。
     * 吉：好吧……不好用再说嘛！！
     *
     * @param params
     * @return
     */
    public boolean toLogin(IDialogParam params) {
        Log.d(TAG, "toLogin: ");
        checkParam(params);
        return getDialog(params.getActivityContext())
                .show(new LoginViewController(params.getActivityContext(), params));
//        return getDialog(activityContext).show(new TestViewController(activityContext));
    }

    public boolean toRegister(IDialogParam params) {
        checkParam(params);
        return getDialog(params.getActivityContext())
                .show(new RegisterViewController(params.getActivityContext(), params));
    }

    public boolean toResetPassword(IDialogParam params) {
        checkParam(params);
        return getDialog(params.getActivityContext())
                .show(new ResetPasswordViewController(params.getActivityContext(), params));
    }


    public ExitAlertDialogView toExitAlertDialogView(Context context) {
        ExitAlertDialogView alertDialogView = new ExitAlertDialogView(context);
        getDialog(context).show(alertDialogView);
        return alertDialogView;
    }

    public GameDownloadDialogView toGameDownloadDialogView(Context context , GameUpdateInfo gameUpdateInfo) {
        GameDownloadDialogView alertDialogView = new GameDownloadDialogView(context,gameUpdateInfo);
        TTMainDialog dialog = getDialog(context) ;
        dialog.setCancelable(false);
        dialog.show(alertDialogView);

        return alertDialogView;
    }


    public void close() {
        Log.d(TAG, "close: ");
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
    // ************ Interface END ************** //

    @TargetApi(17)
    private TTMainDialog getDialog(Context context) {
        if (!(context instanceof Activity)) {
            throw new RuntimeException("context is not an activity !!");
        }
        if (mDialog == null) {
            Log.w(TAG, "mDialog is null");
        } else if (mDialog.getOwnerActivity() == null) {
            Log.w(TAG, "getOwnerActivity is null");
        } else if (mDialog.getOwnerActivity() != context) {
            Log.w(TAG, "dialog activity is not the showing one");
        } else if (Build.VERSION.SDK_INT >= 17 && mDialog.getOwnerActivity().isDestroyed()) {
            Log.w(TAG, "ownerActivity is Destroyed");
        }
        if (mDialog == null || mDialog.getOwnerActivity() == null || mDialog.getOwnerActivity() != context) {
            mDialog = new TTMainDialog(context);
            mDialog.setOwnerActivity((Activity) context);
        } else if (Build.VERSION.SDK_INT >= 17 && mDialog.getOwnerActivity().isDestroyed()) {
            mDialog = new TTMainDialog(context);
        }
        return mDialog;
    }

    private void checkParam(IDialogParam params) {
        if (params == null || params.getActivityContext() == null) {
            throw new IllegalArgumentException(ResourceHelper.getString(R.string.exception_param_error));
        }
    }
}
