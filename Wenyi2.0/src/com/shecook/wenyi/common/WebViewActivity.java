package com.shecook.wenyi.common;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shecook.wenyi.BaseActivity;
import com.shecook.wenyi.R;

public class WebViewActivity extends BaseActivity {
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_webview_layout);
		init();
	}

	private void init() {
		webView = (WebView) findViewById(R.id.webView);
		//启用支持javascript
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		// WebView加载web资源
		String weburl = getIntent().getStringExtra("weburl");
		webView.loadUrl(weburl);
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
	}
}
