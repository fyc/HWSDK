/**
 * @author Luoweiqiang
 * @date 2015年5月22日 上午11:54:44
 * @version V1.0
 */
package com.yiyou.gamesdk.testapp;

import android.app.Application;
import android.os.Build;


/**
 * @author Luoweiqiang
 *
 */
public class TestApplication extends Application {

    private static final String TAG = "TTSDK: " + TestApplication.class.getSimpleName();


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String model = Build.MODEL;
    }


}
