package org.chenmin.daka;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

public class CookiesHttpClient {

	private CloseableHttpClient httpclient = null;
	private CookieStore cookieStore = new BasicCookieStore();

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	public HttpClient getHttpClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		if (httpclient == null) {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

						public boolean isTrusted(X509Certificate[] arg0, String arg1)
								throws java.security.cert.CertificateException {
							return true;
						}
					}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultCookieStore(cookieStore).build();
		}
		return httpclient;
	}

	public void close() throws IOException {
		httpclient.close();
	}

}
