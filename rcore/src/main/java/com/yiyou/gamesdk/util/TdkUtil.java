package com.yiyou.gamesdk.util;

/**
 * Created by charles on 11/11/16.
 */

public class TdkUtil {

    static {
        System.loadLibrary("dtk");
    }

    public native static byte[] getA();

}
