package com.yiyou.gamesdk.core.ui.dialog.biz;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.event.IDialogParam;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.mobilegamebar.rsdk.outer.util.StringUtils;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.def.IAuthApi;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.util.IMEUtil;
import com.yiyou.gamesdk.util.ToastUtils;
import com.yiyou.gamesdk.util.ViewUtils;

import java.util.Map;

public class BindPhoneViewController extends BaseAuthViewController {
    private static final String TAG = "RSDK:BindPhoneViewController ";
    Context mContext;
    private TextView titleTv;
    private TextView closeTitleContainerBtn;
    private EditText accountEdit; //输入号码框
    private EditText verificationCodeEdit; //验证码
    private Button getVerificationCodeButton; //获取验证码
    private Button bindButton; //绑定按钮
    ReGetVerifyCodeButtonController reGetVerifyCodeButtonController;
    private int retryTime = 0;
    private boolean waitingVerifyCode = false;
    public BindPhoneViewController(Context context, IDialogParam params) {
        super(context, params);
        mContext = context;
        initView();
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.tt_sdk_container_bind_phone;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }
    private void initView() {
        titleTv = (TextView) findViewById(R.id.tv_title_container_title);
        titleTv.setText(R.string.str_bind_phone_title);
        closeTitleContainerBtn = (TextView) findViewById(R.id.btn_title_container_close);
        accountEdit = (EditText) findViewById(R.id.edit_bind_phone_container_account);
        verificationCodeEdit = (EditText) findViewById(R.id.edit_bind_phone_container_verification_code);
        getVerificationCodeButton = (Button) findViewById(R.id.btn_bind_phone_container_get_verification_code);
        ViewUtils.setViewEnable(getVerificationCodeButton, false);
        reGetVerifyCodeButtonController = new ReGetVerifyCodeButtonController(getVerificationCodeButton);
        bindButton = (Button) findViewById(R.id.btn_bind_phone_container_bind);
        ViewUtils.setViewEnable(bindButton, false);

        closeTitleContainerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        accountEdit.addTextChangedListener(new TextWatcher() {
            boolean blankHit = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = accountEdit.getText().toString();
                if (!blankHit) {
                    if (StringUtils.isBlank(str)) {
                        blankHit = true;
                    }
                } else {
                    blankHit = false;
                }
                ViewUtils.setViewEnable(bindButton, accountEdit.length() != 0);
                ViewUtils.setViewEnable(getVerificationCodeButton, accountEdit.length() != 0);
            }
        });
        addTextWatcher(accountEdit,verificationCodeEdit);

        getVerificationCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = accountEdit.getText().toString();
                if (StringUtils.isBlank(phone)) {
                    ToastUtils.showMsg(ResourceHelper.getString(R.string.phone_blank));
                    return;
                }
                if (phone.length() != 11) {
                    ToastUtils.showMsg(ResourceHelper.getString(R.string.please_input_11_phone_number));
                    return;
                }
                if (!phone.startsWith("1") && !phone.startsWith("9")) {
                    ToastUtils.showMsg(ResourceHelper.getString(R.string.please_input_valid_number));
                    return;
                }
                IMEUtil.hideIME(BindPhoneViewController.this);
                //获取验证码
                getVerificationCodeButtonImpl(phone);
            }
        });
    }

    private void addTextWatcher(EditText... editTexts) {
        for (final EditText editText : editTexts) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    updateButtonState();
                }
            });
        }
    }

    private void updateButtonState() {
        if (accountEdit.length() == 0 || verificationCodeEdit.length() == 0) {
            ViewUtils.setViewEnable(bindButton, false);
        } else {
            ViewUtils.setViewEnable(bindButton, true);
        }
    }

    /**
     * 获取短信验证码
     *
     * @param phone 手机号码
     */
    private void getVerificationCodeButtonImpl(String phone) {
        showLoading();
        ApiFacade.getInstance().requestVerificationCode(phone, IAuthApi.VCODE_TYPE_REGISTER, retryTime, new TtRespListener<Void>() {
            @Override
            public void onNetSucc(String url, Map<String, String> params, Void result) {
                hideLoading();
                retryTime++;
                if (params != null) {
                    waitingVerifyCode = true;
                    Log.d(TAG, "success request verify code ");
                    ToastUtils.showMsg(R.string.already_sent_verification_tips);
                    reGetVerifyCodeButtonController.prepare();
                    reGetVerifyCodeButtonController.startCountDown();
                } else {
                    Log.d(TAG, "error request verify code.");
                }
            }

            @Override
            public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                super.onNetError(url, params, errno, errmsg);
                hideLoading();
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
                hideLoading();
            }
        });
    }
}
