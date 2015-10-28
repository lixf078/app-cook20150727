package com.shecook.wenyi.common;

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

public class AddCommentActivity extends BaseActivity implements OnClickListener{
	
	private String commentFor = "";
	private ImageView addComment;
	private EditText commentEdit;
	
	private int flag = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_comment_layout);
		initView();
		if(!isLogin()){
			Intent commentIntent = new Intent(AddCommentActivity.this, PersonalLoginCommon.class);
			commentIntent.putExtra("flag", "" + HttpStatus.COMMENT_FOR_ESSAY);
			startActivityForResult(commentIntent, HttpStatus.REQUEST_CODE_ESSAY);
			return;
		}
		
	}
	
	
	private void initView() {
		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		addComment = (ImageView) findViewById(R.id.right_img);
		addComment.setBackgroundResource(R.drawable.f55_btn);
		addComment.setVisibility(View.VISIBLE);
		addComment.setOnClickListener(this);
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		commentFor = getIntent().getStringExtra("commentFor");
		titleView.setText(R.string.comments);
		
		commentEdit = (EditText) findViewById(R.id.comment_text_id);
		
		flag = getIntent().getIntExtra("flag", -1);
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
		case R.id.right_img:
			String comment = commentEdit.getEditableText().toString();
			if(TextUtils.isEmpty(comment)){
				Log.d(TAG, "onClick -> " + flag + " 想跟我说点什么呢？");
				Toast.makeText(AddCommentActivity.this, "想跟我说点什么呢？", Toast.LENGTH_SHORT).show();
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
				}else if(flag == HttpStatus.COMMENT_FOR_CIRCLE){
					paramsub.put("shareid", commentFor);
					postComment(HttpUrls.GROUP_CIRCLE_SHARE_ADD_COMMENT, null, commentResultListener, commentErrorListener, paramsub);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				finish();
			}
			break;
		default:
			break;
		}
	}

	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				setResult(Activity.RESULT_OK);
				AddCommentActivity.this.finish();
				break;
			}
		}
	};
		
	public void postComment(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener, JSONObject paramsub) {
		JSONObject commonsub = Util.getCommonParam(AddCommentActivity.this);
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
