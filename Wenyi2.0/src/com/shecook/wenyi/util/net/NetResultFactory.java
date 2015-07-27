package com.shecook.wenyi.util.net;

import com.shecook.wenyi.R;
import org.json.JSONException;
import org.json.JSONObject;

/** JSON 解析接口，用来生成具体的 {@link NetResult} */
public interface NetResultFactory {

	public NetResult produce(JSONObject object) throws JSONException;

}
