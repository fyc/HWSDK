package com.yiyou.gamesdk.testapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by levyyoung on 14/10/29.
 */
public class ByteUtils {

    public static String bytesToDisplayHexString(byte[] src){
        if (src == null || src.length <= 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < src.length; i++) {
            stringBuilder.append("0x");
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static String bytesToHexString(byte[] src){
        if (src == null || src.length <= 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    //byte 与 int 的相互转换 0~255
    public static byte uintToByte(int x) {
        return (byte) x;
    }

    public static int byteToUint(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    //byte 数组与 int 的相互转换
    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    //byte 数组与 long 的相互转换
    public static byte[] longToBytes(long x) {
        return new byte[]{
                (byte) (x >>> 56),
                (byte) (x >>> 48),
                (byte) (x >>> 40),
                (byte) (x >>> 32),
                (byte) (x >>> 24),
                (byte) (x >>> 16),
                (byte) (x >>> 8),
                (byte) x
        };
    }

    public static long bytesToLong(byte[] bytes) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            buffer[i] = (i < bytes.length) ? bytes[i] : 0;
        }
        return (buffer[0] << 56)
                + ((buffer[1] & 0xFF) << 48)
                + ((buffer[2] & 0xFF) << 40)
                + ((buffer[3] & 0xFF) << 32)
                + ((buffer[4] & 0xFF) << 24)
                + ((buffer[5] & 0xFF) << 16)
                + ((buffer[6] & 0xFF) << 8)
                + (buffer[7] & 0xFF);
    }

    public static byte[] bytesXOR(byte[] srcBytes, byte[] keyBytes) {
        if (srcBytes != null && srcBytes.length > 0
                && keyBytes != null && keyBytes.length > 0) {
            byte[] result = new byte[srcBytes.length];
            int count = 0;
            for (int i = 0; i < srcBytes.length; i++) {
                count = i % keyBytes.length;
                result[i] = (byte) (srcBytes[i] ^ keyBytes[count]);
            }
            return result;
        }
        return null;
    }


    /**
     * 明文转md5 －> hex
     *
     * @param rawPWD
     * @return md5 and hex
     */
    public static String formatPWD(String rawPWD) {
        String formattedPWD = rawPWD;
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            md.update(formattedPWD.getBytes());
            byte[] md5 = md.digest();
            formattedPWD = ByteUtils.bytesToHexString(md5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return formattedPWD;
    }

    public static String generateMd5(String source) {

        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            md.update(source.getBytes());
            return bytesToHexString(md.digest());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String generateMd5V2(String source) {

        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            md.update(source.getBytes());
            return Base64.encode(md.digest());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }


}
