package com.yiyou.gamesdk.core.base.http.volley;

import java.util.List;

/**
 * Created by chenshuide on 15/6/3.
 */
public class Result<T> {
    public static final String HEAD = "head";
    public static final String BODY = "body";
    public static final String APP_BODY = "data";
    public static final String RESULT = "result";
    public static final String MESSAGE = "message";
    public static final String SERVER_TIME = "serverTime";
    public static final String APP_MESSAGE = "msg";
    public static final String RESULT_OK = "0";
    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_PAY_OK = "200";
    public static final String UNKONOW_JSON_ERROR = "10001";

    private boolean isArray;
    private T body;
    private Head head;
    private List<T> arrayBody;



    public boolean isArray() {
        return isArray;
    }

    public void setIsArray(boolean isArray) {
        this.isArray = isArray;
    }


    public List<T> getArrayBody() {
        return arrayBody;
    }

    public void setArrayBody(List<T> arrayBody) {
        this.arrayBody = arrayBody;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public static class Head{
        private String result;
        private String message;
        private long mServerTime;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setServerTime(long serverTime) {
            mServerTime = serverTime;
        }

        public long getServerTime() {
            return mServerTime;
        }
    }

}
