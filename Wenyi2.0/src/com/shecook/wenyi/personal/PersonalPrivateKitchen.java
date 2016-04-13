package com.shecook.wenyi.personal;

import java.util.LinkedList;

import javax.crypto.Mac;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.shecook.wenyi.model.personal.FoodBean;
import com.shecook.wenyi.personal.adapter.PersonalFoodCategoryAdapter;
import com.shecook.wenyi.personal.adapter.PersonalFoodCategoryAdapter.OnCatalogClickListener;
import com.shecook.wenyi.personal.adapter.PersonalHotwordAdapter;
import com.shecook.wenyi.personal.adapter.PersonalHotwordAdapter.OnHotwordClickListener;
import com.shecook.wenyi.personal.adapter.PersonalShiCaiAdaptre;
import com.shecook.wenyi.personal.adapter.PersonalShiCaiAdaptre.OnShiCaiClickListener;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalPrivateKitchen extends BaseActivity implements
		OnClickListener, OnCatalogClickListener, OnShiCaiClickListener, OnHotwordClickListener{

	private String commentFor = "";
	private ImageView right_img;
	private EditText commentEdit;

	private int flag = -1;

	private TextView personal_select_one, personal_select_two,
			personal_select_three, personal_select_start, personal_cookbook_food_selected_ok;

	private RelativeLayout personal_food_catalog_layout;
	private GridView personal_cookbook_catalog_hotword, categoryGridView, shicaiGrideView;

	private PersonalShiCaiAdaptre shicaiAdapter;
	private PersonalFoodCategoryAdapter foodCatalogAdapter;
	private PersonalHotwordAdapter hotwordlogAdapter;
	private LinkedList<FoodBean> foodlist;
	private LinkedList<FoodBean> hotwordList;
	private LinkedList<FoodBean> selectedFoodlist;
	private FoodBean[] selectedFoodArray = new FoodBean[3];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_private_kitchen_layout);
		initView();
		/*
		 * if(!isLogin()){ Intent commentIntent = new
		 * Intent(PersonalPrivateKitchen.this, PersonalLoginCommon.class);
		 * commentIntent.putExtra("flag", "" + HttpStatus.COMMENT_FOR_ESSAY);
		 * startActivityForResult(commentIntent, HttpStatus.REQUEST_CODE_ESSAY);
		 * return; }
		 */
		JSONObject paramsub = new JSONObject();

		postComment(HttpUrls.PERSONAL_KITCHEN_INIT, null, initResultListener,
				initErrorListener, paramsub);
		JSONObject paramsubData = new JSONObject();
		postComment(HttpUrls.PERSONAL_KITCHTN_FOOD_DATA, null,
				dataResultListener, dataErrorListener, paramsubData);

	}

	private void initView() {
		foodlist = new LinkedList<FoodBean>();
		hotwordList = new LinkedList<FoodBean>();
		selectedFoodlist = new LinkedList<FoodBean>();

		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(personal_food_catalog_layout.isShown()){
					personal_food_catalog_layout.setVisibility(View.GONE);
				}else{
					finish();
				}
			}
		});
		right_img = (ImageView) findViewById(R.id.right_img);
		right_img.setVisibility(View.GONE);
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		commentFor = getIntent().getStringExtra("commentFor");
		titleView.setText(R.string.my_kichen_string);
		titleView.setTextColor(getResources().getColor(R.color.black));
		
		commentEdit = (EditText) findViewById(R.id.comment_text_id);

		flag = getIntent().getIntExtra("flag", -1);

		personal_select_one = (TextView) findViewById(R.id.personal_select_one);
		personal_select_one.setOnClickListener(this);
		personal_select_two = (TextView) findViewById(R.id.personal_select_two);
		personal_select_two.setOnClickListener(this);
		personal_select_three = (TextView) findViewById(R.id.personal_select_three);
		personal_select_three.setOnClickListener(this);

		personal_select_start = (TextView) findViewById(R.id.personal_select_start);
		personal_select_start.setOnClickListener(this);

		personal_cookbook_catalog_hotword = (GridView) findViewById(R.id.personal_cookbook_catalog_hotword);
		personal_cookbook_catalog_hotword.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {/*
				Intent intent = new Intent(PersonalPrivateKitchen.this,
						PersonalKitchenCookbookList.class);
				intent.putExtra("keyword", hotwordList.get((int)position).getId());
				intent.putExtra("flag", "hotword");
				startActivity(intent);
			*/}

		});
		
		hotwordlogAdapter = new PersonalHotwordAdapter(
				PersonalPrivateKitchen.this, this, hotwordList);
		personal_cookbook_catalog_hotword.setAdapter(hotwordlogAdapter);
		
		
		personal_food_catalog_layout = (RelativeLayout) findViewById(R.id.personal_food_catalog_layout);
		categoryGridView = (GridView) findViewById(R.id.food_category);
		categoryGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}

		});
		categoryGridView.setSelected(true);
		foodCatalogAdapter = new PersonalFoodCategoryAdapter(
				PersonalPrivateKitchen.this, this, foodlist);
		categoryGridView.setAdapter(foodCatalogAdapter);

		shicaiGrideView = (GridView) findViewById(R.id.shicai_gride);
		shicaiGrideView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}

		});

		shicaiAdapter = new PersonalShiCaiAdaptre(PersonalPrivateKitchen.this,
				foodlist, PersonalPrivateKitchen.this);
		shicaiGrideView.setAdapter(shicaiAdapter);
		
		personal_cookbook_food_selected_ok = (TextView) findViewById(R.id.personal_cookbook_food_selected_ok);
		personal_cookbook_food_selected_ok.setOnClickListener(this);

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

	private int updateFood = -1;

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Log.e("lixufeng", "onClick");
		switch (id) {
		case R.id.right_img:

			break;
		case R.id.personal_select_one:
			updateFood = 0;
			personal_food_catalog_layout.setVisibility(View.VISIBLE);
			break;
		case R.id.personal_select_two:
			updateFood = 1;
			personal_food_catalog_layout.setVisibility(View.VISIBLE);
			break;
		case R.id.personal_select_three:
			updateFood = 2;
			personal_food_catalog_layout.setVisibility(View.VISIBLE);
			break;
		case R.id.personal_select_start:
			if (hasFood()) {
				Intent intent = new Intent(PersonalPrivateKitchen.this,
						PersonalKitchenCookbookList.class);
				intent.putExtra("keyword", fromArray());
				intent.putExtra("flag", "food");
				startActivity(intent);
			} else {
				Toast.makeText(PersonalPrivateKitchen.this, "请选择食材",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.personal_cookbook_food_selected_ok:
			personal_food_catalog_layout.setVisibility(View.GONE);
		default:
			break;
		}
	}

	public void onBackPressed() {
		if(personal_food_catalog_layout.isShown()){
			personal_food_catalog_layout.setVisibility(View.GONE);
		}else{
			super.onBackPressed();
		}
	};
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				shicaiAdapter.notifyDataSetChanged();
				foodCatalogAdapter.notifyDataSetChanged();
				break;
			case HttpStatus.STATUS_OK_2:
				hotwordlogAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	public void postComment(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener,
			JSONObject paramsub) {
		JSONObject commonsub = Util.getCommonParam(PersonalPrivateKitchen.this);
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}
		try {
			jsonObject.put("common", commonsub);
			if (paramsub != null) {
				jsonObject.put("param", paramsub);
			}
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

	Listener<JSONObject> initResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject jsonObject) {
			initHotwordData(jsonObject);
		}
	};

	ErrorListener initErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	Listener<JSONObject> dataResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject jsonObject) {
			Log.e(TAG,
					"catalogResultListener onResponse -> "
							+ jsonObject.toString());
			initData(jsonObject);
		}
	};

	ErrorListener dataErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	private void initHotwordData(JSONObject jsonObject) {
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");

						if (data.has("list")) {
							JSONArray list = data.getJSONArray("list");
							LinkedList<FoodBean> listTemp = new LinkedList<FoodBean>();
							for (int i = 0, j = list.length(); i < j; i++) {
								JSONObject jb = list.getJSONObject(i);
								FoodBean catalogBean = new FoodBean();
								catalogBean.setId(jb.getString("id"));
								catalogBean.setName(jb.getString("title"));
								listTemp.add(catalogBean);
							}
							hotwordList.addAll(listTemp);
						}
						handler.sendEmptyMessage(HttpStatus.STATUS_OK_2);
					}
				} else {
					Toast.makeText(PersonalPrivateKitchen.this,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void initData(JSONObject jsonObject) {
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");

						if (data.has("list")) {
							JSONArray list = data.getJSONArray("list");
							LinkedList<FoodBean> listTemp = new LinkedList<FoodBean>();
							for (int i = 0, j = list.length(); i < j; i++) {
								JSONObject jb = list.getJSONObject(i);
								FoodBean catalogBean = new FoodBean();
								catalogBean.setId(jb.getString("id"));
								catalogBean.setName(jb.getString("cataname"));
								if (jb.has("items")) {
									JSONArray items = jb.getJSONArray("items");
									LinkedList<FoodBean> itemsTemp = new LinkedList<FoodBean>();
									for (int k = 0, t = items.length(); k < t; k++) {
										JSONObject foodObject = items
												.getJSONObject(k);
										FoodBean foodbean = new FoodBean();
										foodbean.setId(foodObject
												.getString("id"));
										foodbean.setName(foodObject
												.getString("name"));
										foodbean.setCatalogId(foodObject
												.getString("cataid"));
										itemsTemp.add(foodbean);
									}
									catalogBean.setSubList(itemsTemp);
								}
								listTemp.add(catalogBean);
							}
							foodlist.addAll(listTemp);
						}
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
					}
				} else {
					Toast.makeText(PersonalPrivateKitchen.this,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private int catalog = 0;

	@Override
	public void refreshCatalog(TextView button, int position) {
		catalog = position;
		shicaiAdapter.updateCatalog(position);
		handler.sendEmptyMessage(HttpStatus.STATUS_OK);
	}

	@Override
	public void refreshFood(TextView button, int position) {
		Log.e(TAG, "refreshFood position " + position + ", food "
				+ foodlist.get(catalog).getSubList().get(position));
		selectedFoodArray[updateFood] = foodlist.get(catalog).getSubList()
				.get(position);
		if(updateFood == 0){
			personal_select_one.setText(foodlist.get(catalog).getSubList()
				.get(position).getName());
		}else if(updateFood == 1){
			personal_select_two.setText(foodlist.get(catalog).getSubList()
					.get(position).getName());
		}else{
			personal_select_three.setText(foodlist.get(catalog).getSubList()
					.get(position).getName());
		}
		personal_food_catalog_layout.setVisibility(View.GONE);
	}

	public boolean hasFood() {
		for (FoodBean food : selectedFoodArray) {
			if (food != null && food.getName() != null) {
				return true;
			}
		}
		return false;
	}

	public String[] fromArray() {
		String[] str = new String[]{"","",""};
		for (int i = 0; i < selectedFoodArray.length; i++) {
			FoodBean bean = selectedFoodArray[i];
			if(bean != null){
				try {
					str[i] = bean.getName();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return str;
	}

	@Override
	public void loadHotword(TextView button, int position) {
		Intent intent = new Intent(PersonalPrivateKitchen.this,
				PersonalKitchenCookbookList.class);
		Log.e(TAG, "loadHotword " + hotwordList.get((int)position).getId());
		intent.putExtra("keyword", hotwordList.get((int)position).getId());
		intent.putExtra("flag", "hotword");
		startActivity(intent);
	}

}
