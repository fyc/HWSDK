package com.yiyou.gamesdk.core.base.http.volley;

import android.text.TextUtils;

import com.android.volley1.AuthFailureError;
import com.android.volley1.DefaultRetryPolicy;
import com.android.volley1.NetworkError;
import com.android.volley1.NetworkResponse;
import com.android.volley1.NoConnectionError;
import com.android.volley1.ParseError;
import com.android.volley1.Request;
import com.android.volley1.Response;
import com.android.volley1.ServerError;
import com.android.volley1.TimeoutError;
import com.android.volley1.VolleyError;
import com.android.volley1.toolbox.HttpHeaderParser;
import com.google.gson1.Gson;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.utils.HttpErrorCodeDef;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.util.ByteUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Win on 2017/4/24.
 */
public class HwRequest<T> extends Request<Result<T>> {

    private static final String TAG = "RSDK: HwRequest";
    protected TtRespListener<T> mListener;
    protected Map<String, String> mParams, mHeader;
    private String mUrl;
    private Class<T> mClazz;
    protected JSONObject postParams;
    private int mContentType;

    /**
     * 超时时间
     */
    private static final int DEFAULT_TIMEOUT = 16000;

    /**
     * 重试次数
     */
    private static final int DEFAULT_MAX_RETRIES = 1;

    /**
     * Default charset for JSON request.
     */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    public static final int CONTENT_TYPE_X_WWW_FORM_URLENCODED = 0;
    public static final int CONTENT_TYPE_RAW_JSON = 1;

    public HwRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
        setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DEFAULT_MAX_RETRIES, 1f));
        setShouldCache(false);
        //generate unique id
        String json = "";
        if (mParams != null) {
            mParams.remove("password");
            JSONObject jsonObject = new JSONObject(mParams);
            json = jsonObject.toString();
        }

        String unique_Id = ByteUtils.generateMd5(url + json);
        setUniqueId(unique_Id);

        Log.d(TAG, "unique Id= " + unique_Id);
    }

    //post
    public HwRequest(String url, Map<String, String> params, Class<T> clazz, TtRespListener<T> listener) {
        this(Method.POST, url, null);
        mListener = listener;
        mParams = params;
        mUrl = url;
        mClazz = clazz;
        mContentType = CONTENT_TYPE_RAW_JSON;
        if (params != null) {
            postParams = new JSONObject(params);
        }
    }

    //post
    public HwRequest(String url, Map<String, String> params, Class<T> clazz, TtRespListener<T> listener, int contentType) {
        this(Method.POST, url, null);
        mListener = listener;
        mParams = params;
        mUrl = url;
        mClazz = clazz;
        mContentType = contentType;

        if (params != null) {
            postParams = new JSONObject(params);

        }
    }

    //get
    public HwRequest(String url, Class<T> clazz, TtRespListener<T> listener) {
        this(Method.GET, url, null);
        mContentType = CONTENT_TYPE_RAW_JSON;
        mListener = listener;
        mUrl = url;
        mClazz = clazz;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mContentType == CONTENT_TYPE_X_WWW_FORM_URLENCODED) {
            return super.getBody();
        } else {
            if (postParams == null)
                return null;
            String string = postParams.toString();
            try {
                return string.getBytes(getParamsEncoding());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Encoding not supported: " + getParamsEncoding(), e);
            }
        }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (postParams == null) {
            return super.getHeaders();

        } else {
            // sign
            Map<String, String> header = new TreeMap<>();
            String sign = ByteUtils.generateMd5V2(postParams.toString() + ApiFacade.getInstance().getSdkKey());
            header.put("sign", sign);
            Log.d(TAG, "sign= " + sign);
            header.put("gameID", ApiFacade.getInstance().getCurrentGameID() + "");
            //添加sid，服务器端用于验证session
            String sid = ApiFacade.getInstance().getSession();
            if (!TextUtils.isEmpty(sid)) {
                header.put("accessToken", sid);
            }
            mHeader = header;
            return header;
        }
    }

    @Override
    protected Response<Result<T>> parseNetworkResponse(NetworkResponse response) {
        String json;
        try {
            json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            if (postParams != null) {
                postParams.remove("logzip");
            }


            Log.i(TAG, "url=" + mUrl + "  params=" + (postParams == null ? mParams : postParams));
            Log.i(TAG, "json =" + json);
            JSONObject jsonObject = new JSONObject(json);

            Result<T> result = parse(jsonObject);

            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, mUrl + mParams + "UnsupportedEncodingException= " + e);
            return Response.error(new ParseError(e));

        } catch (JSONException e) {
            Log.e(TAG, mUrl + mParams + "JSONException= " + e);
            return Response.error(new JsonParseError(e));
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        String errDesc;
        if (error.networkResponse != null) {
            errDesc = error.getClass().getSimpleName() + " code=  " + error.networkResponse.statusCode + "   msg=  " + error.getMessage();
        } else {
            errDesc = error.getClass().getSimpleName() + " " + error.getMessage();
        }

        Log.e(TAG, "url= " + mUrl + " params= " + postParams + " header= " + mHeader + " errDesc= " + errDesc);


        if (mListener == null)
            return;
        mListener.onNetworkComplete();

        int errorType = 0;
        if (error instanceof JsonParseError) {
            errorType = HttpErrorCodeDef.ERROR_JSON_PARSE;
            mListener.onNetError(mUrl, mParams, String.valueOf(errorType), errDesc);
            return;
        } else if (error instanceof ParseError) {
            errorType = HttpErrorCodeDef.ERROR_PARSE;
            mListener.onNetError(mUrl, mParams, String.valueOf(errorType), errDesc);
            return;
        }


        if (error instanceof NoConnectionError) {
            errorType = HttpErrorCodeDef.ERROR_NO_NETWORK;
        } else if (error instanceof TimeoutError) {
            errorType = HttpErrorCodeDef.ERROR_CONNECTION_TIMEOUT;
        } else if (error instanceof NetworkError) {
            errorType = HttpErrorCodeDef.ERROR_NETWORK;
        } else if (error instanceof ServerError) {
            errorType = HttpErrorCodeDef.ERROR_SERVER;
        }

        mListener.onFail(errorType, errDesc);
    }

    @Override
    protected void deliverResponse(Result<T> response) {
        if (mListener == null || response == null)
            return;

        mListener.onNetworkComplete();


        Result.Head head = response.getHead();
        String result = head.getResult();
        String msg = head.getMessage();

        if (TextUtils.isEmpty(result)) {
            mListener.onNetError(mUrl, mParams, result, "result is empty");
        }


        if (Result.RESULT_OK.equals(result)) {// succ

            if (response.isArray()) {

                List<T> list = response.getArrayBody();
                mListener.onNetSucc(mUrl, mParams, list);

            } else {

                T body = response.getBody();
//                if (T instanceof IServerTimer) {
//
//                }
                mListener.onNetSucc(mUrl, mParams, body);
            }


        } else if (Result.RESULT_PAY_OK.equals(result)) { //支付请求的成功
            T body = response.getBody();

            mListener.onNetSucc(mUrl, mParams, body);
        } else if (Result.RESULT_SUCCESS.equals(result)) {
            T body = response.getBody();

            mListener.onNetSucc(mUrl, mParams, body);
        } else {// error
            Log.e(TAG, "url=" + mUrl + "  params=" + postParams + "errno=  " + result + " header= " + mHeader + "  errmsg=  " + msg);

            if (mListener != null) {
                mListener.onNetError(mUrl, mParams, result, msg);
            }

        }
    }

    private Result<T> parse(JSONObject json) {

        Result<T> respose = new Result<>();
        Result.Head head = new Result.Head();

        //公告的特殊json
        if (json.has("state")) {
            head.setResult("0");
            head.setMessage("test");
            respose.setHead(head);
            respose.setBody((T) json);
            return respose;
        }


        //先判断是否有head节点
        if (!json.has(Result.HEAD)) {
            // 没有head节点 简洁模式
            String result = json.optString(Result.RESULT);
            String msg = json.optString(Result.MESSAGE);

            head.setResult(result);
            head.setMessage(msg);

            respose.setHead(head);

            return respose;
        }

        JSONObject headObj = json.optJSONObject(Result.HEAD);

        if (headObj == null || headObj.equals(JSONObject.NULL)) {
            //  unknow json ?
            head.setResult(Result.UNKONOW_JSON_ERROR);
            head.setMessage("unknow json error");
            respose.setHead(head);
            return respose;// go on ???
        }

        String result = headObj.optString(Result.RESULT);
        String msg = headObj.optString(Result.MESSAGE);
        //获取服务器时间
        long serverTime = 0;
        if (headObj.has(Result.SERVER_TIME)) {
            serverTime = headObj.optLong(Result.SERVER_TIME);
        }

        head.setResult(result);
        head.setMessage(msg);
        head.setServerTime(serverTime);
        respose.setHead(head);

        if (mClazz == null || mClazz == Void.class) {
            //如果为null 表示不需要解析body内容
            return respose;
        }


        if (json.has(Result.BODY)) {
            try {
                //check body is a array??
                JSONArray jsonArray = json.getJSONArray(Result.BODY);

                respose.setIsArray(true);
                if (jsonArray == null || jsonArray.length() == 0)
                    return respose;

                Gson gson = new Gson();
                List<T> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject job = jsonArray.optJSONObject(i);
                    T t = gson.fromJson(job.toString(), mClazz);
                    list.add(t);
                }
                respose.setArrayBody(list);
            } catch (Exception e) {

                /**
                 * 1. body为null 这种情况 body为 简单健值对
                 * 2. body 为一个object  这种情况 body为jsonobj
                 */
                try {
                    JSONObject bodyObj = json.getJSONObject(Result.BODY);
                    bodyObj.put(Result.SERVER_TIME, serverTime);
                    if (bodyObj == null || bodyObj.equals(JSONObject.NULL)) {
                        return respose;
                    }
                    Gson gson = new Gson();
                    T t = gson.fromJson(bodyObj.toString(), mClazz);
                    respose.setBody(t);
                } catch (Exception e1) {
                    // come here 表示 body内容为 非jsonobj
                    String body = json.optString(Result.BODY);
                    respose.setBody((T) body);
                    return respose;
                }
            }
        }
        return respose;
    }
}
