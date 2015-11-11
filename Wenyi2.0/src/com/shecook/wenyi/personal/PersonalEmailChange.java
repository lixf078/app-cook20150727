package com.shecook.wenyi.personal;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
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

public class PersonalEmailChange extends BaseActivity implements
		OnClickListener {

	static final String TAG = "PersonalTopicListActivity";
	public static final int STATUS_OK_COMMENT_GROUP = 1;
	public static final int STATUS_OK_COMMENT_CREATE = 2;
	public static final int STATUS_OK_COMMENT_COLLECTED = 3;

	public EditText personal_change_edittext1,personal_change_edittext2;
	public TextView personal_change_textview,middleTextView;
	private int eventtype = -1; // 1 修改邮箱，2 修改密码， 3 账号绑定
	public ImageView right_img;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_change_email);
		eventtype = getIntent().getIntExtra("eventtype", 1);
		initView();
	}

	private void initView() {
		middleTextView = (TextView) findViewById(R.id.middle_title);
		
		personal_change_edittext1 = (EditText) findViewById(R.id.personal_change_edittext1);
		personal_change_edittext2 = (EditText) findViewById(R.id.personal_change_edittext2);
		personal_change_textview = (TextView) findViewById(R.id.personal_change_textview);
		personal_change_textview.setOnClickListener(this);
		if(eventtype == 1){
			personal_change_textview.setText("验证邮箱");
			personal_change_edittext2.setVisibility(View.GONE);
			middleTextView.setText("更改邮箱");
		}else if(eventtype == 2){
			personal_change_edittext1.setHint("原密码");
			personal_change_edittext2.setHint("新密码");
			personal_change_edittext2.setVisibility(View.VISIBLE);
			personal_change_textview.setText("确定修改");
			middleTextView.setText("更改密码");
		}else if(eventtype == 3){
			personal_change_edittext1.setHint("请输入邮箱");
			personal_change_edittext2.setHint("请输入密码");
			personal_change_edittext2.setVisibility(View.VISIBLE);
			personal_change_textview.setText("绑定账号");
		}
		
		right_img = (ImageView) findViewById(R.id.right_img);
		right_img.setVisibility(View.GONE);
		
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				break;
			case STATUS_OK_COMMENT_COLLECTED:
				break;
			case STATUS_OK_COMMENT_CREATE:
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.right_img:
			
			break;

		case R.id.return_img:
			finish();
			break;
		case R.id.personal_change_textview:
			processParam();
			break;
		default:
			break;
		}
	}
	
	

	Listener<JSONObject> changeEmailResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject jsonObject) {
			Log.d(TAG, "catalogResultListener onResponse -> " + jsonObject.toString());
			String msg = "";
			if (jsonObject != null) {
				try {
					if (!jsonObject.isNull("statuscode")
							&& 200 == jsonObject.getInt("statuscode")) {
						if (!jsonObject.isNull("data")) {
							JSONObject data = jsonObject.getJSONObject("data");
							int core_status = data.getInt("core_status");
							Log.e(TAG, "collectedResultListener core_status -> " + core_status);
							if (core_status == 200) {
								handler.sendEmptyMessage(STATUS_OK_COMMENT_COLLECTED);
								msg = "" + "信息更改成功！";
							} else {
								msg = "" + data.getString("msg");
							}
						}
					} else {
						msg = "" + jsonObject.getString("errmsg");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Toast.makeText(PersonalEmailChange.this, msg,
					Toast.LENGTH_SHORT).show();
		}
	};
	
	ErrorListener changeEmailErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	public void processParam(){
		JSONObject paramObject = new JSONObject();
		try {
			if(eventtype == 1){
				paramObject.put("email", "" + personal_change_edittext1.getEditableText().toString());
				Util.commonHttpMethod(PersonalEmailChange.this,	HttpUrls.PERSONAL_CHNAGE_EMAIL,
						paramObject, changeEmailResultListener,
						changeEmailErrorListener);
			}else if(eventtype == 2){
				paramObject.put("opwd", "" + personal_change_edittext1.getEditableText().toString());
				paramObject.put("npwd", "" + personal_change_edittext2.getEditableText().toString());
				Util.commonHttpMethod(PersonalEmailChange.this,	HttpUrls.PERSONAL_CHANGE_PASSWD,
						paramObject, changeEmailResultListener,
						changeEmailErrorListener);
			}else if(eventtype == 3){
				paramObject.put("email", "" + personal_change_edittext1.getEditableText().toString());
				paramObject.put("pwd", "" + personal_change_edittext2.getEditableText().toString());
				Util.commonHttpMethod(PersonalEmailChange.this,	HttpUrls.PERSONAL_CHANGE_BIND,
						paramObject, changeEmailResultListener,
						changeEmailErrorListener);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
