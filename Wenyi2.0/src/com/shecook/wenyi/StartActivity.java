package com.shecook.wenyi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioGroup;

import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.cookbook.CookbookFragment;
import com.shecook.wenyi.essay.WelcomeFragment;
import com.shecook.wenyi.group.GroupFragment;
import com.shecook.wenyi.mainpackage.FragmentTabAdapter;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.personal.PersonalFragment;
import com.shecook.wenyi.piazza.PiazzaFragment;
import com.shecook.wenyi.util.Util;

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

	public static JSONObject welcomeData;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bottom_navigation);

		getTokenFrom(false,tokenResultListener,tokenErrorListener);
	}
	
	public void initView(){
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
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case 1:
				getCatalog(HttpUrls.ESSAY_WENYI_LIST_CATALOG, null, catalogResultListener, catalogErrorListener);
				break;

			default:
				break;
			}
		};
	};
	
	public static JSONObject getWelcomeData() {
		return welcomeData;
	}

	public void setWelcomeData(JSONObject welcomeData) {
		this.welcomeData = welcomeData;
	}
	
	Listener<JSONObject> tokenResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG, "tokenResultListener onResponse -> " + result.toString());
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
							Util.saveUserData(StartActivity.this, user);
							handler.sendEmptyMessage(1);
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
	
	ErrorListener tokenErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	Listener<JSONObject> catalogResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG, "catalogResultListener onResponse -> " + result.toString());
			setWelcomeData(result);
			initView();
		}
	};
	
	ErrorListener catalogErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	
}
