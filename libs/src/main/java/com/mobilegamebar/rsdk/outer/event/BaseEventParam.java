package com.mobilegamebar.rsdk.outer.event;

/**
 * Created by levyyoung on 15/5/12.
 */
public class BaseEventParam<T> {

    public BaseEventParam() {
    }

    public BaseEventParam(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public int code = 0;
    public T data = null;
}
