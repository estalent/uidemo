package com.yunos.tv.source;
//package com.yunos.tv.source.util;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.yunos.tv.source.Config;
//
//public class SourceWasu2 {
//	
//	/**
//	 * 根据类型id获取根目录类目
//	 * @param type 类型id 0:资讯  1:影视
//	 * @return 根目录的内容(包含二级目录)
//	 * @throws Exception http请求失败或者json解析出错
//	 */
//	public static JSONArray getRootCatalog(int type) throws Exception{
//		String urlFormat = "/video/node/root?type=%s";
//		JSONObject obj = getResult(urlFormat,type);
//		return obj.getJSONArray("data");
//	}
//	
//	/**
//	 * 根据类目id获取子类目的列表
//	 * @param parentId 类目id
//	 * @return 
//	 */
//	public static JSONArray getSubCatalog(int parentId) throws Exception{
//		String urlFormat = "/video/node/list/?node_id=%s";
//		JSONObject obj = getResult(urlFormat,parentId);
//		return obj.getJSONArray("data");
//	}
//	
//	/**
//	 * 获取节目列表
//	 * @param catalogId 类目id
//	 * @param page 页码
//	 * @param pageSize 每页大小
//	 * @return 结果列表
//	 * @throws Exception
//	 */
//	public static JSONObject getProgramList(int catalogId,int page,int pageSize) throws Exception{
//		String urlFormat = "/video/program/list/?node_id=%s&page=%s&page_size=%s";
//		JSONObject obj = getResult(urlFormat,catalogId,page,pageSize);
//		return obj.getJSONObject("data");
//	}
//
//	/**
//	 * 获取节目详情
//	 * @param programId 节目id
//	 * @return 节目详情
//	 * @throws Exception
//	 */
//	public static JSONObject getInfo(int programId) throws Exception{
//		String urlFormat = "/video/program/info/?id=%s";
//		JSONObject obj = getResult(urlFormat,programId);
//		return obj.getJSONObject("data");
//	}
//	
//	private static JSONObject getResult(String url_format,Object ...params) throws Exception{
//		String strUrl = Config.root_wasu + ((params.length > 0) ? String.format(url_format, params) : url_format);
//		return getResult(strUrl);
//	}
//	
//	
//	/**
//	 * 根据url获取请求结果
//	 * @param url 请求的地址
//	 * @return 返回结果的JSONObject
//	 * @throws Exception http请求失败或者json解析出错
//	 */
//	public static JSONObject getResult(String url) throws Exception{
//		String strResult = NetWork.get(url);
//		if(strResult == null){ 
//			throw new Exception("http get error:no data response");
//		}
//		JSONObject jsonObj = new JSONObject(strResult);
//		if(jsonObj.getInt("code") == 200){
//			return jsonObj;
//		} else {
//			throw new Exception(jsonObj.getString("message"));
//		}
//	}
//}
