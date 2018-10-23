package com.yiyou.gamesdk.core.interceptor;

import android.content.Context;
import android.content.SharedPreferences;

import com.yiyou.gamesdk.core.storage.sharepref.Constant;
import com.yiyou.gamesdk.core.ui.dialog.ViewControllerNavigator;
import com.yiyou.gamesdk.core.ui.widget.GameDownloadDialogView;
import com.yiyou.gamesdk.model.GameUpdateInfo;
import com.mobilegamebar.rsdk.outer.util.Log;

/**
 * show force dialog interceptor
 * Created by shui on 4/11/16.
 */
public class ShowForceDialogInterceptor implements InitInterceptor {

    private static final String TAG = "TTSDK:ShowForceDialogInterceptor";


    @Override
    public void intercept(final Chain<InitParams> chain) {

        final InitParams initParams = chain.getData();
        GameUpdateInfo result = initParams.getGameUpdateInfo();
        if (result == null) {
            Log.d(TAG,"result is null");
            chain.proceed(initParams);
            return;
        }

        Log.d(TAG,"result: " +result.toString());
        if (GameUpdateInfo.UPDATE_TYPE_FORCE.equals(result.versionType)||GameUpdateInfo.UPDATE_TYPE_NONFORCE.equals(result.versionType)) {

            SharedPreferences sharedPreferences = initParams.activity.getApplicationContext().getSharedPreferences(Constant.KEY_DB_NAME,  Context.MODE_PRIVATE);

            if(result.versionTittle.equals(sharedPreferences.getString(Constant.KEY_NEVER_NOTIFY_UPDATE_VERSION,"")) &&
                    sharedPreferences.getBoolean(Constant.KEY_IS_NEVER_NOTIFY_UPDATE,false)){
                Log.d(TAG, "Ignore update.");
                chain.proceed(initParams);
            }else {
                Log.d(TAG,"show dialog: " + initParams.getActivity());
                GameDownloadDialogView gdv = ViewControllerNavigator.getInstance().
                        toGameDownloadDialogView(initParams.getActivity(), result);

                gdv.setListener(new GameDownloadDialogView.DialogClickListener() {
                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel: ");
                        chain.proceed(initParams);
                    }
                });
            }
        } else {

            Log.w(TAG, "Warning: ignored update type : " + result.versionType);
            chain.proceed(initParams);
        }

    }
}

