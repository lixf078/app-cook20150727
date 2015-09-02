package com.shecook.wenyi.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class RequestHttpUtil {
	public static final String TAG = "RequestHttpUtil";
	public static String mid = "";
	public static String token = "";

	public static void getHttpData(Activity activity, String url, JSONObject jsonObject, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		
		
		try {
			JSONObject sub = Util.getCommonParam(activity);
			if(null == jsonObject){
				jsonObject = new JSONObject();
			}
			jsonObject.put("common", sub);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.e("RequestHttpUtil", "getHttpData jsonObject " + jsonObject);
		JsonObjectRequest wenyiRequest = new JsonObjectRequest(
				Method.POST, url, jsonObject, resultListener, errorListener);
		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}
}
