package com.shecook.wenyi.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class CommonUpload {
	public static final String TAG = "CommonUpload";
	public static final int CAMERA_WITH_DATA = 0x11;// 请求相机功能
	public static final int CAMERA_PICK_PHOTO = 0x13;// 请求相机功能
	public static final int PHOTO_PICKED_WITH_DATA = 0x12; // 请求相册
	public static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
	public Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);// 调用相册用Uri
	public Uri cameraImageUri = null; // 调用相机用的Uri
	public static String imageString = "file:///sdcard/";

	private static Activity mContext;
	
	public static void commonMethod(Activity context, String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener,
			String files) {
		mContext = context;
		JSONObject jsonObject = new JSONObject();
		JSONObject commonsub = Util.getCommonParam(mContext);
		try {
			if (paramsub != null) {
				jsonObject.put("param", paramsub);
			}
			// commonsub.put("mid", "5A1469CD-4819-4863-A934-8871CA1A0281");
			// commonsub.put("token", "591f3c51eca2483b932ed1a64b896a63");
			if (!TextUtils.isEmpty(files)) {
				jsonObject.put("files", files);
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
	
	public void showCameraDialog(Context context) {
		mContext = (Activity) context;
		// 创建一个对话框
		Dialog dialog = new AlertDialog.Builder(context)
				.setTitle("上传照片")
				// 创建标题
				.setMessage("我要上传照片")
				// 设置对话框中的内容
				.setPositiveButton("拍照", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						cameraImageUri = getUri();
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);// action is capture
						intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
						mContext.startActivityForResult(intent, CAMERA_WITH_DATA);// or TAKE_SMALL_PICTURE
					}
				})
				.setNegativeButton("相册", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// getPhotoPickIntent();
						startPickPhotoActivity();
					}
				}).create();
		// 显示对话框
		dialog.show();

	}

	public Uri getUri() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		System.out.println(imageString + dateFormat.format(date) + ".jpg");
		return Uri.parse(imageString + dateFormat.format(date) + ".jpg");
	}
	
	
	public static final int SELECT_PIC_KITKAT = 1;
	public static final int SELECT_PIC = 1;

	private void startPickPhotoActivity() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);// ACTION_OPEN_DOCUMENT
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			mContext.startActivityForResult(intent, SELECT_PIC_KITKAT);
		} else {
			mContext.startActivityForResult(intent, SELECT_PIC);
		}
	}

	public static final Bitmap getBitmap(String fileName) {
		Bitmap bitmap = null;
		try {
			Options options = new Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, options);
			options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
					(double) options.outWidth / 1024f,
					(double) options.outHeight / 1024f)));
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(fileName, options);
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		}

		return bitmap;
	}

	public static String getPath(final Context context, final Uri uri) {

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
	}

	public static String selectImage(Context context, Intent data) {
		Uri selectedImage = data.getData();
		// Log.e(TAG, selectedImage.toString());
		if (selectedImage != null) {
			String uriStr = selectedImage.toString();
			String path = uriStr.substring(10, uriStr.length());
			if (path.startsWith("com.android.gallery3d")) {
				Log.e(TAG,
						"It's auto backup pic path:" + selectedImage.toString());
				return null;
			}
		}
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();
		return picturePath;
	}
}