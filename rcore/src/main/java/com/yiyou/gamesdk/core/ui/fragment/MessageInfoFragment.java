package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;

/**
 * Created by Nekomimi on 2017/11/14.
 */

public class MessageInfoFragment extends WebFragment{

    private String url;
    private String title;
    private String time;

    @Override
    public void updateTitle(String title) {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        super.setFragmentContent(content, container, titleBarFragment);
        TextView title = (TextView)container.findViewById(R.id.message_title);
        TextView time = (TextView)container.findViewById(R.id.message_time);

        time.setText(this.time);
        title.setText(this.title);
    }

    @Override
    protected WebView obtainWebView(Context context) {
        WebView webView = super.obtainWebView(context);
        webView.setVerticalScrollBarEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setBackgroundColor(0);
        try {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return webView;
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return CommonTitlePrimaryFragment.newInstance("通知详情");
    }

    @Override
    public NativeTitleBarUpdateInfo getTitleBarConfig() {
        NativeTitleBarUpdateInfo info = new NativeTitleBarUpdateInfo();
        info.showBackButton = true;
        info.showCloseButton = false;
        return info;
    }

    @Override
    protected int getCustomFragmentLayoutResId() {
        return R.layout.sdk_fragment_web_message;
    }
}