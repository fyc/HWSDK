package com.yiyou.gamesdk.core.ui.dialog.biz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.consts.StatusCodeDef;
import com.yiyou.gamesdk.core.ui.dialog.ViewControllerNavigator;
import com.yiyou.gamesdk.core.ui.widget.DataPicker;
import com.yiyou.gamesdk.core.ui.widget.DrawableEditText;
import com.yiyou.gamesdk.core.ui.widget.StandardDialog;
import com.yiyou.gamesdk.model.AccountHistoryInfo;
import com.yiyou.gamesdk.model.AuthModel;
import com.mobilegamebar.rsdk.outer.event.IDialogParam;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.mobilegamebar.rsdk.outer.util.StringUtils;
import com.yiyou.gamesdk.util.IMEUtil;
import com.yiyou.gamesdk.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Orange on 15/6/10.
 */
public class LoginViewController extends BaseAuthViewController {

    private static final String TAG = "RSDK:LoginViewController ";
    private static final String MD5_PWD_PREFIX = "\u0d62\u0d63\u135f";

    private InputFilter[] phoneLengthFilter = new InputFilter[]{
            new InputFilter.LengthFilter(11)
    };


    private Button loginButton;
    private View backRegisterButton;
    private View resetPasswordBtn;
    private View titleImg;
    private TextView titleTv;
    private DrawableEditText accountEdit;
    private EditText passwordEdit;
    private TextView backTitleContainerBtn;
    private TextView closeTitleContainerBtn;


    private AccountEditViewController accountEditViewController;
    private HistoryPickerController historyPickerController;
    //自动填账号/手机号 缓存.
    private String autoFillCache = "";


    public LoginViewController(Context context, IDialogParam params) {
        super(context, params);
        initView();
        tryShowLastLoginAccount();
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.tt_sdk_container_login;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    private void initView() {
        loginButton = (Button)findViewById(R.id.btn_login_container_login);
        ViewUtils.setViewEnable(loginButton, false);
        titleTv = (TextView)findViewById(R.id.tv_title_container_title);
        titleImg = findViewById(R.id.img_title_container_title);
//        resetPasswordBtn = findViewById(R.id.btn_login_container_forget_password);
        backRegisterButton = findViewById(R.id.btn_login_container_back_register);
        accountEdit = (DrawableEditText) findViewById(R.id.edit_login_container_account);
        accountEditViewController = new AccountEditViewController(accountEdit);
        accountEditViewController.noRightDrawableSate();
        historyPickerController = new HistoryPickerController(accountEdit);
//        passwordEdit = (EditText) findViewById(R.id.edit_login_container_password);
        backTitleContainerBtn = (TextView)findViewById(R.id.btn_title_container_back);
        closeTitleContainerBtn = (TextView)findViewById(R.id.btn_title_container_close);
        backTitleContainerBtn.setVisibility(GONE);
        titleImg.setVisibility(VISIBLE);
        titleTv.setVisibility(GONE);
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
                    } else if (!str.endsWith(autoFillCache)) {
                        passwordEdit.setText("");
                    }
                } else {
                    blankHit = false;
                }
                ViewUtils.setViewEnable(loginButton, accountEdit.length() != 0);
            }
        });

        accountEdit.setOnRightDrawableClickListener(new DrawableEditText.OnDrawableClickListener() {
            @Override
            public void onPressed(DrawableEditText editText) {
            }

            @Override
            public void onCancel(DrawableEditText editText) {

            }

            @Override
            public void onClick(DrawableEditText editText) {
                //try dismiss data pickper
                int state = accountEditViewController.nextRightDrawableState();
                Log.d(TAG, "onClick: " + state);
                IMEUtil.hideIME(LoginViewController.this);
                if (state == 1) {
                    historyPickerController.tryShowPicker();
                } else {
                    historyPickerController.tryDismissPicker();
                }
            }
        });



        historyPickerController.setDataPickListener(new DataPicker.DataPickerListener<AccountHistoryWrapper>() {
            @Override
            public void onDataSelected(DataPicker picker, int position) {
                AccountHistoryWrapper wrapper = (AccountHistoryWrapper) picker.getSelectedData();
                tryFillPasswordFromHistory(wrapper.dataName());
                autoFillCache = wrapper.dataName();
                accountEdit.setText(wrapper.dataName());
            }

            @Override
            public void onDataDeleted(DataPicker picker, AccountHistoryWrapper data) {
                ApiFacade.getInstance().deleteAccountHistory(String.valueOf(data.data().userID));
                tryShowLastLoginAccount();

                if (picker.getSourceData().size() < 2) {
                    accountEdit.post(new Runnable() {
                        @Override
                        public void run() {
                            accountEditViewController.noRightDrawableSate();
                        }
                    });
                }
            }

            @Override
            public void onDataPickerShow(DataPicker picker) {
                String defaultAccountOrPhone = accountEdit.getText().toString();
                if (!StringUtils.isBlank(defaultAccountOrPhone)) {
                    List<AccountHistoryWrapper> sourceData = picker.getSourceData();
                    int position = -1;
                    for (int i = 0; i < sourceData.size(); i++) {
                        if (sourceData.get(i).dataName().equals(defaultAccountOrPhone)) {
                            position = i;
                            break;
                        }
                    }
                    if (position >= 0) {
                        picker.setSelectedPosition(position);
                    }
                }
            }

            @Override
            public void onDataItemClose(final DataPicker picker, final int position, AccountHistoryWrapper data) {
                int warningStringRes = R.string.warning_delete_account;
                StandardDialog dialog
                        = new StandardDialog(getDialogParam().getActivityContext());
                dialog.setTitleTip(R.string.delete_account);
                dialog.setMessageTip(ResourceHelper.getString(warningStringRes, data.dataName()));
                dialog.setEnsureText(R.string.delete);
                dialog.setCancelText(R.string.cancel);
                dialog.setListener(new StandardDialog.DialogClickListener() {
                    @Override
                    public void onEnsureClick() {
                        picker.deleteItem(position);
                    }
                });
                dialog.show();
            }

            @Override
            public void onDataPickerDestroy() {
                accountEditViewController.resetRightDrawableState();
            }
        });

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                IMEUtil.hideIME(LoginViewController.this);
                loginImpl(accountEdit.getText().toString(), passwordEdit.getText().toString());
            }
        });
        backRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                IMEUtil.hideIME(LoginViewController.this);
                ViewControllerNavigator.getInstance().toRegister(getDialogParam());
            }
        });
        resetPasswordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IMEUtil.hideIME(LoginViewController.this);
                ViewControllerNavigator.getInstance().toResetPassword(getDialogParam());
            }
        });
        ViewUtils.bindEditWithButton(passwordEdit, loginButton);

        addTextWatcher(accountEdit, passwordEdit);

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
                    updateLoginButtonState();
                }
            });
        }
    }

    private void updateLoginButtonState(){
        if (accountEdit.length() == 0 || passwordEdit.length() == 0){
            ViewUtils.setViewEnable(loginButton, false);
        }else {
            ViewUtils.setViewEnable(loginButton, true);
        }
    }

    private void loginImpl(String account, String password) {
        loginButton.setEnabled(false);
        showLoading();
        ApiFacade.getInstance().login(account,password,new TtRespListener<AuthModel>() {
            @Override
            public void onNetworkComplete() {
                loginButton.setEnabled(true);
            }

            @Override
            public void onNetSucc(String url, Map<String, String> params, AuthModel result) {
                onLoginSuccess(result);
            }

            @Override
            public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                super.onNetError(url, params, errno, errmsg);
                onLoginFail();
                accountOrPwdErrorHandle(errmsg);
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
                onLoginFail();
                accountOrPwdErrorHandle(errmsg);
            }
        });
    }



    private void onLoginFail() {
        hideLoading();
    }

    private void onLoginSuccess(AuthModel result) {
        hideLoading();
        notifyAuthResult(StatusCodeDef.SUCCESS, "", result);
        close();
    }

    private void cleanInputs() {
        accountEdit.setText("");
        passwordEdit.setText("");
    }

    private void putHistoryAccount2Input(AccountHistoryInfo historyInfo) {
        if (TextUtils.isEmpty(historyInfo.phone)) {
            autoFillCache = historyInfo.username;
            accountEdit.setText(historyInfo.username);
            accountEdit.setSelection(historyInfo.username.length());
        }else {
            autoFillCache = historyInfo.phone;
            accountEdit.setText(historyInfo.phone);
            accountEdit.setSelection(historyInfo.phone.length());
        }
    }

    private void tryShowLastLoginAccount() {
        Log.d(TAG, "tryShowLastLoginAccount: ");
//        List<AccountHistoryInfo> currentGameAuthHistories
//                = ApiFacade.getInstance().getCurrentGameAuthHistories();
        List<AccountHistoryInfo> allGameAuthHistories
                = ApiFacade.getInstance().getAccountHistories();
        if (allGameAuthHistories.size() > 1) {
            Log.d(TAG, "tryShowLastLoginAccount: size:" + allGameAuthHistories.size());
            accountEditViewController.resetRightDrawableState();
        } else {
            accountEditViewController.noRightDrawableSate();
        }
//        if (!currentGameAuthHistories.isEmpty()) {
//            //优先填上次在这个游戏登录过的
//            AccountHistoryInfo historyInfo = currentGameAuthHistories
//                    .get(0);
//            putHistoryAccount2Input(historyInfo);
//            tryFillPasswordFromHistory(historyInfo.username);
//        } else
        if (!allGameAuthHistories.isEmpty()) {
            AccountHistoryInfo historyInfo = allGameAuthHistories
                    .get(0);
            putHistoryAccount2Input(historyInfo);
            tryFillPasswordFromHistory(historyInfo.username);
        } else {
            //什么都没得填
            cleanInputs();
        }
        
        //有一键注册账号就填一键注册账号
//        Pair<String, String> lastAutoRegisterInfo
//                = ApiFacade.getInstance().getLastAutoRegisterInfo();
//        if (lastAutoRegisterInfo != null) {
//            //同一次使用周期有一键注册过账号并成功了
//            autoFillCache = lastAutoRegisterInfo.first;
//            accountEdit.setText(lastAutoRegisterInfo.first);
//            String rPwd = lastAutoRegisterInfo.second;
//            passwordEdit.setText(rPwd == null ? "" : rPwd);
//        }
    }

    private void tryFillPasswordFromHistory(String usernameOrPhone) {
        //加点md5密码的标识
        String pwd = ApiFacade.getInstance().getPasswordFromHistoryByUsername(usernameOrPhone);
        if (StringUtils.isBlank(pwd)) {
            pwd = ApiFacade.getInstance().getPasswordFromHistoryByPhone(usernameOrPhone);
        }

        if (StringUtils.isBlank(pwd)){
            Log.i(TAG, "fail to get pwd from history.");
            passwordEdit.setText("");
        } else {
            String pwdToFill = MD5_PWD_PREFIX + pwd;
            passwordEdit.setText(pwdToFill);
        }
    }

    private void accountOrPwdErrorHandle(String errmsg) {


    }

    class AccountEditViewController {
        DrawableEditText editView;
        int rightDrawableStateCount = 0;
        boolean noRightDrawable;

        public AccountEditViewController(DrawableEditText accountEditView) {
            editView = accountEditView;
        }

        public void noRightDrawableSate() {
            noRightDrawable = true;
            rightDrawableStateCount = 0;
            editView.setDrawableRight(0);
        }

        public void resetRightDrawableState() {
            noRightDrawable = false;
            rightDrawableStateCount = 0;
            editView.setDrawableRight(R.drawable.tt_sdk_icon_expand);
        }

        public int nextRightDrawableState() {
            int state = -1;
            if (noRightDrawable) {
                return state;
            }
            state = ++rightDrawableStateCount % 2;
            if (state == 0) {
                editView.setDrawableRight(R.drawable.tt_sdk_icon_expand);
            } else {
                editView.setDrawableRight(R.drawable.tt_sdk_icon_collapse);
            }
            return state;
        }
    }

    class HistoryPickerController {

        DataPicker picker = null;
        View anchor;
        DataPicker.DataPickerListener<AccountHistoryWrapper> listener = null;

        public HistoryPickerController(View anchorView) {
            anchor = anchorView;
        }

        public void tryDismissPicker() {
            if (picker != null) {
                picker.dismiss();
                picker.setDataPickerListener(null);
                picker = null;
            }
        }

        public void tryShowPicker() {
            tryDismissPicker();
            tryListAccountHistories();
        }

        private void tryListAccountHistories() {

            List<AccountHistoryInfo> allGameAuthHistories
                    = ApiFacade.getInstance().getAccountHistories();
            Log.d(TAG, "tryListAccountHistories: " + allGameAuthHistories.size());
            if (allGameAuthHistories.isEmpty()) {
                return;
            }
            showNewDataPicker(toAccountHistoryWrapper(allGameAuthHistories));
        }



        private void showNewDataPicker(List<AccountHistoryWrapper> data) {
            int height = (int) (ViewUtils.distanceToScreenBottom(anchor) * 0.6f);
            int width = (int) (anchor.getWidth() );
//            int dx = (int) ((anchor.getWidth() - width) / 2.0f);
            picker = new DataPicker<AccountHistoryWrapper>(getContext(), width, height, true);
            picker.setDataSource(data);
            picker.setDataPickerListener(listener);
            picker.showAsDropDown(LoginViewController.this.accountEdit, 0, 0);
        }

        private List<AccountHistoryWrapper> toAccountHistoryWrapper(
                @NonNull List<AccountHistoryInfo> authHistories) {
            List<AccountHistoryWrapper> wrapperList
                    = new ArrayList<>(authHistories.size());
            for (AccountHistoryInfo info : authHistories) {
                wrapperList.add(new AccountHistoryWrapper(info));
            }
            return wrapperList;
        }

        public void setDataPickListener(DataPicker.DataPickerListener<AccountHistoryWrapper> l) {
            listener = l;
        }
    }

    private static class AccountHistoryWrapper implements DataPicker.IDataWrapper {
//        public static final int DATA_TYPE_ACCOUNT = 0;
//        public static final int DATA_TYPE_PHONE = 1;

        private AccountHistoryInfo orgInfo;
//        private int dataType = DATA_TYPE_ACCOUNT;

        public AccountHistoryWrapper(@NonNull AccountHistoryInfo orgInfo) {
            this.orgInfo = orgInfo;
//            this.dataType = dataType;
        }

        @Override
        public String dataName() {

            return TextUtils.isEmpty(orgInfo.phone) ? String.valueOf(orgInfo.username) : orgInfo.phone;
        }

        @Override
        public String key() {
            return String.valueOf(orgInfo.userID);
        }

        public AccountHistoryInfo data() {
            return orgInfo;
        }

    }

}
