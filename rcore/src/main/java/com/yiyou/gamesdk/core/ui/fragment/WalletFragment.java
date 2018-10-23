package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.webkit.WebView;

import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.utils.HttpUtils;
import com.yiyou.gamesdk.core.base.http.utils.Urlpath;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;

import java.util.Map;

/**
 *
 * Created by Nekomimi on 2017/5/8.
 */

public class WalletFragment extends WebFragment {

    private static final String TAG = "RSDK: WalletFragment";

    public static WalletFragment newInstance() {
        WalletFragment fragment = new WalletFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    protected void afterSetFragmentContent(Context context, Fragment titleBarFragment) {
        super.afterSetFragmentContent(context, titleBarFragment);
        ApiFacade.getInstance().updatePaymentActivity(getActivity());
    }

    //    @Override
//    protected void setWebViewFragmentContent(Context context, ViewGroup container) {
//        super.setWebViewFragmentContent(context, container);
//
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public String getUrl() {
//        String key = "2ae000b562cba514";
        Map<String,String> params = new ArrayMap<>();
//        params.put("caller","SDK");
//        params.put("uid",ApiFacade.getInstance().getMainUid()+"");
//        String content = new StringBuilder("{\"caller\":\"SDK\",\"uid\":\"").append(ApiFacade.getInstance().getMainUid()).append("\"}").toString();
        params.put("type","sdk");
        params.put("accessToken",ApiFacade.getInstance().getSession());
        params.put("gameId",ApiFacade.getInstance().getCurrentGameID() + "");
//        params.put("sdkVersion", Vers ionUtil.getSdkVersion());
//        params.put("osType","a");
//        Log.d(TAG, "content: " + content);
//        params.put("sign", ByteUtils.generateMd5V2(content+key));
        return HttpUtils.buildUrl(Urlpath.WALLET,params);
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return CommonTitlePrimaryFragment.newInstance(ResourceHelper.getString(R.string.title_wallet));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 子类可以重写此方法来修改titlebar配置
     *
     * @return NativeTitleBarUpdateInfo
     */
    @Override
    public NativeTitleBarUpdateInfo getTitleBarConfig() {
        NativeTitleBarUpdateInfo titleBarUpdateInfo = new NativeTitleBarUpdateInfo();
        titleBarUpdateInfo.showBackButton = true;
//        titleBarUpdateInfo.showRefreshButton = false;
        titleBarUpdateInfo.showCloseButton = false;
        titleBarUpdateInfo.showConfirmButton = false;
        return titleBarUpdateInfo;
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
}
