package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.FinishFragmentEvent;
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
import com.yiyou.gamesdk.util.IMEUtil;
import com.yiyou.gamesdk.util.ToastUtils;

import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Nekomimi on 2017/5/2.
 */

public class PayPasswordFragment extends BaseFragment implements View.OnClickListener{

    private LinearLayout newPayPasswordLL;
    private LinearLayout changePayPasswordLL;

    private EditText oldPayPwdEdt;
    private EditText newPayPwdEdt;
    private EditText newPayPwdReEdt;
    private EditText payPwdEdt;
    private Button confirmBtn;
    private TextView forgetPwdBtn;
    private LoadingDialog loadingDialog;

    private String requestOldPwdInput = "";
    private String requestNewPwdInput = "";
    private String requestPwdInput = "";


    private boolean hasPayPassword(){
        return ApiFacade.getInstance().getCurrentHistoryAccount().hasPayPassword;
    }


    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        View root = LayoutInflater.from(content).inflate(R.layout.tt_sdk_fragment_pay_password,container,false);
        newPayPasswordLL = (LinearLayout)root.findViewById(R.id.ll_new_pay_password);
        changePayPasswordLL = (LinearLayout)root.findViewById(R.id.ll_change_pay_password);
        oldPayPwdEdt = (EditText)root.findViewById(R.id.edt_old_pay_pwd);
        newPayPwdEdt = (EditText)root.findViewById(R.id.edt_new_pay_pwd);
        newPayPwdReEdt = (EditText)root.findViewById(R.id.edt_new_pay_pwd_re);
        payPwdEdt = (EditText)root.findViewById(R.id.edt_pay_password);
        confirmBtn = (Button)root.findViewById(R.id.btn_confirm);
        forgetPwdBtn = (TextView)root.findViewById(R.id.btn_forget_pay_pwd);
        loadingDialog = new LoadingDialog(getActivity());
        if (hasPayPassword()){
            newPayPasswordLL.setVisibility(GONE);
            changePayPasswordLL.setVisibility(VISIBLE);
            confirmBtn.setText(R.string.btn_change_pay_password);
        }else {
            newPayPasswordLL.setVisibility(VISIBLE);
            changePayPasswordLL.setVisibility(GONE);
            confirmBtn.setText(R.string.btn_set_pay_password);
        }
        confirmBtn.setOnClickListener(this);
        forgetPwdBtn.setOnClickListener(this);
        addTextWatcher(oldPayPwdEdt,newPayPwdEdt,newPayPwdReEdt,payPwdEdt);
        container.addView(root);
    }

    private void updateConfirmBtnState(){
        if (hasPayPassword()){
            if (oldPayPwdEdt.length() > 0 && newPayPwdReEdt.length() > 0 && newPayPwdEdt.length() > 0){
                confirmBtn.setEnabled(true);
            }else {
                confirmBtn.setEnabled(false);
            }
        }else {
            confirmBtn.setEnabled( payPwdEdt.length() > 0 );
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
        return CommonTitlePrimaryFragment.newInstance(getString( hasPayPassword() ? R.string.title_change_pay_password : R.string.title_pay_password));
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
                if (hasPayPassword()){
                    resetPayPassword();
                }else {
                    setPayPassword();
                }
                IMEUtil.hideIME(v);
                break;
            case R.id.btn_forget_pay_pwd:
                forgetPayPwd();
                break;
            default:
                break;
        }
    }

    private void clearRequestInput(){
        requestNewPwdInput = "";
        requestOldPwdInput = "";
        requestPwdInput = "";
    }


    private void resetPayPassword(){
        if (oldPayPwdEdt.length() < 6 || oldPayPwdEdt.length() > 16){
            ToastUtils.showMsg(R.string.hint_pay_password);
            return;
        }
        if (newPayPwdEdt.length() < 6 || newPayPwdEdt.length() > 16){
            ToastUtils.showMsg(R.string.hint_pay_password);
            return;
        }
        if (newPayPwdReEdt.length() < 6 || newPayPwdReEdt.length() > 16){
            ToastUtils.showMsg(R.string.hint_pay_password);
            return;
        }
        if (!newPayPwdReEdt.getText().toString().equals(newPayPwdEdt.getText().toString())){
            ToastUtils.showMsg("两次密码输入不一致");
            return;
        }
        requestNewPwdInput = newPayPwdEdt.getText().toString();
        requestOldPwdInput = oldPayPwdEdt.getText().toString();
        loadingDialog.show();
        ApiFacade.getInstance().modifyPayPassword(requestOldPwdInput,requestNewPwdInput,new TtRespListener(){
            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
                clearRequestInput();
            }

            @Override
            public void onNetSucc(String url, Map params, Object result) {
                super.onNetSucc(url, params, result);
                ToastUtils.showMsg("修改支付密码成功");
                clearRequestInput();
                EventDispatcherAgent.defaultAgent().broadcast(FinishFragmentEvent.TYPE_FINISH_FRAGMENT,
                        new FinishFragmentEvent.Param(0, getActivity(), getParentFragment()));
            }

            @Override
            public void onNetError(String url, Map params, String errno, String errmsg) {
                super.onNetError(url, params, errno, errmsg);
                clearRequestInput();
            }

            @Override
            public void onNetworkComplete() {
                super.onNetworkComplete();
                loadingDialog.dismiss();
            }
        });
    }

    private void setPayPassword(){
        if (payPwdEdt.length() < 6 || payPwdEdt.length() > 16){
            ToastUtils.showMsg(R.string.hint_pay_password);
            return;
        }
        requestPwdInput = payPwdEdt.getText().toString();
        ApiFacade.getInstance().setPayPassword(requestPwdInput,new TtRespListener(){
            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
                clearRequestInput();
            }

            @Override
            public void onNetSucc(String url, Map params, Object result) {
                super.onNetSucc(url, params, result);
                ToastUtils.showMsg("设置支付密码成功");
                AccountHistoryInfo historyInfo = ApiFacade.getInstance().getCurrentHistoryAccount();
                historyInfo.hasPayPassword = true;
                ApiFacade.getInstance().insertOrUpdateAccountHistory(historyInfo);
                clearRequestInput();
                EventDispatcherAgent.defaultAgent().broadcast(FinishFragmentEvent.TYPE_FINISH_FRAGMENT,
                        new FinishFragmentEvent.Param(0, getActivity(), getParentFragment()));
            }

            @Override
            public void onNetError(String url, Map params, String errno, String errmsg) {
                super.onNetError(url, params, errno, errmsg);
                clearRequestInput();
            }
        });
    }

    private void forgetPayPwd(){
        if (!ApiFacade.getInstance().getCurrentHistoryAccount().hasPayPassword){
            ToastUtils.showMsg("此账号尚未设置支付密码，请先设定支付密码");
            return;
        }
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
//                    Bundle bundle = new Bundle();
//                    bundle.putBoolean(StartType.KEY_START_NEW_AND_COLSE_ME,true);
//                    EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                            new StartActivityEvent.FragmentParam(0, getActivity(), BindPhoneFragment.class.getName(), bundle));
//                    EventDispatcherAgent.defaultAgent().broadcast(FinishFragmentEvent.TYPE_FINISH_FRAGMENT,
//                            new FinishFragmentEvent.Param(0, getActivity(), getParentFragment()));
//                    AppInternalHandler.bindPhone(getActivity());
                    startFragment(new BindPhoneFragment(), true);
                }
            });
            builder.create().show();
        }else {
//            Bundle bundle = new Bundle();
//            bundle.putBoolean(StartType.KEY_START_NEW_AND_COLSE_ME,true);
//            EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                    new StartActivityEvent.FragmentParam(0, getActivity(), ForgetPayPasswordFragment.class.getName(), bundle));
//            EventDispatcherAgent.defaultAgent().broadcast(FinishFragmentEvent.TYPE_FINISH_FRAGMENT,
//                    new FinishFragmentEvent.Param(0, getActivity(), getParentFragment()));
            startFragment(new ForgetPayPasswordFragment(), true);
        }
    }
}
