package com.yiyou.gamesdk.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.webkit.MimeTypeMap;

import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.storage.events.GameDownloadEvent;
import com.yiyou.gamesdk.model.GameUpdateInfo;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DownloadBridge {

	private static final String TAG = "RSDK: "+"DownloadBridge";
	private static DownloadBridge downloadBridge;
	private static final String DL_ID = "downloadId1234567";
	public static final Uri CONTENT_URI = Uri
			.parse("content://downloads/my_downloads");
	public DownloadManager downloadManager;
	public long downloadID;
	private SharedPreferences prefs;
	private String apkUrl = "";
	private DownloadChangeObserver downloadObserver;
	public static final String DOWN_FILE_DIR = "/download/";
	public static boolean ISQUIT = true;
	private int mProgress;


	private DownloadBridge() {
		init();
	}

	public static DownloadBridge getInstance() {
		if (null == downloadBridge)
			downloadBridge = new DownloadBridge();
		ISQUIT = false;
		return downloadBridge;
	}


	public void uninit() {
		CoreManager.getContext().getContentResolver().unregisterContentObserver(downloadObserver);
		downloadManager = null;
		downloadObserver = null;

		downloadBridge = null;
	}


	@SuppressLint("InlinedApi")
	private void init() {

		downloadObserver = new DownloadChangeObserver(null);
		CoreManager.getContext().getContentResolver().registerContentObserver(CONTENT_URI,
				true, downloadObserver);

		downloadManager = (DownloadManager) CoreManager.getContext().getSystemService(
				Context.DOWNLOAD_SERVICE);
		prefs = PreferenceManager.getDefaultSharedPreferences(CoreManager.getContext());
	}

	@TargetApi(11)
	public void StartDownloadAPK(GameUpdateInfo gameUpdateInfo) {

        apkUrl = gameUpdateInfo.gameDownloadUrl;
//		apkUrl = "https://img3.doubanio.com/dae/andariel/static/upload/com.douban.frodo_douban_4.17.0.beta_96.apk";
		Log.d(TAG,"apkUrl "+ apkUrl);

		// 开始下载
		Uri resource = Uri.parse(apkUrl);
		Request request = new Request(
				resource);
//		request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
		request.setAllowedOverRoaming(false);
		// 设置文件类型
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		String mimeString = mimeTypeMap
				.getMimeTypeFromExtension(MimeTypeMap
						.getFileExtensionFromUrl(apkUrl));
		request.setMimeType(mimeString);
		// 在通知栏中显示
		request.setShowRunningNotification(true);
		request.setVisibleInDownloadsUi(true);
		if (Build.VERSION.SDK_INT >= 11) {
			request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "game_tt.apk");
		request.setTitle(gameUpdateInfo.gameName);
		downloadID = downloadManager.enqueue(request);
		// 保存id
		updateDownLoadId((int) downloadID);
	}



	/**
	 * 如果服务器不支持中文路径的情况下需要转换url的编码。
	 *
	 * @param string
	 * @return
	 */
	public String encodeGB(String string) {
		// 转换中文编码
		String split[] = string.split("/");
		for (int i = 1; i < split.length; i++) {
			try {
				split[i] = URLEncoder.encode(split[i], "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			split[0] = split[0] + "/" + split[i];
		}
		split[0] = split[0].replaceAll("\\+", "%20");// 处理空格
		return split[0];
	}

	public void UpdateProgress(long curSize, long TotalSize) {

		if (TotalSize == 0 )
			return;

		float radio = (float) (curSize * 100 )/ TotalSize;
		int progress = Math.round(radio);

		if (progress > mProgress) {
			mProgress = progress;
			EventDispatcherAgent.defaultAgent().broadcast(GameDownloadEvent.TYPE_GAME_DOWNLOAD,
					new GameDownloadEvent.GameDownloadEventParam(mProgress, 0));
		}

	}



	public static String getFileName(String path) {
		int start = path.lastIndexOf("/") + 1;
		return path.substring(start, path.length());
	}

	private void updateDownLoadId(int id) {
		Context gameContext = CoreManager.getContext().getApplicationContext();
		SharedPreferences sp = PreferenceUtils.SDKPreference.instancePreferences(gameContext);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(PreferenceUtils.SDKPreference.KEY_GAME_DOWNLOAD_ID, id);
		editor.apply();
	}

}
