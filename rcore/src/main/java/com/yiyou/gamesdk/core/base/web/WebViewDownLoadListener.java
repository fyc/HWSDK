package com.yiyou.gamesdk.core.base.web;

import android.content.Intent;
import android.net.Uri;
import android.webkit.DownloadListener;

import com.yiyou.gamesdk.core.CoreManager;

public class WebViewDownLoadListener implements DownloadListener {

	@Override
	public void onDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		CoreManager.getContext().startActivity(intent);
	}

}