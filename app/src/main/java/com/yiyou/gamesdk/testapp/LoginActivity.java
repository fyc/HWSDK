package com.yiyou.gamesdk.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gamesdk.shouyouba.tzsy.R;
import com.mobilegamebar.rsdk.container.RGameSDK;
import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.consts.OrientationDef;
import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.mobilegamebar.rsdk.outer.model.GameParamInfo;
import com.mobilegamebar.rsdk.outer.util.Log;


public class LoginActivity extends FragmentActivity implements MainFragment.MainFragmentBtnClickListener, View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private MainFragment mainFragment = new MainFragment();
    private PayFragment payFragment = new PayFragment();
    private WindowManager windowManager;
    private LinearLayout layoutRoot;
    private int screenWidth;
    private int screenHeigth;
    private boolean isLandscape = true;
    private int mCurrentView = 0; //0:LoginActivity,1:MainFragment ,2:PayFragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate" + android.os.Process.myPid());
        findViewById(R.id.choose).setOnClickListener(this);
        findViewById(R.id.file).setOnClickListener(this);
        initSdk();
    }


    private void initSdk() {

        GameParamInfo paramInfo = new GameParamInfo();
        paramInfo.setGameId(10000);
//        正式环境
//        paramInfo.setSdkKey("c9f3532184ecd7e2ddb7ac9bcac35c7c");
        //测试环境
//        paramInfo.setSdkKey("c32538c00d360d505eca2290eafeac7f");
        paramInfo.setGameId(10001);
        paramInfo.setSdkKey("ba004a68f0dc487c623191c5b17af8fa");

        mCurrentView = 0;  //0:LoginActivity,1:MainFragment ,2:PayFragmen

        RGameSDK.getInstance().init(this, paramInfo, true, this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ?
                Configuration.ORIENTATION_LANDSCAPE : Configuration.ORIENTATION_PORTRAIT, new IOperateCallback<String>() {
            @Override
            public void onResult(int i, String s) {
                if (i == 0) {
                    RGameSDK.getInstance().setLogoutListener(new IOperateCallback<String>() {
                        @Override
                        public void onResult(int code, String msg) {
                            if (code == TTCodeDef.LOGOUT_NO_INIT || code == TTCodeDef.LOGOUT_FAIL) {
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG)
                                        .show();
                            } else if (code == TTCodeDef.LOGOUT_NO_LOGIN || code == TTCodeDef.SUCCESS) {
                                RGameSDK.getInstance().hideFloatView(LoginActivity.this);
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.remove(mainFragment);
                                ft.commitAllowingStateLoss();
                                findViewById(R.id.choose).setVisibility(View.VISIBLE);
                                findViewById(R.id.file).setVisibility(View.VISIBLE);
                                loginImpl();
                                mCurrentView = 0; //0:LoginActivity,1:MainFragment ,2:PayFragment
                            }
                        }
                    });
                    loginImpl();
                }
            }
        });
    }


    private void loginImpl() {
        GameParamInfo paramInfo = new GameParamInfo();
        paramInfo.setGameId(10000);
//        正式环境
//        paramInfo.setSdkKey("c9f3532184ecd7e2ddb7ac9bcac35c7c");
        //测试环境
        paramInfo.setSdkKey("c32538c00d360d505eca2290eafeac7f");
        int orientation = this.getResources().getConfiguration().orientation;
        Log.d(TAG, "当前orientation = " + orientation);
        RGameSDK.getInstance().init(this, paramInfo, true, orientation == Configuration.ORIENTATION_LANDSCAPE ?
                Configuration.ORIENTATION_LANDSCAPE : Configuration.ORIENTATION_PORTRAIT, new IOperateCallback<String>() {
            @Override
            public void onResult(int i, String s) {
                if (i == 0) {
                    RGameSDK.getInstance().setLogoutListener(new IOperateCallback<String>() {
                        @Override
                        public void onResult(int code, String msg) {
                            if (code == TTCodeDef.LOGOUT_NO_INIT || code == TTCodeDef.LOGOUT_FAIL) {
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG)
                                        .show();
                            } else if (code == TTCodeDef.LOGOUT_NO_LOGIN || code == TTCodeDef.SUCCESS) {
                                RGameSDK.getInstance().hideFloatView(LoginActivity.this);
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.remove(mainFragment);
                                ft.commitAllowingStateLoss();
                                findViewById(R.id.choose).setVisibility(View.VISIBLE);
                                findViewById(R.id.file).setVisibility(View.VISIBLE);
                                loginImpl();
                                mCurrentView = 0; //0:LoginActivity,1:MainFragment ,2:PayFragment
                            }
                        }
                    });
                }
            }
        });
        RGameSDK.getInstance().login(this, new IOperateCallback<String>() {
            @Override
            public void onResult(int code, String s) {
                if (code == TTCodeDef.SUCCESS) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.container, mainFragment);
                    ft.commitAllowingStateLoss();
                    findViewById(R.id.choose).setVisibility(View.GONE);
                    findViewById(R.id.file).setVisibility(View.GONE);
                    mCurrentView = 1;  //0:LoginActivity,1:MainFragment ,2:PayFragment
                    RGameSDK.getInstance().showFloatView(LoginActivity.this);
                    // TODO: cp可以在这里检验login
                    Log.d(TAG, "session: " + RGameSDK.getInstance().getSession());
                    Toast.makeText(LoginActivity.this, "session: " + RGameSDK.getInstance().getSession() + "\n" + "uid: " + RGameSDK.getInstance().getUid(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void exitImpl() {
        RGameSDK.getInstance().uninit(LoginActivity.this, new IOperateCallback<String>() {
            @Override
            public void onResult(int i, String s) {
                if (i == TTCodeDef.SUCCESS) {
                    System.exit(0);
                }
            }
        });
    }

    private void logoutImpl() {
        RGameSDK.getInstance().logout();
        mCurrentView = 0;  //0:LoginActivity,1:MainFragment ,2:PayFragment
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        Log.d(TAG, "onConfigurationChanged");

        View layout = View.inflate(this, R.layout.activity_main, null);
        layoutRoot = (LinearLayout) layout.findViewById(R.id.layout_root);
        windowManager = (WindowManager) LoginActivity.this.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeigth = windowManager.getDefaultDisplay().getHeight();

        if (screenHeigth > screenWidth) {
            isLandscape = false;
            layoutRoot.setBackgroundResource(R.drawable.sdk_bg1);
        } else {
            isLandscape = true;
            layoutRoot.setBackgroundResource(R.drawable.sdk_bg);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.attach(mainFragment);
        ft.commitAllowingStateLoss();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onMainFragmentLogoutClick() {
        logoutImpl();
    }

    @Override
    public void onMainFragmentExitClick() {
        exitImpl();
    }

    @Override
    public void onMainFragmentPayClick() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, payFragment);
        ft.commitAllowingStateLoss();
        mCurrentView = 2;  //0:LoginActivity,1:MainFragment ,2:PayFragment
    }

    @Override
    public void onMainFragmentChangeOrientationClick() {
        int orientation = OrientationDef.LANDSCAPE;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.detach(mainFragment);
        ft.commitAllowingStateLoss();
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            orientation = OrientationDef.PORTRAIT;
        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
//        GameSDK.defaultSDK().setOrientation(orientation);

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");

        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        RGameSDK.getInstance().hideFloatView(LoginActivity.this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        RGameSDK.getInstance().showFloatView(LoginActivity.this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.choose) {

            loginImpl();

        } else if (v.getId() == R.id.file) {
            startActivity(new Intent(this, FileBrowserActivity.class));
        }


    }


    @Override
    public void onBackPressed() {
        //mCurrentView : 0:LoginActivity,1:MainFragment ,2:PayFragment
        if (mCurrentView == 2) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, mainFragment);
            ft.commitAllowingStateLoss();
            mCurrentView = 1;
        } else {
            exitImpl();
        }
    }
}
