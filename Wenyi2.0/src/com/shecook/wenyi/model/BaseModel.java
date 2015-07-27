package com.shecook.wenyi.model;

import org.json.JSONException;
import org.json.JSONObject;
import com.shecook.wenyi.R;
import com.shecook.wenyi.util.net.NetResultParent;

public class BaseModel extends NetResultParent{
	public int statusCode;
	public String errMsg;
	
	public int coreStatus;
	public String msg;
	public String token; // 后台返回，用于每次请求使用
	
	//　解析公共数据
	@Override
	public void parseJson(JSONObject jo) throws JSONException {
		int rd = jo.isNull("statusCode") ? -1 : jo.getInt("statusCode");
		this.statusCode = rd;

		String errMsg = jo.isNull("errMsg") ? "" : jo.getString("errMsg");
		if (null == errMsg) {
			throw new JSONException("从服务器获得结果中没有 errMsg 字段");
		}
		
		JSONObject jObject = jo.isNull("data") ? null : jo.getJSONObject("data");
		if(jObject != null){
			
			coreStatus = jObject.isNull("core_status") ? -1 : jObject.getInt("core_status");
			
			msg = jObject.isNull("msg") ? "" : jObject.getString("msg");
			
			token = jObject.isNull("token") ? "" : jObject.getString("token");
		}
		
		parseSelf(jObject);
	}

	public String getParamtersFromJSON(JSONObject jSONObject, String keyName)
			throws JSONException {
		return jSONObject.isNull(keyName) ? "" : jSONObject.getString(keyName);
	}

	/** 子类重写这个方法，来解析自己的具体内容 */
	protected void parseSelf(JSONObject jObject) throws JSONException{
		
	};
}
