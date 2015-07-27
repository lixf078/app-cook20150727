package com.shecook.wenyi.util;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.AuthFailureError;
import com.shecook.wenyi.common.volley.NetworkError;
import com.shecook.wenyi.common.volley.NoConnectionError;
import com.shecook.wenyi.common.volley.ParseError;
import com.shecook.wenyi.common.volley.ServerError;
import com.shecook.wenyi.common.volley.TimeoutError;
import com.shecook.wenyi.common.volley.VolleyError;

/** 转换VolleyError 成 本地 AppExcetption*/
public class VolleyErrorHelper {

	public static AppException converVolleyError(VolleyError volleyError) {
		if(volleyError instanceof AuthFailureError){//请求身份验证失败
			return AppException.auth(volleyError);
		}else if(volleyError instanceof NoConnectionError){//无网络连接
			return AppException.noNet(volleyError);
		}else if(volleyError instanceof TimeoutError){//请求超时
			return AppException.http(volleyError);
		}else if(volleyError instanceof NetworkError){ // 执行网络请求时发生错误
			return AppException.http(volleyError);
		}else if(volleyError instanceof ParseError){//解析结果发生错误
			return AppException.json(volleyError);
		}else if(volleyError instanceof ServerError){ // 服务器应答，但是 是错误应答
			if(null != volleyError.networkResponse){
				return AppException.http(volleyError.networkResponse.statusCode);
			}else{
				return AppException.server(volleyError);
			}
		}else{
			return AppException.run(volleyError);
		}
	}

}
