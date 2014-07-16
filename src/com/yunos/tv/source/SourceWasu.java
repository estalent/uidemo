package com.yunos.tv.source;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;
import android.util.Log;

import com.aliyun.base.net.http.Request.HttpMethod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yunos.tv.source.entity.AdInfo;
import com.yunos.tv.source.entity.ChannelBill;
import com.yunos.tv.source.entity.ChannelInfoList;
import com.yunos.tv.source.entity.Dianbo;
import com.yunos.tv.source.entity.HomeshellMessageList;
import com.yunos.tv.source.entity.HotWord;
import com.yunos.tv.source.entity.JuheLinkInfo;
import com.yunos.tv.source.entity.Kanba;
import com.yunos.tv.source.entity.PlayBack;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.source.entity.ProgramDetail;
import com.yunos.tv.source.entity.ProgramList;
import com.yunos.tv.source.entity.ProgramLists;
import com.yunos.tv.source.entity.Result;
import com.yunos.tv.source.entity.SearchList;
import com.yunos.tv.source.util.HttpUtils;
import com.yunos.tv.yingshi.Config;
import com.yunos.tv.yingshi.aidl.Catalog;
import com.yunos.tv.yingshi.exception.ErrorCodes;
import com.yunos.tv.yingshi.exception.SourceException;

public class SourceWasu {
	private static Gson gson = getGson();

	private static Gson getGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("yyyy-MM-dd");
		return gsonBuilder.create();
	}

	/**
	 * 根据类型id获取根目录类目
	 * 
	 * @param type
	 *            类型id 0:资讯 1:影视
	 * @return 根目录的内容(包含二级目录)
	 * @throws Exception
	 *             http请求失败或者json解析出错
	 */
	public static List<Catalog> getRootCatalog(int type) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/node/root";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("type", String.valueOf(type));
		Type t = new TypeToken<Result<List<Catalog>>>() {
		}.getType();
		return getResult(HttpMethod.Get, urlFormat, map, t);
	}

	/**
	 * 根据类目id获取子类目的列表
	 * 
	 * @param parentId
	 *            类目id
	 * @return
	 */
	public static List<Catalog> getSubCatalog(String parentId) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/node/list/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("node_id", parentId);
		return getResult(HttpMethod.Get, urlFormat, map, new TypeToken<Result<List<Catalog>>>() {
		}.getType());
	}

	/**
	 * 获取节目列表
	 * 
	 * @param catalogId
	 *            类目id
	 * @param page
	 *            页码
	 * @param pageSize
	 *            每页大小
	 * @return 结果列表
	 * @throws Exception
	 */
	public static ProgramList getProgramList(String catalogId, int page, int pageSize) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/program/list/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("node_id", catalogId);
		map.put("page", String.valueOf(page));
		map.put("page_size", String.valueOf(pageSize));
		Type t = new TypeToken<Result<ProgramList>>() {
		}.getType();
		return getResult(HttpMethod.Get, urlFormat, map, t);
	}

	/**
	 * 取首页下面gridview数据。
	 * 
	 * @param catalogIds
	 * @param pageSize
	 * @return
	 * @throws SourceException
	 */
	// public static ProgramList getProgramList(List<String> catalogIds, int
	// pageSize ) throws SourceException {
	// String urlFormat = Config.WASU_URL + "video/program/list/";
	// LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	// StringBuilder nodeids = new StringBuilder();
	// for (String s: catalogIds) {
	// nodeids.append(s + ",");
	// }
	// nodeids.deleteCharAt(nodeids.length() - 1);
	// Log.i("YingshiIndexFragment", nodeids.toString());
	// map.put("node_ids", nodeids.toString());
	// map.put("page_size", String.valueOf(pageSize));
	// Type t = new TypeToken<Result<ProgramList>>() {}.getType();
	// return getResult(HttpMethod.Get, urlFormat, map, t);
	// }
	// public static List<ProgramLists> getProgramList(String catalogIds, int
	// pageSize) throws SourceException {
	// String urlFormat = "video/program/lists/";
	// LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	//
	// map.put("node_ids", catalogIds);
	// map.put("page_size", String.valueOf(pageSize));
	// Type t = new TypeToken<Result<List<ProgramLists>>>() {
	// }.getType();
	// return getResult(HttpMethod.Get, urlFormat, map, t);
	// }

	public static List<ProgramLists> getHomeProgramList(String catalogId, int pageSize) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/home/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("node_id", catalogId);
		map.put("page_size", String.valueOf(pageSize));
		Type t = new TypeToken<Result<List<ProgramLists>>>() {
		}.getType();
		return getResult(HttpMethod.Get, urlFormat, map, t);
	}

	/**
	 * 获取节目详情
	 * 
	 * @param programId
	 *            节目id
	 * @return 节目详情
	 * @throws Exception
	 */
	public static Program getProgram(String programId) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/program/info/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("id", programId);
		Type t = new TypeToken<Result<ProgramDetail>>() {
		}.getType();
		ProgramDetail programDetail = getResult(HttpMethod.Get, urlFormat, map, t);
		programDetail.program.tags = programDetail.tag;
		programDetail.program.recommend = programDetail.recommend;
		return programDetail.program;
	}

	/**
	 * 获取节目详情v2
	 * 
	 * @param programId
	 *            节目id
	 * @return 节目详情
	 * @throws Exception
	 */
	public static Program getProgramV2(String programId) throws SourceException {
		String urlFormat = Config.WASU_URL + "v2/video/program/info/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("id", programId);
		Type t = new TypeToken<Result<ProgramDetail>>() {
		}.getType();
		ProgramDetail programDetail = getResult(HttpMethod.Get, urlFormat, map, t);
		programDetail.program.tags = programDetail.tag;
		programDetail.program.recommend = programDetail.recommend;
		return programDetail.program;
	}

	/**
	 * 聚合视频详情页
	 * 
	 * @param vedioId
	 * @param classId
	 * @return
	 * @throws SourceException
	 */
	public static Program getProgramJuhe(String videoId, String classId) throws SourceException {
		String urlFormat = Config.WASU_URL + "v2/juhe/video/info/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("videoId", videoId);
		map.put("classId", classId);
		Type t = new TypeToken<Result<Program>>() {
		}.getType();
		return getResult(HttpMethod.Get, urlFormat, map, t);
	}

	/**
	 * 获取热门推荐
	 * 
	 * @param page
	 *            页码
	 * @param pageSize
	 *            每个个数
	 * @return 节目数组
	 * @throws Exception
	 */
	public static ProgramList getRecommand(int page, int pageSize) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/program/recommend";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("page", String.valueOf(page));
		map.put("page_size", String.valueOf(pageSize));
		Type t = new TypeToken<Result<ProgramList>>() {
		}.getType();
		return getResult(HttpMethod.Get, urlFormat, map, t);
	}

	/**
	 * 全局节目搜索
	 * 
	 * @param keyword
	 *            关键字
	 * @return 节目数组
	 * @throws Exception
	 */
	// public static ArrayList<Program> getSearch(String keyword) throws
	// SourceException {
	// return getSearch(keyword,null);
	// String urlFormat = Config.WASU_URL + "video/program/search/";
	// LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	// map.put("keyword", keyword);
	// Type t = new TypeToken<Result<SearchList>>() {}.getType();
	// SearchList list = getResult(HttpMethod.Get, urlFormat, map, t);
	// return list.programList;
	// }
	/**
	 * 搜索某个类目下的节目信息
	 * 
	 * @param keyword
	 *            关键字
	 * @param nodeId
	 *            节目Id
	 * @return
	 * @throws SourceException
	 */
	public static ArrayList<Program> getSearch(String keyword, String nodeId, String showType) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/program/search/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("keyword", keyword);
		if (nodeId != null) {
			map.put("node_id", nodeId);
		}
		if (showType != null) {
			map.put("show_type", showType);
		}
		Type t = new TypeToken<Result<SearchList>>() {
		}.getType();
		SearchList list = getResult(HttpMethod.Get, urlFormat, map, t);
		return list.programList;
	}

	/**
	 * 新的搜索接口,包含聚合内容,可以选择输入类型(T9,拼音和笔画)
	 * 
	 * @return
	 */
	public static ArrayList<Program> getSearch(String keyword, String nodeId, String showType, String keyType) throws SourceException {
		String urlFormat = Config.WASU_URL + "v2/video/program/search";
		// String urlFormat = Config.WASU_URL + "video/program/search/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("keyword", keyword);
		if (nodeId != null) {
			map.put("node_id", nodeId);
		}
		if (showType != null && !showType.equals("")) {
			map.put("show_type", showType);
		}

		if (keyType != null) {
			map.put("key_type", keyType);
		}

		Type t = new TypeToken<Result<SearchList>>() {
		}.getType();
		SearchList list = getResult(HttpMethod.Get, urlFormat, map, t);
		return list.programList;
	}

	/**
	 * 获取广告图片
	 * 
	 * @return 广告地址
	 * @throws SourceException
	 */
	public static AdInfo getAdvertise() throws SourceException {
		String url = Config.WASU_URL + "video/ad";
		Type t = new TypeToken<Result<AdInfo>>() {
		}.getType();
		return getResult(HttpMethod.Get, url, null, t);
	}

	/**
	 * 发送播放日志
	 * 
	 * @param uuid
	 *            设备识别码
	 * @param programId
	 *            节目id
	 * @param sourceId
	 *            资源id
	 * @throws Exception
	 */
	public static void sendPlayLog(String uuid, String programId, String fileId) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/program/addplaylog/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("uuid", uuid);
		map.put("id", programId);
		map.put("fid", fileId);
		Type t = new TypeToken<Result<Object>>() {
		}.getType();
		getResult(HttpMethod.Get, urlFormat, map, t);
	}

	/**
	 * 发送收藏信息
	 * 
	 * @param uuid
	 *            设备识别码
	 * @param programId
	 *            节目id
	 * @throws SourceException
	 */
	public static void addUserFavourite(String uuid, String programId) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/program/adduserfav/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("uuid", uuid);
		map.put("id", programId);
		Type t = new TypeToken<Result<Object>>() {
		}.getType();
		getResult(HttpMethod.Get, urlFormat, map, t);
	}

	// @SuppressWarnings("unchecked")
	// private static <T> T getResult(String url_format,Type t,Object ...params)
	// throws SourceException{
	// String strUrl = Config.root_wasu + ((params.length > 0) ?
	// String.format(url_format, params) : url_format);
	// return (T)getResult(strUrl,t);
	// }

	/**
	 * 热门推荐 coverflow数据
	 * 
	 * @param catalogId
	 * @return
	 * @throws SourceException
	 */
	public static List<Program> getHotProgramList(String catalogId) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/program/hot/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("node_id", catalogId);
		Type t = new TypeToken<Result<List<Program>>>() {
		}.getType();
		return getResult(HttpMethod.Get, urlFormat, map, t);
	}

	/**
	 * 获取专题节目列表
	 * 
	 * @param nodeId
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws SourceException
	 */
	public static ProgramList getTopicProgramList(String nodeId, int page, int pageSize) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/program/topic/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("node_id", nodeId);
		map.put("page", String.valueOf(page));
		map.put("page_size", String.valueOf(pageSize));
		Type t = new TypeToken<Result<ProgramList>>() {
		}.getType();
		return getResult(HttpMethod.Get, urlFormat, map, t);
	}

	public static String getServerSystemTime() throws SourceException {
		String urlFormat = Config.WASU_URL + "openapi/v1/util/server/datetime/";
		/*
		 * LinkedHashMap<String, String> map = new LinkedHashMap<String,
		 * String>(); map.put("pattern", "yyyymmddhhmmss");
		 */
		Type t = new TypeToken<Result<String>>() {
		}.getType();
		return getResult(HttpMethod.Get, urlFormat, null, t);
	}

	/**
	 * 获取加放电视台列表
	 * 
	 * @param channelType
	 *            不填默认全部，频道类型（1：本地，2：央视，3：卫视）
	 * @return
	 * @throws SourceException
	 */
	public static ChannelInfoList getPlayBackChannelList(String channelType) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/tv/playback/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (!TextUtils.isEmpty(channelType)) {
			map.put("channel_type", channelType);
		}
		Type t = new TypeToken<Result<ChannelInfoList>>() {
		}.getType();
		return getResult(HttpMethod.Get, urlFormat, map, t);
	}

	/**
	 * 节目单列表
	 * 
	 * @param channelKey
	 * @return
	 * @throws SourceException
	 */
	public static ChannelBill getPlaybackBill(int channelKey) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/tv/playback/bill/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("channel_key", String.valueOf(channelKey));
		Type t = new TypeToken<Result<ChannelBill>>() {
		}.getType();
		return getResult(HttpMethod.Get, urlFormat, map, t);
	}

	/**
	 * 获取点播列表，默认7.23版本
	 * 
	 * @return
	 * @throws SourceException
	 */
	public static List<Dianbo> getDiandboList() throws SourceException {
		return getDianboList("");
	}
	
	/**
	 * 获取点播列表
	 * 
	 * @param version 
	 * @return
	 * @throws SourceException
	 */
	public static List<Dianbo> getDianboList(String version) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/dianbo/";
		Type t = new TypeToken<Result<List<Dianbo>>>() {
		}.getType();	
		if (!TextUtils.isEmpty(version)) {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put("version", version);
			return getResult(HttpMethod.Get, urlFormat, map, t);
		} else {
			return getResult(HttpMethod.Get, urlFormat, null, t);
		}
	}
	
	/**
	 * 默认取7.23版本
	 * @return
	 * @throws SourceException
	 */
	public static PlayBack getPlayBackList() throws SourceException {
		return getPlayBackList("");
	}
	
	/**
	 * 获取回放列表
	 * 
	 * @return
	 * @throws SourceException
	 */
	public static PlayBack getPlayBackList(String version) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/tv/playback/win8/";
		Type t = new TypeToken<Result<PlayBack>>() {
		}.getType();
		if (!TextUtils.isEmpty(version)) {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put("version", version);
			return getResult(HttpMethod.Get, urlFormat, map, t);
		} else {
			return getResult(HttpMethod.Get, urlFormat, null, t);
		}
	}

	/**
	 * 默认取7.23版本
	 * @return
	 * @throws SourceException
	 */
	public static Kanba getKanbaList() throws SourceException {
		return getKanbaList("");
	}
	
	/**
	 * 获取看吧列表
	 * 
	 * @return
	 * @throws SourceException
	 */
	public static Kanba getKanbaList(String version) throws SourceException {
		String urlFormat = Config.WASU_URL  + "video/kanba/";
		Type t = new TypeToken<Result<Kanba>>() {
		}.getType();
		if (!TextUtils.isEmpty(version)) {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put("version", version);
			return getResult(HttpMethod.Get, urlFormat, map, t);
		} else {
			return getResult(HttpMethod.Get, urlFormat, null, t);
		}
	}

	public static HomeshellMessageList getMessageList(int page, int pageSize) throws SourceException {
		String urlFormat = Config.WASU_URL + "video/message/list/";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("page", String.valueOf(page));
		map.put("page_size", String.valueOf(pageSize));
		Type t = new TypeToken<Result<HomeshellMessageList>>() {
		}.getType();
		return getResult(HttpMethod.Get, urlFormat, map, t);
	}

	public static ArrayList<HotWord> getHotWordList() throws SourceException {
		String urlFormat = Config.WASU_URL + "video/hot/word";
		Type t = new TypeToken<Result<ArrayList<HotWord>>>() {
		}.getType();

		return getResult(HttpMethod.Get, urlFormat, null, t);
	}

	public static JuheLinkInfo getJuheLinkInfo(String videoId, String classId, String playerId) throws SourceException {
		String urlFormat = Config.WASU_URL + "v2/juhe/video/play";
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("videoId", videoId);
		map.put("playerId", playerId);
		map.put("classId", classId);
		Type t = new TypeToken<Result<ArrayList<JuheLinkInfo>>>() {
		}.getType();
		ArrayList<JuheLinkInfo> infoList = getResult(HttpMethod.Get, urlFormat, map, t);
		if (infoList != null && !infoList.isEmpty()) {
			return infoList.get(0);
		}
		return null;
	}
	
	public static List<String> getTopicNodeIds() throws SourceException {
		return getTopicNodeIds("");
	}
	
	public static List<String> getTopicNodeIds(String version) throws SourceException {
		String urlFormat = /*Config.WASU_URL*/"http://10.249.201.60:8080/" + "video/config/topic_nodeids";
		Type t = new TypeToken<Result<ArrayList<String>>>() {
		}.getType();
		if (!TextUtils.isEmpty(version)) {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put("version", version);
			return getResult(HttpMethod.Get, urlFormat, map, t);
		} else {
			return getResult(HttpMethod.Get, urlFormat, null, t);
		}
	}

	/**
	 * 
	 * @param method
	 * @param urlFormat
	 * @param params
	 * @param t
	 * @return
	 * @throws SourceException
	 */
	private static <T> T getResult(HttpMethod method, String urlFormat, Map<String, String> params, Type t) throws SourceException {
		String strJson = HttpUtils.request(method, urlFormat, params);
		Log.i("sourcewasu", "result:" + strJson);
		Result<T> r = gson.fromJson(strJson, t);
		if (r.code != 200 || !r.success) {
			Log.e("sourcewasu", "errorcode:" + r.code);
			switch (r.code) {
			case 201:
				throw new SourceException(ErrorCodes.SERVERERROR_FAIL);
			case 500:
				throw new SourceException(ErrorCodes.SERVERERROR_SYS);
			case 50001:
				throw new SourceException(ErrorCodes.SERVERERROR_INVALID_PARAMS);
			case 404:
				throw new SourceException(ErrorCodes.SERVERERROR_NO_API);
			case 405:
				throw new SourceException(ErrorCodes.SERVERERROR_UNSUPPORTREQUESTMETHOD);
			case 600:
				throw new SourceException(ErrorCodes.RES_NOT_EXIST);
			}
		}
		return r.data;
	}
}
