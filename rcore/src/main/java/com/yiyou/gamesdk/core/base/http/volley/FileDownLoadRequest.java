package com.yiyou.gamesdk.core.base.http.volley;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley1.DefaultRetryPolicy;
import com.android.volley1.NetworkResponse;
import com.android.volley1.Request;
import com.android.volley1.Response;
import com.android.volley1.VolleyError;
import com.android.volley1.toolbox.HttpHeaderParser;
import com.yiyou.gamesdk.core.base.http.volley.listener.FileDownListener;
import com.yiyou.gamesdk.core.base.http.volley.listener.ProgressListener;
import com.mobilegamebar.rsdk.outer.util.FileUtils;
import com.mobilegamebar.rsdk.outer.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by chenshuide on 15/8/4.
 */
public class FileDownLoadRequest extends Request<Void> {

    /**
     * 超时时间
     */
    private static final int DEFAULT_TIMEOUT = 16000;

    /**
     * 重试次数
     */
    private static final int DEFAULT_MAX_RETRIES = 1;

    private static final String TAG = "RSDK: " + "FileDownLoadRequest";
    private static final String TEMP = "temp";
    private static final int MAX_BUFFER_SIZE = 1024 * 10;
    private File mSavePath, mSaveFile, tempFile;
    private FileDownListener mListener;
    private String md5;


    public FileDownLoadRequest(@NonNull String url, @NonNull File saveFilePath, @NonNull String fileName, FileDownListener listener) {
        this(url, saveFilePath, fileName, "", listener);
    }

    public FileDownLoadRequest(@NonNull String url, @NonNull File saveFilePath, @NonNull String fileName, String fileMd5, FileDownListener listener) {
        super(Method.DEPRECATED_GET_OR_POST, url, null);
        setShouldCache(false);
        setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DEFAULT_MAX_RETRIES, 1f));

        setProgressListener(listener);
        mSavePath = saveFilePath;

        tempFile = new File(mSavePath, TEMP);
        mSaveFile = new File(mSavePath, fileName);

        mListener = listener;
        md5 = fileMd5;
    }


    private boolean onProgress(InputStream in, File dest, ProgressListener listener, String length) {
        boolean isSucc = false;
        FileOutputStream fout = null;
        long total;
        try {
            total = Long.parseLong(length);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            total = 0;
        }


        try {
            if (!dest.exists()) {
                dest.createNewFile();
            }


            fout = new FileOutputStream(dest);

            listener.onStart(total);

            byte[] buf = new byte[MAX_BUFFER_SIZE];
            int n = -1;
            long curPos = 0;
            while ((n = in.read(buf)) > 0) {
                curPos = curPos + n;
                listener.onProgress(curPos, total);
                fout.write(buf, 0, n);
                fout.flush();
            }

            listener.onFinish(curPos);
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


    @Override
    protected Response<Void> parseNetworkResponse(NetworkResponse response) {
        byte[] data = response.data;
        String length = response.headers.get("Content-Length");

        boolean isSucc;

        if (data == null) {
            InputStream source = response.source;
            ProgressListener progressListener = getProgressListener();

            if (progressListener != null) {
                isSucc = onProgress(source, tempFile, progressListener, length);
            } else {

                isSucc = FileUtils.copytoFile(source, tempFile);

            }


        } else {
            isSucc = FileUtils.copyByte2File(data, tempFile);

        }


        if (TextUtils.isEmpty(md5)) {
            //不用检验文件
        } else {
            String md5_file = FileUtils.getFileMD5String(tempFile);
            Log.d(TAG, "the file md5 is " + md5_file);
            isSucc = md5.equals(md5_file);
        }

        if (isSucc) {
            return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
        } else {

            return Response.error(new VolleyError("file download error"));
        }

    }

    @Override
    protected void deliverResponse(Void response) {

        tempFile.renameTo(mSaveFile);

        if (mListener != null) {
            mListener.onDownLoadSucc(mSaveFile);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        String errDesc;
        if (error.networkResponse != null) {
            errDesc = error.getClass().getSimpleName() + " code=  " + error.networkResponse.statusCode + "   msg=  " + error.getMessage();
        } else {
            errDesc = error.getClass().getSimpleName() + " " + error.getMessage();
        }

        Log.d(TAG, "errmsg= " + errDesc);

        tempFile.delete();

        if (mListener != null)
            mListener.onDownLoadFail(errDesc);

    }


    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }
}
