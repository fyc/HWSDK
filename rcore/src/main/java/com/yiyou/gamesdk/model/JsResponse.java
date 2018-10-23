package com.yiyou.gamesdk.model;

/**
 * Created by Nekomimi on 2017/11/29.
 */

public class JsResponse {

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private int result;
    private String msg;
    private Object data;

}
