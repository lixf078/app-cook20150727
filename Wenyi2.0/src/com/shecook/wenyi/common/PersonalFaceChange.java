package com.shecook.wenyi.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class PersonalFaceChange {
	
	
	private static final int CAMERA_WITH_DATA = 0x11;// 请求相机功能
	private static final int CAMERA_PICK_PHOTO = 0x13;// 请求相机功能
	private static final int PHOTO_PICKED_WITH_DATA = 0x12; // 请求相册
	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
	private Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);// 调用相册用Uri
	private Uri cameraImageUri = null; // 调用相机用的Uri
	private static String imageString = "file:///sdcard/";

	private Activity mActivity;
	
	private void showCameraDialog(Activity activity) {
		mActivity = activity;
		// 创建一个对话框
		Dialog dialog = new AlertDialog.Builder(activity)
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
						mActivity.startActivityForResult(intent, CAMERA_WITH_DATA);// or
																			// TAKE_SMALL_PICTURE
					}
				})
				.setNegativeButton("相册", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getPhotoPickIntent(mActivity);
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
	
	
	// 调用相册截图
	public void getPhotoPickIntent(Activity activity) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 290);
		intent.putExtra("outputY", 290);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		activity.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
	}
}
