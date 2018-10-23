package com.yiyou.gamesdk.core.ui.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.FinishFragmentEvent;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.def.IAuthApi;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.core.ui.dialog.biz.LoadingDialog;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;
import com.yiyou.gamesdk.util.ToastUtils;
import com.yiyou.gamesdk.util.ViewUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.VISIBLE;

/**
 *
 * Created by Nekomimi on 2017/5/2.
 */

public class ForgetPayPasswordFragment extends BaseFragment {
    private static final String TAG = "ForgetPayPasswordFragment";

    private EditText phoneEdt;
    private EditText vCodeEdt;
    private EditText passwordEdt;
    private Button vCodeGetBtn;
    private Button confirmBtn;
    private LoadingDialog loadingDialog;

    private int retryTime = 0;

    private String requestPhone = "";
    private String requestVCode = "";
    private String requestPassword = "";

    ReGetVerifyCodeButtonController reGetVerifyCodeButtonController;
    SmsVerificationCode smsObserver;

    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        View root = LayoutInflater.from(content).inflate(R.layout.tt_sdk_fragment_forget_pay_password,container,false);
        phoneEdt = (EditText)root.findViewById(R.id.edt_phone);
        vCodeEdt = (EditText)root.findViewById(R.id.edt_phone_vcode);
        passwordEdt = (EditText)root.findViewById(R.id.edt_pay_password);
        vCodeGetBtn = (Button)root.findViewById(R.id.btn_verification_code_get);
        confirmBtn = (Button)root.findViewById(R.id.btn_confirm);
        phoneEdt.setText(ApiFacade.getInstance().getPhone());
        loadingDialog = new LoadingDialog(getActivity());
        reGetVerifyCodeButtonController = new ReGetVerifyCodeButtonController(vCodeGetBtn);
        smsObserver = new SmsVerificationCode(new Handler());
        getContext().getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
        phoneEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                vCodeGetBtn.setEnabled(s.length()>0);
            }
        });
        addTextWatcher(phoneEdt,vCodeEdt,passwordEdt);
        vCodeGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneEdt.length() != 11){
                    ToastUtils.showMsg(R.string.please_input_valid_number);
                }else {
                    getVerificationCodeButtonImpl(phoneEdt.getText().toString());
                }
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput(phoneEdt.getText().toString(), vCodeEdt.getText().toString(), passwordEdt.getText().toString())){
                    resetPayPassword();
                }
            }
        });
        container.addView(root);

    }

    private void resetPayPassword(){
        loadingDialog.show();
        ApiFacade.getInstance().forgetPayPassword(requestPhone,requestPassword,requestVCode,new TtRespListener(){
            @Override
            public void onNetSucc(String url, Map params, Object result) {
                super.onNetSucc(url, params, result);
                ToastUtils.showMsg("修改支付密码成功");
                EventDispatcherAgent.defaultAgent().broadcast(FinishFragmentEvent.TYPE_FINISH_FRAGMENT,
                        new FinishFragmentEvent.Param(0, getActivity(), getParentFragment()));
            }

            @Override
            public void onNetError(String url, Map params, String errno, String errmsg) {
                super.onNetError(url, params, errno, errmsg);
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
            }

            @Override
            public void onNetworkComplete() {
                super.onNetworkComplete();
                loadingDialog.dismiss();
            }
        });

        clearRequestInput();
    }

    private boolean checkInput(String phone, String vCode, String password){
        if (phone.length() != 11){
            ToastUtils.showMsg(R.string.please_input_valid_number);
            return false;
        }
        if (password.length() < 6 || password.length() > 16){
            ToastUtils.showMsg(R.string.reset_password_new_password_tip);
            return false;
        }
        putRequestInput(phone,vCode,password);
        return true;
    }

    private void putRequestInput(String phone, String vCode, String password){
        requestPhone = phone;
        requestVCode = vCode;
        requestPassword = password;
    }

    private void clearRequestInput(){
        requestPhone = "";
        requestVCode = "";
        requestPassword = "";
    }

    private void addTextWatcher(EditText... editTexts){
        for (EditText editText : editTexts){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    updateConfirmBtnState();
                }
            });
        }
    }

    private void updateConfirmBtnState(){
        confirmBtn.setEnabled( phoneEdt.length()>0 && vCodeEdt.length() > 0 && passwordEdt.length() > 0 );
    }

    /**
     * 获取短信验证码
     *
     * @param phone 手机号码
     */
    private void getVerificationCodeButtonImpl(String phone) {
        loadingDialog.show();
        ApiFacade.getInstance().requestVerificationCode(phone, IAuthApi.VCODE_TYPE_RESET_PAY_PASSWORD, retryTime, new TtRespListener<Void>() {
            @Override
            public void onNetSucc(String url, Map<String, String> params, Void result) {
                retryTime++;
                if (params != null) {
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
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
            }

            @Override
            public void onNetworkComplete() {
                super.onNetworkComplete();
                loadingDialog.dismiss();
            }
        });
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return CommonTitlePrimaryFragment.newInstance(getString(R.string.title_forget_pay_password));

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

    @Override
    public void onPause() {
//        reGetVerifyCodeButtonController.cancelCountDown();
        getContext().getContentResolver().unregisterContentObserver(smsObserver);
        super.onPause();
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
            ContentResolver cr = getContext().getContentResolver();
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
                    vCodeEdt.setText(matcher.group(3));
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
