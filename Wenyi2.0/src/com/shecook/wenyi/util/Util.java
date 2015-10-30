package com.shecook.wenyi.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.WebViewActivity;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.cookbook.CookbookHomeworkDeatilActivity;
import com.shecook.wenyi.cookbook.CookbookItemDeatilActivity;
import com.shecook.wenyi.essay.EssayItemDeatilActivity;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.piazza.PizzaQuestionDeatilActivity;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class Util {

	public static final String TAG = "com.shecook.wenyi.util.Util-";

	public static final int SHOW_DIALOG = 100;
	public static final int DISMISS_DIALOG = 101;
	public static final int SHOW_DIALOG_COMMENTS = 102;
	public static final int DISMISS_DIALOG_COMMENTS = 103;
	public static final int SHOW_ZOOM_IMAGE = 104;
	public static final int DISMISS_ZOOM_IMAGE = 105;
	public static final int ADD_MENU_INFO = 106;
	public static final int DISMISS_DIALOG_INPUT = 107;
	public static final int DISMISS_DIALOG_TO = 108;
	public static final int PRAISE_GOOD = 111;
	public static final int HANDLER_GOOD_COUNT = 112;
	public static final int HANDLER_DEL_BUTTON = 120;

	public static final int DISMISS_DIALOG_SEARCH = 113;
	public static final int SHOW_DIALOG_SEARCH = 114;

	public static final int SHOW_DIALOG_DEL = 115;
	public static final int DISMISS_DIALOG_DEL = 116;
	public static final int DISMISS_DIALOG_DEL_ERROR = 119;
	public static final int RESET_IMAGE_BG = 117;

	public static final int SHOW_VERSION_DIALOG = 118;
	public static final int SHOW_MESSAGE_ERROR = 119;

	public static final int DISMISS_DIALOG_LOAD_MORE = 120;

	public static final int SHOW_DIALOG_PRE = 100;
	public static final int DISMISS_DIALOG_PRE = 101;

	public static final int ACTIVITY_EVERY_DAY_CHART = 110;

	public static int width;
	public static int height;
	public static float mScreenWidth = 0;

	public Context mContext;

	private static Object dataObject;

	public Util(Context context) {
		this.mContext = context;
		// width =
		// ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
	}

	public static int getWidth(Context context) {
		if (width == 0) {
			Display display = ((Activity) context).getWindowManager()
					.getDefaultDisplay();
			width = display.getWidth();
		}
		// height = display.getHeight();
		// Log.d(TAG, "width " + width + ",height " + height);
		return width;
	}

	public static int getHeight(Context context, int sourceWidth,
			int sourceHeight) {

		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		float descHeight = (sourceHeight / sourceWidth) * width;
		Log.d(TAG, "getHeight width " + width + ",sourceWidth " + sourceWidth
				+ ", sourceHeight " + sourceHeight + ",descHeight "
				+ descHeight);
		return width;
	}

	public static int getMetricsWidth(Context context) {
		if (mScreenWidth != 0) {

		}
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		return mScreenWidth;
	}

	public static int getMetricsHeigh(Context context, int sourceWidth,
			int sourceHeight) {
		if (mScreenWidth == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(dm);
			mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		}
		// int mScreenHeight = dm.heightPixels;
		float descHeight = mScreenWidth * sourceHeight / sourceWidth;
		// Log.d(TAG, "getMetricsHeigh mScreenWidth " + mScreenWidth +
		// ",sourceWidth " + sourceWidth + ",sourceHeight " + sourceHeight +
		// ", descHeight " + descHeight);
		return (int) descHeight;
	}

	public static int getAdapterMetricsWidth(Context context, float scale) {
		if (mScreenWidth != 0) {

		}
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		return (int) (mScreenWidth * scale);
	}

	public static int getAdapterMetricsHeigh(Context context, int sourceWidth,
			int sourceHeight, float scale) {
		if (mScreenWidth == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(dm);
			mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		}
		// int mScreenHeight = dm.heightPixels;
		if (sourceWidth == 0) {
			sourceWidth = (int) mScreenWidth;
			sourceHeight = (int) mScreenWidth;
		}
		float descHeight = scale * mScreenWidth * sourceHeight / sourceWidth;
		// Log.d(TAG, "getMetricsHeigh mScreenWidth " + mScreenWidth +
		// ",sourceWidth " + sourceWidth + ",sourceHeight " + sourceHeight +
		// ", descHeight " + descHeight);
		return (int) descHeight;
	}

	public static void setWidth(int width) {
		Util.width = width;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int height) {
		Util.height = height;
	}
	
	public static AlertDialog showDialog(Context context,String title, String message, android.content.DialogInterface.OnClickListener listener, android.content.DialogInterface.OnClickListener cancleListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context/*,R.style.maskDialog2*/);
		if(title != null){
			builder.setTitle(title);
		}
		if(message != null){
			builder.setMessage(message);
		}
		builder.setPositiveButton("确定", listener);
		builder.setNegativeButton("取消", cancleListener);
		return builder.create();
	}
	
	public static AlertDialog showLoadDataDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View view = inflater.inflate(R.layout.loading_data_dialog, null);

		AlertDialog dialog = builder.setView(view).create();
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	public static AlertDialog showLoadDataDialog(Context context, int infoId) {
		LayoutInflater inflater = LayoutInflater.from(context);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View view = inflater.inflate(R.layout.loading_data_dialog, null);

		if (infoId != -1) {
			TextView textView = (TextView) view
					.findViewById(R.id.load_data_info);
			textView.setText(infoId);
		}

		AlertDialog dialog = builder.setView(view).create();
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	public static Dialog showDialog(Context context, int layout) {
		LayoutInflater inflater = LayoutInflater.from(context);
		width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		Dialog dialog = new Dialog(context);
		View view = inflater.inflate(layout, null);
		dialog.setContentView(view);
		return dialog;
	}

	public static Dialog getBottomDialog(Context context, int layout) {
		int width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		LayoutInflater inflater = LayoutInflater.from(context);
		Dialog dialog = new Dialog(context,R.style.maskDialog);
		View view = inflater.inflate(layout, null);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.mybottomdialog); // 添加动画
		dialog.setContentView(view, new LinearLayout.LayoutParams(width,
				LayoutParams.WRAP_CONTENT));
		return dialog;
	}

	public static Dialog showAddCommentDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		final Dialog dialog = new Dialog(context, R.style.maskDialog);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.my_dialog);
		View view = inflater.inflate(R.layout.add_comments_dialog, null);

		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				dialog.dismiss();
				return false;
			}
		});
		dialog.setContentView(view, new LinearLayout.LayoutParams(width,
				LayoutParams.WRAP_CONTENT));
		return dialog;
	}

	public static Dialog showDialog(Context context, int layoutId,
			Bitmap bitmap, boolean cancel) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final Dialog dialog = new Dialog(context, R.style.maskDialog);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(layoutId,
				null);

		ImageView imageView = (ImageView) layout.findViewById(0/*
																 * R.id.
																 * cookbook_zoom_img
																 */);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		imageView.setImageBitmap(bitmap);
		width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		dialog.setContentView(layout, new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT));
		return dialog;
	}

	public static class MyDialog extends Dialog {

		Context context;
		int layoutId;

		public MyDialog(Context context, int theme) {
			super(context, theme);
			this.context = context;
		}

		public MyDialog(Context context, int theme, int layoutId) {
			super(context);
			this.context = context;
			this.layoutId = layoutId;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(layoutId);
		}

	}

	public static String formatTimeString(String str) {
		if (null == str || "".equals(str)) {
			return SimpleDateFormat.getDateInstance().format(new Date());
		}
		str = str.replace('T', ' ');
		StringBuffer buffer = new StringBuffer(str);
		int index = buffer.lastIndexOf(":");
		String last = buffer.substring(0, index);
		return last;
	}

	static Calendar calendar = Calendar.getInstance();

	public static String formatTimeString2(String time) {
		if (time != null && !"".equals(time)) {
			try {
				String[] time2 = time.split("T");
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				if (year != Integer.parseInt(time.substring(0, 4))) {
					return time2[0];
				}
				return time2[0].substring(5, time2[0].length());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public static String formatTime2HHmm(String time) {
		if (time != null && !"".equals(time)) {
			try {
				String[] time2 = time.split("T");
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				if (year != Integer.parseInt(time.substring(0, 4))) {
					return time2[0] + time2[1].substring(0, 5);
				}
				return time2[0].substring(5, time2[0].length());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public static int saveUserData(Context context, WenyiUser user) {
		SharedPreferences userInfo = null;
		try {
			userInfo = context.getSharedPreferences("user_info", 0);
			SharedPreferences.Editor editor = userInfo.edit();
			Log.e(TAG,
					"email: " + user.get_email() + ", message: "
							+ user.get_msgcount() + ",token "
							+ user.get_token());
			if (null != user.get_email() && !"".equals(user.get_email())) {
				editor.putString("useremail", user.get_email());
				wenyiUser.set_email(user.get_email());
			}
			if ((token = user.get_token()) != null && !"".equals(token)) {
				editor.putString("_token", token);
				wenyiUser.set_token(user.get_token());
			}
			if (user.get_mID() != null && !"".equals(user.get_mID())) {
				editor.putString("_mid", user.get_mID());
				wenyiUser.set_mID(user.get_mID());
			}

			if (user.get_nickname() != null && !"".equals(user.get_nickname())) {
				editor.putString("_nickname", user.get_nickname());
				wenyiUser.set_nickname(user.get_nickname());
			}

			if (user.get_uimage30() != null && !"".equals(user.get_uimage30())) {
				editor.putString("_uimage30", user.get_uimage30());
				wenyiUser.set_uimage30(user.get_uimage30());
			}

			if (user.get_score() != null && !"".equals(user.get_score())) {
				editor.putString("_score", user.get_score());
				wenyiUser.set_score(user.get_score());
			}

			if (user.get_level() != null && !"".equals(user.get_level())) {
				editor.putString("_level", user.get_level());
				wenyiUser.set_level(user.get_level());
			}

			if (user.get_msgcount() != null && !"".equals(user.get_msgcount())) {
				editor.putString("_msgcount", user.get_msgcount());
				wenyiUser.set_msgcount(user.get_msgcount());
			}

			editor.putString("userpasswd", user.get_password());
			editor.putString("_userguid", user.get_userguid());
			editor.putInt("_flag", user.get_flag());
			editor.putString("_uimage50", user.get_uimage50());
			editor.putString("_uimage180", user.get_uimage180());
			editor.putBoolean("islogin", user.is_isLogin());
			editor.commit();

			wenyiUser.set_password(user.get_password());
			wenyiUser.set_userguid(user.get_userguid());
			wenyiUser.set_flag(user.get_flag());
			wenyiUser.set_isLogin(user.is_isLogin());

			user = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userInfo = null;
		}
		return 0;
	}

	public static boolean resetUser(Context context) {
		SharedPreferences userInfo = null;
		try {
			userInfo = context.getSharedPreferences("user_info", 0);
			SharedPreferences.Editor editor = userInfo.edit();
			editor.putString("useremail", "");
			editor.putString("_token", "");
			// editor.putString("_mid", "");
			editor.putString("userpasswd", "");
			editor.putString("_userguid", "");
			editor.putString("_nickname", "");
			editor.putInt("_flag", -1);
			editor.putString("_uimage30", "");
			editor.putString("_uimage50", "");
			editor.putString("_uimage180", "");
			editor.putString("_score", "");
			editor.putString("_level", "");
			editor.putString("_msgcount", "");
			editor.putString("_level_score", "");
			editor.putBoolean("islogin", false);
			editor.commit();
			wenyiUser = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userInfo = null;
		}
		return false;
	}

	public static String mid = "";

	public static String getMid(Context context) {
		if (!"".equals(mid)) {
			return mid;
		}
		SharedPreferences userInfo = null;
		try {
			userInfo = context.getSharedPreferences("user_info", 0);
			mid = userInfo.getString("_mid", "");
		} catch (Exception e) {
			e.printStackTrace();
			mid = "";
		} finally {
		}
		userInfo = null;
		if ("".equals(mid)) {
			mid = UUID.randomUUID().toString();
			updateStringData(context, "_mid", mid);
		}
		return mid;
	}

	public static String token = "";

	public static String getToken(Context context) {
		if (null != token && !"".equals(token)) {
			return token;
		}
		SharedPreferences userInfo = null;
		try {
			userInfo = context.getSharedPreferences("user_info", 0);
			token = userInfo.getString("_token", "");
		} catch (Exception e) {
			e.printStackTrace();
			token = "";
		} finally {
		}
		userInfo = null;
		return token;
	}

	public static boolean updateBooleanData(Context context, String key,
			boolean value) {
		SharedPreferences userInfo = null;
		try {
			userInfo = context.getSharedPreferences("user_info", 0);
			SharedPreferences.Editor editor = userInfo.edit();
			editor.putBoolean(key, value);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return false;
	}

	public static boolean updateStringData(Context context, String key,
			String value) {
		SharedPreferences userInfo = null;
		try {
			Log.e("lixufeng", "updateStringData key " + key + ", value "
					+ value);
			userInfo = context.getSharedPreferences("user_info", 0);
			SharedPreferences.Editor editor = userInfo.edit();
			editor.putString(key, value);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return false;
	}

	public static JSONObject getCommonParam(Context context) {
		JSONObject sub = new JSONObject();
		if (TextUtils.isEmpty(mid = getMid(context))) {
			mid = UUID.randomUUID().toString();
			updateStringData(context, "_mid", mid);
		}
		try {
			Log.e(TAG, "getCommonParam token " + getToken(context) + ", mid "
					+ mid);
			sub.put("mtype", "android");
			sub.put("mid", mid);
			sub.put("token", token = getToken(context));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sub;
	}

	public static void commonHttpMethod(Activity activity, String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject jsonObject = new JSONObject();
		JSONObject commonsub = Util.getCommonParam(activity);
		try {
			if (paramsub != null) {
				jsonObject.put("param", paramsub);
			}
			jsonObject.put("common", commonsub);
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
	
	public static WenyiUser wenyiUser = null;

	public static WenyiUser getUserData(Context context) {
		SharedPreferences userInfo = null;
		try {
			if (null == wenyiUser) {
				wenyiUser = new WenyiUser();
			}
			userInfo = context.getSharedPreferences("user_info", 0);
			wenyiUser.set_email(userInfo.getString("useremail", ""));
			wenyiUser.set_password(userInfo.getString("userpasswd", ""));
			wenyiUser.set_userguid(userInfo.getString("_userguid", ""));
			wenyiUser.set_nickname(userInfo.getString("_nickname", ""));
			wenyiUser.set_flag(userInfo.getInt("_flag", -1));
			wenyiUser.set_uimage30(userInfo.getString("_uimage30", ""));
			wenyiUser.set_uimage50(userInfo.getString("_uimage50", ""));
			wenyiUser.set_uimage180(userInfo.getString("_uimage180", ""));
			wenyiUser.set_score(userInfo.getString("_score", ""));
			wenyiUser.set_level(userInfo.getString("_level", ""));
			wenyiUser.set_msgcount(userInfo.getString("_msgcount", ""));
			wenyiUser.set_level_core(userInfo.getString("_level_score", ""));
			wenyiUser.set_token(userInfo.getString("_token", ""));
			wenyiUser.set_mID(userInfo.getString("_mid", ""));
			wenyiUser.set_isLogin(userInfo.getBoolean("islogin", false));
		} catch (Exception e) {
			wenyiUser = null;
			e.printStackTrace();
		}
		userInfo = null;
		return wenyiUser;
	}

	public void removeAllList(List list) {
		for (int i = 0, j = list.size(); i < j; i++) {
			list.remove(0);
		}
	}

	public static boolean isLogin() {
		return false;
	}

	// 检测网络连接
	public static boolean checkConnection(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}

	public static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getTypeName().equals("WIFI")) {
			return true;
		}
		return false;
	}

	public static Bitmap reSizeBitmap(Context context, Bitmap bitmap) {
		width = getWidth(context);
		// 获取这个图片的宽和高
		int pwidth = bitmap.getWidth();
		int pheight = bitmap.getHeight();

		// 定义预转换成的图片的宽度和高度
		int newWidth = width;
		int newHeight = height;

		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / pwidth;
		float scaleHeight = ((float) newHeight) / pheight;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Log.d(TAG, "newWidth " + newWidth + ",newHeight " + newHeight
				+ ",width " + width);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static Bitmap reSizeBitmap(Context context, Bitmap bitmap, int colume) {
		width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		if (width == 0) {
		}
		// 获取这个图片的宽和高
		int pwidth = bitmap.getWidth();
		int pheight = bitmap.getHeight();

		// 定义预转换成的图片的宽度和高度
		int newWidth = (width - 20) / colume;
		int newHeight = newWidth;

		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / pwidth;
		float scaleHeight = ((float) newHeight) / pheight;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Log.d(TAG, "newWidth " + newWidth + ",newHeight " + newHeight
				+ ",width " + width);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static Bitmap reSizeBitmap(Context context, Bitmap bitmap,
			int colume, int widthx, int heighty) {
		getWidth(context);
		// 获取这个图片的宽和高
		int pwidth = bitmap.getWidth();
		int pheight = bitmap.getHeight();

		// 定义预转换成的图片的宽度和高度
		int newWidth = (width - widthx) / colume;
		int newHeight = height - heighty;
		;

		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / pwidth;
		float scaleHeight = ((float) newHeight) / pheight;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Log.d(TAG, "newWidth " + newWidth + ",newHeight " + newHeight
				+ ",width " + width);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static Bitmap reSizeBitmapForJuXing(Context context, Bitmap bitmap,
			int colume) {
		width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		if (width == 0) {
		}
		// 获取这个图片的宽和高
		int pwidth = bitmap.getWidth();
		int pheight = bitmap.getHeight();

		// 定义预转换成的图片的宽度和高度
		int newWidth = (width - 20) / colume;
		int newHeight = (pheight * newWidth) / pwidth;

		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / pwidth;
		float scaleHeight = ((float) newHeight) / pheight;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Log.d(TAG, "newWidth " + newWidth + ",newHeight " + newHeight
				+ ",width " + width);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static Bitmap reSizeBitmapForHeight(Context context, Bitmap bitmap,
			int colume) {
		width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		if (width == 0) {
		}
		// 获取这个图片的宽和高
		int pwidth = bitmap.getWidth();
		int pheight = bitmap.getHeight();

		// 定义预转换成的图片的宽度和高度
		int newWidth = (width - 20) / colume;
		int newHeight = (int) (((pheight * newWidth) / pwidth) * 1.1);

		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / pwidth;
		float scaleHeight = ((float) newHeight) / pheight;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Log.d(TAG, "newWidth " + newWidth + ",newHeight " + newHeight
				+ ",width " + width);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static Bitmap reSizeBitmapForHeight(Context context, Bitmap bitmap,
			int colume, int jianheight, int jianwidth) {
		width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		if (width == 0) {
		}
		// 获取这个图片的宽和高
		int pwidth = bitmap.getWidth();
		int pheight = bitmap.getHeight();

		// 定义预转换成的图片的宽度和高度
		int newWidth = (width - 20) / colume - jianwidth;

		int newHeight = (width - 20) / colume - jianheight;

		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / pwidth;
		float scaleHeight = ((float) newHeight) / pheight;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Log.d(TAG, "newWidth " + newWidth + ",newHeight " + newHeight
				+ ",width " + width);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static void checkNewVerson(final Context context) {
		new Thread() {
		}.start();
	}

	private static final Uri STORAGE_URI = Images.Media.EXTERNAL_CONTENT_URI;
	private static final String IMAGE_MIME_TYPE = "image/png";

	public static int addImageToDB(Context context, String title,
			String fileName, String path, String size) {

		try {
			ContentResolver contentResolver = context.getContentResolver();
			ContentValues values = new ContentValues(7);

			values.put(Images.Media.TITLE, title);
			values.put(Images.Media.DISPLAY_NAME, fileName);
			values.put(Images.Media.DATE_TAKEN,
					new Date(System.currentTimeMillis()).toString());
			values.put(Images.Media.MIME_TYPE, IMAGE_MIME_TYPE);
			values.put(Images.Media.ORIENTATION, 0);
			values.put(Images.Media.DATA, path);
			values.put(Images.Media.SIZE, size);

			Uri dataUri = contentResolver.insert(STORAGE_URI, values);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static void setDataObject(Object obj) {
		dataObject = obj;
	}

	public static Object getObject() {
		return dataObject;
	}

	public static void dispatchClickEvent(Context context, String event_type,
			String event_content, String[] info) {
		int type = -1;
		try {
			type = Integer.parseInt(event_type);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			type = -1;
		}
		Intent intent = null;
		if (type != -1) {
			switch (type) {
			case 10000:

				break;
			case 10001:
				intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(event_content));
				context.startActivity(intent);
				context.startActivity(intent);
				break;
			case 10002:
				intent = new Intent(context, EssayItemDeatilActivity.class);
				intent.putExtra("essaytitle", "" + info[0]);
				intent.putExtra("catalogtitle", "文怡随笔");
				intent.putExtra("articleid", "" + event_content);
				context.startActivity(intent);

				break;
			case 10003:
				intent = new Intent(context, CookbookItemDeatilActivity.class);
				intent.putExtra("cookbooktitle", "" + info[0]);
				intent.putExtra("recipeid", "" + event_content);
				context.startActivity(intent);

				break;
			case 10004:
				intent = new Intent(context, WebViewActivity.class);
				intent.putExtra("weburl", "" + event_content);
				context.startActivity(intent);

				break;
			case 10005:
				/*
				 * intent = new Intent(Intent.ACTION_VIEW,
				 * Uri.parse(event_content)); context.startActivity(intent);
				 */
				break;
			case 10006:
				intent = new Intent(context, PizzaQuestionDeatilActivity.class);
				intent.putExtra("topicid", "" + event_content);
				context.startActivity(intent);
			case 10007:
				intent = new Intent(context,
						CookbookHomeworkDeatilActivity.class);
				intent.putExtra("followid", "" + event_content);
				context.startActivity(intent);
				break;
			default:
				break;
			}
		} else {
			if (event_type.equals("article")) {
				intent = new Intent(context, EssayItemDeatilActivity.class);
				intent.putExtra("essaytitle", "");
				intent.putExtra("catalogtitle", "文怡随笔");
				intent.putExtra("articleid", "" + event_content);
				context.startActivity(intent);
			} else if (event_type.equals("article_comment")) {

			} else if (event_type.equals("recipe")) {
				intent = new Intent(context, CookbookItemDeatilActivity.class);
				intent.putExtra("cookbooktitle", "家常菜谱");
				intent.putExtra("recipeid", "" + event_content);
				context.startActivity(intent);
			} else if (event_type.equals("recipe_comment")) {

			} else if (event_type.equals("follow")) {
				intent = new Intent(context,
						CookbookHomeworkDeatilActivity.class);
				intent.putExtra("followid", "" + event_content);
				context.startActivity(intent);
			} else if (event_type.equals("follow_comment")) {

			} else if (event_type.equals("topic")) {
				intent = new Intent(context, PizzaQuestionDeatilActivity.class);
				intent.putExtra("topicid", "" + event_content);
				context.startActivity(intent);
			} else if (event_type.equals("topic_comment")) {

			}
		}

	}

}