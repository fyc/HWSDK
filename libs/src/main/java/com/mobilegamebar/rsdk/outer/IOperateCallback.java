package com.mobilegamebar.rsdk.outer;

/**
 * Created by levyyoung on 15/4/30.
 */

/**
 *
 * @param <T>
 */
public interface IOperateCallback<T> {

    public void onResult(int code, T resultData);

}

