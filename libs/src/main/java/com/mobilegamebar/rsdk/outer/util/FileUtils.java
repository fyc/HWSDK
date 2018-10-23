package com.mobilegamebar.rsdk.outer.util;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by chenshuide on 15/6/3.
 */
public class FileUtils {
    private static final int MAX_BUFFER_SIZE = 10 * 1024;
    private static final String TAG = "RSDK: "+"FileUtils";

    /**
     * copy file
     *
     * @param src  from
     * @param dest to
     * @return succ return true
     */
    public static boolean copyFile(File src, File dest) {
        boolean result = false;
        if (src == null || dest == null)
            return result;

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dest);

            int len = 0;
            byte[] buffer = new byte[MAX_BUFFER_SIZE];

            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

            result = true;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                if (fis != null)
                    fis.close();

                if (fos != null)
                    fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return result;

    }


    public static String getFileMD5String(File file) {
        MessageDigest messagedigest = null;
        try {
            messagedigest = MessageDigest.getInstance("MD5");
            FileInputStream in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                    file.length());
            messagedigest.update(byteBuffer);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferToHex(messagedigest.digest());
    }


    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }


    public static boolean compareFileMd5(File src, String md5) {

        try {
            FileInputStream fis = new FileInputStream(src);

            int len = 0;
            byte[] buffer = new byte[MAX_BUFFER_SIZE];

            while ((len = fis.read(buffer)) != -1) {

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }


    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }


    /**
     * @param in
     * @param dest
     */
    public static boolean copytoFile(InputStream in, File dest) {
        boolean isSucc = false;
        FileOutputStream fout = null;
        try {
            if (!dest.exists()) {
                dest.createNewFile();
            }


            fout = new FileOutputStream(dest);


            byte[] buf = new byte[MAX_BUFFER_SIZE];
            int n = -1;
            while ((n = in.read(buf)) > 0) {
                fout.write(buf, 0, n);
                fout.flush();
            }

            isSucc = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {

                if (fout != null) {
                    fout.close();
                }

                if (in != null) {
                    in.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return isSucc;
    }


    /**
     * 拷贝一级目录下文件
     *
     * @param srcDir
     * @param destDir
     * @return
     */
    public static boolean copyDir(@NonNull File srcDir, @NonNull File destDir) {

        boolean ret = false;

        File[] files = srcDir.listFiles();
        if (files == null || files.length == 0)
            return ret;

        for (File file : files) {

            if (file.isFile()) {
                File destFile = new File(destDir, file.getName());
                ret = copyFile(file, destFile);

            }
            // TODO: 15-9-9 需完善
        }

        return ret;

    }


    /**
     * 从assets copy file to dest
     *
     * @param srcPath
     * @param dest
     */
    public static boolean copyFile4Assets(Context context, String srcPath, File dest) {
        FileOutputStream fout = null;
        InputStream in = null;
        boolean isSucc = false;
        try {
            if (!dest.exists()) {
                dest.createNewFile();
            }

            in = context.getAssets().open(srcPath);
            fout = new FileOutputStream(dest);


            byte[] buf = new byte[MAX_BUFFER_SIZE];
            int n = -1;
            while ((n = in.read(buf)) > 0) {
                fout.write(buf, 0, n);
                fout.flush();
            }

            isSucc = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {

                if (fout != null) {
                    fout.close();
                }

                if (in != null) {
                    in.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return isSucc;
    }


    public static boolean copyByte2File(byte[] data, File dest) {
        boolean isSucc = false;
        try {
            if (!dest.exists())
                dest.createNewFile();

            FileOutputStream fos = new FileOutputStream(dest);

            fos.write(data);
            fos.flush();
            fos.close();
            isSucc = true;

        } catch (IOException e) {
            e.printStackTrace();
            isSucc = false;
        }

        return isSucc;
    }


}
