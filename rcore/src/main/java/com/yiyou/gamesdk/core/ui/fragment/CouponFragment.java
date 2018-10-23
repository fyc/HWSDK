package com.yiyou.gamesdk.core.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.model.CouponInfo;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;
import com.yiyou.gamesdk.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nekomimi on 2017/6/7.
 */

public class CouponFragment extends BaseFragment {

    private static final String TAG = "CouponFragment";
    private ViewPager mCouponFragmentVp;
    protected static SlidingTabLayout mTabLayout;
    private List<BaseCouponFragment> fragmentList;
    private BroadcastReceiver mReceiver;
    private BaseCouponFragment mUnuseFragment;

    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        Log.d(TAG, "setFragmentContent: ");
        View root = LayoutInflater.from(content).inflate(R.layout.tt_sdk_fragment_coupon, container, false);
        initChildrenFragment();
        mTabLayout = (SlidingTabLayout) root.findViewById(R.id.tl_coupon);
        mCouponFragmentVp = (ViewPager) root.findViewById(R.id.vp_coupon);
        mCouponFragmentVp.setAdapter(new Adapter(getChildFragmentManager(), fragmentList));
        mTabLayout.setViewPager(mCouponFragmentVp);
//        fragmentList.get(0).setTab(mTabLayout.getTabAt(0));
        container.addView(root);

        mUnuseFragment.setTab(mTabLayout.getTitleView(0));
    }


    private void initChildrenFragment() {
        fragmentList = new ArrayList<>();
        mUnuseFragment = BaseCouponFragment.getInstance(CouponInfo.UNUSED, "未使用");
        fragmentList.add(mUnuseFragment);
        fragmentList.add(BaseCouponFragment.getInstance(CouponInfo.USED, "已使用"));
        fragmentList.add(BaseCouponFragment.getInstance(CouponInfo.EXPIRED, "已过期"));
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return CommonTitlePrimaryFragment.newInstance("优惠券");

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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

  /*  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.yiyou.gamesdk.core.ui.fragment.CouponFragment");
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                }
            };
        }
        getContext().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            getContext().unregisterReceiver(mReceiver);
        }
    }*/

    static class Adapter extends FragmentPagerAdapter {

        private List<BaseCouponFragment> fragmentList;

        public Adapter(FragmentManager fm, List<BaseCouponFragment> list) {
            super(fm);
            fragmentList = list;
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList != null ? fragmentList.get(i) : null;
        }

        @Override
        public int getCount() {
            return fragmentList != null ? fragmentList.size() : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentList.get(position).getTabName();
        }
    }

    private static class CouponInfoHolder {
        enum State {
            Expand, Collapse
        }

        State state;
        RelativeLayout amountRl;
        TextView amountTv;
        TextView limitAmountTv;
        TextView titleTv;
        TextView dateTv;
        TextView contentTv;
        TextView moreContentTv;
        ImageView expandImg;
        ImageView usedImg;
        TextView typeTv;
    }

    public static class BaseCouponFragment extends Fragment {
        private String title;
        private String type;
        private List<CouponInfo> datalist = new ArrayList<>();
        private View contentView;
        private Context context;
        private BaseAdapter adapter;
        private TextView tip;
        private int size;
        private TextView mTab;

        public static BaseCouponFragment getInstance(String type, String title) {
            Log.d(TAG, "getInstance: " + title);
            BaseCouponFragment fragment = new BaseCouponFragment();
            fragment.setType(type);
            fragment.setTitle(title);
            return fragment;
        }

        public void setTab(TextView tab) {
            mTab = tab;
        }

        public void setDatalist(List<CouponInfo> datalist) {
            this.datalist = datalist;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            context = this.getContext();
        }

        @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.d(TAG, "onCreateView: " + title);
            contentView = inflater.inflate(R.layout.tt_sdk_fragment_base_coupon, container, false);
            final ListView listView = (ListView) contentView.findViewById(R.id.lv_coupon_list);
            tip = (TextView) contentView.findViewById(R.id.tv_no_coupon);
            adapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return datalist != null ? datalist.size() : 0;
                }

                @Override
                public Object getItem(int position) {
                    return datalist != null ? datalist.get(position) : null;
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    final CouponInfoHolder holder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(context).inflate(R.layout.tt_sdk_layout_coupon, parent, false);
                        holder = new CouponInfoHolder();
                        holder.amountRl = (RelativeLayout) convertView.findViewById(R.id.rl_main);
                        holder.amountTv = (TextView) convertView.findViewById(R.id.tv_coupon_amount);
                        holder.limitAmountTv = (TextView) convertView.findViewById(R.id.tv_coupon_limit_amount);
                        holder.contentTv = (TextView) convertView.findViewById(R.id.tv_coupon_content);
                        holder.moreContentTv = (TextView) convertView.findViewById(R.id.tv_more_content);
                        holder.titleTv = (TextView) convertView.findViewById(R.id.tv_coupon_title);
                        holder.dateTv = (TextView) convertView.findViewById(R.id.tv_coupon_date);
                        holder.expandImg = (ImageView) convertView.findViewById(R.id.img_expand);
                        holder.usedImg = (ImageView) convertView.findViewById(R.id.img_coupon_used);
                        holder.typeTv = (TextView) convertView.findViewById(R.id.tv_coupon_type);
                        holder.state = CouponInfoHolder.State.Collapse;
                        convertView.setTag(holder);
                    } else {
                        holder = (CouponInfoHolder) convertView.getTag();
                    }
                    CouponInfo info = datalist.get(position);
                    holder.contentTv.setText(info.getComment());
                    holder.titleTv.setText(info.getName());
                    holder.contentTv.setText(info.getSummary());
                    holder.moreContentTv.setText(info.getDetail());


                    holder.amountTv.setText("" + info.getAmount() / 100);
                    if (info.getLimitAmount() > 0) {
                        holder.typeTv.setText(R.string.coupon_discount);
                        int limitAmount = (int) info.getLimitAmount() / 100;
                        holder.limitAmountTv.setText("满" + limitAmount + "元使用");
                    } else {
                        holder.typeTv.setText(R.string.coupon_cash);
                        holder.limitAmountTv.setText("无金额限制");
                    }
                    if (holder.state == CouponInfoHolder.State.Collapse) {
                        holder.moreContentTv.setVisibility(View.GONE);
                        holder.expandImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sdk_icon_coupons_unfold));
                    } else {
                        holder.moreContentTv.setVisibility(TextUtils.isEmpty(info.getDetail()) ? View.GONE : View.VISIBLE);
                        holder.expandImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sdk_icon_coupons_fold));
                    }

                   /* holder.moreContentTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonUtils.handleUrl(String.format(Locale.CHINA, "tzsybm://nav/game/detail?gameId=%d",ApiFacade.getInstance().getCurrentGameID()),getActivity(), false);
                        }
                    });*/
                    View.OnClickListener l = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.state = holder.state == CouponInfoHolder.State.Expand ? CouponInfoHolder.State.Collapse : CouponInfoHolder.State.Expand;
                            adapter.notifyDataSetChanged();
                        }
                    };
//                    holder.contentTv.setOnClickListener(l);
                    holder.expandImg.setOnClickListener(l);

                    if (CouponInfo.USED.equals(type)) {
                        holder.dateTv.setText(TimeUtils.times(info.getSpendingTime()) + "  已使用 ");
                        holder.usedImg.setVisibility(View.VISIBLE);
                        holder.amountRl.setBackgroundResource(R.drawable.sdk_bg_coupons_gray);
                        holder.typeTv.setBackgroundResource(R.drawable.tz_sdk_radius_expired_bg);
                    } else if (CouponInfo.UNUSED.equals(type)) {
                        holder.dateTv.setText("有效期：" + TimeUtils.times(info.getEffectiveDate()) + " - " + TimeUtils.times(info.getExpiryDate()));
                        holder.typeTv.setBackgroundResource(R.drawable.tz_sdk_radius_unused_bg);
                        holder.usedImg.setVisibility(View.GONE);
                        holder.amountRl.setBackgroundResource(R.drawable.sdk_bg_coupons_red);
                    } else if (CouponInfo.EXPIRED.equals(type)) {
                        holder.usedImg.setVisibility(View.GONE);
                        holder.amountRl.setBackgroundResource(R.drawable.sdk_bg_coupons_gray);
                        holder.typeTv.setBackgroundResource(R.drawable.tz_sdk_radius_expired_bg);
                        holder.dateTv.setText("有效期：" + TimeUtils.times(info.getEffectiveDate()) + " - " + TimeUtils.times(info.getExpiryDate()));
                    }
                    return convertView;
                }

            };
            listView.setAdapter(adapter);

            ApiFacade.getInstance().getCouponInfos(type, new TtRespListener<CouponInfo>() {
                @Override
                public void onNetSucc(String url, Map<String, String> params, List<CouponInfo> result) {
                    if (result != null && result.size() > 0) {
                        datalist = result;
                        if (mTab != null) {
                            size = result.size();
                            mTab.setText(title + "(" + size + ")");
                            tip.setVisibility(View.GONE);
//
//                            if (CouponInfo.UNUSED.equals(type)) {
//                                if (mTabLayout != null) {
//                                    mTabLayout.getTitleView(0).setText(String.format(Locale.CHINA, mTabLayout.getTitleView(0).getText() + "(%s)"));
//                                }
//                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        listView.setVisibility(View.GONE);
                        tip.setVisibility(View.VISIBLE);
                    }
                }
            });
            return contentView;
        }

        public String getTabName() {
            return title;
        }
    }
}
