package com.yiyou.gamesdk.core.ui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.util.CommonUtils;

/**
 * Created by LY on 2015/12/7.
 */
public class MessageTipView implements IDialogView, ImageView.OnClickListener {

    private View layout;
    private OnDialogClickListener lisenter;
    private Dialog dialog;
    private ImageView Iv_close;
    private TextView tv_title;
    private WebView messageUrl;

    private Activity mActivity;
    private Context mContext;
    private TextView htmlText;
    private int msgid = 0;
    private final int LOCK = 2;
    private final int NORMAL = 3;
    private final int NORMAL_WITHOUT_BTN = 0;
    private final int SMALL = 1;
    private final int FLOAT = 4;
    private final int LOTTERY = 5;
    private final int YUNWEI = 6;
    private final int GODDESS = 7;
    private final int EXIT = 8;
    private final int HOLD = 100;


    protected static final String TAG = "TTSDK: " + "MessageTipView";

    public MessageTipView(Context context, int MessageType, Activity activity, int messageID) {
        mContext = context;
        msgid = messageID;
        initView(context, MessageType, activity);
    }

    public MessageTipView(Context context, int MessageType, Activity activity) {
        mContext = context;
        initView(context, MessageType, activity);


    }

    private void initView(Context context, int MessageType, Activity activity) {
        switch (MessageType) {
            case NORMAL_WITHOUT_BTN:
                layout = LayoutInflater.from(context).inflate(R.layout.tt_sdk_notice_normal, null);
                break;
            case SMALL:
                layout = LayoutInflater.from(context).inflate(R.layout.tt_sdk_notice_small, null);
                break;
            default:
                layout = LayoutInflater.from(context).inflate(R.layout.tt_sdk_notice_normal, null);
        }


        Iv_close = (ImageView) layout.findViewById(R.id.back_icon);
        Iv_close.setOnClickListener(this);
        tv_title = (TextView) layout.findViewById(R.id.popup_title);


        messageUrl = (WebView) layout.findViewById(R.id.webView);
        messageUrl.setVerticalScrollBarEnabled(true);
        messageUrl.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        messageUrl.setBackgroundColor(0);
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                messageUrl.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            } catch (Exception e) {
                Log.e(TAG, "disable GPU " + e + " " + messageUrl);
            }
        }

        //启用支持javascript
        WebSettings settings = messageUrl.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8") ;


        mActivity = activity;

    }


    public void setWebView(String url) {
        messageUrl.loadUrl(url);
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setHtmlText(String content) {
        Spannable sp = (Spannable) Html.fromHtml(content);
        final URLSpan[] urls = sp.getSpans(0, sp.length(), URLSpan.class);

        if (urls.length == 0) {
            htmlText.setText(Html.fromHtml(content));
        } else {
            htmlText.setText(Html.fromHtml(content));
            htmlText.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableStringBuilder html = (SpannableStringBuilder) Html.fromHtml(content);
            html.clearSpans();
            html.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Uri uri = Uri.parse(urls[0].getURL());
                    Context context = widget.getContext();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                    context.startActivity(intent);
                }
            }, sp.getSpanStart(urls[0]), sp.getSpanEnd(urls[0]), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            htmlText.setText(html);
        }
    }

    //处理特殊跳转
    public void respondHyperlink(final Activity activity) {
        messageUrl.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "load url:" + url);
                if (CommonUtils.handleUrl(url, activity, true)){
                    return true;
                }else {
                    return super.shouldOverrideUrlLoading(view,url);
                }
            }
        });
    }

    public void disableclose(boolean flag) {
        if (flag) {
            Iv_close.setVisibility(View.GONE);
        } else {
            Iv_close.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View getView() {
        return layout;
    }

    @Override
    public void attach(@NonNull Dialog dialog, OnDialogClickListener lisenter) {
        this.dialog = dialog;
        this.lisenter = lisenter;
    }

    @Override
    public void close() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_icon:
                if (dialog != null)
                    dialog.dismiss();
                break;
        }
    }
}
