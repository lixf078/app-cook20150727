package com.shecook.wenyi.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.personal.PersonalLoginCommon;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class CreatePersonalInfoActivity extends BaseActivity implements OnClickListener{
	
	private String commentFor = "";
	private TextView addComment;
	private EditText commentEdit;
	
	private ImageView image_1_id, image_2_id, image_3_id, image_4_id;
	private String[] photos = new String[4];
	private String[] imgurls = new String[4];
	private int photoId = -1;
	CommonUpload commonUpload = null;
	
	private int flag = -1;
	private String ententId = ""; // 作业，圈子分享等
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_personal_info_layout);
		initView();
		if(!isLogin()){
			Intent commentIntent = new Intent(CreatePersonalInfoActivity.this, PersonalLoginCommon.class);
			commentIntent.putExtra("flag", "" + HttpStatus.COMMENT_FOR_ESSAY);
			startActivityForResult(commentIntent, HttpStatus.REQUEST_CODE_ESSAY);
			return;
		}
		commonUpload = CommonUpload.getInstance();
	}
	
	private void initView() {
		TextView returnImage = (TextView) findViewById(R.id.return_textview);
		returnImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		addComment = (TextView) findViewById(R.id.right_textview);
		addComment.setVisibility(View.VISIBLE);
		addComment.setOnClickListener(this);
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		commentFor = getIntent().getStringExtra("commentFor");
		titleView.setText("");
		
		commentEdit = (EditText) findViewById(R.id.content_text_id);
		
		flag = getIntent().getIntExtra("flag", -1);
		ententId = getIntent().getStringExtra("ententId");
		
		image_1_id = (ImageView) findViewById(R.id.image_1_id);
		image_2_id = (ImageView) findViewById(R.id.image_2_id);
		image_3_id = (ImageView) findViewById(R.id.image_3_id);
		image_4_id = (ImageView) findViewById(R.id.image_4_id);
		image_1_id.setOnClickListener(this);
		image_2_id.setOnClickListener(this);
		image_3_id.setOnClickListener(this);
		image_4_id.setOnClickListener(this);
	}
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	protected void onDestroy() {
		super.onDestroy();
	};
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.right_textview:
			String comment = commentEdit.getEditableText().toString();
			if(TextUtils.isEmpty(comment)){
				Log.d(TAG, "onClick -> " + flag + " 想跟我说点什么呢？");
				Toast.makeText(CreatePersonalInfoActivity.this, "想跟我说点什么呢？", Toast.LENGTH_SHORT).show();
				return;
			}
			Log.d(TAG, "onClick -> " + flag + ",comment " + comment);
			JSONObject paramsub = new JSONObject();
			try {
				paramsub.put("comment", "" + comment);
				if(flag == HttpStatus.COMMENT_FOR_ESSAY){
					paramsub.put("articleid", commentFor);
					postComment(HttpUrls.ESSAY_WENYI_ITEM_DETAI_ADD_COMMENT, null, commentResultListener, commentErrorListener, paramsub);
				}else if(flag == HttpStatus.COMMENT_FOR_COOKBOOK){
					paramsub.put("recipeid", commentFor);
					postComment(HttpUrls.COOKBOOK_WENYI_ITEM_DETAI_ADD_COMMENT, null, commentResultListener, commentErrorListener, paramsub);
				}else if(flag == HttpStatus.COMMENT_FOR_TOPIC){
					paramsub.put("topicid", commentFor);
					postComment(HttpUrls.PIZZA_TOPIC_LIST_ITEM_DETAIL_ADD_COMMENT, null, commentResultListener, commentErrorListener, paramsub);
				}else if(flag == HttpStatus.PUBLIC_FOR_CIRCLE){
					paramsub.put("uptype", "circle");
					StringBuffer str = new StringBuffer();
					for (String path : photos) {
						Log.d(TAG, "path" + path);
						if(!TextUtils.isEmpty(path)){
							str.append(path + ";");
						}
					}
					CommonUpload.commonMethod(CreatePersonalInfoActivity.this, HttpUrls.UPLOAD_IMG, paramsub, listResultListener, listErrorListener, str.subSequence(0, str.length() - 1) + "");
				}
			} catch (JSONException e) {
				e.printStackTrace();
				finish();
			}
			break;
		case R.id.image_1_id:
			photoId = 0;
			commonUpload.showCameraDialog(CreatePersonalInfoActivity.this);
			break;
		case R.id.image_2_id:
			photoId = 1;
			commonUpload.showCameraDialog(CreatePersonalInfoActivity.this);
			break;
		case R.id.image_3_id:
			photoId = 2;
			commonUpload.showCameraDialog(CreatePersonalInfoActivity.this);
			break;
		case R.id.image_4_id:
			photoId = 3;
			commonUpload.showCameraDialog(CreatePersonalInfoActivity.this);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap bitmap = null;
		String mFileName = null;
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CommonUpload.CAMERA_WITH_DATA:
			mFileName = commonUpload.cameraImageUri.getPath();
			Log.e("lixufeng", "onActivityResult CAMERA_WITH_DATA mFileName " + mFileName);
			bitmap = BitmapFactory.decodeFile(mFileName);
			if(photoId == 0){
				image_1_id.setImageBitmap(bitmap);
				photos[photoId] = mFileName;
			}else if(photoId == 1){
				image_2_id.setImageBitmap(bitmap);
				photos[photoId] = mFileName;
			}else if(photoId == 2){
				image_3_id.setImageBitmap(bitmap);
				photos[photoId] = mFileName;
			}else if(photoId == 3){
				image_4_id.setImageBitmap(bitmap);
				photos[photoId] = mFileName;
			}
			break;
		case CommonUpload.PHOTO_PICKED_WITH_DATA:
			Log.e("lixufeng", "PHOTO_PICKED_WITH_DATA mFileName: " + mFileName + ", photoId " + photoId);
			mFileName = CommonUpload.getDataColumn(getApplicationContext(), data.getData(),
					null, null);
			bitmap = CommonUpload.getBitmap(mFileName);
			if(photoId == 0){
				image_1_id.setImageBitmap(bitmap);
				photos[photoId] = mFileName;
			}else if(photoId == 1){
				image_2_id.setImageBitmap(bitmap);
				photos[photoId] = mFileName;
			}else if(photoId == 2){
				image_3_id.setImageBitmap(bitmap);
				photos[photoId] = mFileName;
			}else if(photoId == 3){
				image_4_id.setImageBitmap(bitmap);
				photos[photoId] = mFileName;
			}
			break;
		case CommonUpload.CAMERA_PICK_PHOTO:
			break;
		case CommonUpload.SELECT_PIC_KITKAT:
			mFileName = CommonUpload.getPath(CreatePersonalInfoActivity.this, data.getData());
			Log.e("lixufeng", "SELECT_PIC_KITKAT mFileName: " + mFileName);
			bitmap = CommonUpload.getBitmap(mFileName);
			if(photoId == 0){
				image_1_id.setImageBitmap(bitmap);
				photos[photoId] = mFileName;
			}else if(photoId == 1){
				image_2_id.setImageBitmap(bitmap);
				photos[photoId] = mFileName;
			}else if(photoId == 2){
				image_3_id.setImageBitmap(bitmap);
				photos[photoId] = mFileName;
			}else if(photoId == 3){
				image_4_id.setImageBitmap(bitmap);
				photos[photoId] = mFileName;
			}
			break;
		}
	}
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				setResult(Activity.RESULT_OK);
				CreatePersonalInfoActivity.this.finish();
				break;
			case CommonUpload.UPLOAD_SUCESS:
				createInfo();
				break;
			case CommonUpload.GROUP_CREATE_SUCESS:
				finish();
				break;
			default:
				break;
			}
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
								Toast.makeText(CreatePersonalInfoActivity.this, msg,
										Toast.LENGTH_SHORT).show();
								if(data.has("imageitems")){
									JSONArray imagesJson = data
											.getJSONArray("imageitems");
									for(int i = 0,j = imagesJson.length(); i < j; i++){
										JSONObject image = imagesJson.getJSONObject(i);
										imgurls[i] = image.getString("name");
									}
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
			Toast.makeText(CreatePersonalInfoActivity.this, msg, Toast.LENGTH_SHORT)
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
	
	public void createInfo() {
		JSONObject paramObject = new JSONObject();
		try {
			if(flag == HttpStatus.PUBLIC_FOR_CIRCLE){
				paramObject.put("circleid", ententId);
			}
			String info = commentEdit.getEditableText().toString();

			paramObject.put("desc", info);
			JSONArray jsonArray = new JSONArray();
			for(int i = 0, j = imgurls.length;i<j;i++){
				if(!TextUtils.isEmpty(imgurls[i])){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("name", "" + imgurls[i]);
					jsonArray.put(jsonObject);
				}
			}
			paramObject.put("images", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(flag == HttpStatus.PUBLIC_FOR_CIRCLE){
			CommonUpload.commonMethod(CreatePersonalInfoActivity.this,HttpUrls.GROUP_CIRCLE_PUB, paramObject,
					crtateResultListener, crtateErrorListener, "");
		}else{
		}
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
								msg = "" + "圈子分享成功！";
								Toast.makeText(CreatePersonalInfoActivity.this, msg,
										Toast.LENGTH_SHORT).show();
								handler.sendEmptyMessage(CommonUpload.GROUP_CREATE_SUCESS);
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
			Toast.makeText(CreatePersonalInfoActivity.this, msg, Toast.LENGTH_SHORT)
					.show();
			handler.sendEmptyMessage(CommonUpload.GROUP_CREATE_FAILED);
		}
	};

	ErrorListener crtateErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	public void postComment(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener, JSONObject paramsub) {
		JSONObject commonsub = Util.getCommonParam(CreatePersonalInfoActivity.this);
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}
		try {
			jsonObject.put("common", commonsub);
			jsonObject.put("param", paramsub);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest wenyiRequest = new JsonObjectRequest(Method.POST,
				url, jsonObject, resultListener, errorListener);

		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	Listener<JSONObject> commentResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject jsonObject) {
			Log.d(TAG,
					"catalogResultListener onResponse -> " + jsonObject.toString());
			if (jsonObject != null) {
				try {
					if (!jsonObject.isNull("statuscode")
							&& 200 == jsonObject.getInt("statuscode")) {
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	};

	ErrorListener commentErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
}
