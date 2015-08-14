package com.shecook.wenyi.personal;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.StartActivity;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.util.RequestHttpUtil;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;

public class PersonalFragment extends Fragment implements OnClickListener {
	private static final String TAG = "PersonalFragment";

	private Activity mActivity;

	@Override
	public void onAttach(Activity activity) {
		WenyiLog.logv(TAG, "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		WenyiUser user = Util.getUserData(mActivity);
		RequestHttpUtil.getHttpData(mActivity, HttpUrls.PERSONAL_MYCARD, null,
				userCardResultListener, userCardErrorListener);
		retrieveContactInfoFromSIM();
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
		switch (id) {
		case R.id.personal_center_settings:
			Intent intent = new Intent(this.getActivity(),
					PersonalSettings.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case 1:
				Intent intent = new Intent(mActivity,PersonalLoginCommon.class);
				startActivityForResult(intent, 1);
				break;
			case 2:
				Util.updateBooleanData(mActivity, "islogin", false);
				((StartActivity)getActivity()).getTokenFrom(false, tokenResultListener, tokenErrorListener);
				break;
			case 3:
				break;
			default:
				break;
			}
		};
	};

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
						int core_status = dataJson.getInt("core_status");
						if (core_status == 200) {

						} else {
							// 有错误情况
						}
					} else if (statuscode == HttpStatus.USER_NOT_LOGIN) {
						handler.sendEmptyMessage(1);
					}else if(statuscode == HttpStatus.USER_TOKEN_OUTDATE){
						handler.sendEmptyMessage(2);
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
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

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
