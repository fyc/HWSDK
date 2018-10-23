package com.yiyou.gamesdk.core.base.http.volley;

import com.android.volley1.AuthFailureError;
import com.android.volley1.DefaultRetryPolicy;
import com.android.volley1.NetworkResponse;
import com.android.volley1.ParseError;
import com.android.volley1.Request;
import com.android.volley1.Response;
import com.android.volley1.VolleyError;
import com.android.volley1.toolbox.HttpHeaderParser;
import com.yiyou.gamesdk.core.base.http.utils.HttpErrorCodeDef;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.util.ByteUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by chenshuide on 15-12-22.
 */
public class HwJsonResquest extends Request<JSONObject> {


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
    private static final String TAG = "TtJsonResquest";


    private Map<String, String> mParams;
    private TtRespListener<JSONObject> mListener;
    private String mUrl;


    private HwJsonResquest(int method, String url, Response.ErrorListener listener) {
        super(method, url, null);
        setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DEFAULT_MAX_RETRIES, 1f));
        setShouldCache(false);

        String json = "";

        if (mParams != null) {
            mParams.remove("password");
            JSONObject object = new JSONObject(mParams);
            json = object.toString();
        }

        String unique_Id = ByteUtils.generateMd5(url + json);
        setUniqueId(unique_Id);
    }


    /**
     * response a json for http post
     *
     * @param url
     * @param params
     * @param listener
     */
    public HwJsonResquest(String url, Map<String, String> params, TtRespListener<JSONObject> listener) {
        this(Method.POST, url, null);
        Log.d(TAG, "http post " + url + " params  " + params);

        mParams = params;
        mListener = listener;
        mUrl = url;
    }


    /**
     * response a json for http get
     *
     * @param url
     * @param listener
     */
    public HwJsonResquest(String url, TtRespListener<JSONObject> listener) {
        this(Method.GET, url, null);
        Log.d(TAG, "http get " + url);
        mListener = listener;
        mUrl = url;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }


    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            Log.i(TAG, "url=" + mUrl + "  params=" + mParams);
            Log.i(TAG, "jsonString =" + jsonString);
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        if (mListener == null)
            return;

        mListener.onNetSucc(mUrl, mParams, response);
    }


    @Override
    public void deliverError(VolleyError error) {
        String errorDes = error == null ? "" : error.getMessage();
        Log.d(TAG, "url = " + mUrl + " params= " + mParams + " error= " + errorDes);
        if (mListener == null)
            return;

        mListener.onFail(HttpErrorCodeDef.ERROR_SERVER, errorDes);
    }
}
