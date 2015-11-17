package com.shecook.wenyi.group;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class GroupCreateActivity extends BaseActivity {

	private static final int UPLOAD_SUCESS = 1;
	private static final int UPLOAD_FAILED = 2;
	private static final int GROUP_CREATE_SUCESS = 3;
	private static final int GROUP_CREATE_FAILED = 4;
	private ImageView camera;
	private EditText name, content;
	private TextView right_img, return_textview, middle_title;
	private String circleImage = "";
	private String photoPath = "";

	private String circleid = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_create);

		// processParam("");
		initView();
	}

	public void initView() {
		circleid = getIntent().getStringExtra("circleid");
		camera = (ImageView) findViewById(R.id.group_create_camera);
		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showCameraDialog();
			}
		});
		name = (EditText) findViewById(R.id.group_create_name);
		
		content = (EditText) findViewById(R.id.group_create_content);
		
		return_textview = (TextView) findViewById(R.id.return_textview);
		return_textview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		middle_title = (TextView) findViewById(R.id.middle_title);
		right_img = (TextView) findViewById(R.id.right_textview);
		
		if(TextUtils.isEmpty(circleid)){
			right_img.setText("创建");
			middle_title.setText("创建圈子");
		}else{
			right_img.setText("修改");
			middle_title.setText("修改圈子");
		}
		right_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String filePath = null;
				if (imgType == 0) {
					filePath = photoPath;
				} else if (imgType == 1) {
					filePath = cameraImageUri.getPath();
				}
				handler.sendEmptyMessage(HttpStatus.STATUS_SHOWPROGRESS);
				processParam(filePath);
			}
		});
		
		
		if(!TextUtils.isEmpty(getIntent().getStringExtra("title"))){
			name.setText("" + getIntent().getStringExtra("title"));
		}
		if(!TextUtils.isEmpty(getIntent().getStringExtra("desc"))){
			content.setText("" + getIntent().getStringExtra("desc"));
		}
		
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_SHOWPROGRESS:
				// Util.showDialog(GroupCreateActivity.this, -1);
				break;
			case UPLOAD_SUCESS:
				createCircle();
				break;
			case UPLOAD_FAILED:
				finish();
				break;
			case GROUP_CREATE_SUCESS:
				finish();
				break;
			case GROUP_CREATE_FAILED:
				finish();
				break;
			default:
				break;
			}
		};
	};

	public void processParam(String filepath) {
		JSONObject paramObject = new JSONObject();
		try {
			paramObject.put("uptype", "circle");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		commonMethod(HttpUrls.UPLOAD_IMG, paramObject, listResultListener,
				listErrorListener, filepath);
	}

	public void createCircle() {
		JSONObject paramObject = new JSONObject();
		try {
			String circleName = name.getEditableText().toString();
			String circleContent = content.getEditableText().toString();

			paramObject.put("desc", circleContent);
			paramObject.put("image", circleImage);
			paramObject.put("title", circleName);
			paramObject.put("circleid", "" + circleid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(TextUtils.isEmpty(circleid)){
			commonMethod(HttpUrls.GROUP_CIRCLE_EDIT, paramObject,
					crtateResultListener, crtateErrorListener, "");
		}else{
			commonMethod(HttpUrls.GROUP_CREATE_CIRCLE, paramObject,
					crtateResultListener, crtateErrorListener, "");
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
								if(TextUtils.isEmpty(circleid)){
									msg = "" + "圈子创建成功！";
								}else{
									msg = "" + "圈子修改成功！";
								}
								Toast.makeText(GroupCreateActivity.this, msg,
										Toast.LENGTH_SHORT).show();
								handler.sendEmptyMessage(GROUP_CREATE_SUCESS);
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
			Toast.makeText(GroupCreateActivity.this, msg, Toast.LENGTH_SHORT)
					.show();
			handler.sendEmptyMessage(GROUP_CREATE_FAILED);
		}
	};

	ErrorListener crtateErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	public void commonMethod(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener,
			String files) {
		JSONObject jsonObject = new JSONObject();
		JSONObject commonsub = Util.getCommonParam(GroupCreateActivity.this);
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
								Toast.makeText(GroupCreateActivity.this, msg,
										Toast.LENGTH_SHORT).show();
								JSONArray imagesJson = data
										.getJSONArray("imageitems");
								JSONObject image = imagesJson.getJSONObject(0);
								circleImage = image.getString("name");
								handler.sendEmptyMessage(UPLOAD_SUCESS);
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
			Toast.makeText(GroupCreateActivity.this, msg, Toast.LENGTH_SHORT)
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

	private int imgType = -1;

	private static final int CAMERA_WITH_DATA = 0x11;// 请求相机功能
	private static final int CAMERA_PICK_PHOTO = 0x13;// 请求相机功能
	private static final int PHOTO_PICKED_WITH_DATA = 0x12; // 请求相册
	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
	private Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);// 调用相册用Uri
	private Uri cameraImageUri = null; // 调用相机用的Uri
	private static String imageString = "file:///sdcard/";

	private void showCameraDialog() {

		// 创建一个对话框
		Dialog dialog = new AlertDialog.Builder(GroupCreateActivity.this)
				.setTitle("上传照片")
				// 创建标题
				.setMessage("我要上传照片")
				// 设置对话框中的内容
				.setPositiveButton("拍照", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						cameraImageUri = getUri();
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);// action is
																	// capture
						intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
						startActivityForResult(intent, CAMERA_WITH_DATA);// or
																			// TAKE_SMALL_PICTURE
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

	private Uri getUri() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		System.out.println(imageString + dateFormat.format(date) + ".jpg");
		return Uri.parse(imageString + dateFormat.format(date) + ".jpg");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CAMERA_WITH_DATA:
			imgType = 1;
			Bitmap bitmap = BitmapFactory.decodeFile(cameraImageUri.getPath());
			camera.setImageBitmap(bitmap);
			// cropImageUri(cameraImageUri, 290, 290, CAMERA_PICK_PHOTO);
			break;
		case PHOTO_PICKED_WITH_DATA:
			imgType = 0;
			Bitmap bitmap2 = null;

			photoPath = getDataColumn(getApplicationContext(), data.getData(),
					null, null);
			Log.e("lixufeng", "mFileName: " + photoPath);
			bitmap2 = getBitmap(photoPath);
			camera.setImageBitmap(bitmap2);

			/*
			 * if (imageUri != null) { Intent intent = new
			 * Intent(GroupCreateActivity.this,
			 * CenterUploadUserfaceActivity.class);
			 * intent.putExtra("screenshotPath", imageUri.getPath());
			 * startActivity(intent); }
			 */
			break;
		case CAMERA_PICK_PHOTO:
			/*
			 * if (cameraImageUri != null) { Intent intent = new
			 * Intent(GroupCreateActivity.this,
			 * CenterUploadUserfaceActivity.class);
			 * intent.putExtra("screenshotPath", cameraImageUri.getPath());
			 * startActivity(intent); }
			 */
			break;
		case SELECT_PIC_KITKAT:
			imgType = 0;
			Bitmap bitmap3 = null;
			photoPath = getPath(GroupCreateActivity.this, data.getData());
			Log.e("lixufeng", "photoPath: " + photoPath);
			bitmap3 = getBitmap(photoPath);
			camera.setImageBitmap(bitmap3);
			break;
		}
	}

/*	// 调用相机拍照截图
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 2);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		System.out.println("相机");
		startActivityForResult(intent, requestCode);
	}*/

/*	// 调用相册截图
	public void getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		// intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 290);
		intent.putExtra("outputY", 290);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
	}*/

	public static final int SELECT_PIC_KITKAT = 1;
	public static final int SELECT_PIC = 1;

	private void startPickPhotoActivity() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);// ACTION_OPEN_DOCUMENT
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			startActivityForResult(intent, SELECT_PIC_KITKAT);
		} else {
			startActivityForResult(intent, SELECT_PIC);
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
