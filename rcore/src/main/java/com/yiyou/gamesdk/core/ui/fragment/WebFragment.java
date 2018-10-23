package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mobilegamebar.rsdk.outer.util.IBackPressedHandler;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.base.web.WebViewBuilder;
import com.yiyou.gamesdk.core.base.web.jsi.JSBridge;
import com.yiyou.gamesdk.core.base.web.jsi.TTCompactDelegate;
import com.yiyou.gamesdk.core.base.web.jsi.TTCompactJSAPI;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.util.CommonUtils;
import com.yiyou.gamesdk.util.PhoneUtils;

import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 由客服端自由控制页面
 * Created by charles on 6/6/16.
 */
public abstract class WebFragment extends BaseFragment implements IBackPressedHandler, TTCompactDelegate {

    private static final String TAG = "RSDK:WebFragment";
    private WebView webView;
    TTCompactJSAPI compactTTJS;
    private ProgressBar progressBar;
    CommonTitlePrimaryFragment titlePrimaryFragment;

    @Override
    protected void setFragmentContent(Context content, final ViewGroup container, final Fragment titleBarFragment) {
        webView = obtainWebView(content);
        if (titleBarFragment instanceof CommonTitlePrimaryFragment){
            titlePrimaryFragment = (CommonTitlePrimaryFragment) titleBarFragment;
        }
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setDomStorageEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
//        webView.setWebViewClient(new WebViewClient());
        webView.setVerticalScrollBarEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
//                updateTitle();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return CommonUtils.handleUrl(url, getActivity(), false);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                JSBridge.dispatchJSBridgeReadyEvent(webView);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                Log.d(TAG, "onReceivedTitle: " + title);
                updateTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100) {
                    handler.removeCallbacks(progressTask);
                    Log.d(TAG, "进度条完成");
//                    container.setBackgroundColor(Color.TRANSPARENT);
                    progressBar.setVisibility(View.GONE);
                } else {
                    if (newProgress > progressBar.getProgress()) {
                        progressBar.setProgress(newProgress);
                    }
                    handler.removeCallbacks(progressTask);
                    handler.postDelayed(progressTask, INCREASE_DURATION);
                }
                super.onProgressChanged(view, newProgress);

            }
        });
//        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (Build.VERSION.SDK_INT >= 24) {
            webView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "屏蔽webView长按");
                    return true;
                }
            });
        }
        compactTTJS.setWebView(webView);
        container.addView(webView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        container.setBackgroundColor(getContext().getResources().getColor(R.color.bg_translucence));
        loadUrl(getUrl());

        progressBar = (ProgressBar) container.findViewById(R.id.web_view_progressbar);
        progressBar.bringToFront();
    }

    public void refresh() {
        if (webView != null) webView.reload();
    }


    protected WebView obtainWebView(Context context) {
        WebViewBuilder builder = WebViewBuilder.obtain();
        Map<String, Object> jsi = new ArrayMap<>();
        compactTTJS = new TTCompactJSAPI(context, this);
        jsi.put(TTCompactJSAPI.NAME, compactTTJS);
        return builder.addJavascriptInterface(jsi).build(context);
    }


    public abstract String getUrl();

    private void loadUrl(String url) {
        webView.loadUrl(url);
    }

    public void updateTitle(String title) {
        if (titlePrimaryFragment!= null) {
            if (isChineseByReg(title)) {
                Log.d(TAG, "updateTitle: " + title);
                titlePrimaryFragment.updateViewTitleContent(title);
            }
        }
    }

    /**
     * 是否全是汉字<br>
     * @param str
     * @return
     */
    public  boolean isChineseByReg(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
        return pattern.matcher(str).matches();
    }


    public boolean canGoBack() {
        return webView.canGoBack();
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return CommonTitlePrimaryFragment.newInstance("");
    }


    @Override
    public boolean onBackPressed() {

        if (webView == null) {
            return false;
        }

        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {

            boolean flag = false;
            try {
                flag = (this.getClass().newInstance() instanceof WalletFragment) ||
                        (this.getClass().newInstance() instanceof MessageInfoFragment);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "flag: " + flag);
            if (flag) {
//                EventDispatcherAgent.defaultAgent().broadcast(FinishFragmentEvent.TYPE_FINISH_FRAGMENT,
//                        new FinishFragmentEvent.Param(0, getActivity(), getParentFragment()));
//                return true;
                return false;
            } else {
                PhoneUtils.closeInput(getActivity());
                getActivity().finish();
                return true;
            }
        }
    }

    private static final int FAKE_MAX_PROGRESS = 95;
    private static final int INCREASE_DURATION = 500;
    private Random random;
    private Handler handler = new Handler();
    private Runnable progressTask = new Runnable() {
        @Override
        public void run() {
            if (progressBar != null) {
                int progress = progressBar.getProgress();
                if (random == null) {
                    random = new Random();
                }
                int current = progress + 3 + random.nextInt(10);
                if (current > FAKE_MAX_PROGRESS) {
                    current = FAKE_MAX_PROGRESS;
                } else {
                    handler.postDelayed(progressTask, INCREASE_DURATION);
                }

                progressBar.setProgress(current);
            }
        }
    };


    @Override
    protected int getDefaultFragmentLayoutResId() {
        return R.layout.fragment_web_default;
    }

    @Override
    public void finish() {
        getActivity().finish();
    }
}
