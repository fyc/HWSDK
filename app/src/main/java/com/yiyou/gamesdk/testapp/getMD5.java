package com.yiyou.gamesdk.testapp;

/**
 * Created by Win on 2016/7/22.
 */
public class getMD5 {
    public static void main(String[] arg){
        String app_secret = "5341aec420b842d47da6aeaf5201b60f";
        String app_key = "51da2485e96e8f0912a9bc1fb2fec75f";
        String test = "354649050046412";
        System.out.println("MD5: " + ByteUtils.generateMd5(app_secret + "Anqu" + app_key));
        System.out.println("MD5 test: " + ByteUtils.generateMd5(test));
    }

}
