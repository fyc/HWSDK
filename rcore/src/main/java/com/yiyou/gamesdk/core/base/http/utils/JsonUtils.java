package com.yiyou.gamesdk.core.base.http.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: luoweiqiang
 * Date: 13-11-28
 */
public class JsonUtils {

	public static String getString(JSONObject json, String name) {
		return getString(json, name, "");
	}

	public static String getString(JSONObject json, String name, String defaultValue) {
		if (json.isNull(name)) {
			return defaultValue;
		}
		try {
			return json.getString(name);
		}
		catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Integer getInt(JSONObject json, String name,Integer defaultValue) {
		if (json.isNull(name)) {
			return defaultValue;
		}
		try {
			return json.getInt(name);
		}
		catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}	

	public static Double getDouble(JSONObject json, String name) {
		return getDouble(json, name, 0.0);
	}

	public static Double getDouble(JSONObject json, String name, Double defaultValue) {
		if (json.isNull(name)) {
			return defaultValue;
		}
		try {
			return json.getDouble(name);
		}
		catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Boolean getBoolean(JSONObject json, String name, boolean defaultValue) {
		if (json.isNull(name)) {
			return defaultValue;
		}
		try {
			return json.getBoolean(name);
		}
		catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static JSONObject getJSONObject(JSONObject json, String name, JSONObject defaultValue) throws JSONException {
		if (json.isNull(name)) {
			return defaultValue;
		}

		return json.getJSONObject(name);
		
	}
}
