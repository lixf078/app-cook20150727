package com.shecook.wenyi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioGroup;

import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.cookbook.CookbookFragment;
import com.shecook.wenyi.essay.WelcomeFragment;
import com.shecook.wenyi.group.GroupFragment;
import com.shecook.wenyi.mainpackage.FragmentTabAdapter;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.model.factory.TokenFactory;
import com.shecook.wenyi.personal.PersonalFragment;
import com.shecook.wenyi.piazza.PiazzaFragment;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.net.NetResult;
import com.shecook.wenyi.util.volleybox.VolleyUtils;
import com.shecook.wenyi.util.volleybox.WenyiJSONObjectRequest;

public class StartActivity extends BaseActivity {

	public static final String TAG = "StartActivity";
	/*
	 * 1,检查版本是否需要更新 2,判断启动方式（通知，桌面等）
	 */
	/**
	 * Called when the activity is first created.
	 */
	private RadioGroup rgs;
	public List<Fragment> fragments = new ArrayList<Fragment>();

	public String hello = "hello ";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bottom_navigation);

		getToken(false);

		fragments.add(new WelcomeFragment());
		fragments.add(new CookbookFragment());
		fragments.add(new PiazzaFragment());
		fragments.add(new GroupFragment());
		fragments.add(new PersonalFragment());

		rgs = (RadioGroup) findViewById(R.id.tabs_rg);

		FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments,
				R.id.wenyi_tab_content, rgs);
		tabAdapter
				.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
					@Override
					public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
							int checkedId, int index) {
						System.out.println("Extra---- " + index
								+ " checked!!! ");
					}
				});
		
		
		JSONObject jsonObject = new JSONObject();
		JSONObject sub = new JSONObject();
		if (TextUtils.isEmpty(user.get_mID())) {
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
			public void onResponse(JSONObject response) {
				Log.d("lixufeng", "onResponse " + response.toString());
			}
		};

		ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, error.getMessage(), error);
			}
		};
		JsonObjectRequest wenyiRequest = new JsonObjectRequest(
				Method.POST, HttpUrls.ESSAY_WENYI_LIST, jsonObject, resultListener, errorListener);

		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	
		// getCatalog(HttpUrls.ESSAY_WENYI_LIST, jsonObject, resultListener, errorListener);
	}
	
	public void getCatalog(String url, JSONObject jsonObject, Listener<NetResult> resultListener, ErrorListener errorListener) {
		WenyiUser user = Util.getUserData(this);
		/*RequestQueue requestQueue = Volley.newRequestQueue(getActivity()
				.getApplicationContext());*/
		JSONObject sub = new JSONObject();
		if (TextUtils.isEmpty(user.get_mID())) {
			mid = UUID.randomUUID().toString();
		}
		try {
			sub.put("mtype", "android");
			sub.put("mid", mid);
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
