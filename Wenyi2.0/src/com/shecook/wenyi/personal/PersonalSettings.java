package com.shecook.wenyi.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.shecook.wenyi.BaseActivity;
import com.shecook.wenyi.R;
import com.shecook.wenyi.util.Util;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;

public class PersonalSettings extends BaseActivity implements OnClickListener{

	private TextView login;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private ListView listView;
	private String[] dates = { "关于文怡", "文怡美食生活馆", "厨蜜网", "版本控制", "清除缓存",
			"意见反馈", "关于我们"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.center_set);
		super.onCreate(savedInstanceState);

		login = (TextView) findViewById(R.id.login);
		if(isLogin()){
			login.setText(R.string.user_logout);
		}
		listView = (ListView) findViewById(R.id.listView);

		// 初始化列表
		initList();

		//点击登录
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
					// intent.setClass(PersonalSettings.this, CenterAboutWenyiActivity.class);
					startActivity(intent);
					finish();
					break;
				case 1:
					Intent intent1 = new Intent();
					// intent1.setClass(PersonalSettings.this, CenterWenyiFoodActivity.class);
					startActivity(intent1);
					finish();
					break;
				case 2:
					Intent intent2= new Intent();
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
					// intent5.setClass(PersonalSettings.this, CenterIdeaActivity.class);
					startActivity(intent5);
					finish();
					break;
				case 6:
					Intent intent6 = new Intent();
					// intent6.setClass(PersonalSettings.this, CenterAboutMeActivity.class);
					startActivity(intent6);
					finish();
					break;
				}
			}
		});
	}
	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.login:
			if(isLogin()){
				String type = user.get_login_type();
				if("qq".equals(type)){
					logout(PersonalSettings.this, PersonalLoginCommon.class,SHARE_MEDIA.QQ);
				}else if("sina".equals(user.get_login_type())){
					logout(PersonalSettings.this, PersonalLoginCommon.class,SHARE_MEDIA.SINA);
				}else{
					logoutSucess();
				}
			}else{
				Intent intent = new Intent();
				intent.setClass(PersonalSettings.this, PersonalLoginCommon.class);
				startActivity(intent);
				finish();
			}
			break;
		default:
			break;
		}
	}
	public void logout(final Activity from,final Class to,SHARE_MEDIA media){
		mController.deleteOauth(PersonalSettings.this, media, new SocializeClientListener() {
			
			@Override
			public void onStart() {
				
			}
			
			@Override
			public void onComplete(int arg0, SocializeEntity arg1) {
				
			}
		});
		mController.loginout(PersonalSettings.this, new SocializeClientListener() {
			
			@Override
			public void onStart() {
				
			}
			
			@Override
			public void onComplete(int status, SocializeEntity entity) {
				if(status == 200){
					logoutSucess();
				}
			}
		});
	}
	private void logoutSucess(){
		Toast.makeText(PersonalSettings.this, getString(R.string.user_loginout), Toast.LENGTH_SHORT).show();
		login.setText(R.string.user_login);
		Util.resetUser(PersonalSettings.this);
		isLogin = false;
		setResult(RESULT_OK);
		/*Intent intent = new Intent(PersonalSettings.this,CenterActivity.class);
		startActivity(intent);*/
	}
}
