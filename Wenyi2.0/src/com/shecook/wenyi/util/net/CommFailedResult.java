package com.shecook.wenyi.util.net;

import android.content.Context;

import com.shecook.wenyi.R;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.WenyiLog;

/** 网络请求失败的通用结果 */
public class CommFailedResult implements NetFailedResult {

	private AppException mException;

	@Override
	public void setException(AppException e) {
		this.mException = e;
	}

	@Override
	public void toastFailStr(Context context) {
		mException.makeToast(context);

	}

	@Override
	public void logFailInfo(String tag) {
		WenyiLog.loge("CommFailedResult - " + tag, "" + mException.toString());
	}

}
