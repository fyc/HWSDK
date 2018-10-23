package com.yiyou.gamesdk.core.base.web;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import com.mobilegamebar.rsdk.outer.util.Log;

/**
 * Created by levyyoung on 15/8/4.
 */
public class WebViewPlus extends WebView {

    public static final String TAG = "[TTSDK:Web]";

    public WebViewPlus(Context context) {
        super(context);
//        setOnTouchListener(new WebViewOnTouchListener());
//        setInitialScale(0);
//        setVerticalScrollBarEnabled(false);
//        setHorizontalScrollBarEnabled(false);
//        requestFocusFromTouch();
//        requestFocus();
//        setFocusable(true);

    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        disableGPU(canvas);
//        super.onDraw(canvas);
//    }

    @TargetApi(11)
    private void disableGPU(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                if (canvas.isHardwareAccelerated()) {
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
            } catch (Exception e) {
                Log.e(TAG, "disable GPU " + e + " " + canvas);
            }
        }
    }

    public static class WebViewOnTouchListener implements OnTouchListener{
        public boolean scrollable = true;
        @Override
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
            return (!this.scrollable) && (paramMotionEvent.getAction() == 2);
        }
    }


}
