package com.shecook.wenyi.util;

import android.util.Log;
import com.shecook.wenyi.R;

public class WenyiLog {

	public static final String TAG = "WenyiLog - ";
	
	public static final boolean LOGD = true;
	public static final boolean LOGE = true;
	public static final boolean LOGV = true;
	
	public static void logv(String tag, String log){
		if(LOGV){
			Log.v(TAG + tag, log);
		}
	}
	
	public static void logd(String tag, String log){
		if(LOGD){
			Log.d(TAG + tag, log);
		}
	}
	
	public static void loge(String tag, String log){
		if(LOGE){
			Log.e(TAG + tag, log);
		}
	}
}
