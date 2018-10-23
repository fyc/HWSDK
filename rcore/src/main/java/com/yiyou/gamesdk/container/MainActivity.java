package com.yiyou.gamesdk.container;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mobilegamebar.rsdk.outer.consts.OrientationDef;
import com.mobilegamebar.rsdk.outer.event.BackToMainFragmentEvent;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.FinishFragmentEvent;
import com.mobilegamebar.rsdk.outer.event.IEventListener;
import com.mobilegamebar.rsdk.outer.event.StartActivityEvent;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.PluginManager;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.ui.fragment.AccountFragment;
import com.yiyou.gamesdk.core.ui.fragment.BaseFragment;
import com.yiyou.gamesdk.core.ui.fragment.GameFragment;
import com.yiyou.gamesdk.core.ui.fragment.MessageFragment;
import com.yiyou.gamesdk.core.ui.fragment.UnInternalLinkFragment;
import com.yiyou.gamesdk.util.CommonUtils;
import com.yiyou.gamesdk.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 主Activity容器，负责启动并装载Fragment
 * <p>
 * 启动前所有依赖的资源必须加载完毕（由PluginManager负责）
 * <p>
 * Intent参数：<br>
 * fragment:String，Fragment的类名<br>
 *
 * @author Luoweiqiang
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "RSDK: " + "MainActivity";
    public static final String EXTRA_FRAGMENT_NAME = "EXTRA_FRAGMENT_NAME";
    public static final String EXTRA_FRAGMENT_BUNDLE = "EXTRA_FRAGMENT_BUNDLE";
    public static final String EXTRA_DISPLAY_TYPE = "EXTRA_DISPLAY_TYPE";
    public static final int INDEX_DEFAULT_FRAGMENT = 2;
    public static final int INDEX_ACCOUNT_FRAGMENT = 1;
    public static final int INDEX_MESSAGE_FRAGMENT = 3;
    public static final int INDEX_UNINTERNAL_LINK_FRAGMENT = 4;
    public static final String DEFAULT_FRAGMENT = "com.yiyou.gamesdk.core.ui.fragment.GameFragment";
    public static final String ACCOUNT_FRAGMENT = "com.yiyou.gamesdk.core.ui.fragment.AccountFragment";
    public static final String MESSAGE_FRAGMENT = "com.yiyou.gamesdk.core.ui.fragment.MessageFragment";
    public static final String UNINTERNAL_LINK_FRAGMENT = "com.yiyou.gamesdk.core.ui.fragment.UnInternalLinkFragment";


    private String fragmentName;
    private List<BaseFragment> mFragments = new ArrayList<>();
    private List<String> filter_list = new ArrayList<>();

    private Bundle mBundle;
    private String currentFgmName = "";
    private int tabIndex = 0;
    private BaseFragment currentFgm;
    private boolean isDefaultPage;

    private IEventListener finishFragmentListener;
    private IEventListener backToMainFragmentListener;

    private AccountFragment accountFragment;
    private GameFragment gameFragment;
    private MessageFragment messageFragment;
    private UnInternalLinkFragment unInternalLinkFragment;

    private View accountTab;
    private View gameTab;
    private View messageTab;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate" + PluginManager.getInstance().getOrientation());

        filter_list.clear();
        filter_list.add(DEFAULT_FRAGMENT);
        filter_list.add(ACCOUNT_FRAGMENT);
        filter_list.add(MESSAGE_FRAGMENT);
        filter_list.add(UNINTERNAL_LINK_FRAGMENT);



        if (PluginManager.getInstance().getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int displayType = getIntent().getIntExtra(EXTRA_DISPLAY_TYPE, StartActivityEvent.DISPLAY_TYPE_FULLSCREEN);
        if (PluginManager.getInstance().getOrientation() == OrientationDef.PORTRAIT ||
                displayType == StartActivityEvent.DISPLAY_TYPE_FULLSCREEN) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
//        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        handleIntent();
        addEvent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        android.util.Log.d(TAG, "onNewIntent: ");
        setIntent(intent);
        handleIntent();
        super.onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        android.util.Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        android.util.Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        android.util.Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        android.util.Log.d(TAG, "onStop: ");
    }

    private void handleIntent() {
        fragmentName = getIntent().getStringExtra(EXTRA_FRAGMENT_NAME);
        mBundle = getIntent().getBundleExtra(EXTRA_FRAGMENT_BUNDLE);

        if (TextUtils.isEmpty(fragmentName))
            return;

        if (fragmentName.equals(currentFgmName))
            return;

        currentFgmName = fragmentName;
        Log.d(TAG, "framgent name=" + fragmentName);

        try {
            currentFgm = (BaseFragment) getClassLoader().loadClass(fragmentName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        currentFgm.setArguments(mBundle);

        isDefaultPage = filterFragment(fragmentName);
        if (isDefaultPage) {
            setContentView(R.layout.sdk_activity_main);
            initDefaultView();
        } else {
            initNormalView();
        }
        int displayType = getIntent().getIntExtra(EXTRA_DISPLAY_TYPE, 0);
        updateWindowSize(displayType);
    }

    private void initDefaultView() {
        FragmentTransaction ft = getFragmentTransaction();
        accountTab = findViewById(R.id.account_tab);
        gameTab = findViewById(R.id.game_tab);
        messageTab = findViewById(R.id.message_tab);
        findViewById(R.id.open_taozi_container).setOnClickListener(this);
        findViewById(R.id.message_tab_container).setOnClickListener(this);
        findViewById(R.id.account_tab_container).setOnClickListener(this);
        findViewById(R.id.game_tab_container).setOnClickListener(this);
        findViewById(android.R.id.content).setOnClickListener(this);
        View space = findViewById(R.id.space);
        if (space != null) {
            space.setOnClickListener(this);
        }
        if (currentFgmName.equals(DEFAULT_FRAGMENT) && currentFgm instanceof GameFragment) {
            gameFragment = (GameFragment) currentFgm;
            tabIndex = INDEX_DEFAULT_FRAGMENT;
        } else if (currentFgmName.equals(ACCOUNT_FRAGMENT) && currentFgm instanceof AccountFragment) {
            accountFragment = (AccountFragment) currentFgm;
            tabIndex = INDEX_ACCOUNT_FRAGMENT;
        } else if (currentFgmName.equals(MESSAGE_FRAGMENT) && currentFgm instanceof MessageFragment) {
            messageFragment = (MessageFragment) currentFgm;
            tabIndex = INDEX_MESSAGE_FRAGMENT;
        } else if (currentFgmName.equals(UNINTERNAL_LINK_FRAGMENT) && currentFgm instanceof UnInternalLinkFragment) {
            unInternalLinkFragment = (UnInternalLinkFragment)currentFgm;
            tabIndex = INDEX_UNINTERNAL_LINK_FRAGMENT;
        }
        ft.add(R.id.activity_content, currentFgm);
        ft.commitAllowingStateLoss();
        switchTab();
    }

    private void initNormalView() {
        FragmentTransaction ft = getFragmentTransaction();
        ft.add(android.R.id.content, currentFgm);
        mFragments.clear();
        mFragments.add(currentFgm);
        ft.commitAllowingStateLoss();
    }

    private void hideAllFragment() {
        FragmentTransaction ft = getFragmentTransaction();
        if (accountFragment != null) {
            ft.hide(accountFragment);
        }
        if (gameFragment != null) {
            ft.hide(gameFragment);
        }
        if (messageFragment != null) {
            ft.hide(messageFragment);
        }
        if (unInternalLinkFragment != null){
            ft.hide(unInternalLinkFragment);
        }
        for (BaseFragment fragment : mFragments) {
            ft.remove(fragment);
        }
        ft.commitAllowingStateLoss();
    }

    private FragmentTransaction getFragmentTransaction() {
        return getSupportFragmentManager().beginTransaction();

    }

    private void switchTab() {
        hideAllFragment();
        mFragments.clear();
        FragmentTransaction ft = getFragmentTransaction();
        switch (tabIndex) {
            case INDEX_ACCOUNT_FRAGMENT:
                if (accountFragment != null) {
                    ft.show(accountFragment);
                } else {
                    accountFragment = new AccountFragment();
                    ft.add(R.id.activity_content, accountFragment);
                }
                accountTab.setEnabled(true);
                currentFgm = accountFragment;
                currentFgmName = accountFragment.getClass().getName();
                break;
            case INDEX_DEFAULT_FRAGMENT:
                if (gameFragment != null) {
                    ft.show(gameFragment);
                } else {
                    gameFragment = new GameFragment();
                    ft.add(R.id.activity_content, gameFragment);
                }
                gameTab.setEnabled(true);
                currentFgm = gameFragment;
                currentFgmName = gameFragment.getClass().getName();
                break;
            case INDEX_MESSAGE_FRAGMENT:
                if (messageFragment != null) {
                    ft.show(messageFragment);
                } else {
                    messageFragment = new MessageFragment();
                    ft.add(R.id.activity_content, messageFragment);
                }
                messageTab.setEnabled(true);
                currentFgm = messageFragment;
                currentFgmName = messageFragment.getClass().getName();
                break;
            case INDEX_UNINTERNAL_LINK_FRAGMENT:
                if (unInternalLinkFragment != null) {
                    ft.show(unInternalLinkFragment);
                } else {
                    unInternalLinkFragment = new UnInternalLinkFragment();
                    ft.add(R.id.activity_content, unInternalLinkFragment);
                }
                currentFgm = unInternalLinkFragment;
                currentFgmName = unInternalLinkFragment.getClass().getName();
                break;
            default:
                finish();
                break;
        }
        ft.commitAllowingStateLoss();
    }

    private boolean filterFragment(String fragmentName) {
        return filter_list.contains(fragmentName);
    }

    @TargetApi(16)
    private void updateWindowSize(int displayType) {
        View rootContainer = findViewById(android.R.id.content);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#77000000"));
        if (Build.VERSION.SDK_INT >= 16) {
            rootContainer.setBackground(bg);
        } else {
            rootContainer.setBackgroundDrawable(bg);
        }

        if (displayType == StartActivityEvent.DISPLAY_TYPE_DIALOG) {
            if (PluginManager.getInstance().getOrientation() == OrientationDef.LANDSCAPE){
                ViewGroup.LayoutParams params = rootContainer.getLayoutParams();
                int border = 50;
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int leftRightBorder = (int) (screenWidth * 0.125f);
                rootContainer.setLayoutParams(params);
                rootContainer.setPadding(leftRightBorder, border, leftRightBorder, border);
            }else {
                ViewGroup.LayoutParams params = rootContainer.getLayoutParams();
                int border = ViewUtils.dp2px(this,145);
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                int bottomBorder = screenHeight - ViewUtils.dp2px( this, 350 + 145);
                int leftRightBorder = ViewUtils.dp2px(this,20);
                rootContainer.setLayoutParams(params);
                rootContainer.setPadding(leftRightBorder, border, leftRightBorder, bottomBorder);
            }
        }
    }

    @Override
    public AssetManager getAssets() {
        return PluginManager.getInstance().getAssetManager();
    }

    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }

    //暂时屏蔽， 会影响更改主题。 这里不需要更改基类主题实现， 主题可以直接apply实例
//    @Override
//    public Resources.Theme getTheme() {
//        return PluginManager.getInstance().getTheme() ;
//    }

    /*
     * 此处先判断PluginManager里的ClassLoader是否为null，如果不为就返回PluginManager里的ClassLoader否则返回super.getClassLoader()
     * 因为当activity被杀掉再恢复时会在super.onCreate(savedInstanceState);里创建需要恢复的fragment，如果此时使用super.getClassLoader()会找不到对应的fragment。
     */
    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getClassLoader();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: " + mFragments.size());
        if (mFragments.size() > 0) {
            if (mFragments.get(mFragments.size() - 1).onBackPressed()) {
                return;
            }
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mFragments.size() > 0) {
            int size = mFragments.size();
            ft.remove(mFragments.get(size - 1));
            mFragments.remove(size - 1);
            if (mFragments.size() > 0) {
                size = mFragments.size();
                ft.add(R.id.activity_content, mFragments.get(size - 1));
                ft.commitAllowingStateLoss();
            } else {
                ft.commitAllowingStateLoss();
                switchTab();
            }
        } else {
            finish();
        }

    }

    private void setCurrentFragment(int index) {
        accountTab.setEnabled(false);
        gameTab.setEnabled(false);
        messageTab.setEnabled(false);
        tabIndex = index;
        switchTab();
    }


    private void addEvent() {
        finishFragmentListener = new IEventListener<FinishFragmentEvent.Param>() {
            @Override
            public void onEvent(String s, FinishFragmentEvent.Param param) {
//                finishOrBack();
                onBackPressed();
            }
        };
        backToMainFragmentListener = new IEventListener<BackToMainFragmentEvent.Param>() {
            @Override
            public void onEvent(String s, BackToMainFragmentEvent.Param param) {
                Log.d(TAG, "onEvent: ");
//                returnToMain();
                setCurrentFragment(tabIndex);
            }

        };


        EventDispatcherAgent.defaultAgent().addEventListener(this, FinishFragmentEvent.TYPE_FINISH_FRAGMENT, finishFragmentListener);
        EventDispatcherAgent.defaultAgent().addEventListener(this, BackToMainFragmentEvent.TYPE_BACK_TO_MAIN_FRAGMENT, backToMainFragmentListener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.util.Log.d(TAG, "onDestroy: ");
        EventDispatcherAgent.defaultAgent()
                .removeEventListenersBySource(this);
        mFragments.clear();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged" + newConfig.orientation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_tab_container:
                setCurrentFragment(INDEX_ACCOUNT_FRAGMENT);
                break;
            case R.id.game_tab_container:
                setCurrentFragment(INDEX_DEFAULT_FRAGMENT);
                break;
            case R.id.message_tab_container:
                setCurrentFragment(INDEX_MESSAGE_FRAGMENT);
                break;
            case android.R.id.content:
            case R.id.space:
                finish();
                break;
            case R.id.open_taozi_container:
                CommonUtils.openApp(MainActivity.this);
                break;
        }
    }

//    public void showDownloadFragment(){
//        if (unInternalLinkFragment == null){
//            unInternalLinkFragment = new UnInternalLinkFragment();
//            startFragment(unInternalLinkFragment);
//        }else {
//            setCurrentFragment(INDEX_UNINTERNAL_LINK_FRAGMENT);
//        }
//
//    }

    public void startFragment(BaseFragment fragment) {
        startFragment(fragment, false);
    }

    public void startFragment(BaseFragment fragment, boolean flag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideAllFragment();
        ft.add(R.id.activity_content, fragment);
        if (flag) {
            mFragments.remove(mFragments.size() - 1);
        }
        mFragments.add(fragment);
        ft.commitAllowingStateLoss();
        Log.d(TAG, "startFragment: " + mFragments.size());
    }
}
