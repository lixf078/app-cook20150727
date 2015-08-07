package com.shecook.wenyi.common.pulltorefresh.internal;

import android.util.Log;
import com.shecook.wenyi.R;

public class Utils {

	static final String LOG_TAG = "PullToRefresh";

	public static void warnDeprecation(String depreacted, String replacement) {
		Log.w(LOG_TAG, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
	}

}
