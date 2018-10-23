package com.yiyou.gamesdk.core.ui.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.FinishFragmentEvent;
import com.mobilegamebar.rsdk.outer.event.IEventListener;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.storage.events.NativeTitleBarTitleContentEvent;
import com.yiyou.gamesdk.core.storage.events.NativeTitleBarTitleContentEvent.NativeTitleBarTitleContentEventParam;
import com.yiyou.gamesdk.core.storage.events.NativeTitleBarUpdateEvent;
import com.yiyou.gamesdk.core.storage.events.NativeTitleBarUpdateEvent.NativeTitleBarUpdateEventParam;
import com.yiyou.gamesdk.core.ui.fragment.PayFragment;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;
import com.yiyou.gamesdk.util.ExtraDef;
import com.yiyou.gamesdk.util.PhoneUtils;

public class CommonTitlePrimaryFragment extends Fragment implements OnClickListener {


    public interface OnTitleClick {
        void onClick(View v);
    }

    public interface OnConfirmClick{
        void onClick(View v);
    }

    public static final String TITLE_BAR_ARG = "title_bar_arg";

    private static final String TAG = "RSDK:CommonTitlePrimary";

    private View rootView;
    private TextView tvTitleContent;
    private ImageView imgClose;
    private TextView confirmBtn;
    private String titleContent = "";
    private static final String TITLE_CONTENT = "title_content";
    private ImageView imgBack;
    private IEventListener onNativeBarUpdateListener, onNativeBarContentListener;

    private OnTitleClick onTitleClick;
    private OnConfirmClick onConfirmClick;

    public void setOnTitleClick(OnTitleClick onTitleClick) {
        this.onTitleClick = onTitleClick;
    }

    public void setOnConfirmClick(OnConfirmClick onConfirmClick) {
        this.onConfirmClick = onConfirmClick;
    }

    public static CommonTitlePrimaryFragment newInstance(String content) {
        return newInstance(content, ExtraDef.DisplayStyle.StyleFullScreen);
    }

    public static CommonTitlePrimaryFragment newInstance(String content, int displayStyle) {
        CommonTitlePrimaryFragment fragment = new CommonTitlePrimaryFragment();
        Bundle args = new Bundle();
        args.putString(TITLE_CONTENT, content);
        args.putInt(ExtraDef.EXTRA_DISPLAY_STYLE, displayStyle);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        NativeTitleBarUpdateInfo config = null;
        if (getArguments() != null) {
            titleContent = getArguments().getString(TITLE_CONTENT);
            config = getArguments().getParcelable(TITLE_BAR_ARG);
        }

        addEvent();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.tt_sdk_common_fragment_title_primary, container, false);
            tvTitleContent = (TextView) rootView.findViewById(R.id.tv_title_content);
            imgClose = (ImageView) rootView.findViewById(R.id.img_close);
            imgBack = (ImageView) rootView.findViewById(R.id.img_back);
            confirmBtn = (TextView) rootView.findViewById(R.id.btn_confirm);
            imgClose.setOnClickListener(this);
            imgBack.setOnClickListener(this);
            confirmBtn.setOnClickListener(this);
            tvTitleContent.setText(titleContent);
            tvTitleContent.setOnClickListener(this);
        }
        updateViewTitleBar(config);
        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
            Log.e("TTViews", "titleBar reuse. remove from parent.");
        }

        return rootView;
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.img_close:
                PhoneUtils.closeInput(getActivity());

                closePayCallback();

                getActivity().finish();

                break;


            case R.id.img_back:
                onclickBack();
                break;

            case R.id.btn_confirm:
                if (onConfirmClick != null) {
                    onConfirmClick.onClick(v);
                }
                break;

//            case R.id.img_refresh:
//                EventDispatcherAgent.defaultAgent().broadcast(RefreshEvent.TYPE, new RefreshEvent.RefreshEventParam());
//                break;

            case R.id.tv_title_content:

                if (onTitleClick != null) {
                    onTitleClick.onClick(v);
                }

                break;


        }

    }


    private void onclickBack() {
        PhoneUtils.closeInput(getActivity());

//        if (getParentFragment() != null && getParentFragment() instanceof WebFragment) {
//
//            ((WebFragment) getParentFragment()).onBackPressed();
//
//        } else {
                EventDispatcherAgent.defaultAgent().broadcast(FinishFragmentEvent.TYPE_FINISH_FRAGMENT,
                        new FinishFragmentEvent.Param(0, getActivity(), getParentFragment()));
//        }
    }

    private void closePayCallback() {
        if (getParentFragment() instanceof PayFragment) {
            ApiFacade.getInstance().closeNotify();
        }
    }

    private void addEvent() {
        onNativeBarUpdateListener = new IEventListener<NativeTitleBarUpdateEvent.NativeTitleBarUpdateEventParam>() {

            @Override
            public void onEvent(String eventType, NativeTitleBarUpdateEventParam params) {
                updateViewTitleBar(params.mNativeTitleBarUpdateInfo);
            }
        };


        EventDispatcherAgent.defaultAgent().addEventListener(CommonTitlePrimaryFragment.this,
                NativeTitleBarUpdateEvent.TYPE_ON_NATIVE_TITLE_BAR_UPDATE,
                onNativeBarUpdateListener);


        onNativeBarContentListener = new IEventListener<NativeTitleBarTitleContentEvent.NativeTitleBarTitleContentEventParam>() {

            @Override
            public void onEvent(String eventType, NativeTitleBarTitleContentEventParam params) {
                updateViewTitleContent(params.mTitleContent);
            }
        };

        EventDispatcherAgent.defaultAgent().addEventListener(CommonTitlePrimaryFragment.this,
                NativeTitleBarTitleContentEvent.TYPE_ON_NATIVE_TITLE_BAR_TITLE_CONTENT,
                onNativeBarContentListener);

    }

    private void updateViewTitleBar(NativeTitleBarUpdateInfo nativeTitleBarUpdateInfo) {

        if (nativeTitleBarUpdateInfo != null) {
            imgBack.setVisibility(nativeTitleBarUpdateInfo.showBackButton ? View.VISIBLE : View.GONE);
//            imgRefresh.setVisibility(nativeTitleBarUpdateInfo.showRefreshButton ? View.VISIBLE : View.GONE);
            imgClose.setVisibility(nativeTitleBarUpdateInfo.showCloseButton?View.VISIBLE:View.GONE);
            confirmBtn.setVisibility(nativeTitleBarUpdateInfo.showConfirmButton?View.VISIBLE:View.GONE);
        } else {
            Log.w(TAG, "error updateViewTitleBar : invalid title bar config.");
        }

    }

    public void updateViewTitleContent(String mTitleContent) {
        if (tvTitleContent != null)
        tvTitleContent.setText(mTitleContent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventDispatcherAgent.defaultAgent()
                .removeEventListenersBySource(this);
    }

}
