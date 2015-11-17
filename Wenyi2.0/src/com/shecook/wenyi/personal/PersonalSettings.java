package com.shecook.wenyi.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.shecook.wenyi.BaseActivity;
import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.WebViewActivity;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.util.Util;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;

public class PersonalSettings extends BaseActivity implements OnClickListener {

	private TextView login;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private ListView listView;
	private String[] datas1 = { "修改邮箱", "修改密码", "清除缓存", "关于文怡", "文怡美食生活馆",
			"版本信息" };

	private String[] datas2 = { "账号绑定", "清除缓存", "关于文怡", "文怡美食生活馆", "版本信息" };

	private String[] datas3 = { "清除缓存", "关于文怡", "文怡美食生活馆", "版本信息" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.center_set);
		super.onCreate(savedInstanceState);

		login = (TextView) findViewById(R.id.exit_login_current_account);
		if (isLogin()) {
			login.setText(R.string.user_logout);
		}else{
			login.setText(R.string.user_login);
		}
		listView = (ListView) findViewById(R.id.listView);
		findViewById(R.id.right_img).setVisibility(View.GONE);
		TextView middle = (TextView) findViewById(R.id.middle_title);
		middle.setText("设置");

		// 点击登录
		login.setOnClickListener(this);
		userOperator(HttpUrls.PERSONAL_USER_SET, null, userdataResultListener, userdataErrorListener);
	}

	private void initList() {
		String[] datas = null;
		if(account_status == 10000){
			datas = datas1;
		}else if(account_status == 10001){
			datas = datas2;
		}else if(account_status == 10002){
			datas = datas3;
		}
		for (int i = 0; i < datas.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", datas[i]);
			map.put("img", R.drawable.ico_pull_03);
			list.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(PersonalSettings.this, list,
				R.layout.center_set_item, new String[] { "title", "img" },
				new int[] { R.id.title, R.id.imgs });
		listView.setAdapter(adapter);
		listView.setDivider(null);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int id,
					long position) {
				Intent intent = new Intent();
				switch (id) {
				case 0:
					if(account_status == 10000){
						 intent.setClass(PersonalSettings.this, PersonalEmailChange.class);
						 intent.putExtra("eventtype", 1);
					}else if(account_status == 10001){
						intent.setClass(PersonalSettings.this, PersonalEmailChange.class);
						intent.putExtra("eventtype", 3);
					}else if(account_status == 10002){
						// 清除缓存
					}
					startActivity(intent);
					finish();
					break;
				case 1:
					if(account_status == 10000){
						 intent.setClass(PersonalSettings.this, PersonalEmailChange.class);
						 intent.putExtra("eventtype", 2);
					}else if(account_status == 10001){
						// 清除缓存
					}else if(account_status == 10002){
						// 关于文怡
						intent = new Intent(PersonalSettings.this, WebViewActivity.class);
						intent.putExtra("weburl", "http://wenyijcc.com/other/wenyi.aspx");
					}
					startActivity(intent);
					finish();
					break;
				case 2:
					if(account_status == 10000){
						// 清除缓存
					}else if(account_status == 10001){
						// 关于文怡
						intent = new Intent(PersonalSettings.this, WebViewActivity.class);
						intent.putExtra("weburl", "http://wenyijcc.com/other/wenyi.aspx");
					}else if(account_status == 10002){
						// 文怡美食生活馆 http://wenyijcc.com/other/kitchen.aspx
						intent = new Intent(PersonalSettings.this, WebViewActivity.class);
						intent.putExtra("weburl", "http://wenyijcc.com/other/kitchen.aspx");
					}
					startActivity(intent);
					finish();
					break;
				case 3:
					if(account_status == 10000){
						// 清除缓存
					}else if(account_status == 10001){
						// 文怡美食生活馆
						intent = new Intent(PersonalSettings.this, WebViewActivity.class);
						intent.putExtra("weburl", "http://wenyijcc.com/other/kitchen.aspx");
					}else if(account_status == 10002){
						// 版本信息
						intent.setClass(PersonalSettings.this, PersonalAppVersionActivity.class);
					}
					startActivity(intent);
					break;
				case 4:
					if(account_status == 10000){
						// 文怡美食生活馆
						intent = new Intent(PersonalSettings.this, WebViewActivity.class);
						intent.putExtra("weburl", "http://wenyijcc.com/other/kitchen.aspx");
					}else if(account_status == 10001){
						// 版本信息
						intent.setClass(PersonalSettings.this, PersonalAppVersionActivity.class);
					}
					startActivity(intent);
					break;
				case 5:
					if(account_status == 10000){
						// 版本信息
						intent.setClass(PersonalSettings.this, PersonalAppVersionActivity.class);
					}
					startActivity(intent);
					finish();
					break;
				}
			}
		});
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				// 初始化列表
				initList();
			case HttpStatus.STATUS_LOAD_OTHER:
				break;
			case HttpStatus.STATUS_ERROR:
				
				break;
			case HttpStatus.USER_LOGOUT_SUCCESS:
				finish();
			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.exit_login_current_account:
			if (isLogin()) {
				String type = user.get_login_type();
				if ("qq".equals(type)) {
					logout(PersonalSettings.this, PersonalLoginCommon.class,
							SHARE_MEDIA.QQ);
				} else if ("sina".equals(user.get_login_type())) {
					logout(PersonalSettings.this, PersonalLoginCommon.class,
							SHARE_MEDIA.SINA);
				} else {
					logoutSucess();
				}
			} else {
				Intent intent = new Intent();
				intent.setClass(PersonalSettings.this,
						PersonalLoginCommon.class);
				startActivity(intent);
				finish();
			}
			break;
		default:
			break;
		}
	}

	public void logout(final Activity from, final Class to, SHARE_MEDIA media) {
		mController.deleteOauth(PersonalSettings.this, media,
				new SocializeClientListener() {

					@Override
					public void onStart() {

					}

					@Override
					public void onComplete(int arg0, SocializeEntity arg1) {

					}
				});
		mController.loginout(PersonalSettings.this,
				new SocializeClientListener() {

					@Override
					public void onStart() {

					}

					@Override
					public void onComplete(int status, SocializeEntity entity) {
						if (status == 200) {
							logoutSucess();
						}
					}
				});
	}

	private void logoutSucess() {
		userOperator(HttpUrls.PERSONAL_USER_LOGOUT, null, logoutResultListener,
				logoutCardErrorListener);
	}

	Listener<JSONObject> logoutResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.e(TAG,
					"userCardResultListener onResponse -> " + result.toString());
			String response = result.toString();
			if (!TextUtils.isEmpty(response)) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int statuscode = jsonObject.getInt("statuscode");
					if (statuscode == HttpStatus.STATUS_OK) {
						JSONObject dataJson = jsonObject.getJSONObject("data");
						int core_status = dataJson.getInt("core_status");
						if (core_status == 200) {
							Toast.makeText(PersonalSettings.this,
									getString(R.string.user_loginout),
									Toast.LENGTH_SHORT).show();
							login.setText(R.string.user_login);
							Util.updateBooleanData(PersonalSettings.this,
									"islogin", false);
							isLogin = false;
							handler.sendEmptyMessage(HttpStatus.USER_LOGOUT_SUCCESS);
						} else {
							// 有错误情况
						}
					} else if (statuscode == HttpStatus.USER_NOT_LOGIN) {
						Intent intent = new Intent(
								"com.shecook.wenyi.personal.personallogin");
						startActivityForResult(intent, 1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
			}

		}
	};

	ErrorListener logoutCardErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	Listener<JSONObject> userdataResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
			initUserData(result, 0);

		}
	};

	ErrorListener userdataErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	private int account_status = -1;

	private void initUserData(JSONObject jsonObject, int flag) {

		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");
						account_status = data.getInt("account_status");
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
					}
				} else {
					Toast.makeText(PersonalSettings.this,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			handler.sendEmptyMessage(HttpStatus.STATUS_ERROR);
		}
	}

}
