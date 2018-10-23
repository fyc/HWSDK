package com.yiyou.gamesdk.core.base.web.jsi;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.yiyou.gamesdk.PluginManager;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.impl.payment.PaymentAdapter;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.base.web.util.WebViewUtil;
import com.yiyou.gamesdk.core.ui.dialog.biz.LoadingDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.CommDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.EditInputDialogView;
import com.yiyou.gamesdk.core.ui.widget.dialog.IDialogView;
import com.yiyou.gamesdk.core.ui.widget.dialog.OnDialogClickListener;
import com.yiyou.gamesdk.util.ByteUtils;
import com.yiyou.gamesdk.util.CustomerServiceHelper;
import com.yiyou.gamesdk.util.PhoneUtils;
import com.yiyou.gamesdk.util.ToastUtils;
import com.yiyou.gamesdk.util.VersionUtil;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by levin on 16-3-24.
 */
public class TTCompactJSAPI {

    public static final String NAME = "RSDKJSBridge";
    public static final String TAG = "RSDK:TTCompactJSAPI ";

    private TTCompactDelegate delegate;
    private Context mContext;
    private WebView webView;
    private LoadingDialog loadingDialog;

    public TTCompactJSAPI(Context context, TTCompactDelegate delegate) {
        this.delegate = delegate;
        this.mContext = context;
    }


    public WebView getWebView() {
        return webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    @JavascriptInterface
    public void TZJSBridge(final String jsonObject){
        Log.d(TAG, "TZJSBridge: " + jsonObject);
        webView.post(new Runnable() {
            @Override
            public void run() {
                JSBridge.Call(webView, jsonObject);

            }
        });
    }

    @JavascriptInterface
    public String getAccessToken() {
        return ApiFacade.getInstance().getSession();
    }

    @JavascriptInterface
    public String getUserID() {
        return String.valueOf(ApiFacade.getInstance().getMainUid());
    }

    @JavascriptInterface
    public String getChildUserID() {
        return ApiFacade.getInstance().getSubUid() + "";
    }


    @JavascriptInterface
    public String getUsername() {
        return ApiFacade.getInstance().getUserName();
    }

    @JavascriptInterface
    public String getGameName() {
        return ApiFacade.getInstance().getCurrentGameName();
    }

    @JavascriptInterface
    public String getGameID() {
        return ApiFacade.getInstance().getCurrentGameID() + "";
    }

    @JavascriptInterface
    public String getplatform() {
        return "2";
    }

    @JavascriptInterface
    public String getdeviceID() {
        return PhoneUtils.getDeviceId(CoreManager.getContext());
    }

    @JavascriptInterface
    public String getchannelID() {
        return ApiFacade.getInstance().getChannel();
    }

    @JavascriptInterface
    public String getbundleID() {
        return CoreManager.getContext().getPackageName();
    }

    @JavascriptInterface
    public void close() {
        delegate.finish();
    }

    /**
     * 获取当前游戏屏幕方向
     * @return   1：竖屏； 2：横屏
     */
    @JavascriptInterface
    public int getOrientation() {
        return PluginManager.getInstance().getOrientation();
    }

    @JavascriptInterface
    public void closeSuccess() {
        ApiFacade.getInstance().notifyOrderState(TTCodeDef.SUCCESS, "支付成功");
        delegate.finish();
    }
    /**
     * 校验支付密码
     *
     * @param js h5端的回调方法名字，字符串类型，校验结果通过这个方法通知h5端
     */
    @JavascriptInterface
    public void verifyPayPwd(final String js) {
        Log.d(TAG, "verifyPayPwd: " + js);
        if (ApiFacade.getInstance().getCurrentHistoryAccount().hasPayPassword) {
            openPayPwdDialog(new OnPayPwdVerifyListener() {
                @Override
                public void onSuccess() {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            WebViewUtil.evaluateJavascript(webView, js + "(0)");
                        }
                    });
                }

                @Override
                public void onFail() {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            WebViewUtil.evaluateJavascript(webView, js + "(1)");
                        }
                    });
                }
            });
        } else {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    WebViewUtil.evaluateJavascript(webView, js + "(0)");
                }
            });
        }
    }

    /**
     * 支付宝钱包支付
     *
     * @param dataJsonStr
     */
    @JavascriptInterface
    public void aliPay(String dataJsonStr) {
        Log.v(TAG, "js invoke aliPay - data : " + dataJsonStr);
        ApiFacade.getInstance().orderThroughClient(PaymentAdapter.PAY_WAY_ALI_PAY, dataJsonStr);
    }

    /**
     * 聚合微信支付
     *
     * @param dataJsonStr
     */
    @JavascriptInterface
    public void weChatPay(String dataJsonStr) {
        Log.v(TAG, "js invoke weChatPay - data : " + dataJsonStr);
        ApiFacade.getInstance().orderThroughClient(PaymentAdapter.PAY_WAY_IPAYNOW, dataJsonStr);
    }

    /**
     * 聚合银联支付
     *
     * @param dataJsonStr
     */
    @JavascriptInterface
    public void iUniPay(String dataJsonStr) {
        Log.v(TAG, "js invoke iUniPay - data : " + dataJsonStr);
        ApiFacade.getInstance().orderThroughClient(PaymentAdapter.PAY_WAY_IPAYNOW, dataJsonStr);
    }


    @JavascriptInterface
    public void isAppAliPay(final String orderNo, final String token) { //App pay
        Log.d(TAG, "isAppAliPay: ");
        Map<String, String> params = new TreeMap<>();
        params.put("orderNo", orderNo);
        params.put("token", token);
        params.put("payChannel", "ALIPAY");
        ApiFacade.getInstance().getOrderFromApp(params);
    }

    @JavascriptInterface
    public void isAppWeChat(final String orderNo, final String token) { //App pay
        Log.d(TAG, "isAppWeChat: ");
        Map<String, String> params = new TreeMap<>();
        params.put("orderNo", orderNo);
        params.put("token", token);
        params.put("payChannel", "WECHAT");
        ApiFacade.getInstance().getOrderFromApp(params);
    }

    @JavascriptInterface
    public void isAppUniPay(final String orderNo, final String token) { //App pay
        Log.d(TAG, "isAppUniPay: ");
        Map<String, String> params = new TreeMap<>();
        params.put("orderNo", orderNo);
        params.put("token", token);
        params.put("payChannel", "UNIPAY");
        ApiFacade.getInstance().getOrderFromApp(params);
    }

    /**
     * @param msg          消息内容
     * @param durationType 0 Short  1 Long
     */
    @JavascriptInterface
    public void toast(String msg, int durationType) {
        Log.v(TAG, "js invoke toast msg : " + msg);
        ToastUtils.showMsg(msg, durationType);
    }

    /**
     * 打开客服QQ聊天
     */
    @JavascriptInterface
    public void connectCustomerService() {
        Log.d(TAG, "connectCustomerService: ");
        CustomerServiceHelper.connectCustomerService(mContext);
    }


    @JavascriptInterface
    public boolean copyToCliboard(String str) {
        Log.d(NAME, "copyToCliboard: " + str);
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            clip.setText(str);
        }
        return true;
    }

    @JavascriptInterface
    public String getWalletParams() {
        if (!ApiFacade.getInstance().isLogin()) {
            return "";
        }
        String key = "2ae000b562cba514";
        Map<String, String> params = new HashMap<>();
        params.put("caller", "RW");
        params.put("uid", String.valueOf(ApiFacade.getInstance().getMainUid()));
        String content = new JSONObject(params).toString();
        params.put("accessToken", ApiFacade.getInstance().getSession());
        params.put("sdkVersion", VersionUtil.getSdkVersion());
        params.put("osType", "ANDROID");
        params.put("sign", ByteUtils.generateMd5V2(content + key));
        StringBuilder urlBuffer = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String mapKey = entry.getKey();
            String value = entry.getValue() == null ? "" : entry.getValue();
            if (urlBuffer.length() != 0 && !urlBuffer.toString().endsWith("?")) {
                urlBuffer.append("&");
            }
            urlBuffer.append(mapKey).append("=");
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                try {
                    value = URLEncoder.encode(value, "GBK");
                } catch (UnsupportedEncodingException e1) {
                    return "";
                }
            }
            urlBuffer.append(value);
        }
        Log.d(NAME, "getWalletParams: " + params.toString());
        return urlBuffer.toString();
    }


    private void openPayPwdDialog(final OnPayPwdVerifyListener listener) {
        Log.d(TAG, "openPayPwdDialog: ");
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog = new LoadingDialog((Activity) mContext);
            }
        });
        CommDialog.Builder builder = new CommDialog.Builder((Activity) mContext);
        final EditInputDialogView dialogView = new EditInputDialogView(mContext);
        dialogView.setEnsureText(R.string.ensure);
        dialogView.setTitleTip(R.string.tip_input_pay_password);
        dialogView.setHint(R.string.hint_pay_password);
        dialogView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        builder.setView(dialogView);
        builder.setLisenter(new OnDialogClickListener() {
            @Override
            public void onCancel() {
                listener.onFail();
            }

            @Override
            public void onEnsure(final IDialogView view) {
                String password = dialogView.getInput();
                if (password.length() < 6 || password.length() > 16) {
                    ToastUtils.showMsg(R.string.hint_pay_password);
                    return;
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null)
                            loadingDialog.show();
                    }
                });
                ApiFacade.getInstance().verifyPayPassword(dialogView.getInput(), new TtRespListener() {
                    @Override
                    public void onNetSucc(String url, Map params, Object result) {
                        super.onNetSucc(url, params, result);
                        listener.onSuccess();
                        view.close();
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
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingDialog != null)
                                    loadingDialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
        CommDialog changeChildDialog = builder.create();
        changeChildDialog.show();
    }

    interface OnPayPwdVerifyListener {
        void onSuccess();

        void onFail();
    }


    public static String getStringFromFile(String fileName, String encoding) {
        int bufferSize = 4096;
        InputStream is = null;
        ByteArrayOutputStream outStream = null;
        try {
            is = ResourceHelper.openAssetsFile(fileName);
            outStream = new ByteArrayOutputStream();
            byte[] data = new byte[bufferSize];
            int count;
            while ((count = is.read(data, 0, bufferSize)) != -1) {
                outStream.write(data, 0, count);
            }
            return new String(outStream.toByteArray(), encoding);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
