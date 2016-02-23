package com.shecook.wenyi;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.shecook.wenyi.common.CustomShareBoard;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.cookbook.CookbookCollectionActivity;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.net.NetResult;
import com.shecook.wenyi.util.net.NetResultFactory;
import com.shecook.wenyi.util.volleybox.VolleyUtils;
import com.shecook.wenyi.util.volleybox.WenyiJSONObjectRequest;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.bean.CustomPlatform;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.OnSnsPlatformClickListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.GooglePlusShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.TwitterShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class BaseActivity extends FragmentActivity {

	public static String TAG = "BaseActivity";
	public static boolean isLogin;
	public static WenyiUser user;
	public String userGuid;
	public String pwd = "";

	public static boolean isNeedUpdate;
	public static UMSocialService mController;
	public String appId_wx = "wxf89758f00762d524";
	public String appSecret = "db426a9829e4b49a0dcac7b4162da6b6";

	public void openShareForCookbook(HashMap<String, String> map, final String recipeid) {
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN, SHARE_MEDIA.EMAIL,
				SHARE_MEDIA.SMS, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.TENCENT);
		CustomPlatform customPlatform = new CustomPlatform("收 藏", R.drawable.my_collection);
		customPlatform.mClickListener = new OnSnsPlatformClickListener() {

			@Override
			public void onClick(Context arg0, SocializeEntity arg1, SnsPostListener arg2) {
				Intent intent = new Intent(BaseActivity.this, CookbookCollectionActivity.class);
				intent.putExtra("recipeid", recipeid);
				intent.putExtra("event", "add");
				startActivity(intent);
			}
		};
		mController.getConfig().addCustomPlatform(customPlatform);
		configSso(map);
		// mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
		// SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA);
		mController.openShare(BaseActivity.this, false);
	}

	public void openShare(HashMap<String, String> map) {

		mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
				SHARE_MEDIA.GENERIC);
		configSso(map);
		mController.openShare(BaseActivity.this, false);
		/*
		 * UMWXHandler wxHandler = new UMWXHandler(BaseActivity.this, appId_wx,
		 * appSecret); wxHandler.addToSocialSDK(); UMWXHandler wxCircleHandler =
		 * new UMWXHandler(BaseActivity.this, appId_wx, appSecret);
		 * wxCircleHandler.setToCircle(true); wxCircleHandler.addToSocialSDK();
		 * 
		 * mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
		 * SHARE_MEDIA.DOUBAN, SHARE_MEDIA.EMAIL,SHARE_MEDIA.SMS,SHARE_MEDIA.QQ,
		 * SHARE_MEDIA.QZONE, SHARE_MEDIA.TENCENT); configSso(map);
		 */}

	public void openShare(String content, String url) {
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN, SHARE_MEDIA.EMAIL,
				SHARE_MEDIA.SMS, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.TENCENT);
		mController.setShareContent(content);

		UMImage image = new UMImage(BaseActivity.this, url);
		mController.setShareImage(image);

		mController.openShare(BaseActivity.this, false);
	}

	/**
	 * @Title: configSso @Description: 配置sso授权Handler @return void @throws
	 */
	private void configSso(HashMap<String, String> map) {
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		String title = map.get("title");
		String content = map.get("content");
		String webUrl = map.get("url");
		String imageUrl = map.get("image");
		String shareContent = "";
		String weixinShareContent = "";

		String from = map.get("from");
		if (from != null || !"".equals(from)) {
			if ("book".equals(from)) {
				shareContent = "我正在使用宇宙无敌超级大美丽@文怡 的菜谱APP@文怡家常菜 ，我能吃香的，喝辣的，掌握一道菜馋死别人的本事，还能看画，读随笔，你都没有哦！【" + title
						+ "】 肉指头戳" + webUrl;
				weixinShareContent = "我正在使用宇宙无敌超级大美丽文怡的菜谱APP ，我能吃香的，喝辣的，你有吗？";
			} else {
				if (content.length() > 30) {
					shareContent = "我正在看@文怡家常菜 的@文怡 随笔【" + title + "】 " + content.substring(0, 30) + "...欲知后事如何，请狂戳"
							+ webUrl;
					weixinShareContent = "【" + title + "】 " + content.substring(0, 30);
				} else {
					shareContent = "我正在看@文怡家常菜 的@文怡 随笔【" + title + "】 ";
					weixinShareContent = "【" + title + "】 ";
				}
			}
		} else {
			shareContent = "我正在看@文怡家常菜 的@文怡 随笔【" + title + "】 " + content.substring(0, 30) + "...欲知后事如何，请狂戳" + webUrl;
			weixinShareContent = "【" + title + "】 " + content.substring(0, 30);
		}

		mController.getConfig().setSsoHandler(new SinaSsoHandler());

		UMImage localImage = new UMImage(BaseActivity.this, imageUrl);

		// 设置微信分享
		WeiXinShareContent weixinContent = new WeiXinShareContent(localImage);
		weixinContent.setShareContent(weixinShareContent);
		weixinContent.setTitle(title);
		weixinContent.setTargetUrl(webUrl);
		mController.setShareMedia(weixinContent);

		// 设置朋友圈分享的内容
		CircleShareContent circleMedia = new CircleShareContent(localImage);
		circleMedia.setShareContent(weixinShareContent);
		circleMedia.setTitle(title);
		circleMedia.setAppWebSite(webUrl);
		circleMedia.setTargetUrl(webUrl);
		mController.setShareMedia(circleMedia);

		/*
		 * // 设置QQ空间分享内容 QZoneShareContent qzone = new QZoneShareContent();
		 * qzone.setShareContent(shareContent); qzone.setTargetUrl(webUrl);
		 * qzone.setTitle(title); qzone.setShareImage(localImage);
		 * qzone.setAppWebSite(webUrl); mController.setShareMedia(qzone);
		 * 
		 * QQShareContent qqShareContent = new QQShareContent(localImage);
		 * qqShareContent.setShareContent(shareContent);
		 * qqShareContent.setTitle(title); qqShareContent
		 * .setShareImage(localImage); qqShareContent.setTargetUrl(webUrl);
		 * mController.setShareMedia(qqShareContent);
		 * 
		 * // 设置tencent分享内容 TencentWbShareContent tencent = new
		 * TencentWbShareContent(localImage);
		 * tencent.setShareContent(shareContent); tencent.setAppWebSite(webUrl);
		 * mController.setShareMedia(tencent);
		 */

		// 设置新浪分享内容
		SinaShareContent sinaContent = new SinaShareContent(localImage);
		sinaContent.setShareContent(shareContent);
		sinaContent.setAppWebSite(webUrl);
		mController.setShareMedia(sinaContent);

	}

	/**
	 * 配置分享平台参数</br>
	 */
	private void configPlatforms() {
		// 添加新浪SSO授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加微信、微信朋友圈平台
		addPlatfromForWX();
	}

	public boolean netConnected = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PushAgent.getInstance(this).onAppStart();
		VolleyUtils.getInstance().initVolley(getApplicationContext());

		// configPlatforms();
		// setShareContent();

		if (!Util.checkConnection(this)) {
			Toast.makeText(this, getString(R.string.network_has_problem), Toast.LENGTH_SHORT).show();
			netConnected = false;
		}
		checkLogin();
		userGuid = Util.wenyiUser == null ? Util.getUserData(BaseActivity.this).get_userguid()
				: Util.wenyiUser.get_userguid();
		pwd = Util.wenyiUser == null ? Util.getUserData(BaseActivity.this).get_password()
				: Util.wenyiUser.get_password();
		returnButton = (ImageView) findViewById(R.id.return_img);
		if (returnButton != null) {
			returnButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		// 微博
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		addSharePlatform();
	}

	ImageView returnButton;

	public void addPlatfromForWX() {
		UMWXHandler wxHandler = new UMWXHandler(BaseActivity.this, appId_wx, appSecret);
		wxHandler.addToSocialSDK();
		UMWXHandler wxCircleHandler = new UMWXHandler(BaseActivity.this, appId_wx, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	public void checkLogin() {
		user = Util.getUserData(BaseActivity.this);
		Log.e(TAG, "checkLogin " + user);
		if (user != null && user.is_isLogin()) {
			isLogin = true;
		} else {
			isLogin = false;
		}
	}

	public boolean isLogin() {
		checkLogin();
		Log.e(TAG, "isLogin " + isLogin);
		return isLogin;
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkLogin();
		MobclickAgent.onResume(this);
		;
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean isFinishing() {
		System.gc();
		return super.isFinishing();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	/** 使用SSO授权必须添加如下代码 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);

		mController.getConfig().getSsoHandlersMap();
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	public String basePost(String url, String strRequest, NetResultFactory resultFactory,
			Listener<NetResult> resultListener, ErrorListener errorListener) {

		VolleyUtils.getInstance().initVolley(getApplicationContext());
		WenyiJSONObjectRequest wenyiRequest = new WenyiJSONObjectRequest(Method.POST, url, strRequest, resultFactory,
				resultListener, errorListener);

		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void getTokenFrom(boolean force, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if (!force && user.get_token() != null) {
			// return ;
		}
		JSONObject jsonObject = new JSONObject();
		JSONObject sub = Util.getCommonParam(BaseActivity.this, force);
		try {
			jsonObject.put("common", sub);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JsonObjectRequest wenyiRequest = new JsonObjectRequest(Method.POST, HttpUrls.GET_TOKEN, jsonObject,
				resultListener, errorListener);

		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	public void getCatalog(String url, JSONObject jsonObject, Listener<JSONObject> resultListener,
			ErrorListener errorListener) {
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}
		JSONObject sub = Util.getCommonParam(BaseActivity.this);
		try {
			jsonObject.put("common", sub);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest wenyiRequest = new JsonObjectRequest(Method.POST, url, jsonObject, resultListener,
				errorListener);

		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	public void userOperator(String url, JSONObject jsonObject, Listener<JSONObject> resultListener,
			ErrorListener errorListener) {
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}
		JSONObject sub = Util.getCommonParam(BaseActivity.this);
		try {
			jsonObject.put("common", sub);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest wenyiRequest = new JsonObjectRequest(Method.POST, url, jsonObject, resultListener,
				errorListener);

		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	// new share
	private boolean isEmptyForSsoHandler() {
		SocializeConfig socializeConfig = mController.getConfig();
		SparseArray<UMSsoHandler> sparseArray = socializeConfig.getSsoHandlersMap();

		if (sparseArray != null) {
			return sparseArray.size() > 0 ? false : true;
		}
		return true;
	}

	private void addSharePlatform() {
		addWXPlatform();
		addWXCirclePlatform();
	}

	private void addWXPlatform() {
		// 微信
		UMWXHandler wxHandler = new UMWXHandler(BaseActivity.this, appId_wx, appSecret);
		wxHandler.addToSocialSDK();
	}

	private void addWXCirclePlatform() {
		// 朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(BaseActivity.this, appId_wx, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	public void shareCookbook(HashMap<String, String> map, String recipeid) {

		if (isEmptyForSsoHandler()) {
			addSharePlatform();
		}

		configSso(map);
		CustomShareBoard shareBoard = new CustomShareBoard(this, mController, recipeid);
		shareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
	}
	
	public void shareEssay(HashMap<String, String> map) {

		if (isEmptyForSsoHandler()) {
			addSharePlatform();
		}

		configSso(map);
		CustomShareBoard shareBoard = new CustomShareBoard(this, mController);
		shareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
	}
}
