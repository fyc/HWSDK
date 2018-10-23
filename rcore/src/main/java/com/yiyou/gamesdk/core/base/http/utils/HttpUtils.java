package com.yiyou.gamesdk.core.base.http.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.mobilegamebar.rsdk.outer.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * Created by luoweiqiang Date: 15-05-08
 */
public class HttpUtils {

	private static final String DEFAULT_CHAREST = "UTF-8";
	private static final Uri URI_PREFERAPN = Uri.parse("content://telephony/carriers/preferapn");;
	private static final String TAG = "RSDK:HttpUtils";

//	public static boolean isMultipart(List<HttpParameter> params) {
//		for (HttpParameter httpParameter : params) {
//			if (httpParameter.value instanceof FileHolder) {
//				return true;
//			}
//		}
//		return false;
//	}

	public static boolean isConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isWiFiActive(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected() && info.getTypeName().equalsIgnoreCase("WIFI")) {
				// 判断当前网络是否WIFI连接
				return true;
			}
		}
		return false;
	}

	public static String getApnName(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null) {
				// 判断当前网络是否已经连接
				return info.getExtraInfo();
			}
		}
		return null;
	}

	public static Proxy getAPNProxy(Context context, URI uri) {
		Proxy proxy = Proxy.NO_PROXY;
		if (Build.VERSION.SDK_INT >= 11) {
			ProxySelector defaultProxySelector = ProxySelector.getDefault();
			List<Proxy> proxyList = defaultProxySelector.select(uri);
			if (proxyList != null && proxyList.size() > 0) {
				proxy = proxyList.get(0);
			}
		}else{
			 NetworkInfo localNetworkInfo = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
			 if(localNetworkInfo!=null&&!localNetworkInfo.getTypeName().equalsIgnoreCase("WIFI"))
			 {
				 Cursor localCursor = null;
				 String proxyIp = null;
				 int port;
				 try{
					 localCursor = context.getContentResolver().query(URI_PREFERAPN, null, null, null, null);
					 if(localCursor!=null&&localCursor.moveToFirst())
					 {
						 proxyIp = localCursor.getString(localCursor.getColumnIndex("proxy"));
						 port = localCursor.getInt(localCursor.getColumnIndex("port"));
						 if(!TextUtils.isEmpty(proxyIp))
						 {
							 SocketAddress addr = new InetSocketAddress(proxyIp,port);
							 proxy = new Proxy(Proxy.Type.HTTP, addr);
						 }
					 }
				 }catch (Exception e){
					 e.printStackTrace();
				 }finally{
					 if(localCursor!=null)
						 localCursor.close();
				 }
			 }
		}
		return proxy;
	}

	public static String nvl(Object o) {
		return o == null ? "" : o.toString();
	}

	public static byte[] getGZippedContent(String path) {
		byte[] byteArray = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
		InputStream fis = null;
		BufferedInputStream bis = null;
		GZIPOutputStream out = null;
		try {
			out = new GZIPOutputStream(bos);
			fis = new FileInputStream(path);
			bis = new BufferedInputStream(fis);
			byte[] buf = new byte[4096];
			int cb = -1;
			while ((cb = fis.read(buf)) != -1) {
				out.write(buf, 0, cb);
			}
			out.finish();
			out.flush();
			byteArray = bos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			tryClose(bis);
			tryClose(out);
		}
		return byteArray;
	}

	public static String getDir(String filePath) {
		int lastSlastPos = filePath.lastIndexOf('/');
		return filePath.substring(0, lastSlastPos);
	}

	public static void tryClose(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getUrlEncodeStr(String str) {
		try {
			return URLEncoder.encode(str, DEFAULT_CHAREST);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

//	http://192.168.9.232:18080/sdk.server/
	private static final String DEFAULT_HOST = "www.baidu.com";
	private static final String DEFAULT_PORT = "80";
	private static final String DEFAULT_IP = "180.97.33.107";
	private static String sServiceIpAddress = null;

	public static String getServiceBaseUrl() {
//		return "http://" + getServiceAddressByName() + ":" + DEFAULT_PORT;
		return "http://192.168.9.232:18080/sdk.server";
	}

	public static String getServiceAddressByName() {
		return getServiceAddressByName(DEFAULT_HOST, DEFAULT_IP);
	}

	/**
	 * @param host
	 *            服务器域名
	 * @param defaultIp
	 *            客户端hardcode的服务器ip地址，如果通过域名获取ip失败，将使用此地址
	 * @return
	 */
	public static String getServiceAddressByName(String host, String defaultIp) {

		if (TextUtils.isEmpty(sServiceIpAddress)) {
			try {
				InetAddress inetAddress = InetAddress.getByName(host);
				sServiceIpAddress = inetAddress.getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				sServiceIpAddress = defaultIp;
			}
		}
		return sServiceIpAddress;
	}
	
	/**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     */
    public static String hashKey(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }
    
    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     */
    public static String hashKey(byte[] bytes) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(bytes);
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(bytes.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

	/**
	 * 构建http get url
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	public static String buildUrl(String url, Map<String, String> params) {
		StringBuffer urlBuffer = new StringBuffer(url);
		if (!url.endsWith("?")) {
			urlBuffer.append("?");
		}

		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue() == null ? "" : entry.getValue();
			if (!urlBuffer.toString().endsWith("?")) {
				urlBuffer.append("&");
			}
			urlBuffer.append(key).append("=");
			try {
				value = URLEncoder.encode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				try {
					value = URLEncoder.encode(value, "GBK");
				} catch (UnsupportedEncodingException e1) {
				}
			}
			urlBuffer.append(value);

		}
		Log.d(TAG,"buildUrl: " +urlBuffer.toString());
		return urlBuffer.toString();

	}
}
