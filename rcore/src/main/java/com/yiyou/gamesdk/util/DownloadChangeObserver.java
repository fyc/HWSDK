package com.yiyou.gamesdk.util;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.consts.EventConstant;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.util.Log;

public class DownloadChangeObserver extends ContentObserver {

    private static final String TAG = "RSDK: "+"DownloadChangeObserver";

    public DownloadChangeObserver(Handler handler) {
        super(handler);
    }


    @Override
    public void onChange(boolean selfChange) {
        queryDownloadStatus();
    }

    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(DownloadBridge.getInstance().downloadID);
        Cursor c = DownloadBridge.getInstance().downloadManager.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    int fileSizeIdx = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                    int bytesDLIdx = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                    long fileSize = c.getLong(fileSizeIdx);
                    long bytesDL = c.getLong(bytesDLIdx);

                    String url = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                    switch (status) {
                        case DownloadManager.STATUS_PAUSED:
                        case DownloadManager.STATUS_PENDING:
                        case DownloadManager.STATUS_RUNNING:
                            //正在下载，不做任何事情
                            DownloadBridge.getInstance().UpdateProgress(bytesDL, fileSize);

                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            //完成
                            Log.i(TAG, "下载完成");
                            EventDispatcherAgent.defaultAgent().broadcast(EventConstant.DOWNLOAD_APK_SUCC, null);
                            SystemInstall(url);
                            break;
                        case DownloadManager.STATUS_FAILED:
                            DownloadBridge.getInstance().downloadManager.remove(getDownLoadId());
                            //清除已下载的内容，重新下载
                            break;
                    }
                }
            } finally {
                c.close();
            }

        }
    }

    private int getDownLoadId() {
        Context gameContext = CoreManager.getContext().getApplicationContext();
        SharedPreferences sp = PreferenceUtils.SDKPreference.instancePreferences(gameContext);
        int downloadId = sp.getInt(PreferenceUtils.SDKPreference.KEY_GAME_DOWNLOAD_ID, 0);
        return downloadId;
    }


    public void SystemInstall(String filepath) {
        Uri uri = Uri.parse(filepath);
        Intent intent_system = new Intent();
        intent_system.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_system.setAction(Intent.ACTION_VIEW);
        intent_system.setDataAndType(uri, "application/vnd.android.package-archive");
        CoreManager.getContext().startActivity(intent_system);
    }

}
