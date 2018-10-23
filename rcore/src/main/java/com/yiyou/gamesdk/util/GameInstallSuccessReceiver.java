package com.yiyou.gamesdk.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GameInstallSuccessReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {//替换当前游戏apk
			//替换成功 删除apk包
		}
	}

}
