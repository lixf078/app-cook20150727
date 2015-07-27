package com.shecook.wenyi.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

import com.shecook.wenyi.MainApplication;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.picasso.Transformation;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.util.lib.roundedimageview.RoundedDrawable.CornerLocation;
import com.shecook.wenyi.util.lib.roundedimageview.RoundedTransformationBuilder;
import com.shecook.wenyi.util.net.NetFailedResult;
import com.shecook.wenyi.util.net.NetRequest;
import com.shecook.wenyi.util.net.NetRequest.NetRequestCallback;
import com.shecook.wenyi.util.net.NetResult;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

public class UIHelper {

	/** 程序上下移动动画持续时间 */
	public static final int TRASLATION_ANIMATION_TIME = 550;

	/** 加载本地html的 CSS样式 */
	public static String VIEWPORT = "<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0\"/>";
	public static String CSS = "<style type=\"text/css\">"
			+ "img {width:100%; height:auto;left:15px;right: 15px;}"
			+ "p { font-family: STHEITI,Arial,'myCustomFont','STHeitiSC-Light';overflow: hidden;text-align:justify;}"
			+ "body { font-family: STHEITI,Arial,'myCustomFont','STHeitiSC-Light';    font-size:16px;   line-height:1.6;color:#333;  padding:0 5px;text-align:justify;}"
			+ "span {padding-right: 15px;padding-left: 15px;}"
			+ "</style></head>";

	/** 文章详情页header css样式 */
	public static String INFORMATION_INFO_CSS = "<style>.box{ height:30px; position:relative; margin:0 -5px;}"
			+ ".line{ height:1px; border-bottom:1px solid #B4B4B4; width:100%; position:absolute; left:0; top:15px;}"
			+ ".timer{ font-size:12px; color:#B4B4B4; background:url(http://wwwcdn.kimiss.net/misc/upload/a/image/2014/0929/165334_22178.png) no-repeat left center #fff; line-height:30px; position:absolute; left:0; top:0; padding:0 5px 0 15px;background-size:14px;}"
			+ "</style>";
	/** 文章详情页header本地html代码 */
	public static String INFORMATION_INFO_HEADER_HTML = "<div style='margin:10px -5px; font-size:14px;'>"
			+ "<img src='%1$s' style='width:100%%; margin-bottom:10px;'/>"// 两个%是为了android中的字符串格式化
			// + "护肤评测|贝蒂松岛枫松岛枫随碟附送"
			+ "%2$s"
			+ "</div>"
			+ "<div class='box'><div class='line'></div><div class='timer'>%3$s</div></div>";

	/** 获取网页中img标签图片的url JS函数 */
	public static String JS_IMG_LIST = "javascript:(function(){"
			+ "var images=document.querySelectorAll(\"img\");"
			+ " var imageUrls=[];" + "[].forEach.call(images, function(el) { "
			+ " if (el.getAttribute('imgurl')) {"
			+ "  imageUrls[imageUrls.length] = el.getAttribute('imgurl');"
			+ " } " + " }); " + " return JSON.stringify(imageUrls);"
			+ " })();  ";

	/** 获取网页中img 标签图片的url并且给img标签添加点击事件 */
	public static String JS_IMG_LIST_CLICK = "javascript:(function(){"
			+ "var images=document.querySelectorAll(\"img\");"
			+ " var imageUrls=[];" + "[].forEach.call(images, function(el) { "
			+ " if (el.getAttribute('src')) {"
			+ "  imageUrls[imageUrls.length] = el.getAttribute('src');" + " } "
			+ " }); " + "for(var i=0;i<images.length;i++){"
			+ " images[i].onclick=function()"
			+ "{ window.JSON.openPage(this.src);}" + "} "
			+ " return JSON.stringify(imageUrls);" + " })();  ";

	/** 帖子详情页，给 用户等级图标添加点击事件 */
	public static String JS_WEBPOST_USERLEVE = "javascript:(function () {document.getElementById('userGrade').setAttribute('href', 'kimiss://misc.kimiss.com/common/mapi/v151/?rd=530');})();";

	public static String SELETE_IMG_FROM_GALLERY_RESULT_URI_HOST = "com.android.providers.media.documents";
	public static String SELETE_IMG_FROM_GALLERY_RESULT_URI_PATH = "document/image";

	/**
	 * 应用内提示信息
	 * 
	 * @param context
	 * @param msg
	 * */
	public static void showAppToast(Context context, String msg) {
		// Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		ToastHelper.makeText(context, msg, ToastHelper.LENGTH_SHORT)
				.setAnimation(R.style.PopToast).show();
	}

	/** 将Volley的失败结果信息，转换成app的友好提示展示给用户 */
	public static void showVolleyRequestFailedMessage(Context context,
			VolleyError error) {
		VolleyErrorHelper.converVolleyError(error).makeToast(context);
	}

	public static void showEEErorr(Context context, String ee) {

		if (ee.equals("1")) {
			// Toast.makeText(context, "无错误", Toast.LENGTH_SHORT).show();
			showAppToast(context, "无错误");
		} else if (ee.equals("16")) {
			// Toast.makeText(context, "服务器操作失败", Toast.LENGTH_SHORT).show();
			showAppToast(context, "服务器操作失败");
		} else if (ee.equals("200")) {
			// Toast.makeText(context, "尚未登录", Toast.LENGTH_SHORT).show();
			showAppToast(context, "尚未登录");
		} else if (ee.equals("201")) {
			// Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show();
			showAppToast(context, "密码错误");
		} else if (ee.equals("202")) {
			// Toast.makeText(context, "用户名不存在", Toast.LENGTH_SHORT).show();
			showAppToast(context, "用户名不存在");
		} else if (ee.equals("203")) {
			// Toast.makeText(context, "未知原因的登录失败", Toast.LENGTH_SHORT).show();
			showAppToast(context, "未知原因的登录失败");
		} else if (ee.equals("300")) {
			// Toast.makeText(context, "绑定第三方失败", Toast.LENGTH_SHORT).show();
			showAppToast(context, "绑定第三方失败");
		} else if (ee.equals("301")) {
			// Toast.makeText(context, "此用户名已经被使用", Toast.LENGTH_SHORT).show();
			showAppToast(context, "此用户名已经被使用");
		} else if (ee.equals("400")) {
			// Toast.makeText(context, "使用的Token已过期",
			// Toast.LENGTH_SHORT).show();
			showAppToast(context, "使用的Token已过期");
		}
	}

	/**
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		
	}

	/** 生成向下滑动加载更多的FootView */
	public static View initLoadmoreFootView(Context context) {
		return null;
	}

	/**
	 * 改变 加载更多View的状态
	 * 
	 * @param view
	 *            footview
	 * @param state
	 *            0表示正常，1表示正在加载更多，2表示没有更多数据,3表示加载失败
	 * */
	public static void changeLoadmoreFootViewShow(View view, int state) {}

	/** 隐藏键盘 */
	public static void hideSoftInput(View paramEditText) {
		((InputMethodManager) MainApplication.getInstance().getSystemService(
				"input_method")).hideSoftInputFromWindow(
				paramEditText.getWindowToken(), 0);
	}

	/** 显示键盘 */
	public static void showKeyBoard(final View paramEditText) {
		paramEditText.requestFocus();
		paramEditText.post(new Runnable() {
			@Override
			public void run() {
				((InputMethodManager) MainApplication.getInstance()
						.getSystemService("input_method")).showSoftInput(
						paramEditText, 0);
			}
		});
	}

	/** 将带header和footer的ListView item点击的项的position 转成adapter 中项的 position */
	public static int convertHeadAndFootItemClickPosition(int clickItemPosition) {
		return clickItemPosition - 1;
	}

	/** 测量字符的宽度 */
	public static float measureTextLength(float textSize, String text) {
		Paint paint = new Paint();
		paint.setTextSize(textSize);
		return paint.measureText(text);
	}

	/** 测量View的宽度 */
	public static int measureCellWidth(Context context, View cell) {

		// We need a fake parent
		FrameLayout buffer = new FrameLayout(context);
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		buffer.addView(cell, layoutParams);

		cell.forceLayout();
		cell.measure(1000, 1000);

		int width = cell.getMeasuredWidth();

		buffer.removeAllViews();

		return width;
	}

	/** 将 dp 转换成px */
	public static float converDimenToPx(Context context, float dimen) {
		float dpi = getSreenDPI(context);

		return dimen * (dpi / 160);
	}

	/** 取得屏幕的宽度 */
	public int getScreenWidth(Context act) {
		final DisplayMetrics displayMetrics = new DisplayMetrics();

		WindowManager wm = (WindowManager) act
				.getSystemService(Context.WINDOW_SERVICE);

		wm.getDefaultDisplay().getMetrics(displayMetrics);
		final int width = displayMetrics.widthPixels;
		return width;

	}

	/** 取得屏幕高度 */
	public static int getScreenHeight(Context act) {
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) act
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	/** 取得屏幕 宽度的 dp值 */
	public static float getSreenWidthDp(Activity act) {
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		float midu = (float) displayMetrics.densityDpi;

		return displayMetrics.widthPixels / (midu / 160);
	}

	/** 获取屏幕的 dpi值 */
	public static float getSreenDPI(Context context) {
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);

		float midu = (float) displayMetrics.densityDpi;
		return midu;
	}

	/**
	 * 用于测量指定View的宽高参数
	 * 
	 * @param child
	 *            要测量的View
	 */
	public static void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight,
					View.MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/** 对TextView设置不同状态时其文字颜色。 */
	public static ColorStateList createColorStateList(int normal, int pressed,
			int focused, int unable) {
		int[] colors = new int[] { pressed, focused, normal, focused, unable,
				normal };
		int[][] states = new int[6][];
		states[0] = new int[] { android.R.attr.state_pressed,
				android.R.attr.state_enabled };
		states[1] = new int[] { android.R.attr.state_enabled,
				android.R.attr.state_focused };
		states[2] = new int[] { android.R.attr.state_enabled };
		states[3] = new int[] { android.R.attr.state_focused };
		states[4] = new int[] { android.R.attr.state_window_focused };
		states[5] = new int[] {};
		ColorStateList colorList = new ColorStateList(states, colors);
		return colorList;
	}

	/**
	 * 计算缩放尺寸的 高度，
	 * 
	 * @param orangeWith
	 *            原始 宽度
	 * @param orangeHeight
	 *            原始 高度
	 * @param desWith
	 *            目标宽度
	 * 
	 * @return 目标高度
	 * */
	public static int calculationWithAndHeight(int orangeWith,
			int orangeHeight, int desWith) {

		int desHeight = (orangeHeight * desWith) / orangeWith;

		return desHeight;

	}

	/**
	 * 下载图片到指定目录
	 * 
	 * @param image_url
	 *            图片url
	 * @param save_path
	 *            要保存到的路径
	 * 
	 * @param width
	 *            图片宽度
	 * @param height
	 *            图片高度
	 * 
	 * @param callback
	 *            图片下载的事件通知
	 */
	public static void saveImageFromNet(Context context,
			final String image_url, final String save_path, final int width,
			final int height, final SaveImageFromNetCallback callback) {
		
	}

	// -----------------------------下载相关---------start
	private static String net_flag = UIHelper.class.getName();

	/**
	 * 检测版本是否有更新，如果有更新，就弹出对话框
	 * 
	 * @param context
	 *            ,传递 application context
	 * @param isShowErrorInfo
	 *            联网检测版本信息时，出现错误，弹出错误信息, true 弹出
	 */
	public static void checkUpdateDialog(final Context context,
			final boolean isShowErrorInfo) {
		
	}

	/** 取消检测是否有新版本的 网络请求 */
	public static void cancelUpdateNetRequest() {
		VolleyUtils.getInstance().cancelRequest(net_flag);
	}

	/**
	 * 下载app的更新 apk
	 * 
	 * @param context
	 * @param down_url
	 *            下载链接,注意，这个url必须是 http 开头的，只能下载http协议的文件，否则报错
	 * */
	public static long downloadUpdateAkp(Context context, String down_url) {

		return doDownLoadFile(context, down_url, 0);
	}

	/**
	 * 下载 精彩推荐 apk
	 * 
	 * @param context
	 * @param down_url
	 *            下载链接,注意，这个url必须是 http 开头的，只能下载http协议的文件，否则报错
	 * */
	public static long downloadRecommendAkp(Context context, String down_url) {

		return doDownLoadFile(context, down_url, 1);
	}

	/**
	 * 调用系统自带的下载功能进行下载,必须是 2.2 及以上版本才可以使用;
	 * 
	 * @param context
	 * @param down_url
	 *            下载文件的url,注意，这个url必须是 http 开头的，只能下载http协议的文件，否则报错
	 * @param visible
	 *            通知栏的可见性，0表示只是在下载过程中可见，下载完成后不可见；1表示下载过程中和下载完成后都可见；2表示通知栏始终不可见
	 * 
	 * 
	 * 
	 * @return 系统下载器 唯一标示
	 * */
	public static long doDownLoadFile(Context context, String down_url,
			int visible) {
		return 0l;
	}// -----------------------------下载相关---------end

	/**
	 * 根据 图片url来打开 大图页
	 * 
	 * @param context
	 * @param pointImageUrl
	 *            指定打开的url
	 * @param imageUrls
	 *            要显示的所有图片的url数组
	 * */
	public static void openBigTouchImage(Context context, String pointImageUrl,
			String[] imageUrls) {
		// 
	}

	// ----------------------图片转换成圆形-------------start
	/**
	 * 获取用 picasso 加载图片时，将图片转换成圆形的 转换方法, 边框颜色暂时写死为 "#fe75a3"
	 * 
	 * @param borderWithDp
	 *            边框宽度
	 * @param cornerRadiusDp
	 *            圆形角度
	 * */
	public static Transformation getPicassoRoundedTrans(int borderWithDp,
			int cornerRadiusDp) {
		Transformation transformation = new RoundedTransformationBuilder()
				.borderColor(Color.parseColor("#fe75a3"))
				.borderWidthDp(borderWithDp).cornerRadiusDp(cornerRadiusDp)
				.oval(false).build();
		return transformation;
	}

	/**
	 * 获取用 picasso 加载图片时，将图片转换成圆形的 转换方法
	 * 
	 * @param borderWithDp
	 *            边框宽度
	 * @param borderColor
	 *            边框颜色
	 * @param cornerRadiusDp
	 *            边框弧度
	 * @param cornerRadiusDp
	 *            圆形角度
	 * */
	public static Transformation getPicassoRoundedTrans(int borderWithDp,
			int borderColor, int cornerRadiusDp, CornerLocation location) {
		Transformation transformation = new RoundedTransformationBuilder()
				.borderColor(borderColor).borderWidthDp(borderWithDp)
				.cornerLoaction(location).cornerRadiusDp(cornerRadiusDp)
				.oval(false).build();
		return transformation;
	}

	/**
	 * 获取用 picasso 加载图片时，将图片转换成圆形的 转换方法
	 * 
	 * @param borderWithDp
	 *            边框宽度，会自动转换成dp单位
	 * @param cornerRadiusDp
	 *            圆形角度，会自动转换成dp单位
	 * @param color
	 *            边框颜色
	 * */
	public static Transformation getPicassoRoundedTrans(int borderWithDp,
			int cornerRadiusDp, int color) {
		Transformation transformation = new RoundedTransformationBuilder()
				.borderColor(color).borderWidthDp(borderWithDp)
				.cornerRadiusDp(cornerRadiusDp).oval(false).build();
		return transformation;
	}

	/**
	 * 获取圆形drawable
	 * 
	 * @param context
	 * @param source
	 *            要转换成圆形drawable的bitmap
	 * @param borderWithDp
	 *            边框的宽度，会自动转换成dp单位
	 * @param cornerRadiusDp
	 *            圆弧角度，会自动转换成dp单位
	 * @param borderColor
	 *            边框颜色
	 * */
	public static Drawable getHeaderRoundedPlaceDrawable(Context context,
			Bitmap source, int borderWithDp, int cornerRadiusDp, int borderColor) {
		Transformation transformation = getPicassoRoundedTrans(borderWithDp,
				cornerRadiusDp, borderColor);

		Bitmap bmp = transformation.transform(source);

		return new BitmapDrawable(context.getResources(), bmp);
	}// ----------------------图片转换成圆形-------------end

	/** 从网路上下载图片 结果回调接口 */
	public static interface SaveImageFromNetCallback {
		/**
		 * 保存成功后回调
		 * 
		 * @param url
		 *            下载的url
		 * @param save_path
		 *            保存的路径
		 */
		public void onSucceed(String url, String save_path);

		/**
		 * 保存失败后回调
		 * 
		 * @param url
		 *            下载的url
		 * @param save_path
		 *            保存的路径
		 */
		public void onFail(String url, String save_path);
	}// class end

	/**
	 * Decode and sample down a bitmap from resources to the requested width and
	 * height.
	 * 
	 * @param res
	 *            The resources object containing the image data
	 * @param resId
	 *            The resource id of the image data
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// BEGIN_INCLUDE (read_bitmap_dimensions)
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// END_INCLUDE (read_bitmap_dimensions)

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * Decode and sample down a bitmap from a file to the requested width and
	 * height.
	 * 
	 * @param filename
	 *            The full path of the file to decode
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @param cache
	 *            The ImageCache used to find candidate bitmaps for use with
	 *            inBitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filename,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	/**
	 * Decode and sample down a bitmap from a file input stream to the requested
	 * width and height.
	 * 
	 * @param fileDescriptor
	 *            The file descriptor to read from
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @param cache
	 *            The ImageCache used to find candidate bitmaps for use with
	 *            inBitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromDescriptor(
			FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory
				.decodeFileDescriptor(fileDescriptor, null, options);
	}

	/**
	 * Calculate an inSampleSize for use in a
	 * {@link android.graphics.BitmapFactory.Options} object when decoding
	 * bitmaps using the decode* methods from
	 * {@link android.graphics.BitmapFactory}. This implementation calculates
	 * the closest inSampleSize that is a power of 2 and will result in the
	 * final decoded bitmap having a width and height equal to or larger than
	 * the requested width and height.
	 * 
	 * @param options
	 *            An options object with out* params already populated (run
	 *            through a decode* method with inJustDecodeBounds==true
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return The value to be used for inSampleSize
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// BEGIN_INCLUDE (calculate_sample_size)
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio;
			final int widthRatio;
			if (reqHeight == 0) {
				inSampleSize = (int) Math.floor((float) width / (float) reqWidth);
			} else if (reqWidth == 0) {
				inSampleSize = (int) Math.floor((float) height
						/ (float) reqHeight);
			} else {
				heightRatio = (int) Math.floor((float) height
						/ (float) reqHeight);
				widthRatio = (int) Math.floor((float) width / (float) reqWidth);
				inSampleSize = Math.min(heightRatio, widthRatio);
				// request.centerInside
				// ? Math.max(heightRatio, widthRatio)
				// : Math.min(heightRatio, widthRatio);
			}
		}
//
//		if (height > reqHeight || width > reqWidth) {
//
//			final int halfHeight = height / 2;
//			final int halfWidth = width / 2;
//
//			// Calculate the largest inSampleSize value that is a power of 2 and
//			// keeps both
//			// height and width larger than the requested height and width.
//			while ((halfHeight / inSampleSize) > reqHeight
//					&& (halfWidth / inSampleSize) > reqWidth) {
//				inSampleSize *= 2;
//			}
//
//			// This offers some additional logic in case the image has a strange
//			// aspect ratio. For example, a panorama may have a much larger
//			// width than height. In these cases the total pixels might still
//			// end up being too large to fit comfortably in memory, so we should
//			// be more aggressive with sample down the image (=larger
//			// inSampleSize).
//
//			long totalPixels = width * height / inSampleSize;
//
//			// Anything more than 2x the requested pixels we'll sample down
//			// further
//			final long totalReqPixelsCap = reqWidth * reqHeight * 2;
//
//			while (totalPixels > totalReqPixelsCap) {
//				inSampleSize *= 2;
//				totalPixels /= 2;
//			}
//		}
		return inSampleSize;
		// END_INCLUDE (calculate_sample_size)
	}

	// --------------------------------处理选择本地图片返回的uri--start
	/**
	 * 根据相册返回的 uri来获得 选择的图片路径</p> 4.4 以下使用
	 * 
	 * @param context
	 *            context
	 * @param uri
	 *            在相册选择完图片后，返回的uri
	 * @deprecated
	 */
	public static String getRealPathFromMediaUriOld(Context context,
			Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 根据相册返回的 uri来获得 选择的图片路径</p> 所有版本andriod系统都可以用
	 * 
	 * @param context
	 *            context
	 * @param uri
	 *            在相册选择完图片后，返回的uri
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getRealPathFromMediaUri(final Context context,
			final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}// --------------------------------处理选择本地图片返回的uri--end

}
