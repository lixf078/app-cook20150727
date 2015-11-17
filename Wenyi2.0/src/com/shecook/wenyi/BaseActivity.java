package com.shecook.wenyi;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMWXHandler;
import com.umeng.socialize.controller.listener.SocializeListeners.OnCustomPlatformClickListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.CircleShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.media.WeiXinShareContent;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;

public class BaseActivity extends FragmentActivity {

	public static String TAG = "BaseActivity";
	public static boolean isLogin;
	public static WenyiUser user;
	public String userGuid;
	public String pwd = "";

	public static boolean isNeedUpdate;
	public static UMSocialService mController = UMServiceFactory.getUMSocialService(
			"com.umeng.login", RequestType.SOCIAL);
	public String appId_wx = "wxf89758f00762d524";

	public void openShareForCookbook(HashMap<String, String> map, final String recipeid) {
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN, SHARE_MEDIA.EMAIL,SHARE_MEDIA.SMS,SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.TENCENT);
		CustomPlatform customPlatform = new CustomPlatform("copy_link","收 藏", R.drawable.my_collection);
		customPlatform.mClickListener = new OnCustomPlatformClickListener() {
			
			@Override
			public void onClick(CustomPlatform context, SocializeEntity entity,
					SnsPostListener listener) {
				Intent intent = new Intent(BaseActivity.this, CookbookCollectionActivity.class);
				intent.putExtra("recipeid", recipeid);
				intent.putExtra("event", "add");
				startActivity(intent);
			}
		};
		mController.getConfig().addCustomPlatform(customPlatform);
		configSso(map);
	}

	public void openShare(HashMap<String, String> map) {
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN, SHARE_MEDIA.EMAIL,SHARE_MEDIA.SMS,SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.TENCENT);
		configSso(map);
	}
	
	public void openShare(String content, String url) {
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN, SHARE_MEDIA.EMAIL,SHARE_MEDIA.SMS,SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.TENCENT);
		addPlatfromForWX("");
		mController.setShareContent(content);

		UMImage image = new UMImage(BaseActivity.this, url);
		mController.setShareImage(image);

		mController.openShare(BaseActivity.this, false);
	}

	/**
	 * @Title: configSso
	 * @Description: 配置sso授权Handler
	 * @return void
	 * @throws
	 */
	private void configSso(HashMap<String, String> map) {
		String title = map.get("title");
		String layer = map.get("layer");
		String content = map.get("content");
		String webUrl = map.get("url");
		String imageUrl = map.get("image");

		String shareContent = "";
		String weixinShareContent = "";

		String from = map.get("from");
		if(from != null || !"".equals(from)){
			if("book".equals(from)){
				shareContent = "我正在使用宇宙无敌超级大美丽@文怡 的菜谱APP@文怡家常菜 ，我能吃香的，喝辣的，掌握一道菜馋死别人的本事，还能看画，读随笔，你都没有哦！【"+ title + "】 肉指头戳" + webUrl;
				weixinShareContent = "我正在使用宇宙无敌超级大美丽文怡的菜谱APP ，我能吃香的，喝辣的，你有吗？";
			}else{
				shareContent = "我正在看@文怡家常菜 的@文怡 随笔【" + title + "】 " + content.substring(0, 30) + "...欲知后事如何，请狂戳" + webUrl;
				weixinShareContent = "【" + title + "】 " + content.substring(0, 30);
			}
		}else{
			shareContent = "我正在看@文怡家常菜 的@文怡 随笔【" + title + "】 " + content.substring(0, 30) + "...欲知后事如何，请狂戳" + webUrl;
			weixinShareContent = "【" + title + "】 " + content.substring(0, 30);
		}

		mController.getConfig().supportWXPlatform(
				BaseActivity.this, appId_wx, "http://www.shecook.com/");
		mController.getConfig().supportWXCirclePlatform(
				BaseActivity.this, appId_wx, "http://www.shecook.com/");
		mController.getConfig().supportQQPlatform(BaseActivity.this, "100424468",
				"http://www.umeng.com/social");

		// 配置SSO
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		mController.getConfig().setSsoHandler(
				new QZoneSsoHandler(BaseActivity.this));

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

		/*// 设置QQ空间分享内容
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent(shareContent);
		qzone.setTargetUrl(webUrl);
		qzone.setTitle(title);
		qzone.setShareImage(localImage);
		qzone.setAppWebSite(webUrl);
		mController.setShareMedia(qzone);

		QQShareContent qqShareContent = new QQShareContent(localImage);
		qqShareContent.setShareContent(shareContent);
		qqShareContent.setTitle(title);
		qqShareContent
				.setShareImage(localImage);
		qqShareContent.setTargetUrl(webUrl);
		mController.setShareMedia(qqShareContent);

		// 设置tencent分享内容
		TencentWbShareContent tencent = new TencentWbShareContent(localImage);
		tencent.setShareContent(shareContent);
		tencent.setAppWebSite(webUrl);
		mController.setShareMedia(tencent);*/

		// 设置新浪分享内容
		SinaShareContent sinaContent = new SinaShareContent(localImage);
		sinaContent.setShareContent(shareContent);
		sinaContent.setAppWebSite(webUrl);
		mController.setShareMedia(sinaContent);

		mController.openShare(BaseActivity.this, false);
	}

	public boolean netConnected = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PushAgent.getInstance(this).onAppStart();
		VolleyUtils.getInstance().initVolley(getApplicationContext());
		
		if(!Util.checkConnection(this)){
			Toast.makeText(this, getString(R.string.network_has_problem), Toast.LENGTH_SHORT).show();
			netConnected = false;
		}
		checkLogin();
		userGuid = Util.wenyiUser == null?Util.getUserData(BaseActivity.this).get_userguid():Util.wenyiUser.get_userguid();
		pwd = Util.wenyiUser == null?Util.getUserData(BaseActivity.this).get_password():Util.wenyiUser.get_password();
		returnButton = (ImageView) findViewById(R.id.return_img);
		if(returnButton != null){
			returnButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
	}

	ImageView returnButton;
	public void removePlatform() {

	}

	public void addPlatfromForWX(String url) {
		UMWXHandler wx = mController.getConfig().supportWXPlatform(
				BaseActivity.this, appId_wx, "http://www.wenyijcc.com");
		wx.setWXTitle(getString(R.string.share_to_wenxin));
		UMWXHandler wxp = mController.getConfig().supportWXCirclePlatform(
				BaseActivity.this, appId_wx, "http://www.wenyijcc.com");
		wxp.setCircleTitle(getString(R.string.share_to_wenxin_py));
	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private void addWXPlatform() {

		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wx967daebe835fbeac";
		// 微信图文分享,音乐必须设置一个url
		String contentUrl = "http://m.babytree.com/app/#area-1";
		// 添加微信平台
		UMWXHandler wxHandler = mController.getConfig().supportWXPlatform(
				BaseActivity.this, appId_wx, contentUrl);
		wxHandler.setWXTitle("友盟社会化组件还不错-WXHandler...");

		UMImage mUMImgBitmap = new UMImage(BaseActivity.this,
				"http://www.umeng.com/images/pic/banner_module_social.png");

		UMusic uMusic = new UMusic("http://sns.whalecloud.com/test_music.mp3");
		uMusic.setAuthor("zhangliyong");
		uMusic.setTitle("天籁之音");
		// uMusic.setThumb("http://www.umeng.com/images/pic/banner_module_social.png");
		// 非url类型的缩略图需要传递一个UMImage的对象
		uMusic.setThumb(mUMImgBitmap);
		//
		// 视频分享
		UMVideo umVedio = new UMVideo(
				"http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
		umVedio.setTitle("友盟社会化组件视频");
		// umVedio.setThumb("http://www.umeng.com/images/pic/banner_module_social.png");
		umVedio.setThumb(mUMImgBitmap);
		// 设置分享文字内容
		mController
				.setShareContent("友盟社会化组件还不错，让移动应用快速整合社交分享功能。www.umeng.com/social");
		// mController.setShareContent(null);
		// 设置分享图片
		// mController.setShareMedia(mUMImgBitmap);
		// 支持微信朋友圈
		UMWXHandler circleHandler = mController.getConfig()
				.supportWXCirclePlatform(BaseActivity.this, appId_wx, contentUrl);
		circleHandler.setCircleTitle("友盟社会化组件还不错-CircleHandler...");

		//
		mController.getConfig().registerListener(new SnsPostListener() {

			@Override
			public void onStart() {
				Toast.makeText(BaseActivity.this, "weixin -- xxxx onStart", 0).show();
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				Toast.makeText(BaseActivity.this, platform + " code = " + eCode, 0)
						.show();
			}
		});

		mController.openShare(BaseActivity.this, false);

		// mController.postShare(BaseActivity.this, SHARE_MEDIA.WEIXIN, new
		// SnsPostListener() {
		//
		// @Override
		// public void onStart() {
		//
		// }
		//
		// @Override
		// public void onComplete(SHARE_MEDIA platform, int eCode,
		// SocializeEntity entity) {
		// if (eCode == StatusCode.ST_CODE_SUCCESSED) {
		// Toast.makeText(BaseActivity.this, "微信或者微信朋友圈分享完成啦",
		// Toast.LENGTH_SHORT).show();
		// } else {
		// Toast.makeText(BaseActivity.this, "微信或者微信朋友圈分享失败...",
		// Toast.LENGTH_SHORT).show();
		// }
		// }
		// });
	}

	public void checkLogin(){
		 user = Util.getUserData(BaseActivity.this);
		 Log.e(TAG, "checkLogin " + user);
		 if(user != null && user.is_isLogin()){
			 isLogin = true;
		 }else{
			 isLogin = false;
		 }
	}

	public boolean isLogin(){
		checkLogin();
		Log.e(TAG, "isLogin " + isLogin);
		return isLogin;
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkLogin();
		MobclickAgent.onResume(this);;
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

	/**使用SSO授权必须添加如下代码 */
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}*/
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Log.d("lixufeng", "baseActivity onActivityResult");
	}
	
	public String basePost(String url, String strRequest,
			NetResultFactory resultFactory, Listener<NetResult> resultListener,
			ErrorListener errorListener){
		
		VolleyUtils.getInstance().initVolley(getApplicationContext());
		WenyiJSONObjectRequest wenyiRequest = new WenyiJSONObjectRequest(Method.POST, 
				url, strRequest, resultFactory, resultListener, errorListener);
		
		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	public void getTokenFrom(boolean force, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if(!force && user.get_token() != null){
			// return ;
		}
		JSONObject jsonObject = new JSONObject();
		JSONObject sub = Util.getCommonParam(BaseActivity.this);
		try {
			jsonObject.put("common", sub);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JsonObjectRequest wenyiRequest = new JsonObjectRequest(Method.POST, 
				HttpUrls.GET_TOKEN, jsonObject, resultListener, errorListener);
		
		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}
	
	public void getCatalog(String url, JSONObject jsonObject, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if(null == jsonObject){
			jsonObject = new JSONObject();
		}
		JSONObject sub = Util.getCommonParam(BaseActivity.this);
		try {
			jsonObject.put("common", sub);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JsonObjectRequest wenyiRequest = new JsonObjectRequest(
				Method.POST, url, jsonObject, resultListener, errorListener);

		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}
	
	public void userOperator(String url, JSONObject jsonObject, Listener<JSONObject> resultListener, ErrorListener errorListener){
		if(null == jsonObject){
			jsonObject = new JSONObject();
		}
		JSONObject sub = Util.getCommonParam(BaseActivity.this);
		try {
			jsonObject.put("common", sub);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JsonObjectRequest wenyiRequest = new JsonObjectRequest(
				Method.POST, url, jsonObject, resultListener, errorListener);

		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}
	
}
