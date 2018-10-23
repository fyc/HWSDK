package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson1.Gson;
import com.mobilegamebar.rsdk.outer.util.StringUtils;
import com.yiyou.gamesdk.PluginManager;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.utils.AppInternalHandler;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.storage.sharepref.Constant;
import com.yiyou.gamesdk.core.ui.widget.roundview.RoundTextView;
import com.yiyou.gamesdk.model.GameDiscountInfo;
import com.yiyou.gamesdk.model.GetCouponInfo;
import com.yiyou.gamesdk.util.TimeUtils;
import com.yiyou.gamesdk.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Nekomimi on 2017/11/13.
 */

public class DiscountFragment extends BaseFragment {

    private ListView mCouponList;
    private TextView mDiscountTitle;
    private TextView mDiscountComment;
    private CouponAdapter mAdapter;
    private View discountContainer;
    private TextView mDiscountView;
    private TextView mOriginDiscountTipView;

    private final static int MOST_COUNT_ORIENTATION_PORTRAIT = 5;
    private final static int MOST_COUNT_ORIENTATION_LANDSCAPE = 2;
    private int mostCount;


    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        View root = LayoutInflater.from(content).inflate(R.layout.sdk_fragment_discount, container, false);
        initView(root);

        container.addView(root);
    }

    @Override
    protected void afterSetFragmentContent(Context context, Fragment titleBarFragment) {
        super.afterSetFragmentContent(context, titleBarFragment);
        requestData();
    }

    private void updateData(GameDiscountInfo info) {
        if (info.getCoupons() != null && !info.getCoupons().isEmpty()) {
            List<GameDiscountInfo.Coupon> couponList = new ArrayList<>();
            for (GameDiscountInfo.Coupon item : info.getCoupons()) {
                if (couponList.size() < mostCount) {
                    couponList.add(item);
                }
            }
            mAdapter.setData(couponList);
            mAdapter.notifyDataSetChanged();
        }

        GameDiscountInfo.GameDiscount gameDiscount = info.getGameDiscount();
        if (gameDiscount != null) {
            switch (gameDiscount.getDiscountStatus()) {
                case GameDiscountInfo.GameDiscount.LIMIT_DISCOUNT: {
                    mDiscountTitle.setText("限时充值折扣");
                    String desc = String.format(Locale.getDefault(), "%s，剩余%s", gameDiscount.getTitle(),
                            TimeUtils.getFriendlyTimeDifference(gameDiscount.getEndTime() - info.getServerTime()));
                    Spannable span = new SpannableString(desc);
                    span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.price_text)), gameDiscount.getTitle().length() + 3, desc.length(), 0);
                    mDiscountComment.setText(span);
                    mDiscountView.setTextColor(getResources().getColor(R.color.limit_discount));
                    mDiscountView.setText(String.format(Locale.getDefault(), "%s折", gameDiscount.getDiscountByString()));
                    mOriginDiscountTipView.setVisibility(View.VISIBLE);
                    mOriginDiscountTipView.setText(String.format(Locale.getDefault(), "原折扣：%s折", gameDiscount.getOriginalDiscountByString()));
                    break;
                }
                case GameDiscountInfo.GameDiscount.GOLD_DISCOUNT: {
                    mDiscountTitle.setText("黄金限时折扣");
                    String desc = String.format(Locale.getDefault(), "首次充值%s内有效", TimeUtils.getFriendlyTimeDifference(gameDiscount.getEndTime() - info.getServerTime()));
                    mDiscountComment.setText(desc);
                    mDiscountView.setTextColor(getResources().getColor(R.color.limit_discount));
                    mDiscountView.setText(String.format(Locale.getDefault(), "%s折", gameDiscount.getDiscountByString()));
                    mOriginDiscountTipView.setVisibility(View.VISIBLE);
                    mOriginDiscountTipView.setText(String.format(Locale.getDefault(), "续充：%s折", gameDiscount.getOriginalDiscountByString()));
                    break;
                }
                case GameDiscountInfo.GameDiscount.GOLD_DISCOUNT_COUNTDOWN: {
                    mDiscountTitle.setText("黄金限时折扣");
                    String desc = String.format(Locale.getDefault(), "剩余%s", TimeUtils.getFriendlyTimeDifference(gameDiscount.getEndTime() - info.getServerTime()));
                    mDiscountComment.setText(desc);
                    mDiscountView.setTextColor(getResources().getColor(R.color.limit_discount));
                    mDiscountView.setText(String.format(Locale.getDefault(), "%s折", gameDiscount.getDiscountByString()));
                    mOriginDiscountTipView.setVisibility(View.VISIBLE);
                    mOriginDiscountTipView.setText(String.format(Locale.getDefault(), "续充：%s折", gameDiscount.getOriginalDiscountByString()));
                    break;
                }
                default: {
                    mDiscountTitle.setText("充值折扣");
                    String desc = (gameDiscount.getOriginalDiscountByInt() != 0 && gameDiscount.getOriginalDiscountByInt() != 100) ?
                            String.format(Locale.getDefault(), "直接在游戏内充值，享受%s折优惠", gameDiscount.getOriginalDiscountByString())
                            : "没有充值活动，新活动敬请期待";
                    mDiscountComment.setText(desc);
                    mDiscountView.setTextColor(getResources().getColor(R.color.normal_discount));
                    mDiscountView.setText((gameDiscount.getOriginalDiscountByInt() != 0 && gameDiscount.getOriginalDiscountByInt() != 100) ? String.format(Locale.getDefault(), "%s折", gameDiscount.getOriginalDiscountByString()) : "暂无折扣");
                    mOriginDiscountTipView.setVisibility(View.GONE);
                    discountContainer.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }

    private void initView(@NonNull final View itemView) {
        discountContainer = itemView.findViewById(R.id.ll_discount);
        mDiscountView = (TextView) itemView.findViewById(R.id.discount);
        mOriginDiscountTipView = (TextView) itemView.findViewById(R.id.origin_discount_tip);
        mCouponList = (ListView) itemView.findViewById(R.id.list_coupon);
        mDiscountTitle = (TextView) itemView.findViewById(R.id.title_discount);
        mDiscountComment = (TextView) itemView.findViewById(R.id.comment_discount);
        mostCount = PluginManager.getInstance().getOrientation() == Configuration.ORIENTATION_LANDSCAPE ?
                MOST_COUNT_ORIENTATION_LANDSCAPE : MOST_COUNT_ORIENTATION_PORTRAIT;
        mAdapter = new CouponAdapter();
        mCouponList.setAdapter(mAdapter);

        SharedPreferences sharedPreferences = CoreManager.getContext().getSharedPreferences(Constant.KEY_DB_NAME,  Context.MODE_PRIVATE);
        String str = sharedPreferences.getString(Constant.KEY_DISCOUNT_INFO, "");
        if (!StringUtils.isEmpty(str)){
            Gson gson = new Gson();
            GameDiscountInfo info = gson.fromJson(str,GameDiscountInfo.class);
            updateData(info);
        }
    }

    private void requestData() {
        ApiFacade.getInstance().requestGameDiscount(new TtRespListener<GameDiscountInfo>() {
            @Override
            public void onNetSucc(String url, Map<String, String> params, GameDiscountInfo result) {
                super.onNetSucc(url, params, result);
                if (isAdded()) {
                    updateData(result);
                    Gson gson = new Gson();
                    SharedPreferences sharedPreferences = CoreManager.getContext().getSharedPreferences(Constant.KEY_DB_NAME,  Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString(Constant.KEY_DISCOUNT_INFO, gson.toJson(result)).apply();
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            requestData();
        }
    }

    @Override
    public String getTabName() {
        return "充值优惠";
    }

    class CouponAdapter extends BaseAdapter {


        private List<GameDiscountInfo.Coupon> mData;

        public void setData(List<GameDiscountInfo.Coupon> data) {
            mData = data;
            if (mCouponList.getFooterViewsCount() == 0 && mData != null && !mData.isEmpty()) {
                View footerView = LayoutInflater.from(mCouponList.getContext()).inflate(R.layout.sdk_item_footer_gift, mCouponList, false);
                footerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppInternalHandler.gameDetail(getActivity(), ApiFacade.getInstance().getCurrentGameID());
                    }
                });
                mCouponList.addFooterView(footerView);
            }
            notifyDataSetChanged();
        }

        public List<GameDiscountInfo.Coupon> getmData() {
            return mData;
        }

        @Override
        public int getCount() {
            return mData != null ? mData.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mData != null ? mData.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CouponHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sdk_item_coupon_discount, parent, false);
                holder = new CouponHolder(convertView);
            } else {
                holder = (CouponHolder) convertView.getTag();
            }
            GameDiscountInfo.Coupon info = mData.get(position);
            if (info != null) {
                holder.mAmountTv.setText("￥" + info.getAmount() / 100);
                holder.mCouponTitle.setText(info.getCouponName());
                holder.mDesc.setText(info.getSummary());
                if (info.getLimitAmount() > 0) {
                    int limitAmount = info.getLimitAmount() / 100;
                    holder.mLimitStatusTv.setText("满" + limitAmount + "元使用");
                } else {
                    holder.mLimitStatusTv.setText("无金额限制");
                }
                if (info.getStatus() == 0) {
//                    holder.mGetBtn.getDelegate().setStrokeColor(getResources().getColor(R.color.btn_selected_bg));
                    holder.mGetBtn.setTextColor(getResources().getColor(R.color.btn_selected_bg));
                    holder.mGetBtn.setText("领取");
                    holder.mGetBtn.getDelegate().setStrokeWidth(1);
                } else if (info.getStatus() == 1) {
                    holder.mGetBtn.setTextColor(getResources().getColor(R.color.coupon_disable_txt));
                    holder.mGetBtn.setText("已领取");
                    holder.mGetBtn.getDelegate().setStrokeWidth(0);
                }
                holder.mGetBtn.setTag(info);
                holder.mGetBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final GameDiscountInfo.Coupon info = (GameDiscountInfo.Coupon) v.getTag();
                        if (info == null) return;
                        if (info.getSplitable() == 0) {
                            ApiFacade.getInstance().getCoupon(info.getActId(), new TtRespListener<GetCouponInfo>() {
                                @Override
                                public void onNetSucc(String url, Map<String, String> params, GetCouponInfo result) {
                                    super.onNetSucc(url, params, result);
                                    ToastUtils.showMsg("领取成功");
                                    info.setStatus(1);
                                    notifyDataSetChanged();
                                }
                            });
                        } else if (info.getSplitable() == 1) {
                            ApiFacade.getInstance().getCouponByRule(info.getRuleId(), new TtRespListener<GetCouponInfo>() {
                                @Override
                                public void onNetSucc(String url, Map<String, String> params, GetCouponInfo result) {
                                    super.onNetSucc(url, params, result);
                                    ToastUtils.showMsg("领取成功");
                                    info.setStatus(1);
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

            }


            return convertView;
        }
    }

    class CouponHolder {
        TextView mAmountTv;
        TextView mLimitStatusTv;
        TextView mCouponTitle;
        TextView mDesc;
        RoundTextView mGetBtn;

        public CouponHolder(@NonNull final View itemView) {
            mAmountTv = (TextView) itemView.findViewById(R.id.tv_amount);
            mLimitStatusTv = (TextView) itemView.findViewById(R.id.tv_limit_status);
            mCouponTitle = (TextView) itemView.findViewById(R.id.title_coupon);
            mDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            mGetBtn = (RoundTextView) itemView.findViewById(R.id.btn_select);
            itemView.setTag(this);
        }
    }
}
