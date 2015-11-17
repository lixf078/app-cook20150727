package com.shecook.wenyi.personal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.StartActivity;
import com.shecook.wenyi.common.CommonUpload;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.cookbook.CookbookCollectionActivity;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.util.RequestHttpUtil;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalFragment extends Fragment implements OnClickListener {
	private static final String TAG = "PersonalFragment";

	private StartActivity mActivity;

	private TextView user_level, personal_gold, personal_email, personal_experience, personal_username;
	
	private NetworkImageView userIconView;
	LinearLayout personal_my_collection, personal_my_topic, personal_my_edit,personal_my_kitchen;
	CommonUpload commonUpload;
	String iconUrl = null;
	private String iconHttpUrl = null;
	
	@Override
	public void onAttach(Activity activity) {
		WenyiLog.logv(TAG, "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (StartActivity) getActivity();
		RequestHttpUtil.getHttpData(mActivity, HttpUrls.PERSONAL_MYCARD, null,
				userCardResultListener, userCardErrorListener);
		commonUpload = CommonUpload.getInstance();
		
		// retrieveContactInfoFromSIM();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.personal_fragment, container,
				false);
		initView(rootView);
		return rootView;
	}
	public void initView(View rootView) {
		ImageView settings = (ImageView) rootView
				.findViewById(R.id.personal_center_settings);
		settings.setOnClickListener(this);
		
		userIconView = (NetworkImageView) rootView.findViewById(R.id.user_icon);
		userIconView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				commonUpload.showCameraDialog(mActivity);
			}
		});
//		userIconView.setDefaultImageResId(R.drawable.icon);
//		userIconView.setErrorImageResId(R.drawable.icon);
		personal_username = (TextView) rootView.findViewById(R.id.personal_username);
		personal_username.setOnClickListener(this);
		
		user_level = (TextView) rootView.findViewById(R.id.user_level);
		personal_gold = (TextView) rootView.findViewById(R.id.personal_gold);
		personal_email = (TextView) rootView.findViewById(R.id.personal_email);
		personal_email.setOnClickListener(this);
		personal_experience = (TextView) rootView.findViewById(R.id.personal_experience);
		
		personal_my_collection = (LinearLayout) rootView.findViewById(R.id.personal_my_collection);
		personal_my_collection.setOnClickListener(this);
		
		personal_my_topic = (LinearLayout) rootView.findViewById(R.id.personal_my_topic);
		personal_my_topic.setOnClickListener(this);
		
		personal_my_edit = (LinearLayout) rootView.findViewById(R.id.personal_my_edit);
		personal_my_edit.setOnClickListener(this);
		
		personal_my_kitchen = (LinearLayout) rootView.findViewById(R.id.personal_my_kitchen);
		personal_my_kitchen.setOnClickListener(this);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		WenyiLog.logv(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		WenyiLog.logv(TAG, "onResume");
		if(!mActivity.isLogin()){
			WenyiUser user = new WenyiUser();
			user.set_score("积分");
			user.set_level("厨房小毛猴");
			user.set_msgcount("消息");
			user.set_nickname("");
			user.set_level_core("经验");
			user.set_uimage50("");
			updateView(user);
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		WenyiLog.logv(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		WenyiLog.logv(TAG, "onStop");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		WenyiLog.logv(TAG, "onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		WenyiLog.logv(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		WenyiLog.logv(TAG, "onDetach");
		super.onDetach();
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		Intent intent = null;
		if(!mActivity.isLogin() && id != R.id.personal_center_settings){
			intent = new Intent(mActivity,PersonalLoginCommon.class);
			startActivityForResult(intent, 1);
			return;
		}
		switch (id) {
		case R.id.personal_center_settings:
			intent = new Intent(PersonalFragment.this.getActivity(),
					PersonalSettings.class);
			startActivity(intent);
			break;
		case R.id.personal_my_collection:
			intent = new Intent(PersonalFragment.this.getActivity(),
					CookbookCollectionActivity.class);
			startActivity(intent);
			break;
		case R.id.personal_my_topic:
			intent = new Intent(PersonalFragment.this.getActivity(),
					PersonalCommentsListActivity.class);
			startActivity(intent);
			break;
		case R.id.personal_my_edit:
			 intent = new Intent(PersonalFragment.this.getActivity(),
					PersonalEdition.class);
			startActivity(intent);
			break;
		case R.id.personal_my_kitchen:
			 intent = new Intent(PersonalFragment.this.getActivity(),
					PersonalPrivateKitchen.class);
			startActivity(intent);
			break;
		case R.id.personal_email:
			intent = new Intent(PersonalFragment.this.getActivity(), PersonalMessageListActivity.class);
			startActivity(intent);
			break;
		case R.id.personal_username:
			intent = new Intent(PersonalFragment.this.getActivity(), PersonalNickNameChange.class);
			startActivity(intent);
		default:
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.USER_NOT_LOGIN:
				Intent intent = new Intent(mActivity,PersonalLoginCommon.class);
				startActivityForResult(intent, 1);
				break;
			case HttpStatus.USER_TOKEN_OUTDATE:
				Util.updateBooleanData(mActivity, "islogin", false);
				((StartActivity)getActivity()).getTokenFrom(false, tokenResultListener, tokenErrorListener);
				break;
			case HttpStatus.STATUS_OK:
				WenyiUser user = (WenyiUser) msg.obj;
				
				updateView(user);
				Log.e(TAG, "update user data " + Util.getUserData(mActivity).toString());
				break;
			case CommonUpload.UPLOAD_SUCESS:
				createInfo();
			default:
				break;
			}
		};
	};

	
	public void updateView(WenyiUser user){
		personal_username.setText(user.get_nickname());
		user_level.setText(user.get_level());
		personal_gold.setText(user.get_score());
		personal_email.setText(user.get_msgcount());
		personal_experience.setText(user.get_level_core());
		if("".equals(user.get_uimage50())){
			userIconView.setImageBitmap(null);
			return;
		}
		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
	    userIconView.setImageUrl(user.get_uimage50(), imageLoader);
	}
	
	Listener<JSONObject> userCardResultListener = new Listener<JSONObject>() {

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
						WenyiUser user = new WenyiUser();
						user.set_score(dataJson.getString("u_gold"));
						user.set_level(dataJson.getString("u_lvlname"));
						user.set_msgcount(dataJson.getString("u_newmsg_count"));
						user.set_nickname(dataJson.getString("u_nickname"));
						user.set_level_core(dataJson.getString("u_exp"));
						user.set_u_newfriends_count(dataJson.getString("u_newfriends_count"));
						user.set_uimage50(dataJson.getString("u_portrait"));
						user.set_isLogin(true);
						Util.saveUserData(mActivity, user);
						Message msg = new Message();
						msg.what = HttpStatus.STATUS_OK;
						msg.obj = user;
						handler.sendMessage(msg);
					} else if (statuscode == HttpStatus.USER_NOT_LOGIN) {
						handler.sendEmptyMessage(HttpStatus.USER_NOT_LOGIN);
						Util.updateBooleanData(mActivity, "islogin", false);
						mActivity.isLogin = false;
					}else if(statuscode == HttpStatus.USER_TOKEN_OUTDATE){
						handler.sendEmptyMessage(HttpStatus.USER_TOKEN_OUTDATE);
						Util.updateBooleanData(mActivity, "islogin", false);
						mActivity.isLogin = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
			}

		}
	};

	ErrorListener userCardErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	
	
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
							user.set_mID(Util.getMid(mActivity));
							user.set_token(dataJson.getString("token"));
							Util.saveUserData(mActivity, user);
							handler.sendEmptyMessage(HttpStatus.USER_NOT_LOGIN);
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
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e(TAG, "personalFragment onActivityResult Intent "  + data + ", resultCode " + resultCode + ", requestCode " + requestCode);
		Bitmap bitmap = null;
		String mFileName = null;
		JSONObject paramsub = null;
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		switch (requestCode) {
		case 1:
			RequestHttpUtil.getHttpData(mActivity, HttpUrls.PERSONAL_MYCARD, null,
					userCardResultListener, userCardErrorListener);
			break;
		case CommonUpload.CAMERA_WITH_DATA:
			Log.d(TAG, "CAMERA_WITH_DATA");
			commonUpload.cropImageUri(mActivity, commonUpload.cameraImageUri, 290, 290, CommonUpload.CAMERA_PICK_PHOTO);
			break;
		case CommonUpload.CAMERA_PICK_PHOTO:
			if (commonUpload.cameraImageUri != null) {
				mFileName = commonUpload.cameraImageUri.getPath();
				bitmap = BitmapFactory.decodeFile(mFileName);
				userIconView.setImageBitmap(bitmap);
				iconUrl = mFileName;
			}
			Log.d(TAG, "CAMERA_PICK_PHOTO mFileName " + mFileName);
			
			paramsub = new JSONObject();
			try {
				paramsub.put("uptype", "portrait");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			CommonUpload.commonMethod(mActivity, HttpUrls.UPLOAD_IMG, paramsub, listResultListener, listErrorListener, iconUrl);
			break;
		case CommonUpload.PHOTO_PICKED_WITH_DATA:
			mFileName = CommonUpload.getDataColumn(mActivity, data.getData(),
					null, null);
			bitmap = CommonUpload.getBitmap(mFileName);
			userIconView.setImageBitmap(bitmap);
			iconUrl = mFileName;
			
			Log.d(TAG, "CAMERA_PICK_PHOTO mFileName " + mFileName);
			
			paramsub = new JSONObject();
			try {
				paramsub.put("uptype", "portrait");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			CommonUpload.commonMethod(mActivity, HttpUrls.UPLOAD_IMG, paramsub, listResultListener, listErrorListener, iconUrl);
			break;
			
		case CommonUpload.SELECT_PIC_KITKAT:
			mFileName = CommonUpload.getPath(mActivity, data.getData());
			bitmap = CommonUpload.getBitmap(mFileName);
			userIconView.setImageBitmap(bitmap);
			iconUrl = mFileName;
			
			Log.d(TAG, "SELECT_PIC_KITKAT mFileName " + mFileName);
			LruImageCache lruImageCache2 = LruImageCache.instance();
		    ImageLoader imageLoader2 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache2);
		    
		    userIconView.setImageUrl(iconUrl, imageLoader2);
			
			paramsub = new JSONObject();
			try {
				paramsub.put("uptype", "portrait");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			CommonUpload.commonMethod(mActivity, HttpUrls.UPLOAD_IMG, paramsub, listResultListener, listErrorListener, iconUrl);
			break;
		}
	}
	
	public void createInfo() {
		/*
		JSONObject paramObject = new JSONObject();
		try {
			paramObject.put("circleid", ententId);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		CommonUpload.commonMethod(mActivity,HttpUrls.COOKBOOK_WENYI_HOMEWORK_ADD, paramObject,
				crtateResultListener, crtateErrorListener, "");
		 */
	}
	Listener<JSONObject> crtateResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject jsonObject) {
			Log.d(TAG,
					"catalogResultListener onResponse -> "
							+ jsonObject.toString());
			String msg = "";
			if (jsonObject != null) {
				try {
					if (!jsonObject.isNull("statuscode")
							&& 200 == jsonObject.getInt("statuscode")) {
						if (!jsonObject.isNull("data")) {
							JSONObject data = jsonObject.getJSONObject("data");
							int core_status = data.getInt("core_status");
							if (core_status == 200) {
								msg = "头像更改成功！";
								Toast.makeText(mActivity, msg,
										Toast.LENGTH_SHORT).show();
								handler.sendEmptyMessage(CommonUpload.USERICON_CREATE_SUCESS);
								return;
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
			Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT)
					.show();
			handler.sendEmptyMessage(CommonUpload.USERICON_CREATE_FAILED);
		}
	};

	ErrorListener crtateErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	Listener<JSONObject> listResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject jsonObject) {
			Log.d(TAG,
					"catalogResultListener onResponse -> "
							+ jsonObject.toString());
			String msg = "";
			if (jsonObject != null) {
				try {
					if (!jsonObject.isNull("statuscode")
							&& 200 == jsonObject.getInt("statuscode")) {
						if (!jsonObject.isNull("data")) {
							JSONObject data = jsonObject.getJSONObject("data");
							int core_status = data.getInt("core_status");
							if (core_status == 200) {
								msg = "" + "文件上传成功！";
								Toast.makeText(mActivity, msg,
										Toast.LENGTH_SHORT).show();
								if(data.has("imageitems")){
									JSONArray imagesJson = data
											.getJSONArray("imageitems");
									iconHttpUrl = imagesJson.getJSONObject(0).getString("original");
								}
								handler.sendEmptyMessage(CommonUpload.UPLOAD_SUCESS);
								return;
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
			Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT)
					.show();
			handler.sendEmptyMessage(HttpStatus.STATUS_OK_2);
		}
	};

	ErrorListener listErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	public String toString() {
		return "Personal Fragment is coming...";
	};
	
// ***********************************************************
	// test info

	private static final String SURI = "content://icc/adn";

	/**获取库Phon表字段**/  
    private static final String[] PHONES_PROJECTION = new String[] {  
        Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID }; 
	private ContentResolver resolver = null;

	public void retrieveContactInfoFromSIM() {
		Uri uri = Uri.parse(SURI);
		resolver = mActivity.getContentResolver();
		Cursor cursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);
		Log.e("lixufeng", "retrieveContactInfoFromSIM 1111 " + cursor.getCount());
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
		Log.e("lixufeng", "retrieveContactInfoFromSIM 2222 " + phoneCursor.getCount());
	}
	

}
