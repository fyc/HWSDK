package com.yiyou.gamesdk.core.ui.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.storage.events.GameDownloadEvent;
import com.yiyou.gamesdk.core.storage.events.GameDownloadEvent.GameDownloadEventParam;
import com.yiyou.gamesdk.core.storage.sharepref.Constant;
import com.yiyou.gamesdk.core.ui.dialog.BaseViewController;
import com.yiyou.gamesdk.model.GameUpdateInfo;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.IEventListener;
import com.yiyou.gamesdk.util.DownloadBridge;


public class GameDownloadDialogView extends BaseViewController implements View.OnClickListener {

    private static final String TAG = "GameDownloadDialogView";
    private TextView tv_title, tv_message, tv_ensure, tv_cancel;
    private LinearLayout layoutDialog , layoutGameDownLoad ;
    private IEventListener downloadListener ;
    private TextView txtProgress ;
    private EventDispatcherAgent eventDispatcherAgent ;
    private GameUpdateInfo mUpdateInfo ;
    private ProgressBar pb ;
    private CheckBox cbxNeverNotify;
    private Context windowContext;
    public GameDownloadDialogView(Context context , GameUpdateInfo updateInfo) {
        super(context);
        this.mUpdateInfo = updateInfo ;
        windowContext = context;
//        this.mUpdateInfo.gameName = "xxx" ;
//        this.mUpdateInfo.gameDownloadUr = "http://113.108.88.67/dd.myapp.com/16891/81B48F25908EEB12422105B9767A7823.apk" ;
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.title);
        tv_message = (TextView) findViewById(R.id.message);
        tv_ensure = (TextView) findViewById(R.id.ensure);
        tv_ensure.setOnClickListener(this);
        tv_cancel = (TextView) findViewById(R.id.cancel);
        layoutDialog = (LinearLayout) findViewById(R.id.layout_dialog) ;
        layoutGameDownLoad = (LinearLayout) findViewById(R.id.layout_download) ;
        txtProgress = (TextView) findViewById(R.id.txt_progress) ;
        pb = (ProgressBar) findViewById(R.id.pb_download) ;
        cbxNeverNotify = (CheckBox)findViewById(R.id.cbx_never_notify);


        tv_cancel.setOnClickListener(this);
        if (GameUpdateInfo.UPDATE_TYPE_FORCE.equals(mUpdateInfo.versionType)){
//            tv_cancel.setText("退出游戏");
            tv_ensure.setText("下载新包");
            tv_cancel.setVisibility(GONE);
            findViewById(R.id.divider_center).setVisibility(GONE);
            cbxNeverNotify.setVisibility(GONE);
        }
        tv_title.setText("标题");
        tv_message.setText("更新提示");

        if(mUpdateInfo.versionTittle!=null && !"".equals(mUpdateInfo.versionTittle)){
            tv_title.setText(mUpdateInfo.versionTittle);
        }
        if (mUpdateInfo.versionContent!=null && !"".equals(mUpdateInfo.versionContent)){
            tv_message.setText(mUpdateInfo.versionContent);
        }
    }


    public GameDownloadDialogView setTitleTip(int resid) {
        tv_title.setText(resid);
        return this;
    }


    public GameDownloadDialogView setMessageTip(int resid) {
        tv_message.setText(resid);
        return this;
    }


    public GameDownloadDialogView setEnsureText(int resid) {
        tv_ensure.setText(resid);
        return this;
    }

    public GameDownloadDialogView setCancelText(int resid) {
        tv_cancel.setText(resid);
        return this;
    }

    public void setDownloadingLayout(){

        layoutDialog.setVisibility(View.GONE);
        layoutGameDownLoad.setVisibility(View.VISIBLE);

    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.tt_sdk_dialog_game_download_alert;
    }

    @Override
    public void onShow() {
        initEvent();
    }

    @Override
    public void onHide() {

    }

    @Override
    public void onDismiss() {
        eventDispatcherAgent.removeEventListener(GameDownloadEvent.TYPE_GAME_DOWNLOAD, downloadListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                if(GameUpdateInfo.UPDATE_TYPE_NONFORCE.equals(mUpdateInfo.versionType)){
                    SharedPreferences sharedPreferences = windowContext.getApplicationContext().getSharedPreferences(Constant.KEY_DB_NAME,  Context.MODE_PRIVATE);
                    if(cbxNeverNotify.isChecked()){
                        sharedPreferences.edit().putBoolean(Constant.KEY_IS_NEVER_NOTIFY_UPDATE,true).putString(Constant.KEY_NEVER_NOTIFY_UPDATE_VERSION, mUpdateInfo.versionTittle).apply();
                    }else {
                        sharedPreferences.edit().putBoolean(Constant.KEY_IS_NEVER_NOTIFY_UPDATE,false).putString(Constant.KEY_NEVER_NOTIFY_UPDATE_VERSION, "").apply();
                    }
                }
                close();
                if (listener != null)
                    listener.onCancel();
                break;

            case R.id.ensure:
                ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() || networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(windowContext);
                    builder.setMessage("当前正处于非WIFI环境,建议在WIFI环境下下载游戏.");
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            beginDownload();
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }else{
                    beginDownload();
                }
                break;
        }
    }

    private void beginDownload() {
//        Log.d(TAG, "beginDownload: " + mUpdateInfo.gameDownloadUr);
        if (TextUtils.isEmpty(mUpdateInfo.gameDownloadUrl)){
            return;
        }
        setDownloadingLayout();
        DownloadBridge.getInstance().StartDownloadAPK(mUpdateInfo);
    }

    private void initEvent(){

        eventDispatcherAgent = EventDispatcherAgent.defaultAgent();

        downloadListener = new IEventListener<GameDownloadEvent.GameDownloadEventParam>() {

            @Override
            public void onEvent(String eventType, GameDownloadEventParam params) {
                String progressTip = "下载进度"+params.currentSize+"%";
                txtProgress.setText(progressTip);
//				pb.setMax(params.totalSize);
                pb.setProgress(params.currentSize);
            }
        };

        eventDispatcherAgent.addEventListener(GameDownloadDialogView.this, GameDownloadEvent.TYPE_GAME_DOWNLOAD, downloadListener);
    }



    private DialogClickListener listener;

    public void setListener(DialogClickListener listener) {
        this.listener = listener;
    }

    public interface DialogClickListener {
        void onCancel();
    }




}
