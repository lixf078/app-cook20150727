package com.shecook.wenyi.model;

import com.shecook.wenyi.R;
import org.json.JSONException;
import org.json.JSONObject;

public class AppToken extends BaseModel {

	public int coreStatus;
	public String msg;
	public String token;
	
	@Override
	protected void parseSelf(JSONObject jObject) throws JSONException {

		coreStatus = jObject.isNull("core_status") ? -1 : jObject.getInt("core_status");

		msg = jObject.isNull("msg") ? "" : jObject.getString("msg");
		
		token = jObject.isNull("token") ? "" : jObject.getString("token");
	}

	@Override
	public String toString() {
		return "AppToken [coreStatus=" + coreStatus + ", msg=" + msg
				+ ", token=" + token + ", statusCode=" + statusCode
				+ ", errMsg=" + errMsg + "]";
	}
	
}
