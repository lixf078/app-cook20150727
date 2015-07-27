package com.shecook.wenyi.util.net;

import com.shecook.wenyi.R;
import org.json.JSONException;
import org.json.JSONObject;

/** 网络请求成功请求结果 */
public interface NetResult {

	public enum ResultType {
		/** 来自网络 */
		NET,
		/** 来自缓存 */
		CATCH,
		/**
		 * 中间结果，首先取得缓存结果，再请求网络来获取结果
		 */
		INTERMEDIATE;
	}

	/** 设置数据来源 */
	public void setResultType(ResultType resultType);

	/** 获得数据来源类型 */
	public ResultType getResultType();

	/** 设置tag ,通过 {@link #getReqeustDeliveredTag()}获取此值*/
	public void setReqeustDeliveredTag(Object tag);

	/** 获取相应的 {@link NetRequest#setDeliverToResultTag(Object tag)} 中设置的 tag */
	public Object getReqeustDeliveredTag();

	/** 解析JSONObject */
	public void parseJson(JSONObject jo) throws JSONException;

	/**
	 * 检测结果数据的合法性
	 * 
	 * @return true 数据合法；false数据不合法
	 * */
	public boolean checkResultLegitimacy();

	/**
	 * 更新该结果类对应的 本地保存的数据,
	 * <p>
	 * 例如： 在{@link AbsFragmentListViewRefreshAndLoadmore}
	 * 中加载数据完成后，检测到数据不合法，会调用这个方法后再重新执行请求，那么就需要在这个方法中重置本地保存的对应的版本号为0
	 * 
	 * 
	 * @return 返回请求的参数(如果发起的请求时 没有post参数，那么这里就是返回url)
	 */
	// public String updateLocalData();

}
