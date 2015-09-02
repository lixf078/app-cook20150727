package com.shecook.wenyi.personal;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shecook.wenyi.BaseActivity;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.net.NetResult;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.SocializeUser;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.FetchUserListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;

public class PersonalLoginCommon extends BaseActivity implements OnClickListener{

	public static final String TAG = "PersonalLoginCommon";
	
	private TextView sinaLogin, qqLogin, shecookLogin, register;
	UMSocialService mController = UMServiceFactory.getUMSocialService(
			"com.umeng.login", RequestType.SOCIAL);
	
	private EditText userName, password, email;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.center_login);
		super.onCreate(savedInstanceState);
		initView();
	}

	public void initView(){
		password = (EditText) findViewById(R.id.wenyi_user_password);
		email = (EditText) findViewById(R.id.wenyi_user_email);
		register = (TextView) findViewById(R.id.register);
		sinaLogin = (TextView) findViewById(R.id.sinalogin);
		qqLogin = (TextView) findViewById(R.id.qqlogin);
		shecookLogin = (TextView) findViewById(R.id.login);
		
		sinaLogin.setOnClickListener(this);
		qqLogin.setOnClickListener(this);
		shecookLogin.setOnClickListener(this);
		register.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register:
			Intent intent = new Intent(this,PersonalRegister.class);
			startActivity(intent);
		case R.id.sinalogin:
		    sinaLogin();
			break;
		case R.id.qqlogin:
			qqLogin();
			break;
		case R.id.login:
			
			useremail = email.getEditableText().toString();
			passwd = password.getEditableText().toString();

			JSONObject jsonObject = new JSONObject();
			JSONObject paramSub = new JSONObject();
			JSONObject commonSub = new JSONObject();
			try {
				paramSub.put("loginid", useremail);
				paramSub.put("pwd", passwd);
				
				//paramSub.put("loginid", "694809649@qq.com");
				//paramSub.put("pwd", "aaaaaa");
				
				commonSub.put("mtype", "android");
				commonSub.put("mid", Util.getMid(PersonalLoginCommon.this));
				commonSub.put("token", user.get_token());// 注册，登陆之后可能会更改
				
				jsonObject.put("param", paramSub);
				jsonObject.put("common", commonSub);
				post(jsonObject);
			}catch(Exception e){
				
			}
			break;
		}
	}

	private void qqLogin() {
		mController.getConfig().supportQQPlatform(PersonalLoginCommon.this, HttpUrls.PERSONAL_LOGIN_3RD);
		mController.login(PersonalLoginCommon.this, SHARE_MEDIA.QQ,
				new SocializeClientListener() {

					@Override
					public void onStart() {

					}

					@Override
					public void onComplete(int status, SocializeEntity entity) {
						Log.d("onComplete", "" + status);

						if (status == 200) {
                            mController.getUserInfo(PersonalLoginCommon.this, new FetchUserListener() {

                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onComplete(int status, SocializeUser user) {
                                    uid = user.mLoginAccount.getUsid();
                                    nickname = user.mLoginAccount.getUserName();
                                    image = user.mLoginAccount.getAccountIconUrl();
                                    plat = "qq";
                                    Log.d("lixufeng", "onComplete uid " + uid + ", nickname " + nickname + ", image " + image);
                                    JSONObject jsonObject = new JSONObject();
                        			JSONObject paramSub = new JSONObject();
                        			JSONObject commonSub = new JSONObject();
                        			try {
                        				paramSub.put("connectid", uid);
                        				paramSub.put("plat", plat);
                        				paramSub.put("nickname", nickname);
                        				paramSub.put("portrait", image);
                        				paramSub.put("sex", "");

                        				commonSub.put("mtype", "android");
                        				commonSub.put("mid", "957aea62-3a50-49e3-9640-3824d38b2f45");
                        				commonSub.put("token", "24401ee0244c4655a52e3b7483661d09");// 注册，登陆之后可能会更改

                        				jsonObject.put("param", paramSub);
                        				jsonObject.put("common", commonSub);
                        				post3RD(jsonObject);
                        			}catch(Exception e){

                        			}
                                }
                            });
                        }
					}
				});
	}

	private void sinaLogin() {
        mController.login(PersonalLoginCommon.this, SHARE_MEDIA.SINA,
                new SocializeClientListener() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(int status, SocializeEntity entity) {
                        Log.d("onComplete", "" + status);
                        if (status == 200) {
                            mController.getUserInfo(PersonalLoginCommon.this, new FetchUserListener() {

                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onComplete(int status, SocializeUser user) {
                                    
                                    uid = user.mLoginAccount.getUsid();
                                    nickname = user.mLoginAccount.getUserName();
                                    image = user.mLoginAccount.getAccountIconUrl();
                                    plat = "sina";
                                    Log.d("lixufeng", "onComplete uid " + uid + ", nickname " + nickname + ", image " + image);
                                    JSONObject jsonObject = new JSONObject();
                        			JSONObject paramSub = new JSONObject();
                        			JSONObject commonSub = new JSONObject();
                        			try {
                        				paramSub.put("connectid", uid);
                        				paramSub.put("plat", plat);
                        				paramSub.put("nickname", nickname);
                        				paramSub.put("portrait", image);
                        				paramSub.put("sex", "");
                        				
                        				commonSub.put("mtype", "android");
                        				commonSub.put("mid", "957aea62-3a50-49e3-9640-3824d38b2f45");
                        				commonSub.put("token", PersonalLoginCommon.user.get_token());// 注册，登陆之后可能会更改
                        				
                        				jsonObject.put("param", paramSub);
                        				jsonObject.put("common", commonSub);
                        				post3RD(jsonObject);
                        			}catch(Exception e){
                        				
                        			}
                                    
                                }
                            });
                        }else{

                        }
                    }
                });
    }
	
	private String uid = "";
	private String nickname = "";
	private String plat = "";
	private String image = "";
	private String useremail = "";
	private String passwd = "";
	private void post(JSONObject jsonObject) {
		Log.d(TAG, "wenyi login post start ");
		handler.sendEmptyMessage(Util.SHOW_DIALOG);
//		RegistreFactory registreFactory = new RegistreFactory();
//		basePost(HttpUrls.PERSONAL_LOGIN, jsonObject.toString(), registreFactory, resultListener,errorListener);
		
		userOperator(HttpUrls.PERSONAL_LOGIN, jsonObject, loginResultListener, loginErrorListener);
	}
	
	private void post3RD(JSONObject jsonObject) {
		Log.d(TAG, "wenyi login post start ");
		handler.sendEmptyMessage(Util.SHOW_DIALOG);
//		RegistreFactory registreFactory = new RegistreFactory();
//		basePost(HttpUrls.PERSONAL_LOGIN_3RD, jsonObject.toString(), registreFactory, resultListener,errorListener);
		userOperator(HttpUrls.PERSONAL_LOGIN_3RD, jsonObject, loginResultListener, loginErrorListener);
	}

	
	Listener<JSONObject> loginResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.e(TAG,
					"userCardResultListener onResponse -> " + result.toString());
			String response = result.toString();
			if (!TextUtils.isEmpty(response)) {
				try {
					Log.d(TAG, "response -> " + response.toString());
					Message message = new Message();
					message.what = Util.DISMISS_DIALOG;
					message.obj = response.toString();
					handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
			}

		}
	};

	ErrorListener loginErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
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
	


	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Util.SHOW_DIALOG:
				if (null == alertDialog) {
					alertDialog = Util
							.showLoadDataDialog(PersonalLoginCommon.this);
				}
				if (!alertDialog.isShowing()) {
					Log.d(LOGTAG, "SHOW_DIALOG");
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
								Toast.makeText(PersonalLoginCommon.this, getText(R.string.login_success), Toast.LENGTH_SHORT).show();
								WenyiUser user = new WenyiUser();
								user.set_flag(statuscode);
								user.set_ID(dataJson.getString("uid"));
								user.set_token(dataJson.getString("token"));
								user.set_nickname(nickname);
								user.set_email(useremail);
								user.set_password(passwd);
								user.set_isLogin(true);
								Util.saveUserData(PersonalLoginCommon.this, user);
								isLogin = true;
								if (alertDialog.isShowing()) {
									Log.d(LOGTAG, "DISMISS_DIALOG");
									alertDialog.cancel();
								}
								setResult(RESULT_OK);
								finish();
								break;
							}else{
								Toast.makeText(PersonalLoginCommon.this, dataJson.getString("msg"), Toast.LENGTH_SHORT).show();
							}
						}else{
							if(statuscode == 10000){
								WenyiUser user = Util.getUserData(getApplicationContext());
								user.set_isLogin(true);
								Util.saveUserData(PersonalLoginCommon.this, user);
								isLogin = true;
								if (alertDialog.isShowing()) {
									Log.d(LOGTAG, "DISMISS_DIALOG");
									alertDialog.cancel();
								}
								setResult(RESULT_OK);
								Toast.makeText(PersonalLoginCommon.this, jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
								finish();
								break;
							}else{
								isLogin = false;
							}
							Toast.makeText(PersonalLoginCommon.this, jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(PersonalLoginCommon.this, getText(R.string.login_failed), Toast.LENGTH_SHORT).show();
				}
				
				if (alertDialog.isShowing()) {
					Log.d(LOGTAG, "DISMISS_DIALOG");
					alertDialog.cancel();
				}
				finish();
				setResult(RESULT_CANCELED);
				break;
			default:
				break;
			}
		};
	};
	private static final String LOGTAG = "PersonalLoginCommon";
	AlertDialog alertDialog;
	
	@Override
	protected void onDestroy() {
		if(alertDialog != null && alertDialog.isShowing()){
			alertDialog.dismiss();
		}
		super.onDestroy();
	}
}
