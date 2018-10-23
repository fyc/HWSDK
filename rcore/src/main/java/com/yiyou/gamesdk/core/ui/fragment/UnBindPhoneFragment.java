package com.yiyou.gamesdk.core.ui.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.def.IAuthApi;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.core.ui.dialog.biz.LoadingDialog;
import com.yiyou.gamesdk.model.AccountHistoryInfo;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;
import com.yiyou.gamesdk.util.ToastUtils;
import com.yiyou.gamesdk.util.ViewUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static com.yiyou.gamesdk.core.update.DefaultUpdateImpl.TAG;

/**
 *
 * Created by Nekomimi on 2017/4/28.
 */

public class UnBindPhoneFragment extends BaseFragment {

    private TextView bindPhoneTv;
    private EditText vCodeEdt;
    private Button vCodeGetBtn;
    private Button nextBtn;
    private LoadingDialog loadingDialog;

    private String currentBindPhone;
    private int retryTime = 0;

    private String requestPhone = "";
    private String requestVCode = "";

    ReGetVerifyCodeButtonController reGetVerifyCodeButtonController;
    SmsVerificationCode smsObserver;

    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        View root = LayoutInflater.from(content).inflate(R.layout.tt_sdk_fragment_unbind_phone,container,false);
        bindPhoneTv = (TextView) root.findViewById(R.id.tv_bind_phone);
        vCodeEdt = (EditText)root.findViewById(R.id.edt_bind_phone_vcode);
        vCodeGetBtn = (Button)root.findViewById(R.id.btn_verification_code_get);
        nextBtn = (Button)root.findViewById(R.id.btn_next);
        loadingDialog = new LoadingDialog(getActivity());

        reGetVerifyCodeButtonController = new ReGetVerifyCodeButtonController(vCodeGetBtn);
        smsObserver = new SmsVerificationCode(new Handler());
        getContext().getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);

        currentBindPhone = ApiFacade.getInstance().getPhone();
        SpannableString spannableString = new SpannableString((getString(R.string.current_phone_tip))+ currentBindPhone);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#BDBDBD"));
        spannableString.setSpan(colorSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        bindPhoneTv.setText(spannableString);
        vCodeEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nextBtn.setEnabled(vCodeEdt.length()>0);
            }
        });
        vCodeGetBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getVerificationCodeButtonImpl(currentBindPhone);
            }
        });
        nextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput(currentBindPhone, vCodeEdt.getText().toString())){
                    unbindPhone();
                }
            }
        });
        container.addView(root);

    }


    private boolean checkInput(String phone, String vCode){
        putRequestInput(phone,vCode);
        return true;
    }

    private void putRequestInput(String phone, String vCode){
        requestPhone = phone;
        requestVCode = vCode;
    }

    private void clearRequestInput(){
        requestPhone = "";
        requestVCode = "";
    }

    private void unbindPhone(){
        loadingDialog.show();
        ApiFacade.getInstance().unbindPhone(requestPhone,requestVCode,new TtRespListener(){
            @Override
            public void onNetSucc(String url, Map params, Object result) {
                super.onNetSucc(url, params, result);
                ToastUtils.showMsg(R.string.unbind_phone_succ);
                AccountHistoryInfo accountHistoryInfo = ApiFacade.getInstance().getCurrentHistoryAccount();
                accountHistoryInfo.phone = "";
                ApiFacade.getInstance().insertOrUpdateAccountHistory(accountHistoryInfo);
                clearRequestInput();
//                Bundle bundle = new Bundle();
//                bundle.putBoolean(StartType.KEY_START_NEW_AND_COLSE_ME,true);
//                EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
//                        new StartActivityEvent.FragmentParam(0, getActivity(), BindPhoneFragment.class.getName(), bundle));
//                EventDispatcherAgent.defaultAgent().broadcast(FinishFragmentEvent.TYPE_FINISH_FRAGMENT,
//                        new FinishFragmentEvent.Param(0, getActivity(), getParentFragment()));
                startFragment(new BindPhoneFragment(), true);
//                AppInternalHandler.bindPhone(getActivity());
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
            }

            @Override
            public void onNetError(String url, Map params, String errno, String errmsg) {
                super.onNetError(url, params, errno, errmsg);
            }

            @Override
            public void onNetworkComplete() {
                super.onNetworkComplete();
                loadingDialog.dismiss();
            }
        });
    }

    /**
     * 获取短信验证码
     *
     * @param phone 手机号码
     */
    private void getVerificationCodeButtonImpl(String phone) {
        loadingDialog.show();
        ApiFacade.getInstance().requestVerificationCode(phone, IAuthApi.VCODE_TYPE_UNBIND_PHONE, retryTime, new TtRespListener<Void>() {
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
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    @Override
    protected Fragment getTitleBarFragment() {
        return CommonTitlePrimaryFragment.newInstance(getString(R.string.title_unbind_phone));

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
