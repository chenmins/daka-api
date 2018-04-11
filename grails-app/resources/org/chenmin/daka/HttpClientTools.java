package org.chenmin.daka;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientTools {
	
	public static boolean useProxy = false;
	
	public static String get(HttpClient httpclient, String url,
			Map<String, String> settings) throws ClientProtocolException,
			IOException {
		HttpGet httpGet = new HttpGet(url);
		
		for (Map.Entry<String, String> entry : settings.entrySet()) {
			httpGet.setHeader(entry.getKey(), entry.getValue());
		}
		CloseableHttpResponse response1 = (CloseableHttpResponse) httpclient
				.execute(httpGet);
		try {
			HttpEntity entity1 = response1.getEntity();
			String body = EntityUtils.toString(entity1);
			EntityUtils.consume(entity1);
			return body;
		} finally {
			response1.close();
		}
	}

	public static String post(HttpClient httpclient, String url, String param,
			Map<String, String> settings) throws ClientProtocolException,
			IOException {
		HttpPost httpPost = new HttpPost(url);
		
		
		for (Map.Entry<String, String> entry : settings.entrySet()) {
			httpPost.setHeader(entry.getKey(), entry.getValue());
		}
		httpPost.setEntity(EntityBuilder.create().setText(param).build());
		CloseableHttpResponse response1 = (CloseableHttpResponse) httpclient
				.execute(httpPost);
		try {
			HttpEntity entity1 = response1.getEntity();
			String body = EntityUtils.toString(entity1);
			EntityUtils.consume(entity1);
			return body;
		} finally {
			response1.close();
		}
	}
	
	
	public static String postForm(HttpClient httpclient, String url,
			Map<String, String> param, Map<String, String> settings)
			throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(url);
		
		
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		for (Map.Entry<String, String> entry : param.entrySet()) {
			nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response1 = (CloseableHttpResponse) httpclient.execute(httpPost);
		try {
			HttpEntity entity1 = response1.getEntity();
			//String body = EntityUtils.toString(entity1);
			String body = EntityUtils.toString(entity1, "UTF-8").trim();
			
			EntityUtils.consume(entity1);
			return body;
		} finally {
			response1.close();
		}
	}

	

}
