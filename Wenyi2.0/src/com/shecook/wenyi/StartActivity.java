package com.shecook.wenyi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.shecook.wenyi.common.slidingmenu.SlidingMenu;
import com.shecook.wenyi.common.slidingmenu.SlidingMenu.OnCloseListener;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.cookbook.CookbookFragment;
import com.shecook.wenyi.cookbook.adapter.CookbookExpandableListAdapter;
import com.shecook.wenyi.essay.WelcomeFragment;
import com.shecook.wenyi.group.GroupFragment;
import com.shecook.wenyi.mainpackage.FragmentTabAdapter;
import com.shecook.wenyi.model.CookbookCatalog;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.personal.PersonalFragment;
import com.shecook.wenyi.piazza.PiazzaFragment;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.PreferencesUtils;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class StartActivity extends BaseActivity{

	public static final String TAG = "StartActivity";

	public interface UpdateFragmentListener{
		public void updateFragment(String info);
	}
	
	/*
	 * 1,检查版本是否需要更新 2,判断启动方式（通知，桌面等）
	 */
	/**
	 * Called when the activity is first created.
	 */
	private RadioGroup rgs;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	WelcomeFragment welcomeFragment = null;
	CookbookFragment cookbookFragment = null;
	PiazzaFragment piazzaFragment = null;
	GroupFragment groupFragment = null;
	PersonalFragment personalFragment = null;
	
	UpdateFragmentListener updateFragmentListener;
	
	public String hello = "hello ";

	public static JSONObject welcomeData;
	
	NetworkImageView networkImageView = null;
	
	private long castTime = 0l;
	
	private SlidingMenu menu;
	private View menuView;
	CookbookExpandableListAdapter expandAdapter;
	private LinkedList<CookbookCatalog> mCatalogItems;
	
	public int currentFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bottom_navigation);
		
/*		Log.e(TAG, "catalogResultListener getDefaultDisplay -> " + getWindowManager().getDefaultDisplay().getWidth());
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Log.e(TAG, "catalogResultListener widthPixels -> " + displayMetrics.widthPixels);*/
		
		welcomeFragment = new WelcomeFragment();
		cookbookFragment = new CookbookFragment();
		piazzaFragment = new PiazzaFragment();
		groupFragment = new GroupFragment();
		personalFragment = new PersonalFragment();

		castTime = System.currentTimeMillis();
		mCatalogItems = new LinkedList<CookbookCatalog>();
		networkImageView = (NetworkImageView) findViewById(R.id.main_layout_fillparent);
		networkImageView.setVisibility(View.VISIBLE);
		Log.d(TAG, "catalogResultListener onCreate -> " + castTime);

		if(netConnected){
			getTokenFrom(false,tokenResultListener,tokenErrorListener);
		}else{
			initView();
			handler.sendEmptyMessage(2);
		}
		
	}
	
	public void initMenu(){
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu_frame);
		menu.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public void onClose() {
				
			}
		});
		menuView = menu.findViewById(R.id.expandable_listview);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*if(keyCode == KeyEvent.KEYCODE_BACK){
			menu.toggle();
			return true;
		}*/
		return super.onKeyDown(keyCode, event);
	}
	
	public void initView(){
		initMenu();
		fragments.add(welcomeFragment);
		fragments.add(cookbookFragment);
		fragments.add(piazzaFragment);
		fragments.add(groupFragment);
		fragments.add(personalFragment);

		rgs = (RadioGroup) findViewById(R.id.tabs_rg);

		FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments,
				R.id.wenyi_tab_content, rgs);
		tabAdapter
				.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
					@Override
					public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
							int checkedId, int index) {
						currentFragment = index;
						Log.d(TAG, "OnRgsExtraCheckedChanged -> " + index);
						if(index == 1){
							menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
						}else{
							menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
						}
					}
				});
		
		ExpandableListView expandableListView = (ExpandableListView) menuView.findViewById(R.id.expandable_listview);
//		expandableListView.setChildDivider(getActivity().getResources().getDrawable(R.drawable.transport));
		expandableListView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		expandableListView.setGroupIndicator(null);
		
		
		expandAdapter = new CookbookExpandableListAdapter(StartActivity.this, mCatalogItems);
		expandableListView.setAdapter(expandAdapter);
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
            	WenyiLog.logv(TAG, "onChildClick groupPosition " + groupPosition + ",childPosition " + childPosition);
            	updateFragmentListener.updateFragment(mCatalogItems.get(groupPosition).getCata_items().get(childPosition).getId());
            	menu.toggle();
                return false;
            }
        });
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition,
					long arg3) {
				WenyiLog.logv(TAG, "onGroupClick groupPosition " + groupPosition);
				if(mCatalogItems.get(groupPosition).getCata_items().size() == 0){
					menu.toggle();
					updateFragmentListener.updateFragment(mCatalogItems.get(groupPosition).getId());
				}
				return false;
			}
		});
		
	}
	
	private boolean shouldNotify = false;
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				Util.commonHttpMethod(StartActivity.this, HttpUrls.WENYI_CHECK_VERSION, null, versionResultListener, tokenErrorListener);
				getCatalog(HttpUrls.ESSAY_WENYI_LIST_CATALOG, null, catalogResultListener, catalogErrorListener);
				getCookbookCatalog(HttpUrls.COOKBOOK_LIST_CATALOG, null, cookbookCatalogResultListener, cookbookCatalogErrorListener);
				break;
			case 2:
				if(System.currentTimeMillis() - castTime < 3000){
					handler.sendEmptyMessageDelayed(2, 500);
				}else{
					shouldNotify = true;
					networkImageView.setVisibility(View.GONE);
				}
				break;
			case 3:
				if(shouldNotify){
					expandAdapter.notifyDataSetChanged();
				}else{
					handler.sendEmptyMessageDelayed(3, 500);
				}
				break;
			case Util.SHOW_VERSION_DIALOG:
				dialog(getString(R.string.app_name),
						PreferencesUtils.getString(StartActivity.this,
								"versionDescription", "有新版本啦，赶紧来更新！"));
				break;
			default:
				break;
			}
		};
	};
	
	
	protected void dialog(String title, String message) {
		AlertDialog.Builder builder = new Builder(StartActivity.this);
		builder.setIcon(R.drawable.dialog_icon);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton(getString(R.string.download_new_version_tip),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Uri uri = Uri.parse(PreferencesUtils.getString(
								StartActivity.this, "versionLinkUrl"));
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
						dialog.dismiss();
						finish();
					}
				});
		builder.setNegativeButton(getString(R.string.next_time_tip),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				return true;
			}
		});
		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	
	boolean showVersion = false;
	
	Listener<JSONObject> versionResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG, "versionResultListener onResponse -> " + result.toString());
			String response = result.toString();
			if(!TextUtils.isEmpty(response)){
				try {
					JSONObject jsonObject = new JSONObject(response);
					int statuscode = jsonObject.getInt("statuscode");
					if(statuscode == 200){
						JSONObject jsonobject = jsonObject.getJSONObject("data");
							String newVersion = jsonobject.getString("version");
							String oldVersion = getVersion();
							Log.e(TAG, "version oldVersion " + oldVersion + ", newVersion " + newVersion);
							/*String oldVersion = PreferencesUtils.getString(
									StartActivity.this, "version", "100");*/
							oldVersion = oldVersion.replace(".", "");
							newVersion = newVersion.replace(".", "");
							if (Integer.parseInt(newVersion) > Integer.parseInt(oldVersion)) {
								showVersion = true;
								PreferencesUtils.putString(StartActivity.this, "version", newVersion);
								if(jsonobject.has("description")){
									PreferencesUtils.putString(StartActivity.this,
											"versionDescription",
											jsonobject.getString("description"));
								}
								PreferencesUtils.putString(StartActivity.this,
										"versionLinkUrl", jsonobject.getString("url"));
								handler.sendEmptyMessage(Util.SHOW_VERSION_DIALOG);
								return;
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
	
	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static JSONObject getWelcomeData() {
		return welcomeData;
	}

	public void setWelcomeData(JSONObject welcomeData) {
		this.welcomeData = welcomeData;
	}
	
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
							user.set_isLogin(true);
							user.set_token(dataJson.getString("token"));
							
							Util.saveUserData(StartActivity.this, user);
							handler.sendEmptyMessage(HttpStatus.STATUS_OK);
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
	
	Listener<JSONObject> catalogResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
//			Toast.makeText(getApplicationContext(), "onResponse", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "catalogResultListener onResponse -> " + result.toString());
			handler.sendEmptyMessage(2);
			setWelcomeData(result);
			initView();
		}
	};
	
	ErrorListener catalogErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	public void getCookbookCatalog(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}

		JSONObject sub = Util.getCommonParam(StartActivity.this);

		try {
			jsonObject.put("common", sub);
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
	
	Listener<JSONObject> cookbookCatalogResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			cookbookFragment.setCookbookCatalogObject(result);
			initCatalogData(result);
		}
	};

	ErrorListener cookbookCatalogErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	private void initCatalogData(JSONObject jsonObject) {
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					
					if (!jsonObject.isNull("data")) {
						
						JSONObject data = jsonObject.getJSONObject("data");
						JSONArray list = data.getJSONArray("catalog");
						LinkedList<CookbookCatalog> listTemp = new LinkedList<CookbookCatalog>();
						
						for (int i = 0, j = list.length(); i < j; i++) {
							JSONObject jb = list.getJSONObject(i);
							CookbookCatalog cbc = new CookbookCatalog();
							cbc.setId(jb.getString("id"));
							cbc.setCataname(jb.getString("cataname"));
							cbc.setParentid(jb.getString("parentid"));
							
							LinkedList<CookbookCatalog> listTemp2 = null;
							listTemp2 = new LinkedList<CookbookCatalog>();
							if(jb.has("cata_items")){
								JSONArray sublist = jb.getJSONArray("cata_items");
								for (int k = 0, t = sublist.length(); k < t; k++) {
									JSONObject subjb = sublist.getJSONObject(k);
									CookbookCatalog subCbc = new CookbookCatalog();
									subCbc.setId(subjb.getString("id"));
									subCbc.setCataname(subjb.getString("cataname"));
									subCbc.setParentid(subjb.getString("parentid"));
									listTemp2.add(subCbc);
								}
							}
							cbc.setCata_items(listTemp2);
							listTemp.add(cbc);
						}
						mCatalogItems.addAll(listTemp);

						handler.sendEmptyMessage(3);
					}
				} else {
					Toast.makeText(StartActivity.this,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public UpdateFragmentListener getUpdateFragmentListener() {
		return updateFragmentListener;
	}

	public void setUpdateFragmentListener(
			UpdateFragmentListener updateFragmentListener) {
		this.updateFragmentListener = updateFragmentListener;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e(TAG, "onActivityResult ............ currentFragment " + currentFragment + ", object is " + fragments.get(currentFragment));
		
		fragments.get(currentFragment).onActivityResult(requestCode, resultCode, data);
	}
}
