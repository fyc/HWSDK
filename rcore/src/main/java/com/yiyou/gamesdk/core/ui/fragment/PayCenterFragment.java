package com.yiyou.gamesdk.core.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson1.Gson;
import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.storage.events.NativeTitleBarTitleContentEvent;
import com.yiyou.gamesdk.core.storage.events.NativeTitleBarUpdateEvent;
import com.yiyou.gamesdk.core.ui.common.PayCenterTitleFragment;
import com.yiyou.gamesdk.core.ui.dialog.biz.LoadingDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.CommDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.EditInputDialogView;
import com.yiyou.gamesdk.core.ui.widget.dialog.IDialogView;
import com.yiyou.gamesdk.core.ui.widget.dialog.OnDialogClickListener;
import com.yiyou.gamesdk.core.ui.widget.roundview.RoundRelativeLayout;
import com.yiyou.gamesdk.core.ui.widget.roundview.RoundTextView;
import com.yiyou.gamesdk.model.CouponInfo;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;
import com.yiyou.gamesdk.model.PayInfo;
import com.yiyou.gamesdk.util.ExtraDef;
import com.yiyou.gamesdk.util.TimeUtils;
import com.yiyou.gamesdk.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Nekomimi on 2017/11/14.
 */

public class PayCenterFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "PayCenterFragment";

    private static final String RMB = "￥%f";

    private static final int POSITION_PAYWAY = 0;
    private static final int POSITION_COUPON = 1;


    private PayCenterTitleFragment titleFragment;
    private float offSet = 0;
    private NativeTitleBarUpdateInfo payWayTitleInfo;
    private NativeTitleBarUpdateInfo couponTitleInfo;


    private View mContainerRoot;
    private LoadingDialog loadingDialog;
    private int mPositionType;
    private boolean animaFlag = false;
    private double remainFee;
    private PayInfo payInfo;
    private CouponInfo selectCoupon;
    private Adapter mAdapter;

    private List<CouponListInfo> mCouponList = new ArrayList<>();
    private TextView mDiscountTip;
    private TextView mPriceGoods;
    private TextView mDiscountTxt;
    private TextView mCouponPrice;
    private RoundTextView mSelectCouponBtn;
    private TextView mWalletPrice;
    private TextView mFeeRemain;
    private RoundTextView mWechatPayBtn;
    private RoundTextView mAlipayBtn;
    private RoundTextView mRcoinPayBtn;
    private ListView mSelectCouponList;
    private TextView mNoCouponTip;

    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        titleFragment = (PayCenterTitleFragment) titleBarFragment;
        View root = LayoutInflater.from(content).inflate(R.layout.sdk_fragment_paycenter, container, false);
        initView(root);
        container.addView(root);
    }

    private void initView(@NonNull final View itemView) {
        mContainerRoot = itemView.findViewById(R.id.container_pay_center);
        mDiscountTip = (TextView) itemView.findViewById(R.id.tip_discount);
        mPriceGoods = (TextView) itemView.findViewById(R.id.goods_price);
        mDiscountTxt = (TextView) itemView.findViewById(R.id.txt_discount);
        mCouponPrice = (TextView) itemView.findViewById(R.id.price_coupon);
        mSelectCouponBtn = (RoundTextView) itemView.findViewById(R.id.btn_select_coupon);
        mWalletPrice = (TextView) itemView.findViewById(R.id.price_wallet);
        mFeeRemain = (TextView) itemView.findViewById(R.id.remain_fee);
        mWechatPayBtn = (RoundTextView) itemView.findViewById(R.id.btn_wechat_pay);
        mAlipayBtn = (RoundTextView) itemView.findViewById(R.id.btn_alipay);
        mRcoinPayBtn = (RoundTextView) itemView.findViewById(R.id.btn_rcoin_pay);
        mSelectCouponList = (ListView) itemView.findViewById(R.id.list_select_coupon);
        loadingDialog = new LoadingDialog(getActivity());
        mAdapter = new Adapter(getContext());
        mSelectCouponList.setAdapter(mAdapter);
        mSelectCouponBtn.setOnClickListener(this);
        mAlipayBtn.setOnClickListener(this);
        mWechatPayBtn.setOnClickListener(this);
        mRcoinPayBtn.setOnClickListener(this);
        mNoCouponTip = (TextView) itemView.findViewById(R.id.tip_no_coupon);
    }

    @Override
    protected void afterSetFragmentContent(Context context, Fragment titleBarFragment) {
        super.afterSetFragmentContent(context, titleBarFragment);
        ApiFacade.getInstance().updatePaymentActivity(getActivity());
        Gson gson = new Gson();
        payInfo = gson.fromJson(getArguments().getString(ExtraDef.EXTRA_PAY_INFO), PayInfo.class);
        Log.d(TAG, "afterSetFragmentContent: " + getArguments().getString(ExtraDef.EXTRA_PAY_INFO));
        initPayWay();
        initCouponList();
        refreshPayInfo();
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return PayCenterTitleFragment.newInstance("游戏充值");
    }

    @Override
    public NativeTitleBarUpdateInfo getTitleBarConfig() {
        if (payWayTitleInfo == null) {
            payWayTitleInfo = new NativeTitleBarUpdateInfo();
            payWayTitleInfo.showBackButton = false;
            payWayTitleInfo.showCloseButton = true;

            couponTitleInfo = new NativeTitleBarUpdateInfo();
            couponTitleInfo.showBackButton = true;
            couponTitleInfo.showCloseButton = true;
        }
        return payWayTitleInfo;
    }

    @Override
    public boolean onBackPressed() {
        if (mPositionType == 0) {
            ApiFacade.getInstance().closeNotify();
            return false;
        } else {
            toPayWay();
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_coupon:
                if (offSet == 0f){
                    offSet = - mContainerRoot.getLayoutParams().width / 2;
                }
                toCoupon();
                break;
            case R.id.btn_alipay:
                verifyPayPwd("ALIPAY");
                break;
            case R.id.btn_wechat_pay:
                verifyPayPwd("WECHAT");
                break;
            case R.id.btn_rcoin_pay:
                verifyPayPwd("RCOIN");
                break;
            default:
                break;
        }
    }


    private void toCoupon() {
        if (animaFlag) {
            return;
        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(mContainerRoot, "translationX", 0f, offSet);
        anim.setDuration(450);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animaFlag = false;
                mPositionType = 1;
                EventDispatcherAgent.defaultAgent().broadcast(NativeTitleBarUpdateEvent.TYPE_ON_NATIVE_TITLE_BAR_UPDATE,
                        new NativeTitleBarUpdateEvent.NativeTitleBarUpdateEventParam(couponTitleInfo));
                EventDispatcherAgent.defaultAgent().broadcast(NativeTitleBarTitleContentEvent.TYPE_ON_NATIVE_TITLE_BAR_TITLE_CONTENT,
                        new NativeTitleBarTitleContentEvent.NativeTitleBarTitleContentEventParam("选择优惠券"));
            }
        });
        animaFlag = true;
        anim.start();
    }

    private void toPayWay() {
        if (animaFlag) {
            return;
        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(mContainerRoot, "translationX", offSet, 0f);
        anim.setDuration(450);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animaFlag = false;
                mPositionType = 0;
                EventDispatcherAgent.defaultAgent().broadcast(NativeTitleBarUpdateEvent.TYPE_ON_NATIVE_TITLE_BAR_UPDATE,
                        new NativeTitleBarUpdateEvent.NativeTitleBarUpdateEventParam(payWayTitleInfo));
                EventDispatcherAgent.defaultAgent().broadcast(NativeTitleBarTitleContentEvent.TYPE_ON_NATIVE_TITLE_BAR_TITLE_CONTENT,
                        new NativeTitleBarTitleContentEvent.NativeTitleBarTitleContentEventParam("游戏充值"));
            }
        });
        animaFlag = true;
        anim.start();
    }

    private void initPayWay() {
        if (payInfo.getDiscountStatus() == 1){
            String limitTime =  String.format(Locale.getDefault(), "  (剩余%s)", TimeUtils.getFriendlyTimeDifference(payInfo.getLimitDiscountEndTime() - payInfo.getServerTime()));
            String discount = "限时" + payInfo.getDiscount();
            SpannableString spannableString = new SpannableString(getString(R.string.tip_discount) + discount + "折" + limitTime);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFC4D19")), 11, 11 + discount.length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.price_text)), 12 + discount.length(), limitTime.length() + 12 + discount.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mDiscountTip.setText(spannableString);
        } else if (payInfo.getDiscountStatus() == 2 || payInfo.getDiscountStatus() == 3) {
            String limitTime =  String.format(Locale.getDefault(), "  (剩余%s)", TimeUtils.getFriendlyTimeDifference(payInfo.getLimitDiscountEndTime() - payInfo.getServerTime()));
            String discount = "" + payInfo.getDiscount();
            SpannableString spannableString = new SpannableString(getString(R.string.tip_gold_discount) + discount + "折" + limitTime);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFC4D19")), 11, 11 + discount.length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.price_text)), 12 + discount.length(), limitTime.length() + 12 + discount.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mDiscountTip.setText(spannableString);
        } else {
            String discount = String.valueOf(payInfo.getDiscount());
            SpannableString spannableString = new SpannableString(getString(R.string.tip_discount) + discount + "折");
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFC4D19")), 11, 11 + discount.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mDiscountTip.setText(spannableString);
        }


        mPriceGoods.setText(getString(R.string.str_price, payInfo.getCliPrice()));
        mDiscountTip.setVisibility(payInfo.getDiscount() < 10 ? View.VISIBLE : View.GONE);
        mDiscountTxt.setVisibility(payInfo.getDiscount() < 10 ? View.VISIBLE : View.GONE);
        String discountStr = getString(R.string.str_price_compare, payInfo.getCliOrderPrice(), payInfo.getCliOrderPrice() - payInfo.getCliPrice());
        String cliOrderPriceStr = getString(R.string.str_price,payInfo.getCliOrderPrice());
        SpannableString spannableString = new SpannableString(discountStr);
        spannableString.setSpan(new StrikethroughSpan(), 0, 4 + cliOrderPriceStr.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mDiscountTxt.setText(spannableString);


    }

    private void initCouponList() {
        mCouponList = new ArrayList<>();
        CouponListInfo header = new CouponListInfo();
        header.couponListType = CouponListInfo.LIST_TYPE_HEAD;
        mCouponList.add(header);

        if (payInfo.getDisableCoupons()==null && payInfo.getEnableCoupons()==null){
            mNoCouponTip.setVisibility(View.VISIBLE);
        } else if ( payInfo.getDisableCoupons()!=null&&payInfo.getEnableCoupons()!=null&&
                payInfo.getDisableCoupons().size() == 0 && payInfo.getEnableCoupons().size() == 0) {
            mNoCouponTip.setVisibility(View.VISIBLE);
        } else {
            mNoCouponTip.setVisibility(View.GONE);
            if (payInfo.getEnableCoupons() != null) {
                for (CouponInfo info : payInfo.getEnableCoupons()) {
                    CouponListInfo item = new CouponListInfo();
                    item.couponInfo = info;
                    item.couponListType = CouponListInfo.LIST_TYPE_ITEM_ABLE;
                    mCouponList.add(item);
                }
            }
            if (payInfo.getDisableCoupons()!=null && payInfo.getDisableCoupons().size() > 0) {
                CouponListInfo divider = new CouponListInfo();
                divider.couponListType = CouponListInfo.LIST_TYPE_DIVIDER;
                mCouponList.add(divider);
                for (CouponInfo info : payInfo.getDisableCoupons()) {
                    CouponListInfo item = new CouponListInfo();
                    item.couponInfo = info;
                    item.couponListType = CouponListInfo.LIST_TYPE_ITEM_DISABLE;
                    mCouponList.add(item);
                }
            }
        }
        if (payInfo.getEnableCoupons()!=null && payInfo.getEnableCoupons().size()>0){
            mAdapter.selectCounponId = payInfo.getEnableCoupons().get(0).getId();
            selectCoupon = payInfo.getEnableCoupons().get(0);
        }
        mAdapter.setmCouponList(mCouponList);
        mAdapter.notifyDataSetChanged();
    }

    private void refreshPayInfo() {
        payInfo.setCouponSpend(0);
        payInfo.setRemainFee(payInfo.getCliPrice());
        if (selectCoupon != null) {
            double couponSpend = selectCoupon.getAmount() > 100 * payInfo.getRemainFee() ? payInfo.getRemainFee() : selectCoupon.getAmount() / 100;
            mCouponPrice.setText(getString(R.string.str_sub_price, couponSpend));
            payInfo.setCouponSpend(couponSpend);
        }else {
            payInfo.setCouponSpend(0);
            mCouponPrice.setText(getString(R.string.str_sub_price, 0.00f));
        }
        double walletSpend = payInfo.getCliPrice() - payInfo.getCouponSpend() > payInfo.getWallet() ? payInfo.getWallet() : payInfo.getCliPrice() - payInfo.getCouponSpend();
        mWalletPrice.setText(getString(R.string.str_sub_price, walletSpend));
        payInfo.setWalletSpend(walletSpend);
        double feeRemain = payInfo.getCliPrice() - payInfo.getCouponSpend() - walletSpend;
        payInfo.setRemainFee(feeRemain);

        //余额不足，用第三方支付
        if (feeRemain > 0) {
            mFeeRemain.setText(getString(R.string.str_price, feeRemain));
            mContainerRoot.findViewById(R.id.container_remain_fee).setVisibility(View.VISIBLE);
            mWalletPrice.setTextColor(getResources().getColor(R.color.d_gray_1));
            mWalletPrice.setTextSize(14);
            TextPaint tp = mWalletPrice.getPaint();
            tp.setFakeBoldText(false);
            mAlipayBtn.setVisibility(View.VISIBLE);
            mWechatPayBtn.setVisibility(View.VISIBLE);
            mRcoinPayBtn.setVisibility(View.GONE);
            //余额只够，直接用平台币支付
        } else {
            mWalletPrice.setText(getString(R.string.str_price, walletSpend));
            mFeeRemain.setText(getString(R.string.str_sub_price, feeRemain));
            mContainerRoot.findViewById(R.id.container_remain_fee).setVisibility(View.GONE);
            mWalletPrice.setTextColor(getResources().getColor(R.color.price_text));
            mWalletPrice.setTextSize(20);
            TextPaint tp = mWalletPrice.getPaint();
            tp.setFakeBoldText(true);
            mAlipayBtn.setVisibility(View.GONE);
            mWechatPayBtn.setVisibility(View.GONE);
            mRcoinPayBtn.setVisibility(View.VISIBLE);
        }
    }

    private void verifyPayPwd(String payChannel){
        payInfo.setPayChannel(payChannel);
        if (ApiFacade.getInstance().getCurrentHistoryAccount().hasPayPassword && payInfo.getWalletSpend() > 0) {
            openPayPwdDialog();
        } else {
            requestPay(payChannel);
        }
    }

    private void openPayPwdDialog() {
        Log.d(TAG, "openPayPwdDialog: ");
        CommDialog.Builder builder = new CommDialog.Builder(getActivity());
        final EditInputDialogView dialogView = new EditInputDialogView(getActivity());
        dialogView.setEnsureText(R.string.ensure);
        dialogView.setTitleTip(R.string.tip_input_pay_password);
        dialogView.setHint(R.string.hint_pay_password);
        dialogView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        builder.setView(dialogView);
        builder.setLisenter(new OnDialogClickListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onEnsure(final IDialogView view) {
                final String password = dialogView.getInput();
                if (password.length() < 6 || password.length() > 16) {
                    ToastUtils.showMsg(R.string.hint_pay_password);
                    return;
                }
                loadingDialog.show();
                ApiFacade.getInstance().verifyPayPassword(dialogView.getInput(), new TtRespListener() {
                    @Override
                    public void onNetSucc(String url, Map params, Object result) {
                        super.onNetSucc(url, params, result);
                        view.close();
                        if (payInfo.getPayChannel()!=null){
                            requestPay(payInfo.getPayChannel());
                        }
                    }

                    @Override
                    public void onFail(int errorNo, String errmsg) {
                        super.onFail(errorNo, errmsg);
                    }

                    @Override
                    public void onNetError(String url, Map params, String errno, String errmsg) {
                        super.onNetError(url, params, errno, errmsg);
                    }

                    @Override
                    public void onNetworkComplete() {
                        super.onNetworkComplete();
                        loadingDialog.dismiss();
                    }
                });
            }
        });
        CommDialog changeChildDialog = builder.create();
        changeChildDialog.show();
    }

    private void requestPay(final String payChannel){
        Map<String,String> params = new HashMap<>();
        params.put("orderNo",payInfo.getOrderNo());
        params.put("token",payInfo.getToken());
        params.put("payChannel",payChannel);
        if (selectCoupon!=null){
            params.put("couponId",selectCoupon.getId());
            params.put("amount",String.valueOf(selectCoupon.getAmount()/100));
        }
        loadingDialog.show();
        ApiFacade.getInstance().orderPay(params,new TtRespListener<String>(){
            @Override
            public void onNetSucc(String url, Map<String, String> params, String result) {
                super.onNetSucc(url, params, result);
                Log.d(TAG, "onNetSucc: " + result);
                if (TextUtils.isEmpty(result)){
                    ApiFacade.getInstance().notifyOrderState(TTCodeDef.SUCCESS,"支付成功");
                    getActivity().finish();
                    return;
                }
                int payWay = 1;
                if (payChannel.equals("ALIPAY")){
                    payWay = 0;
                }
                ApiFacade.getInstance().orderThroughClient(payWay,result);
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
                Log.d(TAG, "onFail: ");
                ApiFacade.getInstance().notifyOrderState(TTCodeDef.PAY_RESULT_FAIL,errmsg);
            }

            @Override
            public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                super.onNetError(url, params, errno, errmsg);
                Log.d(TAG, "onNetError: ");
                ApiFacade.getInstance().notifyOrderState(TTCodeDef.PAY_RESULT_FAIL,errmsg);
            }

            @Override
            public void onNetworkComplete() {
                super.onNetworkComplete();
                loadingDialog.dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiFacade.getInstance().clearOrderCache();
    }

    class Adapter extends BaseAdapter {

        private List<CouponListInfo> couponList = new ArrayList<>();
        private Context context;
        private String selectCounponId = "";

        public Adapter(Context context) {
            this.context = context;
        }

        public List<CouponListInfo> getmCouponList() {
            return couponList;
        }

        public void setmCouponList(List<CouponListInfo> mCouponList) {
            this.couponList = mCouponList;
        }

        @Override
        public int getItemViewType(int position) {
            if (couponList != null) {
                return couponList.get(position).couponListType;
            }
            return super.getItemViewType(position);
        }


        @Override
        public int getCount() {
            if (couponList == null || couponList.size() == 0) {
                return 0;
            }
            return couponList.size();
        }

        @Override
        public Object getItem(int position) {
            return couponList != null ? couponList.get(position) : 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case CouponListInfo.LIST_TYPE_HEAD: {
                    CouponListHeadHolder holder;
                    if (convertView == null || !(convertView.getTag() instanceof CouponListHeadHolder)) {
                        convertView =View.inflate(context, R.layout.sdk_item_coupon_head,  null);
                        holder = new CouponListHeadHolder(convertView);
                    } else {
                        holder = (CouponListHeadHolder) convertView.getTag();
                    }
                    holder.selectNonCouponBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectCounponId = "";
                            selectCoupon = null;
                            PayCenterFragment.this.toPayWay();
                            refreshPayInfo();
                            notifyDataSetChanged();
                        }
                    });
                    break;
                }
                case CouponListInfo.LIST_TYPE_ITEM_ABLE:
                case CouponListInfo.LIST_TYPE_ITEM_DISABLE:{
                    final CouponListItemHolder holder;
                    if (convertView == null || !(convertView.getTag() instanceof CouponListItemHolder) ) {
                        convertView =View.inflate(context, R.layout.sdk_item_coupon_able,  null);
                        holder = new CouponListItemHolder(convertView);
                    } else {
                        holder = (CouponListItemHolder) convertView.getTag();
                    }
                    final CouponInfo info = couponList.get(position).couponInfo;
                    if (info != null) {
                        holder.mAmountTv.setText(String.valueOf(info.getAmount() / 100));
                        holder.mCouponTitle.setText(info.getName());
                        holder.mLimitTimeTv.setText("有效期：" + TimeUtils.times(info.getEffectiveDate()) + " - " + TimeUtils.times(info.getExpiryDate()));
                        if (info.getLimitAmount() > 0) {
                            holder.mCouponTypeTv.setText(R.string.coupon_discount);
                            int limitAmount = (int) info.getLimitAmount() / 100;
                            holder.mLimitStatusTv.setText("满" + limitAmount + "元使用");
                        } else {
                            holder.mCouponTypeTv.setText(R.string.coupon_cash);
                            holder.mLimitStatusTv.setText("无金额限制");
                        }
                        holder.mSelectBtn.setVisibility(couponList.get(position).couponListType == CouponListInfo.LIST_TYPE_ITEM_ABLE ?
                            View.VISIBLE : View.GONE);
                        if (selectCounponId.equals(info.getId())){
                            holder.mSelectBtn.setTextColor(getResources().getColor(R.color.d_gray_2));
                            holder.mContainerRoot.getDelegate().setStrokeColor(getResources().getColor(R.color.coupon_border_select));
                            holder.mSelectBtn.getDelegate().setBackgroundColor(getResources().getColor(R.color.coupon_border_normal));
                            holder.mSelectBtn.setText("使用中");
                        }else {
                            holder.mSelectBtn.setTextColor(getResources().getColor(R.color.d_gray_1));
                            holder.mContainerRoot.getDelegate().setStrokeColor(getResources().getColor(R.color.coupon_border_normal));
                            holder.mSelectBtn.getDelegate().setBackgroundColor(getResources().getColor(R.color.btn_selected_bg));
                            holder.mSelectBtn.setText("使用");
                        }
                        holder.mContainerRoot.setTag(getItemViewType(position));
                        holder.mContainerRoot.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if ( ((int)v.getTag()) == CouponListInfo.LIST_TYPE_ITEM_ABLE){
                                    selectCounponId = info.getId();
                                    selectCoupon = info;
                                    refreshPayInfo();
                                    notifyDataSetChanged();
                                    toPayWay();
                                }
                            }
                        });
                    }
                    break;
                }

                case CouponListInfo.LIST_TYPE_DIVIDER: {
                    convertView =View.inflate(context, R.layout.sdk_item_coupon_divider,  null);
                    break;
                }
            }
            return convertView;
        }
    }


    static class CouponListInfo {
        public static final int LIST_TYPE_HEAD = 0;
        public static final int LIST_TYPE_DIVIDER = 1;
        public static final int LIST_TYPE_ITEM_ABLE = 2;
        public static final int LIST_TYPE_ITEM_DISABLE = 3;

        public CouponInfo couponInfo;
        public int couponListType;
    }

    static class CouponListHeadHolder {
        TextView selectNonCouponBtn;

        public CouponListHeadHolder(View root) {
            this.selectNonCouponBtn = (TextView) root.findViewById(R.id.btn_select_non_coupon);
            root.setTag(this);
        }
    }

    static class CouponListItemHolder {
        private RoundRelativeLayout mContainerRoot;
        private TextView mAmountTv;
        private TextView mLimitStatusTv;
        private TextView mCouponTypeTv;
        private TextView mCouponTitle;
        private TextView mLimitTimeTv;
        private RoundTextView mSelectBtn;

        public CouponListItemHolder(@NonNull final View itemView) {
            mAmountTv = (TextView) itemView.findViewById(R.id.tv_amount);
            mLimitStatusTv = (TextView) itemView.findViewById(R.id.tv_limit_status);
            mCouponTypeTv = (TextView) itemView.findViewById(R.id.tv_coupon_type);
            mCouponTitle = (TextView) itemView.findViewById(R.id.title_coupon);
            mLimitTimeTv = (TextView) itemView.findViewById(R.id.tv_limit_time);
            mSelectBtn = (RoundTextView) itemView.findViewById(R.id.btn_select);
            mContainerRoot = (RoundRelativeLayout) itemView.findViewById(R.id.container_pay_coupon_rr);
            itemView.setTag(this);
        }
    }

}
