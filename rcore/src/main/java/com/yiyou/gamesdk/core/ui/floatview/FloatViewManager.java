package com.yiyou.gamesdk.core.ui.floatview;

import android.os.Handler;
import android.os.Looper;

import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.yiyou.gamesdk.core.api.ApiFacade;

public class FloatViewManager {

    private static FloatViewManager instance;
    private FloatViewController floatViewController;




    private FloatViewManager() {
        floatViewController = new FloatViewController(ResourceHelper.getContextHolder().get());
    }

    public static synchronized FloatViewManager getInstance() {
        if (instance == null) {
            instance = new FloatViewManager();
        }
        return instance;
    }


    public synchronized void destory() {
//        floatViewController = null;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (floatViewController!=null) {
                    floatViewController.hide();
                    floatViewController = null;
                    instance =null;
                }
            }
        });
    }


    public synchronized void show() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (floatViewController != null && ApiFacade.getInstance().getMainUid() != 0) {
                    floatViewController.show();
                }
            }
        });
    }

    public synchronized void hide() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (floatViewController!=null) {
                    floatViewController.hide();
                }
            }
        });
    }


}
