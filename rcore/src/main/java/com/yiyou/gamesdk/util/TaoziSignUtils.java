package com.yiyou.gamesdk.util;

import com.mobilegamebar.rsdk.outer.util.Base64Utils;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.StringUtils;
import com.yiyou.gamesdk.core.api.ApiFacade;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Nekomimi on 2017/8/8.
 */

public class TaoziSignUtils {

    private static final String TAG = "TaoziSignUtils";

    private static String salt1 = "e76eb5ec2640cbb4f7b6e507990f6411";
    private static String salt2 = "f96a2a13644fad395d03fc1f6f7dcbfa";
    private static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnkIlZm63jRG4OxITLiqD+m455xz6xkmJC9T6xfwMI1XN0D3mskoelZ0kkwtV7AZfZEQtzyho3+/OAMVTYuOokvh1i/LoQDGeZq11BWTnEDTY5LLD/wDlIFwszPaN+EO3re2+kVrcuNz8YYqG3Zt4CJvZNlNX/mW8ZTMi+4CDydogsdaLYnkQK/QBx+yDOcLgOjF3083cSK4ddcCLsYFRj+pG+VlsfV8/u+6oxnuiS/Rkmq6rb6JIMO9lmW0gtzbxq9EYKv/+TnvJa1whfNsG6WFKWmzONk9DGZNOa8pe+AJCCaGXlz9T+4eqMXU92m37+f8L4SsjTH7pAicBcA9odQIDAQAB";



    public static Map<String, String> addSign(Map<String, String> params) {

        if (params != null && params.size() > 0) {
            params.put("channelID", ApiFacade.getInstance().getChannel());
            String[] keyList = {"deviceID", "osVer", "salt1", "bundleID", "appVer", "channelID", "salt2", "timestamp"};

            StringBuilder paramsStr = new StringBuilder();
            for (int i = 0; i < keyList.length; i++) {

                String key = keyList[i];
                paramsStr.append(key);
                paramsStr.append('=');


                String value = params.get(key);
                if (StringUtils.isEmpty(value)) {
                    value = "";
                }

                switch (key) {
                    case "salt1":
                        value = salt1;
                        break;
                    case "salt2":
                        value = salt2;
                        break;
                    case "timestamp":
                        value = System.currentTimeMillis() / 1000 + "";
                        break;
                }

                paramsStr.append(value);
                if (i != keyList.length - 1) {
                    paramsStr.append('&');
                }
            }

            String result = paramsStr.toString();

            byte[] signBytes = sign(result);

            if (signBytes != null) {
                params.put("sign", new String(signBytes));
            } else {
                params.put("sign", "");
            }
        }

        return params;
    }


    public static byte[] sign(String content) {
        return sign(content.getBytes(), publicKey);
    }

    private static byte[] sign(byte[] content, String publicKey) {

        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            RSAPublicKey rsaPublicKey = loadPublicKeyByStr(publicKey);

            if (rsaPublicKey == null) {
                return null;
            }

            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            byte[] output = cipher.doFinal(content);
            return Base64Utils.encode(output, android.util.Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            Log.i(TAG, "sign: 无此加密算法" + e.toString());
            return null;
        } catch (NoSuchPaddingException e) {
            Log.i(TAG, "sign: NoSuchPaddingException" + e.toString());
            return null;
        } catch (InvalidKeyException e) {
            Log.i(TAG, "sign: 加密公钥非法" + e.toString());
            return null;
        } catch (Exception e) {
            Log.i(TAG, "sign:" + e.toString());
            return null;
        }
    }

    private static RSAPublicKey loadPublicKeyByStr(String publicKeyStr) {
        try {
            byte[] buffer = android.util.Base64.decode(publicKeyStr, android.util.Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            Log.i(TAG, "loadPublicKeyByStr: 无此算法" + e.toString());
            return null;
        } catch (InvalidKeySpecException e) {
            Log.i(TAG, "loadPublicKeyByStr: 公钥非法" + e.toString());
            return null;
        } catch (NullPointerException e) {
            Log.i(TAG, "loadPublicKeyByStr: 公钥数据为空" + e.toString());
            return null;
        }
    }

}
