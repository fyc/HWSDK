package com.yiyou.gamesdk.core.base.web;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.webkit.WebView;

import java.util.Map;

/**
 * Created by levyyoung on 15/5/29.
 */
public class WebViewBuilder {

    private static final String TAG = "TTSDK: "+"WebViewBuilder";

    private WebView finalWebView;

    private Map<String, Object> jsInterfaces = new ArrayMap<>();

    private WebViewBuilder() {

    }

    public static WebViewBuilder obtain() {
        return new WebViewBuilder();
    }

    public WebViewBuilder addJavascriptInterface(Map<String, Object> interfaces) {
        if (interfaces != null) {
            jsInterfaces.putAll(interfaces);
        }
        return this;
    }

    @TargetApi(16)
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public WebView build(Context context) {
        finalWebView = new WebViewPlus(context);
        finalWebView.getSettings().setJavaScriptEnabled(true);


//        finalWebView.clearCache(false);
//        finalWebView.destroyDrawingCache();
//        if (Build.VERSION.SDK_INT >= 18) {
//            //webView.clearView method was deprecated in API level 18.
//            //Use WebView.loadUrl("about:blank") to reliably reset the view state
//            // and release page resources (including any running JavaScript).
//            finalWebView.loadUrl("about:blank");
//        }
//        if (Build.VERSION.SDK_INT >= 21) {
//            WebView.enableSlowWholeDocumentDraw();
//        }
//
////        finalWebView.setWebViewClient(new WebViewClient());
//        WebSettings webSettings = finalWebView.getSettings();
//        webSettings.setDefaultTextEncodingName("utf-8");
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setNeedInitialFocus(true);
//        webSettings.setSaveFormData(false);
//        webSettings.setSavePassword(false);
//        webSettings.setBuiltInZoomControls(false);
//        webSettings.setSupportZoom(true);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setAllowFileAccess(true);
//        webSettings.setCacheMode(-1);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setAppCachePath(context.getDir("cache", Context.MODE_PRIVATE).getPath());
//        //API19以下指定DB路径
//        webSettings.setDatabasePath(context.getDir("databases", Context.MODE_PRIVATE).getPath());
//        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);// to compat version below api 18;
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setDatabaseEnabled(true);
//        webSettings.setAppCacheEnabled(true);

        for (Map.Entry<String, Object> ji : jsInterfaces.entrySet()) {
            finalWebView.addJavascriptInterface(ji.getValue(), ji.getKey());
        }

        return finalWebView;
    }

}
