package com.yunos.tv.source.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.Build;

import com.aliyun.base.net.http.Request;
import com.aliyun.base.net.http.Request.HttpMethod;
import com.aliyun.base.net.http.Response;
import com.yunos.tv.yingshi.exception.ErrorCodes;
import com.yunos.tv.yingshi.exception.SourceException;
import com.yunos.tv.yingshi.utils.StringUtils;
import com.yunos.tv.yingshi.utils.SystemProUtils;

public class HttpUtils {
	
	public static String system_info = null;
	
	public static String getSystemInfo() {
		if (system_info == null) {
			JSONObject json = new JSONObject();
			try {
				json.put("uuid", SystemProUtils.getUUID());
				json.put("device_model", Build.MODEL);
				json.put("device_sn", StringUtils.getSubStr(SystemProUtils.getUUID(), 32, false));
				json.put("device_system_version", SystemProUtils.getSystemVersion());
				json.put("device_firmware_version", Build.VERSION.RELEASE);
				system_info = json.toString();
			} catch (Exception e) {}
		}
//		Log.i("aabb", "getSystemInfo:" + system_info + "," + system_info.length());
		return system_info;
	}
	
	/**
	 * 
	 * @param method
	 * @param actionUrl
	 * @param map
	 * @return
	 * @throws SourceException 这里抛出的都是SourceException，所以base包中的ExceptionManager默认处理BaseException
	 * 
	 */
	public static String request(HttpMethod method, String actionUrl, Map<String, String> map) throws SourceException {
		String result = null;

		Request request = new Request(method, actionUrl);
		try {
			ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
			if (map != null) {
				Set<Entry<String, String>> set = map.entrySet();
				for (Entry<String, String> en : set) {
					pairs.add(new BasicNameValuePair(en.getKey(), en.getValue()));
				}
				pairs.add(new BasicNameValuePair("system_info", getSystemInfo()));
			} else {
				pairs.add(new BasicNameValuePair("system_info", getSystemInfo()));
			}
			request.setParams(pairs);
			Response response = request.execute();
			result = response.getString();
		} catch (UnsupportedEncodingException ex) {
			throw new SourceException(ErrorCodes.UNSUPPORTENCODING, ex);
		} catch (MalformedURLException ex) {
			throw new SourceException(ErrorCodes.URLILLEGAL, ex);
		} catch (UnknownHostException ex) {
			throw new SourceException(ErrorCodes.NETWORK_UnknownHostException, ex);
		} catch (InterruptedIOException ex) {
			//ConnectTimeoutException/SocketTimeoutException/ConnectionPoolTimeoutException
			throw new SourceException(ErrorCodes.NETWORK_InterruptedIOException, ex);
		} catch (HttpHostConnectException ex) {
			HttpHostConnectException hhce = (HttpHostConnectException) ex;
			String hostName = hhce.getHost().getHostName();
			SourceException se = new SourceException(ErrorCodes.NETWORK_HttpHostConnectException, ex);
			se.setErrorMessage(ErrorCodes.NETWORK_HttpHostConnectException.getMessage() + ":" + hostName);
			throw se;
		} catch (SocketException ex) {
			throw new SourceException(ErrorCodes.NETWORK_SocketException, ex);
		} catch (IOException ex) {
			throw new SourceException(ErrorCodes.NETWORK_Other, ex);
		} catch (Exception ex) {
			throw new SourceException(ErrorCodes.UNKNOWNERROR, ex);
		}
		return result;
	}

}