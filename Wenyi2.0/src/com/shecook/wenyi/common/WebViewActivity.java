package com.shecook.wenyi.common;

import com.shecook.wenyi.BaseActivity;
import com.shecook.wenyi.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class WebViewActivity extends BaseActivity {
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_webview_layout);
		init();
	}

	private void init() {
		
		ImageView return_img = (ImageView) findViewById(R.id.return_img);
		return_img.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ImageView right_img = (ImageView) findViewById(R.id.right_img);
		right_img.setVisibility(View.GONE);
		TextView middle_title = (TextView) findViewById(R.id.middle_title);
		right_img.setVisibility(View.GONE);
		
		webView = (WebView) findViewById(R.id.webView);
		//启用支持javascript
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		// WebView加载web资源
		String weburl = getIntent().getStringExtra("weburl");
		Log.e("WebViewActivity", "weburl " + weburl);
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
