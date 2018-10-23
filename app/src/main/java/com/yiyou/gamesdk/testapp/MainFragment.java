package com.yiyou.gamesdk.testapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mobilegamebar.rsdk.container.RGameSDK;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.gamesdk.shouyouba.tzsy.R;

public class MainFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TTSDK: "+"MainActivity";

    private int screenWidth;
    private int screenHeigth;
    private WindowManager windowManager;
    private LinearLayout layoutRoot;
    private Button loginButton;
    private Button logoutButton;
    private Button exitButton;
    private Button payButton;
    private Button OrientationButton;
    private View layout;

    public interface MainFragmentBtnClickListener {
        void onMainFragmentChangeOrientationClick();

        void onMainFragmentLogoutClick();

        void onMainFragmentExitClick();

        void onMainFragmentPayClick();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.activity_main, container, false);
        Log.e(TAG, "onCreateView");
        initView();
//        TTGameSDK.defaultSDK().createFloatButton(getActivity());
        return layout;
    }

    private void initView() {

        layoutRoot = (LinearLayout) layout.findViewById(R.id.layout_root);
        windowManager = (WindowManager) getActivity().getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeigth = windowManager.getDefaultDisplay().getHeight();

        if (screenHeigth > screenWidth) {
            layoutRoot.setBackgroundResource(R.drawable.sdk_bg1);
        } else {
            layoutRoot.setBackgroundResource(R.drawable.sdk_bg);
        }

        OrientationButton = (Button) layout.findViewById(R.id.orientation);
        OrientationButton.setOnClickListener(this);

        loginButton = (Button) layout.findViewById(R.id.login);
        loginButton.setOnClickListener(this);

        logoutButton = (Button) layout.findViewById(R.id.logout);
        logoutButton.setVisibility(View.VISIBLE);
        logoutButton.setOnClickListener(this);

        exitButton = (Button) layout.findViewById(R.id.exit);
        exitButton.setOnClickListener(this);

        payButton = (Button) layout.findViewById(R.id.pay);
        payButton.setOnClickListener(this);

        layout.findViewById(R.id.show).setOnClickListener(this);
        layout.findViewById(R.id.hide).setOnClickListener(this);

    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        initView();
//    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!StringUtils.isBlank(TTGameSDK.defaultSDK().getSession())) {
//            TTGameSDK.defaultSDK().createFloatButton(getActivity());
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
//          TTGameSDK.defaultSDK().destroyFloatButton(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        if (getActivity() instanceof MainFragmentBtnClickListener) {
            if (v == logoutButton) {
                ((MainFragmentBtnClickListener) getActivity()).onMainFragmentLogoutClick();
            } else if (v == exitButton) {
                ((MainFragmentBtnClickListener) getActivity()).onMainFragmentExitClick();
            } else if (v == payButton) {
                ((MainFragmentBtnClickListener) getActivity()).onMainFragmentPayClick();
            } else if (v == OrientationButton) {
                ((MainFragmentBtnClickListener) getActivity()).onMainFragmentChangeOrientationClick();
            } else if (v.getId() == R.id.show) {
                RGameSDK.getInstance().showFloatView(getActivity());
            } else{
                RGameSDK.getInstance().hideFloatView(getActivity());
            }
        }


    }
}
