package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.consts.StartType;
import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.yiyou.gamesdk.PluginManager;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.core.ui.dialog.biz.LoadingDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.AlertDialogView;
import com.yiyou.gamesdk.core.ui.widget.dialog.CommDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.IDialogView;
import com.yiyou.gamesdk.core.ui.widget.dialog.OnDialogClickListener;
import com.yiyou.gamesdk.model.AccountHistoryInfo;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;
import com.yiyou.gamesdk.util.ToastUtils;

import java.util.Map;

/**
 * 修改密码fragment
 * Created by Nekomimi on 2017/4/26.
 */

public class ResetPasswordFragment extends BaseFragment implements View.OnClickListener{
    private static final String TAG = "ResetPasswordFragment";

    private EditText oldPwdEdt;
    private EditText newPwdEdt;
    private EditText newPwdReEdt;
    private Button confirmBtn;
    private TextView forgetPwdBtn;
    private LoadingDialog loadingDialog;

    private String requestOldPwdInput = "";
    private String requestNewPwdInput = "";

    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        View root = LayoutInflater.from(content).inflate(R.layout.tt_sdk_fragment_reset_password, container, false);
        oldPwdEdt = (EditText)root.findViewById(R.id.edt_old_pwd);
        newPwdEdt = (EditText)root.findViewById(R.id.edt_new_pwd);
        newPwdReEdt = (EditText)root.findViewById(R.id.edt_new_pwd_re);
        confirmBtn = (Button)root.findViewById(R.id.btn_confirm);
        forgetPwdBtn = (TextView)root.findViewById(R.id.btn_forget_pwd);
        confirmBtn.setEnabled(false);
        confirmBtn.setOnClickListener(this);
        forgetPwdBtn.setOnClickListener(this);
        loadingDialog = new LoadingDialog(getActivity());
        addTextWatcher(oldPwdEdt, newPwdEdt, newPwdReEdt);
        container.addView(root);

    }

    private void updateConfirmBtnState(){
        if (oldPwdEdt.length() > 0 && newPwdReEdt.length() > 0 && newPwdEdt.length() > 0){
            confirmBtn.setEnabled(true);
        }else {
            confirmBtn.setEnabled(false);
        }
    }

    private void addTextWatcher(EditText... editTexts ){
        for (final EditText editText : editTexts){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    updateConfirmBtnState();
                }
            });
        }
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return CommonTitlePrimaryFragment.newInstance(getString(R.string.account_reset_passwd));

    }

    @Override
    public NativeTitleBarUpdateInfo getTitleBarConfig() {
        NativeTitleBarUpdateInfo info = new NativeTitleBarUpdateInfo();
        info.showBackButton = true;
        info.showCloseButton = false;
//        info.showRefreshButton = false;
        info.showConfirmButton = false;
        return info;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm:
                checkInput();
                break;
            case R.id.btn_forget_pwd:
                forgetPwd();
                break;
            default:
                break;
        }
    }

    private void checkInput(){
        String tip = "";
        if (oldPwdEdt.length()<6 || oldPwdEdt.length() > 16){
            tip = "请输入正确的登录密码";
        }else if (newPwdEdt.length()<6 || newPwdEdt.length() > 16 || newPwdReEdt.length() < 6 || newPwdReEdt.length() > 16){
            tip = "请输入6 - 16 位的新密码";
        }else if(!newPwdEdt.getText().toString().equals(newPwdReEdt.getText().toString())){
            tip = "两次输入不同！";
        }
        if (!TextUtils.isEmpty(tip)){
            ToastUtils.showMsg(tip);
            return;
        }
        resetPassword();
    }

    private void putRequesetInput(String oldPwd, String newPwd){
        requestOldPwdInput = oldPwd;
        requestNewPwdInput = newPwd;
    }

    private void clearRequestInput(){
        requestOldPwdInput = "";
        requestNewPwdInput = "";
    }

    private void resetPassword(){
        putRequesetInput(oldPwdEdt.getText().toString(), newPwdEdt.getText().toString());
        loadingDialog.show();
        ApiFacade.getInstance().modifyPassword(requestOldPwdInput, requestNewPwdInput, new TtRespListener(){
            @Override
            public void onNetSucc(String url, Map params, Object result) {
                super.onNetSucc(url, params, result);
                ToastUtils.showMsg(R.string.modify_password_succ);
                AccountHistoryInfo info = ApiFacade.getInstance().getCurrentHistoryAccount();
                if (info!=null){
                    info.password = (String)params.get("newpwd");
                    ApiFacade.getInstance().insertOrUpdateAccountHistory(info);
                }
                ApiFacade.getInstance().logout(new IOperateCallback<String>() {
                    @Override
                    public void onResult(int i, String s) {
                        if (i == TTCodeDef.SUCCESS){
                            getActivity().finish();
                            if (PluginManager.getInstance().getLogoutCallback()!= null){
                                PluginManager.getInstance().getLogoutCallback().onResult(i,s);
                            }
                        }
                    }
                });
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
                Log.d(TAG, "onFail: " + errmsg);
                clearRequestInput();
            }

            @Override
            public void onNetError(String url, Map params, String errno, String errmsg) {
                super.onNetError(url, params, errno, errmsg);
                Log.d(TAG, "onNetError: " + errmsg);
                clearRequestInput();
            }

            @Override
            public void onNetworkComplete() {
                super.onNetworkComplete();
                loadingDialog.dismiss();
            }
        });

    }

    private void forgetPwd(){
        String phone = ApiFacade.getInstance().getPhone();
        if (TextUtils.isEmpty(phone)){
            CommDialog.Builder builder = new CommDialog.Builder(getActivity());
            AlertDialogView dialogView = new AlertDialogView(getActivity());
            dialogView.setTitleTip(0);
            dialogView.setMessageTip(R.string.str_need_bind_phone);
            dialogView.setEnsureText(R.string.bind_phone);
            dialogView.setCancelText(R.string.str_not_bind);
            builder.setView(dialogView);
            builder.setLisenter(new OnDialogClickListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onEnsure(IDialogView view) {
//                    ToastUtils.showMsg("go to reset password");
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(StartType.KEY_START_NEW_AND_COLSE_ME,true);
//                    EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                            new StartActivityEvent.FragmentParam(0, getActivity(), BindPhoneFragment.class.getName(), bundle));
                    startFragment(new BindPhoneFragment());
//                    AppInternalHandler.bindPhone(getActivity());
                }
            });
            builder.create().show();
        }else {
            Bundle bundle = new Bundle();
            bundle.putBoolean(StartType.KEY_START_NEW_AND_COLSE_ME,true);
//            EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                    new StartActivityEvent.FragmentParam(0, getActivity(), ForgetPasswordFragment.class.getName(), bundle));
            startFragment(new ForgetPasswordFragment());
        }


    }


}
