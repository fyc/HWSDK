package com.yiyou.gamesdk.core.base.web.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;

import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.StringUtils;

/**
 * Created by levyyoung on 15/6/5.
 */
public class WebViewUtil {
    public static final String TAG = "TTSDK: "+"WebViewUtil";

    @TargetApi(19)
    public static void evaluateJavascript(WebView webView, String script) {
        if (StringUtils.isBlank(script)) {
            Log.w(TAG,"error to evaluateJavascript. empty script.");
            return;
        }
        if (!script.startsWith("javascript:")) {
            script = "javascript:" + script;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            webView.evaluateJavascript(script, null);
        } else {
            webView.loadUrl(script);
        }
    }


    public static void evaluateJavaScriptV2(final WebView webView, final String javascript) {
        Log.d(TAG , "evaluateJavaScript " + javascript);

        if (((Activity)webView.getContext()).isFinishing()) {
            return;
        }

        if (Build.VERSION.SDK_INT <= 18) {
            safetyLoadUrl(webView, javascript);
            return;
        }

        if (!TextUtils.isEmpty(javascript)) {
            ((Activity) webView.getContext()).runOnUiThread(new Runnable() {
                @TargetApi(19)
                @Override
                public void run() {
                    try {
                        if (Build.VERSION.SDK_INT > 18) {
                            webView.evaluateJavascript(javascript, null);
//                            webView.loadUrl(javascript);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "e = ", e);
                    }
                }
            });
        }
    }

    public static void safetyLoadUrl(final WebView webView, final String url) {
        if (webView != null && url != null) {
            ((Activity) webView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        webView.loadUrl(url);
                    } catch (Exception e) {
                        Log.e(TAG, "e = ", e);
                    }
                }
            });
        }
    }
}
