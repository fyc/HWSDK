package com.yiyou.gamesdk.core.ui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mobilegamebar.rsdk.outer.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by shui on 15-8-27.
 */

public class CommDialog extends Dialog {
    private static final int CLOSE = 1;
    private static final String TAG = "CommDialog";

    private MyHanler handler;

    private CommDialog(Activity context) {
//        this(context, 0);
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);//default
        setOwnerActivity(context);
        handler = new MyHanler(this);
    }

    private CommDialog(Activity context, int theme) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);//default
        setOwnerActivity(context);
        handler = new MyHanler(this);
    }


    @Override
    public void show() {
        if (!isShowing()) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    android.util.Log.e(TAG, "show: ");
                    CommDialog.super.show(); //fix 连续close show 导致的 消息队列顺序问题
                }
            });

        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }


    public void dismiss(long delayMillis, DialogMissCallback callback) {
        Message msg = Message.obtain();
        msg.obj = callback;
        msg.what = CLOSE;
        handler.sendMessageDelayed(msg, delayMillis);
    }


    public static class Builder {

        private Activity mContext;

        private IDialogView view;
        private OnDialogClickListener lisenter;
        private ViewGroup.LayoutParams params;

        private boolean full;

        public void setView(@NonNull IDialogView view) {
            this.view = view;
        }

        public void setView(@NonNull IDialogView view, ViewGroup.LayoutParams params) {
            this.view = view;
            this.params = params;
        }

        public void setFullScreen(boolean full) {
            this.full = full;
        }

        public void setLisenter(OnDialogClickListener lisenter) {
            this.lisenter = lisenter;
        }

        public Builder(Activity context) {
            this.mContext = context;
        }

        public CommDialog create() {

            if (view == null)
                throw new IllegalStateException("must be call setView before call create");

//            CommDialog commDialog = null;
//            if (full) {
//                commDialog = new CommDialog(mContext, R.style.DialogFullScreen);
//            } else {
//                commDialog = new CommDialog(mContext);
//            }
            CommDialog commDialog = new CommDialog(mContext);

            if (params == null) {
                commDialog.setContentView(view.getView());
            } else {
                commDialog.setContentView(view.getView(), params);
            }

            if (full && commDialog.getWindow()!=null){
//                Window window = commDialog.getWindow();
//                window.getDecorView().setPadding(0, 0, 0, 0);
//                window.setGravity(Gravity.CENTER);
//                WindowManager.LayoutParams lp = window.getAttributes();
//                lp.width = width;
//                lp.height = height;
//                window.setAttributes(lp);
                commDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                commDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            }

            view.attach(commDialog, lisenter);

            return commDialog;
        }

    }


    private static class MyHanler extends Handler {


        private WeakReference<CommDialog> weakReference;


        public MyHanler(CommDialog dialog) {
            this.weakReference = new WeakReference<>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {

            CommDialog dialog = weakReference.get();
            if (dialog == null) {
                Log.e(TAG, "dialog is null msg= " + msg.what);
                return;
            }


            switch (msg.what) {

                case CLOSE:
                    dialog.dismiss();

                    Object obj = msg.obj;
                    if (obj != null && obj instanceof DialogMissCallback) {
                        ((DialogMissCallback) obj).done();

                    }

                    break;


            }

        }
    }


    public interface DialogMissCallback {
        void done();
    }


}
