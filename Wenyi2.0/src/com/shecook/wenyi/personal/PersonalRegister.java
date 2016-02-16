package com.shecook.wenyi.personal;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.shecook.wenyi.BaseActivity;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.model.factory.RegistreFactory;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.net.NetResult;

public class PersonalRegister extends BaseActivity implements OnClickListener{
	public static final String TAG = "PersonalRegister";

	private ImageView registerImg;

	private EditText userName, password, email;

	AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.center_register);
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		registerImg = (ImageView) findViewById(R.id.register);
		userName = (EditText) findViewById(R.id.wenyi_user_nickname);
		password = (EditText) findViewById(R.id.wenyi_user_password);
		email = (EditText) findViewById(R.id.wenyi_user_email);
		registerImg.setOnClickListener(this);
	}

	String nickname = "";
	String useremail = "";
	String passwd = "";
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.register:
			String nickname = userName.getEditableText().toString();
			String useremail = email.getEditableText().toString();
			String passwd = password.getEditableText().toString();

			JSONObject jsonObject = new JSONObject();
			JSONObject paramSub = new JSONObject();
			JSONObject commonSub = new JSONObject();
			try {
				paramSub.put("loginid", useremail);
				paramSub.put("pwd", passwd);
				paramSub.put("nickname", nickname);
				
				paramSub.put("loginid", "694809649@qq.com");
				paramSub.put("pwd", "aaaaaa");
				paramSub.put("nickname", "ja_lxfqqqqq");
				
				commonSub.put("mtype", "android");
				commonSub.put("mid", "957aea62-3a50-49e3-9640-3824d38b2f45");
				commonSub.put("token", PersonalRegister.user.get_token());
				
				jsonObject.put("param", paramSub);
				jsonObject.put("common", commonSub);
				post(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			break;
		default:
			break;
		}
	}

	private void post(JSONObject jsonObject) {
		Log.d(TAG, "post start ");
		handler.sendEmptyMessage(Util.SHOW_DIALOG);
		RegistreFactory registreFactory = new RegistreFactory();
		basePost(HttpUrls.PERSONAL_REGISTER, jsonObject.toString(), registreFactory, resultListener,errorListener);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Util.SHOW_DIALOG:
				if (null == alertDialog) {
					alertDialog = Util
							.showLoadDataDialog(PersonalRegister.this);
				}
				if (!alertDialog.isShowing()) {
					Log.d(TAG, "SHOW_DIALOG");
					alertDialog.show();
				}
				break;
			case Util.DISMISS_DIALOG:
				String response = (String) msg.obj;
				if(!TextUtils.isEmpty(response)){
					try {
						JSONObject jsonObject = new JSONObject(response);
						int statuscode = jsonObject.getInt("statuscode");
						if(statuscode == 200){
							JSONObject dataJson = jsonObject.getJSONObject("data");
							int core_status = dataJson.getInt("core_status");
							if(core_status == 200){
								Toast.makeText(PersonalRegister.this, getText(R.string.login_success), Toast.LENGTH_SHORT).show();
								WenyiUser user = new WenyiUser();
								user.set_flag(statuscode);
								user.set_ID(dataJson.getString("uid"));
								user.set_token(dataJson.getString("token"));
								user.set_nickname(nickname);
								user.set_email(useremail);
								user.set_password(passwd);
								user.set_isLogin(true);
								Util.saveUserData(PersonalRegister.this, user);
								isLogin = true;
							}else{
								// 有错误情况
							}
						}else{
							isLogin = false;
							Toast.makeText(PersonalRegister.this, jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(PersonalRegister.this, getText(R.string.login_failed), Toast.LENGTH_SHORT).show();
				}
				
				if (alertDialog.isShowing()) {
					Log.d(TAG, "DISMISS_DIALOG");
					alertDialog.cancel();
				}
				finish();
				break;
			default:
				break;
			}
		};
	};
	
	Listener<NetResult> resultListener = new Listener<NetResult>() {

		@Override
		public void onResponse(NetResult response) {
			Log.d(TAG, "response -> " + response.toString());
			Message message = new Message();
			message.what = Util.DISMISS_DIALOG;
			message.obj = response.toString();
			handler.sendMessage(message);
		}
	};
	
	ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
}
