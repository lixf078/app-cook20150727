package com.shecook.wenyi.wxapi;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.platformtools.Log;
import com.umeng.socialize.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {

	@Override
	public void onReq(BaseReq req) {
		Log.d("WXEntryActivity", "onReq " + req.transaction);
		super.onReq(req);
	}

	@Override
	public void onResp(BaseResp res) {
		super.onResp(res);
		Log.d("WXEntryActivity", "onResp " + res.errStr);
	}

}
