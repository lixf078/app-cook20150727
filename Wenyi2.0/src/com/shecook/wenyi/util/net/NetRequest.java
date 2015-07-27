package com.shecook.wenyi.util.net;

import com.shecook.wenyi.R;

/** 执行网络请求，并传递结果 */
public interface NetRequest {

	/** 执行网络请求 */
	public void doRequest();

	/** 此值会传递给结果 */
	public void setDeliverToResultTag(Object tag);

	/** 获取 {@link #setDeliverToResultTag(Object tag)} 设置的值 */
	public Object getDeliverToResultTag();

	/** 设置请求完的回调接口 */
	public void setCallBack(NetRequestCallback callback);

	/** 网络请求结果的回调接口 */
	public static interface NetRequestCallback {

		/** 网络请求成功回调 */
		public void onSucceed(NetResult result);

		/** 网络请求失败回调 */
		public void onFailed(NetFailedResult failResult);
	}

}
