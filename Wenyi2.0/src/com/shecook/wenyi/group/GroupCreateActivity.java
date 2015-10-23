package com.shecook.wenyi.group;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
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

	private ImageView camera;
	private EditText name, content;
	private ImageView right_img;
	private String circleImage = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_create);

		// processParam("");
		initView();
	}

	public void initView() {
		camera = (ImageView) findViewById(R.id.group_create_camera);
		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showCameraDialog();
			}
		});
		name = (EditText) findViewById(R.id.group_create_name);
		content = (EditText) findViewById(R.id.group_create_content);

		right_img = (ImageView) findViewById(R.id.right_img);
		right_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String filePath = null;
				if (imgType == 0) {
					filePath = imageUri.getPath();
				} else if (imgType == 1) {
					filePath = cameraImageUri.getPath();
				}
				handler.sendEmptyMessage(HttpStatus.STATUS_SHOWPROGRESS);
				processParam(filePath);
			}
		});
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_SHOWPROGRESS:
				Util.showDialog(GroupCreateActivity.this, -1);
				break;
			case HttpStatus.STATUS_OK:
				createCircle();
				break;
			case HttpStatus.STATUS_OK_2:
				
				break;
			case HttpStatus.STATUS_ERROR:

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
		String dir = Environment.getExternalStorageDirectory() + "";
		String files = dir + "/text1.png;" + dir + "/text2.png;" + dir
				+ "/20151022202319.jpg;" + dir + "/20151022204753.jpg";
		String files2 = dir + "/text1.png;" + dir + "/text2.png;" + dir
				+ "/text1.png;" + dir + "/text1.png";
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
		commonMethod(HttpUrls.UPLOAD_IMG, paramObject, crtateResultListener,
				crtateErrorListener, "");
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
								msg = "" + "文件上传成功！";
								Toast.makeText(GroupCreateActivity.this, msg,
										Toast.LENGTH_SHORT).show();
								JSONArray imagesJson = data.getJSONArray("imageitems");
								JSONObject image = imagesJson.getJSONObject(0);
								circleImage = image.getString("name");
								handler.sendEmptyMessage(HttpStatus.STATUS_OK);
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
			commonsub.put("mid", "5A1469CD-4819-4863-A934-8871CA1A0281");
			commonsub.put("token", "591f3c51eca2483b932ed1a64b896a63");
			if(!TextUtils.isEmpty(files)){
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
								JSONArray imagesJson = data.getJSONArray("imageitems");
								JSONObject image = imagesJson.getJSONObject(0);
								circleImage = image.getString("name");
								handler.sendEmptyMessage(HttpStatus.STATUS_OK);
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
						getPhotoPickIntent();
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
			Bitmap bitmap2 = BitmapFactory.decodeFile(imageUri.getPath());
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
		}
	}

	// 调用相机拍照截图
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
	}

	// 调用相册截图
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
	}

}
