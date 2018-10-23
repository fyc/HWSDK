package com.yiyou.gamesdk.core.base.http.utils;

import org.apache.http.HttpStatus;

/**
 * Created by luoweiqiang
 * Date: 15-05-08
 */
@SuppressWarnings("deprecation")
public class HttpErrorCodeDef {
	
	public static final int HTTP_OK = HttpStatus.SC_OK;
	public static final int ERROR_NO_NETWORK = 1000;
	public static final int ERROR_CONNECTION_TIMEOUT = 1001;
	public static final int ERROR_READ_TIMEOUT = 1002;
	public static final int ERROR_READ_LOCAL_FILE_ERROR = 1003;
	public static final int ERROR_WRITE_DATA_TO_SERVICE_ERROR = 1004;
	public static final int ERROR_WRITE_DATA_TO_LOCAL_CACHE = 1005;
	public static final int ERROR_HANDLE_DATA_ERROR = 1006;
	public static final int ERROR_IOEXCEPTION = 1007;
	public static final int ERROR_URISYNTAXEXCEPTION = 1008;
	public static final int ERROR_SERVER = 1009;
	public static final int ERROR_NETWORK = 1010;
	public static final int ERROR_PARSE = 1011;
	public static final int ERROR_JSON_PARSE = 1012;
}
