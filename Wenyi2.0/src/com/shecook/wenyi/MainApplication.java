package com.shecook.wenyi;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.CrashHandler;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class MainApplication extends Application {
	public static final String TAG = "MainApplication";
	// 单例模式
	private static MainApplication appContext;
	VolleyUtils mVolleyUtils = null;
	private String mid = "";
	
	
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        mVolleyUtils = VolleyUtils.getInstance();
        mVolleyUtils.initVolley(this);
        // getToken();
    }
    
	public static MainApplication getInstance() {
		return appContext;
	}
	
	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/** 获取版本号 */
	public String getAppVerName() {
		return getPackageInfo().versionName;
	}
	
	public void getToken() {
		JSONObject jsonObject = new JSONObject();
		JSONObject sub = new JSONObject();
		if(TextUtils.isEmpty(Util.getUserData(MainApplication.this).get_mID())){
			mid = UUID.randomUUID().toString();
		}
		try {
			sub.put("mtype", "android");
			sub.put("mid", mid);
			jsonObject.put("common", sub);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Listener<JSONObject> resultListener = new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject result) {
				Log.d(TAG, "response -> " + result.toString());
				String response = result.toString();
				if(!TextUtils.isEmpty(response)){
					try {
						JSONObject jsonObject = new JSONObject(response);
						int statuscode = jsonObject.getInt("statuscode");
						if(statuscode == 200){
							JSONObject dataJson = jsonObject.getJSONObject("data");
							int core_status = dataJson.getInt("core_status");
							if(core_status == 200){
								WenyiUser user = new WenyiUser();
								user.set_flag(statuscode);
								user.set_mID(mid);
								user.set_token(dataJson.getString("token"));
								Util.saveUserData(MainApplication.this, user);
							}else{
								// 有错误情况
							}
						}else{
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
				}
				
				
			}
		};
		
		ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, error.getMessage(), error);
			}
		};
		JsonObjectRequest wenyiRequest = new JsonObjectRequest(Method.POST, 
				HttpUrls.GET_TOKEN, jsonObject, resultListener, errorListener);
		
		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}
	
}
