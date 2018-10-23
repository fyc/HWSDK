package com.yiyou.gamesdk.core.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.mobilegamebar.rsdk.outer.util.Log;

/**
 * Created by Orange on 15/6/9.
 */
public final class TTMainDialog extends Dialog {
    private static final String TAG = "RSDK:TTMainDialog ";

    private IDialogContent mShowingDialogContent;

    /**
     * @param context           必须是activity的context哦哦哦哦哦
     */
    TTMainDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                notifyOnHide();
                notifyOnDismiss();
            }
        });
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mShowingDialogContent != null) {
                        return mShowingDialogContent.onBackPressed();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
    }

    public boolean show(IDialogContent contentView) {
        if (contentView == null) {
            Log.w(TAG, "contentView is null");
            return false;
        }
        if (contentView.getContentView() == null) {
            Log.w(TAG, "contentView content is null");
            return false;
        }

        notifyOnHide();
        setContentView(contentView.getContentView());
        mShowingDialogContent = contentView;
        mShowingDialogContent.onShow();

        if (getOwnerActivity() == null){
            Log.d(TAG,"owner activity is null");
            return false;
        }

        if (getOwnerActivity().isFinishing())
            return false;


        if (!isShowing()) {
            super.show();
        }
        return true;
    }

    private void notifyOnHide() {
        if (mShowingDialogContent != null) {
            mShowingDialogContent.onHide();
        }
    }

    private void notifyOnDismiss() {
        if (mShowingDialogContent != null) {
            mShowingDialogContent.onDismiss();
        }
    }

}
