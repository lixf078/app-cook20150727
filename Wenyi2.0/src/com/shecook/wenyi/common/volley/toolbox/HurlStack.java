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

package com.shecook.wenyi.common.volley.toolbox;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.activeandroid.util.Log;
import com.shecook.wenyi.common.volley.AuthFailureError;
import com.shecook.wenyi.common.volley.Request;
import com.shecook.wenyi.common.volley.VolleyLog;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.util.WenyiLog;

/**
 * 基于 {@link HttpURLConnection} 的 {@link HttpStack}。
 * <P>
 * 
 * 
 * An {@link HttpStack} based on {@link HttpURLConnection}.
 */
public class HurlStack implements HttpStack {

	private static final String HEADER_CONTENT_TYPE = "Content-Type";

	/**
	 * 在使用前转化URL的接口。
	 * <P>
	 * 
	 * An interface for transforming URLs before use.
	 */
	public interface UrlRewriter {
		/**
		 * Returns a URL to use instead of the provided one, or null to indicate
		 * this URL should not be used at all.
		 */
		public String rewriteUrl(String originalUrl);
	}

	private final UrlRewriter mUrlRewriter;
	private final SSLSocketFactory mSslSocketFactory;

	public HurlStack() {
		this(null);
	}

	/**
	 * @param urlRewriter
	 *            Rewriter to use for request URLs
	 */
	public HurlStack(UrlRewriter urlRewriter) {
		this(urlRewriter, null);
	}

	/**
	 * @param urlRewriter
	 *            Rewriter to use for request URLs
	 * @param sslSocketFactory
	 *            SSL factory to use for HTTPS connections
	 */
	public HurlStack(UrlRewriter urlRewriter, SSLSocketFactory sslSocketFactory) {
		mUrlRewriter = urlRewriter;
		mSslSocketFactory = sslSocketFactory;
	}

	@Override
	public HttpResponse performRequest(Request<?> request,
			Map<String, String> additionalHeaders) throws IOException,
			AuthFailureError {
		if (request.getMethod() == Method.POST) {
			return doPost(request, new DefaultHttpClient());
		}
		String url = request.getUrl();
		HashMap<String, String> map = new HashMap<String, String>();
		map.putAll(request.getHeaders());
		map.putAll(additionalHeaders);
		if (mUrlRewriter != null) {
			String rewritten = mUrlRewriter.rewriteUrl(url);
			if (rewritten == null) {
				throw new IOException("URL blocked by rewriter: " + url);
			}
			url = rewritten;
		}
		URL parsedUrl = new URL(url);
		VolleyLog.e("lixufeng performRequest parsedUrl %s request %s ",
				parsedUrl, request);

		HttpURLConnection connection = openConnection(parsedUrl, request);
		for (String headerName : map.keySet()) {
			VolleyLog.e(
					"lixufeng performRequest headerName %s ,headerValue %s ",
					headerName, map.get(headerName));
			connection.addRequestProperty(headerName, map.get(headerName));
		}
		
		
		connection.addRequestProperty(HEADER_CONTENT_TYPE,
				"application/json; charset=utf-8");
		setConnectionParametersForRequest(connection, request);
		// Initialize HttpResponse with data from the HttpURLConnection.
		ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
		int responseCode = connection.getResponseCode();
		if (responseCode == -1) {

			// -1 is returned by getResponseCode() if the response code could
			// not be retrieved.
			// Signal to the caller that something was wrong with the
			// connection.
			throw new IOException(
					"Could not retrieve response code from HttpUrlConnection.");
		}
		StatusLine responseStatus = new BasicStatusLine(protocolVersion,
				connection.getResponseCode(), connection.getResponseMessage());
		BasicHttpResponse response = new BasicHttpResponse(responseStatus);
		response.setEntity(entityFromConnection(connection));
		for (Entry<String, List<String>> header : connection.getHeaderFields()
				.entrySet()) {
			if (header.getKey() != null) {
				Header h = new BasicHeader(header.getKey(), header.getValue()
						.get(0));
				response.addHeader(h);
			}
		}
		return response;
	}

	/**
	 * Initializes an {@link HttpEntity} from the given
	 * {@link HttpURLConnection}.
	 * 
	 * @param connection
	 * @return an HttpEntity populated with data from <code>connection</code>.
	 */
	private static HttpEntity entityFromConnection(HttpURLConnection connection) {
		BasicHttpEntity entity = new BasicHttpEntity();
		InputStream inputStream;
		try {
			inputStream = connection.getInputStream();
		} catch (IOException ioe) {
			inputStream = connection.getErrorStream();
		}
		entity.setContent(inputStream);
		entity.setContentLength(connection.getContentLength());
		entity.setContentEncoding(connection.getContentEncoding());
		entity.setContentType(connection.getContentType());
		return entity;
	}

	/**
	 * Create an {@link HttpURLConnection} for the specified {@code url}.
	 */
	protected HttpURLConnection createConnection(URL url) throws IOException {
		return (HttpURLConnection) url.openConnection();
	}

	/**
	 * Opens an {@link HttpURLConnection} with parameters.
	 * 
	 * @param url
	 * @return an open connection
	 * @throws IOException
	 */
	private HttpURLConnection openConnection(URL url, Request<?> request)
			throws IOException {
		HttpURLConnection connection = createConnection(url);

		int timeoutMs = request.getTimeoutMs();
		connection.setConnectTimeout(timeoutMs);
		connection.setReadTimeout(timeoutMs);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		// use caller-provided custom SslSocketFactory, if any, for HTTPS
		if ("https".equals(url.getProtocol()) && mSslSocketFactory != null) {
			((HttpsURLConnection) connection)
					.setSSLSocketFactory(mSslSocketFactory);
		}

		return connection;
	}

	/** parameter 就是POST发送的数据 */
	@SuppressWarnings("deprecation")
	/* package */static void setConnectionParametersForRequest(
			HttpURLConnection connection, Request<?> request)
			throws IOException, AuthFailureError {
		switch (request.getMethod()) {
		case Method.DEPRECATED_GET_OR_POST:
			// This is the deprecated way that needs to be handled for backwards
			// compatibility.
			// If the request's post body is null, then the assumption is that
			// the request is
			// GET. Otherwise, it is assumed that the request is a POST.
			byte[] postBody = request.getPostBody();
			if (postBody != null) {
				// Prepare output. There is no need to set Content-Length
				// explicitly,
				// since this is handled by HttpURLConnection using the size of
				// the prepared
				// output stream.
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.addRequestProperty(HEADER_CONTENT_TYPE,
						request.getPostBodyContentType());
				DataOutputStream out = new DataOutputStream(
						connection.getOutputStream());
				out.write(postBody);
				out.close();
			}
			break;
		case Method.GET:
			// Not necessary to set the request method because connection
			// defaults to GET but
			// being explicit here.
			connection.setRequestMethod("GET");
			break;
		case Method.DELETE:
			connection.setRequestMethod("DELETE");
			break;
		case Method.POST:
			connection.setRequestMethod("POST");
			addBodyIfExists(connection, request);
			break;
		case Method.PUT:
			connection.setRequestMethod("PUT");
			addBodyIfExists(connection, request);
			break;
		case Method.HEAD:
			connection.setRequestMethod("HEAD");
			break;
		case Method.OPTIONS:
			connection.setRequestMethod("OPTIONS");
			break;
		case Method.TRACE:
			connection.setRequestMethod("TRACE");
			break;
		case Method.PATCH:
			addBodyIfExists(connection, request);
			connection.setRequestMethod("PATCH");
			break;
		default:
			throw new IllegalStateException("Unknown method type.");
		}
	}

	private static void addBodyIfExists(HttpURLConnection connection,
			Request<?> request) throws IOException, AuthFailureError {
		byte[] body = request.getBody();
		String string = new String(body);
		VolleyLog.d("lixufeng addBodyIfExists body %s", string);
		if (body != null) {
			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Charset", "utf-8");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			out.write(body);
			out.flush();
			out.close();
		}
	}

	public static HttpResponse doPost(Request<?> request, HttpClient httpClient) {
		HttpPost httpRequest = new HttpPost(request.getUrl());
		httpRequest.setHeader("sign", "ABESDSGTESFSFSFSAVBGA");
		HttpEntity httpentity;
		try {
			List<NameValuePair> params = adapterParams(new String(request.getBody()));
			// upload file start
			if(params.size() >= 3){
				// upload file
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);// 设置浏览器兼容模式
				builder.addTextBody(params.get(0).getName(), params.get(0).getValue());//设置请求参数
				builder.addTextBody(params.get(1).getName(), params.get(1).getValue());//设置请求参数
				int count = 0;
				String[] files = params.get(2).getValue().split(";");
				Log.e("lixufeng", "file value " + params.get(2).getValue());
				for (String tempfile : files) {
					builder.addBinaryBody("file" + count, new File(tempfile));
					count++;
				}
				httpentity = builder.build();// 生成 HTTP POST 实体 
				// upload file end
			}else{
				httpentity = new UrlEncodedFormEntity(params, "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		try {
			httpRequest.setEntity(httpentity);
			HttpResponse response = httpClient.execute(httpRequest);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static List<NameValuePair> adapterParams(String param) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			
			JSONObject json = new JSONObject(param);
			if (!json.isNull("param")) {
				params.add(new BasicNameValuePair("param", json.getString("param")));
			}
			if (!json.isNull("common")) {
				params.add(new BasicNameValuePair("common", json.getString("common")));
			}
			if (!json.isNull("files")) {
				params.add(new BasicNameValuePair("files", json.getString("files")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return params;
	}
	

}
