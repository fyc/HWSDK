package com.yiyou.gamesdk.core.ui.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.yiyou.gamesdk.core.base.http.utils.Urlpath;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;

/**
 * Created by LY on 2016/6/15.
 */
public class LicenseFragment extends WebFragment {
    @Override
    public String getUrl() {
        return Urlpath.SERVICE_TERMS;
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return CommonTitlePrimaryFragment.newInstance("服务条款");
    }

    @Override
    public NativeTitleBarUpdateInfo getTitleBarConfig() {
        NativeTitleBarUpdateInfo updateInfo = new NativeTitleBarUpdateInfo();
        updateInfo.showBackButton = false;
        updateInfo.showCloseButton = true;
//        updateInfo.showRefreshButton = false;
        updateInfo.showConfirmButton = false;
        return updateInfo;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
