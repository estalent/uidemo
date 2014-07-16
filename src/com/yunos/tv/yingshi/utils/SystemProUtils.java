package com.yunos.tv.yingshi.utils;

import java.lang.reflect.Method;
import java.util.UUID;

import android.os.Build;

//import android.os.SystemProperties;

/**
 * 来自:http://ued.aliyun-inc.com/cloudapp/index.php?title=CloudUUID_%E5%AE%A2%E6%
 * 88%B7%E7%AB%AF%E5%BC%80%E5%8F%91%E6%8C%87%E5%8D%97
 * 
 */
public class SystemProUtils {

	// public static final String getUUID() {
	// // return SystemProperties.get("ro.aliyun.clouduuid","false"); // git入库
	// return "123456"; // for debug
	// }
	
	public static String getSystemVersion() {
		String version = Build.VERSION.RELEASE;
		String versionArray[] = version.split("-");
		return versionArray[0];
	}

	public static String getUUID() {
		try {
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			Method m = SystemProperties.getMethod("get", String.class, String.class);
			String result = (String) m.invoke(null, "ro.aliyun.clouduuid", "falsenull");
			if ("falsenull".equals(result)) {
				return "unknow_tv_imei";
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getDeviceName() {
		try {
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			Method m = SystemProperties.getMethod("get", String.class, String.class);
			String result = (String) m.invoke(null, "ro.yunos.product.board", "falsenull");
			if ("falsenull".equals(result)) {
				return null;
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
