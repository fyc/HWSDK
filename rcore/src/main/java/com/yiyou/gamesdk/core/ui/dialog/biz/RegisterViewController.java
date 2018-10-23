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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.IDialogParam;
import com.mobilegamebar.rsdk.outer.event.StartActivityEvent;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.mobilegamebar.rsdk.outer.util.StringUtils;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.def.IAuthApi;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.consts.StatusCodeDef;
import com.yiyou.gamesdk.core.ui.dialog.ViewControllerNavigator;
import com.yiyou.gamesdk.core.ui.fragment.LicenseFragment;
import com.yiyou.gamesdk.core.ui.widget.StandardDialog;
import com.yiyou.gamesdk.model.AuthModel;
import com.yiyou.gamesdk.model.UserInfo;
import com.yiyou.gamesdk.util.IMEUtil;
import com.yiyou.gamesdk.util.ToastUtils;
import com.yiyou.gamesdk.util.ViewUtils;

import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Orange on 15/6/11.
 * 注册，手机号注册，一键注册
 */
public class RegisterViewController extends BaseAuthViewController {
    private static final String TAG = "RSDK:RegisterViewController ";

    private static final int STATE_REGISTER_PHONE = 1;
    private static final int STATE_REGISTER_ACCOUNT = 2;

    private boolean waitingVerifyCode = false;

    private int currentState;
    private int retryTime = 0;

    //header
    private ViewGroup registerTypeRadioGroup;
    private RadioButton registerPhoneRadioButton;
    private RadioButton registerAccountRadioButton;

    private View registerPhoneContainer;
    private View registerAccountContainer;
    private EditText phoneEdit;
    private EditText phonePasswordEdit;
    private EditText accountEdit;
    private EditText accountPasswordEdit;
    private Button getVerificationCodeButton;
    private Button backTitleContainerBtn;
    private Button closeTitleContainerBtn;
    private EditText verificationCodeEdit;
    private Button registerButton;
    private TextView serviceTermsBtn;
    private TextView titleTv;
    private View titleImg;
    ReGetVerifyCodeButtonController reGetVerifyCodeButtonController;
    SmsVerificationCode smsObserver;


    //data
    private String requestingPhoneNumber;
    private String requestingPhoneMd5Pwd;
    private String requestingAccount;
    private String requestingAccountMd5Pwd;
    private Stack<UserInfo> registerUserCache = new Stack<>();

    Context mContext;

    public RegisterViewController(Context context, IDialogParam params) {
        super(context, params);
        mContext = context;
        initView();
        switchRegisterTypeState(STATE_REGISTER_PHONE);
        //注册短信变化监听
        smsObserver = new SmsVerificationCode(new Handler());
        mContext.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.tt_sdk_container_register;
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

    private void initView() {
        titleTv = (TextView)findViewById(R.id.tv_title_container_title);
        titleImg = findViewById(R.id.img_title_container_title);
        serviceTermsBtn = (TextView)findViewById(R.id.tv_service_terms);
        registerTypeRadioGroup = (ViewGroup) findViewById(R.id.radio_group_item_register_type);
        registerPhoneRadioButton = (RadioButton) findViewById(R.id.radio_item_register_phone);
        registerPhoneRadioButton.setSelected(true);
        registerAccountRadioButton = (RadioButton) findViewById(R.id.radio_item_register_account);
        registerPhoneContainer = findViewById(R.id.container_item_register_phone);
        registerAccountContainer = findViewById(R.id.container_item_register_account);
        getVerificationCodeButton = (Button) findViewById(R.id.btn_register_container_get_verification_code);
        ViewUtils.setViewEnable(getVerificationCodeButton, false);
        registerButton = (Button)findViewById(R.id.btn_register_container_register);
        ViewUtils.setViewEnable(registerButton, false);
        phoneEdit = (EditText) findViewById(R.id.edit_register_container_phone);
        phonePasswordEdit = (EditText) findViewById(R.id.edit_register_container_password);
        accountEdit = (EditText)findViewById(R.id.edit_register_container_account);
        accountPasswordEdit = (EditText)findViewById(R.id.edit_register_container_account_password);
        reGetVerifyCodeButtonController = new ReGetVerifyCodeButtonController(getVerificationCodeButton);
        verificationCodeEdit = (EditText) findViewById(R.id.edit_register_container_verification_code);
        backTitleContainerBtn = (Button)findViewById(R.id.btn_title_container_back);
        closeTitleContainerBtn = (Button)findViewById(R.id.btn_title_container_close);
        backTitleContainerBtn.setVisibility(VISIBLE);
        titleTv.setVisibility(VISIBLE);
        titleTv.setText(R.string.register);
        titleImg.setVisibility(GONE);
        serviceTermsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
                        new StartActivityEvent.FragmentParam(0, mContext,
                                StartActivityEvent.DISPLAY_TYPE_FULLSCREEN,
                                null, LicenseFragment.class.getName(), null));
            }
        });
        closeTitleContainerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                warningExitRegister(new TwoChoiceJob() {
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
                IMEUtil.hideIME(RegisterViewController.this);
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

        registerAccountRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!registerAccountRadioButton.isSelected()) {
                    switchRegisterTypeState(STATE_REGISTER_ACCOUNT);
                }
            }
        });

        registerPhoneRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!registerPhoneRadioButton.isSelected()) {
                    registerPhoneRadioButton.setSelected(true);
                    switchRegisterTypeState(STATE_REGISTER_PHONE);
                }
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
                IMEUtil.hideIME(RegisterViewController.this);
                //获取验证码
                getVerificationCodeButtonImpl(phone);
            }
        });

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                IMEUtil.hideIME(RegisterViewController.this);
                if (currentState == STATE_REGISTER_PHONE){
                    if (checkPhoneInput(phoneEdit.getText().toString(), phonePasswordEdit.getText().toString())){
                        registerByPhoneImpl(requestingPhoneNumber, requestingPhoneMd5Pwd,verificationCodeEdit.getText().toString());
                    }
                }else if (currentState == STATE_REGISTER_ACCOUNT){
                    if (checkAccountInput(accountEdit.getText().toString(),accountPasswordEdit.getText().toString()))
                    registerByAccountImpl(requestingAccount,requestingAccountMd5Pwd);
                }
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
        ViewUtils.bindEditWithButton(accountPasswordEdit, registerButton);
        ViewUtils.bindEditWithButton(phonePasswordEdit, registerButton);

        addTextWatcher(phoneEdit,phonePasswordEdit,accountEdit,accountPasswordEdit,verificationCodeEdit);
    }

    private void updateRegisterButtonState(){
        if (currentState == STATE_REGISTER_PHONE){
            if (phoneEdit.length() == 0 || phonePasswordEdit.length() == 0
                    || verificationCodeEdit.length() == 0){
               registerButton.setEnabled(false);
            }else {
                registerButton.setEnabled(true);
            }
        }else if (currentState == STATE_REGISTER_ACCOUNT){
            if (accountEdit.length() == 0 || accountPasswordEdit.length() == 0){
                registerButton.setEnabled(false);
            }else {
                registerButton.setEnabled(true);
            }
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
                    updateRegisterButtonState();
                }
            });
        }
    }
    private boolean checkPhoneInput(String phone, String password){
        if (phone.length() != 11){
            ToastUtils.showMsg(ResourceHelper.getString(R.string.please_input_valid_11_phone_num));
            return false;
        }
        if (password.length()<6 || password.length() > 16){
            ToastUtils.showMsg(ResourceHelper.getString(R.string.register_password_hint));
            return false;
        }
        putRegisterPhoneInfo(phone,password);
        return true;
    }

    private boolean checkAccountInput(String account, String password){
        if (account.length() < 4 || account.length() > 16){
            ToastUtils.showMsg(ResourceHelper.getString(R.string.register_account_hint));
            return false;
        }
        if (password.length()<6 || password.length() > 16){
            ToastUtils.showMsg(ResourceHelper.getString(R.string.register_password_hint));
            return false;
        }
        putRegisterAccountInfo(account,password);
        return true;
    }
    @Override
    public boolean onBackPressed() {
        boolean blockBackAction = waitingVerifyCode;
        if (waitingVerifyCode) {
            IMEUtil.hideIME(RegisterViewController.this);
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

    /**
     * 获取短信验证码
     *
     * @param phone 手机号码
     */
    private void getVerificationCodeButtonImpl(String phone) {
        showLoading();
        ApiFacade.getInstance().requestVerificationCode(phone, IAuthApi.VCODE_TYPE_REGISTER, retryTime,new TtRespListener<Void>() {
            @Override
            public void onNetSucc(String url, Map<String, String> params, Void result) {
                hideLoading();
                retryTime ++;
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
    private void registerByPhoneImpl(final String phone, final String password,final String vCode) {
        showLoading();
        TtRespListener<AuthModel> callback = new TtRespListener<AuthModel>() {
            @Override
            public void onNetSucc(String url, Map params, AuthModel result) {
                if (params != null) {
                    Log.d(TAG, "registerByPhone " + result.toString());
                    notifyAuthResult(StatusCodeDef.SUCCESS, "", result);
                    close();
                } else {
                    Log.d(TAG, "registerByPhone return null;");
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
        };

        ApiFacade.getInstance().registerByPhone(phone, password,
                vCode, callback);

    }

    private void invokeRegisterByPhone(String phone, String password, String vCode) {
        showLoading();
        TtRespListener<AuthModel> callback = new TtRespListener<AuthModel>() {
            @Override
            public void onNetSucc(String url, Map params, AuthModel result) {
                if (params != null) {
                    Log.d(TAG, "registerByPhone " + result.toString());
                    notifyAuthResult(StatusCodeDef.SUCCESS, "", result);
                    close();
                } else {
                    Log.d(TAG, "registerByPhone return null;");
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
        };

        ApiFacade.getInstance().registerByPhone(phone, password,
                vCode, callback);
    }

    private void registerByAccountImpl(final String account, final String password){
        showLoading();
        ApiFacade.getInstance().registerByUserName(password,account,new TtRespListener<AuthModel>(){
            @Override
            public void onNetworkComplete() {
                super.onNetworkComplete();
                hideLoading();
            }

            @Override
            public void onNetSucc(String url, Map<String, String> params, AuthModel result) {
                if (params != null) {
                    Log.d(TAG, "registerByPhone " + result.toString());
                    notifyAuthResult(StatusCodeDef.SUCCESS, "", result);
                    close();
                } else {
                    Log.d(TAG, "registerByPhone return null;");
                }
            }
        });
    }

    private void tipTologin(){
        ToastUtils.showMsg("提示前往登录页面吧");
    }

    private void clearRegisterPhoneInfo() {
        requestingPhoneNumber = "";
        requestingPhoneMd5Pwd = "";
    }

    private void putRegisterPhoneInfo(String phone, String md5Pwd) {
        requestingPhoneNumber = phone;
        requestingPhoneMd5Pwd = md5Pwd;
    }

    private void putRegisterAccountInfo(String account, String md5Pwd) {
        requestingAccount = account;
        requestingAccountMd5Pwd = md5Pwd;
    }

    private void switchRegisterTypeState(int state) {
        if (currentState == state) {
            return;
        }
        switch (state) {
            case STATE_REGISTER_PHONE:
                registerPhoneContainer.setVisibility(View.VISIBLE);
                registerAccountContainer.setVisibility(View.GONE);
                registerTypeRadioGroup.setVisibility(View.VISIBLE);
                registerPhoneRadioButton.setSelected(true);
                registerAccountRadioButton.setSelected(false);
                registerPhoneRadioButton
                        .setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
                                R.drawable.tt_sdk_shape_dialog_radio_button_bg);
                registerAccountRadioButton
                        .setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
                                R.drawable.tt_sdk_shape_dialog_radio_button_bg_empty);

                break;
            case STATE_REGISTER_ACCOUNT:
                registerPhoneContainer.setVisibility(View.GONE);
                registerAccountContainer.setVisibility(View.VISIBLE);
                registerPhoneRadioButton.setSelected(false);
                registerAccountRadioButton.setSelected(true);
                registerPhoneRadioButton
                        .setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
                                R.drawable.tt_sdk_shape_dialog_radio_button_bg_empty);
                registerAccountRadioButton
                        .setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
                                R.drawable.tt_sdk_shape_dialog_radio_button_bg);
                break;
            default:
                return;
        }
        currentState = state;
        updateRegisterButtonState();
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

    /*
    * 监听短信数据库
    */
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
                //根据正则分组表达式获取验证码  // TODO: 2017/8/10  改成桃子的
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
