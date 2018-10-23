package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.core.ui.widget.roundview.RoundTextView;
import com.yiyou.gamesdk.model.AnnouncementInfo;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;
import com.yiyou.gamesdk.util.PackageUtil;
import com.yiyou.gamesdk.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nekomimi on 2017/11/9.
 */

public class MessageFragment extends BaseFragment {
    private static final String TAG = "MessageFragment";
    private ListView mMessageList;
    private Adapter mAdapter;
    private View emptyHint;
    private MessageInfoFragment mMessageInfoFragment;

    private List<AnnouncementInfo> mAnnouncementInfos = new ArrayList<>();

    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        View root = LayoutInflater.from(content).inflate(R.layout.sdk_fragment_message, container, false);
        initView(root);
        container.addView(root);
    }

    private void initView(@NonNull final View itemView) {
        emptyHint = itemView.findViewById(R.id.empty_hint);
        mMessageList = (ListView) itemView.findViewById(R.id.list_message);
        mAdapter = new Adapter(getContext());
        mMessageInfoFragment = new MessageInfoFragment();
        mMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMessageInfoFragment.setUrl(mAnnouncementInfos.get(position).getUrl());
                mMessageInfoFragment.setTitle(mAnnouncementInfos.get(position).getTitle());
                int day = TimeUtils.fromDay(mAnnouncementInfos.get(position).getFirstDay());
                String time = day > 0 ? getString(R.string.str_day_count, day) : "今天";
                mMessageInfoFragment.setTime(time);
                startFragment(mMessageInfoFragment);
            }
        });
    }

    @Override
    protected void afterSetFragmentContent(Context context, Fragment titleBarFragment) {
        super.afterSetFragmentContent(context, titleBarFragment);
        List<AnnouncementInfo> announcementInfos = ApiFacade.getInstance().getLocalAnnouncement();
        for (AnnouncementInfo info : announcementInfos){
            if (!TextUtils.isEmpty(info.getTitle()) && !TextUtils.isEmpty(info.getFirstDay()) && !TextUtils.isEmpty(info.getUrl())){
                mAnnouncementInfos.add(info);
            }
        }
        emptyHint.setVisibility(mAnnouncementInfos.size() > 0 ? View.GONE : View.VISIBLE);
        mMessageList.setVisibility(mAnnouncementInfos.size() > 0 ? View.VISIBLE : View.GONE);
        mAdapter.setAnnouncementInfos(mAnnouncementInfos);
        mMessageList.setAdapter(mAdapter);
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return CommonTitlePrimaryFragment.newInstance("消息通知");
    }

    @Override
    public NativeTitleBarUpdateInfo getTitleBarConfig() {
        NativeTitleBarUpdateInfo updateInfo = new NativeTitleBarUpdateInfo();
        updateInfo.showBackButton = false;
        updateInfo.showCloseButton = false;
        return updateInfo;
    }

    class MessageHolder{
        TextView title;
        TextView time;
        RoundTextView type;
        View root;

        public MessageHolder(View root) {
            this.root = root;
            title = (TextView)root.findViewById(R.id.message_title);
            time = (TextView)root.findViewById(R.id.message_time);
            type = (RoundTextView)root.findViewById(R.id.message_type);
            root.setTag(this);
        }
    }

    class Adapter extends BaseAdapter{
        private Context context;
        private List<AnnouncementInfo> announcementInfos;

        public Adapter(Context context) {
            this.context = context;
        }

        public void setAnnouncementInfos(List<AnnouncementInfo> announcementInfos) {
            this.announcementInfos = announcementInfos;
        }

        @Override
        public int getCount() {
            return announcementInfos!=null ? announcementInfos.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return announcementInfos!=null ? announcementInfos.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MessageHolder holder;
            if (convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.sdk_layout_message_item,parent,false);
                holder = new MessageHolder(convertView);
            }else {
                holder = (MessageHolder)convertView.getTag();
            }
            if (announcementInfos.get(position).getFirstDay()!=null){
                int day = TimeUtils.fromDay(announcementInfos.get(position).getFirstDay());
                if (day > 0){
                    holder.time.setText(getString(R.string.str_day_count,day));
                }else {
                    holder.time.setText("今天");
                }
            }
            if (announcementInfos.get(position).getTitle()!=null){
                holder.title.setText(announcementInfos.get(position).getTitle());
            }
            if (announcementInfos.get(position).getIsPlatformBulletin() == 0){
                Log.d(TAG, "app名字: " + PackageUtil.getAppName(context));
                holder.type.setText(PackageUtil.getAppName(context));
            }else {
                holder.type.setText("桃子官方");
            }
            return convertView;
        }
    }


}
