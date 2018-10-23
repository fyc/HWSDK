package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yiyou.gamesdk.PluginManager;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.utils.AppInternalHandler;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.ui.dialog.biz.LoadingDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.CommDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.GiftGetSuccDialogView;
import com.yiyou.gamesdk.model.GamePackages;
import com.yiyou.gamesdk.util.CommonUtils;
import com.yiyou.gamesdk.util.TimeUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Nekomimi on 2017/11/13.
 */

public class GiftCenterFragment extends BaseFragment {

    private ListView mGiftListView;
    private View mEmptyHint;
    private Adapter mAdapter;
    private final static int MOST_COUNT_ORIENTATION_PORTRAIT = 6;
    private final static int MOST_COUNT_ORIENTATION_LANDSCAPE = 3;
    private int mostCount;

    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        View layout = LayoutInflater.from(content).inflate(R.layout.tt_sdk_fragment_gift_center, container, false);
        mEmptyHint = layout.findViewById(R.id.empty_hint);
        mGiftListView = (ListView) layout.findViewById(R.id.list);
        mAdapter = new Adapter();
        mGiftListView.setAdapter(mAdapter);
        mostCount = PluginManager.getInstance().getOrientation() == Configuration.ORIENTATION_LANDSCAPE ?
                MOST_COUNT_ORIENTATION_LANDSCAPE : MOST_COUNT_ORIENTATION_PORTRAIT;
        container.addView(layout);
        requestData();
    }

    private void requestData() {
        ApiFacade.getInstance().getGamePackage(new TtRespListener<GamePackages>() {
            @Override
            public void onNetSucc(String url, Map<String, String> params, GamePackages result) {
                if (isAdded()) {
                    updateData(result);
                }
            }
        });
    }

    private void updateData(GamePackages gamePackages) {
        mAdapter.setData(gamePackages.getGamePackageList());
        mGiftListView.setVisibility(View.VISIBLE);
        if (gamePackages.getGamePackageList() != null && !gamePackages.getGamePackageList().isEmpty()) {
            Fragment parentFragment = getParentFragment();
            Log.d(myTag, "getParentFragment() fragment = " + parentFragment);
            if (parentFragment instanceof GameFragment) {
                ((GameFragment) parentFragment).showViewPagerMsg(1, gamePackages.getGamePackageList().size());
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            requestData();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            requestData();
        }
    }

    @Override
    public String getTabName() {
        return "礼包中心";
    }

//    public void updatePkgStatus(int packageId, int pkgRemain, int pkgTotal) {
//        if (mAdapter != null) {
//            List<GamePackages.GamePackageInfo> data = mAdapter.getData();
//            if (data != null) {
//                for (GamePackages.GamePackageInfo info : data) {
//                    if (info.getPackageId() == packageId) {
//                        info.setPackageRemain(pkgRemain);
//                        info.setPackageTotal(pkgTotal);
//                        mAdapter.notifyDataSetChanged();
//                        return;
//                    }
//                }
//            }
//        }
//    }


    class Adapter extends BaseAdapter {

        private List<GamePackages.GamePackageInfo> mData;

        public void setData(List<GamePackages.GamePackageInfo> data) {
            mData = data;
            mEmptyHint.setVisibility(mData == null || mData.isEmpty() ? View.VISIBLE : View.GONE);
            if (mGiftListView.getFooterViewsCount() == 0 && mData != null && !mData.isEmpty()) {
                View footerView = LayoutInflater.from(mGiftListView.getContext()).inflate(R.layout.sdk_item_footer_gift, mGiftListView, false);
                footerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppInternalHandler.gameDetail(getActivity(), ApiFacade.getInstance().getCurrentGameID());
                    }
                });
                mGiftListView.addFooterView(footerView);
            }
            notifyDataSetChanged();
        }

        public List<GamePackages.GamePackageInfo> getData() {
            return mData;
        }

        @Override
        public int getCount() {
            return mData == null || mData.isEmpty() ? 0 : (mData.size() > mostCount ? mostCount : mData.size());
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sdk_item_gift, parent, false);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.count = (TextView) convertView.findViewById(R.id.count);
                holder.dueTime = (TextView) convertView.findViewById(R.id.due_time);
                holder.get = (TextView) convertView.findViewById(R.id.get);
                holder.condition = (TextView) convertView.findViewById(R.id.condition);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            final GamePackages.GamePackageInfo item = (GamePackages.GamePackageInfo) getItem(position);
            try {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFragment(GiftDetailFragment.newInstance(String.valueOf(item.getPackageId())));
                    }
                });
                if (item.getRedeemCode() == null || item.getRedeemCode().isEmpty()) {
                    holder.get.setTextColor(getActivity().getResources().getColor(R.color.btn_selected_bg));
                    holder.get.setSelected(false);
                    holder.get.setText("领取");
                    holder.get.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                            loadingDialog.show();
                            int packageId = item.getPackageId();
                            ApiFacade.getInstance().receiveGamePackage(packageId + "", new TtRespListener<GamePackages.GamePackageInfo>() {
                                @Override
                                public void onNetSucc(String url, Map<String, String> params, GamePackages.GamePackageInfo result) {
                                    if (loadingDialog != null && loadingDialog.isShowing()) {
                                        loadingDialog.dismiss();
                                    }
                                    item.setPackageRemain(item.getPackageRemain() - 1);
                                    item.setRedeemCode(result.getRedeemCode());
                                    notifyDataSetChanged();
                                    CommDialog.Builder builder = new CommDialog.Builder(getActivity());
                                    GiftGetSuccDialogView dialogView = new GiftGetSuccDialogView(getActivity());
                                    builder.setView(dialogView);
                                    dialogView.setPackageCode(result.getRedeemCode());
                                    builder.create().show();
                                }

                                @Override
                                public void onFail(int errorNo, String errmsg) {
                                    super.onFail(errorNo, errmsg);
                                    if (loadingDialog != null && loadingDialog.isShowing()) {
                                        loadingDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                                    super.onNetError(url, params, errno, errmsg);
                                    if (loadingDialog != null && loadingDialog.isShowing()) {
                                        loadingDialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    if (item.getPackageType() == 1) {
                        holder.condition.setVisibility(View.VISIBLE);
                        if (item.getPackageAmounts() == 0) {
                            holder.condition.setText("无金额限制");
                        } else {
                            holder.condition.setText(getActivity().getString(R.string.game_package_get_limit_hint,
                                    CommonUtils.pennyToYuan(item.getPackageAmounts())));
                        }
                    } else {
                        holder.condition.setVisibility(View.GONE);
                    }
                } else {
                    holder.condition.setVisibility(View.GONE);
                    holder.get.setTextColor(getActivity().getResources().getColor(R.color.d_gray_2));
                    holder.get.setSelected(true);
                    holder.get.setText("已领");
                    holder.get.setOnClickListener(null);
                }
                holder.name.setText(item.getPackageName());
                holder.count.setText(getActivity().getString(R.string.game_package_num, item.getPackageRemain(), item.getPackageTotal()));
                holder.dueTime.setText(getActivity().getString(R.string.game_package_expire_time, TimeUtils.formatTimeYMD(Long.parseLong(item.getExpTime()))));
            } catch (NumberFormatException e) {
            }

            return convertView;
        }
    }

    class Holder {
        TextView name;
        TextView count;
        TextView dueTime;
        TextView get;
        TextView condition;
    }
}
