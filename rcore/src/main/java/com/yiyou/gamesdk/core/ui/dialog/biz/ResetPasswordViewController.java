package com.yiyou.gamesdk.core.ui.dialog.biz;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.def.IAuthApi;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.ui.dialog.ViewControllerNavigator;
import com.yiyou.gamesdk.core.ui.widget.StandardDialog;
import com.yiyou.gamesdk.model.AccountHistoryInfo;
import com.mobilegamebar.rsdk.outer.event.IDialogParam;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.mobilegamebar.rsdk.outer.util.StringUtils;
import com.yiyou.gamesdk.util.IMEUtil;
import com.yiyou.gamesdk.util.ToastUtils;
import com.yiyou.gamesdk.util.ViewUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nekomimi on 2017/4/25.
 */

public class ResetPasswordViewController extends BaseAuthViewController {
    private static final String TAG = "RSDK:ResetPasswordViewContro";

    private static final long AUTO_REGISTER_VERIFY_MODE_EXPIRATION = 60 * 60 * 1000; //验证模式持续时间
    private boolean waitingVerifyCode = false;


    private EditText phoneEdit;
    private EditText phonePasswordEdit;
    private Button getVerificationCodeButton;
    private Button backTitleContainerBtn;
    private Button closeTitleContainerBtn;
    private EditText verificationCodeEdit;
    private Button confirmButton;
    private TextView titleTv;
    private View titleImg;

    ReGetVerifyCodeButtonController reGetVerifyCodeButtonController;
    SmsVerificationCode smsObserver;


    //data
    private String requestingPhoneNumber;
    private String requestingPhonePwd;
    Context mContext;

    private int retryTime = 0;

    public ResetPasswordViewController(Context context, IDialogParam params) {
        super(context, params);
        mContext = context;
        initView();
        //注册短信变化监听
        smsObserver = new SmsVerificationCode(new Handler());
        mContext.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
    }

    private void initView(){
        titleTv = (TextView)findViewById(R.id.tv_title_container_title);
        titleImg = findViewById(R.id.img_title_container_title);
        getVerificationCodeButton = (Button) findViewById(R.id.btn_register_container_get_verification_code);
        ViewUtils.setViewEnable(getVerificationCodeButton, false);
        confirmButton = (Button) findViewById(R.id.btn_reset_pwd_confirm);
        ViewUtils.setViewEnable(confirmButton, false);
        phoneEdit = (EditText) findViewById(R.id.edit_register_container_phone);
        phonePasswordEdit = (EditText) findViewById(R.id.edit_register_container_password);
//        reGetVerificationCodeButton = (TextView) findViewById(R.id.btn_register_container_re_get_verification_code);
        reGetVerifyCodeButtonController = new ReGetVerifyCodeButtonController(getVerificationCodeButton);
        verificationCodeEdit = (EditText) findViewById(R.id.edit_register_container_verification_code);
        backTitleContainerBtn = (Button)findViewById(R.id.btn_title_container_back);
        closeTitleContainerBtn = (Button)findViewById(R.id.btn_title_container_close);
        backTitleContainerBtn.setVisibility(VISIBLE);
        titleImg.setVisibility(GONE);
        titleTv.setVisibility(VISIBLE);
        titleTv.setText(R.string.title_forget_password);
        closeTitleContainerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                warningLeaveRegister(new TwoChoiceJob() {
                    @Override
                    public void jobOnChoose() {
                        waitingVerifyCode = false;
                        close();
                    }

                    @Override
                    public void jobDefault() {
                        close();
                    }
                });
            }
        });
        backTitleContainerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IMEUtil.hideIME(ResetPasswordViewController.this);
                warningLeaveRegister(new TwoChoiceJob() {
                    @Override
                    public void jobOnChoose() {
                        waitingVerifyCode = false;
                        ViewControllerNavigator.getInstance().toLogin( getDialogParam());
                    }

                    @Override
                    public void jobDefault() {
                        ViewControllerNavigator.getInstance().toLogin(getDialogParam());
                    }
                });
            }
        });
        getVerificationCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phoneEdit.getText().toString();
                String pwd = phonePasswordEdit.getText().toString();
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
                IMEUtil.hideIME(ResetPasswordViewController.this);
                //获取验证码
                getVerificationCodeButtonImpl(phoneEdit.getText().toString());
            }
        });
        phoneEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ViewUtils.setViewEnable(getVerificationCodeButton, phoneEdit.length() != 0);
            }
        });

        confirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IMEUtil.hideIME(ResetPasswordViewController.this);
                if (checkInput()){
                    resetPassword(phoneEdit.getText().toString(), phonePasswordEdit.getText().toString(),verificationCodeEdit.getText().toString());
                }
            }
        });
        ViewUtils.bindEditWithButton(phonePasswordEdit, confirmButton);
        addTextWatcher(phoneEdit,phonePasswordEdit,verificationCodeEdit);
    }

    private boolean checkInput(){
        int msg = 0;
        if (phoneEdit.getText().toString().length() != 11){
            msg = R.string.please_input_valid_number;
        }else if (phonePasswordEdit.length()< 6 || phonePasswordEdit.length() > 16){
            msg = R.string.reset_password_new_password_tip;
        }
        if(msg != 0){
            ToastUtils.showMsg(msg);
            return false;
        }else {
            return true;
        }
    }

    private void putRequestInput(String phone, String password){
        requestingPhoneNumber = phone;
        requestingPhonePwd = password;
    }

    private void clearRequestInput(){
        requestingPhoneNumber = "";
        requestingPhonePwd = "";
    }
    public void resetPassword(String phone, String password, String vCode){
        putRequestInput(phone, password);
        showLoading();
        ApiFacade.getInstance().forgetPassword(requestingPhoneNumber, requestingPhonePwd,vCode,new TtRespListener(){
            @Override
            public void onNetSucc(String url, Map params, Object result) {
                super.onNetSucc(url, params, result);
                ToastUtils.showMsg(R.string.resetpasswd_succ);
                AccountHistoryInfo info = ApiFacade.getInstance().getHistoryAccountByPhone(requestingPhoneNumber);
                info.password = (String)params.get("newpwd");
                ApiFacade.getInstance().insertOrUpdateAccountHistory(info);
                waitingVerifyCode = false;
                clearRequestInput();
                ViewControllerNavigator.getInstance().toLogin( getDialogParam());
            }

            @Override
            public void onNetError(String url, Map params, String errno, String errmsg) {
                clearRequestInput();
                ToastUtils.showMsg(errmsg);
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                Log.e(TAG,"errorNo: " +errorNo+" errmsg: "+errmsg);
                clearRequestInput();
            }

            @Override
            public void onNetworkComplete() {
                super.onNetworkComplete();
                hideLoading();
            }
        });

    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.tt_sdk_container_reset_pwd;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {
//        reGetVerifyCodeButtonController.cancelCountDown();
    }

    @Override
    public void close() {
        super.close();
        //注销短信监听器
        mContext.getContentResolver().unregisterContentObserver(smsObserver);
    }

    @Override
    public boolean onBackPressed() {
        boolean blockBackAction = waitingVerifyCode;
        if (waitingVerifyCode) {
            IMEUtil.hideIME(ResetPasswordViewController.this);
            warningExitRegister(new TwoChoiceJob() {
                @Override
                public void jobOnChoose() {
                    waitingVerifyCode = false;
                    close();
                }

                @Override
                public void jobDefault() {

                }
            });
        }
        return blockBackAction;
    }

    private interface TwoChoiceJob {
        void jobOnChoose();

        void jobDefault();
    }

    private void warningLeaveRegister(final TwoChoiceJob job) {
        if (waitingVerifyCode) {
            StandardDialog dialog
                    = new StandardDialog(getDialogParam().getActivityContext());
            dialog.setMessageTip(R.string.warning_back_to_register);
            dialog.setEnsureText(R.string.restart);
            dialog.setCancelText(R.string.keep_waiting);
            dialog.setListener(new StandardDialog.DialogClickListener() {
                @Override
                public void onEnsureClick() {
                    verificationCodeEdit.setText("");
                    reGetVerifyCodeButtonController.cancelCountDown();
                    job.jobOnChoose();
                }
            });
            dialog.show();
        } else {
            job.jobDefault();
        }
    }

    private void warningExitRegister(final TwoChoiceJob job) {
        if (waitingVerifyCode) {
            StandardDialog dialog
                    = new StandardDialog(getDialogParam().getActivityContext());
            dialog.setMessageTip(R.string.warning_back_to_register);
            dialog.setEnsureText(R.string.yes_i_wanna_exit);
            dialog.setCancelText(R.string.keep_waiting);
            dialog.setListener(new StandardDialog.DialogClickListener() {
                @Override
                public void onEnsureClick() {
                    verificationCodeEdit.setText("");
                    reGetVerifyCodeButtonController.cancelCountDown();
                    job.jobOnChoose();
                }
            });
            dialog.show();
        } else {
            job.jobDefault();
        }
    }

    private void updateConfirmButtonState(){
            if (phoneEdit.length() == 0 || phonePasswordEdit.length() == 0
                    || verificationCodeEdit.length() == 0){
                confirmButton.setEnabled(false);
            }else {
                confirmButton.setEnabled(true);
            }

    }

    private void addTextWatcher(EditText... editTexts ){
        for (final EditText editText : editTexts){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    updateConfirmButtonState();
                }
            });
        }
    }

    /**
     * 获取短信验证码
     *
     * @param phone 手机号码
     */
    private void getVerificationCodeButtonImpl(String phone) {
        showLoading();
        ApiFacade.getInstance().requestVerificationCode(phone, IAuthApi.VCODE_TYPE_RESET_PASSWORD, retryTime, new TtRespListener<Void>() {
            @Override
            public void onNetSucc(String url, Map<String, String> params, Void result) {
                hideLoading();
                retryTime++;
                if (params != null) {
                    waitingVerifyCode = true;
                    Log.d(TAG, "success request verify code ");
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


    private class ReGetVerifyCodeButtonController {
        TextView textView;

        public ReGetVerifyCodeButtonController(TextView textView) {
            this.textView = textView;
        }

        public void prepare() {
            ViewUtils.setViewEnable(textView, false);
        }

        private CountDownTimer timer;

        public void startCountDown() {
            if (timer != null) {
                return;
            }
            timer = new CountDownTimer(60 * 1000l, 1000l) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (textView.getVisibility() == VISIBLE){
                        int sec = (int) (millisUntilFinished / 1000l);
                        textView.setText(ResourceHelper.getString(R.string.re_get_verification_fmt, sec));
                    }
                }

                @Override
                public void onFinish() {
                    timer = null;
                    textView.setText(ResourceHelper.getString(R.string.re_get_verification));
                    ViewUtils.setViewEnable(textView, true);
                }
            };
            timer.start();
        }

        public void cancelCountDown() {
            if (timer != null) {
                timer.cancel();
                timer = null;
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(ResourceHelper.getString(R.string.re_get_verification));
                        ViewUtils.setViewEnable(textView, true);
                    }
                });
            }
        }

    }


    public class SmsVerificationCode extends ContentObserver {

        private Uri SMS_ALL = Uri.parse("content://sms/");
        private Cursor cursor = null;

        public SmsVerificationCode(Handler handler) {
            super(handler);
        }


        public void getSmsFromPhone() {
            ContentResolver cr = mContext.getContentResolver();
            String[] projection = new String[]{"body"};
            // 读取短信内容
            cursor = cr.query(SMS_ALL, projection, null, null, null);
            if (null == cursor) {
                return;
            }
            if (cursor.moveToNext()) {
                String body = cursor.getString(cursor.getColumnIndex("body")); //获取短信的内容
                //根据正则分组表达式获取验证码
                Pattern pattern = Pattern.compile("(【TT语音】|【趣丸网】)(\\s*验证码\\s*)([0-9]{4})");
                Matcher matcher = pattern.matcher(body);
                if (matcher.find()) {
                    verificationCodeEdit.setText(matcher.group(3));
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getSmsFromPhone();
        }
    }
}