package com.shecook.wenyi.util.net;

import android.content.Context;

import com.shecook.wenyi.R;
import com.shecook.wenyi.util.AppException;

/** 网络请求失败的结果 */
public interface NetFailedResult {

	/** 设置结果的异常 */
	public void setException(AppException e);

	/** 提示异常信息,这个是给用户看的 */
	public void toastFailStr(Context context);

	/** 打印异常信息，这个是调试程序用的 */
	public void logFailInfo(String tag);
}
