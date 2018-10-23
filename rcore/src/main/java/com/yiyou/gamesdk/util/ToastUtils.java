package com.yiyou.gamesdk.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yiyou.gamesdk.R;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;

import java.lang.ref.WeakReference;

/**
 * Created by chenshuide on 15/6/2.
 */
public class ToastUtils {

    private static final String TAG = "RSDK:ToastUtils ";
    private static WeakReference<Toast> toastInstanceHolder;

    /**
     * note 传入的context必须是重写getResource()之后的context
     *
     * @param context note 传入的context必须是重写getResource()之后的context
     * @param msg     message
     */
    private synchronized static void showMsg(final Context context, final String msg, final int durationType) {

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = null;
                if (toastInstanceHolder != null) {
                    toast = toastInstanceHolder.get();
                }

                if (toast == null) {
                    toast = new Toast(context);
                    View customView = LayoutInflater.from(context)
                            .inflate(R.layout.tt_sdk_custom_toast, null);
                    toast.setView(customView);
                    toastInstanceHolder = new WeakReference<Toast>(toast);
                }

                toast.setDuration(durationType);
                TextView contentText = (TextView) toast.getView().findViewById(R.id.toast_content);
                contentText.setText(msg);

                toast.show();
            }
        });
    }

    public static void showMsg(String msg, int durationType) {
        WeakReference<Context> contextHolder = ResourceHelper.getContextHolder();
        if (contextHolder == null){
            return;
        }

        Context context = contextHolder.get();
        if (context == null) {
            return;
        }
        if (durationType > 1) {
            durationType = Toast.LENGTH_LONG;
        }
        if (durationType < 0) {
            durationType = Toast.LENGTH_SHORT;
        }
        showMsg(context, msg, durationType);
    }

    public static void showMsg(String msg) {
        showMsg(msg, Toast.LENGTH_LONG);
    }

    public static void showMsg(int resid) {
        Context context = ResourceHelper.getContextHolder().get();
        if (context == null) {
            return;
        }

        showMsg(context, resid);
    }

    /**
     * note 传入的context必须是重写getResource()之后的context
     *
     * @param context note 传入的context必须是重写getResource()之后的context
     * @param resid   资源id
     */
    public static void showMsg(Context context, int resid) {
        showMsg(context, context.getString(resid), Toast.LENGTH_LONG);
    }


    public static void dismiss() {
        if (toastInstanceHolder == null)
            return;
        Toast toast = toastInstanceHolder.get();
        if (toast == null)
            return;
        Log.d(TAG, "toast cancel");
        toast.cancel();
        toastInstanceHolder.clear();

    }

}
