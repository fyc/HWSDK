package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.core.ui.dialog.biz.LoadingDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.CommDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.GiftGetSuccDialogView;
import com.yiyou.gamesdk.model.GamePackages;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;
import com.yiyou.gamesdk.util.CommonUtils;
import com.yiyou.gamesdk.util.TimeUtils;

import java.util.Map;

/**
 * Created by BM on 2017/11/16.
 * <p>
 * desc:
 */

public class GiftDetailFragment extends BaseFragment {

    public static final String PACKAGE_ID = "packageId";

    private TextView mName;
    private TextView mCount;
    private TextView mDueTime;
    private TextView mDesc;
    private TextView mUsage;
    private TextView mUseRange;
    private Button mGet;
    private String mPackageId;

    public static GiftDetailFragment newInstance(String packageId) {
        GiftDetailFragment fragment = new GiftDetailFragment();
        Bundle args = new Bundle();
        args.putString(PACKAGE_ID, packageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mPackageId = arguments.getString(PACKAGE_ID);
        if (mPackageId == null || mPackageId.isEmpty()) {
            throw new IllegalArgumentException("packageId null");
        }
    }

    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        View rootView = LayoutInflater.from(content).inflate(R.layout.sdk_gift_detail, container, false);
        mName = (TextView) rootView.findViewById(R.id.name);
        mCount = (TextView) rootView.findViewById(R.id.count);
        mDueTime = (TextView) rootView.findViewById(R.id.due_time);
        mDesc = (TextView) rootView.findViewById(R.id.desc);
        mUsage = (TextView) rootView.findViewById(R.id.usage);
        mGet = (Button) rootView.findViewById(R.id.get);
        mUseRange = (TextView)rootView.findViewById(R.id.use_range);
        container.addView(rootView);

        requestData();
    }

    private void requestData() {
        ApiFacade.getInstance().getGamePackageDetail(mPackageId, new TtRespListener<GamePackages.GamePackageInfo>() {
            @Override
            public void onNetSucc(String url, Map<String, String> params, GamePackages.GamePackageInfo result) {
                super.onNetSucc(url, params, result);
                if (isAdded()) {
                    updateData(result);
                }
            }
        });
    }

    private void updateData(final GamePackages.GamePackageInfo info) {
        if (info == null) {
            return;
        }
//        updateGiftCenterData(info);
        mName.setText(info.getPackageName());
        mCount.setText(mName.getContext().getString(R.string.game_package_num, info.getPackageRemain(), info.getPackageTotal()));
        mDueTime.setText(mName.getContext().getString(R.string.game_package_expire_time, TimeUtils.formatTimeYMD(Long.parseLong(info.getExpTime()))));
        mDesc.setText(info.getPackageContent());
        mUsage.setText(info.getPackageMetho());
        mUseRange.setText(info.getPackageRanges());
        mGet.setVisibility(View.VISIBLE);
        if (info.getRedeemCode() == null || info.getRedeemCode().isEmpty()) {
            if (info.getPackageType() == 1 && info.getUserReceiveStatus() == 0) {  //如果是充值礼包，并且用户是不可领取状态
                mGet.setEnabled(false);
                mGet.setText(ResourceHelper.getString(R.string.game_package_get_limit_hint, CommonUtils.pennyToYuan(info.getPackageAmounts())));
                mGet.setOnClickListener(null);
                return;
            } else {
//                mGet.setSelected(false);
                mGet.setText("领取礼包");
                mGet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //未领取
                        final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                        loadingDialog.show();
                        ApiFacade.getInstance().receiveGamePackage(mPackageId + "", new TtRespListener<GamePackages.GamePackageInfo>() {
                            @Override
                            public void onNetSucc(String url, Map<String, String> params, GamePackages.GamePackageInfo result) {
                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }
                                mCount.setText(mName.getContext().getString(R.string.game_package_num, info.getPackageRemain() - 1, info.getPackageTotal()));
                                mGet.setText(mName.getContext().getString(R.string.game_package_copy, result.getRedeemCode()));
                                mGet.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CommonUtils.setClipboardText(getActivity(), info.getRedeemCode());
                                    }
                                });
                                CommDialog.Builder builder = new CommDialog.Builder(getActivity());
                                GiftGetSuccDialogView dialogView = new GiftGetSuccDialogView(getActivity());
                                builder.setView(dialogView);
                                dialogView.setPackageCode(result.getRedeemCode());
                                builder.create().show();

//                                updateGiftCenterData(info);
                            }

                            @Override
                            public void onFail(int errorNo, String errmsg) {
                                super.onFail(errorNo, errmsg);
                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }
                            }

                            @Override
                            public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                                super.onNetError(url, params, errno, errmsg);
                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }
                            }
                        });
                    }
                });
            }
        } else {
            mGet.setText(mName.getContext().getString(R.string.game_package_copy, info.getRedeemCode()));
            mGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtils.setClipboardText(getActivity(), info.getRedeemCode());
                }
            });
        }
    }

//    private void updateGiftCenterData(GamePackages.GamePackageInfo info) {
//        Fragment parent = getParentFragment();
//        if (parent instanceof GameFragment) {
//            ((GameFragment) parent).updateGiftGetBtn(info.getPackageId(),
//                    info.getPackageRemain() - 1, info.getPackageTotal());
//        }
//    }

    @Override
    protected Fragment getTitleBarFragment() {
        return CommonTitlePrimaryFragment.newInstance("礼包详情");

    }

    @Override
    public NativeTitleBarUpdateInfo getTitleBarConfig() {
        NativeTitleBarUpdateInfo info = new NativeTitleBarUpdateInfo();
        info.showBackButton = true;
        info.showCloseButton = false;
//        info.showRefreshButton = false;
        info.showConfirmButton = false;
        return info;
    }
}
