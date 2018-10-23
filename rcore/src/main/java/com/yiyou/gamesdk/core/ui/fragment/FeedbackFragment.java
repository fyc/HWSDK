package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.FinishFragmentEvent;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.core.ui.dialog.biz.LoadingDialog;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;
import com.yiyou.gamesdk.util.IMEUtil;
import com.yiyou.gamesdk.util.ToastUtils;

import java.util.Map;

/**
 * Created by Nekomimi on 2017/5/25.
 */

public class FeedbackFragment extends BaseFragment {

    private static final String TAG = "FeedbackFragment";
    private CommonTitlePrimaryFragment titlePrimaryFragment;
    private EditText contentEdt;
    private LoadingDialog loadingDialog;

    private boolean isSubmitting = false;

    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        View root = LayoutInflater.from(content).inflate(R.layout.tt_sdk_fragment_feedback, container, false);
        contentEdt = (EditText) root.findViewById(R.id.content_edt);
        loadingDialog = new LoadingDialog(getActivity());
        container.addView(root);
    }

    private void submit(){
        Log.d(TAG, "submit: ");
        if (isSubmitting){
            return;
        }
        if (contentEdt.length() <= 0){
            ToastUtils.showMsg("请输入反馈内容");
            return;
        }
        isSubmitting = true;
        IMEUtil.hideIME(contentEdt);
        loadingDialog.show();
        ApiFacade.getInstance().feedback(contentEdt.getText().toString(),new TtRespListener<Void>(){
            @Override
            public void onNetworkComplete() {
                super.onNetworkComplete();
                isSubmitting = false;
                loadingDialog.dismiss();
            }

            @Override
            public void onNetSucc(String url, Map<String, String> params, Void result) {
                super.onNetSucc(url, params, result);
                Log.d(TAG, "onNetSucc: ");
                ToastUtils.showMsg("提交成功！");
                EventDispatcherAgent.defaultAgent().broadcast(FinishFragmentEvent.TYPE_FINISH_FRAGMENT,
                        new FinishFragmentEvent.Param(0, getActivity(), getParentFragment()));
            }

            @Override
            public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                super.onNetError(url, params, errno, errmsg);
               ToastUtils.showMsg(errmsg);
            }


            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
                ToastUtils.showMsg(errmsg);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (titlePrimaryFragment != null){
            titlePrimaryFragment.setOnConfirmClick(new CommonTitlePrimaryFragment.OnConfirmClick() {
                @Override
                public void onClick(View v) {
                    submit();
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        IMEUtil.hideIME(contentEdt);
        if (titlePrimaryFragment != null) {
            titlePrimaryFragment.setOnConfirmClick(null);
        }
    }

    @Override
    protected Fragment getTitleBarFragment() {
        CommonTitlePrimaryFragment title =  CommonTitlePrimaryFragment.newInstance(getString(R.string.account_feedback));
        title.setOnConfirmClick(new CommonTitlePrimaryFragment.OnConfirmClick() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        titlePrimaryFragment = title;
        return title;
    }


    @Override
    public NativeTitleBarUpdateInfo getTitleBarConfig() {
        NativeTitleBarUpdateInfo info = new NativeTitleBarUpdateInfo();
        info.showBackButton = true;
        info.showCloseButton = false;
//        info.showRefreshButton = false;
        info.showConfirmButton = true;
        return info;
    }

}
