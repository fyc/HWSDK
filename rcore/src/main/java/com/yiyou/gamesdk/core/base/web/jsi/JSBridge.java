package com.yiyou.gamesdk.core.base.web.jsi;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.google.gson1.Gson;
import com.google.gson1.JsonObject;
import com.yiyou.gamesdk.PluginManager;
import com.yiyou.gamesdk.core.base.web.util.WebViewUtil;
import com.yiyou.gamesdk.model.JsResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nekomimi on 2017/11/29.
 */

public class JSBridge {
    private static final String TAG = "JSBridge";

    private static final String ERROR_CALLBACKNAME = "error callback";
    private static final String ERROR_JSON_ERROR = "error json";
    private static final String ERROR_NO_FUNCTION_FOUND = "can't find support function";
    private static final String SUCCESS = "success";

    private static final String GET_ORIENTATION = "getOrientation";

    private static String JSBridgeScript;

    static {
        JSBridgeScript = "(function(){if(window.WebViewJavascriptBridge){return;}window.TZJSBridge=function(json){window.RSDKJSBridge.TZJSBridge(JSON.stringify(json));};window.WebViewJavascriptBridge={__RETURN_VALUE__:undefined};})();";
    }

    public static void dispatchJSBridgeReadyEvent(WebView webView) {
        WebViewUtil.evaluateJavaScriptV2(webView, "javascript:" + JSBridgeScript);
    }

    public static void Call(@NonNull WebView webView,@NonNull String js){
        String cmd = "";
        String callbackName = "";
        try {
            JSONObject jsonObject = new JSONObject(js);
            cmd = jsonObject.optString("cmd");
            callbackName = jsonObject.optString("callbackName");
        }catch (JSONException e){
            e.printStackTrace();
            if (!TextUtils.isEmpty(callbackName)){
                WebViewUtil.evaluateJavascript(webView, buildCallback(callbackName,1,ERROR_CALLBACKNAME,""));
            }
            return;
        }

        if (GET_ORIENTATION.equals(cmd)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("orientation", PluginManager.getInstance().getOrientation());
            WebViewUtil.evaluateJavascript(webView, buildCallback(callbackName, 0, SUCCESS, jsonObject));
        }else {
            WebViewUtil.evaluateJavascript(webView, buildCallback(callbackName,1,ERROR_NO_FUNCTION_FOUND,""));
        }



    }

    public static String buildCallback(String callbackName, int result, String msg, Object data){
        JsResponse response = new JsResponse();
        response.setResult(result);
        response.setMsg(msg);
        response.setData(data);
        Gson gson = new Gson();
        Log.d(TAG, "buildCallback: " + gson.toJson(response));
        return callbackName + "(" + gson.toJson(response) + ")";

    }

}
