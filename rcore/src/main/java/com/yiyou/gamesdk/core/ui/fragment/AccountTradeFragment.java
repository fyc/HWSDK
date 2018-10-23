package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.RequestManager;
import com.yiyou.gamesdk.core.base.http.utils.AppInternalHandler;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.base.http.volley.view.HttpImageView;
import com.yiyou.gamesdk.model.InventoriesInfo;
import com.yiyou.gamesdk.model.InventoryInfo;
import com.yiyou.gamesdk.util.CommonUtils;
import com.yiyou.gamesdk.util.QNImageMogrHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nekomimi on 2017/11/13.
 */

public class AccountTradeFragment extends BaseFragment {

    private ListView mTradeList;
    private Adapter mAdapter;
    private List<InventoryInfo> mDataList = new ArrayList<>();
    private View mEmptyHint;
    private View mSell;

//    private final static int MOST_COUNT_ORIENTATION_PORTRAIT = 10;
//    private final static int MOST_COUNT_ORIENTATION_LANDSCAPE = 5;
//    private int mostCount;

    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        View root = LayoutInflater.from(content).inflate(R.layout.sdk_fragment_account_trade, container, false);
        initView(root);
        container.addView(root);
    }

    @Override
    protected void afterSetFragmentContent(Context context, Fragment titleBarFragment) {
        super.afterSetFragmentContent(context, titleBarFragment);
        getTradeInfo();
    }

    private void getTradeInfo() {
        ApiFacade.getInstance().getInventories(new TtRespListener<InventoriesInfo>() {
            @Override
            public void onNetSucc(String url, Map<String, String> params, InventoriesInfo result) {
                super.onNetSucc(url, params, result);
                if (isAdded()) {
                    if (getParentFragment() instanceof GameFragment && mDataList.size() > 0) {
                        ((GameFragment) getParentFragment()).showViewPagerMsg(2, mDataList.size());
                    }
                    if (result.getInventories() != null && !result.getInventories().isEmpty()) {
                        mDataList = result.getInventories();
                        mAdapter.setmDataList(mDataList);
                        mAdapter.notifyDataSetChanged();
                        mEmptyHint.setVisibility(View.GONE);
                        mTradeList.setVisibility(View.VISIBLE);
                        mSell.setVisibility(View.VISIBLE);
                        if (mTradeList.getHeaderViewsCount() == 0 && mDataList != null && !mDataList.isEmpty()) {
                            View footerView = LayoutInflater.from(mTradeList.getContext()).inflate(R.layout.sdk_item_footer_gift, mTradeList, false);
                            footerView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AppInternalHandler.currentInventoryList(getActivity(), ApiFacade.getInstance().getCurrentGameID());
                                }
                            });
                            mTradeList.addFooterView(footerView);
                        }
                        mAdapter.notifyDataSetChanged();
                        Fragment parentFragment = getParentFragment();
                        Log.d(myTag, "getParentFragment() fragment = " + parentFragment);
                        if (parentFragment instanceof GameFragment) {
                            ((GameFragment) parentFragment).showViewPagerMsg(2, result.getInventories().size());
                        }
                    } else {
                        mEmptyHint.setVisibility(View.VISIBLE);
                        mTradeList.setVisibility(View.GONE);
                        mSell.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            Fragment parentFragment = getParentFragment();
//            Log.d(myTag, "getParentFragment() fragment = " + parentFragment);
//            if (parentFragment instanceof GameFragment) {
//                ((GameFragment) parentFragment).hideViewPagerMsg(2);
//            }
//        }
    }

    @Override
    public String getTabName() {
        return "账号交易";
    }

    private void initView(@NonNull final View itemView) {
//        mostCount = PluginManager.getInstance().getOrientation() == Configuration.ORIENTATION_LANDSCAPE ?
//                MOST_COUNT_ORIENTATION_LANDSCAPE : MOST_COUNT_ORIENTATION_PORTRAIT;
        mEmptyHint = itemView.findViewById(R.id.empty_hint);
        mTradeList = (ListView) itemView.findViewById(R.id.list_trade);
        mSell = itemView.findViewById(R.id.sell);
        mSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppInternalHandler.sellMyPlayed(getActivity());
            }
        });
        mAdapter = new Adapter(getContext());
        mTradeList.setAdapter(mAdapter);
        mTradeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInternalHandler.inventoryDetail(getActivity(), mDataList.get(position).getInventoryID(), ApiFacade.getInstance().getCurrentGameID());
            }
        });
        mTradeList.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean scrollFlag = false;// 标记是否滑动
            private int lastVisibleItemPosition;// 标记上次滑动位置

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ||
                        scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    scrollFlag = true;
                } else {
                    scrollFlag = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollFlag) {
                    if (firstVisibleItem > lastVisibleItemPosition) {
                        if (mSell.getVisibility() != View.INVISIBLE) {
                            ViewCompat.animate(mSell).alpha(0)
                                    .setDuration(200)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            mSell.setVisibility(View.INVISIBLE);
                                        }
                                    })
                                    .start();
                        }
                    }
                    if (firstVisibleItem < lastVisibleItemPosition) {
                        if (mSell.getVisibility() != View.VISIBLE) {
                            mSell.setVisibility(View.VISIBLE);
                            ViewCompat.animate(mSell).alpha(1f)
                                    .setDuration(200)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            mSell.setVisibility(View.VISIBLE);
                                        }
                                    })
                                    .start();
                        }
                    }
                    if (firstVisibleItem == lastVisibleItemPosition) {
                        return;
                    }
                    lastVisibleItemPosition = firstVisibleItem;
                }
            }
        });
    }

    public int getScrollY(ListView listView) {
        View c = listView.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight();
    }

    class Adapter extends BaseAdapter {

        private Context context;
        private List<InventoryInfo> mDataList = new ArrayList<>();

        public Adapter(Context context) {
            this.context = context;
        }

        public List<InventoryInfo> getmDataList() {
            return mDataList;
        }

        public void setmDataList(List<InventoryInfo> mDataList) {
            this.mDataList = mDataList;
        }

        @Override
        public int getCount() {
            return mDataList != null ?  mDataList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mDataList != null ? mDataList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            InventoryHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.sdk_item_inventory, parent, false);
                holder = new InventoryHolder(convertView);
            } else {
                holder = (InventoryHolder) convertView.getTag();
            }
            InventoryInfo info = mDataList.get(position);
            holder.title.setText(info.getTitle());
            holder.price.setText(CommonUtils.format("￥%d", info.getPrice() / 100));
            holder.desc.setText(info.getDesc());
            if (info.getMateriales() != null && info.getMateriales().size() > 0) {
                holder.cover.setImageUrl(QNImageMogrHelper.handleCoverImgUrl(info.getMateriales().get(0).getPicURL()), RequestManager.getInstance(context).getImageLoader());
            }
            return convertView;
        }
    }

    class InventoryHolder {

        private View root;
        private TextView title;
        private HttpImageView cover;
        private TextView desc;
        private TextView price;

        public InventoryHolder(View root) {
            this.root = root;
            this.cover = (HttpImageView) root.findViewById(R.id.img_cover);
            this.desc = (TextView) root.findViewById(R.id.desc);
            this.price = (TextView) root.findViewById(R.id.goods_price);
            this.title = (TextView) root.findViewById(R.id.title_inventory);
            root.setTag(this);
        }
    }
}
