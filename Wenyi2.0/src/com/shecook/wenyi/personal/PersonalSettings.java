package com.shecook.wenyi.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
	private String[] dates = { "修改邮箱", "修改密码", "清除缓存", "关于文怡", "文怡美食生活馆",
			"版本信息" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.center_set);
		super.onCreate(savedInstanceState);

		login = (TextView) findViewById(R.id.exit_login_current_account);
		if (isLogin()) {
			login.setText(R.string.user_logout);
		}
		listView = (ListView) findViewById(R.id.listView);

		// 初始化列表
		initList();

		// 点击登录
		login.setOnClickListener(this);
	}

	private void initList() {
		for (int i = 0; i < dates.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", dates[i]);
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
				switch (id) {
				case 0:
					Intent intent = new Intent();
					// intent.setClass(PersonalSettings.this,
					// CenterAboutWenyiActivity.class);
					startActivity(intent);
					finish();
					break;
				case 1:
					Intent intent1 = new Intent();
					// intent1.setClass(PersonalSettings.this,
					// CenterWenyiFoodActivity.class);
					startActivity(intent1);
					finish();
					break;
				case 2:
					Intent intent2 = new Intent();
					intent2.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse("http://www.shecook.com");
					intent2.setData(content_url);
					startActivity(intent2);
					finish();
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					Intent intent5 = new Intent();
					// intent5.setClass(PersonalSettings.this,
					// CenterIdeaActivity.class);
					startActivity(intent5);
					finish();
					break;
				case 6:
					Intent intent6 = new Intent();
					// intent6.setClass(PersonalSettings.this,
					// CenterAboutMeActivity.class);
					startActivity(intent6);
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
			case HttpStatus.STATUS_LOAD_OTHER_OK:
				break;
			case HttpStatus.STATUS_LOAD_OTHER:
				break;
			case Util.SHOW_DIALOG_COMMENTS:
				break;
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

	Listener<JSONObject> commentsResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
			initCommentsData(result, 0);

		}
	};

	ErrorListener commentsErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	private int account_status = -1;

	private void initCommentsData(JSONObject jsonObject, int flag) {

		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");
						account_status = data.getInt("account_status");
					}
				} else {
					Toast.makeText(PersonalSettings.this,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			handler.sendEmptyMessage(HttpStatus.STATUS_LOAD_OTHER_OK);
		}
	}

}
