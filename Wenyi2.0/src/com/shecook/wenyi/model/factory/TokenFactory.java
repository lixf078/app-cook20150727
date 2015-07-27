package com.shecook.wenyi.model.factory;

import org.json.JSONException;
import org.json.JSONObject;

import com.shecook.wenyi.R;
import com.shecook.wenyi.model.AppToken;
import com.shecook.wenyi.util.net.NetResult;
import com.shecook.wenyi.util.net.NetResultFactory;

public class TokenFactory implements NetResultFactory{

	@Override
	public NetResult produce(JSONObject object) throws JSONException {
		AppToken token = new AppToken();
		token.parseJson(object);
		return token;
	}

}
