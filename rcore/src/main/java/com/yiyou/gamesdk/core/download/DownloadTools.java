package com.yiyou.gamesdk.core.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.base.http.volley.listener.FileDownListener;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.util.PackageUtil;
import com.yiyou.gamesdk.util.ToastUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * download tools
 * Created by charles on 13/7/16.
 */
public class DownloadTools extends BroadcastReceiver {

    private static final String TAG = "DownloadTools";
    private Map<Long,FileDownListener> callbacks = new HashMap<>();

    private static DownloadTools instance;

    private Context appContext;
    private DownloadManager downloadManager;


    private DownloadTools(Context context) {
        appContext = context.getApplicationContext();
        downloadManager = (DownloadManager) appContext.getSystemService(Context.DOWNLOAD_SERVICE);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        appContext.registerReceiver(this, intentFilter);


    }

    /**
     * 关闭下载器
     */
    public void close() {

        appContext.unregisterReceiver(this);
        callbacks.clear();
        downloadManager = null;

        instance = null;


    }


    private void downloadStatus() {


        DownloadManager.Query query = new DownloadManager.Query();

        Cursor cursor = downloadManager.query(query);


        while (cursor.moveToNext()) {

            long taskId = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));

            FileDownListener listener = callbacks.get(taskId);

            if (listener == null) {
                Log.d(TAG, "listener is null  " + taskId);
                return;
            }


            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

            long total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));


            switch (status) {

                case DownloadManager.STATUS_SUCCESSFUL:

                    listener.onFinish(total);

                    String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                    try {
                        File file = new File(new URI(uri));
                        listener.onDownLoadSucc(file);

                    } catch (URISyntaxException e) {
                        listener.onDownLoadFail(e.getMessage());
                        e.printStackTrace();
                    }

                    callbacks.remove(taskId);


                    break;

                case DownloadManager.STATUS_FAILED:
                    listener.onDownLoadFail("download error");
                    callbacks.remove(taskId);


                    break;

                case DownloadManager.STATUS_PAUSED:

                    System.out.println("uri = ");

                    break;

                case DownloadManager.STATUS_PENDING:

                    break;

                case DownloadManager.STATUS_RUNNING:

                    long cur_size = cursor.getLong(cursor.getColumnIndex
                            (DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));

                    listener.onProgress(cur_size, total);

                    break;


            }


        }


    }


    public static DownloadTools getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadTools(context);
        }
        return instance;
    }


    /**
     * 静默下载文件 只在wifi状态下下载
     * <p>
     * this requires the permission android.permission.DOWNLOAD_WITHOUT_NOTIFICATION.
     *
     * @param url      下载链接
     * @param savePath 保存目录
     * @param fileName 文件名
     * @return taskid
     */
    public long downloadSilent(String url, String savePath, String fileName, FileDownListener listener) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalFilesDir(appContext, savePath, fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        String mimeType = mimeTypeMap.getMimeTypeFromExtension(url);
        request.setMimeType(mimeType);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

        long taskId = downloadManager.enqueue(request);
        callbacks.put(taskId, listener);

        return taskId;

    }

    /**
     * 下载文件
     *
     * @param url      下载链接
     * @param savePath 保存目录
     * @param fileName 文件名
     * @return taskId
     */
    public long download(String url, String savePath, String fileName, boolean onlyWifi,FileDownListener listener) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        File file = new File(savePath,fileName);
        request.setDestinationUri(Uri.fromFile(file));

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeType = mimeTypeMap.getMimeTypeFromExtension(url);
        request.setMimeType(mimeType);

        if (onlyWifi) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        }

//        if (silent) {
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//        }

        long taskId = downloadManager.enqueue(request);
        callbacks.put(taskId, listener);

        return taskId;

    }


    /**
     * wifi 状态下载apk 并安装
     *
     * @param url 下载链接
     * @return taskId
     */
    public long downloadApkAndInstall(String url) {

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showMsg("sdcard 不可用");
            return 0;
        }

        File savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String downFileName = System.currentTimeMillis() + ".apk";


        long taskId = download(url, savePath.getAbsolutePath(), downFileName, true, new FileDownListener() {

            @Override
            public void onDownLoadSucc(File file) {

                Uri uri = Uri.fromFile(file);
                PackageUtil.installApk(CoreManager.getContext(), uri);

            }
        });


        return taskId;
    }


    /**
     * 取消下载
     *
     * @param taskId the IDs of the downloads to remove
     * @return the number of downloads actually removed
     */
    public int cancelDownload(long... taskId) {

        return downloadManager.remove(taskId);

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        downloadStatus();
    }
}
