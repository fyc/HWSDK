package com.yiyou.gamesdk.core.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.yiyou.gamesdk.R;

/**
 * Created by win on 2017/4/28.
 */
public class YunWeiDialogView implements IDialogView ,View.OnClickListener{

    private View layout;
    private OnDialogClickListener lisenter;
    private Dialog dialog;
    private TextView htmlText;
    private ImageView Iv_close;
    private TextView tv_title;

    public YunWeiDialogView(Context context){
        initView(context);
    }

    private void initView(Context context){
        layout = LayoutInflater.from(context).inflate(R.layout.tt_sdk_before_login_notice, null);
        htmlText = (TextView) layout.findViewById(R.id.textView);
        tv_title = (TextView) layout.findViewById(R.id.popup_title);
        Iv_close = (ImageView) layout.findViewById(R.id.back_icon);
        Iv_close.setOnClickListener(this);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_icon:
                if (dialog != null)
                    dialog.dismiss();
                break;
        }
    }
}
