package com.shecook.wenyi;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.RequestQueue;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.toolbox.Volley;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.model.factory.TokenFactory;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.net.NetResult;
import com.shecook.wenyi.util.volleybox.VolleyUtils;
import com.shecook.wenyi.util.volleybox.WenyiJSONObjectRequest;

public class BaseFragmeng extends Fragment {

	public static final String TAG = "BaseFragmeng";
	String mid = "";

	public void getToken(boolean force) {
		WenyiUser user = Util.getUserData(getActivity());
		if (!force && user.get_token() != null) {
			return;
		}
		RequestQueue requestQueue = Volley.newRequestQueue(getActivity()
				.getApplicationContext());
		JSONObject jsonObject = new JSONObject();
		
		JSONObject sub = Util.getCommonParam(getActivity());
		try {
			jsonObject.put("common", sub);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Listener<NetResult> resultListener = new Listener<NetResult>() {

			@Override
			public void onResponse(NetResult result) {
				Log.d(TAG, "response -> " + result.toString());
				String response = result.toString();
				if (!TextUtils.isEmpty(response)) {
					try {
						JSONObject jsonObject = new JSONObject(response);
						int statuscode = jsonObject.getInt("statuscode");
						if (statuscode == 200) {
							JSONObject dataJson = jsonObject
									.getJSONObject("data");
							int core_status = dataJson.getInt("core_status");
							if (core_status == 200) {
//								WenyiUser user = new WenyiUser();
//								user.set_flag(statuscode);
//								user.set_mID(mid);
//								user.set_token(dataJson.getString("token"));
//								Util.saveUserData(getActivity(), user);
								
								Util.updateIntData(getActivity(), "_flag", statuscode);
								Util.updateStringData(getActivity(), "_token", dataJson.getString("token"));
								Util.updateStringData(getActivity(), "_mid", mid);
							} else {
								// 有错误情况
							}
						} else {
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
				}

			}
		};

		ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, error.getMessage(), error);
			}
		};
		WenyiJSONObjectRequest wenyiRequest = new WenyiJSONObjectRequest(
				Method.POST, HttpUrls.GET_TOKEN, jsonObject.toString(),
				new TokenFactory(), resultListener, errorListener);

		requestQueue.add(wenyiRequest);
	}
	
	public void getCatalog(String url, JSONObject jsonObject, Listener<NetResult> resultListener, ErrorListener errorListener) {

		if(null == jsonObject){
			jsonObject = new JSONObject();
		}
		
		JSONObject sub = Util.getCommonParam(getActivity());
		try {
			jsonObject.put("common", sub);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		WenyiJSONObjectRequest wenyiRequest = new WenyiJSONObjectRequest(
				Method.POST, url, jsonObject.toString(),
				new TokenFactory(), resultListener, errorListener);

		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
//		requestQueue.add(wenyiRequest);
	}
	
}
