package com.yiyou.gamesdk.util;

import android.support.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by shui on 15-10-10.
 */
public class AES {
    /**
     */
    private static byte[] iv = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5};
    private static final String CHARSET = "utf-8";


    public static String encrypt(String content,String aesKey) {
        byte[] encryptedData = new byte[0];
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(aesKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            encryptedData = cipher.doFinal(content.getBytes(CHARSET));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return Base64.encode(encryptedData);
    }

    public static String decrypt(String encrypted,String aesKey) {
        byte[] decryptedData;
        try {
            byte[] byteMi = Base64.decode(encrypted);
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(aesKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData, CHARSET);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static String encryptMap(@NonNull TreeMap<String, Object> enMap, String aesKey) {


        Set<String> set = enMap.keySet();
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (String item : set) {
            if (!first) {
                sb.append("&");
            } else {
                first = false;
            }
            sb.append(item);
            sb.append("=");
            sb.append(enMap.get(item));
        }
        String src = sb.toString();
        String encodeStr = encrypt(src,aesKey); // 加密

        return encodeStr;

    }


}
