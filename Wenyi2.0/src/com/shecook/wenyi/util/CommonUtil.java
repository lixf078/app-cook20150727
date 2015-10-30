package com.shecook.wenyi.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CommonUtil {

	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	public static String getRootFilePath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/";// filePath:/sdcard/
		} else {
			return Environment.getDataDirectory().getAbsolutePath() + "/data/"; // filePath:
																				// /data/data/
		}
	}

	public static boolean checkNetState(Context context) {
		boolean netstate = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						netstate = true;
						break;
					}
				}
			}
		}
		return netstate;
	}

	public static void showToask(Context context, String tip) {
		Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
	}

	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	public static String formatTime(String time) {
		if (time != null) {
			return time.split("T")[0];
		}
		return "";
	}

    public static String formatTime_Year(String time) {
        if(time != null && !"".equals(time)){
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

	public static void setListViewHeightBasedOnChildren(ListView listView, BaseAdapter adapter) {
//		ListAdapter listAdapter = adapter;
		if (adapter == null) {
			return;
		}

		int totalHeight = 0;
		Log.i("setListViewHeightBasedOnChildren", ""+adapter.getCount());
		for (int i = 0; i < adapter.getCount(); i++) {
			View listItem = (View) adapter.getView(i, null, null);
			if (listItem == null) {
				break;
			}
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount()));
		listView.setLayoutParams(params);

	}

	/**
	 * 获取当前时间
	 *
	 * @return 日期格式：yyyy-mm-dd hh:mm
	 */
	public static String getCurrentTime() {
		Date d = new Date(currentTimeMillis());
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
		return mSimpleDateFormat.format(d);
	}

	/** 获取系统时间戳 */
	public static long currentTimeMillis(){
		return System.currentTimeMillis();
	}

}
