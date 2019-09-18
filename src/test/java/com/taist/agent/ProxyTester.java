package com.taist.agent;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
public class ProxyTester {
		private Map<String, Header> header = new HashMap<String, Header>();
		private List<NameValuePair> param = new ArrayList<NameValuePair>();
		private String location = null;
		
		private volatile static HttpClientContext context = HttpClientContext.create();
		private volatile static HttpClientBuilder clientBuilder = HttpClients.custom();
		private static HttpClient client = null;

		private static final RequestConfig requestConfig = RequestConfig.custom()
				.setProxy(new HttpHost("127.0.0.1", 8008))
				.build();
		
		public String getLocation() {
			return location;
		}
		
		public ProxyTester() {
			trustClient();
			client = clientBuilder.build();
		}
		
		public void run() {
			try {
				String s = get("https://www.baidu.com/");
				System.out.println("end.");
				System.out.println(s);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void trustClient() {
			try {
				SSLContext context = new SSLContextBuilder().loadTrustMaterial(
						new TrustStrategy() {
							
							public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
								// TODO Auto-generated method stub
								return true;
							}
						}
						).build();
				SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(context);
				clientBuilder.setSSLSocketFactory(factory);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private HttpURLConnection getConnection(URL url) throws Exception {

			//if(isHttps()) {
			trustAll();
			//}
	        // 打开和URL之间的连接
	        URLConnection conn = url.openConnection();
	        
	        //setHeader(conn);
	        
	        return (HttpURLConnection)conn;
		}
		
		private void setHeader(HttpRequestBase request) {
			for(Header h : header.values()) {
				request.setHeader(h);
			}
		}
		
		protected synchronized String get(String url) throws Exception {
		    HttpGet httpGet = new HttpGet(getRealUrl(url));
		    httpGet.setConfig(requestConfig);
		    setHeader(httpGet);
		    HttpResponse response = client.execute(httpGet, context);
		    setLocation(response);
		    /*List<Cookie> cookies = context.getCookieStore().getCookies();
			System.out.println(JSONObject.toJSONString(cookies));*/
			return EntityUtils.toString(response.getEntity());
		}
		
		protected String get1(String url) {
			System.out.println("**************** GET " + url);
			String result = "";
	        BufferedReader in = null;
	        try {
	        	URL realUrl = null;
	        	if(param.size() == 0) {
	        		realUrl = new URL(url);
	        	}
	        	else {
	        		realUrl = new URL(url + "?" + getParamString());
	        	}
	            // 打开和URL之间的连接
	            HttpURLConnection connection = getConnection(realUrl);
	            System.out.println("************** HEADER ************");
	            print(connection.getRequestProperties());
	            System.out.println();
	            connection.connect();
	            // 定义 BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	            //cacheHeaders(connection);
	        } catch (Exception e) {
	            System.out.println("发送GET请求出现异常！" + e);
	            e.printStackTrace();
	        }
	        // 使用finally块来关闭输入流
	        finally {
	            safeClose(in);
	        }
	        return result;
		}

		private void safeClose(Closeable c) {
			if(c !=  null) {
				try {
					c.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		protected synchronized String post(String url) throws Exception {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			setHeader(httpPost);
			UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(param, "UTF-8");
			httpPost.setEntity(postEntity);
			//System.out.println("request line:" + httpPost.getRequestLine());
			// 执行post请求
			HttpResponse response = client.execute(httpPost, context);
			setLocation(response);
			return EntityUtils.toString(response.getEntity());
		}
		
		private void setLocation(HttpResponse response) {
			Header locationHeader = response.getFirstHeader("Location");
			if(locationHeader == null) {
			    location = null;
			}
			else {
			    location = locationHeader.getValue();
			}
		}
		
		private String getRealUrl(String url) {
			if(param.size() == 0) {
	    		return url;
	    	}
	    	else {
	    		return url + "?" + getParamString();
	    	}
		}
		
		private void print(Map<String, List<String>> header) {
			if(header == null) {
				return;
			}
			
			for(String key : header.keySet()) {
				System.out.println(key + ": " + String.join(",", header.get(key)));
			}
		}

		private String decompress(InputStream in) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        try {
	            GZIPInputStream ungzip = new GZIPInputStream(in);
	            byte[] buffer = new byte[256];
	            int n;
	            while ((n = ungzip.read(buffer)) >= 0) {
	                out.write(buffer, 0, n);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        try {
				return new String(out.toByteArray(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	        return null;
		}
		
		protected void clearHeader() {
			header.clear();
		}
		
		protected void setHeader(String key, String value) {
			if(value == null || value.length() == 0) {
				header.remove(key);
			}
			else {
				header.put(key, new BasicHeader(key, value));
			}
		}
		
		protected void setParam(String key, String value) {
			NameValuePair u = new BasicNameValuePair(key, value);
			param.add(u);
		}
		
		protected void setParam(String key, long value) {
			setParam(key, String.valueOf(value));
		}
		
		protected void setParam(String key, double value) {
			setParam(key, String.valueOf(value));
		}
		
		protected void setParam(String key, boolean value) {
			setParam(key, String.valueOf(value));
		}
		
		protected void removeParam(String key) {
			List<NameValuePair> plist = new ArrayList<NameValuePair>(param);
			for(NameValuePair p : plist) {
				if(p.getName().equals(key)) {
					param.remove(plist.indexOf(p));
				}
			}
		}
		
		protected void clearParam() {
			param.clear();
		}
		
		protected String getParamString() {
			List<String> paramList = new ArrayList<String>();
			List<NameValuePair> plist = new ArrayList<NameValuePair>(param);
			for(NameValuePair p : plist) {
				paramList.add(String.format("%s=%s", p.getName(), p.getValue()));
			}
			
			return String.join("&", paramList);
		}

		private void trustAll() throws Exception {
//			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//				
//				public boolean verify(String hostname, SSLSession session) {
//					return true;
//				}
//			});
			
			TrustManager[] manager = {
					new TrustAuth()
			};
			
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, manager, null);
			//HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		}
		
		private class TrustAuth implements TrustManager, X509TrustManager{

			public X509Certificate[] getAcceptedIssuers() {
	            return null;
	        }

	        /*public boolean isServerTrusted(X509Certificate[] certs) {
	            return true;
	        }

	        public boolean isClientTrusted(X509Certificate[] certs) {
	            return true;
	        }*/

	        public void checkServerTrusted(X509Certificate[] certs, String authType)
	                throws CertificateException {
	            return;
	        }

	        public void checkClientTrusted(X509Certificate[] certs, String authType)
	                throws CertificateException {
	            return;
	        }
		}
}
