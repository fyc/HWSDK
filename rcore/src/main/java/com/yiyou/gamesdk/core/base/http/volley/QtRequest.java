package com.yiyou.gamesdk.core.base.http.volley;

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
import com.google.gson1.JsonSyntaxException;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.core.base.http.utils.HttpErrorCodeDef;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.util.ByteUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

public class QtRequest<T> extends Request<T> {

    private static final String TAG = "RSDK: QtRequest";
    private final Gson gson = new Gson();
    protected TtRespListener<T> mListener;
    protected Map<String, String> mParams, mHeader;
    private String mUrl;
    private Class<T> mClazz;
    protected JSONObject postParams;
    private int mContentType;

    private String src;
    private static final int KEY = 123123123;
    public static final String GAMW_ID = "201810221735413530001013eed";
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

    public QtRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
        setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DEFAULT_MAX_RETRIES, 1f));
        setShouldCache(false);
    }

    //post
    public QtRequest(String url, Map<String, String> params, Class<T> clazz, TtRespListener<T> listener) {
        this(Method.POST, url, null);
        mListener = listener;
        mParams = params;
        src = mParams.get("src");
        mUrl = url;
        mClazz = clazz;
        mContentType = CONTENT_TYPE_RAW_JSON;
        if (params != null) {
            postParams = new JSONObject(params);
        }
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
            String sign = ByteUtils.generateMd5(src + KEY).toLowerCase();
            header.put("sign", sign);
            Log.d(TAG, "getHeaders:sign= " + sign);
            mHeader = header;
            return header;
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Log.i(TAG, "parseNetworkResponse:url=" + mUrl + "  params=" + (postParams == null ? mParams : postParams));
            Log.i(TAG, "parseNetworkResponse:json =" + json);
            return Response.success(
                    gson.fromJson(json, mClazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new JsonParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        Log.e(TAG, "deliverResponse:response:" + response.toString());
        mListener.onNetSucc(mUrl, mParams, response);
    }

    @Override
    public void deliverError(VolleyError error) {
        String errDesc;
        if (error.networkResponse != null) {
            errDesc = error.getClass().getSimpleName() + " code=  " + error.networkResponse.statusCode + "   msg=  " + error.getMessage();
        } else {
            errDesc = error.getClass().getSimpleName() + " " + error.getMessage();
        }

        Log.e(TAG, "deliverError:url= " + mUrl + " params= " + postParams + " header= " + mHeader + " errDesc= " + errDesc);
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
}
