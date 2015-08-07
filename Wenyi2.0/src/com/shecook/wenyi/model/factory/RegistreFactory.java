package com.shecook.wenyi.model.factory;

import org.json.JSONException;
import org.json.JSONObject;

import com.shecook.wenyi.R;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.util.net.NetResult;
import com.shecook.wenyi.util.net.NetResultFactory;

public class RegistreFactory implements NetResultFactory{

	@Override
	public NetResult produce(JSONObject object) throws JSONException {
		WenyiUser user = new WenyiUser();
		user.parseJson(object);
		return user;
	}

}
