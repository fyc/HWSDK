package com.yiyou.gamesdk.core.ui.fragment;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.PluginManager;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.storage.events.NativeTitleBarUpdateEvent;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.core.ui.dialog.biz.LoadingDialog;
import com.yiyou.gamesdk.core.ui.widget.AccountLayout;
import com.yiyou.gamesdk.core.ui.widget.dialog.AlertDialogView;
import com.yiyou.gamesdk.core.ui.widget.dialog.CommDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.IDialogView;
import com.yiyou.gamesdk.core.ui.widget.dialog.OnDialogClickListener;
import com.yiyou.gamesdk.model.BalanceInfo;
import com.yiyou.gamesdk.model.CouponCountInfo;
import com.yiyou.gamesdk.model.CouponInfo;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;
import com.yiyou.gamesdk.util.PropertiesUtil;
import com.yiyou.gamesdk.util.VersionUtil;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by chenshuide on 15/5/28.
 * 账号
 */
public class AccountFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "RSDK:AccountFragment";
    private AccountLayout walletAl, /*purchaseHistoryAl,*/
            childrenAccountAl, resetPasswordAl, paymentPasswordAl, feedbackAl, couponAl;
    private CommDialog alertDialog;
    //    private TextView bindPhoneTv;
    //    private TextView supportTv;
    private TextView versionTv;
    private LoadingDialog loadingDialog;

    //    private HttpImageView avatarImg;
    private AccountLayout bindPhoneLl;

    @Override
    public NativeTitleBarUpdateInfo getTitleBarConfig() {
        NativeTitleBarUpdateInfo updateInfo = new NativeTitleBarUpdateInfo();
//        updateInfo.showRefreshButton = false;
        updateInfo.showBackButton = false;
        updateInfo.showCloseButton = false;
        return updateInfo;
    }

    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        View layout = LayoutInflater.from(content).inflate(R.layout.tt_sdk_fragment_account, container, false);
        container.addView(layout);
        loadingDialog = new LoadingDialog(getActivity());
        walletAl = (AccountLayout) layout.findViewById(R.id.ll_wallet);

//        purchaseHistoryAl = (AccountLayout)layout.findViewById(R.id.ll_purchase_history);
        childrenAccountAl = (AccountLayout) layout.findViewById(R.id.ll_children_account);
        resetPasswordAl = (AccountLayout) layout.findViewById(R.id.ll_resetpass);
        paymentPasswordAl = (AccountLayout) layout.findViewById(R.id.ll_payment_password);
        feedbackAl = (AccountLayout) layout.findViewById(R.id.ll_feedback);
        bindPhoneLl = (AccountLayout) layout.findViewById(R.id.ll_bind_phone);
        couponAl = (AccountLayout) layout.findViewById(R.id.ll_coupon);
//        supportTv = (TextView)layout.findViewById(R.id.tv_support);
        versionTv = (TextView)layout.findViewById(R.id.tv_version);
        walletAl.setOnClickListener(this);
        couponAl.setOnClickListener(this);
//        purchaseHistoryAl.setOnClickListener(this);
        childrenAccountAl.setOnClickListener(this);
        paymentPasswordAl.setOnClickListener(this);
        resetPasswordAl.setOnClickListener(this);
        feedbackAl.setOnClickListener(this);
//        supportTv.setOnClickListener(this);
        childrenAccountAl.setRightText(getString(R.string.str_children_account_manager));

//        avatarImg = (HttpImageView) layout.findViewById(R.id.img_avatar);
//        TextView userNameTv = (TextView) layout.findViewById(R.id.tv_user_name);
        AccountLayout logout = (AccountLayout) layout.findViewById(R.id.tv_logout);
        logout.setRightText(getString(R.string.str_change_account_tile));
        logout.setOnClickListener(this);
        logout.setLeftSmallText(ApiFacade.getInstance().getUserName());
        if (!PropertiesUtil.parseChangeAccount4Config(content).toLowerCase().equals("true")) {
            logout.setVisibility(View.GONE);
        }
//        bindPhoneTv = (TextView) layout.findViewById(R.id.bind_phone);
        bindPhoneLl.setOnClickListener(this);
//        bindPhoneTv.setOnClickListener(this);
//        userNameTv.setText(ApiFacade.getInstance().getUserName());
//        avatarImg.setDefaultImageResId(R.drawable.user_portrait);
//        avatarImg.setImageUrl(ApiFacade.getInstance().getCurrentHistoryAccount().avatarUrl, RequestManager.getInstance(getActivity()).getImageLoader());
        versionTv.setText("Ver" + VersionUtil.getSdkVersion() + "-R" + VersionUtil.getCoreVersion());

        getCouponCount();
    }


    @Override
    public void onResume() {
        super.onResume();
        getBindPhone();
        getBalance();
        getCouponCount();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d(TAG, "onHiddenChanged: " + hidden);
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getBindPhone();
            getBalance();
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        Log.d(TAG, "onFragmentFirstVisible: ");
        NativeTitleBarUpdateInfo info = getTitleBarConfig();
        EventDispatcherAgent.defaultAgent().broadcast(NativeTitleBarUpdateEvent.TYPE_ON_NATIVE_TITLE_BAR_UPDATE,
                new NativeTitleBarUpdateEvent.NativeTitleBarUpdateEventParam(info));
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        Log.d(TAG, "onFragmentVisibleChange: " + isVisible);
        if (isVisible) {
            NativeTitleBarUpdateInfo info = getTitleBarConfig();
            EventDispatcherAgent.defaultAgent().broadcast(NativeTitleBarUpdateEvent.TYPE_ON_NATIVE_TITLE_BAR_UPDATE,
                    new NativeTitleBarUpdateEvent.NativeTitleBarUpdateEventParam(info));
        }
    }


    private void getBalance() {
        if (walletAl == null) {
            return;
        }
        ApiFacade.getInstance().getBalance(new TtRespListener<BalanceInfo>() {
            @Override
            public void onNetSucc(String url, Map<String, String> params, BalanceInfo result) {
                super.onNetSucc(url, params, result);
                double balance = Double.valueOf(result.getBalance()) / 100;
                DecimalFormat df = new DecimalFormat("##0.00");
                walletAl.setRightText(df.format(balance));
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
            }

            @Override
            public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                super.onNetError(url, params, errno, errmsg);
            }
        });
    }

    private void getCouponCount() {
        if (couponAl == null) {
            return;
        }
        ApiFacade.getInstance().getCouponCount(CouponInfo.UNUSED, new TtRespListener<CouponCountInfo>() {
            @Override
            public void onNetSucc(String url, Map<String, String> params, CouponCountInfo result) {
                if (result != null) {
                    couponAl.setRightText("" + result.getTotal());
                }
            }
        });
    }

    /**
     * 显示用户绑定的手机
     */
    private void getBindPhone() {
        Log.d(TAG, "getBindPhone: ");
        if (bindPhoneLl == null) {
            return;
        }
        String phone = ApiFacade.getInstance().getPhone();
        if (!TextUtils.isEmpty(phone)) {
            bindPhoneLl.setRightText(phone);
        } else {
            bindPhoneLl.setRightText(R.string.str_no_bind_phone);
        }
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return CommonTitlePrimaryFragment.newInstance(getString(R.string.user_center));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_bind_phone:
                if (TextUtils.isEmpty(ApiFacade.getInstance().getPhone())) {
                    CommDialog.Builder builder = new CommDialog.Builder(getActivity());
                    AlertDialogView dialogView = new AlertDialogView(getActivity());
                    builder.setView(dialogView);
                    dialogView.setTitleTip(0);
                    dialogView.setMessageTip(R.string.str_bind_phone_msg_hint);
                    dialogView.setEnsureText(R.string.ensure);
                    builder.setLisenter(new OnDialogClickListener() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onEnsure(IDialogView view) {
//                            AppInternalHandler.bindPhone(getActivity());
                            startFragment(new BindPhoneFragment());
                        }
                    });
                    builder.create().show();
//                    EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                            new StartActivityEvent.FragmentParam(0, getActivity(), BindPhoneFragment.class.getName(), null));
                } else {
                    CommDialog.Builder builder = new CommDialog.Builder(getActivity());
                    AlertDialogView dialogView = new AlertDialogView(getActivity());
                    builder.setView(dialogView);
                    dialogView.setTitleTip(0);
                    dialogView.setMessageTip(R.string.str_change_bind_phone_msg_hint);
                    dialogView.setEnsureText(R.string.ensure);
                    builder.setLisenter(new OnDialogClickListener() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onEnsure(IDialogView view) {
                            startFragment(new UnBindPhoneFragment());
//                            AppInternalHandler.changeBindPhone(getActivity());
                        }
                    });
                    builder.create().show();
//                    EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                            new StartActivityEvent.FragmentParam(0, getActivity(), UnBindPhoneFragment.class.getName(), null));
                }
                break;
            case R.id.ll_resetpass: //reset password
//                EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                        new StartActivityEvent.FragmentParam(0, getActivity(), ResetPasswordFragment.class.getName(), null));
                startFragment(new ResetPasswordFragment());
//                AppInternalHandler.changePassword(getActivity());
                break;

            case R.id.ll_wallet:// bind phone
                startFragment(new WalletFragment());
//                EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                        new StartActivityEvent.FragmentParam(0, getActivity(), WalletFragment.class.getName(), null));
                break;

            case R.id.ll_children_account:
//                EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                        new StartActivityEvent.FragmentParam(0, getActivity(), ChildrenAccountFragment.class.getName(), null));
                startFragment(new ChildrenAccountFragment());
                break;
            case R.id.ll_payment_password:
                startFragment(new PayPasswordFragment());
//                EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                        new StartActivityEvent.FragmentParam(0, getActivity(), PayPasswordFragment.class.getName(), null));
                break;

            case R.id.ll_feedback:
                startFragment(new FeedbackFragment());
//                EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                        new StartActivityEvent.FragmentParam(0, getActivity(), FeedbackFragment.class.getName(), null));
                break;
          /*  case R.id.tv_support:
                CustomerServiceHelper.connectCustomerService(getContext());
                break;*/
            case R.id.ll_coupon:
                startFragment(new CouponFragment());
//                EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                        new StartActivityEvent.FragmentParam(0, getActivity(), CouponFragment.class.getName(), null));
                break;
            case R.id.tv_logout:
                if (alertDialog == null) {
                    CommDialog.Builder builder = new CommDialog.Builder(getActivity());
                    AlertDialogView dialogView = new AlertDialogView(getActivity());
                    builder.setView(dialogView);
                    dialogView.setTitleTip(0);
                    dialogView.setMessageTip(R.string.str_change_account_msg);
                    dialogView.setEnsureText(R.string.ensure);
                    builder.setLisenter(new OnDialogClickListener() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onEnsure(IDialogView view) {
                            loadingDialog.show();
                            ApiFacade.getInstance().logout(new IOperateCallback<String>() {
                                @Override
                                public void onResult(int i, String s) {
                                    loadingDialog.dismiss();
                                    if (i == TTCodeDef.SUCCESS) {
                                        getActivity().finish();
                                        if (PluginManager.getInstance().getLogoutCallback() != null) {
                                            PluginManager.getInstance().getLogoutCallback().onResult(i, s);
                                        }
                                    }
                                }
                            });
                        }
                    });
                    alertDialog = builder.create();
                }
                alertDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public String getTabName() {
        return "用户中心";
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }
}
