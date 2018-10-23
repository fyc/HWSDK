package com.yiyou.gamesdk.core.base.http.volley;

import com.android.volley1.NetworkResponse;
import com.android.volley1.VolleyError;

/**
 * Created by chenshuide on 15/6/4.
 */
public class JsonParseError extends VolleyError {

    public JsonParseError() { }

    public JsonParseError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public JsonParseError(Throwable cause) {
        super(cause);
    }
}
