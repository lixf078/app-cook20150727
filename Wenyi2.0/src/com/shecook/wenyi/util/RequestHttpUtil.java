package com.shecook.wenyi.util;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class RequestHttpUtil {
	public static final String TAG = "RequestHttpUtil";
	public static String mid = "";
	public static String token = "";

	public static void getHttpData(Activity activity, String url, JSONObject jsonObject, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		WenyiUser user = Util.getUserData(activity);
		
		JSONObject sub = new JSONObject();
		
		if("".equals(mid)){
			if (null == user || TextUtils.isEmpty(mid = user.get_mID())) {
				mid = UUID.randomUUID().toString();
			}
		}
		try {
			sub.put("mtype", "android");
			sub.put("mid", mid);
			sub.put("token", user.get_token());
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
