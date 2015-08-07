/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shecook.wenyi.util.volleybox;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.AuthFailureError;
import com.shecook.wenyi.common.volley.NetworkResponse;
import com.shecook.wenyi.common.volley.ParseError;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.toolbox.HttpHeaderParser;
import com.shecook.wenyi.common.volley.toolbox.JsonRequest;
import com.shecook.wenyi.model.BaseModel;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.net.NetResult;
import com.shecook.wenyi.util.net.NetResultFactory;

/**
 * A request for retrieving a {@link JSONObject} response body at a given URL,
 * allowing for an optional {@link JSONObject} to be passed in as part of the
 * request body.
 */
public class WenyiJSONObjectRequest extends JsonRequest<NetResult> {

	// private final String TAG = getClass().getName();

	private NetResultFactory mResultFactory;
	private Object mOrangeTag;// 会原封不动的 封装到结果中，可以在 成功回调接口中，判断结果是哪个 请求的结果

	/**
	 * Creates a new request.
	 * 
	 * @param method
	 *            请求方式
	 * @param url
	 *            请求的URL
	 * @param strRequest
	 *            post请求发送的数据
	 * @param resultFactory
	 *            解析JSON数据，生成结果对象的工厂
	 * @param listener
	 *            传递正确结果的接口
	 * @param errorListener
	 *            传递异常信息的接口
	 * */
	public WenyiJSONObjectRequest(int method, String url, String strRequest,
			NetResultFactory resultFactory, Listener<NetResult> listener,
			ErrorListener errorListener) {
		super(method, url, strRequest, listener, errorListener);
		Log.d("WenyiJSONObjectRequest", "WenyiJSONObjectRequest strRequest " + strRequest);
		this.mResultFactory = resultFactory;
	}

	/** 设置额外的需要传递给 结果的 数据 */
	public void setDeliverToResultTag(Object tag) {
		this.mOrangeTag = tag;
	}

	@Override
	protected Response<NetResult> parseNetworkResponse(NetworkResponse response) {
		try {
			
			if(null == mResultFactory){
				return null;
			}

			// Log.d("Volley", "解析结果开始：" + System.currentTimeMillis());
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			JSONObject jObject = new JSONObject(jsonString);
			WenyiLog.logd("","请求结果------》  "+jObject);
			
			NetResult resultBean = null;
			try {
				resultBean = mResultFactory.produce(jObject);
			} catch (Exception e) {
				resultBean = new BaseModel();
				e.printStackTrace();
			}

			Log.d("Volley", "解析结果结束：" + System.currentTimeMillis());
			Response<NetResult> resultResponse = Response.success(resultBean,
					HttpHeaderParser.parseCacheHeaders(response));
			resultResponse.setResultDatLegitimacy(resultBean
					.checkResultLegitimacy());

			return resultResponse;
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			je.printStackTrace();
			return Response.error(new ParseError(je));
		}
	}

	/** 必须去掉 接口版本号字段，否则永远读取不到缓存，因为当接口请求成功后会更新本地保存的接口版本号，这样当再次请求时，CacheKey就变了 */
/*	@Override
	public String getCacheKey() {

		String postDataNoVersionParam = VolleyUtils
				.deletVersionParam(getRequestParamsStr());

		// AppDebugLog.logSystemOut("缓存Key："+getUrl() + postDataNoVersionParam);

		return getUrl() + postDataNoVersionParam;
	}*/
	
	
	/** 设置请求类型 */
	private NetResult.ResultType createAppResultType(NetworkResponse response) {
		boolean isFromCache = response.isFromCache();
		if (isFromCache) {
			return NetResult.ResultType.CATCH;
		} else {
			return NetResult.ResultType.NET;
		}
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Log.d("lixufeng", "wenyijsonObjectRequest");
		Map<String, String> param = new HashMap<String, String>();
    	param.put("mid", "957aea62-3a50-49e3-9640-3824d38b2f14");
    	param.put("mtype", "android");
		return param;
	}

	
}
