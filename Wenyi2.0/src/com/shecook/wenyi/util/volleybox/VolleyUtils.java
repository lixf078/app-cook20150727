package com.shecook.wenyi.util.volleybox;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.RequestQueue;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.common.volley.toolbox.JsonRequest;
import com.shecook.wenyi.common.volley.toolbox.Volley;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.net.NetResult;
import com.shecook.wenyi.util.net.NetResultFactory;
import com.ta.utdid2.android.utils.StringUtils;

public class VolleyUtils {

	/** 各个接口中的版本号字段 */
	public static final String[] VERSION_PARAM = new String[] { "vnh", "rtvn",
			"vnc", "vnb", "vnbuy" };

	private Context mApplicationContext;
	private RequestQueue mRequestQueue;

	private static VolleyUtils mUtils;

	private VolleyUtils() {
	}

	public static VolleyUtils getInstance() {
		if (null == mUtils) {
			mUtils = new VolleyUtils();
		}
		return mUtils;
	}

	public RequestQueue getRequestQueue(){
		return mRequestQueue;
	}
	
	/** 将 Map 转换成字符串 */
	@SuppressWarnings("deprecation")
	public static String converMapParamToStr(Map<String, String> params) {

		StringBuilder sb = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(entry.getKey()).append('=')
						.append(URLEncoder.encode(entry.getValue()))
						.append('&');
			}
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	/** 将PostData 转换成Map */
	public static Map<String, String> converPostDataToMap(String postData) {

		if (StringUtils.isEmpty(postData)) {
			return null;
		}

		Map<String, String> result = new HashMap<String, String>();
		String[] params = postData.split("\\&");
		for (String str : params) {
			String[] oneParam = str.split("\\=");
			result.put(oneParam[0], oneParam[1]);
		}
		return result;
	}

	/** 去掉PostData中的版本控制号 */
	public static String deletVersionParam(String orangePostData) {
		Map<String, String> postMap = converPostDataToMap(orangePostData);

		if (null == postMap) {
			return null;
		}

		for (String key : VERSION_PARAM) {
			String removeStr = postMap.remove(key);
			if (null != removeStr) {
				break;
			}
		}

		return converMapParamToStr(postMap);
	}

	/**
	 * 网络请求,如果有缓存就读取缓存(并且缓存不能过期（根据http报头信息判断是否过期的）)，否则读取网络
	 * 
	 * @param url
	 *            请求url
	 * @param postData
	 *            请求参数
	 * @param factory
	 *            生成具体 结果对象JSONObjectResultBean 的代理
	 * @param succeedListener
	 *            数据接收成功接口
	 * @param errorListener
	 *            数据接收失败接口
	 * @param cancelTag
	 *            取消任务用的
	 * @param deliverToResult
	 *            原封不动传递给 结果
	 * 
	 * */
	public static void requestNetData(String url, String postData,
			NetResultFactory factory,
			Response.Listener<NetResult> succeedListener,
			Response.ErrorListener errorListener, Object cancelTag,
			Object deliverToResult) {
		// 执行请求
		WenyiJSONObjectRequest jpRequest = new WenyiJSONObjectRequest(Method.POST, url,
				postData, factory, succeedListener, errorListener);
		jpRequest.setTag(cancelTag);
		jpRequest.setDeliverToResultTag(deliverToResult);
		try {
			VolleyUtils.getInstance().addReequest(jpRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 请求数据，会跳过缓存，但是会把结果缓存起来
	 * 
	 * @param url
	 *            请求url
	 * @param postData
	 *            请求参数
	 * @param factory
	 *            生成具体 结果对象JSONObjectResultBean 的代理
	 * @param succeedListener
	 *            数据接收成功接口
	 * @param errorListener
	 *            数据接收失败接口
	 * @param cancelTag
	 *            取消任务用的
	 * @param deliverToResult
	 *            原封不动传递给 结果
	 * */
	public static void requestDataSkipCache(String url, String postData,
			NetResultFactory factory,
			Response.Listener<NetResult> succeedListener,
			Response.ErrorListener errorListener, Object cancelTag,
			Object deliverToResult) {
		// 执行请求
		WenyiJSONObjectRequest jpRequest = new WenyiJSONObjectRequest(Method.POST, url,
				postData, factory, succeedListener, errorListener);
		jpRequest.setTag(cancelTag);
		jpRequest.setDeliverToResultTag(deliverToResult);
		jpRequest.skipCache();
		try {
			VolleyUtils.getInstance().addReequest(jpRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 网络请求,缓存优先(即使缓存已经过去仍然会去读缓存)
	 * 
	 * @param url
	 *            请求url
	 * @param postData
	 *            请求参数
	 * @param factory
	 *            生成具体 结果对象JSONObjectResultBean 的代理
	 * @param succeedListener
	 *            数据接收成功接口
	 * @param errorListener
	 *            数据接收失败接口
	 * @param cancelTag
	 *            取消任务用的
	 * @param deliverToResult
	 *            原封不动传递给 结果
	 * */
	public static void requestNetDataFirstCache(String url, String postData,
			NetResultFactory factory,
			Response.Listener<NetResult> succeedListener,
			Response.ErrorListener errorListener, Object cancelTag,
			Object deliverToResult) {
		// 执行请求
		WenyiJSONObjectRequest jpRequest = new WenyiJSONObjectRequest(Method.POST, url,
				postData, factory, succeedListener, errorListener);
		jpRequest.setTag(cancelTag);
		jpRequest.setDeliverToResultTag(deliverToResult);
		jpRequest.setReadCacheWithoutTimeLimit();
		try {
			VolleyUtils.getInstance().addReequest(jpRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 网络请求,不启用缓存
	 * 
	 * @param url
	 *            请求url
	 * @param postData
	 *            请求参数
	 * @param factory
	 *            生成具体 结果对象JSONObjectResultBean 的代理
	 * @param succeedListener
	 *            数据接收成功接口
	 * @param errorListener
	 *            数据接收失败接口
	 * @param cancelTag
	 *            取消任务用的
	 * @param deliverToResult
	 *            原封不动传递给 结果
	 * */
	public static void requestNetDataWithoutCache(String url, String postData,
			NetResultFactory factory,
			Response.Listener<NetResult> succeedListener,
			Response.ErrorListener errorListener, Object cancelTag,
			Object deliverToResult) {
		// 执行请求
		WenyiJSONObjectRequest jpRequest = new WenyiJSONObjectRequest(Method.POST, url,
				postData, factory, succeedListener, errorListener);
		jpRequest.setShouldCache(false);
		jpRequest.setTag(cancelTag);
		jpRequest.setDeliverToResultTag(deliverToResult);
		try {
			VolleyUtils.getInstance().addReequest(jpRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检测Volley是否被初始化了
	 * 
	 * @return true 被初始化了；false没有初始化;
	 * */
	private boolean checkInit() {

		if (null != mApplicationContext && null != mRequestQueue) {
			return true;
		}

		if (null != mApplicationContext) {
			if (null == mRequestQueue) {
				mRequestQueue = Volley.newRequestQueue(mApplicationContext);
				return true;
			}
		}

		return false;
	}

	/** 取消请求 */
	public void cancelRequest(Object tag) {
		mRequestQueue.cancelAll(tag);
	}

	/**
	 * 添加请求
	 * 
	 * @throws AppException
	 */
	public void addReequest(WenyiJSONObjectRequest request) throws AppException {

		if (checkInit() == false) {
			throw AppException.run(new Exception("VolleyUtils必须先初始化"));
		}

		mRequestQueue.add(request);
	}
	
	/**
	 * 添加请求
	 * 
	 * @throws AppException
	 */
	public void addReequest(JsonRequest<WenyiJSONObjectRequest> request) throws AppException {

		if (checkInit() == false) {
			throw AppException.run(new Exception("VolleyUtils必须先初始化"));
		}

		mRequestQueue.add(request);
	}
	
	/**
	 * 添加请求
	 * 
	 * @throws AppException
	 */
	public void addReequest(JsonObjectRequest request) throws AppException {

		if (checkInit() == false) {
			throw AppException.run(new Exception("VolleyUtils必须先初始化"));
		}

		mRequestQueue.add(request);
	}
	

	/** 初始化Volley框架 */
	public void initVolley(Context context) {
		if (null == mRequestQueue) {
			mRequestQueue = Volley.newRequestQueue(context);
		}
		mApplicationContext = context.getApplicationContext();
	}

	/** 关闭Volley */
	public void closeVolley() {
		if (null != mRequestQueue) {
			mRequestQueue.stop();
			mRequestQueue = null;
		}
	}
}
