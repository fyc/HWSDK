package com.yiyou.gamesdk.core.ui.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.yiyou.gamesdk.PluginManager;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.core.ui.dialog.biz.LoadingDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.AlertDialogView;
import com.yiyou.gamesdk.core.ui.widget.dialog.CommDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.EditInputDialogView;
import com.yiyou.gamesdk.core.ui.widget.dialog.IDialogView;
import com.yiyou.gamesdk.core.ui.widget.dialog.OnDialogClickListener;
import com.yiyou.gamesdk.model.AuthModel;
import com.yiyou.gamesdk.model.ChildrenAccountHistoryInfo;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;
import com.yiyou.gamesdk.util.PropertiesUtil;
import com.yiyou.gamesdk.util.ToastUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  小号管理的fragment
 * Created by Nekomimi on 2017/4/25.
 */

public class ChildrenAccountFragment extends BaseFragment implements View.OnClickListener{
    private static final String TAG = "ChildrenAccountFragment";

    private ListView childrenAccountLv;
    private Button addChildrenAccountBtn;
    private Adapter adapter;
    private LoadingDialog loadingDialog;


    private static final int MAX_CHILDACCOUNT_NAME_LENGTH = 20;
    private static final int MIN_CHILDACCOUNT_NAME_LENGTH = 2;
    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        Log.d(TAG, "setFragmentContent: ");
        View root = LayoutInflater.from(content).inflate(R.layout.tt_sdk_fragment_children_account,container,false);
        loadingDialog = new LoadingDialog(getActivity());
        childrenAccountLv = (ListView)root.findViewById(R.id.lv_children_account);
        addChildrenAccountBtn = (Button)root.findViewById(R.id.btn_add_children_account);
        adapter = new Adapter(content, ApiFacade.getInstance().getCurrentChildrenAccountHistory());
        childrenAccountLv.setAdapter(adapter);
        addChildrenAccountBtn.setOnClickListener(this);
        childrenAccountLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                Log.d(TAG, "onClick: " + adapter.childrenAccountHistoryInfos.get(position).childrenUserID);
                if (getCurrentChildrenAccountUid() != adapter.childrenAccountHistoryInfos.get(position).childrenUserID){
                    CommDialog.Builder builder = new CommDialog.Builder(getActivity());
                    final AlertDialogView dialogView = new AlertDialogView(getActivity());
                    dialogView.setEnsureText(R.string.ensure);
                    dialogView.setMessageTip(PropertiesUtil.parseChangeAccount4Config(ChildrenAccountFragment.this.getContext())
                            .toLowerCase().equals("true") ? R.string.str_change_children_account_tip :
                            R.string.str_change_children_account_reboot_tip);
                    dialogView.setTitleTip(0);
                    builder.setView(dialogView);
                    builder.setLisenter(new OnDialogClickListener() {
                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public void onEnsure(IDialogView view1) {
                            changeChildrenAccount(adapter.childrenAccountHistoryInfos.get(position));
                        }
                    });
                    CommDialog changeChildDialog = builder.create();
                    changeChildDialog.show();
                }
            }
        });
        update();
        container.addView(root);
    }

    private void update(){
        if (getCurrentChildrenAccountCount() >= getMaxChildrenAccountCount()){
            addChildrenAccountBtn.setVisibility(View.GONE);
        }else {
            addChildrenAccountBtn.setVisibility(View.VISIBLE);
        }
    }


    public  class Adapter extends BaseAdapter implements View.OnClickListener{
        private List<ChildrenAccountHistoryInfo> childrenAccountHistoryInfos;
        private Context context;

        public Adapter(Context context,List<ChildrenAccountHistoryInfo> childrenAccountHistoryInfos) {
            this.context = context;
            this.childrenAccountHistoryInfos = childrenAccountHistoryInfos;
        }

        public List<ChildrenAccountHistoryInfo> getChildrenAccountHistoryInfos() {
            return childrenAccountHistoryInfos;
        }

        public void setChildrenAccountHistoryInfos(List<ChildrenAccountHistoryInfo> childrenAccountHistoryInfos) {
            this.childrenAccountHistoryInfos = childrenAccountHistoryInfos;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return childrenAccountHistoryInfos == null ? 0 : childrenAccountHistoryInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return childrenAccountHistoryInfos == null ? null : childrenAccountHistoryInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ChildrenAccountHolder holder;
            if (convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.tt_sdk_layout_children_account,parent,false);
                holder = new ChildrenAccountHolder();
                holder.usernameTv = (TextView)convertView.findViewById(R.id.tv_children_account_username);
                holder.isSelectedTv = (TextView)convertView.findViewById(R.id.tv_is_selected);
                holder.edit = (ImageView)convertView.findViewById(R.id.btn_edit);
                holder.edit.setOnClickListener(this);
                convertView.setTag(holder);
            }else {
                holder = (ChildrenAccountHolder) convertView.getTag();
            }
            holder.edit.setTag(position);
            holder.usernameTv.setText(childrenAccountHistoryInfos.get(position).childrenUsername);
            holder.isSelectedTv.setText(getCurrentChildrenAccountUid()==childrenAccountHistoryInfos.get(position).childrenUserID?"(当前)":"");
            return convertView;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_edit){
                final int position = (Integer)v.getTag();
                editChildAccount(position);
            }
        }
    }

    private static class ChildrenAccountHolder{
        TextView usernameTv;
        TextView isSelectedTv;
        ImageView edit;
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return CommonTitlePrimaryFragment.newInstance(getString(R.string.children_account_manager));

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
            case R.id.btn_add_children_account:
                if (getCurrentChildrenAccountCount() < getMaxChildrenAccountCount()){
                    addChildAccount();
                }
                break;
            default:
                break;
        }
    }

    private void changeChildrenAccount(ChildrenAccountHistoryInfo info){
        Log.d(TAG, "changeChildrenAccount: " + info.childrenUserID);
        ApiFacade.getInstance().setLastLoginChildAccount(info.childrenUserID,info.childrenUsername,info.TTAccount);
        adapter.notifyDataSetChanged();
        if(!PropertiesUtil.parseChangeAccount4Config(this.getContext()).toLowerCase().equals("true")){
            ToastUtils.showMsg("切换成功，即将重启游戏");
            reboot();
        }else {
            ApiFacade.getInstance().logout(new IOperateCallback<String>() {
                @Override
                public void onResult(int i, String s) {
                    loadingDialog.dismiss();
                    if (i == TTCodeDef.SUCCESS) {
                        getActivity().finish();
                        if (PluginManager.getInstance().getLogoutCallback() != null) {
                            Log.d(TAG, "onResult: getLogoutCallback" );
                            PluginManager.getInstance().getLogoutCallback().onResult(i, s);
                        }
                    }
                }
            });
        }
    }

    private void reboot(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                    android.os.Process.killProcess(android.os.Process.myPid());
//                    Intent i = getActivity().getBaseContext().getPackageManager()
//                            .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    Log.d(TAG, "restart: ");
//                    startActivity(i);
                try {
                    Context c = getContext();
                    //check if the context is given
                    if (c != null) {
                        //fetch the packagemanager so we can get the default launch activity
                        // (you can replace this intent with any other activity if you want
                        PackageManager pm = c.getPackageManager();
                        //check if we got the PackageManager
                        if (pm != null) {
                            //create the intent with the default start activity for your application
                            Intent mStartActivity = pm.getLaunchIntentForPackage(
                                    c.getPackageName()
                            );
                            if (mStartActivity != null) {
                                mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                //create a pending intent so the application is restarted after System.exit(0) was called.
                                // We use an AlarmManager to call this intent in 100ms
                                int mPendingIntentId = 223344;
                                PendingIntent mPendingIntent = PendingIntent
                                        .getActivity(c, mPendingIntentId, mStartActivity,
                                                PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 200, mPendingIntent);
                                //kill the application
                                    android.os.Process.killProcess(android.os.Process.myPid());
//                                System.exit(0);
                            } else {
                                Log.e(TAG, "Was not able to restart application, mStartActivity null");
                            }
                        } else {
                            Log.e(TAG, "Was not able to restart application, PM null");
                        }
                    } else {
                        Log.e(TAG, "Was not able to restart application, Context null");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "Was not able to restart application");
                }
            }
        },2000);
    }
    private void editChildAccount(final int position){
            CommDialog.Builder builder = new CommDialog.Builder(getActivity());
            final EditInputDialogView dialogView = new EditInputDialogView(getActivity());
            builder.setView(dialogView);
            dialogView.setTitleTip(R.string.edit_children_account_tip);
            dialogView.setEnsureText(R.string.str_edt);
            builder.setLisenter(new OnDialogClickListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onEnsure(final IDialogView view) {
                    if (!isInputLegal(dialogView.getInput().trim())){
                        return;
                    }
                    loadingDialog.show();
                    ApiFacade.getInstance().editChildrenAccountName(adapter.childrenAccountHistoryInfos.get(position).childrenUserID,
                            dialogView.getInput().trim(),new TtRespListener<AuthModel.childAccount>(){
                        @Override
                        public void onNetSucc(String url, Map<String, String> params, AuthModel.childAccount result) {
                            super.onNetSucc(url, params, result);
                            ChildrenAccountHistoryInfo historyInfo = adapter.childrenAccountHistoryInfos.get(position);
                            historyInfo.childrenUsername = dialogView.getInput().trim();
                            ApiFacade.getInstance().insertOrUpdateChildrenAccountHistory(historyInfo);
                            adapter.setChildrenAccountHistoryInfos(ApiFacade.getInstance().getCurrentChildrenAccountHistory());
                            adapter.notifyDataSetChanged();
                            update();
                            view.close();
                        }

                        @Override
                        public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                            super.onNetError(url, params, errno, errmsg);
                        }

                        @Override
                        public void onFail(int errorNo, String errmsg) {
                            super.onFail(errorNo, errmsg);
                        }

                        @Override
                        public void onNetworkComplete() {
                            super.onNetworkComplete();
                            loadingDialog.dismiss();
                        }
                    });
                }
            });
        builder.create().show();
    }

    private void addChildAccount(){
            CommDialog.Builder builder = new CommDialog.Builder(getActivity());
            final EditInputDialogView dialogView = new EditInputDialogView(getActivity());
            builder.setView(dialogView);
            dialogView.setTitleTip(R.string.str_add_children_account);
            dialogView.setEnsureText(R.string.str_add);
            builder.setLisenter(new OnDialogClickListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onEnsure(final IDialogView view) {
                    if (!isInputLegal(dialogView.getInput().trim())){
                        return;
                    }
                    loadingDialog.show();
                    ApiFacade.getInstance().registerChildAccount(dialogView.getInput().trim(),new TtRespListener<AuthModel.childAccount>(){
                        @Override
                        public void onNetSucc(String url, Map<String, String> params, AuthModel.childAccount result) {
                            super.onNetSucc(url, params, result);
                            ChildrenAccountHistoryInfo historyInfo = new ChildrenAccountHistoryInfo();
                            historyInfo.childrenUserID = result.getChildUserID();
                            historyInfo.userID = result.getUserID();
                            historyInfo.bundleID = result.getBundleID();
                            historyInfo.childrenUsername = result.getChildUserName();
                            historyInfo.gameId = String.valueOf(ApiFacade.getInstance().getCurrentGameID());
                            historyInfo.TTAccount = result.getTTAccount();
                            historyInfo.lastLoginTime = new Date().getTime();
                            ApiFacade.getInstance().insertOrUpdateChildrenAccountHistory(historyInfo);
                            adapter.setChildrenAccountHistoryInfos(ApiFacade.getInstance().getCurrentChildrenAccountHistory());
                            adapter.notifyDataSetChanged();
                            update();
                            view.close();
                        }

                        @Override
                        public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                            super.onNetError(url, params, errno, errmsg);
                        }

                        @Override
                        public void onFail(int errorNo, String errmsg) {
                            super.onFail(errorNo, errmsg);
                        }

                        @Override
                        public void onNetworkComplete() {
                            super.onNetworkComplete();
                            loadingDialog.dismiss();
                        }
                    });
                }
            });
        builder.create().show();

    }

    /**
     *
     * @return  当前小号uid
     */
    private  long getCurrentChildrenAccountUid(){
        return ApiFacade.getInstance().getSubUid();
    }

    private int getCurrentChildrenAccountCount(){
        return adapter.getChildrenAccountHistoryInfos().size();
    }


    /**
     *
     * @return  小号数量限制
     */
    private int getMaxChildrenAccountCount(){
        return 10;
    }

    private boolean isInputLegal(String str){
        int length = str.length();
        if (length < MIN_CHILDACCOUNT_NAME_LENGTH){
            ToastUtils.showMsg(R.string.str_too_short_account);
            return false;
        }
        if (length > MAX_CHILDACCOUNT_NAME_LENGTH){
            ToastUtils.showMsg(R.string.str_too_long_account);
            return false;
        }
        return true;
    }
}
