package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.View;

import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.utils.HttpUtils;
import com.yiyou.gamesdk.core.base.http.utils.Urlpath;
import com.yiyou.gamesdk.core.storage.events.NativeTitleBarUpdateEvent;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;

import java.util.Map;

public class GiftFragment extends WebFragment {

    private static final String TAG = "GiftFragment";
    private View contentView;

    public static GiftFragment newInstance() {
        GiftFragment fragment = new GiftFragment();
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
        Map<String,String> params = new ArrayMap<>();
        params.put("accessToken", ApiFacade.getInstance().getSession());
        params.put("userName",ApiFacade.getInstance().getUserName());
        params.put("gameId",ApiFacade.getInstance().getCurrentGameID() + "");
//        params.put("uid",ApiFacade.getInstance().getUid()+"");
        return HttpUtils.buildUrl(Urlpath.GIFT_CENTER,params);
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void updateTitle(String title){
        updateTitleBar();
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible){
            updateTitleBar();
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        updateTitleBar();
    }

    /**
     * 子类可以重写此方法来修改titlebar配置
     *
     * @return NativeTitleBarUpdateInfo
     */
    @Override
    public NativeTitleBarUpdateInfo getTitleBarConfig() {
        NativeTitleBarUpdateInfo titleBarUpdateInfo = new NativeTitleBarUpdateInfo();
        titleBarUpdateInfo.showBackButton = false;
//        titleBarUpdateInfo.showRefreshButton = false;
        titleBarUpdateInfo.showCloseButton = true;
        return titleBarUpdateInfo;
    }

    @Override
    public String getTabName() {
        return "礼包";
    }

    public void updateTitleBar(){
        NativeTitleBarUpdateInfo info = getTitleBarConfig();
        info.showBackButton = canGoBack();
        info.showCloseButton = !canGoBack();
        EventDispatcherAgent.defaultAgent().broadcast(NativeTitleBarUpdateEvent.TYPE_ON_NATIVE_TITLE_BAR_UPDATE,
                new NativeTitleBarUpdateEvent.NativeTitleBarUpdateEventParam(info));
    }
}
