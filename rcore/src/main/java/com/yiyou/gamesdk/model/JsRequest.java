package com.yiyou.gamesdk.model;

/**
 * Created by Nekomimi on 2017/11/29.
 */

public class JsRequest<T> {

    private String cmd;
    private T req;
    private String callbackName;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public T getReq() {
        return req;
    }

    public void setReq(T req) {
        this.req = req;
    }

    public String getCallbackName() {
        return callbackName;
    }

    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }
}
